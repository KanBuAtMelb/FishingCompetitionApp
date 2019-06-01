package com.example.fishingtest.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.DateFormatSymbols;
import android.icu.util.DateInterval;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShotPreviewActivity extends AppCompatActivity {

    ImageButton imgBtn_shot;
    ImageButton imgBtn_measure;
    TextView txt_measure;
    EditText txt_fishname;
    Button btn_refresh;
    Button btn_post;
    TextView txt_shotHints;
    TextView txt_measureHints;
    public static final int PHOTO_REQUEST_CAREMA = 1; // Take Photo Code
    private int REQUEST_CODE_ASK_Take_Photo_Action = 1;
    private int REQUEST_CODE_ASK_Get_Latest_Data_Action = 2;
    public static final String measurePackageName = "com.google.tango.measure";
    public static File tempFile;
    private Uri originalImageUri;
    private Uri measuredImageUri;
    private static File originalFilePath;
    private static File measuredFilePath;
    boolean photoReady = false;
    boolean measuredDataReady = false;
    String measuredLong;
    Competition currentComp;
    DatabaseReference database;
    FirebaseUser fbUser;
    Location comp_location;
    Double comp_latitude;
    Double comp_longitude;
    float comp_radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot_preview);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        database = FirebaseDatabase.getInstance().getReference();
        imgBtn_shot = (ImageButton) findViewById(R.id.imageButton_shot);
        imgBtn_measure = (ImageButton) findViewById(R.id.imageButton_latest);
        txt_measure =(TextView) findViewById(R.id.clipboardTextView);
        txt_fishname =(EditText) findViewById(R.id.fixedFishText);
        btn_post = (Button) findViewById(R.id.postButton);
        txt_shotHints = (TextView) findViewById(R.id.shotHintsTextView);
        txt_measureHints = (TextView) findViewById(R.id.refreshHintsTextView);
        btn_refresh = (Button) findViewById(R.id.refreshButton);

        Intent intent = getIntent();
        currentComp = (Competition) intent.getSerializableExtra("currentComp");

        comp_latitude = Double.parseDouble(currentComp.getGeo_map().split(",")[0]);
        comp_longitude = Double.parseDouble(currentComp.getGeo_map().split(",")[1]);

        try {
            comp_radius = Float.valueOf((currentComp.getGeo_map().split(",")[2]).trim()).floatValue() * 1000;
        } catch (NumberFormatException e) {
            Toast.makeText(ShotPreviewActivity.this, "Competition radius wrong.",Toast.LENGTH_SHORT).show();
        }

        comp_location = new Location("");
        comp_location.setLatitude(comp_latitude);
        comp_location.setLongitude(comp_longitude);

        imgBtn_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_Take_Photo_Action);
                } else {
                    openCamera();
                }
            }
        });

        imgBtn_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_Get_Latest_Data_Action);
                } else {
                    intentToMeasure();
                    btn_refresh.setClickable(true);
                    btn_refresh.setBackgroundColor(greenOrRed(true));
                }
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeasureData();
                txt_measureHints.setVisibility(View.INVISIBLE);
                btn_post.setClickable(postReady());
                btn_post.setBackgroundColor(greenOrRed(postReady()));
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.curLoc != null) {
                    boolean freshFlag = checkImageFresh(originalFilePath, measuredFilePath);

                    if (Common.ifInCircle(comp_location, Common.curLoc, comp_radius)) {
                        if (freshFlag) {
                            Toast.makeText(ShotPreviewActivity.this, "You clicked the post button",Toast.LENGTH_SHORT).show();
                            Common.uploadFishingPost(ShotPreviewActivity.this, database, fbUser, currentComp, originalImageUri, measuredImageUri, measuredLong, txt_fishname.getText().toString());
                        }
                    } else {
                        Toast.makeText(ShotPreviewActivity.this, "Your location is not in competition area.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShotPreviewActivity.this, "Sorry, Your location is not ready or you are staying in room with WiFi, Please move your step at outside to active it.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_refresh.setClickable(postReady());
        btn_refresh.setBackgroundColor(greenOrRed(postReady()));
        btn_post.setClickable(postReady());
        btn_post.setBackgroundColor(greenOrRed(postReady()));
    }

    private boolean postReady() {
        return measuredDataReady && photoReady;
    }

    private int greenOrRed(boolean givenBool) {
        if (givenBool) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(originalImageUri));
                        imgBtn_shot.setImageBitmap(bitmap);
                        txt_shotHints.setVisibility(View.INVISIBLE);
                        photoReady = true;
                        btn_post.setClickable(postReady());
                        btn_post.setBackgroundColor(greenOrRed(postReady()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void intentToMeasure() {
        if (isAppInstalled(this, measurePackageName)) {
            Toast.makeText(this,"We will jump to 'Measure' app to let you measure your Fish, please take the measured photo and copy your measured data before you touch Read Measure button.",Toast.LENGTH_SHORT).show();
            PackageManager packageManager = getPackageManager();
            Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(measurePackageName);
            if (launchIntentForPackage != null)
                startActivity(launchIntentForPackage);
        } else {
            Toast.makeText(this,"Please Download 'Measure' in Google Store.",Toast.LENGTH_SHORT).show();
            goToMarket(this, measurePackageName);
        }
    }

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,"Please Download 'Measure' in Google Store.",Toast.LENGTH_SHORT).show();
        }
    }

    private void getMeasureData() {
        File measuredImageFile = getLatestPhoto(this);
        measuredImageUri = Common.getImageContentUri(this, measuredImageFile);
        Picasso.get().load(measuredImageUri).rotate(90).into(imgBtn_measure);
        //imgBtn_measure.setImageURI(measuredImageUri);
        String regex = "\\d+(\\.\\d+)?cm|\\d+(\\.\\d+)?m";
        Pattern meaPattern = Pattern.compile(regex);
        Matcher meaMatcher;
        try {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData data = cm.getPrimaryClip();
            ClipData.Item item = data.getItemAt(0);
            String clipContent = item.getText().toString();
            meaMatcher = meaPattern.matcher(clipContent);
            while(meaMatcher.find()) {
                measuredLong = meaMatcher.group();
                break;
            }
        } catch (NullPointerException e) {
            Toast.makeText(this,"Please copy your fish measured data.",Toast.LENGTH_SHORT).show();
        }

        if (!measuredLong.isEmpty() && measuredImageUri != null) {
            txt_measure.setText(measuredLong);
            measuredDataReady = true;
        } else {
            Toast.makeText(this,"Please retry to measure your fish measured data.",Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        //get current version
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // make intent for system default camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // looking for sd card mount
        if (hasSdcard()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // create file uri
                originalImageUri = Uri.fromFile(tempFile);
                originalFilePath = tempFile;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
            } else {
                //compatite with Android 7.0
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //check permission of R/W
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //Ask permission
                    Toast.makeText(this,"please give permission for read/write",Toast.LENGTH_SHORT).show();
                    return;
                }
                originalImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                originalFilePath = tempFile;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
            }
        }
        // get result of system default camera
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    private static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private static File getLatestPhoto(Context context) {
        //find album path
        String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/Pictures/Measure";
        //get photo id
        String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
        //check path and time
        String[] projection = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED};
        //
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        //
        String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};

        //check album and sort photo
        String cameraPair = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            cameraPair = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File imagefile = new File(cameraPair);
            measuredFilePath = imagefile;
            return imagefile;
        }
        return null;
    }

    private static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_ASK_Take_Photo_Action){
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }else {
                    Toast.makeText(this, "No permission for take photo", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_ASK_Get_Latest_Data_Action) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToMeasure();
                btn_post.setClickable(postReady());
                btn_post.setBackgroundColor(greenOrRed(postReady()));
            }else {
                Toast.makeText(this, "No permission for get Measure Data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkImageFresh(File oriImg, File meaImg) {
        try {
            long fiveMins = 60*5;
            long threeMins = 60*3;

            Long ori_fileTime = (oriImg.lastModified())/1000;
            Long mea_fileTime = (meaImg.lastModified())/1000;
            Long current_time = System.currentTimeMillis()/1000;

            if (((current_time - ori_fileTime) < fiveMins) && ((current_time - mea_fileTime) < fiveMins)) {
                if (Math.abs(mea_fileTime - ori_fileTime) <= threeMins) {
                    ExifInterface ori_exifInterface = new ExifInterface(oriImg.getPath());

                    Double ori_latitude = Common.DMStoDD(ori_exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE), ori_exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                    Double ori_longitude = Common.DMStoDD(ori_exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE), ori_exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

                    Location ori_location = new Location("");
                    ori_location.setLatitude(ori_latitude);
                    ori_location.setLongitude(ori_longitude);

                    if (Common.ifInCircle(comp_location, ori_location, comp_radius)) {
                        return true;
                    } else {
                        Toast.makeText(this, "The photo is not taken in competition area." + "Comp Loc: " + Double.toString(comp_location.getLatitude()) + "," + Double.toString(comp_location.getLongitude()) + " OriImg Loc: " + Double.toString(ori_location.getLatitude()) + "," + Double.toString(ori_location.getLongitude()), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(this, "The time difference between the two photos can not exceed 3 minutes.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(this, "Both photos must be taken in 5 minutes.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Given photo is not correct", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Given photo is not correct", Toast.LENGTH_SHORT).show();
        return false;
    }
}

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


import java.io.File;
import java.io.FileNotFoundException;
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
    boolean photoReady = false;
    boolean measuredDataReady = false;
    String measuredLong;
    Competition currentComp;
    DatabaseReference database;
    FirebaseUser fbUser;

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
        btn_post = (Button) findViewById(R.id.postButton);
        txt_shotHints = (TextView) findViewById(R.id.shotHintsTextView);
        txt_measureHints = (TextView) findViewById(R.id.refreshHintsTextView);
        btn_refresh = (Button) findViewById(R.id.refreshButton);

        Intent intent = getIntent();
        currentComp = (Competition) intent.getSerializableExtra("currentComp");

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
                //Todo: Need Geo Check and Image Fresh Check
                Toast.makeText(ShotPreviewActivity.this, "You click the post button",Toast.LENGTH_SHORT).show();
                String postUUID = UUID.randomUUID().toString();
                Common.uploadFishingPost(ShotPreviewActivity.this, database, fbUser, currentComp, postUUID, originalImageUri, measuredImageUri, measuredLong);
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
        imgBtn_measure.setImageURI(measuredImageUri);
        String regex = "\\d+cm";
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
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                originalImageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this,"please give permission for read/write",Toast.LENGTH_SHORT).show();
                    return;
                }
                originalImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 判断sdcard是否被挂载
     */
    private static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private static File getLatestPhoto(Context context) {
        //拍摄照片的地址
        String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/Pictures/Measure";
        //拍摄照片的地址ID
        String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
        //查询路径和修改时间
        String[] projection = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED};
        //
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        //
        String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};

        //检查camera文件夹，查询并排序
        String cameraPair = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            cameraPair = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File imagefile = new File(cameraPair);
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
            if (grantResults.length > 0) {//grantResults 数组中存放的是授权结果
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }else {//用户拒绝授权
                    //可以简单提示用户
                    Toast.makeText(this, "No permission for take photo", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_ASK_Get_Latest_Data_Action) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToMeasure();
                btn_post.setClickable(postReady());
                btn_post.setBackgroundColor(greenOrRed(postReady()));
            }else {//用户拒绝授权
                //可以简单提示用户
                Toast.makeText(this, "No permission for get Measure Data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

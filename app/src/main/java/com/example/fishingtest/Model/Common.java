package com.example.fishingtest.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Common {

    public Common() {
    }

    private final static String TAG = "Common";

    public static Competition currentItem = null;

    // Location
    public static double curLat = 200.0;
    public static double curLon = 200.0;

//    public static List<Competition> compList = new ArrayList<>();
//    public static List<Competition> compsBydate = new ArrayList<>();
//    public static List<Competition> compsByName = new ArrayList<>();
//    public static List<Competition> compsByReward = new ArrayList<>();
//    public static List<Competition> compsByType = new ArrayList<>();


    // Used in Competition and User classes
    public static String NA = "NA";
    public static Date NA_Date = new Date(2019,1,1);
    public static int NA_Integer = -1;
    public static int EMPTY_SPINNER = 0;
    public static String EMPTY = "";


    // Used in User class
    public static String user_member = "Member";
    public static String user_admin = "Administrator";


    // Check the time to the competition date
    // competition date given in the format of  "14/03/2019 20:50 GMT+08:00"
    public static long timeToCompStart(String compTime_str){

        long diff = -1L;
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date compDate = new Date();
        try {
            // Convert string into Date
            compDate = df.parse(compTime_str);
            System.out.println("Converted by df2 = " + df.format(compDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        TimeZone uTimeZone= calendar.getTimeZone();

        if(uTimeZone.inDaylightTime(new Date())) {
            diff = compDate.getTime() -(currentLocalTime.getTime()+uTimeZone.getDSTSavings());
        }else {
            diff = compDate.getTime() -currentLocalTime.getTime();
        }

        return diff;


    };


    public static Date formattingDate(String compDate){
        // For Competition info
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        try {
        // Convert string into Date
        date = df.parse("20/04/19 23:50 GMT+08:00");
        } catch (Exception e) {
            e.printStackTrace();
        }

      return date;
    };

    //this function include uploading to storage and database. Before image save its download url in database, the image should be uploaded to the cloud storage and get the download url from the storage,
    //so that we can save the download url to database and retrieve by another usage directly for downloading the image.
    public static void uploadFishingPost(final Context context, final DatabaseReference database, final FirebaseUser currentUser, final Competition currentComp, final Uri oriImageUri, final Uri meaImageUri, final String measuredData) {
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();

        final String competitionCategory = "Competitions";
        final String competitionImageCategory = "Comp_Images";
        final String postImageCategory = "Posts";
        final String originalFishPhotoCategory = "Original";
        final String measuredFishPhotoCategory = "Measured";
        final String postID = timestamp + "_" + currentUser.getUid();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference compRef = storageRef.child(competitionImageCategory).child(postImageCategory);
        StorageReference userRef = compRef.child(currentUser.getUid());
        StorageReference postRef = userRef.child(postID);
        StorageReference originalImagesRef = postRef.child(originalFishPhotoCategory);
        StorageReference measuredImagesRef = postRef.child(measuredFishPhotoCategory);

        String originalFilename = currentUser.getUid() + "_" + timestamp + "_ori";
        String measuredFilename = currentUser.getUid() + "_" + timestamp + "_mea";
        StorageReference originalFileRef = originalImagesRef.child(originalFilename);
        StorageReference measuredFileRef = measuredImagesRef.child(measuredFilename);

        DatabaseReference postDBRef = database.child("Posts").child(competitionCategory).child(currentComp.getCompID()).child(postID);
        //create upload task which is a new thread, then it will upload the image to cloud storage by local image uri
        UploadTask uploadOriTask = originalFileRef.putFile(oriImageUri);
        UploadTask uploadMeaTask = measuredFileRef.putFile(meaImageUri);

        uploadOriTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Original Image: Upload failed!\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri oriDownloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(context, "Original Image: Upload finished!", Toast.LENGTH_SHORT).show();
                uploadMeaTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads+
                        Toast.makeText(context, "Measured Image: Upload failed!\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri meaDownloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(context, "Measured Image: Upload finished!", Toast.LENGTH_SHORT).show();
                        if (uploadOriTask.isComplete() && uploadMeaTask.isComplete()) {
                            Post post = new Post(postID, currentUser.getUid(), currentComp.getCompID(), oriDownloadUrl.toString(), meaDownloadUrl.toString(), measuredData, timestamp, curLon, curLat);
                            Toast.makeText(context, "Posting......", Toast.LENGTH_SHORT).show();
                            postToDB(context, postDBRef, post);
                        }
                    }
                });
            }
        });
    }

    //upload the post with additional information to database, it will upload the image and post as two different category in database and link themselves by post id to match the image and post
    private static void postToDB(final Context context, final DatabaseReference database, Post post) {
        database.setValue(post);
        Toast.makeText(context, "Post Success!", Toast.LENGTH_SHORT).show();
    }

    public static void commentToDB(final Context context, final DatabaseReference database, Comment comment) {
        database.setValue(comment);
        Toast.makeText(context, "Comment Success!", Toast.LENGTH_SHORT).show();
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String timeStampToTime(String timeStamp) {
        String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(String.valueOf(timeStamp)) * 1000));
        return result;
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}

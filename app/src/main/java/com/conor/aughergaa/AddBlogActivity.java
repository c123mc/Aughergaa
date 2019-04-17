package com.conor.aughergaa;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class AddBlogActivity extends AppCompatActivity {

    ImageView image_blog;
    EditText title,description;
    Button add_blog;
    Uri selected_Bitmap;
    int MY_CAMERA_PERMISSION_CODE=10;
    int REQUEST_GET_SINGLE_FILE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar!=null;

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FABE44")));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setTitle("New Blog");

        image_blog = (ImageView) findViewById(R.id.image_blog);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        add_blog = (Button) findViewById(R.id.add_blog);

        image_blog.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(AddBlogActivity.this), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_CAMERA_PERMISSION_CODE);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GET_SINGLE_FILE);
                }
            }
        });

        add_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()){
                    Toast.makeText(AddBlogActivity.this, "Title Field is empty", Toast.LENGTH_SHORT).show();
                }
                else if (description.getText().toString().isEmpty()){
                    Toast.makeText(AddBlogActivity.this, "Description field is empty", Toast.LENGTH_SHORT).show();
                }else if (selected_Bitmap == null){
                    Toast.makeText(AddBlogActivity.this, "Please Add header Image ", Toast.LENGTH_SHORT).show();
                }else {
                    upload();
                }
            }
        });
    }

    void upload(){

        final ProgressDialog dialog = new ProgressDialog(AddBlogActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Please wait while the data is uploaded...");
        dialog.show();

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Blogs/"+System.currentTimeMillis()+".jpg");
        final UploadTask uploadTask = ref.putFile(selected_Bitmap);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dialog.dismiss();
                Toast.makeText(AddBlogActivity.this, "Image Uploading Failed...", Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference dref= FirebaseDatabase.getInstance().getReference().child("Blogs").push();
                        dref.child("Title").setValue(title.getText().toString());
                        dref.child("Description").setValue(description.getText().toString());
                        dref.child("Image").setValue(uri.toString());
                        selected_Bitmap = null;
                        title.setText("");
                        description.setText("");
                        dialog.dismiss();
                        Toast.makeText(AddBlogActivity.this, "Blog Added Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public Bitmap loadBitmap(Uri url)
    {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    selected_Bitmap = selectedImageUri;
                    // Set the image in ImageView
                    image_blog.setImageURI(selectedImageUri);
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}

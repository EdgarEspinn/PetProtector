package edu.orangecoastcollege.cs273.petprotector;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PetListActivity extends AppCompatActivity {

    private ImageView mPetImageView;
    private Uri imageUri;

    // Constant for permissions:
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int DENIED = PackageManager.PERMISSION_DENIED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        mPetImageView = (ImageView) findViewById(R.id.petImageView);

        mPetImageView.setImageURI(getUriFromResource(this, R.drawable.none));
    }

    public void selectPetImage(View v)
    {
        List<String> permslist = new ArrayList<>();

        // Check each permission individually

        int hasCameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (hasCameraPerm == DENIED)
            permslist.add(Manifest.permission.CAMERA);

        int readStoragePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readStoragePerm == DENIED)
            permslist.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        int writeStoragePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeStoragePerm == DENIED)
            permslist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Some of the permissions have not been granted
        if (permslist.size() > 0)
        {
            // Convert the permslist into an array
            String[] permsArray = new String[permslist.size()];
            permslist.toArray(permsArray);

            // Ask user for them
            ActivityCompat.requestPermissions(this, permsArray, 1337);
        }

        // Make sure we have all the permissions, then start the Image Gallery:
        if (hasCameraPerm == GRANTED && readStoragePerm == GRANTED && writeStoragePerm == GRANTED)
        {
            // Open up the Image Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the activity for a result (picture)
            startActivityForResult(galleryIntent, 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            // data = data from GalleryIntent (the uri of some image)
            imageUri = data.getData();
            mPetImageView.setImageURI(imageUri);
        }
    }

    public static Uri getUriFromResource(Context context, int resId)
    {
        Resources res = context.getResources();
        // Build a string in the form
        // android.resource://edu.orangecoastcollege.cs273.petprotector/drawable/none
        String uri = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + res.getResourcePackageName(resId) + "/"
                + res.getResourceTypeName(resId) + "/"
                + res.getResourceEntryName(resId);

        // Parse the string inorder to construct a URI
        return Uri.parse(uri);
    }
}

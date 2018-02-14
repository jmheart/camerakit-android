package com.camerakit.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.camerakit.CameraView;
import com.camerakit.Facing;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private CameraView cameraView;
    private Toolbar toolbar;
    private FloatingActionButton photoButton;

    private Button previewSettingsButton;
    private Button photoSettingsButton;
    private Button flashlightButton;
    private Button facingButton;

    private ImageView newPhotoImageView;
    private ImageView photoImageView;

//    private CameraJpegFile mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);

        photoButton = findViewById(R.id.fabPhoto);
        photoButton.setOnClickListener(photoOnClickListener);

        previewSettingsButton = findViewById(R.id.previewSettingsButton);
        previewSettingsButton.setOnClickListener(previewSettingsOnClickListener);

        photoSettingsButton = findViewById(R.id.photoSettingsButton);
        photoSettingsButton.setOnClickListener(photoSettingsOnClickListener);

        flashlightButton = findViewById(R.id.flashlightButton);
        flashlightButton.setOnClickListener(flashlightOnClickListener);

        facingButton = findViewById(R.id.facingButton);
        facingButton.setOnClickListener(facingOnClickListener);

        newPhotoImageView = findViewById(R.id.newImageView);
        newPhotoImageView.setVisibility(View.GONE);

        photoImageView = findViewById(R.id.photoImageView);
        photoImageView.setAlpha(0f);
//        photoImageView.setOnClickListener((v -> {
//            if (mPhoto != null) {
//                MediaScannerConnection.scanFile(this, new String[]{mPhoto.getFile().getAbsolutePath()}, null,
//                        (path, uri) -> {
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            intent.setDataAndType(uri, "image/*");
//                            startActivity(intent);
//                        });
//            }
//        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start(Facing.BACK);
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_about) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.about_dialog_title)
                    .setMessage(R.string.about_dialog_message)
                    .setNeutralButton("Dismiss", null)
                    .show();

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#91B8CC"));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(Html.fromHtml("<b>Dismiss</b>"));

            return true;
        }

        if (item.getItemId() == R.id.main_menu_gallery) {
            String bucketId = "";

            final String[] projection = new String[]{"DISTINCT " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + ", " + MediaStore.Images.Media.BUCKET_ID};
            final Cursor cur = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            while (cur != null && cur.moveToNext()) {
                final String bucketName = cur.getString((cur.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                if (bucketName.equals("CameraKit")) {
                    bucketId = cur.getString((cur.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)));
                    break;
                }
            }

            Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            if (bucketId.length() > 0) {
                mediaUri = mediaUri.buildUpon()
                        .authority("media")
                        .appendQueryParameter("bucketId", bucketId)
                        .build();
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, mediaUri);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private View.OnClickListener photoOnClickListener = v -> {
        CameraPhotographer photographer = new CameraPhotographer();
    };


    private View.OnClickListener previewSettingsOnClickListener = v -> {

    };

    private View.OnClickListener photoSettingsOnClickListener = v -> {

    };

    private View.OnClickListener flashlightOnClickListener = v -> {

    };

    private View.OnClickListener facingOnClickListener = v -> {
//        cameraView.toggleFacing();
    };

}

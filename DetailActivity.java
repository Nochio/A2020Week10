package com.example.mypersonalnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mypersonalnotes.storage.MemoryStorage;

import java.io.InputStream;

public class DetailActivity extends AppCompatActivity {

    EditText editTextHead;
    EditText editTextBody;
    private ImageView imageView;
    private int row = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        editTextHead = findViewById(R.id.editTextHead);
        editTextBody = findViewById(R.id.editTextBody);

        row = getIntent().getIntExtra(MainActivity.INDEX_KEY, 0);
        editTextHead.setText(MemoryStorage.getNote(row).getHead());
        editTextBody.setText(MemoryStorage.getNote(row).getBody());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MemoryStorage.editNote(row, editTextHead.getText().toString(), editTextBody.getText().toString());
    }

    public void galleryBtnPressed(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void cameraBtnPressed(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        backFromGallery(requestCode, resultCode, data);
        if(requestCode == 2){ // from gall
            if (resultCode == -1) {  // -1 is code for OK
                System.out.println("Success !!");
                Bitmap bitmap = (Bitmap) data.getExtras().get("data"); // ask for data from the incoming intent.
                imageView.setImageBitmap(bitmap); // assign the data to our imageView
            } else {
                System.out.println("failed to get image");
            }
        }
    }

    private void backFromGallery(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) { // from gallery
            if (resultCode == -1) { // -1 means OK
                Uri uri = data.getData(); // get path to where the image is located
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {

                }
            }
        }
    }
}

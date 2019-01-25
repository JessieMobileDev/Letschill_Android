package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloadHandler {

    public static Bitmap downloadFacebookImageToBitmap(String imageURL) {

        try {

            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap imageBitmap = BitmapFactory.decodeStream(input);
            return imageBitmap;

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    public static void downloadFirebaseImageAndSetBitmap(String photoPath, final ImageView imageView) {

        // Variables
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference mStorageReference = mFirebaseStorage.getReference().child(photoPath);
        final long ONE_MEGABYTE = 1024 * 1024;

        mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void showDefaultImagesIfOffline(String category, ImageView imageView) {

        switch (category) {

            case "Video Game":
                imageView.setImageResource(R.drawable.videogame);
                break;
            case "Sports":
                imageView.setImageResource(R.drawable.sports);
                break;
            case "Technology":
                imageView.setImageResource(R.drawable.technology);
                break;
            case "Outdoor Activities":
                imageView.setImageResource(R.drawable.create_back);
                break;
            case "Indoor Activities":
                imageView.setImageResource(R.drawable.create_back);
                break;
            case "Arts":
                imageView.setImageResource(R.drawable.art);
                break;
            case "Music":
                imageView.setImageResource(R.drawable.music);
                break;
            case "Movies":
                imageView.setImageResource(R.drawable.movie);
                break;
            case "Auto":
                imageView.setImageResource(R.drawable.auto);
                break;
            case "Food":
                imageView.setImageResource(R.drawable.food);
                break;
            case "Fitness":
                imageView.setImageResource(R.drawable.fitness);
                break;
        }
    }
}

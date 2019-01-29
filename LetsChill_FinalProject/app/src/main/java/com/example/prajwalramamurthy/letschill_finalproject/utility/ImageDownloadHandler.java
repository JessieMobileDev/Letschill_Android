package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.ProfileActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ImageDownloadHandler extends IntentService {

    // Constants
    public static final String EXTRA_RESULT_RECEIVER = "com.example.prajwalramamurthy.letschill_finalproject.utility.EXTRA_RESULT_RECEIVER";
    public static final String EXTRA_BITMAP = "com.example.prajwalramamurthy.letschill_finalproject.utility.BITMAP";

    public ImageDownloadHandler() {
        super("ImageDownloadHandler");
    }

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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // If the intent passed does not contain any of the extras variables, throw an exception
        if (!Objects.requireNonNull(intent).hasExtra(EXTRA_RESULT_RECEIVER)) {
            throw new IllegalArgumentException("EXTRA_RESULT_RECEIVER is missing!");
        }

        final ResultReceiver mReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        int imageType = intent.getIntExtra(ProfileActivity.INTENT_IMAGE_TYPE, 3);
        String imageUrl = intent.getStringExtra(ProfileActivity.INTENT_IMAGE_URL);
        User loggedUser = intent.getParcelableExtra(ProfileActivity.INTENT_LOGGED_USER_OBJECT);

        if (imageType != 3 && imageUrl != null && loggedUser != null) {

            // FACEBOOK IMAGE DOWNLOAD PROCESS
            if (imageType == 0) {

                Bitmap fbImage = downloadFacebookImageToBitmap(imageUrl);

                if (fbImage != null) {

                    Bundle dataToSendBack = new Bundle();
                    dataToSendBack.putParcelable(ProfileActivity.INTENT_LOGGED_USER_OBJECT, loggedUser);
                    dataToSendBack.putParcelable(EXTRA_BITMAP, fbImage);
                    mReceiver.send(Activity.RESULT_OK, dataToSendBack);
                }
            }
        }
    }
}

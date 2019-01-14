package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    // Variables
    private EditText mEditText_fullName, mEditText_username, mEditText_email, mEditText_phone,
            mEditText_facebookEmail;
    private TextView mTextView_interests;
    private ImageView mImageView_profilePicture;
    private Button mButton_chooseInterests;
    private ConstraintLayout mContraintLayout_verified;
    private User retrievedUser;
    private DatabaseReference mDBReference;
    private Bitmap mBitmap;
    private String mUrl;
    private Intent mGalleryIntent, mCropIntent;
    private Uri mImageUri;
    private final Handler mHandler = new Handler();
    private boolean didSelectAnImage = false;
    private EditProfileInterface mEditProfileInterface;

    // Constants
    private static final String ARG_LOGGED_USER = "ARG_LOGGED_USER";
    private static final String ERROR_EMPTY_FIELDS = "Do not leave this field empty";
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";

    public interface EditProfileInterface {

        void closeEditProfileActivity();
    }

    public static EditProfileFragment newInstance(User loggedUser) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, loggedUser);

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditProfileInterface) {

            mEditProfileInterface = (EditProfileInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            if (item.getItemId() == R.id.action_submit_edittedProfile) {

                retrieveProfilePicture();

            } else if (item.getItemId() == R.id.action_settings) {

                MenuIntentHandler.getMenuIntents(item, getContext(), getActivity());
            }
        }

        return false;

    }

    private void openGallery() {

        mGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mGalleryIntent, "Select Image from Gallery"),
                2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (data != null) {

                Bundle mBundle = data.getExtras();

                mBitmap = mBundle.getParcelable("data");

                mImageView_profilePicture.setImageBitmap(mBitmap);

                didSelectAnImage = true;
            }

        } else if (requestCode == 2) {

            if (data != null) {

                mImageUri = data.getData();

                cropImage();
            }
        }
    }

    private void cropImage() {

        try {

            // Crop the image under the requirements below (do not change the values)
            mCropIntent = new Intent("com.android.camera.action.CROP");
            mCropIntent.setDataAndType(mImageUri, "image/*");
            mCropIntent.putExtra(CROP_EXTRA, true);
            mCropIntent.putExtra(CROP_OUTPUTX, 400);
            mCropIntent.putExtra(CROP_OUTPUTY, 300);
            mCropIntent.putExtra(CROP_ASPECTX, 4);
            mCropIntent.putExtra(CROP_ASPECTY, 3);
            mCropIntent.putExtra(CROP_SCALEUP_IFNEEDED, true);
            mCropIntent.putExtra(CROP_RETURN_DATA, true);

            startActivityForResult(mCropIntent, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (getView() != null && getArguments() != null) {

            // Get the views
            mEditText_fullName = getView().findViewById(R.id.editText_editProfile_fullName);
            mEditText_username = getView().findViewById(R.id.editText_editProfile_username);
            mEditText_email = getView().findViewById(R.id.editText_editProfile_email);
            mEditText_phone = getView().findViewById(R.id.editText_editProfile_phone);
            mEditText_facebookEmail = getView().findViewById(R.id.editText_editProfile_fb);
            mTextView_interests = getView().findViewById(R.id.textView_editProfile_interests);
            mButton_chooseInterests = getView().findViewById(R.id.button_editProfile_interests);
            mContraintLayout_verified = getView().findViewById(R.id.constraint_notVerified);
            mImageView_profilePicture = getView().findViewById(R.id.imageView_editProfile);

            // Collect the user object passed into this fragment
            retrievedUser = getArguments().getParcelable(ARG_LOGGED_USER);

            // Assign click listeners
            mImageView_profilePicture.setOnClickListener(this);

            if (retrievedUser != null) {

                // Assign the data back to the edit texts and text views where needed
                mEditText_username.setText(retrievedUser.getUsername());
                mEditText_email.setText(retrievedUser.getEmail());

                // Concatenate all the interests into one string, breaking line right after each one
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < retrievedUser.getInterests().size(); i++) {

                    if (i == retrievedUser.getInterests().size() - 1) {

                        sb.append(retrievedUser.getInterests().get(i));
                    } else {

                        sb.append(retrievedUser.getInterests().get(i)).append("\n");
                    }
                }

                mTextView_interests.setText(sb.toString());
            }

        }
    }

    private void retrieveProfilePicture() {

        // Get reference
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("IDImages");

        StorageReference imagesRef = storageReference.child(String.valueOf((new Date()).getTime()) + ".jpg");

        // Convert bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (didSelectAnImage) {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        } else {
            mBitmap = ((BitmapDrawable)mImageView_profilePicture.getDrawable()).getBitmap();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }

        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mUrl = taskSnapshot.getMetadata().getPath();

                // Call the save changes to database method
                saveProfileChangesToDatabase();
            }
        });

    }

    private void saveProfileChangesToDatabase() {


        // Validate the fields
        if (!mEditText_fullName.getText().toString().isEmpty()) {

            if (!mEditText_username.getText().toString().isEmpty()) {

                if (!mEditText_email.getText().toString().isEmpty()) {

                    if (!mEditText_phone.getText().toString().isEmpty()) {

                        if (!mEditText_facebookEmail.getText().toString().isEmpty()) {

                            if (!mTextView_interests.getText().toString().isEmpty()) {

                                String[] interests = mTextView_interests.getText().toString().split("\n");

                                final ArrayList<String> newInterests = new ArrayList<>(Arrays.asList(interests));

                                mDBReference = FirebaseDatabase.getInstance().getReference("Users");

                                mDBReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot user: dataSnapshot.getChildren()) {

                                            User person = user.getValue(User.class);

                                            if (person != null && person.getEmail().equals(retrievedUser.getEmail())) {

                                                // Collect all data from the edit texts
                                                String fullName = mEditText_fullName.getText().toString();
                                                String username = mEditText_username.getText().toString();
                                                String email = mEditText_email.getText().toString();
                                                String phone = mEditText_phone.getText().toString();
                                                String fbEmail = mEditText_facebookEmail.getText().toString();
//                                                ArrayList<String> joinedEvents = retrievedUser.getJoinedEvents();

                                                // Create a new object
                                                User mEdittedUser = new User(mUrl, fullName, username, email,
                                                        phone, fbEmail, newInterests);
                                                mDBReference.child(user.getKey()).setValue(mEdittedUser);

                                                if (getContext() != null) {

                                                    Toast.makeText(getContext(), R.string.toast_changesSaved, Toast.LENGTH_LONG).show();

                                                    // Close the activity
                                                    mEditProfileInterface.closeEditProfileActivity();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

//                                // Compare both lists
//                                int sameInterestCount = 0;
//                                for (int i = 0; i < retrievedUser.getInterests().size()-1; i++) {
//
//                                    for (int j = 0; j < interests.length; j++) {
//
//                                        if (retrievedUser.getInterests().get(i).equals(interests[j])) {
//
//                                            sameInterestCount += 1;
//                                        }
//                                    }
//                                }
//
//                                // Check if the count is the same as the amount of items on the retrieved array
//                                if (sameInterestCount == retrievedUser.getInterests().size()) {
//
//                                    // Nothing is different, just grab the values from the text view
//
//                                } else {
//
//                                    // Something has changed, grab
//                                }
                            }


                        } else {

                            mEditText_facebookEmail.setError(ERROR_EMPTY_FIELDS);
                        }

                    } else {

                        mEditText_phone.setError(ERROR_EMPTY_FIELDS);
                    }
                } else {

                    mEditText_email.setError(ERROR_EMPTY_FIELDS);
                }

            } else {

                mEditText_username.setError(ERROR_EMPTY_FIELDS);
            }
        } else {

            mEditText_fullName.setError(ERROR_EMPTY_FIELDS);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView_editProfile:

                // Open gallery if the image view is tapped
                openGallery();
                break;
        }
    }
}

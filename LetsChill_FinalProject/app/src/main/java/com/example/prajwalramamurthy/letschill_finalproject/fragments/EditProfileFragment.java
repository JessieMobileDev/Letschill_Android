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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.prajwalramamurthy.letschill_finalproject.utility.ImageDownloadHandler;
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
    private EditText mEditText_fullName, mEditText_username, mEditText_phone,
            mEditText_facebookEmail;
    private TextView mTextView_interests;
    private ImageView mImageView_profilePicture;
    private Button mButton_chooseInterests;
    private ConstraintLayout mContraintLayout_verified;
    private User retrievedUser;
    private DatabaseReference mDBReference;
    private Bitmap mBitmap;
    private String mUrl, mPhotoPath;
    private Intent mGalleryIntent, mCropIntent;
    private Uri mImageUri;
    private boolean didSelectAnImage = false;
    private EditProfileInterface mEditProfileInterface;
    private ArrayList<String> mNewInterests;
    private Bundle allTypedData;

    // Constants
    private static final String ARG_LOGGED_USER = "ARG_LOGGED_USER";
    private static final String ARG_ALL_TYPED_DATA = "ARG_ALL_TYPED_DATA";
    private static final String ARG_INTERESTS = "ARG_INTERESTS";
    private static final String ERROR_EMPTY_FIELDS = "Do not leave this field empty";
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";
    private static final String BUNDLE_FULL_NAME = "BUNDLE_FULL_NAME";
    private static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";
    private static final String BUNDLE_PHONE = "BUNDLE_PHONE";
    private static final String BUNDLE_FB_EMAIL = "BUNDLE_FB_EMAIL";
    private static final String BUNDLE_INTERESTS = "BUNDLE_INTERESTS";
    private static final String BUNDLE_IMAGE = "BUNDLE_IMAGE";
    private static final String BUNDLE_EMAIL = "BUNDLE_EMAIL";
    private static final String BUNDLE_ID = "BUNDLE_ID";

    public interface EditProfileInterface {

        void closeEditProfileActivity();
        void openInterestsActivity(User loggedUser, int openedFromProfile, Bundle allTypedData,
                                   ArrayList<String> interests);
    }

    public static EditProfileFragment newInstance(User loggedUser, Bundle allTypedData, ArrayList<String> newInterests) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, loggedUser);
        args.putBundle(ARG_ALL_TYPED_DATA, allTypedData);
        args.putStringArrayList(ARG_INTERESTS, newInterests);

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
            mEditText_phone = getView().findViewById(R.id.editText_editProfile_phone);
            mEditText_facebookEmail = getView().findViewById(R.id.editText_editProfile_fb);
            mTextView_interests = getView().findViewById(R.id.textView_editProfile_interests);
            mButton_chooseInterests = getView().findViewById(R.id.button_editProfile_interests);
            mContraintLayout_verified = getView().findViewById(R.id.constraint_notVerified);
            mImageView_profilePicture = getView().findViewById(R.id.imageView_editProfile);

            // Collect the user object passed into this fragment
            retrievedUser = getArguments().getParcelable(ARG_LOGGED_USER);
            allTypedData = getArguments().getBundle(ARG_ALL_TYPED_DATA);
            mNewInterests = getArguments().getStringArrayList(ARG_INTERESTS);

            // Assign click listeners
            mImageView_profilePicture.setOnClickListener(this);
            mButton_chooseInterests.setOnClickListener(this);

            if (retrievedUser != null) {

                // Assign the data back to the edit texts and text views where needed
                mEditText_username.setText(retrievedUser.getUsername());

                if (!retrievedUser.getFullName().equals("N/A")) {
                    mEditText_fullName.setText(retrievedUser.getFullName());
                }
                if (!retrievedUser.getPhone().equals("N/A")) {
                    mEditText_phone.setText(retrievedUser.getPhone());
                }
                if (!retrievedUser.getFacebookEmail().equals("N/A")) {
                    mEditText_facebookEmail.setText(retrievedUser.getFacebookEmail());
                }

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

                Log.d("pic1", "onActivityCreated: pic: " + retrievedUser.getProfilePhoto());
                // Check if photo contains facebook in it, if so, download the image to display
                if (retrievedUser.getProfilePhoto().contains("https")) {

                    mImageView_profilePicture.setImageBitmap(ImageDownloadHandler
                            .downloadFacebookImageToBitmap(retrievedUser.getProfilePhoto()));
                } else {

                    if (retrievedUser.getProfilePhoto().contains("IDImages")) {

                        ImageDownloadHandler.downloadFirebaseImageAndSetBitmap
                                (retrievedUser.getProfilePhoto(), mImageView_profilePicture);
                    }
                }

                mPhotoPath = retrievedUser.getProfilePhoto();
            }

            if (allTypedData != null && mNewInterests != null) {

                // Paste all the data back into the edit texts
                if (!allTypedData.getString(BUNDLE_FULL_NAME).isEmpty()) {
                    mEditText_fullName.setText(allTypedData.getString(BUNDLE_FULL_NAME));
                }
                if (!allTypedData.getString(BUNDLE_USERNAME).isEmpty()) {
                    mEditText_username.setText(allTypedData.getString(BUNDLE_USERNAME));
                }
                if (!allTypedData.getString(BUNDLE_PHONE).isEmpty()) {
                    mEditText_phone.setText(allTypedData.getString(BUNDLE_PHONE));
                }
                if (!allTypedData.getString(BUNDLE_FB_EMAIL).isEmpty()) {
                    mEditText_facebookEmail.setText(allTypedData.getString(BUNDLE_FB_EMAIL));
                }

                // Concatenate all the interests into one string, breaking line right after each one
//                ArrayList<String> userInterests = (ArrayList<String>)FirebaseDatabase.getInstance().getReference("Users").child(retrievedUser.getUserID()).child("interests");
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < mNewInterests.size(); i++) {

                    if (i == mNewInterests.size() - 1) {

                        sb.append(mNewInterests.get(i));
                    } else {

                        sb.append(mNewInterests.get(i)).append("\n");
                    }
                }

                mTextView_interests.setText(sb.toString());

                // Check if photo contains facebook in it, if so, download the image to display
                String photoPath = allTypedData.getString(BUNDLE_IMAGE);

                Log.d("pic1-2", "onActivityCreated: pic: " + photoPath);

                if (photoPath != null) {

                    if (photoPath.contains("https")) {

                        mImageView_profilePicture.setImageBitmap(ImageDownloadHandler
                                .downloadFacebookImageToBitmap(photoPath));
                    } else {

                        if (photoPath.contains("IDImages")) {

                            ImageDownloadHandler.downloadFirebaseImageAndSetBitmap
                                    (photoPath, mImageView_profilePicture);
                        }
                    }
                }
            }

            // Add a listener to the phone edit text so we can insert ( ) and - to the phone number
            mEditText_phone.addTextChangedListener(new TextWatcher() {

                int length = 0;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    String str = mEditText_phone.getText().toString();
                    length = str.length();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    String str = mEditText_phone.getText().toString();

                    if(str.length() == 1 && length < str.length()){
                        mEditText_phone.setText("");
                        mEditText_phone.append("(" + str);
                    }
                    if(str.length() == 4 && length < str.length()){//len check for backspace
                        mEditText_phone.append(")");
                    }
                    if(str.length() == 8 && length < str.length()){//len check for backspace
                        mEditText_phone.append("-");
                    }
                }
            });
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
        if (!mEditText_username.getText().toString().isEmpty()) {

            if (!mTextView_interests.getText().toString().isEmpty()) {

//                String[] interests = mTextView_interests.getText().toString().split("\n");
//
//                final ArrayList<String> newInterests = new ArrayList<>(Arrays.asList(interests));

                mDBReference = FirebaseDatabase.getInstance().getReference("Users");

                mDBReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot user: dataSnapshot.getChildren()) {

                            User person = user.getValue(User.class);

//                            if (retrievedUser != null) {

                                if (person != null && person.getEmail().equals(retrievedUser.getEmail())) {

                                    // Variables to be saved back in the database
                                    String fullName = "Not defined";
                                    String username = "Not defined";
                                    String phone = "Not defined";
                                    String fbEmail = "Not defined";

                                    // Full name, phone number and facebook email are not required
                                    // If they are empty, save in the database as "Not defined"
                                    if (!mEditText_fullName.getText().toString().isEmpty()) {

                                        fullName = mEditText_fullName.getText().toString();
                                        mDBReference.child(retrievedUser.getUserID()).child("fullName").setValue(fullName);
                                    }
                                    if (!mEditText_phone.getText().toString().isEmpty()) {

                                        phone = mEditText_phone.getText().toString();
                                        mDBReference.child(retrievedUser.getUserID()).child("phone").setValue(phone);
                                    }
                                    if (!mEditText_facebookEmail.getText().toString().isEmpty()) {

                                        fbEmail = mEditText_facebookEmail.getText().toString();
                                        mDBReference.child(retrievedUser.getUserID()).child("facebookEmail").setValue(fbEmail);
                                    }
                                    if (didSelectAnImage) {

                                        mDBReference.child(retrievedUser.getUserID()).child("profilePhoto").setValue(mUrl);
                                    }

                                    // Username, email and interests are required in order to save the changes
                                    username = mEditText_username.getText().toString();

                                    // Save each individual piece of info separately
                                    mDBReference.child(retrievedUser.getUserID()).child("username").setValue(username);


//                                                ArrayList<String> joinedEvents = retrievedUser.getJoinedEvents();


                                    if (getContext() != null) {

                                        Toast.makeText(getContext(), R.string.toast_changesSaved, Toast.LENGTH_LONG).show();


                                    }
                                    // Close the activity
                                    mEditProfileInterface.closeEditProfileActivity();
                                }
//                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {

            mEditText_username.setError(ERROR_EMPTY_FIELDS);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView_editProfile:

                // Open gallery if the image view is tapped
                openGallery();
                break;

            case R.id.button_editProfile_interests:

                // Retrieve whatever was typed in the fields and save in a bundle
                Bundle allTypedData = new Bundle();
                allTypedData.putString(BUNDLE_FULL_NAME, mEditText_fullName.getText().toString());
                allTypedData.putString(BUNDLE_USERNAME, mEditText_username.getText().toString());
                allTypedData.putString(BUNDLE_PHONE, mEditText_phone.getText().toString());
                allTypedData.putString(BUNDLE_FB_EMAIL, mEditText_facebookEmail.getText().toString());
                allTypedData.putString(BUNDLE_IMAGE, mPhotoPath);
//                allTypedData.putString(BUNDLE_EMAIL, retrievedUser.getEmail());
//                allTypedData.putString(BUNDLE_ID, retrievedUser.getUserID());

                Log.d("pic", "onClick: pic (when clicking interests button): " + mPhotoPath);

                // Split the interests string and turn into an array
                String[] interests = mTextView_interests.getText().toString().split("\n");
                ArrayList<String> interestsArrayList = new ArrayList<>(Arrays.asList(interests));

                mEditProfileInterface.openInterestsActivity(retrievedUser, 1, allTypedData, interestsArrayList);
                break;
        }
    }
}

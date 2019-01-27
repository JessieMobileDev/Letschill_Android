package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MapActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    // Variables
    private EditText mEditText_Name, mEditText_Description, mLocation, mEditText_TimeStart,
            mEditText_TimeEnd, mParticipants, mEditText_Date;
    private Spinner mCategories;
    private CheckBox mCheckBox_IsRecurring, mCheckBox_PublicOrPrivate;
    private ImageView mImageView_eventBackground;
    private DatabaseReference mDatabase;
    private Button mButton_saveButton, mButton_mapButton;
    private Intent mGalleryIntent, mCropIntent;
    private File mFile;
    private Uri mImageUri;
    private CreateEventFragmentInterface mCreateEventFragmentInterface;
    private SharedPreferences mPrefs;
    private StorageReference mStorage;
    private FirebaseUser mFirebaseUser;
    private String mUid;
    private DatabaseReference mDBReference;
    private String url = "";
    private ProgressBar mProgressBar;
    private boolean didSelectNewImage = false;
    private Bitmap mBitmap;
    private boolean mFirstInstance;
    private Bitmap newBitmap;

    // Constants
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";
    private static final String ARG_ADDRESS = "ARG_ADDRESS";
    private static final String ARG_FIRSTINSTANCE = "ARG_FIRSTINSTANCE";
    private static final String BUNDLE_IMAGE = "BUNDLE_IMAGE";
    private static final String BUNDLE_NAME = "BUNDLE_NAME";
    private static final String BUNDLE_LOCATION = "BUNDLE_LOCATION";
    private static final String BUNDLE_DATE = "BUNDLE_DATE";
    private static final String BUNDLE_TIMESTART = "BUNDLE_TIMESTART";
    private static final String BUNDLE_TIMEEND = "BUNDLE_TIMEEND";
    private static final String BUNDLE_DESCRIPTION = "BUNDLE_DESCRIPTION";
    public static final String BUNDLE_PARTICIPANTS = "BUNDLE_PARTICIPANTS";
    private static final String BUNDLE_CATEGORY = "BUNDLE_CATEGORY";
    private static final String BUNDLE_RECURRING = "BUNDLE_RECURRING";
    private static final String BUNDLE_PUBLIC = "BUNDLE_PUBLIC";
    private static final String BUNDLE_DIDSELECTNEWIMAGE = "BUNDLE_DIDSELECTNEWIMAGE";

    public interface CreateEventFragmentInterface {

        void closeCreateEventActivity();
        void imageUploader();
        void openMapActivity(Bundle bundle);
    }

    public static CreateEventFragment newInstance(String mAddress, Bundle allDataBundle) {

        Bundle args = new Bundle();
        args.putString(MapActivity.INTENT_RESULT_ADDRESS, mAddress);
        args.putBundle(MapFragment.ARG_ALL_DATA_BUNDLE, allDataBundle);

        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify if the interface is an instance of this context
        if (context instanceof CreateEventFragmentInterface) {
            mCreateEventFragmentInterface = (CreateEventFragmentInterface)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the storage reference
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (getView() != null && getContext() != null && getArguments() != null) {

            // Find all the views
            mEditText_Name = getView().findViewById(R.id.editText_create_eventName);
            mLocation = getView().findViewById(R.id.editText_create_location);
            mEditText_Date = getView().findViewById(R.id.editText_create_date);
            mEditText_TimeStart = getView().findViewById(R.id.editText_create_timeStart);
            mEditText_TimeEnd = getView().findViewById(R.id.editText_create_timeEnd);
            mParticipants = getView().findViewById(R.id.editText_create_participants);
            mEditText_Description = getView().findViewById(R.id.editText_create_desc);
            mCategories = getView().findViewById(R.id.spinner_create_category);
            mCheckBox_IsRecurring = getView().findViewById(R.id.checkBox_create_recurring);
            mCheckBox_PublicOrPrivate = getView().findViewById(R.id.checkBox_create_isPublic);
            mImageView_eventBackground = getView().findViewById(R.id.imageView_create_background);
            mButton_saveButton = getView().findViewById(R.id.save_createEvent_button);
            mButton_mapButton = getView().findViewById(R.id.button_map);
            mProgressBar = getView().findViewById(R.id.progressBar_createEvent_save);

            mEditText_Date.setInputType(InputType.TYPE_NULL);

            // Assign click listeners to UI elements
            mImageView_eventBackground.setOnClickListener(this);
            mEditText_Date.setOnClickListener(this);
            mEditText_TimeStart.setOnClickListener(this);
            mEditText_TimeEnd.setOnClickListener(this);
            mButton_saveButton.setOnClickListener(this);
            mButton_mapButton.setOnClickListener(this);

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            // Check if the bundle passed to this fragment is not null
            Bundle allDataBundle = getArguments().getBundle(MapFragment.ARG_ALL_DATA_BUNDLE);
            String address = getArguments().getString(MapActivity.INTENT_RESULT_ADDRESS);



            if (allDataBundle != null) {
                Log.d("address", "onActivityCreated: reloading frag address: " + address +
                        " - Participants: " + allDataBundle.getString(BUNDLE_PARTICIPANTS));

                boolean imageWasSelected = allDataBundle.getBoolean(BUNDLE_DIDSELECTNEWIMAGE);
                if (imageWasSelected) {

                    // If an image was selected previously, then recover from bundle
                    mBitmap = allDataBundle.getParcelable(BUNDLE_IMAGE);
                    mImageView_eventBackground.setImageBitmap(mBitmap);
                }

                // Pass the data back to the fields
                mEditText_Name.setText(allDataBundle.getString(BUNDLE_NAME));
                mEditText_Date.setText(allDataBundle.getString(BUNDLE_DATE));
                mEditText_TimeStart.setText(allDataBundle.getString(BUNDLE_TIMESTART));
                mEditText_TimeEnd.setText(allDataBundle.getString(BUNDLE_TIMEEND));
                mEditText_Description.setText(allDataBundle.getString(BUNDLE_DESCRIPTION));
                mParticipants.setText(allDataBundle.getString(BUNDLE_PARTICIPANTS));
                mCategories.setSelection(allDataBundle.getInt(BUNDLE_CATEGORY));
                mLocation.setText(allDataBundle.getString(BUNDLE_LOCATION));
                mCheckBox_PublicOrPrivate.setChecked(allDataBundle.getBoolean(BUNDLE_PUBLIC));
                mCheckBox_IsRecurring.setChecked(allDataBundle.getBoolean(BUNDLE_RECURRING));

                // Apply the new address to the edit text
                mLocation.setText(address);

                // Apply the condition for "didSelectNewImage" variable
                didSelectNewImage = allDataBundle.getBoolean(BUNDLE_DIDSELECTNEWIMAGE);
            }

        }
    }

    public void openMap()
    {

        Toast.makeText(getContext(), R.string.map_open_toast, Toast.LENGTH_LONG).show();
        // Collect all the data from the form and pass through the interface
        Bundle allDataBundle = new Bundle();
        if (didSelectNewImage) {

            // If an image was selected, then save the uploaded image to the bundle
            Bitmap bitmap = ((BitmapDrawable)mImageView_eventBackground.getDrawable()).getBitmap();
            allDataBundle.putParcelable(BUNDLE_IMAGE, bitmap);
        }

        allDataBundle.putString(BUNDLE_NAME, mEditText_Name.getText().toString());
        allDataBundle.putString(BUNDLE_DATE, mEditText_Date.getText().toString());
        allDataBundle.putString(BUNDLE_TIMESTART, mEditText_TimeStart.getText().toString());
        allDataBundle.putString(BUNDLE_TIMEEND, mEditText_TimeEnd.getText().toString());
        allDataBundle.putString(BUNDLE_DESCRIPTION, mEditText_Description.getText().toString());
        allDataBundle.putString(BUNDLE_PARTICIPANTS, mParticipants.getText().toString());
        allDataBundle.putInt(BUNDLE_CATEGORY, mCategories.getSelectedItemPosition());
        allDataBundle.putString(BUNDLE_LOCATION, mLocation.getText().toString());
        allDataBundle.putBoolean(BUNDLE_PUBLIC, mCheckBox_PublicOrPrivate.isChecked());
        allDataBundle.putBoolean(BUNDLE_RECURRING, mCheckBox_IsRecurring.isChecked());
        allDataBundle.putBoolean(BUNDLE_DIDSELECTNEWIMAGE, didSelectNewImage);

        // Open the MapActivity
        mCreateEventFragmentInterface.openMapActivity(allDataBundle);

        Log.d("test", "openMap: it was pressed");

    }

    public void uploadImage()
    {
        mGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mGalleryIntent, "Select image from Gallery"), 2);

    }

    public void displayImage( Bitmap bitmap )
    {
        if(getView() != null) {
            ((ImageView) (getView()).findViewById(R.id.imageView_create_background)).setImageBitmap(bitmap);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            Log.d("test", "onActivityResult: inside request code 1");
            if (data != null) {

                Log.d("test", "onActivityResult: inside request code 1 - data not null");
                Bundle mBundle = data.getExtras();

                mBitmap = mBundle.getParcelable("data");

                if (mBitmap != null) {

                    Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 400, 300, true);
                    mImageView_eventBackground.setImageBitmap(newBitmap);

                    didSelectNewImage = true;
                }

                Log.d("test", "onActivityResult: inside request code 1 - bitmap: " + mBitmap.toString());
            }

        } else if (requestCode == 2) {

            if (data != null) {

                mImageUri = data.getData();

                if (getContext() != null) {

                    try {

                        mBitmap = Bitmap.createScaledBitmap((MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri)), 400, 300, true);
//                        newBitmap = Bitmap.createScaledBitmap(mBitmap, 400, 300, true);

                        mImageView_eventBackground.setImageBitmap(mBitmap);

                        didSelectNewImage = true;

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
//               cropImage();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity(), MenuIntentHandler.CREATE_EVENT_ACTIVITY);
        }

        return false;

    }

    private void saveEventDataToDatabase() {

        if (mEditText_Name.getText().toString().isEmpty() || mLocation.getText().toString().isEmpty()
                || mEditText_Date.getText().toString().isEmpty() || mEditText_TimeStart.getText().toString().isEmpty()
                || mParticipants.getText().toString().isEmpty() || mEditText_Description.getText().toString().isEmpty()
                || mEditText_TimeEnd.getText().toString().isEmpty()) {

            // Show toast if fields are left blank
            Toast.makeText(getContext(), R.string.create_toast_empty, Toast.LENGTH_LONG).show();

        } else {

            // Validate the fields
            // Cases: 1. Title and description must be longer than 2 characters
            //        2. Title must not be longer than 50 characters
            //        3. Description must not be longer than 280 characters
            //        4. Start time and end time cannot be the same value

            if (mEditText_Name.getText().length() >= 2 && mEditText_Name.getText().length() <= 50) {

                if (mEditText_Description.getText().length() >= 2 && mEditText_Description.getText().length() <= 280) {

                    // Make the progress bar show up
                    mProgressBar.setVisibility(View.VISIBLE);

                    if (FormValidation.isStartTimeBeforeEndTime(mEditText_TimeStart.getText().toString(),
                            mEditText_TimeEnd.getText().toString())) {

                        Log.d("opa", "saveEventDataToDatabase: participants: " + mParticipants.getText().toString());

                        if (Integer.valueOf(mParticipants.getText().toString()) != 0
                                || Integer.valueOf(mParticipants.getText().toString()) != 1
                                || Integer.valueOf(mParticipants.getText().toString()) < 1000) {

//
                            // Disable save button
                            mButton_saveButton.setEnabled(false);

                            // Make the progress bar show up
                            mProgressBar.setVisibility(View.VISIBLE);

                            // Catch and store the user input and pass it to our data model
                            final String mEvtName = mEditText_Name.getText().toString();
                            final String mEvtDesc = mEditText_Description.getText().toString();
                            final String mEvtLocation = mLocation.getText().toString();
                            final String mEvtTimeStart = mEditText_TimeStart.getText().toString();
                            final String mEvtTimeEnd = mEditText_TimeEnd.getText().toString();
                            final String mEvtDate = mEditText_Date.getText().toString();
                            final String mEvtPart = mParticipants.getText().toString();
                            final String mEvtCategory = mCategories.getSelectedItem().toString();

                            // Retrieve the username from the current logged in user
                            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUid = mFirebaseUser.getUid();

                            mDBReference = FirebaseDatabase.getInstance().getReference("Users");

                            mDBReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Read each data from uid
                                    String mUsername = dataSnapshot.child(mUid).child("username").getValue(String.class);

                                    String device_token = dataSnapshot.child(mUid).child("device_token").getValue(String.class);

                                    String current_token = FirebaseInstanceId.getInstance().getToken();

                                    if(!device_token.equals(current_token)) {
                                        mDBReference.child(mUid).child("device_token").setValue(current_token);
                                    }

                                    Log.d("test", "onDataChange: USERNAMEEEEEE: " + mUsername);

                                    //Date date = new Date();

                                    Date date;

                                    String dateString = "Month";

                                    try {

                                        // Convert the string date to a date variable and extract the month out of it
                                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                        date = mSimpleDateFormat.parse(mEvtDate);
                                        dateString = String.valueOf(date.getMonth());

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String mEventId = mDatabase.child("Events").child(dateString).push().getKey();

                                    if (mEventId != null) {

                                        ArrayList<String> mJoinedPeopleIds = new ArrayList<>();
                                        mJoinedPeopleIds.add(mUid);

                                        Event newEvent = new Event(mEventId, mEvtName, mEvtLocation, mEvtDate, mEvtTimeStart, mEvtTimeEnd, mEvtDesc,
                                                mEvtPart, mEvtCategory, mUsername, mCheckBox_IsRecurring.isChecked(),
                                                mCheckBox_PublicOrPrivate.isChecked(), url, false,
                                                getAddressFromString(mEvtLocation).getLatitude(), getAddressFromString(mEvtLocation).getLongitude(),
                                                0, mFirebaseUser.getUid(), mJoinedPeopleIds);

                                        mDatabase.child("Events").child(dateString).child(mEvtDate).child(mEventId).setValue(newEvent);

                                        if (getContext() != null) {

                                            // show toast for confirmation
                                            Toast.makeText(getContext(), R.string.string_event_success, Toast.LENGTH_LONG).show();
                                        }

                                        // Make the progress bar disappear
                                        mProgressBar.setVisibility(View.GONE);

                                        // Exit the current activity
                                        mCreateEventFragmentInterface.closeCreateEventActivity();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {

//                            mParticipants.setError(getString(R.string.max_participants_string));
                            mParticipants.setError("Must be at least 2 participants and less than 1000");
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                    } else {

                        // Make the progress bar disappear
                        mProgressBar.setVisibility(View.GONE);

                        mEditText_TimeStart.setError(getResources().getString(R.string.editText_error_sameTime));
                    }

                } else {

                    mEditText_Description.setError(getResources().getString(R.string.editText_error_description));
                }
            } else {

                mEditText_Name.setError(getResources().getString(R.string.editText_error_title));
            }
        }
    }

    private Address getAddressFromString(String address) {

        List<Address> addresses = new ArrayList<>();

        if (getContext() != null) {

            Geocoder geocoder = new Geocoder(getContext());

            // Try to find the location
            try {

                addresses = geocoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
        // Isolate the address
        return addresses.get(0);
    }



    private void saveImagetoStorageDatabase()
    {

        if (didSelectNewImage) {
            // will save our ID image to our database


            // get reference
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("IDImages");

            StorageReference imagesRef = storageRef.child(String.valueOf((new Date()).getTime()) + ".jpg");

            // convert bitmap
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(data);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    url = taskSnapshot.getMetadata().getPath();

                    if (ConnectionHandler.isConnected(getContext())) {
                        saveEventDataToDatabase();
                    }
                }
            });


        } else {

            Toast.makeText(getContext(), R.string.toast_create_imageBack, Toast.LENGTH_LONG).show();
        }
    }

    private void timePickerDialog(int mStartOrEndTime)
    {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;


        if (mStartOrEndTime == 0) { // Start Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    if(selectedHour >= 12) {
                        mEditText_TimeStart.setText(String.format("%02d:%02d" + " PM", selectedHour, selectedMinute));
                    }
                    else
                    {
                        mEditText_TimeStart.setText(String.format("%02d:%02d" + " AM", selectedHour, selectedMinute));
                    }
                }
            }, hour, minute, false);//No 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        } else if (mStartOrEndTime == 1) { // End Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(selectedHour >= 12) {
                        mEditText_TimeEnd.setText(String.format("%02d:%02d" + " PM", selectedHour, selectedMinute));
                    }
                    else
                    {
                        mEditText_TimeEnd.setText(String.format("%02d:%02d" + " AM", selectedHour, selectedMinute));
                    }
                }
            }, hour, minute, false);//No 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    // shows the date picker when user clicks on select date
    private void pickerDialog()
    {
        if(getContext() != null)
        {
            // Retrieve the current date
            Calendar mCalendar = Calendar.getInstance();
            int mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            int mCurrentMonth = mCalendar.get(Calendar.MONTH);
            int mCurrentYear = mCalendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(getContext(), this, mCurrentYear, mCurrentMonth, mCurrentDay);
            datePicker.show();
        }
    }

    // function to format date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        StringBuffer sb = new StringBuffer();

        String hireDate = formatter.format(calendar.getTime(), sb, new FieldPosition(0)).toString();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        StringBuffer infoType = new StringBuffer();

        String stringPref = sharedPref.getString("dateType", hireDate);

        SimpleDateFormat simpDate = new SimpleDateFormat(stringPref);

        String dateToDisplay = simpDate.format(calendar.getTime(), infoType, new FieldPosition(0)).toString();

        mEditText_Date.setText(dateToDisplay);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView_create_background:

                // If the user selects the background image, gallery is opened
                uploadImage();
                break;
            case R.id.editText_create_date:

                // When the date edit text is tapped, date picker is opened
                pickerDialog();
                break;
            case R.id.editText_create_timeStart:

                // When the time edit text is tapped, time picker is opened
                timePickerDialog(0);
                break;
            case R.id.editText_create_timeEnd:

                // When the time edit text is tapped, time picker is opened
                timePickerDialog(1);
                break;
            case R.id.save_createEvent_button:

                // If save button is tapped, all collected data is stored in the database
                saveImagetoStorageDatabase();

                break;
            case R.id.button_map:

                if (ConnectionHandler.isConnected(getContext())) {
                    // If map button is tapped
                    openMap();

                }
                break;
        }
    }
}

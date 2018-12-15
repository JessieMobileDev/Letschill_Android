package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    // Variables
    private EditText mEditText_Name, mEditText_Description, mLocation, mEditText_TimeStart, mEditText_TimeEnd, mParticipants, mEditText_Date;
    private Spinner mCategories;
    private CheckBox mCheckBox_IsRecurring, mCheckBox_PublicOrPrivate;
    private ImageView mImageView_eventBackground;
    private DatabaseReference mDatabase;
    private Button mButton_saveButton, mButton_mapButton;
    private Intent mGalleryIntent, mCropIntent;
    private Uri mImageUri;
    private CreateEventFragmentInterface mCreateEventFragmentInterface;
    private SharedPreferences mPrefs;
    private StorageReference mStorage;

    // Constants
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";


    public interface CreateEventFragmentInterface {

        void closeCreateEventActivity();
    }

    public static CreateEventFragment newInstance() {

        Bundle args = new Bundle();

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

        if (getView() != null && getContext() != null) {

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

        }
    }

    public void openMap()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void uploadImage()
    {
        
        mGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mGalleryIntent, "Select Image from Gallery"), 2);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            Log.d("test", "onActivityResult: inside request code 1");
            if (data != null) {

                Log.d("test", "onActivityResult: inside request code 1 - data not null");
                Bundle mBundle = data.getExtras();

                Bitmap mBitmap = mBundle.getParcelable("data");

                mImageView_eventBackground.setImageBitmap(mBitmap);

//                // TODO: save the bitmap to the database
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                if (mBitmap != null) {
//
//                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] mByteData = baos.toByteArray();
//
//                    UploadTask mUploadTask = mStorage.putBytes(mByteData);
//
//                    // Upload the image bytes to Firebase storage
//                    mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            Log.d("test", "onSuccess: image uploaded successfully");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                }


                Log.d("test", "onActivityResult: inside request code 1 - bitmap: " + mBitmap.toString());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity());
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

            if (mEditText_Name.getText().length() >= 2 && mEditText_Name.getText().length() <= 50) {

                if (mEditText_Description.getText().length() >= 2 && mEditText_Description.getText().length() <= 280) {

                    // Catch and store the user input and pass it to our data model
                    String mEvtName = mEditText_Name.getText().toString();
                    String mEvtDesc = mEditText_Description.getText().toString();
                    String mEvtLocation = mLocation.getText().toString();
                    String mEvtTimeStart = mEditText_TimeStart.getText().toString();
                    String mEvtTimeEnd = mEditText_TimeEnd.getText().toString();
                    String mEvtDate = mEditText_Date.getText().toString();
                    String mEvtPart = mParticipants.getText().toString();
                    String mEvtCategory = mCategories.getSelectedItem().toString();

                    // Retrieve the user's uid from SharedPreferences
                    String mUserUid = mPrefs.getString(SignUpFragment.PREFS_USER_UID, "default");

                    Event newEvent = new Event(mEvtName, mEvtLocation, mEvtDate, mEvtTimeStart, mEvtTimeEnd, mEvtDesc,
                            mEvtPart, mEvtCategory, mUserUid, mCheckBox_IsRecurring.isChecked(),
                            mCheckBox_PublicOrPrivate.isChecked());

                    mDatabase.child("Events").push().setValue(newEvent);

                    // show toast for confirmation
                    Toast.makeText(getContext(), "Event successfully created.", Toast.LENGTH_LONG).show();

                    // Exit the current activity
                    mCreateEventFragmentInterface.closeCreateEventActivity();

                } else {

                    mEditText_Description.setError(getResources().getString(R.string.editText_error_description));
                }
            } else {

                mEditText_Name.setError(getResources().getString(R.string.editText_error_title));
            }
        }
    }

    private void timePickerDialog(int mStartOrEndTime)
    {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;

        if (mStartOrEndTime == 0) { // Start Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    mEditText_TimeStart.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        } else if (mStartOrEndTime == 1) { // End Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    mEditText_TimeEnd.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time

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
                saveEventDataToDatabase();
                break;
            case R.id.button_map:

                // If map button is tapped, in-built map is opened
                openMap();
                break;
        }
    }
}

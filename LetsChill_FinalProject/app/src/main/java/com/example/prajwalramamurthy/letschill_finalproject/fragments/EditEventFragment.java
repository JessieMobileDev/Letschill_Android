package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.DatePickerDialog;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditEventFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    // Variables
    private ImageView mImageView_eventImage;
    private EditText mEditText_eventTitle, mEditText_eventDate, mEditText_eventStartTime, mEditText_EndEvent,
            mEditText_participantsCount, mEditText_location, mEditText_description;
    private CheckBox mCheckBox_isPublic, mCheckBox_isRecurring;
    private Spinner mSpinner_category;
    private Button mButton_map, mButton_save, mButton_delete;
    private DatabaseReference mDBReference;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private String mUid;
    private EditEventInterface mEditEventInterface;
    private Intent mGalleryIntent, mCropIntent;
    private Uri mImageUri;

    // Constants
    private static final String ARGS_OBJECT = "ARGS_OBJECT";
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";

    public interface EditEventInterface {

        void closeEditEventActivity();

    }

    public static EditEventFragment newInstance(Event mEvent) {
        
        Bundle args = new Bundle();
        args.putParcelable(ARGS_OBJECT, mEvent);
        
        EditEventFragment fragment = new EditEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditEventInterface) {

            mEditEventInterface = (EditEventInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {

            // Get views
            mEditText_eventTitle = getView().findViewById(R.id.editText_eventTitle_edit);
            mEditText_eventDate = getView().findViewById(R.id.editText_date_edit);
            mEditText_eventStartTime = getView().findViewById(R.id.editText_startTime_edit);
            mEditText_EndEvent = getView().findViewById(R.id.editText_endTime_edit);
            mEditText_location = getView().findViewById(R.id.editText_location_edit);
            mEditText_description = getView().findViewById(R.id.editText_description_edit);
            mEditText_participantsCount = getView().findViewById(R.id.editText_participants_edit);
            mSpinner_category = getView().findViewById(R.id.spinner_category_edit);
            mCheckBox_isPublic = getView().findViewById(R.id.checkBox_isPublic_edit);
            mCheckBox_isRecurring = getView().findViewById(R.id.checkBox_recurring_edit);
            mButton_map = getView().findViewById(R.id.button_map_edit);
            mButton_save = getView().findViewById(R.id.save_editEvent_button);
            mButton_delete = getView().findViewById(R.id.button_delete_edit);

            // Assign click listeners
            mButton_save.setOnClickListener(this);
            mButton_delete.setOnClickListener(this);
            mButton_map.setOnClickListener(this);
            mEditText_eventStartTime.setOnClickListener(this);
            mEditText_eventDate.setOnClickListener(this);

            // Retrieve the object that was passed into this fragment
            Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

            // Assign values to the fields
            if (mEvent != null) {

                mEditText_eventTitle.setText(mEvent.getmEventName());
                mEditText_eventDate.setText(mEvent.getmEventDate());
                mEditText_eventStartTime.setText(mEvent.getmEventTimeStart());
                mEditText_EndEvent.setText(mEvent.getmEventTimeFinish());
                mEditText_location.setText(mEvent.getmEventLocation());
                mEditText_description.setText(mEvent.getmDescription());
                mEditText_participantsCount.setText(mEvent.getmParticipants());

                String[] mSpinnerValues = getResources().getStringArray(R.array.spinner_category);

                for (int i = 0; i < mSpinnerValues.length; i++) {

                    if (mSpinnerValues[i].equals(mEvent.getmCategory())) {

                        mSpinner_category.setSelection(i);
                    }
                }

                if (mEvent.ismIsRecurringEvent()) {

                    mCheckBox_isRecurring.setChecked(true);
                }

                if (mEvent.ismPublicOrPrivate()) {

                    mCheckBox_isPublic.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView_edit_eventImage:

                // If the user selects the background image, gallery is opened
                uploadImage();

                break;
            case R.id.editText_startTime_edit:

                // When the time edit text is tapped, time picker is opened
                timePickerDialog(0);

                break;
            case R.id.editText_endTime_edit:

                // When the time edit text is tapped, time picker is opened
                timePickerDialog(1);

                break;
            case R.id.editText_date_edit:

                // When the date edit text is tapped, date picker is opened
                pickerDialog();

                break;
            case R.id.save_editEvent_button:

                saveEditEventToDatabase();

                break;
            case R.id.button_delete_edit:
                break;
            case R.id.button_map_edit:

                // If map button is tapped, in-built map is opened
                openMap();

                break;
        }
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

                mImageView_eventImage.setImageBitmap(mBitmap);


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

    public void openMap()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    // shows the date picker when user clicks on select date
    private void pickerDialog() {

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

    private void timePickerDialog(int mStartOrEndTime) {

        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;

        if (mStartOrEndTime == 0) { // Start Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    mEditText_eventStartTime.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        } else if (mStartOrEndTime == 1) { // End Time

            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    mEditText_EndEvent.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
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

        mEditText_eventDate.setText(dateToDisplay);

    }

    private void saveEditEventToDatabase() {

        if (mEditText_eventTitle.getText().toString().isEmpty() || mEditText_location.getText().toString().isEmpty()
                || mEditText_description.getText().toString().isEmpty() || mEditText_eventStartTime.getText().toString().isEmpty()
                || mEditText_EndEvent.getText().toString().isEmpty() || mEditText_participantsCount.getText().toString().isEmpty()
                || mEditText_eventDate.getText().toString().isEmpty()) {

            // Show toast if fields are left blank
            Toast.makeText(getContext(), R.string.create_toast_empty, Toast.LENGTH_LONG).show();

        } else {

            // Validate the fields
            // Cases: 1. Title and description must be longer than 2 characters
            //        2. Title must not be longer than 50 characters
            //        3. Description must not be longer than 280 characters

            if (mEditText_eventTitle.getText().toString().length() >= 2 && mEditText_eventTitle.getText().toString().length() <= 50) {

                if (mEditText_description.getText().toString().length() >= 2 && mEditText_description.getText().length() <= 280) {

                    // Catch and store the user input and pass it to our data model
                    final String mEvtName = mEditText_eventTitle.getText().toString();
                    final String mEvtDesc = mEditText_description.getText().toString();
                    final String mEvtLocation = mEditText_location.getText().toString();
                    final String mEvtTimeStart = mEditText_eventStartTime.getText().toString();
                    final String mEvtTimeEnd = mEditText_EndEvent.getText().toString();
                    final String mEvtDate = mEditText_eventDate.getText().toString();
                    final String mEvtPart = mEditText_participantsCount.getText().toString();
                    final String mEvtCategory = mSpinner_category.getSelectedItem().toString();
                    final boolean mIsPublic = mCheckBox_isPublic.isChecked();
                    final boolean mIsRecurring = mCheckBox_isRecurring.isChecked();

                    // Retrieve the object that was passed into this fragment
                    final Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

                    if (mEvent != null) {

                        Log.d("test", "saveEditEventToDatabase: event id: " + mEvent.getmEventId());

                        /*
                        String mUsername = dataSnapshot.child(mUid).child("username").getValue(String.class);
                         */
                        // Get the database reference
                        mDBReference = FirebaseDatabase.getInstance().getReference("Events").child(mEvent.getmEventId());

                        // Retrieve the username from the current logged in user
                        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        mUid = mFirebaseUser.getUid();


                        FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("username").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String mUsername = dataSnapshot.getValue(String.class);

                                Log.d("test", "saveEditEventToDatabase: retrieved NAME: " + mUsername);

                                // Make a new event object with the new data to be stored
                                Event mEdittedEvent = new Event(mEvent.getmEventId(), mEvtName, mEvtLocation, mEvtDate, mEvtTimeStart,
                                        mEvtTimeEnd, mEvtDesc, mEvtPart, mEvtCategory, mUsername, mIsRecurring, mIsPublic);


                                // Save the new object to the database under the same uid
                                mDBReference.setValue(mEdittedEvent);

                                // Show a toast when changes were saved
                                Toast.makeText(getContext(), R.string.toast_changesSaved, Toast.LENGTH_LONG).show();

                                // Close this activity
                                mEditEventInterface.closeEditEventActivity();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }
        }

    }
}

package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.InputType;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventFragment extends Fragment implements DatePickerDialog.OnDateSetListener
{

    // member variables
    private EditText mName, mDesc, mLocation, mTimeStart, mTimeEnd, mParticipants, mDate;
    private Spinner mCategories;
    private CheckBox mIsRecurring, mPublicOrPrivate;
    private ImageView eventBackground;
    private static final int PICTURE_REQUEST = 0x0101;
    private DatabaseReference mDatabase;
    private Button saveButton, mapButton;



    public static CreateEventFragment newInstance() {

        Bundle args = new Bundle();

        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
            mName = getView().findViewById(R.id.editText_create_eventName);
            mLocation = getView().findViewById(R.id.editText_create_location);
            mDate = getView().findViewById(R.id.editText_create_date);
            mTimeStart = getView().findViewById(R.id.editText_create_time);
            mTimeEnd = getView().findViewById(R.id.editText_create_time2);
            mParticipants = getView().findViewById(R.id.editText_create_participants);
            mDesc = getView().findViewById(R.id.editText_create_desc);
            mCategories = getView().findViewById(R.id.spinner_create_category);
            mIsRecurring = getView().findViewById(R.id.checkBox_create_recurring);
            mPublicOrPrivate = getView().findViewById(R.id.checkBox_create_isPublic);
            eventBackground = getView().findViewById(R.id.imageView_create_background);
            saveButton = getView().findViewById(R.id.save_test_button);
            mapButton = getView().findViewById(R.id.button_map);

            mDate.setInputType(InputType.TYPE_NULL);


            // If the user selects the background image then open images
            eventBackground.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // call our method that opens gallery
                    uploadImage();
                }
            });

            // When the date edit text is selected it will open up a date picker.
            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerDialog();
                }
            });

            mTimeStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timePickerDialog();
                }
            });

            mTimeEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timePickerDialog();
                }
            });

            // if save button is clicked it will save it to the database
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    saveEventDataToDatabase();
                }
            });

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMap();
                }
            });


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
        // then create a new intent
        Intent intentCamera = new Intent(Intent.ACTION_PICK);

        intentCamera.setType("image/jpeg");
        // start activity
        startActivityForResult(intentCamera, PICTURE_REQUEST);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Integer itemId = item.getItemId();

        switch(itemId) {
            // when my save button is clicked this is what will run
            case R.id.save_test_button:
            {
                saveEventDataToDatabase();
                return true;
            }
            case R.id.button_save:
            {
                saveEventDataToDatabase();
                return true;
            }
        }
        return false;

    }

    private void saveEventDataToDatabase()
    {
        if (mName.getText().toString().isEmpty() || mLocation.getText().toString().isEmpty()
                || mDate.getText().toString().isEmpty() || mTimeStart.getText().toString().isEmpty()
                || mParticipants.getText().toString().isEmpty() || mDesc.getText().toString().isEmpty()
                || mTimeEnd.getText().toString().isEmpty())
        {
            // Show toast if fields are left blank
            Toast.makeText(getContext(), R.string.create_toast_empty, Toast.LENGTH_LONG).show();
        }
        else
        {
            // catch and store the user input and pass it to our data model
            String mEvtName = mName.getText().toString();
            String mEvtDesc = mDesc.getText().toString();
            String mEvtLocation = mLocation.getText().toString();
            String mEvtTimeStart = mTimeStart.getText().toString();
            String mEvtTimeEnd = mTimeEnd.getText().toString();
            String mEvtDate = mDate.getText().toString();
            String mEvtPart = mParticipants.getText().toString();
            String mEvtCategory = mCategories.getSelectedItem().toString();

            Boolean mEvtRecurr;
            Boolean mEvtPublic;

            if(mIsRecurring.isChecked() || mPublicOrPrivate.isChecked())
            {
                mEvtRecurr = true;
                mEvtPublic = true;
            }
            else
            {
                mEvtRecurr = false;
                mEvtPublic = false;
            }

            Event newEvent = new Event(mEvtName, mEvtLocation, mEvtDate, mEvtTimeStart, mEvtTimeEnd, mEvtDesc, mEvtPart, mEvtCategory, mEvtRecurr, mEvtPublic);

            mDatabase.child("Events").push().setValue(newEvent);
            // show toast for confirmation
            Toast.makeText(getContext(), "Congrats! Event successfully created.", Toast.LENGTH_LONG).show();



        }
    }

    private void timePickerDialog()
    {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mTimeStart.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


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

        mDate.setText(dateToDisplay);

    }
}

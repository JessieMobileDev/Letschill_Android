package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.AddressValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private final Handler mHandler = new Handler();
    private Bitmap mBitmap;
    private String url;
    private boolean didSelectAnImage = false;

    // Constants
    public static final String ARGS_OBJECT = "ARGS_OBJECT";
    private static final String CROP_EXTRA = "crop";
    private static final String CROP_OUTPUTX = "outputX";
    private static final String CROP_OUTPUTY = "outputY";
    private static final String CROP_ASPECTX = "aspectX";
    private static final String CROP_ASPECTY = "aspectY";
    private static final String CROP_SCALEUP_IFNEEDED = "scaleUpIfNeeded";
    private static final String CROP_RETURN_DATA = "return-data";
    public static final String EXTRA_DB_DELETE_ID = "EXTRA_DB_DELETE_ID";

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
            mImageView_eventImage = getView().findViewById(R.id.imageView_edit_eventImage);

            // Assign click listeners
            mButton_save.setOnClickListener(this);
            mButton_delete.setOnClickListener(this);
            mButton_map.setOnClickListener(this);
            mEditText_eventStartTime.setOnClickListener(this);
            mEditText_eventDate.setOnClickListener(this);
            mImageView_eventImage.setOnClickListener(this);

            // Retrieve the object that was passed into this fragment
            Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

            // Assign values to the fields
            if (mEvent != null) {

                if (mEvent.getmUrl() != null && !mEvent.getmUrl().isEmpty()) {

                    // Variables
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference mStorageReference = mFirebaseStorage.getReference().child(mEvent.getmUrl());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            mImageView_eventImage.setImageBitmap(bmp);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }

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

            // Check who is current logged on and see if it's the same as the host name
            // If they match, display the "delete button", otherwise don't
            getCurrentSignedInUser();
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

                // When the delete button is tapped, the event in the database has the variable
                // isDeleted set to true and does not show anymore on the app
                deleteEvent();

                break;
            case R.id.button_map_edit:

                // If map button is tapped, in-built map is opened
                openMap();

                break;
        }
    }

    private void getCurrentSignedInUser() {

        // Retrieve the username from the current logged in user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mFirebaseUser.getUid();

        FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getArguments() != null) {

                    // Get the current logged in user
                    String mUsername = dataSnapshot.getValue(String.class);

                    // Retrieve the event that was passed to this fragment
                    Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

                    if (mEvent != null) {

                        if (!mEvent.getmHost().equals(mUsername)) {

                            // Make the delete button disappear
                            mButton_delete.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteEvent() {

        // Start an intent service to request the deletion of an event
        // We'll change the variable isDelete from false to true
        if (getContext() != null && getArguments() != null) {

            if (ConnectionHandler.isConnected(getContext())) {

                // Retrieve the selected event from the arguments
                final Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

                // Display a dialog box asking the user to be sure of their action
                AlertDialog.Builder mDeleteAlert = new AlertDialog.Builder(getContext());
                mDeleteAlert.setTitle(R.string.alert_title);
                mDeleteAlert.setMessage(R.string.alert_message);
                mDeleteAlert.setNegativeButton(R.string.alert_No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mDeleteAlert.setPositiveButton(R.string.alert_Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mEvent != null) {

                            Intent mChangeVariableIntent = new Intent(getContext(), DatabaseEventIntentService.class);
                            mChangeVariableIntent.putExtra(DatabaseEventIntentService.EXTRA_RESULT_RECEIVER, new DatabaseEventDataReceiver());
                            mChangeVariableIntent.putExtra(EXTRA_DB_DELETE_ID, 2);
                            mChangeVariableIntent.putExtra(ARGS_OBJECT, mEvent);
                            getContext().startService(mChangeVariableIntent);
                        }
                    }
                });
                mDeleteAlert.create().show();
            }
        }

    }

    public class DatabaseEventDataReceiver extends ResultReceiver {

        DatabaseEventDataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (getContext() != null) {

                // The event was deleted successfully, so display a toast
                Toast.makeText(getContext(), R.string.toast_deleted, Toast.LENGTH_SHORT).show();

                // Close this activity
                mEditEventInterface.closeEditEventActivity();
            }
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

                mBitmap = mBundle.getParcelable("data");

                mImageView_eventImage.setImageBitmap(mBitmap);

                didSelectAnImage = true;


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

                        // Get reference
                        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("IDImages");
                        StorageReference imagesRef = storageRef.child(String.valueOf((new Date()).getTime()) + ".jpg");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if (didSelectAnImage) {
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        } else {
                            mBitmap = ((BitmapDrawable)mImageView_eventImage.getDrawable()).getBitmap();
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        }

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

//                    url = taskSnapshot.getUploadSessionUri().toString();
                                url = taskSnapshot.getMetadata().getPath();

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

                                        // TODO: Redo the image capture again
                                        if (getContext() != null) {

                                            // Make a new event object with the new data to be stored
                                            ArrayList<String> mJoinedUsersId = new ArrayList<>();
                                            Event mEdittedEvent = new Event(mEvent.getmEventId(), mEvtName, mEvtLocation, mEvtDate, mEvtTimeStart,
                                                    mEvtTimeEnd, mEvtDesc, mEvtPart, mEvtCategory, mUsername, mIsRecurring, mIsPublic, url,
                                                    false, AddressValidation.getAddressFromString(mEvtLocation, getContext()).getLatitude(),
                                                    AddressValidation.getAddressFromString(mEvtLocation, getContext()).getLongitude(),
                                                    0, mJoinedUsersId);

                                            // Save the new object to the database under the same uid
                                            mDBReference.setValue(mEdittedEvent);

                                            // Show a toast when changes were saved
                                            Toast.makeText(getContext(), R.string.toast_changesSaved, Toast.LENGTH_LONG).show();

                                            // Close this activity
                                            mEditEventInterface.closeEditEventActivity();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        });

                    }
                }
            }
        }

    }
}

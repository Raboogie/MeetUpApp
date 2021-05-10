package com.example.meetupapp.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.meetupapp.MapActivity;
import com.example.meetupapp.MessageActivity;
import com.example.meetupapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeetUpPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetUpPlanFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String item;

    //Initialized variables
    private EditText location;
    private EditText date;
    private EditText time;
    private EditText message;
    private Button confirmButton;
    private Button searchButton;
    private Spinner userNameSelect;
    public final int MAP_REQUEST = 1;

    //Getting the database
    DatabaseReference myRef;

    public MeetUpPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetUpPlanFragment newInstance(String param1, String param2) {
        MeetUpPlanFragment fragment = new MeetUpPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<String> userNameList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_meetup_plan, container, false);
        location = view.findViewById(R.id.addressInput);
        date = view.findViewById(R.id.dateInput);
        time = view.findViewById(R.id.timeInput);
        message = view.findViewById(R.id.messageInput);
        confirmButton = view.findViewById(R.id.submitButton);
        confirmButton.setOnClickListener(this);
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST && resultCode == Activity.RESULT_OK) {
            location.setText(data.getStringExtra("location"));
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //Code for submit button
            case R.id.submitButton:
                String meetUpPlan = "";
                //Let the user know they didn't fill out enough if one of the required fields are empty
                if ((location.getText().toString().matches("")) || (date.getText().toString().matches("")) || (time.getText().toString().matches(""))) {
                    Toast warningMessage = Toast.makeText(this.getContext(), "Please fill out all the required fields before confirming. User is " + item, Toast.LENGTH_LONG);
                    warningMessage.show();
                } else {
                    //Bring up an alert dialog so the user can verify the meetUp plan is

                    meetUpPlan = "Location: " + location.getText().toString() +
                            "\nTime: " + time.getText().toString() +
                            "\nDate: " + date.getText().toString();

                    //Add a message if the user entered one
                    if (!message.getText().toString().matches("")) {
                        meetUpPlan += "\nMessage: " + message.getText().toString();
                    }

                    String finalizedPlan = meetUpPlan;
                    new AlertDialog.Builder(getContext())
                            .setTitle("Verify The Information")
                            .setMessage("Is this information correct? \n\n" + meetUpPlan)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendMeetUpPlanToChat(finalizedPlan);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }
                break;

            case R.id.searchButton:
                //Start map activity with the intent of getting a result which is the location
                startActivityForResult(new Intent(getContext(), MapActivity.class), MAP_REQUEST);
                break;

        }
    }

    public void sendMeetUpPlanToChat(String plan) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", plan);
        clipboard.setPrimaryClip(clip);
        //Alerting the user that the information has been copied
        Toast infoCopied = Toast.makeText(this.getContext(), "MeetUp information copied to clipboard", Toast.LENGTH_LONG);
        infoCopied.show();
    }
}
package com.apacheasif.rideShare;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    static final int SELECT_PICTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 1;
    RelativeLayout layout;
    Button btnSave;
    Button btnPicture;
    Spinner spinInterest;
    EditText fName, lName, phone;
    String itemInterest = "";
    ImageView img;
    String fn, ln, pn, itr, pic;
    private String picturePath;

    public ProfileFragment() {
    }

    @SuppressLint("ValidFragment")
    public ProfileFragment(String fname, String lname, String phone, String interest, String picture) {

        fn = fname;
        ln = lname;
        pn = phone;
        itr = interest;
        pic = picture;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        btnSave = (Button) layout.findViewById(R.id.buttonSave);
        btnPicture = (Button) layout.findViewById(R.id.buttonPicture);
        spinInterest = (Spinner) layout.findViewById(R.id.spinnerInterest);
        fName = (EditText) layout.findViewById(R.id.editTextFname);
        lName = (EditText) layout.findViewById(R.id.editTextLname);
        phone = (EditText) layout.findViewById(R.id.editTextPNo);
        spinInterest.setOnItemSelectedListener(this);
        //insert values
        List<String> interestList = new ArrayList<>();
        interestList.add("Travelling");
        interestList.add("Books");
        interestList.add("Bike");
        interestList.add("Programming");
        interestList.add("None");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, interestList);

        // attaching data adapter to spinner
        spinInterest.setAdapter(dataAdapter);

        btnSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        btnPicture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        return layout;
    }

    private void saveData() {
        String fname = fName.getText().toString();
        String lname = lName.getText().toString();
        String pNo = phone.getText().toString();
        String interest = itemInterest;

        MyDBhelperProfile myDB = new MyDBhelperProfile(getActivity());
        myDB.saveProfile(fname, lname, pNo, interest, picturePath);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("a", picturePath);


        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        itemInterest = parent.getItemAtPosition(position).toString();

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

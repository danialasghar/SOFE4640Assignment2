package com.mobiledev.locationfinder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mobiledev.locationfinder.db.DatabaseHelper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AddLocationActivity extends AppCompatActivity {
    TextInputEditText addressInput;
    TextInputEditText latInput;
    TextInputEditText longInput;
    Geocoder coder;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        //Set back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar_addLocation);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);

        //Instantiate variables
        this.addressInput = findViewById(R.id.address_input);
        this.latInput = findViewById(R.id.lat_input);
        this.longInput = findViewById(R.id.long_input);
        this.coder = new Geocoder(this);
        this.db = new DatabaseHelper(this);


        setTextFieldListeners();
    }

    public void setTextFieldListeners(){
        //Create text listener for address field
        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (addressInput.hasFocus()){
                    if(TextUtils.isEmpty(s.toString().trim())){
                        //Sets the Longitude and Latitude fields editable if address is empty
                        latInput.setEnabled(true);
                        longInput.setEnabled(true);
                    }
                    else{
                        //Disables the Longitude and Latitude fields if address field has content
                        latInput.setEnabled(false);
                        longInput.setEnabled(false);
                        //Removes the existing Lat-Long values if the user wants to update address field
                        latInput.setText("");
                        longInput.setText("");
                    }
                }
            }
        });

        //Create a text change listener for latitude field
        latInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (latInput.hasFocus()){
                    if(TextUtils.isEmpty(s.toString().trim()) && TextUtils.isEmpty(longInput.getText().toString().trim())){
                        //Enables the address field if the field is empty
                        addressInput.setEnabled(true);
                    }
                    else{
                        //Disables the Address fields if user inputting land and long
                        addressInput.setEnabled(false);
                        //Removes the Address fields existing address to avoid conflict
                        addressInput.setText("");
                    }
                }
            }
        });

        //Create a listener for longitude field
        longInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (longInput.hasFocus()){
                    if(TextUtils.isEmpty(s.toString().trim()) && TextUtils.isEmpty(latInput.getText().toString().trim())){
                        //Enables the address field if the lat-long fields are empty to avoid conflict
                        addressInput.setEnabled(true);

                    }
                    else{
                        //Disables the Address fields if user inputting land and long
                        addressInput.setEnabled(false);
                        //Removes the Address fields existing address to avoid conflict
                        addressInput.setText("");
                    }
                }
            }
        });
    }

    public void saveLocationOnClick(View view) {
        if (TextUtils.isEmpty(addressInput.getText().toString().trim()) && TextUtils.isEmpty(latInput.getText().toString().trim()) && TextUtils.isEmpty(longInput.getText().toString())){
            Toast.makeText(this, "Enter data into fields", Toast.LENGTH_SHORT).show();
        } else {
            if(!addressInput.getText().toString().equals("")){
                LatLng calculatedLatLng = getLatLongFromAddress(addressInput.getText().toString());
                while (calculatedLatLng == null){
                    Toast.makeText(this, "Incorrect address, check and try again", Toast.LENGTH_SHORT).show();
                    calculatedLatLng = getLatLongFromAddress(addressInput.getText().toString());
                }
                String latitudeResult = Double.toString(calculatedLatLng.latitude);
                String longitudeResult = Double.toString(calculatedLatLng.longitude);
                saveLocation(addressInput.getText().toString().trim(), latitudeResult, longitudeResult);
            }
            else {
                if((latInput.getText().toString().equals("") && !longInput.getText().toString().equals("")) || (!latInput.getText().toString().equals("") && longInput.getText().toString().equals(""))){
                    Toast.makeText(this, "Please make sure both Latitude and Longitude is entered!", Toast.LENGTH_SHORT).show();
                }
                else{
                    double latitudeInput = Double.parseDouble(latInput.getText().toString().trim());
                    double longitudeInput = Double.parseDouble(longInput.getText().toString().trim());

                    if (validLongitude(longitudeInput) && validLatitude(latitudeInput)){
                        String address = getAddressFromLatLong(latitudeInput, longitudeInput);
                        while (address == null){
                            Toast.makeText(this, "Incorrect Pair of Latitude and Longitude, check and try again", Toast.LENGTH_SHORT).show();
                            latitudeInput = Double.parseDouble(latInput.getText().toString().trim());
                            longitudeInput = Double.parseDouble(longInput.getText().toString().trim());
                            address = getAddressFromLatLong(latitudeInput, longitudeInput);
                        }
                        saveLocation(address, Double.toString(latitudeInput), Double.toString(longitudeInput));
                    }
                }
            }
        }

    }

    public void saveLocation(String address, String latitude, String longitude){
        long result = db.saveLocation(address, latitude, longitude);
        if (result == -1){
            Toast.makeText(this, "Location Save Failed" , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Saved Successfully" , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public boolean validLatitude (double latitude){
        boolean result = (latitude >=-90 && latitude <= 90);
        if (result == false){
            Toast.makeText(this, "Invalid: Latitude must be Greater/Equal than -90 and Less/Equal than 90", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public boolean validLongitude (double longitude){
        boolean result = (longitude >=-180 && longitude <= 180);
        if (result == false){
            Toast.makeText(this, "Invalid: Longitude must be Greater/Equal than -180 and Less/Equal than 180", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public LatLng getLatLongFromAddress( String strAddress){
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,2);
            if (address == null){
                return null;
            }
            else {
                Address location  = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return p1;
    }

    public String getAddressFromLatLong (double latitude, double longitude){
        List<Address> address;
        String calculatedAddress = "";

        try {
            address = coder.getFromLocation(latitude, longitude, 2);
            if (address == null){
                return null;
            }
            else {
                calculatedAddress = address.get(0).getAddressLine(0).toString();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return calculatedAddress;
    }
}
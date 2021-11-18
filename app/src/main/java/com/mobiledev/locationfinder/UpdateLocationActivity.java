package com.mobiledev.locationfinder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.mobiledev.locationfinder.db.DatabaseHelper;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UpdateLocationActivity extends AppCompatActivity {

    TextInputEditText addressInput;
    TextInputEditText latInput;
    TextInputEditText longInput;
    Geocoder coder;
    DatabaseHelper db;

    String id;
    String address;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);

        //Set back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar_updateLocation);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);

        //Instantiate variables
        this.addressInput = findViewById(R.id.address_input_update);
        this.latInput = findViewById(R.id.lat_input_update);
        this.longInput = findViewById(R.id.long_input_update);
        this.coder = new Geocoder(this);
        this.db = new DatabaseHelper(this);

        populateExistingData();
        setTextFieldsListeners();

    }

    public void populateExistingData(){
        //Get the incoming intent and set the global variables
        Intent intent = getIntent();
        this.id = intent.getStringExtra("id");
        this.address = intent.getStringExtra("address");
        this.latitude = intent.getStringExtra("latitude");
        this.longitude = intent.getStringExtra("longitude");

        //Populate the fields with the incoming data
        addressInput.setText(address);
        latInput.setText(latitude);
        longInput.setText(longitude);
    }

    public void setTextFieldsListeners(){
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
                        //Disabes the Longitude and Latitude fields if address field has content
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_location) {
            //Creates an alert asking the user to confirm if they want to delete
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Delete Note");
            builder.setMessage("Please confirm if you want to delete");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteLocation();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteLocation(){
        //calls the database to delete the record using the id
        db.deleteLocation(id);
        Toast.makeText(this, "Location Deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean validLatitude (double latitude){
        //Checks if the latitude input is valid
        boolean result = (latitude >=-90 && latitude <= 90);
        if (result == false){
            Toast.makeText(this, "Invalid: Latitude must be Greater/Equal than -90 and Less/Equal than 90", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public boolean validLongitude (double longitude){
        //Checks if the longitude input is valid
        boolean result = (longitude >=-180 && longitude <= 180);
        if (result == false){
            Toast.makeText(this, "Invalid: Longitude must be Greater/Equal than -180 and Less/Equal than 180", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public LatLng getLatLongFromAddress(String strAddress){
        List<Address> address;
        LatLng p1 = null;
        //Gets the lat-long coordinates using a physical address and sends it back
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
        //Gets the physical address using a pair of coordinates and sends it back
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

    public void updateLocationOnClick(View view) {
        //Checks to ensure that all fields arent empty before saving
        if (TextUtils.isEmpty(addressInput.getText().toString().trim()) && TextUtils.isEmpty(latInput.getText().toString().trim()) && TextUtils.isEmpty(longInput.getText().toString())){
            Toast.makeText(this, "Enter data into fields", Toast.LENGTH_SHORT).show();
        } else {
            //If the address field is populated then get its value and call appropriate methods to update the db
            if(!addressInput.getText().toString().equals("")){
                LatLng calculatedLatLng = getLatLongFromAddress(addressInput.getText().toString());
                while (calculatedLatLng == null){
                    Toast.makeText(this, "Incorrect address, check and try again", Toast.LENGTH_SHORT).show();
                    calculatedLatLng = getLatLongFromAddress(addressInput.getText().toString());
                }
                String latitudeResult = Double.toString(calculatedLatLng.latitude);
                String longitudeResult = Double.toString(calculatedLatLng.longitude);
                updateLocation(id, addressInput.getText().toString().trim(), latitudeResult, longitudeResult);
            }
            else {
                //Checks whether either of the Lat-Long coordinates is empty, if so prompt user to enter both
                if((latInput.getText().toString().equals("") && !longInput.getText().toString().equals("")) || (!latInput.getText().toString().equals("") && longInput.getText().toString().equals(""))){
                    Toast.makeText(this, "Please make sure both Latitude and Longitude is entered!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //get the value of the coordinates input by the user, validate them and then call the appropriate method to save it to db
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
                        updateLocation(id, address, Double.toString(latitudeInput), Double.toString(longitudeInput));
                    }
                }
            }
        }
    }

    public void updateLocation(String id, String address, String latitude, String longitude){
        //Updates the database with the parameters sent in and navigates user back to mainactivity 
        boolean result = db.updateNote(id, address, latitude, longitude);
        if (result) {
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
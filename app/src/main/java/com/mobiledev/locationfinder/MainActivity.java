package com.mobiledev.locationfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mobiledev.locationfinder.adapters.Adapter;
import com.mobiledev.locationfinder.db.DatabaseHelper;
import com.mobiledev.locationfinder.models.Location;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Adapter.MyViewHolder.OnNoteListener {
    Toolbar myToolbar;
    DatabaseHelper db;
    RecyclerView recyclerView;
    Adapter adapter;
    List<Location> locationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start the Toolbar
        Toolbar myToolbar = findViewById(R.id.topAppBar_main);
        setSupportActionBar(myToolbar);

        //Create the Database and get all existing Locations
        db = new DatabaseHelper(this);
        fetchAllLocations();

    }

    public void fetchAllLocations(){
        Cursor cursor = db.readAllData();
        this.locationsList = new ArrayList<>();
        if (cursor.getCount() == 0){
            createData();
        }
        else {
            while (cursor.moveToNext()){
                Location location = new Location();
                location.setId(cursor.getString(0));
                location.setAddress(cursor.getString(1));
                location.setLatitude(cursor.getString(2));
                location.setLongitude(cursor.getString(3));
                locationsList.add(location);
            }
        populateRecyclerView();
        }
    }



    public void getAddressFromLatLong (ArrayList<LatLng> locationsList){
        List<Address> address;
        String calculatedAddress = "";
        Geocoder coder = new Geocoder(this);
        for (int i = 0; i < 50 ; i++){
            try {
                double latitude = locationsList.get(i).latitude;
                double longitude = locationsList.get(i).longitude;
                address = coder.getFromLocation(latitude, longitude, 2);
                calculatedAddress = address.get(0).getAddressLine(0).toString();
                db.saveLocation(calculatedAddress, Double.toString(latitude), Double.toString(longitude));

            } catch (IOException e){
                e.printStackTrace();
            }
        }
        fetchAllLocations();
    }

    private void populateRecyclerView(){
        //Instantiate RecyclerView
        this.recyclerView = findViewById(R.id.recycler_view);
        adapter = new Adapter(this, MainActivity.this, locationsList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_locations);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Addresses");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.search_locations:
                Toast.makeText(this, "Search", Toast.LENGTH_LONG ).show();
                break;
            case R.id.action_add_location:
                Intent intent = new Intent(this, AddLocationActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, UpdateLocationActivity.class);
        intent.putExtra("id", locationsList.get(position).getId());
        intent.putExtra("address", locationsList.get(position).getAddress());
        intent.putExtra("latitude", locationsList.get(position).getLatitude());
        intent.putExtra("longitude", locationsList.get(position).getLongitude());
        startActivity(intent);
    }

    public void createData(){
        //Instantiate the Random class to create ints of lat and long
        ArrayList<LatLng> locationsList = new ArrayList<>();
        locationsList.add(new LatLng(44.89870597,-76.24505033));
        locationsList.add(new LatLng(44.30784753,-78.31681876));
        locationsList.add(new LatLng(42.98909763,-82.39497908));
        locationsList.add(new LatLng(43.36869473,-80.98198705));
        locationsList.add(new LatLng(48.38121456,-89.26255766));
        locationsList.add(new LatLng(48.47664633,-81.32780365));
        locationsList.add(new LatLng(43.65266674,-79.38176066));
        locationsList.add(new LatLng(43.76121043,-79.4079073));
        locationsList.add(new LatLng(44.13076608,-81.15020631));
        locationsList.add(new LatLng(42.99256976,-79.24796428));
        locationsList.add(new LatLng(43.54710058,-80.24792417));
        locationsList.add(new LatLng(44.22782049,-76.48968005));
        locationsList.add(new LatLng(42.78007425,-81.19180411));
        locationsList.add(new LatLng(43.37143641,-80.98511575));
        locationsList.add(new LatLng(43.1318532,-80.76008874));
        locationsList.add(new LatLng(44.24500014,-76.48708785));
        locationsList.add(new LatLng(44.0442449,-79.47974357));
        locationsList.add(new LatLng(43.16013695,-79.24819257));
        locationsList.add(new LatLng(45.42071192,-75.69160618));
        locationsList.add(new LatLng(43.66264802,-79.72529751));
        locationsList.add(new LatLng(44.39216301,-79.68394809));
        locationsList.add(new LatLng(43.76928902,-79.46862588));
        locationsList.add(new LatLng(49.78687051,-92.82602843));
        locationsList.add(new LatLng(43.45376777,-80.48281556));
        locationsList.add(new LatLng(43.72648663,-79.29011301));
        locationsList.add(new LatLng(43.54360527,-80.24495457));
        locationsList.add(new LatLng(44.16343755,-77.38319432));
        locationsList.add(new LatLng(43.25547841,-79.87180121));
        locationsList.add(new LatLng(46.3159987,-79.46838517));
        locationsList.add(new LatLng(46.4919254,-80.99161737));
        locationsList.add(new LatLng(43.25523875,-79.86730902));
        locationsList.add(new LatLng(44.00903769,-77.14262392));
        locationsList.add(new LatLng(44.25198521,-76.94966451));
        locationsList.add(new LatLng(43.14031974,-80.26662988));
        locationsList.add(new LatLng(45.01825687,-74.72998041));
        locationsList.add(new LatLng(42.40355242,-82.20800969));
        locationsList.add(new LatLng(42.31922646,-83.03698826));
        locationsList.add(new LatLng(43.66024371,-79.38317604));
        locationsList.add(new LatLng(48.47693816,-81.32680209));
        locationsList.add(new LatLng(43.35863819,-80.31259613));
        locationsList.add(new LatLng(44.305298,-78.31662327));
        locationsList.add(new LatLng(43.65398787,-79.38718652));
        locationsList.add(new LatLng(43.91956662,-80.10303034));
        locationsList.add(new LatLng(44.16575296,-77.38286318));
        locationsList.add(new LatLng(43.66145484,-79.72588701));
        locationsList.add(new LatLng(44.23448812,-76.48089675));
        locationsList.add(new LatLng(43.75224583,-79.53670186));
        locationsList.add(new LatLng(44.56230223,-80.92629489));
        locationsList.add(new LatLng(43.65163934,-79.38753313));
        locationsList.add(new LatLng(45.61987306,-74.69157567));
        getAddressFromLatLong(locationsList);
    }
}


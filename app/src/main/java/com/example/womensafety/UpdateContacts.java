package com.example.womensafety;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class UpdateContacts extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    Button contact1, contact2, contact3, contact4, remove1, remove2, remove3, remove4;
    TextView mob1, mob2, mob3, mob4;
    SharedPreferences sp;
    String lati,longi;

    private static final String TAG = "MainActivity";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;
    private long FASTEST_INTERVAL = 2000;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);

       // ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();

        contact1 = findViewById(R.id.c1);
        contact2 = findViewById(R.id.c2);
        contact3 = findViewById(R.id.c3);
        contact4 = findViewById(R.id.c4);

        remove1 = findViewById(R.id.r1);
        remove2 = findViewById(R.id.r2);
        remove3 = findViewById(R.id.r3);
        remove4 = findViewById(R.id.r4);

        mob1 = findViewById(R.id.num1);
        mob2 = findViewById(R.id.num2);
        mob3 = findViewById(R.id.num3);
        mob4 = findViewById(R.id.num4);

        load();


        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        checkLocation(); //check whether location service is enable or not in your  phone


    }

    public void updContacts(View view) {
        int choose_button;
        if (view.getId() == R.id.c1)
            choose_button = 1;
        else if (view.getId() == R.id.c2)
            choose_button = 2;
        else if (view.getId() == R.id.c3)
            choose_button = 3;
        else
            choose_button = 4;

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, choose_button);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == 0)
            return;
        String cNumber = null;
        String name = null;

        if (resultCode == Activity.RESULT_OK) {

            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {


                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    cNumber = phones.getString(phones.getColumnIndex("data1"));
                    System.out.println("number is:" + cNumber);
                }
                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            }
        }
        storeData(name, cNumber, reqCode);
        showSelectedNumber(cNumber);
        switch (reqCode) {
            case 1:
                contact1.setText(name);
                break;
            case 2:
                contact2.setText(name);
                break;
            case 3:
                contact3.setText(name);
                break;
            case 4:
                contact4.setText(name);
                break;
        }
        load();
    }

    public void showSelectedNumber(String number) {
        Toast.makeText(this, "Selected contact Number: " + number, Toast.LENGTH_LONG).show();
        //   CoordinatorLayout cl = findViewById(R.id.coordinatorLayout);
    }

    public void storeData(String name, String cNumber, int choose_button) {
        sp = getSharedPreferences("Contacts", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Integer.toString(choose_button), cNumber);
        ed.putString(Integer.toString(choose_button) + "_1", name);
        ed.commit();
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void load() {
        sp = getSharedPreferences("Contacts", Context.MODE_PRIVATE);

        String s1 = sp.getString("1_1", "choose Contact 1");
        String s2 = sp.getString("2_1", "choose Contact 2");
        String s3 = sp.getString("3_1", "choose Contact 3");
        String s4 = sp.getString("4_1", "choose Contact 4");

        String n1 = sp.getString("1", "No number");
        String n2 = sp.getString("2", "No number");
        String n3 = sp.getString("3", "No number");
        String n4 = sp.getString("4", "No number");

        contact1.setText(s1);
        contact2.setText(s2);
        contact3.setText(s3);
        contact4.setText(s4);

        mob1.setText(n1);
        mob2.setText(n2);
        mob3.setText(n3);
        mob4.setText(n4);

        return;

    }

    public void deleteContact(View v) {
        String s;
        SharedPreferences.Editor ed = sp.edit();
        switch (v.getId()) {
            case R.id.r1:
                s = sp.getString("1_1", "choose Contact 1");
                if (s != "choose Contact 1") {
                    ed.remove("1_1");
                    ed.commit();
                    ed.remove("1");
                    ed.commit();
                    contact1.setText("choose Contact 1");
                    mob1.setText("No number");
                    Toast.makeText(getApplicationContext(), "Contact Removed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.r2:
                s = sp.getString("2_1", "choose Contact 2");
                if (s != "choose Contact 2") {
                    ed.remove("2_1");
                    ed.commit();
                    ed.remove("2");
                    ed.commit();
                    contact2.setText("choose Contact 2");
                    mob2.setText("No number");
                    Toast.makeText(getApplicationContext(), "Contact Removed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.r3:
                s = sp.getString("3_1", "choose Contact 3");
                if (s != "choose Contact 3") {
                    ed.remove("3_1");
                    ed.commit();
                    ed.remove("3");
                    ed.commit();
                    contact3.setText("choose Contact 3");
                    mob3.setText("No number");
                    Toast.makeText(getApplicationContext(), "Contact Removed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.r4:
                s = sp.getString("4_1", "choose Contact 4");
                if (s != "choose Contact 4") {
                    ed.remove("4_1");
                    ed.commit();
                    ed.remove("4");
                    ed.commit();
                    contact4.setText("choose Contact 4");
                    mob4.setText("No number");
                    Toast.makeText(getApplicationContext(), "Contact Removed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
            startLocationUpdates();

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLocation == null) {
                startLocationUpdates();
            }
            if (mLocation != null) {

                // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
                //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
            }


        }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        lati = mLatitudeTextView.getText().toString();
        longi = mLongitudeTextView.getText().toString();
       // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
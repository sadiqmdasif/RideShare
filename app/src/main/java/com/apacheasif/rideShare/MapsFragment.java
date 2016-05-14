package com.apacheasif.rideShare;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;


public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // JSON Node names
    private static final String TAG_ITEMS = "items";
    private static final String TAG_ID = "user_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LAT = "user_lat";
    private static final String TAG_LONG = "user_long";
    private static final String TAG_LAST_UPDATED = "last_updated";
    private static final String TAG_LOCATION_STATUS = "location_status";
    private static final String TAG_DESTINATION = "destination";
    // URL to get contacts JSON
    private static String url = "http://apex.oracle.com/pls/apex/share/taftech/tempinfo/";
    // user JSONArray
    JSONArray items = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> userList;
    double mLatitude;
    double mLongitude;
    private GoogleMap mMap;
    private ProgressDialog pDialog;
    private MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng userLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mapsview, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapsView);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        userList = new ArrayList<HashMap<String, String>>();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Calling async task to get json
        new GetUsers().execute();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    void setDefaultMarker(double lat, double lng) {
        userLocation = new LatLng(lat, lng);
        Marker defaultMarker = mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("You")
                .snippet("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        defaultMarker.showInfoWindow();

    }

    void setMarker(String id, String name, String latitude, String longitude, String destination, String status, String lastUpdated) {

        // Log.d("marker","aa");

        float lat = Float.parseFloat(latitude);
        float lng = Float.parseFloat(longitude);
        float markerColor = 0;
        if (status.equals("Y")) {
            markerColor = BitmapDescriptorFactory.HUE_RED;
        } else {
            markerColor = BitmapDescriptorFactory.HUE_BLUE;
        }

        LatLng position = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(position).title(id).snippet(destination)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location changed", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Status changed", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Provider enabled", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Provider disabled", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("position", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            Log.d("position", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));

            setDefaultMarker(mLatitude, mLongitude);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            sdf.setTimeZone(TimeZone.getDefault());
            String currentDateandTime = sdf.format(new Date());

            MyDBhelperMaps myDB = new MyDBhelperMaps(getActivity());
            myDB.saveLocation(currentDateandTime, mLatitude + " " + mLongitude);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("disconnected ", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("failed", String.valueOf(mLatitude) + " " + String.valueOf(mLongitude));
    }

    private class GetUsers extends AsyncTask<Void, Void, Void> {

        String id = "";
        String name = "";
        String userLat = "";
        String userLong = "";
        String lastUpdated = "";
        String destination = "";
        String locationStatus = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    items = jsonObj.getJSONArray(TAG_ITEMS);

                    // looping through All Contacts

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
                        //Log.d("length: ",String.valueOf(items.length()));
                        id = c.getString(TAG_ID);
                        if (!c.isNull(c.getString(TAG_NAME))) {
                            name = c.getString(TAG_NAME);
                        } else {
                            name = "Anonymous";
                        }


                        userLat = c.getString(TAG_LAT);


                        userLong = c.getString(TAG_LONG);

                        if (!c.isNull(c.getString(TAG_LAST_UPDATED))) {
                            lastUpdated = c.getString(TAG_LAST_UPDATED);
                        } else {
                            lastUpdated = "Never";
                        }

                        if (!c.isNull(TAG_DESTINATION)) {
                            destination = c.getString(TAG_DESTINATION);
                        } else {
                            destination = "Not Set";
                        }
                        if (!c.isNull(TAG_LOCATION_STATUS)) {
                            locationStatus = c.getString(TAG_LOCATION_STATUS);
                        } else {
                            locationStatus = "N";
                        }


                        // tmp hashmap for single user
                        HashMap<String, String> user = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        user.put(TAG_ID, id);
                        user.put(TAG_NAME, name);
                        user.put(TAG_LAT, userLat);
                        user.put(TAG_LONG, userLong);
                        user.put(TAG_LAST_UPDATED, lastUpdated);
                        user.put(TAG_DESTINATION, destination);
                        user.put(TAG_LOCATION_STATUS, locationStatus);


                        // adding user to user list
                        userList.add(user);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Iterator iterator = userList.iterator();
            while (iterator.hasNext()) {
                // Log.d("Post Execute","-");
                HashMap map = (HashMap) iterator.next();
                String id = (String) map.get(TAG_ID);
                String name = map.get(TAG_NAME).toString();
                String userLat = map.get(TAG_LAT).toString();
                String userLong = map.get(TAG_LONG).toString();
                String lastUpdated = map.get(TAG_LAST_UPDATED).toString();
                String destination = map.get(TAG_DESTINATION).toString();
                String locationStatus = map.get(TAG_LOCATION_STATUS).toString();

                if (!userLat.contentEquals("0") && !userLong.contentEquals("0")) {
                    setMarker(id, name, userLat, userLong, destination, locationStatus, lastUpdated);
                }
            }

            if (mLatitude != 0 && mLongitude != 0) {
                setDefaultMarker(mLatitude, mLongitude);
            }


        }

    }
}

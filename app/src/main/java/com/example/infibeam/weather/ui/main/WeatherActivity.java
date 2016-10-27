package com.example.infibeam.weather.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infibeam.weather.R;
import com.example.infibeam.weather.data.model.List;
import com.example.infibeam.weather.data.model.WeatherData;
import com.example.infibeam.weather.ui.base.BaseActivity;
import com.example.infibeam.weather.util.DialogFactory;
import com.example.infibeam.weather.util.NetworkUtil;
import com.example.infibeam.weather.util.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kunal.chauhan on 10/26/2016.
 */

public class WeatherActivity extends BaseActivity implements WeatherMvpView, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject WeatherPresenter mWeatherPresenter;

    @BindView(R.id.txt_city) TextView txtCity;
    @BindView(R.id.txt_time) TextView txtTime;
    @BindView(R.id.txt_temp) TextView txtTemp;
    @BindView(R.id.txt_day1) TextView txtDay1;
    @BindView(R.id.txt_day2) TextView txtDay2;
    @BindView(R.id.txt_day3) TextView txtDay3;
    @BindView(R.id.txt_day4) TextView txtDay4;
    @BindView(R.id.txt_temp1) TextView txtTemp1;
    @BindView(R.id.txt_temp2) TextView txtTemp2;
    @BindView(R.id.txt_temp3) TextView txtTemp3;
    @BindView(R.id.txt_temp4) TextView txtTemp4;
    @BindView(R.id.img_main) ImageView imgMain;
    @BindView(R.id.img1) ImageView img1;
    @BindView(R.id.img2) ImageView img2;
    @BindView(R.id.img3) ImageView img3;
    @BindView(R.id.img4) ImageView img4;

    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        mWeatherPresenter.attachView(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeatherPresenter.detachView();
    }

    /*****
     * MVP View methods implementation
     *****/

    @Override
    public void onWeatherDataSuccess(WeatherData weatherData) {
        WeatherData mWeatherData = weatherData;

        ArrayList<List> customList = new ArrayList<>();
        customList.add(mWeatherData.getList().get(0));

        String date = Utility.getDate(mWeatherData.getList().get(0).getDtTxt());
        String sameDate = date;

        for(int i=0 ; i< mWeatherData.getList().size(); i++){

            String mDate = Utility.getDate(mWeatherData.getList().get(i).getDtTxt());

            if(!mDate.equals(sameDate))
                customList.add(mWeatherData.getList().get(i));

            sameDate = mDate;
        }

        txtCity.setText(mWeatherData.getCity().getName());
        txtTemp.setText(customList.get(0).getMain().getTemp().toString() + (char) 0x00B0);
        txtTemp1.setText(customList.get(1).getMain().getTemp().toString() + (char) 0x00B0);
        txtTemp2.setText(customList.get(2).getMain().getTemp().toString() + (char) 0x00B0);
        txtTemp3.setText(customList.get(3).getMain().getTemp().toString() + (char) 0x00B0);
        txtTemp4.setText(customList.get(4).getMain().getTemp().toString() + (char) 0x00B0);
        txtDay1.setText(Utility.getDay(customList.get(1).getDtTxt()));
        txtDay2.setText(Utility.getDay(customList.get(2).getDtTxt()));
        txtDay3.setText(Utility.getDay(customList.get(3).getDtTxt()));
        txtDay4.setText(Utility.getDay(customList.get(4).getDtTxt()));
        txtTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
        imgMain.setImageDrawable(ContextCompat.getDrawable(this,Utility.getArtResourceForWeatherCondition(customList.get(0).getWeather().get(0).getId())));
        img1.setImageDrawable(ContextCompat.getDrawable(this,Utility.getArtResourceForWeatherCondition(customList.get(1).getWeather().get(0).getId())));
        img2.setImageDrawable(ContextCompat.getDrawable(this,Utility.getArtResourceForWeatherCondition(customList.get(2).getWeather().get(0).getId())));
        img3.setImageDrawable(ContextCompat.getDrawable(this,Utility.getArtResourceForWeatherCondition(customList.get(3).getWeather().get(0).getId())));
        img4.setImageDrawable(ContextCompat.getDrawable(this,Utility.getArtResourceForWeatherCondition(customList.get(4).getWeather().get(0).getId())));

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onWeatherDataError() {
        swipeRefreshLayout.setRefreshing(false);
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_data))
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    private void getLocation() {
        if (checkLocationPermission()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                String latitude, longitude;
                latitude = String.valueOf(mLastLocation.getLatitude());
                longitude = String.valueOf(mLastLocation.getLongitude());
                Toast.makeText(this, getString(R.string.location_success), Toast.LENGTH_SHORT).show();
                if(NetworkUtil.isNetworkConnected(this)){
                    swipeRefreshLayout.setRefreshing(true);
                    mWeatherPresenter.loadWeatherData(latitude,longitude,getString(R.string.app_id));
                }
                else{
                    DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_data))
                            .show();
                    txtTime.setText("Please check your internet connection");}
                    swipeRefreshLayout.setRefreshing(false);
            }
            else{
                Toast.makeText(this, getString(R.string.location_failure), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
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
                    //mGoogleMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onRefresh() {
        getLocation();
    }
}


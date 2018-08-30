package nanang.application.id.semutsoft;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Nanang on 10/10/2017.
 */

public class LocationHelper extends Service implements LocationListener {

    private Context context;

    private boolean isGPSEnable;
    private boolean isNetworkEnable;
    public boolean canGetLocation;

    private Location location;
    private double lat;
    private double lng;

    private static final long MIN_DISTANCE_UPDATES = 10;
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public LocationHelper(Context context) {
        this.context = context;
        getLocation();
    }


    public Location getLocation() {

        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // get location from network provider fisrt
            if (isNetworkEnable) {
                canGetLocation = true;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_DISTANCE_UPDATES, MIN_TIME_UPDATES, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            }
            // get location from gps service
            if (isGPSEnable) {
                canGetLocation = true;
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_DISTANCE_UPDATES, MIN_TIME_UPDATES, this);
                    if (locationManager!=null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location!=null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public double getLat() {
        if (location!=null){
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLng() {
        if (location!=null){
            lng = location.getLongitude();
        }
        return lng;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

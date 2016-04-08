package com.example.fshen4.CS442_Places_Near_Me;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private GMapV2Direction md;
    private EditText address;
    private LatLng origin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.fshen4.CS442_Places_Near_Me.R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.example.fshen4.CS442_Places_Near_Me.R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setUpMapIfNeeded();
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
        }

        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_search);
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.5),
                (int)(drawable.getIntrinsicHeight()*0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, 1, 1);
        address = (EditText)findViewById(R.id.tAddress);
        address.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example
        address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSearchRequesting();
            }
        });

        origin = new LatLng(41.838598, -87.627383);
    }


    private void setUpMapIfNeeded() {
        return;
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 移動地圖
        moveMap(origin);
        addMarker(origin, "Hello!", "Stuart Building");
        mMap.setMyLocationEnabled(true);
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
        Intent intent = getIntent();
        Place newPlace = (Place) intent.getSerializableExtra("SelectedPlace");
        if (newPlace != null) {
            Log.d("Place", String.valueOf(newPlace.getLongitude()) + " " + String.valueOf(newPlace.getLatitude()));
            // see more on Marker: https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/Marker
            //mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .title(newPlace.getName())
                    .position(
                            new LatLng(newPlace.getLatitude(), newPlace.getLongitude()))
                    .icon(BitmapDescriptorFactory
                            .fromResource(com.example.fshen4.CS442_Places_Near_Me.R.drawable.pin))
                    .snippet(newPlace.getVicinity()));
            moveMap(new LatLng(newPlace.getLatitude(), newPlace.getLongitude()));
            Document doc = null;
            LatLng destPosition = new LatLng(newPlace.getLatitude(), newPlace.getLongitude());
            md = new GMapV2Direction(origin, destPosition, GMapV2Direction.MODE_WALKING);
            md.execute();
            try {
                doc = md.get();

                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                Polyline polylin = mMap.addPolyline(rectLine);
            } catch (Exception e) {
                //nothing to do for now
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.fshen4.Places_Near_Me/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.fshen4.Places_Near_Me/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // 移動地圖到參數指定的位置
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(13)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // 在地圖加入指定位置與標題的標記
    private void addMarker(LatLng place, String title, String snippet) {
        BitmapDescriptor icon =
                com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(com.example.fshen4.CS442_Places_Near_Me.R.mipmap.ic_map_marker);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title)
                .snippet(snippet)
                .icon(icon);

        mMap.addMarker(markerOptions);
    }
    public boolean onSearchRequesting() {
        //tracking user and calory calculation
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 1);
        return true;
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, android.content.Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        switch(reqCode) {
            case (1) : {
                if (resCode == 1) {
                    //Address newAddress = (Address)data.getParcelableExtra("SelectedAddress");
                    Place newPlace = (Place) data.getSerializableExtra("SelectedPlace");
                    Log.d("Place", String.valueOf(newPlace.getLongitude()) + " " + String.valueOf(newPlace.getLatitude()));
                    // see more on Marker: https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/Marker
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .title(newPlace.getName())
                            .position(
                                    new LatLng(newPlace.getLatitude(), newPlace.getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(com.example.fshen4.CS442_Places_Near_Me.R.drawable.pin))
                            .snippet(newPlace.getVicinity()));
                    moveMap(new LatLng(newPlace.getLatitude(), newPlace.getLongitude()));Document doc = null;
                    LatLng destPosition = new LatLng(newPlace.getLatitude(), newPlace.getLongitude());
                    md = new GMapV2Direction(origin, destPosition, GMapV2Direction.MODE_WALKING);
                    md.execute();
                    try {
                        doc = md.get();

                        ArrayList<LatLng> directionPoint = md.getDirection(doc);
                        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE);

                        for (int i = 0; i < directionPoint.size(); i++) {
                            rectLine.add(directionPoint.get(i));
                        }
                        Polyline polylin = mMap.addPolyline(rectLine);
                    } catch (Exception e) {
                        //nothing to do for now
                    }
                }
                break;
            }
            default: break;
        }
    }

    public void onZoom(View view) {
        if(view.getId() == com.example.fshen4.CS442_Places_Near_Me.R.id.Zoomin) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if(view.getId() == com.example.fshen4.CS442_Places_Near_Me.R.id.Zoomout) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

}

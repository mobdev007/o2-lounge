package com.ospring.o2lounge.fragments;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ospring.o2lounge.R;

/**
 * Created by Vetero on 12-12-2015.
 */
public class LocateFragment extends Activity {
    LatLng TutorialsPoint = new LatLng(17.4994763, 78.5632461);
    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_locate_us);

        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Marker TP = googleMap.addMarker(new MarkerOptions()
                    .position(TutorialsPoint)
                    .title("O2 Coffee Lounge, Defence Colony, Sainikpuri," +
                    " Secunderabad"));

            TP.showInfoWindow();

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(TutorialsPoint)
                    .zoom(12.8f)
                    .build();

            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);

            googleMap.moveCamera(camUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.client.itrack.fragments.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.client.itrack.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sony on 28-07-2016.
 */
public class ContactMapFragment extends Fragment  implements OnMapReadyCallback{

    View view  ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view =  inflater.inflate(R.layout.contact_map_fragment,container,false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(24.9865562, 55.1454869);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("P.O.Box:474452. EBC Building,1st Floor, Office # 13, Dubai Investment Park,UAE"));
    }
}

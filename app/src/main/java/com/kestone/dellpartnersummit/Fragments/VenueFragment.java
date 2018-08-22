package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VenueFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private TextView venueAddressTv,venueNameTv;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public VenueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venue, container, false);

        venueAddressTv = (TextView) view.findViewById(R.id.venueAddressTv);
        venueNameTv = (TextView) view.findViewById(R.id.venueNameTv);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Progress.showProgress(getContext());
        if(ConnectionCheck.connectionStatus(getActivity())){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("EmailID", UserDetails.EmailID);
                Log.d("VenueParams",jsonObject.toString());

                new VenueDataFetch(ApiUrl.Venue,jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            Progress.closeProgress();
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }



        return view;
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

    private class VenueDataFetch {
        private String url;

        private VenueDataFetch(String url, String postBody) {
            this.url = url;
            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {


                    Progress.closeProgress();

                    if(response.isSuccessful()){
                        String myResponse = response.body().string();
                        Log.d("Venue Reponse", myResponse);
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(myResponse);
                            final JSONObject jsonObject = jsonArray.getJSONObject(0);
                            final String venueName = jsonObject.getString("VenueName");
                            final String venueCity = jsonObject.getString("Address");
                            final Double lat = jsonObject.getDouble("Latitude");
                            final Double longt = jsonObject.getDouble("Longitude");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    venueNameTv.setText(venueName);
                                    venueAddressTv.setText(venueCity);
                                    setMap(lat,longt);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }



                }
            });
        }
    }

    public void setMap(final double lat,final double longt){
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                // googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(lat, longt);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Hotel Radissson").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

}

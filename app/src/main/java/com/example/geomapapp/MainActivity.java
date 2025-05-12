package com.example.geomapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.widget.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private TextView tvLatitude, tvLongitude;
    private Button btnShare;
    private double currentLat = 0.0, currentLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        btnShare = findViewById(R.id.btnShare);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnShare.setOnClickListener(v -> {
            if (currentLat != 0.0 && currentLon != 0.0) {
                String message = "Ma position actuelle :\nLatitude : " + currentLat +
                        "\nLongitude : " + currentLon +
                        "\nhttps://maps.google.com/?q=" + currentLat + "," + currentLon;

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(shareIntent, "Partager via"));
            } else {
                Toast.makeText(MainActivity.this, "Position non disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        tvLatitude.setText("Latitude : " + currentLat);
        tvLongitude.setText("Longitude : " + currentLon);

        LatLng current = new LatLng(currentLat, currentLon);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(current).title("Vous êtes ici"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(@NonNull String provider) {}
    @Override public void onProviderDisabled(@NonNull String provider) {}
}

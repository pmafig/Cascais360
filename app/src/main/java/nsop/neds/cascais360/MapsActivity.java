package nsop.neds.cascais360;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nsop.neds.cascais360.Entities.Json.Detail;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.Json.Point;
import nsop.neds.cascais360.Manager.ControlsManager.CustomExpandableListAdapter;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, OnRequestPermissionsResultCallback{


    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private List<Point> point_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.






        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout menuFragment = findViewById(R.id.menu);
        new MenuManager(this, toolbar, menuFragment, null);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView tv_title = findViewById(R.id.event_route_title);
        tv_title.setText(getIntent().getStringExtra(Variables.Title));

        ImageView logo = findViewById(R.id.maps_logo);
        logo.setColorFilter(Color.parseColor(Settings.colors.YearColor));

        ExpandableListAdapter expandableListAdapter;
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        List<String> points = new ArrayList<>();

        Type PointTypeList = new TypeToken<ArrayList<Point>>(){}.getType();
        point_list = new Gson().fromJson(getIntent().getStringExtra(Variables.MapPoints), PointTypeList);

        int i = 1;

        for (Point p: point_list) {
            points.add(String.format("%s - %s", i++, p.Title));
        }

        expandableListDetail.put(Settings.labels.Route, points);

        List<String>  expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }


        mMap.setMapType(googleMap.MAP_TYPE_SATELLITE);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        int i = 1;

        PolylineOptions pol = new PolylineOptions();

        for (Point p: point_list) {

            LatLng _pinpoint = new LatLng(p.Coordinates.Lat, p.Coordinates.Lng);

            pol.add(_pinpoint);

            builder.include(_pinpoint);

            float sp = 20;
            int px = Math.round(sp * getResources().getDisplayMetrics().scaledDensity);

            Marker m = mMap.addMarker(new MarkerOptions().position(_pinpoint).icon(BitmapDescriptorFactory.defaultMarker(1)));

            Bitmap bitmap = Bitmap.createBitmap( px, px, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            paint.setColor(Color.parseColor(Settings.colors.YearColor));

            Paint text = new Paint();
            text.setColor(getResources().getColor(R.color.colorWhite));
            text.setTextSize(px/2);

            float v = text.ascent() + text.descent();

            int xPos = canvas.getWidth() / 2;
            int yPos = (int) ((canvas.getHeight() / 2) - ((text.descent() + text.ascent()) / 2)) ;

            canvas.drawCircle(xPos,xPos,xPos, paint);

            //TODO: mellhorar a forma como está a ser desenhado o número no circulo
            if(i > 9) {
                canvas.drawText(String.valueOf(i++), (xPos - (int)(px * 0.3)), yPos, text);
            }else{
                canvas.drawText(String.valueOf(i++), (xPos - (int)(px * 0.15)), yPos, text);
            }

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

            m.setIcon(icon);
        }

        LatLngBounds bounds = builder.build();

        mMap.setLatLngBoundsForCameraTarget(builder.build());

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        pol.color(Color.parseColor(Settings.colors.YearColor));

        Polyline polyline1 = mMap.addPolyline(pol);

        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());

        polyline1.setJointType(JointType.ROUND);

        mMap.animateCamera(cu);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    //https://github.com/Vysh01/android-maps-directions/blob/master/app/src/main/java/com/thecodecity/mapsdirection/MapActivity.java

    //https://www.youtube.com/watch?v=wRDLjUK8nyU
}

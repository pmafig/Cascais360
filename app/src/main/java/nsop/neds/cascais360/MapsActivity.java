package nsop.neds.cascais360;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Point p: point_list) {

            LatLng _pinpoint = new LatLng(p.Coordinates.Lat, p.Coordinates.Lng);

            Marker m = mMap.addMarker(new MarkerOptions().position(_pinpoint).icon(BitmapDescriptorFactory.defaultMarker(1)));

            builder.include(m.getPosition());

            Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);;

            m.setIcon(icon);
        }

        LatLngBounds bounds = builder.build();

        mMap.setLatLngBoundsForCameraTarget(builder.build());

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);

    }
}

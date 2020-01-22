package nsop.neds.cascais360;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Entities.Json.Point;
import nsop.neds.cascais360.Entities.Json.PointMap;
import nsop.neds.cascais360.Entities.Maps.Directions;
import nsop.neds.cascais360.Entities.Maps.Route;
import nsop.neds.cascais360.Entities.Maps.Step;
import nsop.neds.cascais360.Manager.ControlsManager.CustomExpandableListAdapter;
import nsop.neds.cascais360.Manager.ControlsManager.DownloadImageAsync;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;
import nsop.neds.cascais360.WebApi.WebApiClient;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, OnRequestPermissionsResultCallback {


    LocationManager locationManager;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private List<PointMap> map_point_list;
    private List<Point> point_list;

    private boolean seeRoute;
    private LatLng destination;

    private LatLng origin;

    private Polyline line;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        seeRoute = getIntent().getBooleanExtra(Variables.SeeRoute, false);

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

        Type MapPointTypeList = new TypeToken<ArrayList<PointMap>>() {
        }.getType();
        Type PointTypeList = new TypeToken<ArrayList<Point>>() {
        }.getType();

        map_point_list = new Gson().fromJson(getIntent().getStringExtra(Variables.MapPoints), MapPointTypeList);
        point_list = new Gson().fromJson(getIntent().getStringExtra(Variables.Points), PointTypeList);

        if (point_list.size() > 0) {
            destination = new LatLng(point_list.get(0).Coordinates.Lat, point_list.get(0).Coordinates.Lng);
        }

        int i = 1;

        if (!seeRoute) {
            for (Point p : point_list) {
                points.add(String.format("%s - %s", i++, p.Title));
            }
        }

        expandableListDetail.put(Settings.labels.Route, points);

        List<String> expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            if(seeRoute) {
                LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                //LatLng origin = new LatLng(38.699192,-9.423563);
                DrawRoute(origin, destination);
            }
            /*double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();*/
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
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            if (mMap != null) {
                drawInfoPoints(map_point_list);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                mMap.setInfoWindowAdapter(this);
                mMap.setOnInfoWindowClickListener(this);

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    origin = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (seeRoute) {
                                        DrawRoute(origin, destination);
                                    }
                                }
                            }
                        });

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000,
                            10, locationListenerGPS);
                } else {
                    // Show rationale and request permission.
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
                }

                mMap.setMapType(googleMap.MAP_TYPE_SATELLITE);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                int i = 1;

                for (Point p : point_list) {

                    LatLng _pinpoint = new LatLng(p.Coordinates.Lat, p.Coordinates.Lng);

                    builder.include(_pinpoint);

                    float sp = 20;
                    int px = Math.round(sp * getResources().getDisplayMetrics().scaledDensity);

                    Marker m = mMap.addMarker(new MarkerOptions().position(_pinpoint).icon(BitmapDescriptorFactory.defaultMarker(1)));

                    PointMap pointMap = map_point_list.get(i - 1);
                    pointMap.Index = i;
                    m.setTag(pointMap);

                    Bitmap bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);

                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor(Settings.colors.YearColor));

                    Paint text = new Paint();
                    text.setColor(getResources().getColor(R.color.colorWhite));
                    text.setTextSize(px / 2);

                    float v = text.ascent() + text.descent();

                    int xPos = canvas.getWidth() / 2;
                    int yPos = (int) ((canvas.getHeight() / 2) - ((text.descent() + text.ascent()) / 2));

                    canvas.drawCircle(xPos, xPos, xPos, paint);

                    //TODO: mellhorar a forma como está a ser desenhado o número no circulo
                    if (i > 9) {
                        canvas.drawText(String.valueOf(i++), (xPos - (int) (px * 0.3)), yPos, text);
                    } else {
                        canvas.drawText(String.valueOf(i++), (xPos - (int) (px * 0.15)), yPos, text);
                    }

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    m.setIcon(icon);
                }

                LatLngBounds bounds = builder.build();

                mMap.setLatLngBoundsForCameraTarget(builder.build());

                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                if (seeRoute) {

                    LatLng _pinpoint = new LatLng(point_list.get(0).Coordinates.Lat, point_list.get(0).Coordinates.Lng);

                    LatLng _pinpoint2 = new LatLng(point_list.get(1).Coordinates.Lat, point_list.get(1).Coordinates.Lng);

                    //DrawRoute(_pinpoint, _pinpoint2);
                }

        /*polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());

        polyline.setJointType(JointType.ROUND);*/

                mMap.moveCamera(cu);
                //mMap.animateCamera(cu);
            }

        }catch (Exception ex){
            String e = ex.getMessage();
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {

        PointMap point = (PointMap) marker.getTag();

        ViewPager viewPager = findViewById(R.id.sliderPager);

        viewPager.setCurrentItem(point.Index-1, false);

        findViewById(R.id.marker_info_wrapper).animate().alpha(1.0f);
        findViewById(R.id.marker_info_wrapper).setVisibility(View.VISIBLE);

        //TODO set destination for walking route

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

    private String getUrl(LatLng origin, LatLng dest, String directionMode, String language) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String lang = "language=" + language;
        String parameters = str_origin + "&" + str_dest + "&" + mode+ "&" + lang;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private void drawInfoPoints(List<PointMap> pointMap){

        final List<View> views = new ArrayList<>();

        int i = 1;

        for (final PointMap info: pointMap) {
            View view = View.inflate(this, R.layout.block_marker_info, null);

            TextView index = view.findViewById(R.id.marker_index);
            index.setText(String.valueOf(i++));

            Drawable bg = getDrawable(R.drawable.ic_dot);
            bg.setTint(Color.parseColor(Settings.colors.YearColor));
            index.setBackground(bg);

            TextView title = view.findViewById(R.id.marker_title);
            title.setText(info.Point.get(0).Title);

            //String html = "<style>body{ margin:0; padding:0;} p{font-family:\"montserrat_light\";} }</style><body>%s</body>";

            TextView address = view.findViewById(R.id.marker_address);
            address.setText(Html.fromHtml(info.Point.get(0).Address));
            //address.loadData(String.format(Settings.html, info.Point.get(0).Address), "text/html; charset=utf-8", "UTF-8");

            TextView latLng = view.findViewById(R.id.marker_lat_log);
            latLng.setText(String.format("Lat:%s | Lng:%s", info.Point.get(0).Coordinates.Lat, info.Point.get(0).Coordinates.Lng));

            TextView description = view.findViewById(R.id.marker_description);
            description.setText(Html.fromHtml(info.Description));
            //description.loadData(String.format(Settings.html, info.Description), "text/html; charset=utf-8", "UTF-8");

            final ImageView img = view.findViewById(R.id.frame_image);

            DownloadImageAsync obj = new DownloadImageAsync() {
                @Override
                protected void onPostExecute(Bitmap bmp) {
                    super.onPostExecute(bmp);
                    img.setImageBitmap(bmp);
                }
            };
            obj.execute(info.Images.get(0));

            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            ImageView playButton = view.findViewById(R.id.play_media);

            final Boolean[] playing = {false};
            final Boolean[] firstPlaying = {true};

            if(info.AudioGuide != null && info.AudioGuide.size() > 0){
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        try {

                            if (playing[0]) {
                                mediaPlayer.pause();
                                playing[0] = false;
                                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                            } else {
                                if(firstPlaying[0]) {
                                    Uri myUri = Uri.parse(info.AudioGuide.get(0));
                                    mediaPlayer.setDataSource(getApplicationContext(), myUri);
                                    mediaPlayer.prepare();

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            ((ImageView) v).setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                                            playing[0] = false;
                                        }
                                    });
                                    //mediaPlayer.getDuration()

                                    firstPlaying[0] = false;
                                }
                                mediaPlayer.start();
                                playing[0] = true;
                                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
                            }
                        } catch (IOException e) {
                            //Toast.makeText(this,R.string.common_signin_button_text , Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                playButton.setVisibility(View.GONE);
            }

            ImageView close = view.findViewById(R.id.marker_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.marker_info_wrapper).animate().alpha(0.0f);
                    findViewById(R.id.marker_info_wrapper).setVisibility(View.GONE);
                }
            });

            Button getDirections = view.findViewById(R.id.get_directions);
            getDirections.setText(Settings.labels.GetDirections);
            getDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(info.Point != null && info.Point.size() > 0) {
                        LatLng dest = new LatLng(info.Point.get(0).Coordinates.Lat, info.Point.get(0).Coordinates.Lng);
                        if(origin != null) {
                            DrawRoute(origin, dest);
                            findViewById(R.id.marker_info_wrapper).animate().alpha(0.0f);
                            findViewById(R.id.marker_info_wrapper).setVisibility(View.GONE);
                        }
                    }
                }
            });

            views.add(view);
        }

        final ViewPager viewPager = findViewById(R.id.sliderPager);
        final TextView numeration = findViewById(R.id.sliderPagerNumeration);

        viewPager.setAdapter(new SliderPageAdapter(views, this));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                numeration.setText(String.format("%s / %s", position + 1 , views.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    private void DrawRoute(LatLng origin, LatLng destination){
        String mode = "walking";
        String language = "pt-PT";

        if(origin != null && destination != null) {
            switch (Settings.LangCode) {
                case "en":
                    language = "en";
                    break;
            }

            WebApiClient.get(getUrl(origin, destination, mode, language), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

                        if(line != null) {
                            line.remove();
                        }

                        Directions directions = new Gson().fromJson(responseString, Directions.class);

                        List<String> points = new ArrayList<>();

                        Route route = directions.routes.get(0);

                        for (Step s : route.legs.get(0).steps){
                            points.add(String.format("%s\t%s", s.duration.text, s.distance.text));
                            points.add(Html.fromHtml(s.html_instructions).toString());
                        }

                        List<LatLng> list = PolyUtil.decode(route.overview_polyline.points);


                        PolylineOptions options = new PolylineOptions();

                        if (list != null) {
                            for (LatLng p : list) {
                                options.add(p).width(20).color(Color.parseColor(Settings.colors.YearColor));
                            }
                        }

                        line = mMap.addPolyline(options);


                        ExpandableListAdapter expandableListAdapter;
                        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

                        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

                        expandableListDetail.put(Settings.labels.Route, points);

                        List<String> expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

                        expandableListAdapter = new CustomExpandableListAdapter(getBaseContext(), expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(expandableListAdapter);

                    } catch (Exception ex) {

                    }
                }
            });
        }
    }
}

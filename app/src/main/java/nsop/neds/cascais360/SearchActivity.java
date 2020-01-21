package nsop.neds.cascais360;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Entities.Json.Coordinates;
import nsop.neds.cascais360.Entities.Json.MapMarker;
import nsop.neds.cascais360.Manager.CommonManager;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.PinpointManager;
import nsop.neds.cascais360.Manager.SearchManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Data;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;
import nsop.neds.cascais360.WebApi.WebApiClient;
import nsop.neds.cascais360.WebApi.WebApiMessages;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    LocationManager locationManager;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private Calendar calendar;
    private GoogleMap mGoogleMap;
    //private MyClusterManagerRenderer mClusterManagerRenderer;
    //private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private LatLngBounds mMapBoundary;
    //private ClusterManager<ClusterMarker> mClusterManager;

    private Marker lastMarket;

    private LatLng origin;
    private LatLng receivedOrigin;

    private int mapScale = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        String type = getIntent().getStringExtra(Variables.Type);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        new MenuManager(this, toolbar, menuFragment, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //MENU BUTTONS
        final ImageView menu_search = findViewById(R.id.menu_search_button);
        menu_search.setColorFilter(Color.parseColor(Settings.colors.YearColor));

        final ImageView menu_calendar = findViewById(R.id.menu_calendar_button);
        menu_calendar.setColorFilter(getResources().getColor(R.color.colorWhite));

        final ImageView menu_map = findViewById(R.id.menu_map_button);
        menu_map.setColorFilter(getResources().getColor(R.color.colorWhite));

        //PAINELS
        final LinearLayout painel_search = findViewById(R.id.painel_search);
        painel_search.setVisibility(View.VISIBLE);
        final LinearLayout painel_calendar = findViewById(R.id.painel_calendar);
        painel_calendar.setVisibility(View.GONE);
        final LinearLayout painel_map = findViewById(R.id.painel_map);
        painel_map.setVisibility(View.GONE);

        //OTHER BUTTONS
        final Button search_button = findViewById(R.id.search_button);
        LinearLayout search_button_wrapper = findViewById(R.id.search_button_wrapper);
        search_button_wrapper.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));


        //MENU BUTTONS CLICK EVENTS
        menu_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_search.setColorFilter(Color.parseColor(Settings.colors.YearColor));
                menu_calendar.setColorFilter(getResources().getColor(R.color.colorWhite));
                menu_map.setColorFilter(getResources().getColor(R.color.colorWhite));

                painel_search.setVisibility(View.VISIBLE);
                painel_calendar.setVisibility(View.GONE);
                painel_map.setVisibility(View.GONE);
            }
        });

        menu_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();

                menu_search.setColorFilter(getResources().getColor(R.color.colorWhite));
                menu_calendar.setColorFilter(Color.parseColor(Settings.colors.YearColor));
                menu_map.setColorFilter(getResources().getColor(R.color.colorWhite));

                painel_search.setVisibility(View.GONE);
                painel_calendar.setVisibility(View.VISIBLE);
                painel_map.setVisibility(View.GONE);

                Calendar _c = Calendar.getInstance();
                Calendar _c1 = new GregorianCalendar();

                int year = _c.get(Calendar.YEAR);
                int month = _c.get(Calendar.MONTH);
                int day = _c.getActualMaximum(Calendar.DAY_OF_MONTH);

                _c1.set(year, month, day);
                searchByCalendar(_c1);
            }
        });

        menu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_calendar.setColorFilter(getResources().getColor(R.color.colorWhite));
                menu_search.setColorFilter(getResources().getColor(R.color.colorWhite));
                menu_map.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                painel_search.setVisibility(View.GONE);
                painel_calendar.setVisibility(View.GONE);
                painel_map.setVisibility(View.VISIBLE);
            }
        });


        final ViewPager viewMonth = findViewById(R.id.sliderMonth);

        viewMonth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        List<View> views = new ArrayList<>();

        calendar = Calendar.getInstance();

        final ImageView monthLeft = findViewById(R.id.month_left_arrow);
        monthLeft.setColorFilter(Color.parseColor(Settings.colors.YearColor));
        monthLeft.setVisibility(View.GONE);

        final int[] m = {0};

        String[] months = {"janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};

        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);

        Settings.selected_month = currentMonth;

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        final int currentDay = calendar.get(Calendar.DATE);

        Data.current_day = currentDay;
        Data.current_month = calendar.get(Calendar.MONTH);
        Data.current_year = calendar.get(Calendar.YEAR);

        monthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading();

                viewMonth.setCurrentItem(--m[0]);
                if (m[0] == 0) {
                    monthLeft.setVisibility(View.INVISIBLE);
                    int l = m[0];
                    viewMonth.setCurrentItem(l);
                }

                calendar.add(Calendar.MONTH, -1);

                if (m[0] == 0) {
                    setDaysByMonth(m[0], currentDay, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else {
                    setDaysByMonth(m[0], 0, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                }

                Calendar _c1 = new GregorianCalendar();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                _c1.set(year, month, day);
                searchByCalendar(_c1);
            }
        });


        ImageView monthRigth = findViewById(R.id.month_right_arrow);
        monthRigth.setColorFilter(Color.parseColor(Settings.colors.YearColor));

        monthRigth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();

                viewMonth.setCurrentItem(++m[0]);
                if (m[0] > 0) {
                    monthLeft.setVisibility(View.VISIBLE);
                    int r = m[0];
                    viewMonth.setCurrentItem(r);
                }

                calendar.add(Calendar.MONTH, 1);

                if (m[0] == 0) {
                    setDaysByMonth(m[0], currentDay, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else {
                    setDaysByMonth(m[0], 0, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                }

                Calendar _c1 = new GregorianCalendar();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                _c1.set(year, month, day);
                searchByCalendar(_c1);
            }
        });

        //MONTHS OF CURRENT YEAR
        for (int mt = currentMonth; mt < months.length; mt++) {
            View view = View.inflate(this, R.layout.search_calendar_month, null);
            TextView month = view.findViewById(R.id.search_month_title);
            month.setText(months[mt] + " " + currentYear);
            views.add(view);
        }


        int nextYear = calendar.get(Calendar.YEAR) + 1;

        //MONTHS OF NEXT YEAR
        for (int mt = 0; mt < months.length; mt++) {
            View view = View.inflate(this, R.layout.search_calendar_month, null);
            TextView month = view.findViewById(R.id.search_month_title);
            month.setText(months[mt] + " " + nextYear);
            views.add(view);
        }

        setDaysByMonth(0, currentDay, maxDay);

        viewMonth.setAdapter(new SliderPageAdapter(views, this));

        if (type.startsWith("calendar")) {
            loading();

            menu_calendar.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            menu_search.setColorFilter(getResources().getColor(R.color.colorWhite));
            menu_map.setColorFilter(getResources().getColor(R.color.colorWhite));

            painel_calendar.setVisibility(View.VISIBLE);
            painel_search.setVisibility(View.GONE);
            painel_map.setVisibility(View.GONE);

            Calendar _c = Calendar.getInstance();
            Calendar _c1 = new GregorianCalendar();

            int year = _c.get(Calendar.YEAR);
            int month = _c.get(Calendar.MONTH);
            int day = _c.getActualMaximum(Calendar.DAY_OF_MONTH);

            _c1.set(year, month, day);
            searchByCalendar(_c1);
        } else if (type.startsWith("maps")) {
            menu_calendar.setColorFilter(getResources().getColor(R.color.colorWhite));
            menu_search.setColorFilter(getResources().getColor(R.color.colorWhite));
            menu_map.setColorFilter(Color.parseColor(Settings.colors.YearColor));

            painel_map.setVisibility(View.VISIBLE);
            painel_calendar.setVisibility(View.GONE);
            painel_search.setVisibility(View.GONE);

            searchByMap();
        } else {

            final EditText search_text = findViewById(R.id.search_by_text);
            search_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (search_text.getText().toString().startsWith("O quê?...")) {
                        search_text.setText("");
                    }
                }
            });


            final LinearLayout searchWrapper = findViewById(R.id.search_button_wrapper);
            searchWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchByText();
                }
            });
            final Button searchButton = findViewById(R.id.search_button);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchByText();
                }
            });
        }
    }

    private void searchByText() {
        loading();

        EditText search_text = findViewById(R.id.search_by_text);
        String _t = search_text.getText().toString();

        if (!_t.isEmpty()) {
            searchByText(_t);
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();

        return parentIntent;
    }

    private void setDaysByMonth(final int currentMonth, int currentDay, int maxDay) {
        try {
            final LinearLayout viewDays = findViewById(R.id.sliderDays);

            viewDays.removeAllViews();

            View all_v = View.inflate(this, R.layout.search_calendar_day, null);
            final TextView all_tx = all_v.findViewById(R.id.search_month_day);
            all_tx.setText("Todos");
            all_tx.setTextColor(Color.parseColor(Settings.colors.YearColor));
            viewDays.addView(all_v);

            for (int d = 1; d <= maxDay; d++) {
                View v = View.inflate(this, R.layout.search_calendar_day, null);
                final TextView day = v.findViewById(R.id.search_month_day);

                String _d = String.valueOf(d);

                day.setText(_d);

                if (d < currentDay) {
                    day.setTextColor(Color.parseColor(Settings.colors.Gray3));
                }

                if (d == currentDay && currentDay != 0) {
                    Drawable bg = getResources().getDrawable(R.drawable.calendar_search_currentday);
                    bg.setTint(Color.parseColor(Settings.colors.YearColor));
                    day.setBackground(bg);
                }

                if (d >= currentDay) {
                    viewDays.addView(v);
                }

            /*if(Settings.selected_day != null && _d == Settings.selected_day && Settings.selected_month == currentMonth){
                day.setTextColor(Color.parseColor(Settings.color));
                Settings.tv_selected_day = day;

                Drawable bg = getDrawable(R.drawable.calendar_search_eventday);
                bg.setTint(Color.parseColor(Settings.color));
                day.setBackground(bg);
            }*/

                day.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Settings.tv_selected_day != null) {
                            Settings.tv_selected_day.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        String _d = day.getText().toString();

                        if (Settings.selected_day == _d && Settings.selected_month == Data.current_month) {
                            Settings.selected_day = "";
                        } else {
                            Settings.selected_day = _d;
                            Settings.tv_selected_day = day;
                            day.setTextColor(Color.parseColor(Settings.colors.YearColor));
                        }

                        Settings.selected_month = currentMonth;

                        searchByDay(_d);
                    }
                });
            }
        } catch (Exception e) {
            //TODO: Noservice Activity
        }
    }

    private void searchByCalendar(Calendar date) {
        try {
            String monthYear = String.format("%d_%d", date.get(Calendar.MONTH) + 1, date.get(Calendar.YEAR));

            if (Data.CalendarEvents != null && Data.CalendarEvents.containsKey(monthYear)) {
                new SearchManager(this, (LinearLayout) findViewById(R.id.search_calendar_result), (RelativeLayout) findViewById(R.id.loadingPanel), (LinearLayout) findViewById(R.id.sliderDays)).drawMonthsEvents(monthYear);//.drawMonthsEvents(monthYear);
            } else {
                new SearchManager(this, (LinearLayout) findViewById(R.id.search_calendar_result), (RelativeLayout) findViewById(R.id.loadingPanel), (LinearLayout) findViewById(R.id.sliderDays)).execute(WebApiCalls.getSearch(String.valueOf(date.getTimeInMillis() / 1000)), monthYear);
            }
        } catch (Exception ex) {
            //TODO: Noservice Activity
        }
    }

    public void searchByDay(String day) {
        try {
            new SearchManager(this, (LinearLayout) findViewById(R.id.search_calendar_result), (RelativeLayout) findViewById(R.id.loadingPanel), (LinearLayout) findViewById(R.id.sliderDays)).drawDayEvents(day);
        } catch (Exception ex) {
            //TODO: Noservice Activity
        }
    }

    public void searchByText(String data) {
        try {
            View view = this.findViewById(android.R.id.content);

            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            new SearchManager(this, (LinearLayout) findViewById(R.id.search_result_text), (RelativeLayout) findViewById(R.id.loadingPanel), (LinearLayout) findViewById(R.id.sliderDays)).execute(WebApiCalls.getSearchByText(data));
        } catch (Exception ex) {
            //TODO: Noservice Activity
        }
    }

    public void searchByMap() {
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (intent.hasExtra(Variables.Lat) && intent.hasExtra(Variables.Lng)) {
                float lat = bundle.getFloat(Variables.Lat);
                float lng = bundle.getFloat(Variables.Lng);

                receivedOrigin = new LatLng(Float.valueOf(lat), Float.valueOf(lng));
            } else {
                origin = new LatLng(38.7025943, -9.3966299);
            }

            /*if (origin != null) {
                new PinpointManager().execute(WebApiCalls.getSearchByMap(String.valueOf(origin.latitude), String.valueOf(origin.longitude)));
            } else if (receivedOrigin != null) {
                new PinpointManager().execute(WebApiCalls.getSearchByMap(String.valueOf(receivedOrigin.latitude), String.valueOf(receivedOrigin.longitude)));
            }*/

        } catch (Exception ex) {
            String as = "";
            //TODO: Noservice Activity
        }
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {

            if(receivedOrigin != null) {
                origin = new LatLng(location.getLatitude(), location.getLongitude());
                //DrawRoute(origin, destination);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, mapScale));
            }
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


    /*@Override
    public void onInfoWindowClick(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.fragment_slide, null);

        TextView title = view.findViewById(R.id.frame_title);

        PointEntity point = (PointEntity) marker.getTag();


        //title.setText(point.Title() + " - " + point.Address());

        //Toast.makeText(this, point.Title() + " - " + point.Address(), Toast.LENGTH_LONG).show();
    }*/

  /*  @Override
    public boolean onMarkerClick(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.fragment_slide, null);

        TextView title = view.findViewById(R.id.frame_title);

        //PointEntity point = (PointEntity) marker.getTag();

        //title.setText(point.Title() + " - " + point.Address());

        title.setText("asdfasdf");





        //Toast.makeText(this, point.Title() + " - " + point.Address(), Toast.LENGTH_LONG).show();

        return true;
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        try {
            if (mGoogleMap != null) {
                mGoogleMap.setInfoWindowAdapter(this);
                mGoogleMap.setOnInfoWindowClickListener(this);
                //mGoogleMap.setOnMarkerClickListener(this);
                //mGoogleMap.setMyLocationEnabled(true);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);

                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000,
                            10, locationListenerGPS);
                }

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null && receivedOrigin == null) {
                            origin = new LatLng(location.getLatitude(), location.getLongitude());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, mapScale));
                        }
                    }
                });

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (lastMarket != null) {
                            lastMarket.setIcon(bitmapDescriptorFromVector(getBaseContext(), true));
                            lastMarket = null;
                        }
                    }
                });

                mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            //Toast.makeText(SearchActivity.this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();
                            origin = mGoogleMap.getCameraPosition().target;
                            getMapMarkers();
                        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                            //Toast.makeText(SearchActivity.this, "The user tapped something on the map.", Toast.LENGTH_SHORT).show();
                        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
                            //Toast.makeText(SearchActivity.this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                getMapMarkers();
            }
        }catch (Exception ex){
            String e = ex.getMessage();
        }

    }

    private void loading() {
        RelativeLayout loading = findViewById(R.id.loadingPanel);

        loading.setVisibility(View.VISIBLE);
        loading.bringToFront();
    }

    private void getMapMarkers() {

        if (mGoogleMap != null) {

            WebApiClient.get(WebApiCalls.getSearchByMap(String.valueOf(origin.latitude), String.valueOf(origin.longitude)),  new TextHttpResponseHandler(){
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    String message = WebApiMessages.DecryptMessage(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    String message = WebApiMessages.DecryptMessage(responseString);
                    if(message != null) {

                        try {
                            JSONObject response = new JSONObject(message);

                            JSONObject responseData = response.getJSONObject("ResponseData");

                            JSONArray jsonArray = responseData.getJSONArray("Data");

                            Type MapMarkerTypeList = new TypeToken<ArrayList<MapMarker>>() {}.getType();
                            List<MapMarker> mapMarkers = new Gson().fromJson(jsonArray.toString(), MapMarkerTypeList);

                            if (Data.LocalMarkers == null) {
                                Data.LocalMarkers = new ArrayList<>();
                            }

                            boolean refreshMap = false;

                            for (MapMarker m : mapMarkers) {
                                boolean add = true;
                                for(MapMarker mm : Data.LocalMarkers){
                                    if(m.ID == mm.ID){
                                        add = false;
                                        break;
                                    }
                                }
                                if(add) {
                                    refreshMap = true;
                                    Data.LocalMarkers.add(m);
                                }
                            }

                            if(refreshMap) {
                                MapMarkers();
                            }

                        }catch (Exception ex){
                            String e = ex.getMessage();
                        }

                    }
                }
            });

            /*if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(this, mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for(PinPointEntity p : Data.PinpointEvents.values()){
                ClusterMarker newClusterMarker = new ClusterMarker(
                        new LatLng(p.Latitude(), p.Logitude()),
                        p.Title(),
                        "snippet",
                        p.Nid());

                mClusterManager.addItem(newClusterMarker);
                mClusterMarkers.add(newClusterMarker);
            }

            mClusterManager.cluster();*/
        }
    }

    private void MapMarkers(){
        for(MapMarker m : Data.LocalMarkers){

            Coordinates coordinates = m.Coordinates.get(0);

            if(coordinates != null) {
                LatLng point = new LatLng(coordinates.Lat, coordinates.Lng);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(point).title(m.Title));
                marker.setIcon(bitmapDescriptorFromVector(this, true));
                marker.setTag(m);
            }
        }

        if(first) {
            setCameraView();
            first = false;
        }
    }

    private boolean first = true;

    private void setCameraView() {
        LatLng center;

        if (receivedOrigin != null) {
            center = receivedOrigin;
        } else if(origin != null) {
            center = origin;
        }else{
            center = new LatLng(38.7025943, -9.3966299);
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, mapScale));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        try {
            /*View view = getLayoutInflater().inflate(R.layout.fragment_map_marker, null);

            final PinPointEntity p = (PinPointEntity) marker.getTag();

            TextView title = view.findViewById(R.id.frame_title);

            title.setText(p.Title());

            ImageView img = view.findViewById(R.id.frame_image);
            img.setImageBitmap(p.Image());

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getBaseContext();
                    Intent event = new Intent(context, EventActivity.class);
                    int nid = p.Nid();
                    event.putExtra("nid", nid);
                    context.startActivity(event);
                }
            });

            TextView navegationTitle = view.findViewById(R.id.frame_navegation);
            navegationTitle.setTextColor(Color.parseColor(Settings.color));



            return view;*/
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {

        //marker.setIcon(bitmapDescriptorFromVector(this, false));

        /*if(lastMarket !=  null){
            lastMarket.setIcon(bitmapDescriptorFromVector(this, true));
        }*/

        View view = getLayoutInflater().inflate(R.layout.fragment_map_marker, null);

        /*final PinPointEntity p = (PinPointEntity) marker.getTag();


        TextView title = view.findViewById(R.id.frame_title);
        title.setText(p.Title());

        ImageView img = view.findViewById(R.id.frame_image);
        img.setImageBitmap(p.Image());*/

        //ImageView icon = view.findViewById(R.id.arrow_icon);
        //icon.setColorFilter(Color.parseColor(Settings.color));

        /*img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getBaseContext();
                Intent event = new Intent(context, EventActivity.class);
                int nid = p.Nid();
                event.putExtra("nid", nid);
                context.startActivity(event);
            }
        });*/

        TextView navegationTitle = view.findViewById(R.id.frame_navegation);
        navegationTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));

        lastMarket = marker;

        return view;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, boolean setColor) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_locationmark);
        background.setBounds(0, 0, 60, 80);
        if (setColor) {
            background.setTint(Color.parseColor(Settings.colors.YearColor));
        }
        Bitmap bitmap = Bitmap.createBitmap(80, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent event = new Intent(this, DetailActivity.class);
        /*PinPointEntity p = (PinPointEntity) marker.getTag();
        int nid = p.Nid();
        event.putExtra("nid", nid);*/
        this.startActivity(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {

            if (permissions.length == 1 && permissions[0].startsWith(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mGoogleMap.setMyLocationEnabled(true);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);
            }
        }
    }
}


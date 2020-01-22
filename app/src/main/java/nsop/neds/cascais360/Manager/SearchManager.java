package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SearchEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nsop.neds.cascais360.Entities.FrameEntity;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.Search;
import nsop.neds.cascais360.Entities.Json.SearchByDate;
import nsop.neds.cascais360.Manager.Layout.LayoutManager;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Data;
import nsop.neds.cascais360.Settings.Settings;


public class SearchManager extends AsyncTask<String, Void, Search> {

    Context context;
    LinearLayout mainContent;
    RelativeLayout loading;
    LinearLayout daysPainel;

    Boolean render = true;

    public SearchManager(){
        render = false;
    }

    public SearchManager(Context context, LinearLayout mainContent, RelativeLayout loading, LinearLayout daysPainel){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
        this.daysPainel = daysPainel;
    }

    @Override
    protected Search doInBackground(String... strings) {

        if(strings.length == 1){
            return getSearchResult(strings[0]);
        }

        if(Data.CalendarEvents == null){
            Data.CalendarEvents = new HashMap();
        }

        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");
                JSONArray jsonArray = responseData.getJSONArray("Data");

                Type listType = new TypeToken<ArrayList<SearchByDate>>(){}.getType();
                List<SearchByDate> list = new Gson().fromJson(jsonArray.toString(), listType);

                Search s = new Search();
                s.Events = list.get(0).Data;

                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Search search) {
        super.onPostExecute(search);

        if(render) {

            mainContent.removeAllViews();

            if(search != null) {
                if(search.Routes != null || search.Places != null || search.Events != null) {
                    mainContent.addView(LayoutManager.setSearch(search, context));
                }else{
                    View searchInfoBlock = View.inflate(context, R.layout.block_search_info, null);
                    TextView searchInfo = searchInfoBlock.findViewById(R.id.search_result_text_info);
                    searchInfo.setText(Settings.labels.NoResultsFound);

                    mainContent.addView(searchInfoBlock);
                }
            }else{
                View searchInfoBlock = View.inflate(context, R.layout.block_search_info, null);
                TextView searchInfo = searchInfoBlock.findViewById(R.id.search_result_text_info);
                searchInfo.setText(Settings.labels.NoResultsFound);

                mainContent.addView(searchInfoBlock);
            }

            mainContent.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    private void setSpotlightBlock(final FrameEntity e) {

        /*View frame = View.inflate(context, R.layout.slide, null);

        TextView frameTitle = frame.findViewById(R.id.frame_title);
        frameTitle.setText(e.Title());

        ImageView img = frame.findViewById(R.id.frame_image);
        img.setImageBitmap(e.Image());

        if (e.Nid() > 0) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent event = new Intent(context, EventActivity.class);
                    int nid = e.Nid();
                    event.putExtra("nid", nid);
                    context.startActivity(event);
                }
            });
        }

        TextView frameDate = frame.findViewById(R.id.frame_date);
        frameDate.setTextColor(Color.parseColor(Settings.color));
        if (e.DisplayDate() != "null") {
            frameDate.setText(e.DisplayDate());
        } else {
            frameDate.setText("");
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 50, 0, 100);

        mainContent.addView(frame, layoutParams);*/
    }

    public void drawMonthsEvents(String monthYear) {
        HashMap<String, List<FrameEntity>> selectMonth = Data.CalendarEvents.get(monthYear);

        String[] _monthYear = monthYear.split("\\_+");


        if(_monthYear[0].length() == 1){
            _monthYear[0] = "0" + _monthYear[0];
        }

        Data.selected_month = _monthYear[0];
        Data.selected_year = _monthYear[1];

        mainContent.removeAllViews();

        Iterator<Map.Entry<String, List<FrameEntity>>> iterator = selectMonth.entrySet().iterator();



        while(iterator.hasNext()){
            Map.Entry<String, List<FrameEntity>> entry = iterator.next();

            for (FrameEntity e : entry.getValue()) {
                setSpotlightBlock(e);
            }
        }

        //Drawable bg = context.getDrawable(R.drawable.calendar_search_eventday);
        //bg.setTint(Color.parseColor(Settings.color));

        for(int d = 0; d < daysPainel.getChildCount(); d++) {
            LinearLayout day = (LinearLayout) daysPainel.getChildAt(d);

            String _d = ((TextView)day.getChildAt(0)).getText().toString();

            if(_d.length() == 1){
                _d = "0" + _d;
            }

            /*if(selectMonth.containsKey(_monthYear[1] + "/" + _monthYear[0] + "/" + _d)){
                ((TextView)day.getChildAt(0)).setBackground(bg);
            }*/
        }

        loading.setVisibility(View.GONE);
    }

    public void drawDayEvents(String day) {

        try {
            mainContent.removeAllViews();

            if(day.length() == 1){
                day = "0" + day;
            }

            if (Data.CalendarEvents.containsKey(String.format("%s_%s", Data.selected_month, Data.selected_year))) {
                List<FrameEntity> events = Data.CalendarEvents.get(String.format("%s_%s", Data.selected_month, Data.selected_year)).get(String.format("%s/%s/%s", Data.selected_year, Data.selected_month, day));

                for (FrameEntity e : events) {
                    setSpotlightBlock(e);
                }
            }

            loading.setVisibility(View.GONE);
        }catch (Exception e){
            Log.e("DrawDayEvents", e.getMessage());
        }
    }

    protected Search getSearchResult(String... strings) {
        try {

            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");
                JSONObject jsonArray = responseData.getJSONObject("Data");

                Search search = new Gson().fromJson(jsonArray.toString(), Search.class);

                return search;
            }
        } catch (JSONException je) {

        }

        return null;
    }
}

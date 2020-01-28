package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
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
import java.util.List;

import nsop.neds.mycascais.Entities.FrameEntity;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.SearchByDate;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;


public class SearchManager extends AsyncTask<String, Void, Search> {

    Context context;
    LinearLayout mainContent;
    RelativeLayout loading;
    LinearLayout daysPainel;

    Boolean render = true;

    public SearchManager(){
        render = false;
    }

    public SearchManager(Context context, LinearLayout mainContent, RelativeLayout loading){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;

    }

    @Override
    protected Search doInBackground(String... strings) {

        //return search by text
        if(strings.length == 1){
            return getSearchResult(strings[0]);
        }

        //init cach
        if(Data.CalendarEvents == null){
            Data.CalendarEvents = new HashMap();
        }

        //search by text
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            if (response != null) {
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
        Search search = Data.CalendarEvents.get(monthYear);

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

    /*public void drawDayEvents(String day) {

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
    }*/

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

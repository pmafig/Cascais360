package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.Json.Dashboard;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.InfoBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.Json.SubTitle;
import nsop.neds.cascais360.Manager.ControlsManager.DownloadImageAsync;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.ControlsManager.SliderTwoPageAdapter;
import nsop.neds.cascais360.Manager.Layout.LayoutManager;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class DashboardManager extends AsyncTask<String, Void, List<LayoutBlock>> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;

    DashboardEntity dboard;

    Boolean render = true;

    public DashboardManager(){
        render = false;

        if(dboard == null) {
            dboard = new DashboardEntity();
        }
    }

    public DashboardManager(Context context, LinearLayout mainContent, RelativeLayout loading){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;

        if(dboard == null) {
            dboard = new DashboardEntity();
        }
    }

    @Override
    protected List<LayoutBlock> doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            SessionManager sm = new SessionManager(context);

            if(response != null) {

                JSONObject responseData = response.getJSONObject("ResponseData");

                String crc = responseData.getString("CRC");

                Dashboard inMemory = sm.getDashboard();

                if(inMemory != null && crc.startsWith(inMemory.CRC))
                    return inMemory.Blocks;

                final JSONArray jsonArray = responseData.getJSONArray("Data");

                String _s = jsonArray.toString();

                Type listType = new TypeToken<ArrayList<LayoutBlock>>(){}.getType();
                List<LayoutBlock> list = new Gson().fromJson(_s, listType);

                Dashboard dashboard = new Dashboard();
                dashboard.CRC = crc;
                dashboard.Blocks = list;

                sm.setDashboard(dashboard);

                sm = null;

                return list;
            }else{
                Dashboard inMemory = sm.getDashboard();

                sm = null;

                if(inMemory != null)
                    return inMemory.Blocks;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LayoutBlock> blockList) {
        super.onPostExecute(blockList);

        if(render) {
            try {
                mainContent.removeAllViews();

                //sort blocklist
                Collections.sort(blockList, new Comparator<LayoutBlock>() {
                    @Override
                    public int compare(LayoutBlock o1, LayoutBlock o2) {
                        return o1.Weight < o2.Weight ? -1
                                : o1.Weight > o2.Weight ? 1
                                : 0;
                    }
                });

                Type InfoBlockTypeList = new TypeToken<ArrayList<InfoBlock>>(){}.getType();

                for (LayoutBlock b : blockList) {

                    switch (b.Type) {
                        case 1:
                            JsonObject jsonObjectType1 = new Gson().toJsonTree(b.Contents).getAsJsonObject();
                            HighLight t1 = new Gson().fromJson(jsonObjectType1.toString(), HighLight.class);
                            mainContent.addView(LayoutManager.setHighLightBlock(t1, context));
                            break;
                        case 2:
                            JsonArray jsonObjectType2 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            List<InfoBlock> listType2 = new Gson().fromJson(jsonObjectType2.toString(), InfoBlockTypeList);
                            mainContent.addView(LayoutManager.setSliderBlock(b.Title, listType2, context));
                            break;
                        case 3:

                            break;
                        case 4:
                            JsonArray jsonObjectType4 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            List<InfoBlock> list = new Gson().fromJson(jsonObjectType4.toString(), InfoBlockTypeList);
                            mainContent.addView(LayoutManager.setSpotlightBlock(b.Title, list, context));
                            break;
                        case 5:
                            JsonArray jsonObjectType5 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            Type NodeTypeList = new TypeToken<ArrayList<Node>>(){}.getType();
                            List<Node> node_list = new Gson().fromJson(jsonObjectType5.toString(), NodeTypeList);
                            mainContent.addView(LayoutManager.setCategorySliderBlock(b.Title, node_list, context));
                            break;
                    }
                }

                loading.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }
}


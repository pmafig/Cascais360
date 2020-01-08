package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.cascais360.Entities.Json.Dashboard;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Manager.Layout.LayoutManager;

public class DetailManager extends AsyncTask<String, Void, List<LayoutBlock>> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    int nid;

    public DetailManager(int nid, Context context, LinearLayout mainContent, RelativeLayout loading){
        this.nid = nid;
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
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

        try {
            mainContent.removeAllViews();

            Collections.sort(blockList, new Comparator<LayoutBlock>() {
                @Override
                public int compare(LayoutBlock o1, LayoutBlock o2) {
                    return o1.Weight < o2.Weight ? -1
                            : o1.Weight > o2.Weight ? 1
                            : 0;
                }
            });

            for (LayoutBlock b : blockList) {

                switch (b.Type) {
                    case 1:
                        JsonObject jsonObjectType1 = new Gson().toJsonTree(b.Contents).getAsJsonObject();
                        HighLight t1 = new Gson().fromJson(jsonObjectType1.toString(), HighLight.class);
                        mainContent.addView(LayoutManager.setHighLightBlock(t1, context));
                        break;
                    case 6:
                        JsonArray jsonObjectType6 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                        Type NodeTypeList = new TypeToken<ArrayList<Node>>(){}.getType();
                        List<Node> node_list = new Gson().fromJson(jsonObjectType6.toString(), NodeTypeList);
                        mainContent.addView(LayoutManager.setCategoryListBlock(b.Title, node_list, context));
                        break;
                }
            }

            loading.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }
}

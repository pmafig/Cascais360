package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nsop.neds.mycascais.Entities.Json.CategoryListDetail;
import nsop.neds.mycascais.Entities.Json.Dashboard;
import nsop.neds.mycascais.Entities.Json.LayoutBlock;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.R;

public class ListDetailManager extends AsyncTask<String, Void, List<LayoutBlock>> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    androidx.appcompat.widget.Toolbar sortList;

    public ListDetailManager(Context context, androidx.appcompat.widget.Toolbar sortList, LinearLayout mainContent, RelativeLayout loading){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
        this.sortList = sortList;
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

            LinearLayout sortingList = (LinearLayout) sortList.findViewById(R.id.list_sorting);

            mainContent.removeAllViews();

            sortingList.removeAllViews();

            for (LayoutBlock b : blockList) {

                switch (b.Type) {
                    case 7:
                        JsonArray jsonObjectType7 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                        CategoryListDetail detail = new Gson().fromJson(jsonObjectType7.get(0).toString(), CategoryListDetail.class);
                        mainContent.addView(LayoutManager.setCategoryListDetailBlock(b.Title, detail, context));
                        LayoutManager.setCategoryListSortBlock(detail, sortingList, context);
                        break;
                }
            }

            loading.setVisibility(View.GONE);
            sortList.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

}

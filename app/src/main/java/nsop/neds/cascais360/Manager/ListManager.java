package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.cascais360.Entities.BlockEntity;
import nsop.neds.cascais360.Entities.CategoryEntity;
import nsop.neds.cascais360.Entities.CategoryNodeEntity;
import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.Json.Dashboard;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.InfoBlock;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.WeatherEntity;

import nsop.neds.cascais360.Manager.Layout.LayoutManager;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class ListManager extends AsyncTask<String, Void, List<LayoutBlock>> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;

    DashboardEntity dboard;

    public ListManager(Context context, LinearLayout mainContent, RelativeLayout loading){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
        dboard = new DashboardEntity();
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

    //region Add Dashboard Type Block to Array
    //Add Type 1main_content
    private void addHighLightBlock(JSONObject block){
         try {
            JSONObject contents = block.getJSONObject("Contents");

            JSONArray images = contents.getJSONArray("Images");

            if(contents.getString("Date") == null || contents.getString("Date") == "null"){
                dboard.getBlockList().add(
                        new BlockEntity(block.getInt("Type"),
                                block.getInt("Weight"),
                                contents.getInt("ID"),
                                contents.getString("Title"),
                                contents.getString("SubTitle"),
                                images.get(0).toString()
                        )
                );
            }else{
                dboard.getBlockList().add(
                        new BlockEntity(block.getInt("Type"),
                                block.getInt("Weight"),
                                contents.getInt("ID"),
                                contents.getString("Title"),
                                contents.getString("SubTitle"),
                                images.get(0).toString(),
                                new SimpleDateFormat("yyyy-MM-dd").parse(contents.getString("Date"))
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void addCategoryBlock(JSONObject block){

        try {

            BlockEntity be = new BlockEntity(block.getInt("Type"),
                    block.getInt("Weight"),
                    block.getString("Title"));

            JSONArray contents = block.getJSONArray("Contents");

            for (int c = 0; c < contents.length(); c++) {
                JSONObject category = (JSONObject) contents.get(c);

                JSONArray nodes = category.getJSONArray("Nodes");

                CategoryEntity categoryEntity = new CategoryEntity((category.getJSONObject("Category")).getString("Description"), (category.getJSONObject("Category")).getInt("ID"));

                for (int n = 0; n < nodes.length(); n++) {
                    JSONObject node = ((JSONObject) nodes.get(n));

                    categoryEntity.Nodes().add(new CategoryNodeEntity(node.getInt("ID"), ((JSONObject)node.getJSONArray("SubTitle").get(0)).getString("Text"), node.getString("Title")));
                }

                be.CategoryList().add(categoryEntity);
            }

            dboard.getBlockList().add(be);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //region Render Block Types to Dashboard
    private void setHighLightBlock(final BlockEntity b){
/*        View block = View.inflate(context, R.layout.highlight, null);

        final View main = View.inflate(context, R.layout.app_bar_main, null);

        ImageView img = block.findViewById(R.id.image_highlight);
        img.setImageBitmap(b.Image());

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent event = new Intent(context, EventActivity.class);

                int nid = b.Nid();

                event.putExtra("nid", nid);

                context.startActivity(event);
        }
        });

        block.findViewById(R.id.day_month).setVisibility(View.GONE);

        TextView title = block.findViewById(R.id.tv_title);
        title.setText(b.Title());

        TextView subTitle = block.findViewById(R.id.tv_subtitle);
        subTitle.setText(b.SubTitle());

        mainContent.addView(block);*/
    }

    private void setCategoryListBlock(final BlockEntity b){

        /*View block = View.inflate(context, R.layout.list_block, null);

        LinearLayout category = block.findViewById(R.id.list_block_views);

        /*View mainTitleBlock = View.inflate(context, R.layout.list_block_maintitle, null);
        TextView mainTitle = mainTitleBlock.findViewById(R.id.list_block_maintitle);
        mainTitle.setText(b.Title());
        mainTitle.setTextColor(Color.parseColor(Settings.color));

        category.addView(mainTitleBlock);

        for (final CategoryEntity c:b.CategoryList()) {

            View blockTitle = View.inflate(context, R.layout.list_block_title, null);
            TextView title = blockTitle.findViewById(R.id.list_block_title);
            title.setText(c.Title());

            ImageView icon = blockTitle.findViewById(R.id.list_block_icon);
            Drawable plusCircle = context.getResources().getDrawable(R.drawable.ic_pluscircle);
            plusCircle.setTint(Color.parseColor(Settings.color));
            icon.setImageDrawable(plusCircle);

            LinearLayout title_wrapper = blockTitle.findViewById(R.id.list_block_title_wrapper);

            title_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent event = new Intent(context, CategoryListActivity.class);
                    int id = c.Id();
                    event.putExtra("id", id);
                    context.startActivity(event);
                }
            });

            category.addView(blockTitle);

            for (final CategoryNodeEntity n : c.Nodes()) {
                View frame = View.inflate(context, R.layout.list_item_smaller, null);

                LinearLayout item = frame.findViewById(R.id.list_item);

                LinearLayout route = frame.findViewById(R.id.event_route_briefing);
                if(n.Difficulty() > 0 && n.Distance() > 0) {
                    TextView distance = frame.findViewById(R.id.event_route_distance);
                    distance.setText(n.Distance());

                    TextView difficulty = frame.findViewById(R.id.event_route_difficulty);
                    difficulty.setText(n.Difficulty());

                    TextView date = frame.findViewById(R.id.list_item_date);
                    date.setVisibility(View.GONE);

                    route.setVisibility(View.VISIBLE);
                }else {
                    route.setVisibility(View.GONE);
                }

                TextView frameTitle = frame.findViewById(R.id.list_item_date);
                frameTitle.setText(n.Title());
                frameTitle.setTextColor(Color.parseColor(Settings.color));

                TextView subTitle = frame.findViewById(R.id.list_item_title);
                subTitle.setText(n.SubTitle());

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent event = new Intent(context, EventActivity.class);
                        int nid = n.Nid();
                        event.putExtra("nid", nid);
                        context.startActivity(event);
                    }
                });

                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, 0, 0, 100);

                category.addView(frame, layoutParams);

                category.addView(frame);


            }

            View bottomline = View.inflate(context, R.layout.list_item_bottomline, null);
            category.addView(bottomline);
        }

        mainContent.addView(block);*/
    }
}

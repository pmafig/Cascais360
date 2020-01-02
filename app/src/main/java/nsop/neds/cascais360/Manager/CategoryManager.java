package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nsop.neds.cascais360.Entities.BlockEntity;
import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.FrameEntity;
import nsop.neds.cascais360.Entities.WeatherEntity;
import nsop.neds.cascais360.R;

public class CategoryManager extends AsyncTask<String, Void, DashboardEntity> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    //NavigationView navigation;

    DashboardEntity dboard;
    WeatherEntity weather;

    public CategoryManager(Context context, LinearLayout mainContent, RelativeLayout loading){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
        dboard = new DashboardEntity();
    }

    @Override
    protected DashboardEntity doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                JSONObject jsonArray = responseData.getJSONObject("Data");

                addSlideshowBlock(jsonArray);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.dboard;
    }

    @Override
    protected void onPostExecute(DashboardEntity blockList) {
        super.onPostExecute(blockList);

        try {
            mainContent.removeAllViews();

            for (BlockEntity b : blockList.getBlockList()) {
                setSpotlightBlock(b);
            }

            loading.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

    //region Add Dashboard Type Block to Array
    //Add Type 1main_content
    private void addSlideshowBlock(JSONObject block){
        JSONArray contents = null;
        try {

            BlockEntity be = new BlockEntity();

            contents = block.getJSONArray("Events");

            if(contents.length() > 0) {
                be.setTitle(((JSONObject)((JSONObject) contents.get(0)).getJSONArray("Category").get(0)).getString("Description"));
            }

            for(int c = 0; c < contents.length(); c++){

                int n = ((JSONObject) contents.get(c)).getInt("ID");
                String t = ((JSONObject) contents.get(c)).getString("Title");
                String i = ((JSONObject) contents.get(c)).getJSONArray("Images").get(0).toString();

                be.FrameList().add(new FrameEntity(n, t, i, ""));
            }

            dboard.getBlockList().add(be);

        } catch (JSONException e) {
            e.printStackTrace();
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

    private void setSpotlightBlock(final BlockEntity b){

       /* View block = View.inflate(context, R.layout.spotlight_block, null);

        TextView titleBlock = block.findViewById(R.id.spotlight_block_title);
        titleBlock.setVisibility(View.GONE);

        TextView titleCategoryBlock = block.findViewById(R.id.spotlight_block_category_title);
        titleCategoryBlock.setVisibility(View.VISIBLE);
        titleCategoryBlock.setText(b.Title());

        LinearLayout wrapper = block.findViewById(R.id.spotlight_block_views);

        for (final FrameEntity f:b.FrameList()) {
            View frame = View.inflate(context, R.layout.slide, null);

            TextView frameTitle = frame.findViewById(R.id.frame_title);
            frameTitle.setText(f.Title());
            frameTitle.setPadding(0, 10,0,0);

            ImageView img = frame.findViewById(R.id.frame_image);
            img.setImageBitmap(f.Image());

            if(f.Nid() > 0) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent event = new Intent(context, EventActivity.class);
                        int nid = f.Nid();
                        event.putExtra("nid", nid);
                        context.startActivity(event);
                    }
                });
            }

            TextView frameDate = frame.findViewById(R.id.frame_date);
            frameDate.setVisibility(View.GONE);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0, 0, 50);

            wrapper.addView(frame, layoutParams);
        }

        mainContent.addView(block);*/
    }
}

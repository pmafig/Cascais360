package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
import java.util.Set;

import nsop.neds.cascais360.Entities.BlockEntity;
import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.Json.Category;
import nsop.neds.cascais360.Entities.Json.Dashboard;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.InfoBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Manager.ControlsManager.DownloadImageAsync;
import nsop.neds.cascais360.Entities.WeatherEntity;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.ControlsManager.SliderTwoPageAdapter;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class DashboardManager extends AsyncTask<String, Void, List<LayoutBlock>> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;

    DashboardEntity dboard;
    WeatherEntity weather;

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
                            setHighLightBlock(t1);
                            break;
                        case 2:
                            JsonArray jsonObjectType2 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            List<InfoBlock> listType2 = new Gson().fromJson(jsonObjectType2.toString(), InfoBlockTypeList);
                            setSliderBlock(b.Title, listType2);
                            break;
                        case 3:

                            break;
                        case 4:
                            JsonArray jsonObjectType4 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            List<InfoBlock> list = new Gson().fromJson(jsonObjectType4.toString(), InfoBlockTypeList);
                            setSpotlightBlock(b.Title, list);
                            break;
                        case 5:
                            JsonArray jsonObjectType5 = new Gson().toJsonTree(b.Contents).getAsJsonArray();
                            Type NodeTypeList = new TypeToken<ArrayList<Node>>(){}.getType();
                            List<Node> node_list = new Gson().fromJson(jsonObjectType5.toString(), NodeTypeList);

                            setCategorySliderBlock(b.Title, node_list);
                            break;
                    }
                }

                loading.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    //region Render Block Types to Dashboard
    private void setHighLightBlock(final HighLight b) throws IOException {
        View block = View.inflate(context, R.layout.block_highlight, null);

        LinearLayout frameLayout = block.findViewById(R.id.dashboard_highlight);

        final ImageView img = block.findViewById(R.id.image_highlight);

        DownloadImageAsync obj = new DownloadImageAsync(){

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);
                img.setImageBitmap(bmp);
            }
        };
        obj.execute(b.Images.get(0));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                vibe.vibrate(100);

                /*Intent event = new Intent(context, EventActivity.class);

                int nid = b.Nid();

                event.putExtra("nid", nid);

                context.startActivity(event);*/
        }
        });

        TextView title = block.findViewById(R.id.tv_title);
        title.setText(b.Title);

        TextView subTitle = block.findViewById(R.id.tv_subtitle);
        subTitle.setText(b.SubTitle);

        if(b.Date == null) {
            LinearLayout day_month = block.findViewById(R.id.day_month);
            day_month.setVisibility(View.GONE);
        }

        mainContent.addView(block);
    }

    private void setSliderBlock(String title, final List<InfoBlock> slider_list){

        View block = View.inflate(context, R.layout.block_frame_slider, null);
        final ViewPager viewPager = block.findViewById(R.id.sliderPager);

        TextView layout_title = block.findViewById(R.id.tv_slideTitle);
        layout_title.setText(title);

        List<View> views = new ArrayList<>();

        for (final InfoBlock f:slider_list) {
            View frame = View.inflate(context, R.layout.block_frame, null);

            TextView frameTitle = frame.findViewById(R.id.frame_title);
            frameTitle.setText(f.Title);

            final ImageView img = frame.findViewById(R.id.frame_image);

            DownloadImageAsync obj = new DownloadImageAsync(){

                @Override
                protected void onPostExecute(Bitmap bmp) {
                    super.onPostExecute(bmp);
                    img.setImageBitmap(bmp);
                }
            };
            obj.execute(f.Images.get(0));

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                    vibe.vibrate(100);

                    /*Intent event = new Intent(context, EventActivity.class);
                    int nid = f.Nid();
                    event.putExtra("nid", nid);
                    context.startActivity(event);*/
                }
            });

            TextView frameDate = frame.findViewById(R.id.frame_date);
            frameDate.setTextColor(Color.parseColor(Settings.colors.YearColor));

            views.add(frame);
        }

        final LinearLayout sliderdots = block.findViewById(R.id.sliderdots);
        final ImageView[] dots = new ImageView[slider_list.size()];

        final ImageView leftArrow = block.findViewById(R.id.sliderdots_left_arrow);
        leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if(i > 0 || i < dots.length){
                    viewPager.setCurrentItem(i - 1);
                }
            }
        });

        final ImageView rightArrow = block.findViewById(R.id.sliderdots_right_arrow);
        rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if(i == 0 || i < dots.length - 1){
                    viewPager.setCurrentItem(i + 1);
                }
            }
        });

        for(int d = 0; d < dots.length; d++){
            dots[d] = new ImageView(context);
            dots[d].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
            dots[d].setColorFilter(Color.parseColor(Settings.colors.Gray2));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int sp = Math.round(Settings.dotsMargin * context.getResources().getDisplayMetrics().scaledDensity);

            params.setMargins(sp, 0, sp, 0);

            sliderdots.addView(dots[d], params);
        }

        dots[0].setColorFilter(Color.parseColor(Settings.colors.YearColor));

        viewPager.setAdapter(new SliderPageAdapter(views, context));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }else if (i == dots.length-1){
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                }
                else {
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }

                for(int d = 0; d < dots.length; d++){
                    dots[d].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
                    dots[d].setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }

                dots[i].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
                dots[i].setColorFilter(Color.parseColor(Settings.colors.YearColor));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mainContent.addView(block);
    }

    private void setSpotlightBlock(String title, final List<InfoBlock> slider_list){

        View frame_list = View.inflate(context, R.layout.block_frame_list, null);

        TextView layout_title = frame_list.findViewById(R.id.spotlight_block_title);
        layout_title.setText(title);

        LinearLayout views_wrapper = frame_list.findViewById(R.id.spotlight_block_views);

        for (final InfoBlock f: slider_list) {
            View frame = View.inflate(context, R.layout.block_frame, null);

            TextView frameTitle = frame.findViewById(R.id.frame_title);
            frameTitle.setText(f.Title);

            final ImageView img = frame.findViewById(R.id.frame_image);
            DownloadImageAsync obj = new DownloadImageAsync(){

                @Override
                protected void onPostExecute(Bitmap bmp) {
                    super.onPostExecute(bmp);
                    img.setImageBitmap(bmp);
                }
            };
            obj.execute(f.Images.get(0));

            if(f.ID > 0) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                        vibe.vibrate(100);

                        /*Intent event = new Intent(context, EventActivity.class);
                        int nid = f.Nid();
                        event.putExtra("nid", nid);
                        context.startActivity(event);*/
                    }
                });
            }

            TextView frameDate = frame.findViewById(R.id.frame_date);
            frameDate.setTextColor(Color.parseColor(Settings.colors.YearColor));


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0, 0, 100);

            views_wrapper.addView(frame, layoutParams);
        }

        mainContent.addView(frame_list);
    }

    private void setCategorySliderBlock(String title, final List<Node> node_list){

        View category_block = View.inflate(context, R.layout.block_category, null);

        TextView category_block_title = category_block.findViewById(R.id.category_block_title);

        //final ViewFlipper flipper = category_block.findViewById(R.id.category_flipper);
        final ViewPager viewPager = category_block.findViewById(R.id.sliderPager);

        List<View> views = new ArrayList<>();

        category_block_title.setText(title);

        for(int c = 0; c < node_list.size(); c++){

            Node n = node_list.get(c);

            View category = View.inflate(context, R.layout.block_category_list, null);

            TextView category_title = category.findViewById(R.id.category_list_title);
            LinearLayout category_list = category.findViewById(R.id.category_list);
            ImageView icon = category.findViewById(R.id.category_icon);

            category_title.setText(n.Category.Description);
            category_title.setTextColor(Color.parseColor(Settings.colors.YearColor));
            icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

            if(c < node_list.size()-1){
                category.setBackground(context.getDrawable(R.drawable.border_right));
            }

            for(int i = 0; i < n.Nodes.size(); i++){
                View category_item = View.inflate(context, R.layout.block_category_list_item, null);

                InfoBlock info = n.Nodes.get(i);

                TextView t = category_item.findViewById(R.id.list_item_title);
                TextView st = category_item.findViewById(R.id.list_item_date);

                st.setTextColor(Color.parseColor(Settings.colors.YearColor));
                st.setText(info.SubTitle.get(0).Text);

                t.setText(info.Title);

                category_list.addView(category_item);
            }

            views.add(category);
        }


        SliderTwoPageAdapter pageAdapter = new SliderTwoPageAdapter(views);
        viewPager.setAdapter(pageAdapter);

        final int total_dots =  node_list.size()/2 + (node_list.size() % 2 > 0 ? 1 : 0) + 1;

        final LinearLayout sliderdots = category_block.findViewById(R.id.sliderdots);
        final ImageView[] dots = new ImageView[total_dots];

        final ImageView leftArrow = category_block.findViewById(R.id.sliderdots_left_arrow);
        leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if(i > 0 || i < dots.length){
                    viewPager.setCurrentItem(i - 1);
                }
            }
        });

        final ImageView rightArrow = category_block.findViewById(R.id.sliderdots_right_arrow);
        rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if(i == 0 || i < dots.length - 1){
                    viewPager.setCurrentItem(i + 1);
                }
            }
        });

        for(int d = 0; d < dots.length ; d++){
            dots[d] = new ImageView(context);
            dots[d].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
            dots[d].setColorFilter(Color.parseColor(Settings.colors.Gray2));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int sp = Math.round(Settings.dotsMargin * context.getResources().getDisplayMetrics().scaledDensity);

            params.setMargins(sp, 0, sp, 0);

            sliderdots.addView(dots[d], params);
        }

        dots[0].setColorFilter(Color.parseColor(Settings.colors.YearColor));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {

                i = i > 0 ? i / 2 + 1 : 0;

                if(i == 0){
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }else if (i == dots.length-1){
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                }
                else {
                    leftArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                    rightArrow.setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }

                for(int d = 0; d < dots.length; d++){
                    dots[d].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
                    dots[d].setColorFilter(Color.parseColor(Settings.colors.Gray2));
                }

                if(i < dots.length) {
                    dots[i].setImageDrawable(context.getDrawable(R.drawable.ic_dot));
                    dots[i].setColorFilter(Color.parseColor(Settings.colors.YearColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mainContent.addView(category_block);
    }
    //endregion
}


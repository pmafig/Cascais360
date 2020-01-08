package nsop.neds.cascais360.Manager.Layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.InfoBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.Json.SubTitle;
import nsop.neds.cascais360.Manager.ControlsManager.DownloadImageAsync;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.ControlsManager.SliderTwoPageAdapter;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class LayoutManager {

    public static View setHighLightBlock(final HighLight b, final Context context){
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

        return block;
    }

    public static View setSliderBlock(String title, final List<InfoBlock> slider_list, final Context context){

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

        return block;
    }

    public static View setSpotlightBlock(String title, final List<InfoBlock> slider_list, final Context context){

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
            LinearLayout routeInfo = frame.findViewById(R.id.route_briefing);

            if(f.SubTitle.size() > 1){
                routeInfo.setVisibility(View.VISIBLE);
                frameDate.setVisibility(View.GONE);

                for (SubTitle st: f.SubTitle) {

                    if(st.Icon != null) {
                        switch (st.Icon) {
                            case "Hike":
                                LinearLayout w_distance = frame.findViewById(R.id.event_distance_icon_wrapper);
                                w_distance.setVisibility(View.VISIBLE);

                                TextView t_distance = frame.findViewById(R.id.event_route_distance);
                                t_distance.setText(st.Text);
                                break;
                            case "Level":
                                LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                                w_level.setVisibility(View.VISIBLE);

                                TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                                t_level.setText(st.Text);
                                break;
                        }
                    }
                }

            }else {
                frameDate.setVisibility(View.VISIBLE);
                routeInfo.setVisibility(View.GONE);

                frameDate.setTextColor(Color.parseColor(Settings.colors.YearColor));
                frameDate.setText(f.SubTitle.get(0).Text);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int px = Math.round(Settings.spotLightBottomMargin * context.getResources().getDisplayMetrics().scaledDensity);

            layoutParams.setMargins(0, 0, 0, px);

            views_wrapper.addView(frame, layoutParams);
        }

        return frame_list;
    }

    public static View setCategorySliderBlock(String title, final List<Node> node_list, final Context context){

        View category_block = View.inflate(context, R.layout.block_category_slider, null);

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

        return category_block;
    }

    public static View setCategoryListBlock(String title, final List<Node> node_list, final Context context){

        View category_block = View.inflate(context, R.layout.block_category_scroller, null);

        LinearLayout scroller = category_block.findViewById(R.id.list_block_views);

        for(int c = 0; c < node_list.size(); c++){

            Node n = node_list.get(c);

            View category = View.inflate(context, R.layout.block_category_scroller_list, null);

            TextView category_title = category.findViewById(R.id.category_list_title);
            LinearLayout category_list = category.findViewById(R.id.category_list);
            ImageView icon = category.findViewById(R.id.category_icon);

            category_title.setText(n.Category.Description);
            //category_title.setTextColor(Color.parseColor(Settings.colors.YearColor));
            icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

            if(c < node_list.size()-1){
                category.setBackground(context.getDrawable(R.drawable.menu_border_bottom));
            }

            for(int i = 0; i < n.Nodes.size(); i++){
                View category_item = View.inflate(context, R.layout.block_category_scroller_item, null);

                InfoBlock info = n.Nodes.get(i);

                TextView t = category_item.findViewById(R.id.list_item_title);
                TextView st = category_item.findViewById(R.id.list_item_date);

                st.setTextColor(Color.parseColor(Settings.colors.YearColor));
                st.setText(info.SubTitle.get(0).Text);

                t.setText(info.Title);

                category_list.addView(category_item);
            }

            scroller.addView(category);
        }

        return category_block;
    }
}

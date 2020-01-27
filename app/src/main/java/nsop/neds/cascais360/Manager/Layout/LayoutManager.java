package nsop.neds.cascais360.Manager.Layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.cascais360.DetailActivity;
import nsop.neds.cascais360.Entities.Json.CategoryListDetail;
import nsop.neds.cascais360.Entities.Json.Event;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.InfoBlock;
import nsop.neds.cascais360.Entities.Json.InfoEventBlock;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.Json.Place;
import nsop.neds.cascais360.Entities.Json.Point;
import nsop.neds.cascais360.Entities.Json.PointMap;
import nsop.neds.cascais360.Entities.Json.Route;
import nsop.neds.cascais360.Entities.Json.Search;
import nsop.neds.cascais360.Entities.Json.Sorting;
import nsop.neds.cascais360.Entities.Json.SubTitle;
import nsop.neds.cascais360.ListDetailActivity;
import nsop.neds.cascais360.Manager.CommonManager;
import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Manager.ControlsManager.SliderTwoPageAdapter;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.MapsActivity;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.SearchActivity;
import nsop.neds.cascais360.Settings.Data;
import nsop.neds.cascais360.Settings.Settings;

public class LayoutManager {

    public static View setHighLightBlock(final HighLight b, final Context context){
        View block = View.inflate(context, R.layout.block_highlight, null);

        LinearLayout frameLayout = block.findViewById(R.id.dashboard_highlight);

        final ImageView img = block.findViewById(R.id.image_highlight);

        Glide.with(context).load(b.Images.get(0)).into(img);

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                Intent event = new Intent(context, DetailActivity.class);
                event.putExtra(Variables.Id, b.ID);
                context.startActivity(event);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

            TextView subTitle = frame.findViewById(R.id.frame_date);
            subTitle.setText(f.SubTitle.get(0).Text);

            final ImageView img = frame.findViewById(R.id.frame_image);

            Glide.with(context).load(f.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

            CardView clicableFrame = frame.findViewById(R.id.frame);

            clicableFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);

                    Intent event = new Intent(context, DetailActivity.class);
                    event.putExtra(Variables.Id, f.ID);
                    context.startActivity(event);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            TextView frameDate = frame.findViewById(R.id.frame_date);
            frameDate.setTextColor(Color.parseColor(Settings.colors.YearColor));

            views.add(frame);
        }

        viewPager.setAdapter(new SliderPageAdapter(views, context));

        if(views.size() == 1){
            block.findViewById(R.id.sliderdots_frame).setVisibility(View.GONE);
            return block;
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

            CardView cardView = frame.findViewById(R.id.frame);

            Glide.with(context).load(f.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

            if(f.ID > 0) {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(10);

                        //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,img,"imageMain");
                        Intent event = new Intent(context, DetailActivity.class);
                        int id = f.ID;
                        event.putExtra(Variables.Id, id);
                        context.startActivity(event);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //context.startActivity(event, activityOptionsCompat.toBundle());

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
                                ImageView distance_icon = frame.findViewById(R.id.event_distance_icon);
                                distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                w_distance.setVisibility(View.VISIBLE);

                                TextView t_distance = frame.findViewById(R.id.event_route_distance);
                                t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                t_distance.setText(st.Text);
                                break;
                            case "Level":
                                LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                                ImageView level_icon = frame.findViewById(R.id.event_difficulty_icon);
                                level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                w_level.setVisibility(View.VISIBLE);

                                TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                                t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
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

            final Node n = node_list.get(c);

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

            LinearLayout categoryFrame = category.findViewById(R.id.category_title);

            categoryFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListDetailActivity.class);
                    intent.putExtra(Variables.Title, n.Category.Description);
                    intent.putExtra(Variables.Id, n.Category.ID);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            for(int i = 0; i < n.Nodes.size(); i++){
                View category_item = View.inflate(context, R.layout.block_category_list_item, null);

                final InfoBlock info = n.Nodes.get(i);

                TextView t = category_item.findViewById(R.id.list_item_title);
                TextView st = category_item.findViewById(R.id.list_item_date);

                st.setTextColor(Color.parseColor(Settings.colors.YearColor));
                st.setText(info.SubTitle.get(0).Text);

                t.setText(info.Title);

                category_list.addView(category_item);

                LinearLayout itemFrame = category_item.findViewById(R.id.list_item);
                itemFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent event = new Intent(context, DetailActivity.class);
                        event.putExtra(Variables.Id, info.ID);
                        context.startActivity(event);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }

            views.add(category);
        }


        SliderTwoPageAdapter pageAdapter = new SliderTwoPageAdapter(views);
        viewPager.setAdapter(pageAdapter);

        final int total_dots =  pageAdapter.getCount() /2 + (pageAdapter.getCount() % 2 > 0 ? 1 : 0) + 1;

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
                i = i != 0 ? i / 2 + 1 : 0;

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

            final Node n = node_list.get(c);

            View category = View.inflate(context, R.layout.block_category_scroller_list, null);

            LinearLayout category_frame = category.findViewById(R.id.category_title);

            category_frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListDetailActivity.class);
                    intent.putExtra(Variables.Title, n.Category.Description);
                    intent.putExtra(Variables.Id, n.Category.ID);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            TextView category_title = category.findViewById(R.id.category_list_title);
            LinearLayout category_list = category.findViewById(R.id.category_list);
            ImageView icon = category.findViewById(R.id.category_icon);

            category_title.setText(n.Category.Description);
            icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

            if(c < node_list.size()-1){
                category.setBackground(context.getDrawable(R.drawable.border_bottom));
            }


            for(int i = 0; i < n.Nodes.size(); i++){
                View category_item = View.inflate(context, R.layout.block_category_scroller_item, null);


                final InfoBlock info = n.Nodes.get(i);

                LinearLayout frame = category_item.findViewById(R.id.list_item_frame);
                frame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(Variables.Title, n.Category.Description);
                        intent.putExtra(Variables.Id, info.ID);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });


                TextView t = category_item.findViewById(R.id.list_item_title);
                LinearLayout routeInfo = category_item.findViewById(R.id.route_briefing);

                if(info.SubTitle.size() > 1){
                    routeInfo.setVisibility(View.VISIBLE);
                    category_item.findViewById(R.id.list_item_date).setVisibility(View.GONE);

                    for (SubTitle st: info.SubTitle) {
                        if(st.Icon != null) {
                            switch (st.Icon) {
                                case "Hike":
                                    LinearLayout w_distance = category_item.findViewById(R.id.event_distance_icon_wrapper);
                                    ImageView distance_icon = category_item.findViewById(R.id.event_distance_icon);
                                    distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_distance.setVisibility(View.VISIBLE);

                                    TextView t_distance = category_item.findViewById(R.id.event_route_distance);
                                    t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                    t_distance.setText(st.Text);
                                    break;
                                case "Level":
                                    LinearLayout w_level = category_item.findViewById(R.id.event_difficulty_icon_wrapper);
                                    ImageView level_icon = category_item.findViewById(R.id.event_difficulty_icon);
                                    level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_level.setVisibility(View.VISIBLE);

                                    TextView t_level = category_item.findViewById(R.id.event_route_difficulty);
                                    t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                    t_level.setText(st.Text);
                                    break;
                            }
                        }
                    }

                }else {
                    TextView st = category_item.findViewById(R.id.list_item_date);

                    st.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    st.setText(info.SubTitle.get(0).Text);
                }

                t.setText(info.Title);
                category_list.addView(category_item);
            }

            scroller.addView(category);
        }

        return category_block;
    }

    public static View setCategoryListDetailBlock(String title, final CategoryListDetail categoryListDetail, final Context context){

        View frame_list = View.inflate(context, R.layout.block_frame_list, null);

        TextView layout_title = frame_list.findViewById(R.id.spotlight_block_title);

        if(title != null) {
            layout_title.setText(title);
            layout_title.setVisibility(View.VISIBLE);
        }else{
            layout_title.setVisibility(View.GONE);
        }

        Data.CurrentCategoryList = categoryListDetail.Nodes;

        LinearLayout views_wrapper = frame_list.findViewById(R.id.spotlight_block_views);

        for (final InfoEventBlock f: categoryListDetail.Nodes) {
            View frame = View.inflate(context, R.layout.block_frame, null);

            TextView frameTitle = frame.findViewById(R.id.frame_title);
            frameTitle.setText(f.Title);

            final ImageView img = frame.findViewById(R.id.frame_image);

            Glide.with(context).load(f.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

            if(f.ID > 0) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);

                        Intent event = new Intent(context, DetailActivity.class);
                        int id = f.ID;
                        event.putExtra(Variables.Id, id);
                        context.startActivity(event);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                                ImageView distance_icon = frame.findViewById(R.id.event_distance_icon);
                                distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                w_distance.setVisibility(View.VISIBLE);

                                TextView t_distance = frame.findViewById(R.id.event_route_distance);
                                t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                t_distance.setText(st.Text);
                                break;
                            case "Level":
                                LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                                ImageView level_icon = frame.findViewById(R.id.event_difficulty_icon);
                                level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                w_level.setVisibility(View.VISIBLE);


                                TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                                t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
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

    public static void setCategoryListSortBlock(final CategoryListDetail categoryListDetail, LinearLayout sortList, final Context context){
        sortList.removeAllViews();

        for (final Sorting s: categoryListDetail.Sorting) {
            View frame = View.inflate(context, R.layout.block_sort_option, null);

            final TextView sort = frame.findViewById(R.id.sort_title);
            sort.setText(s.Description);
            sort.setTextColor(Color.parseColor(Settings.colors.Gray3));

            sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Data.CurrentCategorySortOption != null) {
                        //Data.CurrentCategorySortOption.setTypeface(Data.CurrentCategorySortOption.getTypeface(), Typeface.NORMAL);
                        Data.CurrentCategorySortOption.setTextColor(Color.parseColor(Settings.colors.Gray3));
                    }

                    //sort.setTypeface(sort.getTypeface(), Typeface.BOLD);
                    sort.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    Data.CurrentCategorySortOption = sort;
                    sortCategoryListSortBlock(context, s.Key);
                }
            });

            sortList.addView(frame);
        }
    }

    public static void sortCategoryListSortBlock(final Context context, String sort) {
        try {

            LinearLayout mainContent = ((Activity) context).findViewById(R.id.main_content);
            mainContent.removeAllViews();

            List<InfoEventBlock> list =  Data.CurrentCategoryList;

            if(sort.contentEquals("Date")) {
                //sortList
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {
                        return o1.Date < o2.Date ? -1
                                : o1.Date > o2.Date ? 1
                                : 0;
                    }
                });
            }else if(sort.contentEquals("Price")){
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {

                        float p1;
                        float p2;

                        if(!o1.Price.equals("") && !o2.Price.equals("")){
                            p1 = Float.valueOf(o1.Price);
                            p2 = Float.valueOf(o2.Price);
                            return p1 < p2 ? -1 : p1 > p2 ? 1 : 0;
                        }
                        return 1;
                    }
                });
            }else if(sort.contentEquals("Local")){
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {
                        return o1.Local.compareTo(o2.Local);
                    }
                });
            }else if(sort.contentEquals("Difficulty")){
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {
                        return o1.Difficulty < o2.Difficulty ? -1
                                : o1.Difficulty > o2.Difficulty ? 1
                                : 0;
                    }
                });
            }else if(sort.contentEquals("Distance")){
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {
                        return o1.Distance < o2.Distance ? -1
                                : o1.Distance > o2.Distance ? 1
                                : 0;
                    }
                });
            }else{
                Collections.sort(list, new Comparator<InfoEventBlock>() {
                    @Override
                    public int compare(InfoEventBlock o1, InfoEventBlock o2) {
                        return o1.Title.compareTo(o2.Title);
                    }
                });
            }

            for (final InfoEventBlock f: list) {
                View frame = View.inflate(context, R.layout.block_frame, null);

                TextView frameTitle = frame.findViewById(R.id.frame_title);
                frameTitle.setText(f.Title);

                final ImageView img = frame.findViewById(R.id.frame_image);

                Glide.with(context).load(f.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

                if(f.ID > 0) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            Intent event = new Intent(context, DetailActivity.class);
                            int id = f.ID;
                            event.putExtra(Variables.Id, id);
                            context.startActivity(event);
                            ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                                    ImageView distance_icon = frame.findViewById(R.id.event_distance_icon);
                                    distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_distance.setVisibility(View.VISIBLE);

                                    TextView t_distance = frame.findViewById(R.id.event_route_distance);
                                    t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                    t_distance.setText(st.Text);
                                    break;
                                case "Level":
                                    LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                                    ImageView level_icon = frame.findViewById(R.id.event_difficulty_icon);
                                    level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_level.setVisibility(View.VISIBLE);


                                    TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                                    t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
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

                mainContent.addView(frame, layoutParams);

                ScrollView scroll = ((Activity)context).findViewById(R.id.main_content_scroll);
                scroll.fullScroll(ScrollView.FOCUS_UP);
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public static View setSearch(final Search search, final Context context){

        View frame_list = View.inflate(context, R.layout.block_frame_list, null);

        TextView layout_title = frame_list.findViewById(R.id.spotlight_block_title);
        layout_title.setVisibility(View.GONE);

        LinearLayout views_wrapper = frame_list.findViewById(R.id.spotlight_block_views);

        if(search.Events != null && search.Events.size() > 0) {
            for (final Event e : search.Events) {
                View frame = View.inflate(context, R.layout.block_frame, null);

                TextView frameTitle = frame.findViewById(R.id.frame_title);
                frameTitle.setText(e.Title);

                TextView subTitle = frame.findViewById(R.id.frame_date);
                subTitle.setText(e.SubTitle.get(0).Text);
                subTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));

                final ImageView img = frame.findViewById(R.id.frame_image);
                /*DownloadImageAsync obj = new DownloadImageAsync() {

                    @Override
                    protected void onPostExecute(Bitmap bmp) {
                        super.onPostExecute(bmp);
                        img.setImageBitmap(bmp);
                    }
                };
                obj.execute(e.Images.get(0));*/

                Glide.with(context).load(e.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

                if (e.ID > 0) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent event = new Intent(context, DetailActivity.class);
                            int id = e.ID;
                            event.putExtra(Variables.Id, id);
                            context.startActivity(event);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                int px = Math.round(Settings.spotLightBottomMargin * context.getResources().getDisplayMetrics().scaledDensity);

                layoutParams.setMargins(0, 0, 0, px);

                views_wrapper.addView(frame, layoutParams);
            }
        }

        if(search.Places != null && search.Places.size() > 0) {
            for (final Place e : search.Places) {
                View frame = View.inflate(context, R.layout.block_frame, null);

                TextView frameTitle = frame.findViewById(R.id.frame_title);
                frameTitle.setText(e.Title);

                TextView subTitle = frame.findViewById(R.id.frame_date);
                subTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));
                subTitle.setText(e.SubTitle.get(0).Text);

                final ImageView img = frame.findViewById(R.id.frame_image);
                /*DownloadImageAsync obj = new DownloadImageAsync() {

                    @Override
                    protected void onPostExecute(Bitmap bmp) {
                        super.onPostExecute(bmp);
                        img.setImageBitmap(bmp);
                    }
                };
                obj.execute(e.Images.get(0));*/

                Glide.with(context).load(e.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

                if (e.ID > 0) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent event = new Intent(context, DetailActivity.class);
                            int id = e.ID;
                            event.putExtra(Variables.Id, id);
                            context.startActivity(event);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                int px = Math.round(Settings.spotLightBottomMargin * context.getResources().getDisplayMetrics().scaledDensity);

                layoutParams.setMargins(0, 0, 0, px);

                views_wrapper.addView(frame, layoutParams);
            }
        }

        if(search.Routes != null && search.Routes.size() > 0) {
            for (final Route f: search.Routes) {
                View frame = View.inflate(context, R.layout.block_frame, null);

                TextView frameTitle = frame.findViewById(R.id.frame_title);
                frameTitle.setText(f.Title);

                final ImageView img = frame.findViewById(R.id.frame_image);
                /*DownloadImageAsync obj = new DownloadImageAsync(){

                    @Override
                    protected void onPostExecute(Bitmap bmp) {
                        super.onPostExecute(bmp);
                        img.setImageBitmap(bmp);
                    }
                };
                obj.execute(f.Images.get(0));*/

                Glide.with(context).load(f.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

                if(f.ID > 0) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,img,"imageMain");
                            Intent event = new Intent(context, DetailActivity.class);
                            int id = f.ID;
                            event.putExtra(Variables.Id, id);
                            context.startActivity(event);
                            ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            //context.startActivity(event, activityOptionsCompat.toBundle());

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
                                    ImageView distance_icon = frame.findViewById(R.id.event_distance_icon);
                                    distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_distance.setVisibility(View.VISIBLE);

                                    TextView t_distance = frame.findViewById(R.id.event_route_distance);
                                    t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                                    t_distance.setText(st.Text);
                                    break;
                                case "Level":
                                    LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                                    ImageView level_icon = frame.findViewById(R.id.event_difficulty_icon);
                                    level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                                    w_level.setVisibility(View.VISIBLE);

                                    TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                                    t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
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
        }

        return frame_list;
    }

    public static void setEvent(final Context context, LinearLayout mainContent, final Event event, boolean like, boolean subscribe){
        TextView title = mainContent.findViewById(R.id.event_title);
        title.setText(event.Title);

        final ImageView img = mainContent.findViewById(R.id.detail_image);

        Glide.with(context).load(event.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

        if(like){
            ImageView likeButton = mainContent.findViewById(R.id.event_ac_heart_icon);
            likeButton.setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.SRC_ATOP);
        }

        if(subscribe){
            ImageView subscribeButton = mainContent.findViewById(R.id.event_ac_bell_icon);
            subscribeButton.setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.SRC_ATOP);
        }

        TextView descriptionTitle = mainContent.findViewById(R.id.event_description_title);
        descriptionTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));
        //TODO setsize with resources value
        descriptionTitle.setTextSize(16);

        String dateInfo = event.NextDates != null && event.NextDates.size() > 0 ? event.NextDates.get(0) : null;
        LinearLayout date_frame = mainContent.findViewById(R.id.event_date_wrapper);
        if(dateInfo != null) {
            ImageView dateIcon = mainContent.findViewById(R.id.date_icon);
            dateIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView dateLabel = mainContent.findViewById(R.id.date_label);
            dateLabel.setText(Settings.labels.Date);
            dateLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

            TextView moreDateLabel = mainContent.findViewById(R.id.label_more_dates);
            if(event.NextDates.size() > 1) {
                moreDateLabel.setText(String.format("[+ %s]", Settings.labels.Dates));
                moreDateLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
                moreDateLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context);

                        View dates = View.inflate(context, R.layout.block_event_more_dates, null);
                        LinearLayout wrapper = dates.findViewById(R.id.more_dates_content);

                        dates.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        for (String d : event.NextDates) {
                            TextView date = new TextView(context);
                            date.setText(d);

                            wrapper.addView(date);
                        }

                        dialog.setContentView(dates);

                        ((TextView) dialog.findViewById(R.id.more_dates_title)).setTextColor(Color.parseColor(Settings.colors.YearColor));

                        dialog.show();
                    }
                });
                moreDateLabel.setVisibility(View.VISIBLE);
            }else{
                moreDateLabel.setVisibility(View.GONE);
            }

            TextView date = mainContent.findViewById(R.id.event_date);
            date.setText(event.NextDates.get(0));
            date.setVisibility(View.VISIBLE);
            date_frame.setVisibility(View.VISIBLE);
        }else{
            date_frame.setVisibility(View.GONE);
        }


        LinearLayout schedule_frame = mainContent.findViewById(R.id.event_time_wrapper);
        if(event.CustomHours != null){
            schedule_frame.setVisibility(View.VISIBLE);

            ImageView timeIcon = mainContent.findViewById(R.id.time_icon);
            timeIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView timeLabel = mainContent.findViewById(R.id.label_time);
            timeLabel.setText(Settings.labels.Schedule);
            timeLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            TextView moreTimeLabel = mainContent.findViewById(R.id.label_more_time);
            moreTimeLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

            schedule_frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.block_event_hours);
                    dialog.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ((TextView) dialog.findViewById(R.id.more_hours)).setTextColor(Color.parseColor(Settings.colors.YearColor));

                    WebView schedule = dialog.findViewById(R.id.more_hours_content);
                    schedule.loadData(CommonManager.WebViewFormatRegular(event.CustomHours), CommonManager.MimeType(), CommonManager.Encoding());

                    dialog.show();
                }
            });
        }else{
            schedule_frame.setVisibility(View.GONE);
        }

        LinearLayout locationWrapper = mainContent.findViewById(R.id.event_location_wrapper);

        if(event.Points != null && event.Points.size() > 0) {
            final Point point = event.Points.get(0);

            ImageView locationIcon = mainContent.findViewById(R.id.location_icon);
            locationIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView locationLabel = mainContent.findViewById(R.id.label_locality);
            locationLabel.setText(Settings.labels.Place);
            locationLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            TextView moreInfoLabel = mainContent.findViewById(R.id.label_more_info);
            moreInfoLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

            TextView locationName = mainContent.findViewById(R.id.location_name);
            locationName.setText(point.Title);

            LinearLayout locationFrame = mainContent.findViewById(R.id.location_frame);
            locationFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.block_event_more_info);

                    TextView local_label = dialog.findViewById(R.id.more_info_title);
                    TextView name_label = dialog.findViewById(R.id.more_info_label_name);
                    TextView address_label = dialog.findViewById(R.id.more_info_label_address);
                    TextView town_label = dialog.findViewById(R.id.more_info_label_town);
                    TextView location_label = dialog.findViewById(R.id.more_info_label_geo_location);

                    local_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    name_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    address_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    town_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    location_label.setTextColor(Color.parseColor(Settings.colors.YearColor));

                    local_label.setText(Settings.labels.Place);
                    name_label.setText(Settings.labels.Name);
                    address_label.setText(Settings.labels.Address);
                    town_label.setText(Settings.labels.Parish);
                    location_label.setText(Settings.labels.Geolocation);

                    dialog.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ImageView map_detail = dialog.findViewById(R.id.map_detail);
                    map_detail.setColorFilter(Color.parseColor(Settings.colors.YearColor));
                    map_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra(Variables.Type, Variables.Maps);
                            intent.putExtra(Variables.Lat, point.Coordinates.Lat);
                            intent.putExtra(Variables.Lng, point.Coordinates.Lng);
                            context.startActivity(intent);
                        }
                    });

                    TextView name = (TextView) dialog.findViewById(R.id.more_info_name);
                    name.setText(point.Title);

                    WebView addess = dialog.findViewById(R.id.more_info_address);

                    addess.loadData(CommonManager.WebViewFormatLight(point.Address, context.getResources().getDisplayMetrics()), CommonManager.MimeType(), CommonManager.Encoding());

                    TextView town = (TextView) dialog.findViewById(R.id.more_info_town);
                    town.setText(point.TownCouncil.Description);

                    TextView location = (TextView) dialog.findViewById(R.id.more_info_geo_location);
                    location.setText(String.format("%s, %s", point.Coordinates.Lat, point.Coordinates.Lng));

                    dialog.show();
                }
            });

            locationWrapper.setVisibility(View.VISIBLE);
        }


        LinearLayout price_frame = mainContent.findViewById(R.id.event_price_wrapper);

        if(event.Price.Text != null) {
            ImageView euroIcon = mainContent.findViewById(R.id.euro_icon);
            euroIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView priceLabel = mainContent.findViewById(R.id.label_price);
            priceLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            priceLabel.setText(Settings.labels.Price);
            WebView price = mainContent.findViewById(R.id.price);

            String _p = event.Price.Text.substring(event.Price.Text.indexOf("<p>"), event.Price.Text.indexOf("</p>")) + "</p>" ;

            price.loadData(String.format(Settings.html, _p), "text/html; charset=utf-8", "UTF-8");

            Button eventTicket = mainContent.findViewById(R.id.event_ticket);
            eventTicket.setVisibility(event.OnlineTicket != null ? View.VISIBLE : View.GONE );
            if(event.OnlineTicket != null) {
                final String ticket_page = event.OnlineTicket;
                eventTicket.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Settings.colors.YearColor)));
                eventTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(ticket_page));
                        context.startActivity(browser);
                    }
                });
            }

            price_frame.setVisibility(View.VISIBLE);
        }

        if(event.Description != null) {
            WebView description = mainContent.findViewById(R.id.event_description_info);
            description.loadData(CommonManager.WebViewFormatRegular(event.Description), CommonManager.MimeType(), CommonManager.Encoding());
            mainContent.findViewById(R.id.event_description_wrapper).setVisibility(View.VISIBLE);
        }
    }

    public static void setPlace(final Context context, LinearLayout mainContent, final Place place){
        TextView title = mainContent.findViewById(R.id.event_title);
        title.setText(place.Title);

        final ImageView img = mainContent.findViewById(R.id.detail_image);

        Glide.with(context).load(place.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

        TextView descriptionTitle = mainContent.findViewById(R.id.event_description_title);
        descriptionTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));
        descriptionTitle.setText(Settings.labels.Description);

        String dateInfo = place.NextDates != null && place.NextDates.size() > 0 ? place.NextDates.get(0) : null;
        LinearLayout date_frame = mainContent.findViewById(R.id.event_date_wrapper);
        if(dateInfo != null) {
            ImageView dateIcon = mainContent.findViewById(R.id.date_icon);
            dateIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView dateLabel = mainContent.findViewById(R.id.date_label);
            dateLabel.setText(Settings.labels.Date);
            dateLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
        }else{
            date_frame.setVisibility(View.GONE);
        }

        if(place.OfficeHours != null) {
            if(place.OfficeHours.StatusLabel != null) {
                TextView officeHoursLabel = mainContent.findViewById(R.id.label_office_hours_statuc);
                officeHoursLabel.setText(place.OfficeHours.StatusLabel);
                officeHoursLabel.setTextColor(context.getResources().getColor(R.color.colorWhite));

                Drawable border = context.getDrawable(R.drawable.office_hours);
                border.setTint(Color.parseColor(Settings.colors.YearColor));
                officeHoursLabel.setBackground(border);

                officeHoursLabel.setVisibility(View.VISIBLE);
            }

            if(place.OfficeHours.Text != null) {
                WebView officeHours = mainContent.findViewById(R.id.place_date);
                officeHours.loadData(String.format(Settings.html, place.OfficeHours.Text), "text/html; charset=utf-8", "UTF-8");
                officeHours.setVisibility(View.VISIBLE);
            }
        }

        if((place.OfficeHours.Text != null) || (place.OfficeHours.StatusLabel != null)) {
            date_frame.setVisibility(View.VISIBLE);
        }else{
            date_frame.setVisibility(View.GONE);
        }


        LinearLayout schedule_frame = mainContent.findViewById(R.id.event_time_wrapper);
        if(place.CustomHours != null){
            schedule_frame.setVisibility(View.VISIBLE);

            ImageView timeIcon = mainContent.findViewById(R.id.time_icon);
            timeIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView timeLabel = mainContent.findViewById(R.id.label_time);
            timeLabel.setText(Settings.labels.Schedule);
            timeLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            TextView moreTimeLabel = mainContent.findViewById(R.id.label_more_time);
            moreTimeLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

            schedule_frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.block_event_hours);

                    dialog.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ((TextView) dialog.findViewById(R.id.more_hours)).setTextColor(Color.parseColor(Settings.colors.YearColor));

                    WebView schedule = dialog.findViewById(R.id.more_hours_content);
                    schedule.loadData(String.format(Settings.html, place.CustomHours), "text/html; charset=utf-8", "UTF-8");

                    dialog.show();
                }
            });
        }else{
            schedule_frame.setVisibility(View.GONE);
        }

        LinearLayout locationWrapper = mainContent.findViewById(R.id.event_location_wrapper);

        if(place.Points != null && place.Points.size() > 0) {
            final Point point = place.Points.get(0);

            ImageView locationIcon = mainContent.findViewById(R.id.location_icon);
            locationIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView locationLabel = mainContent.findViewById(R.id.label_locality);
            locationLabel.setText(Settings.labels.Place);
            locationLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            TextView moreInfoLabel = mainContent.findViewById(R.id.label_more_info);
            moreInfoLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

            TextView locationName = mainContent.findViewById(R.id.location_name);
            locationName.setText(point.Title);

            LinearLayout locationFrame = mainContent.findViewById(R.id.location_frame);
            locationFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.block_event_more_info);

                    dialog.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView local_label = dialog.findViewById(R.id.more_info_title);
                    TextView name_label = dialog.findViewById(R.id.more_info_label_name);
                    TextView address_label = dialog.findViewById(R.id.more_info_label_address);
                    TextView town_label = dialog.findViewById(R.id.more_info_label_town);
                    TextView location_label = dialog.findViewById(R.id.more_info_label_geo_location);

                    local_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    name_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    address_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    town_label.setTextColor(Color.parseColor(Settings.colors.YearColor));
                    location_label.setTextColor(Color.parseColor(Settings.colors.YearColor));

                    local_label.setText(Settings.labels.Place);
                    name_label.setText(Settings.labels.Name);
                    address_label.setText(Settings.labels.Address);
                    town_label.setText(Settings.labels.Parish);
                    location_label.setText(Settings.labels.Geolocation);

                    ImageView map_detail = dialog.findViewById(R.id.map_detail);
                    map_detail.setColorFilter(Color.parseColor(Settings.colors.YearColor));
                    map_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra(Variables.Type, Variables.Maps);
                            intent.putExtra(Variables.Lat, point.Coordinates.Lat);
                            intent.putExtra(Variables.Lng, point.Coordinates.Lng);
                            context.startActivity(intent);
                        }
                    });


                    TextView name = (TextView) dialog.findViewById(R.id.more_info_name);
                    name.setText(point.Title);

                    WebView addess = dialog.findViewById(R.id.more_info_address);
                    addess.loadData(CommonManager.WebViewFormatLight(point.Address, context.getResources().getDisplayMetrics()), CommonManager.MimeType(), CommonManager.Encoding());

                    TextView town = dialog.findViewById(R.id.more_info_town);
                    town.setText(point.TownCouncil.Description);

                    TextView location = dialog.findViewById(R.id.more_info_geo_location);
                    location.setText(String.format("%s, %s", point.Coordinates.Lat, point.Coordinates.Lng));

                    dialog.show();
                }
            });

            locationWrapper.setVisibility(View.VISIBLE);
        }


        LinearLayout price_frame = mainContent.findViewById(R.id.event_price_wrapper);

        if(place.Price.Text != null && !place.Price.Text.trim().equals("")) {
            ImageView euroIcon = mainContent.findViewById(R.id.euro_icon);
            euroIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
            TextView priceLabel = mainContent.findViewById(R.id.label_price);
            priceLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            priceLabel.setText(Settings.labels.Price);
            WebView price = mainContent.findViewById(R.id.price);

            String _p = CommonManager.ReturnFirstPrice(place.Price.Text);

            price.loadData(CommonManager.WebViewFormatRegular(_p), CommonManager.MimeType(), CommonManager.Encoding());

            TextView morePriceLabel = mainContent.findViewById(R.id.label_more_price);
            morePriceLabel.setText(String.format("[+ %s]", Settings.labels.Info));
            morePriceLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));
            morePriceLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.block_event_hours);

                    dialog.findViewById(R.id.close_detail).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView title = dialog.findViewById(R.id.more_hours);
                    title.setText(Settings.labels.Price);
                    title.setTextColor(Color.parseColor(Settings.colors.YearColor));

                    WebView schedule = dialog.findViewById(R.id.more_hours_content);
                    schedule.loadData(CommonManager.WebViewFormatRegular(place.Price.Text), CommonManager.MimeType(), CommonManager.Encoding());

                    dialog.show();
                }
            });
            morePriceLabel.setVisibility(View.VISIBLE);


            Button eventTicket = mainContent.findViewById(R.id.event_ticket);
            eventTicket.setVisibility(place.OnlineTicket != null ? View.VISIBLE : View.GONE );
            if(place.OnlineTicket != null) {
                final String ticket_page = place.OnlineTicket;
                eventTicket.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Settings.colors.YearColor)));
                eventTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(ticket_page));
                        context.startActivity(browser);*/
                    }
                });
            }

            price_frame.setVisibility(View.VISIBLE);
        }

        if(place.Description != null && !place.Description.trim().equals("")) {
            WebView description = mainContent.findViewById(R.id.event_description_info);
            description.loadData(CommonManager.WebViewFormatRegular(place.Description), CommonManager.MimeType(), CommonManager.Encoding());
            mainContent.findViewById(R.id.event_description_wrapper).setVisibility(View.VISIBLE);
        }

        if(place.ItHappensHere != null){
            LinearLayout here = mainContent.findViewById(R.id.detail_it_happens_here);
            JsonArray jsonObjectType5 = new Gson().toJsonTree(place.ItHappensHere.Contents).getAsJsonArray();
            Type NodeTypeList = new TypeToken<ArrayList<InfoBlock>>(){}.getType();
            List<InfoBlock> node_list = new Gson().fromJson(jsonObjectType5.toString(), NodeTypeList);
            here.addView(LayoutManager.setSliderBlock(place.ItHappensHere.Title, node_list, context));
        }
    }

    public static void setRoute(final Context context, LinearLayout mainContent, final Route route){
        TextView title = mainContent.findViewById(R.id.event_title);
        title.setText(route.Title);

        final ImageView img = mainContent.findViewById(R.id.detail_image);
        /*DownloadImageAsync obj = new DownloadImageAsync(){

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);
                img.setImageBitmap(bmp);
            }
        };
        obj.execute(route.Images.get(0));*/

        Glide.with(context).load(route.Images.get(0)).placeholder(R.drawable.image_frame).into(img);

        TextView descriptionTitle = mainContent.findViewById(R.id.event_description_title);
        descriptionTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));


        ImageView distanceIcon = mainContent.findViewById(R.id.event_distance_icon);
        distanceIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
        TextView distanceLabel = mainContent.findViewById(R.id.event_route_distance);
        distanceLabel.setText(Settings.labels.Place);
        distanceLabel.setTextColor(Color.parseColor(Settings.colors.YearColor));

        LinearLayout frame = mainContent.findViewById(R.id.event_route_briefing);

        for (SubTitle st: route.SubTitle) {

            if(st.Icon != null) {
                switch (st.Icon) {
                    case "Hike":
                        LinearLayout w_distance = frame.findViewById(R.id.event_distance_icon_wrapper);
                        ImageView distance_icon = frame.findViewById(R.id.event_distance_icon);
                        distance_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                        w_distance.setVisibility(View.VISIBLE);

                        TextView t_distance = frame.findViewById(R.id.event_route_distance);
                        t_distance.setTextColor(Color.parseColor(Settings.colors.YearColor));
                        t_distance.setText(st.Text);
                        break;
                    case "Level":
                        LinearLayout w_level = frame.findViewById(R.id.event_difficulty_icon_wrapper);
                        ImageView level_icon = frame.findViewById(R.id.event_difficulty_icon);
                        level_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

                        w_level.setVisibility(View.VISIBLE);

                        TextView t_level = frame.findViewById(R.id.event_route_difficulty);
                        t_level.setTextColor(Color.parseColor(Settings.colors.YearColor));
                        t_level.setText(st.Text);
                        break;
                }
            }
        }

        ImageView duration_icon = frame.findViewById(R.id.event_duration_icon);
        duration_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

        TextView t_duration = frame.findViewById(R.id.event_route_duration);
        t_duration.setTextColor(Color.parseColor(Settings.colors.YearColor));
        t_duration.setText(route.Duration);


        ImageView price_icon = frame.findViewById(R.id.event_euro_icon);
        price_icon.setColorFilter(Color.parseColor(Settings.colors.YearColor));

        TextView t_price = frame.findViewById(R.id.event_price_label);
        t_price.setTextColor(Color.parseColor(Settings.colors.YearColor));
        t_price.setText(Settings.labels.Free);


        final LinearLayout routeMaps = mainContent.findViewById(R.id.event_route_buttons);

        if(route.PointsMap.size() > 0){
            routeMaps.setVisibility(View.VISIBLE);
            Button seeMap = mainContent.findViewById(R.id.btn_route_seemap);
            seeMap.setText(Settings.labels.SeeMap);
            Drawable border = context.getDrawable(R.drawable.see_map_border);
            border.setTint(Color.parseColor(Settings.colors.YearColor));
            seeMap.setBackground(border);

            seeMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapsActivity.class);

                    List<Point> pointsList = new ArrayList<>();

                    for (PointMap pm:route.PointsMap) {
                        for (Point p:pm.Point) {
                            pointsList.add(p);
                        }
                    }

                    String mapPoints = new Gson().toJson(route.PointsMap);
                    String points = new Gson().toJson(pointsList);

                    intent.putExtra(Variables.Title, route.Title);
                    intent.putExtra(Variables.SeeRoute, false);
                    intent.putExtra(Variables.MapPoints, mapPoints);
                    intent.putExtra(Variables.Points, points);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            Button seeRoute = mainContent.findViewById(R.id.btn_route_route);
            seeRoute.setText(Settings.labels.InitTrip);
            //seeRoute.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Settings.colors.YearColor)));
            Drawable bg = context.getDrawable(R.drawable.see_map_bg);
            bg.setTint(Color.parseColor(Settings.colors.YearColor));
            seeRoute.setBackground(bg);

            seeRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapsActivity.class);

                    List<Point> pointsList = new ArrayList<>();

                    for (PointMap pm:route.PointsMap) {
                        for (Point p:pm.Point) {
                            pointsList.add(p);
                        }
                    }

                    String mapPoints = new Gson().toJson(route.PointsMap);
                    String points = new Gson().toJson(pointsList);

                    intent.putExtra(Variables.Title, route.Title);
                    intent.putExtra(Variables.SeeRoute, true);
                    intent.putExtra(Variables.MapPoints, mapPoints);
                    intent.putExtra(Variables.Points, points);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

        }else{
            routeMaps.setVisibility(View.GONE);
        }


        frame.setVisibility(View.VISIBLE);

        LinearLayout description_frame = mainContent.findViewById(R.id.event_description_wrapper);

        if(route.Description != null) {
            description_frame.setBackground(null);

            WebView description = mainContent.findViewById(R.id.event_description_info);
            description.loadData(String.format(Settings.html, route.Description), "text/html; charset=utf-8", "UTF-8");
            description_frame.setVisibility(View.VISIBLE);
        }
    }
}

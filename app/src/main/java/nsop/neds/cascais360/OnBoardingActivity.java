package nsop.neds.cascais360;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import nsop.neds.cascais360.Manager.ControlsManager.SliderPageAdapter;
import nsop.neds.cascais360.Settings.Settings;

public class OnBoardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_on_boarding);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        final ViewPager viewPager = findViewById(R.id.sliderPager);

        List<View> views = new ArrayList<>();
        List<Drawable> img = new ArrayList<>();

        img.add(getDrawable(R.drawable.on_boarding_01));
        img.add(getDrawable(R.drawable.on_boarding_02));
        img.add(getDrawable(R.drawable.on_boarding_03));
        img.add(getDrawable(R.drawable.on_boarding_04));
        img.add(getDrawable(R.drawable.on_boarding_06));
        img.add(getDrawable(R.drawable.on_boarding_07));
        img.add(getDrawable(R.drawable.on_boarding_08));
        img.add(getDrawable(R.drawable.on_boarding_09));
        img.add(getDrawable(R.drawable.on_boarding_10));
        img.add(getDrawable(R.drawable.on_boarding_11));
        img.add(getDrawable(R.drawable.on_boarding_12));

        for(int i = 0; i < img.size(); i++) {
            ImageView view = new ImageView(this);
            view.setImageDrawable(img.get(i));
            view.setAdjustViewBounds(true);
            view.setCropToPadding(false);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            views.add(view);
        }

        viewPager.setAdapter(new SliderPageAdapter(views, this));

        final ImageView left_navigation = findViewById(R.id.left_arrow);
        final ImageView right_navigation = findViewById(R.id.right_arrow);
        final TextView app_navigation = findViewById(R.id.launch_app);

        left_navigation.setColorFilter(Color.parseColor(Settings.colors.Gray1));

        final int slider_size = img.size();

        left_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();

                if(i > 0 || i < slider_size){
                    viewPager.setCurrentItem(i - 1);
                }
            }
        });

        right_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();

                if(i == 0 || i < slider_size){
                    viewPager.setCurrentItem(i + 1);
                }
            }
        });

        app_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnBoardingActivity.this, MainActivity.class));
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    left_navigation.setColorFilter(Color.parseColor(Settings.colors.FieldDisabledBackground));
                    right_navigation.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                }else if (position == slider_size){
                    left_navigation.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                    right_navigation.setColorFilter(Color.parseColor(Settings.colors.FieldDisabledBackground));
                }
                else {
                    left_navigation.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                    right_navigation.setColorFilter(Color.parseColor(Settings.colors.Gray1));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}

package nsop.neds.mycascais.Manager.ControlsManager;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SliderTwoPageAdapter extends PagerAdapter {
    List<View> views;

    public SliderTwoPageAdapter(List<View> views){
        this.views = views;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View wrapper = views.get(position);
        collection.addView(wrapper);
        return wrapper;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.5f;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

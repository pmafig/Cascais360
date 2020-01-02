package nsop.neds.cascais360.Manager.ControlsManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SliderPageAdapter extends PagerAdapter {
    List<View> views;
    private Context context;

    public SliderPageAdapter(List<View> views, Context context){
        this.views = views;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View view = views.get(position);
        collection.addView(view);
        return view;
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

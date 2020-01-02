package nsop.neds.cascais360.Manager.ControlsManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import nsop.neds.cascais360.Entities.BlockEntity;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class HightLightManager extends AsyncTask<String, Void, JSONObject> {

    LinearLayout mainContent;
    Context context;

    public HightLightManager(Context context, LinearLayout mainContent){
        this.context = context;
        this.mainContent = mainContent;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        JSONObject block = null;

        try {
            block = new JSONObject(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return block;
    }

    @Override
    protected void onPostExecute(JSONObject block) {
        super.onPostExecute(block);

        try {

            setHighLightBlock(addHighLightBlock(block));

        } catch (Exception e) {
             Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

    //region Add Dashboard Type Block to Array
    //Add Type 1main_content
    private BlockEntity addHighLightBlock(JSONObject block){
        BlockEntity bEntity = null;
        try {
            JSONObject contents = block.getJSONObject("Contents");

            JSONArray images = contents.getJSONArray("Images");

            bEntity = new BlockEntity(block.getInt("Type"),
                    block.getInt("Weight"),
                    contents.getInt("ID"),
                    contents.getString("Title"),
                    contents.getString("SubTitle"),
                    images.get(0).toString(),
                    new SimpleDateFormat("yyyy-MM-dd").parse(contents.getString("Date")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return bEntity;
    }

    //region Render Block Types to Dashboard
    private void setHighLightBlock(final BlockEntity b){
        /*View block = View.inflate(context, R.layout.highlight, null);

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

        TextView title = block.findViewById(R.id.tv_title);
        title.setText(b.Title());

        TextView subTitle = block.findViewById(R.id.tv_subtitle);
        subTitle.setText(b.SubTitle());

        TextView month = block.findViewById(R.id.tv_month);
        Drawable bottom = context.getResources().getDrawable(R.drawable.hightlight_border_bottom);
        bottom.setTint(Color.parseColor(Settings.color));

        month.setBackground(bottom);
        month.setText(b.Month());
        month.setTextColor(Color.parseColor(Settings.color));

        TextView day = block.findViewById(R.id.tv_day);

        Drawable top = context.getResources().getDrawable(R.drawable.hightlight_border_top);
        top.setTint(Color.parseColor(Settings.color));

        day.setBackground(top);
        day.setTextColor(Color.parseColor(Settings.color));
        day.setText(b.Day());

        mainContent.addView(block);*/
    }

    //endregion
}

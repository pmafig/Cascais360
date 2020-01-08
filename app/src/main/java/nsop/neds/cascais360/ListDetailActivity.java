package nsop.neds.cascais360;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class ListDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        final Intent intent = getIntent();

        /*CallCategoryList(intent.getIntExtra("id", 0),
                intent.hasExtra("sort") ? intent.getStringExtra("sort") : "",
                intent.hasExtra("data") ? intent.getStringExtra("data") : "");*/

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        new MenuManager(this, toolbar, menuFragment, intent.getStringExtra(Variables.Title));

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();

        return parentIntent;
    }

    private void CallCategoryList(int id, String sort, String data){
        /*new SortListManager(this,
                (LinearLayout) findViewById(R.id.main_content),
                (RelativeLayout) findViewById(R.id.loadingPanel),
                (LinearLayout) findViewById(R.id.sorting_content)).execute(WebApiCalls.getCategory(id), sort, data);*/
    }

}

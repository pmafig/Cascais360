package nsop.neds.cascais360;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import nsop.neds.cascais360.Manager.DashboardManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        new DashboardManager(this,
                (LinearLayout) findViewById(R.id.main_content), (RelativeLayout) findViewById(R.id.loadingPanel)
        ).execute(WebApiCalls.getDashBoard());

        Toolbar toolbar = findViewById(R.id.toolbar);

        final ImageButton menu = toolbar.findViewById(R.id.menu_button);

        menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                //MenuFragment menu = (MenuFragment) getSupportFragmentManager().findFragmentByTag("menu");

                if (findViewById(R.id.menu).getVisibility() == View.VISIBLE){//getSupportFragmentManager().getFragments().size() > 0) {
                    /*for (Fragment fragment : getSupportFragmentManager().getFragments()) {

                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        menu.setBackground(getDrawable(R.drawable.ic_action_name));
                    }*/
                    findViewById(R.id.menu).setVisibility(View.GONE);
                    menu.setBackground(getDrawable(R.drawable.ic_action_name));
                }
                else {

                    menu.setBackground(getDrawable(R.drawable.ic_close));

                    /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_menu, new MenuFragment());
                    transaction.addToBackStack("menu");
                    transaction.commit();*/

                    findViewById(R.id.menu).setVisibility(View.VISIBLE);

                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu,new MenuFragment()).commit();
                }

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

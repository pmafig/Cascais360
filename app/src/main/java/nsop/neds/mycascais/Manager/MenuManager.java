package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.mycascais.AboutAppActivity;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.ListActivity;
import nsop.neds.mycascais.LoginActivity;
import nsop.neds.mycascais.MainActivity;
import nsop.neds.mycascais.ProfileActivity;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.SearchActivity;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.SettingsActivity;

public class MenuManager {

    Context context;

    Toolbar toolbar;
    LinearLayout menuFragment;
    ImageButton menu;
    FrameLayout frame;

    SessionManager sm;

    public MenuManager(final Context context, Toolbar toolbar, final LinearLayout menuFragment, @Nullable String title){
        this.context = context;

        this.toolbar = toolbar;
        this.menuFragment = menuFragment;
        this.menu = toolbar.findViewById(R.id.menu_button);
        this.frame = toolbar.findViewById(R.id.menu_button_frame);

        Drawable border = context.getDrawable(R.drawable.toolbar_border_bottom);
        //border.setTint(Color.parseColor(Settings.colors.YearColor));
        toolbar.setBackground(border);

        if(title != null) {
            TextView tv = toolbar.findViewById(R.id.toolbar_title);
            tv.setText(title);
            tv.setTextColor(Color.parseColor(Settings.colors.YearColor));
            tv.setVisibility(View.VISIBLE);

            toolbar.findViewById(R.id.toolbar_image).setVisibility(View.GONE);
        }

        TextView backButton = toolbar.findViewById(R.id.back_button);
        backButton.setText(Settings.labels.Back);

        sm = new SessionManager(context);

        setToolbarUserInfo();

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMenu();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMenu();
            }
        });

        TextView infoCascais = menuFragment.findViewById(R.id.label_WeatherCascais);
        infoCascais.setText(Settings.labels.WeatherCascais);

        TextView today = menuFragment.findViewById(R.id.label_Today);
        today.setText(Settings.labels.Today);

        menuFragment.findViewById(R.id.user_frame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button settings = menuFragment.findViewById(R.id.menu_button_settings);
        settings.setText(Settings.labels.Settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button about = menuFragment.findViewById(R.id.menu_button_about);
        about.setText(Settings.labels.AboutApp);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AboutAppActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        menuFragment.findViewById(R.id.menu_button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                if(AccountGeneral.logout(context)){
                    setToolbarUserInfo();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        Button login = menuFragment.findViewById(R.id.menu_button_login);
        login.setText(Settings.labels.LoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button home = menuFragment.findViewById(R.id.menu_button_home);
        home.setText(Settings.menus.get(0).Label);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button agenda = menuFragment.findViewById(R.id.menu_button_agenda);
        agenda.setText(Settings.menus.get(1).Label);
        agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Agenda);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button visit = menuFragment.findViewById(R.id.menu_button_visit);
        visit.setText(Settings.menus.get(2).Label);
        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Visit);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button route = menuFragment.findViewById(R.id.menu_button_route);
        route.setText(Settings.menus.get(3).Label);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Routes);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button search = menuFragment.findViewById(R.id.menu_button_search);
        search.setText(Settings.menus.get(4).Label);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra(Variables.Type, Variables.Search);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button calendar = menuFragment.findViewById(R.id.menu_button_calendar);
        calendar.setText(Settings.menus.get(5).Label);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra(Variables.Type, Variables.Calendar);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Button maps = menuFragment.findViewById(R.id.menu_button_map);
        maps.setText(Settings.menus.get(6).Label);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra(Variables.Type, Variables.Maps);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void clickMenu(){
        if (menuFragment.getVisibility() == View.VISIBLE){
            menuFragment.setVisibility(View.GONE);
            menu.setBackground(context.getDrawable(R.drawable.ic_action_name));
        }
        else {
            menu.setBackground(context.getDrawable(R.drawable.ic_close));
            menuFragment.setVisibility(View.VISIBLE);
        }
    }

    private void closeMenu(){
        menuFragment.setVisibility(View.GONE);
        menu.setBackground(context.getDrawable(R.drawable.ic_action_name));
    }

    private void setToolbarUserInfo(){

        if(sm.asUserLoggedOn()){
            TextView name = menuFragment.findViewById(R.id.user_name);
            name.setText(sm.getDisplayname());

            menuFragment.findViewById(R.id.user_loggedon_header).setVisibility(View.VISIBLE);
            menuFragment.findViewById(R.id.menu_button_login_frame).setVisibility(View.GONE);
        }else{
            menuFragment.findViewById(R.id.menu_button_login_frame).setVisibility(View.VISIBLE);
            menuFragment.findViewById(R.id.user_loggedon_header).setVisibility(View.GONE);
        }
    }
}

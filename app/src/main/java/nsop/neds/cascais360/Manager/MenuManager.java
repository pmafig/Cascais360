package nsop.neds.cascais360.Manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.ListActivity;
import nsop.neds.cascais360.LoginActivity;
import nsop.neds.cascais360.MainActivity;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.ReportManager;

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
        border.setTint(Color.parseColor(Settings.colors.YearColor));
        toolbar.setBackground(border);

        if(title != null) {
            TextView tv = toolbar.findViewById(R.id.toolbar_title);
            tv.setText(title);
            tv.setTextColor(Color.parseColor(Settings.colors.YearColor));
            tv.setVisibility(View.VISIBLE);

            toolbar.findViewById(R.id.toolbar_image).setVisibility(View.GONE);
        }

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

        menuFragment.findViewById(R.id.menu_button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                if(AccountGeneral.logout(context)){
                    setToolbarUserInfo();
                }
            }
        });

        menuFragment.findViewById(R.id.menu_button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });

        menuFragment.findViewById(R.id.menu_button_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });

        menuFragment.findViewById(R.id.menu_button_agenda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Agenda);
                context.startActivity(intent);
            }
        });

        menuFragment.findViewById(R.id.menu_button_visit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Visit);
                context.startActivity(intent);
            }
        });

        menuFragment.findViewById(R.id.menu_button_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra(Variables.Type, Variables.Routes);
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


   /* public static void CallWeather(Context context, NavigationView nav_view){
        new WeatherManager(context, nav_view).execute(WebApiCalls.getWeather());
    }

    public static boolean NavigationItemSelected(Context context, int id, DrawerLayout drawer){

        if(id == R.id.nav_login){
            context.startActivity(new Intent(context, LoginActivity.class));
        }else if (id == R.id.nav_home) {
            context.startActivity(new Intent(context, MainActivity.class));
        }else if (id == R.id.nav_definitions) {
            context.startActivity(new Intent(context, DefinitionsActivity.class));
        } else if (id == R.id.nav_agenda) {
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra("Type", "agenda");
            context.startActivity(intent);
        } else if (id == R.id.nav_visit) {
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra("Type", "visit");
            context.startActivity(intent);
        } else if (id == R.id.nav_routes) {
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra("Type", "routes");
            context.startActivity(intent);
        } else if (id == R.id.nav_search) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("type", "search");
            context.startActivity(intent);
        } else if (id == R.id.nav_calendar) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("type", "calendar");
            context.startActivity(intent);
        }else if (id == R.id.nav_map) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("type", "maps");
            context.startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void SetUserSettings(final Context context, NavigationView navigationView){

        if(sm == null) {
            sm = new SessionManager(context);
        }

        View navHeader = navigationView.getHeaderView(0);

        if(sm.asUserLoggedOn()) {
            navHeader.setVisibility(View.VISIBLE);
            ImageView usericon = navHeader.findViewById(R.id.user_icon_image);
            TextView username = navHeader.findViewById(R.id.user_name);
            username.setText(sm.getDisplayname());

            navigationView.getMenu().setGroupVisible(R.id.menu_gpr1, false);

            navigationView.getMenu().getItem(8).setVisible(true);

            ImageView logout = navHeader.findViewById(R.id.logout_icon);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AccountGeneral.logout(context)){
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                }
            });

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });

            usericon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });

        }else{
            navHeader.setVisibility(View.GONE);
            navigationView.getMenu().setGroupVisible(R.id.menu_gpr1, true);
        }
    }*/
}
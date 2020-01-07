package nsop.neds.cascais360;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Manager.Variables;

public class EmailSentActivity extends AppCompatActivity {

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        toolbar.findViewById(R.id.toolbar_image).setVisibility(View.GONE);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Intent intent = getIntent();
        String email = intent.getStringExtra(Variables.Email);

        TextView view = findViewById(R.id.accountEmail);
        view.setText(email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

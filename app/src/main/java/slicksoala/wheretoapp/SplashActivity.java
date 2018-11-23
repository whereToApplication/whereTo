package slicksoala.wheretoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.skyfishjy.library.RippleBackground;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.ripple);
        rippleBackground.startRippleAnimation();
    }
    @Override
    public void onBackPressed() {
        Intent backHomeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(backHomeIntent);
    }
}

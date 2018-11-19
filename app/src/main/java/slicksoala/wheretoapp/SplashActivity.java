package slicksoala.wheretoapp;

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
}

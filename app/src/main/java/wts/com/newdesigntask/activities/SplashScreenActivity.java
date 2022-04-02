package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import wts.com.newdesigntask.R;

public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String user;
    ImageView imgLogo;
    Animation fromTop, fromBottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imgLogo = findViewById(R.id.img_logo);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        imgLogo.setAnimation(fromTop);


        int SPLASH_SCREEN_TIME_OUT = 2500;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                user = sharedPreferences.getString("username", null);
                if (user != null) {
                    startActivity(new Intent(SplashScreenActivity.this, HomeDashboardActivity.class));
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
}
package billfold.com.synerzip.billfold;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import billfold.com.synerzip.billfold.constant.AppConstant;
import billfold.com.synerzip.billfold.ui.HomeActivity;
import billfold.com.synerzip.billfold.ui.LoginUserActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getActionBar().hide()
        setContentView(R.layout.activity_fullscreen);
        getSupportActionBar().hide();
        exitSplashScreen();
    }


    /*
       Showing splash screen with a timer. This will be useful when you
       want to show case your app logo / company,
        after finish your timer close splash screen and open login screen
      */
    private void exitSplashScreen() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FullscreenActivity.this);
                int id = preferences.getInt(AppConstant.USER_ID, -1);
                if (id == -1) {
                    Intent i = new Intent(FullscreenActivity.this, LoginUserActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent intent = new Intent(FullscreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                // close this activity

            }
        }, SPLASH_TIME_OUT);
    }


}

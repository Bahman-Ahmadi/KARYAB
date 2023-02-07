package ir.Bprogrammer.ika;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        boolean requestWindowFeature = requestWindowFeature(1);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(4098);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(1024, 1024);
        }
        setContentView(R.layout.activity_splash);

        TextView title = findViewById(R.id.title);
        TextView note = findViewById(R.id.note);
        
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazirblack.ttf"), 1);
        note.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);
    
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent move = new Intent(SplashActivity.this, MainActivity.class);
                move.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(move);
                overridePendingTransition(0,0);
                finish();
            }
        }, 2500); // wait for 5 seconds
        } catch( Exception e ) {
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
     }
}

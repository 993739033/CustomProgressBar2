package app.example.com.customprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    private CustomPB customPd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customPd = (CustomPB) findViewById(R.id.customPd);
    }

    public void onEnter(View view) {
        customPd.startAnim();
    }
}

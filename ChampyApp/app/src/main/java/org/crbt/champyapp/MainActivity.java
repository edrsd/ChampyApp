package org.crbt.champyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container,FgmConnectToChampy.newInstance(),"fgm_connect_to_champy")
                .commitNow();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

//    @Override
//    public boolean onKeyDown(int key_code, KeyEvent key_event) {
//        if (key_code== KeyEvent.KEYCODE_BACK) {
//            super.onKeyDown(key_code, key_event);
//            return true;
//        }
//        return false;
//    }

}
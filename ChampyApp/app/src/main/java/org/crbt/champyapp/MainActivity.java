package org.crbt.champyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Animation anim1,anim2,anim3;
    ImageView logoPavisa,logoInst,logoChampy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        anim1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pavisa_anim);
        anim2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.logo_anim);
        anim3= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.champy_anim);

        logoPavisa=findViewById(R.id.iv_pavisa_logo);
        logoInst=findViewById(R.id.iv_inst_logo);
        logoChampy=findViewById(R.id.iv_champy_logo);

        logoPavisa.setAnimation(anim1);
        logoInst.setAnimation(anim2);
        logoChampy.setAnimation(anim3);

        new Handler().postDelayed(()->{
            logoPavisa.setVisibility(View.GONE);
            logoInst.setVisibility(View.GONE);
            logoChampy.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container,FgmConnectToChampy.newInstance(),"fgm_connect_to_champy")
                    .commit();
        },3000);


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
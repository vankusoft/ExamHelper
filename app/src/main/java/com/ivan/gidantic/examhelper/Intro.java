package com.ivan.gidantic.examhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by IVAN on 2.12.2015 г..
 */
public class Intro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        Thread thread =new Thread(){
            public void run(){
                try{
                    wait(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent i = new Intent(Intro.this,SettingInfo.class);
                    startActivity(i);
                    finish();
                }
            }

        };
        thread.start();

    }
}

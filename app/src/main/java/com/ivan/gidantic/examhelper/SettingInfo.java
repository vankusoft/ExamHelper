package com.ivan.gidantic.examhelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;


public class SettingInfo extends Activity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    EditText etExam;
    EditText etGrade,etDate;
    Button btnSave,btnShowList;
    DatabaseInformation db;
    RadioGroup mainRadioGroup;
    RadioButton rbMainPassed,rbMainNotPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_info);
          settingReferences();

        btnSave.setOnClickListener(this);
        btnShowList.setOnClickListener(this);
        db= new DatabaseInformation(SettingInfo.this);
        mainRadioGroup.setOnCheckedChangeListener(this);

        etGrade.setEnabled(false);
        rbMainNotPassed.setChecked(true);

    }
    public void settingReferences(){
        etExam=(EditText)findViewById(R.id.etExam);
        etDate=(EditText)findViewById(R.id.etDate);
        etGrade=(EditText)findViewById(R.id.etGrade);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnShowList=(Button)findViewById(R.id.btnShowList);

        mainRadioGroup=(RadioGroup)findViewById(R.id.radioGroupMain);
        rbMainPassed=(RadioButton)findViewById(R.id.rbMainPassed);
        rbMainNotPassed=(RadioButton)findViewById(R.id.rbMainNotPassed);
    }


    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnSave:
                String examResult=etExam.getText().toString();
                String gradeResult=etGrade.getText().toString();
                String dateResult=etDate.getText().toString();
                String note="";
                boolean didItWork=true;
               try{
                   db.open();
                   db.addNewExam(examResult,dateResult,gradeResult,note);
                   db.close();
               }catch (SQLiteException e){
                   didItWork=false;
                   e.printStackTrace();
               }finally {
                   if(didItWork){
                       etExam.setText("");
                       etGrade.setText("");
                       etDate.setText("");
                       Toast.makeText(SettingInfo.this,"DATA SAVED SUCCESSFUL",Toast.LENGTH_SHORT).show();
                   }
               }
                rbMainNotPassed.setChecked(true);

                break;

            case R.id.btnShowList:
                Bundle bundle= new Bundle();
                int refresh=1;
                bundle.putInt("refresh",refresh);
                Intent ii = new Intent(SettingInfo.this,OutputDatabaseInfo.class);
                ii.putExtras(bundle);
                startActivity(ii);
                finish();
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId==R.id.rbMainPassed){
                etGrade.setEnabled(true);
            }else {
                etGrade.setEnabled(false);
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.exit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.btnExitMenu:
                finish();
                break;
        }
        return false;
    }
}

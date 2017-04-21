package com.ivan.gidantic.examhelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by IVAN on 29.11.2015 г..
 */
public class OutputDatabaseInfo extends Activity implements View.OnClickListener,
 AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener,RadioGroup.OnCheckedChangeListener{

    ListView examListView,optionListView;
    TextView tvExamInfo,tvDateInfo,tvGradeInfo,tvNoteInfo;
    Button btnAddExam,btnDialogEdit,btnDialogInfo;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    DatabaseInformation db;
    Cursor c;
    int mainPosition;
    Dialog dialogEdit,dialog,onItemClickDialog;
    String examEditResult,dateEditResult,gradeEditResult,noteEditResult;
    EditText etExamEdit,etDateEdit,etGradeEdit,etNoteEdit;
    RadioButton rbPassed,rbNotPassed;
    String resultFromEtGrade="";
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_info);

        setStuff();
        setDialogEdit();
        setListAndAdapter();
        BundleInformation();
        setDialogInformation();

         dialog= new Dialog(OutputDatabaseInfo.this);

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

    public void setStuff(){
        examListView=(ListView)findViewById(R.id.examListView);
        btnAddExam=(Button)findViewById(R.id.btnAddExam);
        btnAddExam.setOnClickListener(this);
    }

    public void setDialogEdit(){
        //
        dialogEdit= new Dialog(OutputDatabaseInfo.this);
        dialogEdit.setContentView(R.layout.dialog_edit);
        dialogEdit.setTitle("Промяна на съдържание.");
        etExamEdit=(EditText)dialogEdit.findViewById(R.id.etExamEdit);
        etDateEdit=(EditText)dialogEdit.findViewById(R.id.etDateEdit);
        etGradeEdit=(EditText)dialogEdit.findViewById(R.id.etGradeEdit);
        etNoteEdit=(EditText)dialogEdit.findViewById(R.id.etNoteEdit);
        btnDialogEdit=(Button)dialogEdit.findViewById(R.id.btnDialogEdit);
        btnDialogEdit.setOnClickListener(this);

        radioGroup=(RadioGroup)dialogEdit.findViewById(R.id.radioGroupID);
        rbPassed=(RadioButton)dialogEdit.findViewById(R.id.rbPassed);
        rbNotPassed=(RadioButton)dialogEdit.findViewById(R.id.rbNotPassed);
        radioGroup.setOnCheckedChangeListener(this);
        //
    }

    public void setListAndAdapter(){
        listItems= new ArrayList<>();
        adapter=new ArrayAdapter<String>(OutputDatabaseInfo.this,android.R.layout.simple_list_item_1,listItems);
        db=new DatabaseInformation(OutputDatabaseInfo.this);
        examListView.setAdapter(adapter);
        examListView.setOnItemLongClickListener(this);
        examListView.setOnItemClickListener(this);
    }

    public void ListViewRefresh(){
        listItems.clear();
        try {
            db.open();
            c = db.getAllExams();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                listItems.add(c.getString(c.getColumnIndex(db.KEY_EXAM)));
                adapter.notifyDataSetChanged();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            db.close();
            c.close();
        }
    }

    public void BundleInformation(){

        int refreshResult=0;
        try{
            Bundle refreshExtras=getIntent().getExtras();
             refreshResult=refreshExtras.getInt("refresh",2);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(refreshResult==1){
                ListViewRefresh();
            }else{
                Toast.makeText(OutputDatabaseInfo.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDialogInformation(){

        onItemClickDialog = new Dialog(OutputDatabaseInfo.this);
        onItemClickDialog.setTitle("Информация");
        onItemClickDialog.setContentView(R.layout.exam_information_only_layout);
        btnDialogInfo=(Button)onItemClickDialog.findViewById(R.id.btnDialogInfo);
        btnDialogInfo.setOnClickListener(this);
        tvExamInfo=(TextView)onItemClickDialog.findViewById(R.id.tvExamInfo);
        tvDateInfo=(TextView)onItemClickDialog.findViewById(R.id.tvDateInfo);
        tvGradeInfo=(TextView)onItemClickDialog.findViewById(R.id.tvGradeInfo);
        tvNoteInfo=(TextView)onItemClickDialog.findViewById(R.id.tvNoteInfo);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnAddExam:
                Intent i = new Intent(OutputDatabaseInfo.this,SettingInfo.class);
                startActivity(i);
                finish();
                break;

            case R.id.btnDialogEdit:
                examEditResult=etExamEdit.getText().toString();
                dateEditResult=etDateEdit.getText().toString();
                gradeEditResult=etGradeEdit.getText().toString();
                noteEditResult=etNoteEdit.getText().toString();
                try {
                    db.open();
                    db.updateDatabase(examEditResult, dateEditResult, gradeEditResult,noteEditResult, mainPosition);

                }catch (SQLiteException e){
                    e.printStackTrace();
                }finally {
                    db.close();
                    c.close();
                }
                dialogEdit.dismiss();
                dialog.dismiss();
                ListViewRefresh();
                break;

            case R.id.btnDialogInfo:
                onItemClickDialog.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            db.open();
            c = db.getCurrentExam(position + 1);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                tvExamInfo.setText(c.getString(c.getColumnIndex(db.KEY_EXAM)));
                tvDateInfo.setText(c.getString(c.getColumnIndex(db.KEY_DATE)));
                tvGradeInfo.setText(c.getString(c.getColumnIndex(db.KEY_GRADE)));
                tvNoteInfo.setText(c.getString(c.getColumnIndex(db.KEY_NOTE)));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            String resultEt=tvGradeInfo.getText().toString();
            if(resultEt.contentEquals("")){
                tvGradeInfo.setText("Все още няма!");
            }
            db.close();
            c.close();
            onItemClickDialog.show();
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        String[] options= new String[]{"Промени","Изтрий"};

        dialog.setTitle("Изберете опция");

        optionListView= new ListView(OutputDatabaseInfo.this);
        ArrayAdapter<String> optionArrayAdapter=new ArrayAdapter<String>(OutputDatabaseInfo.this,
                android.R.layout.simple_list_item_1,options);

        optionListView.setAdapter(optionArrayAdapter);
        dialog.setContentView(optionListView);
        dialog.show();

        mainPosition=position+1;

        optionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){

                    case 0:

                        try {
                            db.open();
                            c = db.getCurrentExam(mainPosition);
                            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                etExamEdit.setText(c.getString(c.getColumnIndex(db.KEY_EXAM)));
                                etDateEdit.setText(c.getString(c.getColumnIndex(db.KEY_DATE)));
                                etGradeEdit.setText(c.getString(c.getColumnIndex(db.KEY_GRADE)));
                                etNoteEdit.setText(c.getString(c.getColumnIndex(db.KEY_NOTE)));
                                resultFromEtGrade=c.getString(c.getColumnIndex(db.KEY_GRADE));
                            }
                        }catch (SQLiteException e){
                            e.printStackTrace();
                        }finally {
                            db.close();
                            c.close();
                        }
                        if(resultFromEtGrade.contentEquals("")||resultFromEtGrade.contentEquals("2")){
                            rbPassed.setChecked(false);
                            rbNotPassed.setChecked(true);
                            etGradeEdit.setEnabled(false);

                        }else {
                            rbNotPassed.setChecked(false);
                            rbPassed.setChecked(true);
                            etGradeEdit.setEnabled(true);
                        }

                        dialogEdit.show();
                        break;

                    case 1:
                        try {
                            db.open();
                            db.deleteFromDatabase(mainPosition);
                            db.close();

                            listItems.clear();
                            db.open();
                            c = db.getAllExams();
                            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                listItems.add(c.getString(c.getColumnIndex(db.KEY_EXAM)));
                                adapter.notifyDataSetChanged();
                            }
                        }catch (SQLiteException e){
                            e.printStackTrace();
                        }finally {
                            db.close();
                            c.close();
                        }
                        dialog.dismiss();
                        break;
                }
            }
        });
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.rbPassed){
            etGradeEdit.setEnabled(true);
        }else {
            etGradeEdit.setEnabled(false);
        }
    }
}

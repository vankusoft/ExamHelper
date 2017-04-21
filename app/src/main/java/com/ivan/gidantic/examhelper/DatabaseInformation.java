package com.ivan.gidantic.examhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IVAN on 29.11.2015 г..
 */
public class DatabaseInformation {

    private static final String DATABASE_TABLE = "table_database";
    private static final String DATABASE_NAME = "exam_helper";
    private static final int DATABASE_VERSION = 19;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_EXAM = "exam_type";
    public static final String KEY_DATE = "exam_date";
    public static final String KEY_GRADE = "exam_grade";
    public static final String KEY_NOTE ="exam_note";

    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;
    private final Context ourContext;

    private static class DbHelper extends SQLiteOpenHelper {

        private DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " ("
                            + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_EXAM + " TEXT NOT NULL, " +
                            KEY_DATE + " TEXT NOT NULL, " +
                            KEY_GRADE + " TEXT NOT NULL, " +
                            KEY_NOTE + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DatabaseInformation(Context context) {
        ourContext = context;
    }

    public DatabaseInformation open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        ourDatabase=ourHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        ourDatabase.close();
    }

    public long addNewExam(String exam,String date,String grade,String note){
        ContentValues cv = new ContentValues();
            cv.put(KEY_EXAM, exam);
            cv.put(KEY_DATE, date);
            cv.put(KEY_GRADE, grade);
            cv.put(KEY_NOTE,note);
            return ourDatabase.insert(DATABASE_TABLE, null, cv);


    }

    public Cursor getAllExams(){
            String[] columns= new String[]{KEY_ROWID,KEY_EXAM,KEY_DATE,KEY_GRADE,KEY_NOTE};
           return ourDatabase.query(DATABASE_TABLE,columns,null,null,null,null,null);
    }

    public Cursor getCurrentExam(int position){
        String[] columns= new String[]{KEY_ROWID,KEY_EXAM,KEY_DATE,KEY_GRADE,KEY_NOTE};
        return ourDatabase.query(DATABASE_TABLE,columns,KEY_ROWID +" = "+position,null,null,null,null);
    }

    public void updateDatabase(String exam,String date,String grade,String note,int position){
        ContentValues cv= new ContentValues();
        cv.put(KEY_EXAM,exam);
        cv.put(KEY_DATE,date);
        cv.put(KEY_GRADE,grade);
        cv.put(KEY_NOTE,note);
        ourDatabase.update(DATABASE_TABLE,cv,KEY_ROWID+" = "+position,null);
    }

    public void deleteFromDatabase(int position){

        ourDatabase.delete(DATABASE_TABLE,KEY_ROWID + " = " + position,null);

    }
}

package org.asu.cse535.group9assignment2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.asu.cse535.group9assignment2.bean.AccelerometerVO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.asu.cse535.group9assignment2.activity.Assignment2Activity.DB_NAME;


/*
credits : https://gist.github.com/zabawaba99/980ea292b305cbdd230a
 */



public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "PATIENT_DB_HELPER";

    private String name;
    private int age;
    private int ID;
    private String sex;
    private String patientTableName;

    public DatabaseHelper(Context context, String patientName, int patientId, int patientAge, String patientSex, String folderName) {

        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + folderName + DB_NAME, null, 1);

        this.name = patientName;
        this.age = patientAge;
        this.ID = patientId;
        this.sex = patientSex;
        patientTableName = this.name + "_" + this.ID + "_" + this.age + "_" + this.sex;
    }

    public void createDBForPatient() {

        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        Log.d("Creating table ", patientTableName);
        Log.d("path ",writableDatabase.getPath());

        String createPatientTableQuery = "CREATE TABLE " +
                patientTableName +
                "( createdAt DATETIME," +
                "x INTEGER, " +
                "y INTEGER, " +
                "z INTEGER )";

        writableDatabase.execSQL(createPatientTableQuery);
        Log.d("Creating table ", patientTableName);
    }

    // run query to create the table
    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addEntryToTable(AccelerometerVO accelerometerBean) {

        Log.d("addEntryToTable  ", accelerometerBean.toString());

        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        contentValues.put("createdAt", dateFormat.format(date)); // insert at current time
        contentValues.put("x", accelerometerBean.getX());
        contentValues.put("y", accelerometerBean.getY());
        contentValues.put("z", accelerometerBean.getZ());

        writableDatabase.insert(patientTableName, null, contentValues);
    }

}

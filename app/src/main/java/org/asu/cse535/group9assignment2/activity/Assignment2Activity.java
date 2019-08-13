package org.asu.cse535.group9assignment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import org.asu.cse535.group9assignment2.R;
import org.asu.cse535.group9assignment2.bean.AccelerometerVO;
import org.asu.cse535.group9assignment2.db.DatabaseHelper;
import org.asu.cse535.group9assignment2.server.DownloadDb;
import org.asu.cse535.group9assignment2.server.UploadDb;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Assignment2Activity extends AppCompatActivity  implements SensorEventListener {

    private static final String TAG = "Assignment2Activity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor sensors;

    private LineChart mChart1;
    private LineChart mChart2;
    private LineChart mChart3;
    private Thread thread;
    private boolean plotData = false;
    private boolean isReadyForNextSet = false;
    private boolean hasDownLoadedDataPloted = false;

    public static final String SAVE_FOLDER_NAME = "CSE535_ASSIGNMENT2/";
    public static final String DOWNLOAD_FOLDER_NAME = "CSE535_ASSIGNMENT2_DOWN/";

    public static final String DB_NAME = "GROUP9_ASSIGNMENT2.db";
    //public static final String SERVER_URI = "http://impact.asu.edu/CSE535Spring19Folder";
    public static final String SERVER_URI = "http://192.168.0.225";

    Button runButton;
    Button stopButton;
    Button submitButton;
    Button uploadButton;
    Button downloadButton;

    private EditText patientName;
    private EditText patientAge;
    private EditText patientID;
    private String patientSex = "M";
    private String name ;
    private int ID ;
    private int age;

    private DatabaseHelper dbHelper;
    private DatabaseHelper dbHelperDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment2);

        patientName = (EditText) findViewById(R.id.patientName);
        patientAge = (EditText) findViewById(R.id.patientAge);
        patientID = (EditText) findViewById(R.id.patientId);


        runButton = (Button) findViewById(R.id.run);
        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mChart1.setVisibility(View.VISIBLE);
                mChart2.setVisibility(View.VISIBLE);
                mChart3.setVisibility(View.VISIBLE);
                plotData = true;
            }
        });

        stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                plotData = false;
                mChart1.setVisibility(View.INVISIBLE);
                mChart2.setVisibility(View.INVISIBLE);
                mChart3.setVisibility(View.INVISIBLE);
            }
        });

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runButton.setEnabled(true);
                stopButton.setEnabled(true);
                uploadButton.setEnabled(true);
                downloadButton.setEnabled(true);

                Context applicationContext = getApplicationContext();

                // Validate name
                if (patientName.getText().length() < 1) {
                    CharSequence toastMessage = "Please enter patient name";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                // Validate ID
                if (patientID.getText().length() < 1) {

                    CharSequence toastMessage = "Please enter patient ID";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                // Validate age
                if (patientAge.getText().length() < 1) {

                    CharSequence toastMessage = "Please enter patient age";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                name = patientName.getText().toString().replaceAll("\\s+", "_");
                age = Integer.parseInt(patientAge.getText().toString());
                ID = Integer.parseInt(patientID.getText().toString());

                String saveFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + SAVE_FOLDER_NAME;

                Log.d(TAG, "SAVE PATH = " + saveFilePath);

                File directory = new File(saveFilePath);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                dbHelper = new DatabaseHelper(getApplicationContext(),
                        name, ID, age, patientSex,
                        SAVE_FOLDER_NAME);
                dbHelper.createDBForPatient();

            }
        });

        uploadButton = (Button) findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    UploadDb uploadDb = new UploadDb(Assignment2Activity.this,SERVER_URI,DB_NAME);
                    String uploadFile = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                            + "/" + SAVE_FOLDER_NAME + DB_NAME;

                    File database = new File(uploadFile);
                    uploadDb.execute(database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        downloadButton = (Button) findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Over-write the existing file if any on the filesystem, to ensure updated values
                String overwriteFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + DOWNLOAD_FOLDER_NAME ;
                File overwriteDirectory = new File(overwriteFilePath);
                if (!overwriteDirectory.exists()) {
                    overwriteDirectory.mkdir();
                }
                DownloadDb overwriteDb = new DownloadDb(DB_NAME, overwriteFilePath, Assignment2Activity.this);
                overwriteDb.execute("");
            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensors.size(); i++) {
            Log.d(TAG, "onCreate: Sensor " + i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart1 = (LineChart) findViewById(R.id.chart1);
        mChart2 = (LineChart) findViewById(R.id.chart2);
        mChart3 = (LineChart) findViewById(R.id.chart3);

        initializeChart(mChart1);
        initializeChart(mChart2);
        initializeChart(mChart3);

        startPlot();
    }

    public void onSexSelected(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    patientSex = "M";
                break;
            case R.id.female:
                if (checked)
                    patientSex = "F";
                break;
        }
    }

    private void addEntry(float val, LineChart mChart, int axis) {

        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet(axis);
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), val + 3), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(150);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet(int axis) {

        LineDataSet set = null;

        switch(axis){
            case 0:
                set = new LineDataSet(null, "Accelerometer X-Axis");
                break;
            case 1:
                set = new LineDataSet(null, "Accelerometer Y-Axis");
                break;
            case 2:
                set =  new LineDataSet(null, "Accelerometer Z-Axis");
                break;
        }

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.GREEN);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void startPlot() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (plotData) {
                                isReadyForNextSet = true;
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void plotDownloadedData(){
        hasDownLoadedDataPloted = true;
        plotData = false;
        mChart1.setVisibility(View.VISIBLE);
        mChart2.setVisibility(View.VISIBLE);
        mChart3.setVisibility(View.VISIBLE);

        String overwriteFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + DOWNLOAD_FOLDER_NAME + DB_NAME;

        SQLiteDatabase readDatabase  = SQLiteDatabase.openDatabase(overwriteFilePath,null,SQLiteDatabase.CREATE_IF_NECESSARY);

        String patientTableName = this.name + "_" + this.ID + "_" + this.age + "_" + this.patientSex;
        String top10Query = "SELECT * FROM " + patientTableName + " ORDER BY datetime(createdAt) DESC LIMIT 10";
        List<AccelerometerVO> accelerometerEntryList = new ArrayList<>();

        Cursor cursor = readDatabase.rawQuery(top10Query, null);

        AccelerometerVO entry = null;
        if (cursor.moveToFirst()) {
            do {
                entry = new AccelerometerVO();
                entry.setTimestamp(Timestamp.valueOf(cursor.getString(0)));
                entry.setX(Float.parseFloat(cursor.getString(1)));
                entry.setY(Float.parseFloat(cursor.getString(2)));
                entry.setZ(Float.parseFloat(cursor.getString(3)));

                // Add to list
                accelerometerEntryList.add(entry);
            } while (cursor.moveToNext());
        }
        Log.d("Getting all entries ", accelerometerEntryList.toString());


        initializeChart(mChart1);
        initializeChart(mChart2);
        initializeChart(mChart3);

        if(accelerometerEntryList!=null){
            for(AccelerometerVO obj : accelerometerEntryList){
                addEntry(obj.getX(), mChart1,0);
                addEntry(obj.getY(), mChart2,1);
                addEntry(obj.getZ(), mChart3,2);
            }
            /*addEntry(0, mChart1);
            addEntry(0, mChart2);
            addEntry(0, mChart3);*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (plotData && isReadyForNextSet) {

            if(hasDownLoadedDataPloted){
                initializeChart(mChart1);
                initializeChart(mChart2);
                initializeChart(mChart3);
                hasDownLoadedDataPloted = false;
            }
            Sensor usedSensor = event.sensor;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            addEntry(x, mChart1,0);
            addEntry(y, mChart2,1);
            addEntry(z, mChart3,2);

            AccelerometerVO accelerometerBean = AccelerometerVO.getInstance(x, y, x);
            dbHelper.addEntryToTable(accelerometerBean);
            isReadyForNextSet = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(Assignment2Activity.this);
        thread.interrupt();
        super.onDestroy();
    }

    // Below function initializes the chart
    public void initializeChart(LineChart myGraph) {
        // enable description text
        myGraph.getDescription().setEnabled(true);
        myGraph.getDescription().setText("Health Monitor");
        // disable touch gestures
        myGraph.setTouchEnabled(false);

        // disable scaling and dragging
        myGraph.setDragEnabled(false);
        myGraph.setScaleEnabled(false);
        myGraph.setDrawGridBackground(false);

        // disable pinch zoom
        myGraph.setPinchZoom(false);

        // backgraound color setting to white
        myGraph.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // adding the empty data
        myGraph.setData(data);
        myGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}

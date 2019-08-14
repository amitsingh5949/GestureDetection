package org.asu.cse535.group9assignment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.asu.cse535.group9assignment2.R;
import org.asu.cse535.group9assignment2.bean.AccelerometerVO;
import org.asu.cse535.group9assignment2.db.GestureWriteHelper;
import org.asu.cse535.group9assignment2.server.RunAlgorithm;
import org.asu.cse535.group9assignment2.server.UploadDb;
import org.asu.cse535.group9assignment2.util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinalActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "FinalActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Thread thread;

    Button clickedButton;
    boolean readData;
    boolean isReadyForNextSet;
    boolean dataSent;
    public int counter;

    TextView truePositiveVal;
    TextView falsePositiveVal;

    public static final DecimalFormat df2 = new DecimalFormat("#.##");

    public static final String SAVE_FOLDER_NAME = "GESTURE_DATA/";
    public static final String DATA_FILE = "data_file.csv";

    public List<Integer> copResult = new ArrayList<>();
    public List<Integer> aboutResult = new ArrayList<>();
    public List<Integer> headacheResult = new ArrayList<>();
    public List<Integer> hungryResult = new ArrayList<>();

    private GestureWriteHelper gestureWriteHelper;
    private boolean append = true;

    List<AccelerometerVO> gestureAccelerometerVOList = new ArrayList<>();

    public static final String SERVER_URI = "http://13.52.177.43:5000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        truePositiveVal = (TextView) findViewById(R.id.truePositiveValue);
        falsePositiveVal = (TextView) findViewById(R.id.falsePositiveValue);

        createWriter();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < sensors.size(); i++) {
            Log.d(TAG, "onCreate: Sensor " + i + ": " + sensors.get(i).toString());
        }
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        Button inputCopButton = (Button) findViewById(R.id.inputCop);
        inputCopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                clickedButton = b;
                String buttonText = b.getText().toString();
                if (buttonText.equals("STOP")) {
                    ((Button) v).setText("Input Cop Gesture");
                    readData = false;
                    writeExperimentData();
                    gestureAccelerometerVOList.clear();

                } else {
                    ((Button) v).setText("STOP");
                    readData = true;
                    counter++;
                    startIngestingData();
                }

            }
        });

        Button inputHungryButton = (Button) findViewById(R.id.inputHungry);
        inputHungryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                clickedButton = b;
                String buttonText = b.getText().toString();
                if (buttonText.equals("STOP")) {
                    ((Button) v).setText("Input Hungry Gesture");
                    readData = false;
                    writeExperimentData();
                    gestureAccelerometerVOList.clear();

                } else {
                    ((Button) v).setText("STOP");
                    readData = true;
                    counter++;
                    startIngestingData();
                }

            }
        });

        Button inputHeadacheButton = (Button) findViewById(R.id.inputHeadache);
        inputHeadacheButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                clickedButton = b;
                String buttonText = b.getText().toString();
                if (buttonText.equals("STOP")) {
                    ((Button) v).setText("Input Headache Gesture");
                    readData = false;
                    writeExperimentData();
                    gestureAccelerometerVOList.clear();

                } else {
                    ((Button) v).setText("STOP");
                    readData = true;
                    counter++;
                    startIngestingData();
                }

            }
        });

        Button inputAboutButton = (Button) findViewById(R.id.inputAbout);
        inputAboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                clickedButton = b;
                String buttonText = b.getText().toString();
                if (buttonText.equals("STOP")) {
                    ((Button) v).setText("Input About Gesture");
                    readData = false;
                    writeExperimentData();
                    gestureAccelerometerVOList.clear();

                } else {
                    ((Button) v).setText("STOP");
                    readData = true;
                    counter++;
                    startIngestingData();
                }

            }
        });

        Button runCopButton = (Button) findViewById(R.id.runCop);
        runCopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performRunButtonAction(v);
            }
        });

        Button runHeadacheButton = (Button) findViewById(R.id.runHeadAche);
        runHeadacheButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performRunButtonAction(v);
            }
        });

        Button runHungryButton = (Button) findViewById(R.id.runHungry);
        runHungryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performRunButtonAction(v);
            }
        });

        Button runAboutButton = (Button) findViewById(R.id.runAbout);
        runAboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performRunButtonAction(v);
            }
        });


        Button resetButton = (Button) findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button) v;
                clickedButton = b;
                readData = false;
                isReadyForNextSet = false;
                dataSent = false;
                counter = 0;
                truePositiveVal.setText("0%");
                falsePositiveVal.setText("0%");
                copResult = new ArrayList<>();
                aboutResult = new ArrayList<>();
                headacheResult = new ArrayList<>();
                hungryResult = new ArrayList<>();
                gestureAccelerometerVOList = new ArrayList<>();
                reCreateDataFile();
            }
        });

        Button resetValButton = (Button) findViewById(R.id.resetValue);
        resetValButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                truePositiveVal.setText("0%");
                falsePositiveVal.setText("0%");
            }
        });
    }

    public void performRunButtonAction(View v) {
        Button b = (Button) v;
        clickedButton = b;
        if (!dataSent) {
            dataSent = true;
            uploadGestureDataFiles();
        } else {
            showResult();
        }
    }

    private void startIngestingData() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (readData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (readData) {
                                isReadyForNextSet = true;
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
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
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (readData && isReadyForNextSet) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            AccelerometerVO accelerometerBean = AccelerometerVO.getInstance(x, y, z);
            Log.i(TAG, accelerometerBean.toString());
            gestureAccelerometerVOList.add(accelerometerBean);
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
        mSensorManager.unregisterListener(FinalActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

    public String getFilePath(String fileName) {

        String csvFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + SAVE_FOLDER_NAME + DATA_FILE;

        if (gestureWriteHelper != null) {
            gestureWriteHelper.closeGestureDataWriter();
            gestureWriteHelper = null;
        }

        File csvFile = new File(csvFilePath);
        if (csvFile.exists()) {
            csvFile.delete();
        }

        String saveFileDirectory = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + SAVE_FOLDER_NAME;

        Log.d(TAG, "SAVE PATH = " + saveFileDirectory);

        File directory = new File(saveFileDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }


        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFilePath;
    }

    public void reCreateDataFile() {
        String csvFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + SAVE_FOLDER_NAME + DATA_FILE;
        if (gestureWriteHelper != null) {
            gestureWriteHelper.closeGestureDataWriter();
            gestureWriteHelper = null;
        }
        File csvFile = new File(csvFilePath);
        if (csvFile.exists()) {
            csvFile.delete();
        }
        createWriter();
    }

    public void createWriter() {
        if (gestureWriteHelper == null) {
            gestureWriteHelper = new GestureWriteHelper(getFilePath(DATA_FILE), append);
            gestureWriteHelper.createGestureDataWriter();
            gestureWriteHelper.writeMetaData();
        }
    }

    public void writeExperimentData() {
        gestureWriteHelper.writeGestureDataBulk(gestureAccelerometerVOList, counter);
    }

    public void uploadGestureDataFiles() {
        try {
            UploadDb uploadDb = new UploadDb(FinalActivity.this, SERVER_URI, DATA_FILE);
            String uploadFile = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                    + "/" + SAVE_FOLDER_NAME + "/" + DATA_FILE;

            File f = new File(uploadFile);
            uploadDb.execute(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAlgorithm() {
        new RunAlgorithm(new RunAlgorithm.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.i(TAG, output);
                if (output != null && output.length() > 0 && output.contains("_")) {
                    String[] arr = output.split("_");
                    for (int i = 0; i < 4 && i < arr.length; i++) {
                        hungryResult.add(Integer.parseInt(arr[i]));
                    }
                    for (int i = 4; i < 8 && i < arr.length; i++) {
                        copResult.add(Integer.parseInt(arr[i]));
                    }
                    for (int i = 8; i < 12 && i < arr.length; i++) {
                        headacheResult.add(Integer.parseInt(arr[i]));
                    }
                    for (int i = 12; i < 16 && i < arr.length; i++) {
                        aboutResult.add(Integer.parseInt(arr[i]));
                    }
                }
                showResult();
            }
        }).execute(SERVER_URI + "/predict");
    }

    public void showResult() {
        int id = clickedButton.getId();
        List<Integer> l = new ArrayList<>();
        String truePositive;
        String falsePositive;
        switch (id) {
            case R.id.runAbout:
                l.clear();
                l.addAll(copResult);
                l.addAll(hungryResult);
                l.addAll(headacheResult);
                truePositive = getPercentage(aboutResult, Constants.ABOUT_GESTURE);
                falsePositive = getPercentage(l, Constants.ABOUT_GESTURE);
                truePositiveVal.setText(truePositive);
                falsePositiveVal.setText(falsePositive);
                break;
            case R.id.runCop:
                l.clear();
                l.addAll(aboutResult);
                l.addAll(hungryResult);
                l.addAll(headacheResult);
                truePositive = getPercentage(copResult, Constants.COP_GESTURE);
                falsePositive = getPercentage(l, Constants.COP_GESTURE);
                truePositiveVal.setText(truePositive);
                falsePositiveVal.setText(falsePositive);
                break;
            case R.id.runHeadAche:
                l.clear();
                l.addAll(copResult);
                l.addAll(hungryResult);
                l.addAll(aboutResult);
                truePositive = getPercentage(headacheResult, Constants.HEADACHE_GESTURE);
                falsePositive = getPercentage(l, Constants.HEADACHE_GESTURE);
                truePositiveVal.setText(truePositive);
                falsePositiveVal.setText(falsePositive);
                break;
            case R.id.runHungry:
                l.clear();
                l.addAll(copResult);
                l.addAll(headacheResult);
                l.addAll(aboutResult);
                truePositive = getPercentage(hungryResult, Constants.HUNGRY_GESTURE);
                falsePositive = getPercentage(l, Constants.HUNGRY_GESTURE);
                truePositiveVal.setText(truePositive);
                falsePositiveVal.setText(falsePositive);
                break;
        }
    }

    public String getPercentage(List<Integer> l, int id) {
        double d = (Collections.frequency(l, id) * 100.0) / l.size();
        return df2.format(d) + "%";
    }
}

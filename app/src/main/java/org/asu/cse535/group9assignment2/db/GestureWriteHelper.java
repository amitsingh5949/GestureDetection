package org.asu.cse535.group9assignment2.db;

import android.util.Log;

import com.opencsv.CSVWriter;

import org.asu.cse535.group9assignment2.bean.AccelerometerVO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestureWriteHelper {
    private static final String TAG = "GestureWriteHelper";
    private String filePath;
    private File f = null;
    private CSVWriter writer;
    private FileWriter fileWriter;
    private boolean append;

    public GestureWriteHelper(String filePath, boolean append) {
        this.filePath = filePath;
        this.append = append;
    }

    public void createGestureDataWriter(){
        f = new File(filePath);
        try {
            if (f.exists() && f.isFile()) {
                fileWriter = new FileWriter(filePath, append);
                writer = new CSVWriter(fileWriter);
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
            }
        } catch (IOException e) {
            Log.i(TAG,"Unable open file writer");
            e.printStackTrace();
        }
    }

    public void writeGestureData(AccelerometerVO accelerometerVO, int ID){
        writer.writeNext(new String[]{Integer.valueOf(ID).toString(),
                accelerometerVO.getTimestamp().toString(),
                Double.valueOf(accelerometerVO.getX()).toString(),
                Double.valueOf(accelerometerVO.getY()).toString(),
                Double.valueOf(accelerometerVO.getZ()).toString()
        });
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"Unable to flush data file writer");
        }
    }

    public void writeGestureDataBulk(List<AccelerometerVO> accelerometerVOList, int ID){

        List<String[]> data = new ArrayList<String[]>();

        for(AccelerometerVO accelerometerVO : accelerometerVOList) {
            data.add(new String[]{Integer.valueOf(ID).toString(),
                    accelerometerVO.getTimestamp().toString(),
                    Double.valueOf(accelerometerVO.getX()).toString(),
                    Double.valueOf(accelerometerVO.getY()).toString(),
                    Double.valueOf(accelerometerVO.getZ()).toString(),
            });
        }
        writer.writeAll(data);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"Unable to flush data file writer");
        }
    }

    public void writeMetaData() {
        writer.writeNext(new String[]{"ID", "time_stamp", "x", "y", "z"});
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"Unable to flush data file writer");
        }
    }

    public void closeGestureDataWriter(){
        if(getFileWriter()!=null){
            try {
                getFileWriter().close();
            } catch (IOException e) {
                Log.i(TAG,"Unable to close file writer");
                e.printStackTrace();
            }
        }
        if(getWriter()!=null){
            try {
                getWriter().close();
            } catch (IOException e) {
                Log.i(TAG,"Unable to close csv writer");
                e.printStackTrace();
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getF() {
        return f;
    }

    public void setF(File f) {
        this.f = f;
    }

    public CSVWriter getWriter() {
        return writer;
    }

    public void setWriter(CSVWriter writer) {
        this.writer = writer;
    }

    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }
}

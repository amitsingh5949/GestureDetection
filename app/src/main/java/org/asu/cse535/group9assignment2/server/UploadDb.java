package org.asu.cse535.group9assignment2.server;
/**
 * CREDIT
 * Code referred from:
 * https://stackoverflow.com/questions/25398200/uploading-file-in-php-server-from-android-device/25398449
 * http://findnerd.com/list/view/How-to-download-file-from-server-in-android/6701/
 * http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.asu.cse535.group9assignment2.activity.Assignment2Activity;
import org.asu.cse535.group9assignment2.activity.FinalActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UploadDb extends AsyncTask<File, Void, Void> {

    public static  String UPLOAD_URI;
    public static  String DB_NAME;

    Activity mActivity;
    private boolean uploaded = true;

    public UploadDb(Activity mainActivity, String uploadRRI, String dbName) {
        this.mActivity = mainActivity;
        this.UPLOAD_URI = uploadRRI;
        this.DB_NAME= dbName;
    }


    @Override
    protected Void doInBackground(File... params) {
        upload(params[0]);
        return null;
    }

    private int upload(File sourceFileUri) {

        ProgressDialog progressDialog = null;
        int serverResponseCode = 0;

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            uploaded = false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            uploaded = false;
        }

        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1048576;
        File sourceFile = new File(String.valueOf(sourceFileUri));
        if (!sourceFile.isFile()) {
            progressDialog.dismiss();
            Log.e("UploadFile", "Source File not exist :");
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URI);

                // Open a HTTP  connection to  the URL
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + DB_NAME + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necessary after file data...
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = httpURLConnection.getResponseCode();
                String serverResponseMessage = httpURLConnection.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                //close the streams //
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                uploaded = false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
                uploaded = false;
            }


        }
        return serverResponseCode;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mActivity instanceof Assignment2Activity) {
            if (uploaded) {
                Toast.makeText(mActivity.getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "Failed to upload file", Toast.LENGTH_SHORT).show();
            }
        }
        else if(mActivity instanceof FinalActivity){
            ((FinalActivity) mActivity).runAlgorithm();
        }
    }

}
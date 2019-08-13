package org.asu.cse535.group9assignment2.server;


/**
 * CREDIT
 * Code referred from:
 * https://stackoverflow.com/questions/25398200/uploading-file-in-php-server-from-android-device/25398449
 * http://findnerd.com/list/view/How-to-download-file-from-server-in-android/6701/
 * http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83
 */


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import org.asu.cse535.group9assignment2.activity.Assignment2Activity;
import org.asu.cse535.group9assignment2.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.asu.cse535.group9assignment2.activity.Assignment2Activity.SERVER_URI;
import static org.asu.cse535.group9assignment2.activity.Assignment2Activity.DB_NAME;


public class DownloadDb extends AsyncTask<String, Void, Boolean> {

    public static final String TAG = "DOWNLOAD_DB";

    Assignment2Activity mActivity;
    private boolean downloaded = true;

    public static final String DOWNLOAD_URI = SERVER_URI + "/uploads/" + DB_NAME;

    String downloadFilePath;
    String downloadFileName;

    public DownloadDb(String fileName, String filePath, Assignment2Activity mActivity) {
        downloadFileName = fileName;
        downloadFilePath = filePath;
        this.mActivity = mActivity;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        download(downloadFileName, downloadFilePath);
        return true;
    }

    private void download(String downloadFileName, String downloadFilePath) {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            downloaded = false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            downloaded = false;
        }

        try {
            URL url = new URL(DOWNLOAD_URI);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(downloadFilePath, downloadFileName));

            try {
                byte[] buffer = new byte[1024];
                int bufferLength;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    Log.i(TAG, Integer.valueOf(bufferLength).toString());
                    outputStream.write(buffer, 0, bufferLength);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                outputStream.close();
                downloaded = false;
            }

        } catch (final MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURLException", "error: " + e.getMessage(), e);
            downloaded = false;
        } catch (final IOException e) {
            e.printStackTrace();
            Log.e("IOException", "error: " + e.getMessage(), e);
            downloaded = false;
        } catch (final Exception e) {
            Log.e(e.getMessage(), String.valueOf(e));
            downloaded = false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (downloaded) {
            mActivity.plotDownloadedData();
            Toast.makeText(mActivity.getApplicationContext(), "File Downloaded", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity.getApplicationContext(), "Unable to Download File", Toast.LENGTH_LONG).show();
        }
    }
}
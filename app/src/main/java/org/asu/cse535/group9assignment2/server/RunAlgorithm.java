package org.asu.cse535.group9assignment2.server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RunAlgorithm extends AsyncTask<String, Void, String> {

    String server_response;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public RunAlgorithm(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... strings) {

        String responseCode = "";

        int serverResponseCode = 0;

        HttpURLConnection httpURLConnection = null;

        try {
            // open a URL connection to the Servlet
            URL url = new URL(strings[0]);

            // Open a HTTP  connection to  the URL
            httpURLConnection = (HttpURLConnection) url.openConnection();


            serverResponseCode = httpURLConnection.getResponseCode();

            if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(httpURLConnection.getInputStream());
                Log.v("CatalogClient", server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(server_response);
    }
}

package net.ornew.mnist.app;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download extends AsyncTask<URL, Integer, Boolean> {
    private Context context;
    private String pathTo;

    Download(Context context, String pathTo) {
        this.context = context;
        this.pathTo = pathTo;
    }

    @Override
    protected Boolean doInBackground(URL... urls) {
        final byte[] buffer = new byte[4096];
        HttpURLConnection connection = null;
        InputStream input = null;
        OutputStream output = null;
        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            connection.connect();

            int length = connection.getContentLength();

            input = connection.getInputStream();
            output = this.context.openFileOutput(pathTo, Context.MODE_PRIVATE);

            int totalBytes = 0;
            int bytes = 0;
            while ((bytes = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytes);
                totalBytes += bytes;
                publishProgress((int)(totalBytes * 100.f / length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(input != null){
                    input.close();
                }
                if(output != null){
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
    }
}

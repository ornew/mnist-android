package net.ornew.mnistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String FILENAME = "mnist_android_model";
    String TAG = "MNIST_ANDROID";

    Button recognize;
    Button clear;
    TextView result;
    CanvasView canvas;
    ProgressDialog downloading;

    private void print(String text){
        result.setText(result.getText() + text + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = (CanvasView) findViewById(R.id.canvas);
        recognize = (Button) findViewById(R.id.recognize);
        result = (TextView) findViewById(R.id.result);
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pixels[] = canvas.getPixels();
                float data[] = new float[pixels.length];
                for(int i = 0; i < pixels.length; ++i){
                    data[i] = Color.alpha(pixels[i]) / 255.f;
                }
                float answers[] = JNI.inference(data);

                result.setText("");
                int bestIndex = 0;
                float bestScore = answers[0];
                for(int i = 1; i < answers.length; ++i) {
                    if(bestScore < answers[i]){
                        bestScore = answers[i];
                        bestIndex = i;
                    }
                }
                print("予測: " + bestIndex);
                for(int i = 0; i < answers.length; ++i) {
                    print(i + ": " + answers[i]);
                }
            }
        });
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.clear();
            }
        });

        String[] files = this.getApplicationContext().fileList();
        for(String i:files){
            Log.d(TAG,i);
        }
        if (Arrays.asList(files).contains(FILENAME)) {
            Log.d(TAG, "Found model data.");
            initializeModel();
        } else {
            try {
                downloading = new ProgressDialog(this);
                downloading.setTitle("LOADING");
                downloading.setMessage("モデルをダウンロード中です");
                downloading.setMax(1);
                downloading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //downloading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                URL url = new URL("https://github.com/ornew/MNIST-for-Android/raw/master/model/frozen_mnist.pb");
                new Download(this.getApplicationContext(), FILENAME) {
                    @Override
                    protected void onPostExecute(Boolean success) {
                        super.onPostExecute(success);
                        Log.d(TAG, "Download model data.");
                        initializeModel();
                    }
                }.execute(url);
                downloading.show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    void initializeModel(){
        String dir = this.getApplicationContext().getFilesDir().getAbsolutePath();
        Log.d("FilesDir: ", dir);
        System.loadLibrary("app");
        JNI.initialize(dir + "/" + FILENAME);

        recognize.setEnabled(true);
    }

    public class Download extends AsyncTask<URL, Void, Boolean> {
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
                    downloading.incrementProgressBy((int)(totalBytes * 100.f / length));
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
            downloading.dismiss();
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
        }
    }
}

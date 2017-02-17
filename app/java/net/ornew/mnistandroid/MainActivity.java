package net.ornew.mnist.app;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.ornew.mnist.MNIST;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MNIST";
    static final String MODEL_URL = "https://github.com/ornew/mnist-android/releases/download/v1.0.0/frozen_mnist.pb";
    static final String FILENAME = "mnist.frozen.pb";

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
                float answers[] = MNIST.inference(data);

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

        String[] files = getApplicationContext().fileList();
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
                downloading.setMax(100);
                downloading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                URL url = new URL(MODEL_URL);
                new Download(getApplicationContext(), FILENAME) {
                    @Override
                    protected void onProgressUpdate(Integer... progress) {
                        super.onProgressUpdate(progress);
                        downloading.setProgress(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(Boolean success) {
                        super.onPostExecute(success);
                        Log.d(TAG, "Download model data.");
                        downloading.dismiss();
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
        MNIST.initialize(dir + "/" + FILENAME);

        recognize.setEnabled(true);
    }
}

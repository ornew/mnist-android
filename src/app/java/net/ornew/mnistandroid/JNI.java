package net.ornew.mnistandroid;

/**
 * Created by ornew on 2017/02/04.
 */

public class JNI {
    static {
        System.loadLibrary("app");
    }
    public static native void initialize(String model_path);
    public static native float[] inference(float x[]);
}

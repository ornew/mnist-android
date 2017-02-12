package net.ornew.mnistandroid;

/**
 * Created by ornew on 2017/02/04.
 */

public class JNI {
    static {
        System.loadLibrary("mnist_android");
    }
    public static native void create(String j_model_path);
    public static native float[] ask(float x[]);
}

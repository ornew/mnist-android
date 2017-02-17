package net.ornew.mnist;

/**
 * Created by ornew on 2017/02/04.
 */

public class MNIST {
    static {
        System.loadLibrary("mnist");
    }
    public static native void initialize(String model_path);
    public static native float[] inference(float x[]);
}

package tuev.konstantin.a9gagopfinder;


import android.app.Activity;

public class fuckyou {
        static {
            System.loadLibrary("fuckyou");
        }

    public static native void systemcall(String op);
    public static native boolean cell(String string);
    public static native String cr(String string);
    public static native void modcall(String op, Activity activity);
}

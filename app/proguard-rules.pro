# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kosio/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class tuev.konstantin.a9gagopfinder.RootTools {
public static tuev.konstantin.a9gagopfinder.execution.Shell getShell(boolean); 
}
-keep class tuev.konstantin.a9gagopfinder.execution.Shell {
public tuev.konstantin.a9gagopfinder.execution.Command add(tuev.konstantin.a9gagopfinder.execution.Command); 
}
-ignorewarnings
-keep class tuev.konstantin.a9gagopfinder.execution.Command { *; }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
-keep,allowshrinking class com.google.** { *;}
package tuev.konstantin.a9gagopfinder;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.WatchRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressWarnings("ResourceType")
public class MainActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    EditText tv;
    Button b;
    String op = null;
    SharedPreferences sp;
    private String modded = "no";
    Button mod;
    TextView info;
    private BroadcastReceiver br;
    GoogleAccountCredential mCredential;
    Button realOP;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_READONLY};

    public final static String HELLO = "Hi decompiler and of course fuck you because I use native classes and if you are 9GAG employee then fuck you twice.";
    static final int REQ = 1323;
    Button checkGmail;
    ProgressBar pb;
    private boolean share = false;
    private Bundle meta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button) findViewById(R.id.button);
        realOP = (Button) findViewById(R.id.button4);
        mod = (Button) findViewById(R.id.button2);
        pb = (ProgressBar) findViewById(R.id.progressBar4);
        checkGmail = (Button) findViewById(R.id.button3);
        info = (TextView) findViewById(R.id.textView);
        tv = (EditText) findViewById(R.id.editText);
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Toast.makeText(this, HELLO, Toast.LENGTH_SHORT);
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo("com.ninegag.modded.app", PackageManager.GET_META_DATA);
            meta = ai.metaData;
            modded = meta.getString("modded");
        } catch (PackageManager.NameNotFoundException | NullPointerException ignored) {
        }
        if (is9GAGInstalled() || isMod9GAGInstalled()) {
            if (((modded != null && !modded.equalsIgnoreCase("yes")) || modded == null)) {
                final AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Checking root...")
                        .setMessage("We are checking your root access availability and do the needed actions after it is confirmed that you have or you don't have root.")
                        .setCancelable(false)
                        .create();
                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!RootTools.isAccessGiven()) {
                                    ad.cancel();
                                    warning();
                                } else {
                                    ad.cancel();
                                    enable();
                                }
                            }
                        }, 100);
                    }
                });
                ad.show();
            } else {
                enable();
                if (meta.getInt("ModVersion", 0) < 3) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Update?")
                            .setMessage("Update modded 9gag to newest version?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isStoragePermissionGranted()) {
                                        installMod();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            checkGmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getResultsFromApi();
                    disable();
                }
            });

            info.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String id = "kgneFxDqVw8";
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + id));
                    try {
                        startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                    return true;
                }
            });

            mod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((modded != null && !modded.equalsIgnoreCase("yes") || (modded == null))) {
                        if (is9GAGInstalled()) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Attention!!!")
                                    .setMessage("Do you want to reinstall modded 9GAG(keep original if any)?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isStoragePermissionGranted()) {
                                                installMod();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        } else {
                            if (isStoragePermissionGranted()) {
                                installMod();
                            }
                        }
                    } else if (modded != null && modded.equalsIgnoreCase("yes")) {
                        if (isStoragePermissionGranted()) {
                            installMod();
                        }
                    }
                }
            });

            final Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    share = true;
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    final LinearLayout ly = new LinearLayout(MainActivity.this);
                    ly.setOrientation(LinearLayout.VERTICAL);
                    ly.setGravity(Gravity.CENTER_HORIZONTAL);
                    final RadioGroup rg = new RadioGroup(MainActivity.this);
                    rg.setGravity(Gravity.CENTER_HORIZONTAL);
                    RadioButton op = new RadioButton(MainActivity.this);
                    op.setText("9GAG OP");
                    op.setId(0);
                    op.setChecked(true);
                    rg.addView(op);
                    RadioButton rop = new RadioButton(MainActivity.this);
                    rop.setText("REAL OP");
                    rop.setId(1);
                    rg.addView(rop);
                    ly.addView(rg);
                    final TextView vt = new TextView(MainActivity.this);
                    final String austart = "Auto select after: %s seconds";
                    vt.setText(String.format(Locale.ENGLISH, austart, String.valueOf(10)));
                    vt.setTextColor(Color.parseColor("#e53935"));
                    vt.setTextSize(18);
                    vt.setGravity(Gravity.CENTER_HORIZONTAL);
                    ly.addView(vt);
                    final CountDownTimer[] cd = new CountDownTimer[1];
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("FIND:")
                                    .setView(ly)
                                    .setCancelable(true)
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            int checkedId = rg.getCheckedRadioButtonId();
                                            cd[0].cancel();
                                            if (checkedId == 0) {
                                                handleSendText(intent);
                                            } else if (checkedId == 1) {
                                                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                                                if (sharedText != null) {
                                                    proccess9GAG(sharedText);
                                                }
                                            }
                                        }
                                    })
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int checkedId = rg.getCheckedRadioButtonId();
                                            cd[0].cancel();
                                            if (checkedId == 0) {
                                                handleSendText(intent);
                                            } else if (checkedId == 1) {
                                                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                                                if (sharedText != null) {
                                                    proccess9GAG(sharedText);
                                                }
                                            }
                                        }
                                    }).show();
                            cd[0] = new CountDownTimer(10000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    vt.setText(String.format(Locale.ENGLISH, austart, String.valueOf(millisUntilFinished / 1000)));
                                    //here you can have your logic to set text to edittext
                                }

                                public void onFinish() {
                                    ad.dismiss();
                                    int checkedId = rg.getCheckedRadioButtonId();
                                    if (checkedId == 0) {
                                        handleSendText(intent);
                                    } else if (checkedId == 1) {
                                        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                                        if (sharedText != null) {
                                            proccess9GAG(sharedText);
                                        }
                                    }
                                }

                            }.start();
                        }
                    }, 200);
                }
            } else if ("tuev.konstantin.a9gagopfinder.FindTheOP".equals(action)) {
                share = true;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                if (!intent.getExtras().getString("post", "no").equals("no")) {
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String text = "https://9gag.com/gag/" + intent.getExtras().getString("post", "no");
                            if ((modded != null && modded.equalsIgnoreCase("yes"))) {
                                proccess(text);
                            } else if ((RootTools.isAccessGiven())) {
                                proccess(text);
                            }
                        }
                    }, 200);
                }
            } else if ("tuev.konstantin.a9gagopfinder.GMAIL".equals(action)) {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkGmail.callOnClick();
                    }
                }, 200);
            } else if ("tuev.konstantin.a9gagopfinder.CLIP".equals(action)) {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = clipBoard.getPrimaryClip();
                            ClipData.Item item = clipData.getItemAt(0);
                            String text = item.getText().toString();
                            if ((modded != null && modded.equalsIgnoreCase("yes"))) {
                                proccess(text);
                            } else if ((RootTools.isAccessGiven())) {
                                proccess(text);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 200);
            } else if ("tuev.konstantin.a9gagopfinder.RealOP".equals(action)) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                if (!intent.getExtras().getString("post", "no").equals("no")) {
                    String url = "https://tineye.com/search/?pluginver=chrome-1.1.5&sort=crawl_date&order=asc&url=" + "https://img-9gag-fun.9cache.com/photo/" + intent.getExtras().getString("post", "no") + "_700b.jpg";
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(Color.parseColor("#4CAF50"));
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 900);
                }
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String tvS = tv.getText().toString();
                        if (tvS.contains("gag/")) {
                            int g = tvS.indexOf("gag/");
                            int end;
                            if (tvS.contains("?ref")) {
                                end = tvS.indexOf("?ref");
                            } else {
                                end = tvS.length();
                            }
                            new DownloadWebPageTask().execute("https://9gag.com/gag/" + tvS.substring(g + 4, end));
                        } else {
                            Toast.makeText(MainActivity.this, "Enter valid link!!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, "Enter valid link!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            realOP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tvS = tv.getText().toString();
                    proccess9GAG(tvS);
                }
            });
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Install 9GAG?")
                    .setMessage("You don't have 9GAG installed so you can install the modded one because that way you won't need root access or install the original 9GAG app.")
                    .setPositiveButton("Install modded", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isStoragePermissionGranted()) {
                                installMod();
                            }
                        }
                    })
                    .setNegativeButton("Install original(Play Store)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = "com.ninegag.android.app";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            finish();
                        }
                    })
                    .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }
    public void enable(){
        b.setEnabled(true);
        mod.setEnabled(true);
        tv.setEnabled(true);
        checkGmail.setEnabled(true);
        realOP.setEnabled(true);
        pb.setVisibility(View.INVISIBLE);
    }
    public void disable() {
        b.setEnabled(false);
        mod.setEnabled(false);
        tv.setEnabled(false);
        checkGmail.setEnabled(false);
        realOP.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
    }

    private void warning() {
        b.setVisibility(View.GONE);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Attention!!!")
                .setMessage("You need root access or install modded 9GAG.")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Delete original,\n install modded", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isStoragePermissionGranted()) {
                            installMod();
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).show();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if ((modded != null && modded.equalsIgnoreCase("yes"))) {
                proccess(sharedText);
            } else if ((RootTools.isAccessGiven())) {
                proccess(sharedText);
            }
        }
    }

    private void proccess(String sharedText) {

        try {
            if (sharedText.contains("gag/")) {
                int g = sharedText.indexOf("gag/");
                int end;
                if (sharedText.contains("?ref")) {
                    end = sharedText.indexOf("?ref");
                } else {
                    end = sharedText.length();
                }
                final String res = "https://9gag.com/gag/" + sharedText.substring(g + 4, end);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(res);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new DownloadWebPageTask().execute(res);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Enter valid link!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Link is not valid!!!", Toast.LENGTH_SHORT).show();
                }
            });
            ex.printStackTrace();
        }
    }

    private void proccess9GAG(String sharedText) {
        try {
            if (sharedText.contains("gag/")) {
                int g = sharedText.indexOf("gag/");
                int end;
                if (sharedText.contains("?ref")) {
                    end = sharedText.indexOf("?ref");
                } else {
                    end = sharedText.length();
                }
                String url = "https://tineye.com/search/?pluginver=chrome-1.1.5&sort=crawl_date&order=asc&url="+"https://img-9gag-fun.9cache.com/photo/"+ sharedText.substring(g + 4, end)+"_700b.jpg";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(Color.parseColor("#4CAF50"));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Enter valid link!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Link is not valid!!!", Toast.LENGTH_SHORT).show();
                }
            });
            ex.printStackTrace();
        }
        if (share) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this,
                        "This app's gmail functionality requires Google Play Services. Please install " +
                                "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
            } else {
                getResultsFromApi();
            }

        } else if (requestCode == REQUEST_ACCOUNT_PICKER) {
            if (resultCode == RESULT_OK && data != null &&
                    data.getExtras() != null) {
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    SharedPreferences settings =
                            getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    mCredential.setSelectedAccountName(accountName);
                    disable();
                    getResultsFromApi();
                }
            } else {
                enable();
            }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                getResultsFromApi();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(REQ)
    public void installMod() {
        final AssetManager assetManager = getAssets();

        final InputStream[] in = new InputStream[1];
        final OutputStream[] out = new OutputStream[1];

        new AsyncTask<Void, Void, Void>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(MainActivity.this, "Loading...", "", true, false);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri URI = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory() + "/9gag.apk"));
                    intent.setDataAndType(URI,
                            "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, URI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(intent);
                } catch (Exception ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/9gag.apk")),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                    Toolbar t = new Toolbar(MainActivity.this);
                    t.getTitle();
                }
                br = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        b.setVisibility(View.VISIBLE);
                        String pack = intent.getData().toString();
                        if (pack.equalsIgnoreCase("package:com.ninegag.modded.app")) {
                            Toast.makeText(MainActivity.this, "9GAG modded app installed/updated.", Toast.LENGTH_LONG).show();
                            enable();
                            File ap9g = new File(Environment.getExternalStorageDirectory() + "/9gag.apk");
                            if (ap9g.exists()) {
                                ap9g.delete();
                            }
                            modded = "yes";
                        }
                        if (br != null) {
                            MainActivity.this.unregisterReceiver(br);
                        }
                    }
                };
                IntentFilter iF = new IntentFilter();
                iF.addAction(Intent.ACTION_PACKAGE_ADDED);
                iF.addAction(Intent.ACTION_PACKAGE_CHANGED);
                iF.addDataScheme("package");
                iF.setPriority(1000);
                registerReceiver(br, iF);
                pd.cancel();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    in[0] = assetManager.open("9gag.apk");
                    String filename = "9gag.apk";
                    new File(Environment.getExternalStorageDirectory()+"/"+filename).delete();
                    new File(Environment.getExternalStorageDirectory()+"/"+filename).createNewFile();
                    out[0] = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/"+filename));

                    byte[] buffer = new byte[1024];

                    int read;
                    while ((read = in[0].read(buffer)) != -1) {

                        out[0].write(buffer, 0, read);

                    }

                    in[0].close();

                    out[0].flush();
                    out[0].close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        if (br != null) {
            try {
                File ap9g = new File(Environment.getExternalStorageDirectory() + "/9gag.apk");
                if (ap9g.exists()) {
                    ap9g.delete();
                }
                MainActivity.this.unregisterReceiver(br);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private boolean is9GAGInstalled() {
        try {
            getPackageManager().getPackageInfo("com.ninegag.android.app", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isMod9GAGInstalled() {
        try {
            getPackageManager().getPackageInfo("com.ninegag.modded.app", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_PERMISSION_GET_ACCOUNTS) {
            disable();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_PERMISSION_GET_ACCOUNTS) {
            enable();
        }
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                // Read all the text returned by the server
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    if (fuckyou.cell(str)) {
                        response = str;
                        return response;
                    }
                }
                in.close();
            } catch (IOException ignored) {
            }
            return response;
        }

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this, "Finding op...", "", true, false);
        }

        @Override
        protected void onPostExecute(String result) {
            op = fuckyou.cr(result);
            if (op.isEmpty()) {
                op = null;
            }
            if (op != null && !op.equals("0")) {
                if ((modded != null && modded.equalsIgnoreCase("yes"))) {
                    fuckyou.modcall(op, MainActivity.this);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            if (share) {
                                finish();
                            }
                        }
                    }, 1000);
                } else if ((RootTools.isAccessGiven())) {
                    fuckyou.systemcall(op);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            if (share) {
                                finish();
                            }
                        }
                    }, 3000);
                }
            } else {
                pd.cancel();
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("OP can't be found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                if (op == null) {
                    dialog.setMessage("An error occurred.\nMaybe you have no internet or the link is incorrect.\nTry again later.");
                } else if (op.equals("0")) {
                    dialog.setMessage("This post has no OP.\nMy theory is that posts without OP are posted from some kind of bot or 9gag employees.");
                }
                dialog.show();
            }
        }

    }
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
            disable();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
            enable();
        } else {
            //new MakeRequestTask(mCredential).execute();
            new GetMessages(mCredential).execute();
        }
    }
    private class GetMessages extends AsyncTask<Void, Void, List<Message>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        GetMessages(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("9GAG OP Finder")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<Message> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        private List<Message> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            List<Message> messages = new LinkedList<>();
            ListMessagesResponse listMessageResponse =null;
            Gmail.Users.Messages.List request = mService.users().messages().list(user)
                    .setLabelIds(Collections.singletonList("INBOX"))
                    .setMaxResults(Long.parseLong("2"));

            listMessageResponse = request.execute();
            messages.addAll(listMessageResponse.getMessages());
            return messages;
        }


        @Override
        protected void onPreExecute() {
            disable();
        }

        @Override
        protected void onPostExecute(final List<Message> output) {
            if (output != null && output.size() > 0) {
                new AsyncTask<Void, Void, String>() {
                    boolean foundGAG = false;
                    @Override
                    protected void onPostExecute(final String res) {
                        enable();
                        if (!foundGAG) {
                            Toast.makeText(MainActivity.this, "No 9GAG links found in your last 2 emails.", Toast.LENGTH_SHORT).show();
                        }
                        if (res != null) {
                            final LinearLayout ly = new LinearLayout(MainActivity.this);
                            ly.setOrientation(LinearLayout.VERTICAL);
                            ly.setGravity(Gravity.CENTER_HORIZONTAL);
                            final RadioGroup rg = new RadioGroup(MainActivity.this);
                            rg.setGravity(Gravity.CENTER_HORIZONTAL);
                            RadioButton op = new RadioButton(MainActivity.this);
                            op.setText("9GAG OP");
                            op.setId(0);
                            op.setChecked(true);
                            rg.addView(op);
                            RadioButton rop = new RadioButton(MainActivity.this);
                            rop.setText("REAL OP");
                            rop.setId(1);
                            rg.addView(rop);
                            ly.addView(rg);
                            final TextView vt = new TextView(MainActivity.this);
                            final String austart = "Auto select after: %s seconds";
                            vt.setText(String.format(Locale.ENGLISH, austart, String.valueOf(10)));
                            vt.setTextColor(Color.parseColor("#e53935"));
                            vt.setTextSize(18);
                            vt.setGravity(Gravity.CENTER_HORIZONTAL);
                            ly.addView(vt);
                            final CountDownTimer[] cd = new CountDownTimer[1];
                                    final AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("FIND:")
                                            .setView(ly)
                                            .setCancelable(true)
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    int checkedId = rg.getCheckedRadioButtonId();
                                                    cd[0].cancel();
                                                    if (checkedId == 0) {
                                                        proccess(res);
                                                    } else if (checkedId == 1) {
                                                        proccess9GAG(res);
                                                    }
                                                }
                                            })
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int checkedId = rg.getCheckedRadioButtonId();
                                                    cd[0].cancel();
                                                    if (checkedId == 0) {
                                                        proccess(res);
                                                    } else if (checkedId == 1) {
                                                        proccess9GAG(res);
                                                    }
                                                }
                                            }).show();
                                    cd[0] = new CountDownTimer(10000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            vt.setText(String.format(Locale.ENGLISH, austart, String.valueOf(millisUntilFinished/1000)));
                                            //here you can have your logic to set text to edittext
                                        }

                                        public void onFinish() {
                                            ad.dismiss();
                                            int checkedId = rg.getCheckedRadioButtonId();
                                            if (checkedId == 0) {
                                                proccess(res);
                                            } else if (checkedId == 1) {
                                                proccess9GAG(res);
                                            }
                                        }

                                    }.start();
                        } else {
                            if (foundGAG) {
                                Toast.makeText(MainActivity.this, "ERROR: Empty response.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        for (int i = 0; i < output.size(); i++) {
                            try {
                                Message message = mService.users().messages().get("me", output.get(i).getId()).execute();
                                String mimeType = message.getPayload().getMimeType();
                                List<MessagePart> parts = message.getPayload().getParts();
                                if (mimeType.contains("alternative")) {
                                    for (MessagePart part : parts) {
                                        String mailBody = new String(Base64.decodeBase64(part.getBody()
                                                .getData().getBytes()));
                                        if (mailBody.contains("9gag.com/gag")) {
                                            foundGAG = true;
                                            return mailBody;
                                        }
                                    }
                                }
                                if (!foundGAG) {
                                    for(MessagePartHeader h:message.getPayload().getHeaders()) {
                                        if (h.getName().equalsIgnoreCase("Subject") && h.getValue().contains("9gag.com/gag")) {
                                            foundGAG = true;
                                            return h.getValue();
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (foundGAG) {
                                break;
                            }
                        }
                        return null;
                    }
                }.execute();

            }
        }

        @Override
        protected void onCancelled() {
            enable();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                }
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);


            enable();
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}

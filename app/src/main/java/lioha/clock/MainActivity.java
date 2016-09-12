package lioha.clock;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mTextView;
    private Button clockIn, clockOut;
    public static ProgressBar preloader;
    public NfcAdapter mNfcAdapter;
    private String type;
    private static final String TAG = MainActivity.class.getName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    public DataSender dataSender;
    private ToastCustom tc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);
        setContentView(R.layout.activity_main);

        /**
         * Данный кусочек кода держит экран постоянно включеным пока данное
         * приложение не выйдет из Main Thread
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /********************************************************************************/

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        tc = new ToastCustom(MainActivity.this);
        dataSender = new DataSender(MainActivity.this);

        mTextView = (TextView) findViewById(R.id.tv);
        clockIn = (Button) findViewById(R.id.checkin);
        clockOut = (Button) findViewById(R.id.checkout);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        preloader = (ProgressBar) findViewById(R.id.preloader);

        clockIn.setOnClickListener(this);
        clockOut.setOnClickListener(this);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");
        nfcAdapter.disableForegroundDispatch(this);
    }

    private Thread.UncaughtExceptionHandler onRuntimeError = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            sendBroadcast(new Intent(Constants.RESURRECT_STRING));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkin:
                clockIn.setActivated(true);
                clockOut.setActivated(false);
                type = "01";
                break;
            case R.id.checkout:
                clockIn.setActivated(false);
                clockOut.setActivated(true);
                type = "02";
                break;

        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        String parsedUID = DataCollector.ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
        mTextView.setText(parsedUID);
        if(type != null) {
            if (parsedUID != null) {
                preloader.setVisibility(View.VISIBLE);
                new InternetConnectionCheck().execute(parsedUID);
            } else {
                tc.showCustomToast(2, null);
            }
        } else {
            tc.showCustomToast(1, null);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if(nfcAdapter != null) {
            enableForegroundMode();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if(nfcAdapter != null) {
            disableForegroundMode();
        }
    }

    /**
     * Метод проверяющий есть ли выход в интернет перед отправкой данных
     */
    public class InternetConnectionCheck extends AsyncTask<String, String, String> {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        private String cardId;

        @Override
        protected String doInBackground(String... params) {
            cardId = params[0];
            String connectionIsOk = null;
            if (activeNetworkInfo != null) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    connectionIsOk = "CONNECTED";
                    return connectionIsOk;
                } catch (IOException e) {

                }
            }
            return connectionIsOk;
        }

        @Override
        protected void onPostExecute(String connectionIsOk) {
            if (connectionIsOk != null && connectionIsOk.equals("CONNECTED")) {
                DataSender.PreparePostParams(type, cardId);
            } else {
                tc.showCustomToast(6, null);
                preloader.setVisibility(View.GONE);
            }
        }
    }
}

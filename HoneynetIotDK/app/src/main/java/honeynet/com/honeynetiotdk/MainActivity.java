package honeynet.com.honeynetiotdk;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import honeynet.com.honeynetiotdk.constants.IotConstant;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText edIP;
    private EditText edPort;
    private Switch lightSwitch;
    private Switch pumpSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightSwitch = (Switch) findViewById(R.id.switchLight);
        pumpSwitch = (Switch) findViewById(R.id.switchPump);
        edIP = (EditText) findViewById(R.id.edIP);
        edPort = (EditText) findViewById(R.id.edPort);

        // Kiểm tra file đọc địa chỉ IP
        try {
            readSettingsFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (checkInternetConenction()) {
            Toast.makeText(this, "Bạn chưa kết nối Internet!", Toast.LENGTH_SHORT);
        }

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkInternetConenction()) {
                        new GetTask().execute(IotConstant.TURN_ON_LIGHT_URL);
                    }
                } else {
                    if (checkInternetConenction()) {
                        new GetTask().execute(IotConstant.TURN_OFF_LIGHT_URL);
                    }
                }
            }
        });

        pumpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkInternetConenction()) {
                        new GetTask().execute(IotConstant.TURN_ON_PUMP_URL);
                    }
                } else {
                    if (checkInternetConenction()) {
                        new GetTask().execute(IotConstant.TURN_OFF_PUMP_URL);
                    }
                }
            }
        });

    }

    /**
     * Calling Settings Activity
     * @param view
     */
    public void callSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Calling About Activity
     * @param view
     */
    public void callAboutActivity(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }


    /**
     * Check the Internet connection. Return true if phone has connection.
     * Retrun false if phone hasn't connection
     * @return
     */
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }
    private class GetTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, length);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Read values setting from file
     * @throws IOException
     */
    public void readSettingsFile() throws IOException {
        File file = this.getFileStreamPath(IotConstant.SETTINGS_FILE_NAME);
        if (file.exists()) { //Read file if it exist
            FileInputStream fileInputStream = this.openFileInput(file.getName());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = "";
            ArrayList<String> arrayList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                arrayList.add(line);
            }

            if(arrayList.size() != 0) {
                String ip = arrayList.get(0);
                String port = arrayList.get(1);
                String[] fp = arrayList.get(2).split(",");

                IotConstant.IP = ip;
                IotConstant.PORT = port;
                IotConstant.FP1 = fp[0];
                IotConstant.FP2 = fp[1];

                edIP.setText(IotConstant.IP);
                edPort.setText(IotConstant.PORT);
            }
        } else {
            edIP.setText(IotConstant.IP);
            edPort.setText(IotConstant.PORT);
        }
    }
}

package honeynet.com.honeynetiotdk;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honeynet.com.honeynetiotdk.constants.IotConstant;

import static java.util.Calendar.*;
import static java.util.Calendar.getInstance;

/**
 * Created by Phat on 13/6/2016.
 */
public class GraphActivity extends AppCompatActivity {

    //Set default value
    private String macDevice = "1a:fe:34:a2:ca:19";
    private Button btnDatePicker;
    private String datepicked;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        createWidget();

        //Set up graph
        new GetValueForChart().execute(IotConstant.URL_GET_VALUE_TEMPERATURE_CHART(
                macDevice,
                String.valueOf(getDMYNow("M") + 1),
                String.valueOf(getDMYNow("Y"))
        ));

    }

    public void createWidget(){

        //Set default text view date picker
        btnDatePicker = (Button) findViewById(R.id.graph_btn_datepicker);

        //Set value for date picked
        datepicked = "Tháng: " + (getDMYNow("M") + 1) + "/" + getDMYNow("Y");

        btnDatePicker.setText(datepicked);


    }

    /**
     * Get Day|Month|Year => D|M|Y
     * @param flag
     * @return
     */
    public int getDMYNow(String flag){
        java.util.Date date= new Date();
        Calendar cal = getInstance();
        cal.setTime(date);

        switch (flag){
            case "D":
                return cal.get(DAY_OF_MONTH);
            case "M":
                return cal.get(MONTH);
            case "Y":
                return cal.get(YEAR);
            default:
                break;
        }
        return 0;
    }

    private class GetValueForChart extends AsyncTask<String, String, LineData>{
        LineChart lineChart;
        String strError;
        String location;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lineChart = (LineChart) findViewById(R.id.graph_chart);
        }

        @Override
        protected LineData doInBackground(String... params) {
            try {
                String strResult = downloadContent(params[0]);
                String strValues = "";
                JSONObject objResult = new JSONObject(strResult);

                LineData data = null;
                if(objResult.getBoolean("success") == true){
                    location = objResult.getJSONObject("data").getString("location");
                    strValues = objResult.getJSONObject("data").getString("values");

                    strValues = strValues.substring(1,strValues.length()-1);
                    String[] arrStrValues = strValues.split(",");


                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<String>();
                    for (int i = 0; i < arrStrValues.length ; i++){
                        entries.add(new Entry(Float.parseFloat(arrStrValues[i]), i));
                        labels.add(String.valueOf(i+1));
                    }

                    LineDataSet dataset = new LineDataSet(entries, " # of Temperature");
                    data = new LineData(labels, dataset);

                }else{
                    strError = objResult.getString("message");
                }
                return data;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LineData data) {
            super.onPostExecute(data);

            if(data != null){
                lineChart.setData(data); // set the data and list of lables into chart

                //Description of chart
                lineChart.setDescription(location);

                //Display max value X range
                lineChart.setVisibleXRangeMaximum(10);

                //ZoomIn
//                lineChart.zoomIn();

                //Fresher chart
                lineChart.invalidate();

                btnDatePicker.setText(datepicked);

            }else{
                Toast.makeText(GraphActivity.this, strError, Toast.LENGTH_LONG).show();
            }



        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //Draw line

            // creating list of entry

        }
    }

    /**
     * Get content from server
     * @param param is url request to server
     * @return
     * @throws IOException
     */
    private String downloadContent(String param) throws IOException {

        String url = param;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Event click date picker
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Class set up frame datepicker
     */
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

//            btnDatePicker.setText("Tháng: " + (month + 1) + "/" + year);
            datepicked = "Tháng: " + (month + 1) + "/" + year;

            //Set up graph
            new GetValueForChart().execute(IotConstant.URL_GET_VALUE_TEMPERATURE_CHART(
                    macDevice,
                    String.valueOf(month + 1),
                    String.valueOf(year)
            ));
        }
    }

}

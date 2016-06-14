package honeynet.com.honeynetiotdk;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import honeynet.com.honeynetiotdk.constants.IotConstant;

/**
 * Created by Phat on 13/6/2016.
 */
public class GraphActivity extends AppCompatActivity {

    //Set default value
    private String macDevice = "1a:fe:34:a2:ca:19";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        LineChart lineChart = (LineChart) findViewById(R.id.graph_chart);
        Toast.makeText(this, "Loading ...", Toast.LENGTH_LONG).show();
        new GetValueForChart().execute(IotConstant.URL_GET_VALUE_TEMPERATURE_CHART(macDevice));


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

                    LineDataSet dataset = new LineDataSet(entries, " # of Date");
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
                lineChart.zoomIn();

                //Fresher chart
                lineChart.invalidate();


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


}

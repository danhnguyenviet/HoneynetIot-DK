package honeynet.com.honeynetiotdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import honeynet.com.honeynetiotdk.adapter.AdapterSetting;
import honeynet.com.honeynetiotdk.constants.IotConstant;
import honeynet.com.honeynetiotdk.threads.SettingsThread;

public class SettingsActivity extends AppCompatActivity {

    private EditText ipAddress;
    private EditText port;
    private Button btnCancel;
    private Button btnSave;
    private ProgressDialog progressDialog;
    private ListView lvListFP;
    private ArrayList<String> arrFP;
    private AdapterSetting adapterSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ipAddress = (EditText) findViewById(R.id.ed_setting_ip);
        port = (EditText) findViewById(R.id.ed_setting_port);
        lvListFP = (ListView)findViewById(R.id.lv_setting_list_fp);

        btnSave = (Button)findViewById(R.id.btn_setting_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettingToFile();
            }
        });

        btnCancel = (Button)findViewById(R.id.btn_setting_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        readSettingFromFile();
    }

    //Read Data Setting from File
    public void readSettingFromFile(){
        //Create progressDialog for beauty
        progressDialog = ProgressDialog.show(SettingsActivity.this, "Đọc dữ liệu", "Xin chờ...");

        SettingsThread threadSettingsFile = new SettingsThread(SettingsActivity.this,handler,"read");
        threadSettingsFile.start();
    }

    //Save Data Setting to File
    public void saveSettingToFile(){
        String sendIp = ipAddress.getText().toString();
        String sendPort = port.getText().toString();

        //Create String FP and send to Server.( FP = "1,2,3,4,5...")
        String listFP = "";
        for (int i = 0; i < arrFP.size(); i++) {
            if (i == (arrFP.size() - 1)) {
                listFP += arrFP.get(i).toString();
                break;
            }
            listFP = listFP + arrFP.get(i).toString() + ",";

        }

        String[] strArrFP = listFP.split(",");

        //Create progressDialog for beauty
        progressDialog = ProgressDialog.show(SettingsActivity.this, "Lưu dữ liệu", "Xin chờ...");

        SettingsThread threadSettingsFile = new SettingsThread(sendIp,sendPort,strArrFP,SettingsActivity.this,handler,"save");
        threadSettingsFile.start();
    }

    //Create Default Data if Data not exist on Server
    public void createDefaultData(){
        arrFP = new ArrayList<>();
        arrFP.add(IotConstant.FP1);
        arrFP.add(IotConstant.FP2);

        adapterSetting = new AdapterSetting(SettingsActivity.this, R.layout.item_list_setting, arrFP);
        lvListFP.setAdapter(adapterSetting);

        ipAddress.setText(IotConstant.IP);
        port.setText(IotConstant.PORT);
    }

    //Create Handler for thread and update for Screen Setting
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //File
            if(msg.getData().getString("flagSetting").toString() == "readFile"){
                if(msg.getData().getBoolean("null",true) == true){ //Data not exist on Server. Get Default Data
                    Toast.makeText(SettingsActivity.this, "Can't find data", Toast.LENGTH_SHORT).show();
                    createDefaultData();
                    Toast.makeText(SettingsActivity.this,"Create default data...",Toast.LENGTH_SHORT).show();
                }else{
                    readResultFromFile(msg.getData().getString("ip"),
                            msg.getData().getString("port"),
                            msg.getData().getStringArray("fp"));
                }
                progressDialog.dismiss();
            }
            if(msg.getData().getString("flagSetting").toString() == "saveFile"){
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this,"Saved success...", Toast.LENGTH_SHORT).show();

            }

        }
    };

    public void readResultFromFile(String ip, String ports, String[] fp){
        arrFP = new ArrayList(Arrays.asList(fp));

        //Create adapter setting
        adapterSetting = new AdapterSetting(SettingsActivity.this,R.layout.item_list_setting,arrFP);
        lvListFP.setAdapter(adapterSetting);

        //Set ip and port
        ipAddress.setText(ip);
        port.setText(ports);
    }
}

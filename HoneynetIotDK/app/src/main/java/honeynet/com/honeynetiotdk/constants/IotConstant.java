package honeynet.com.honeynetiotdk.constants;

import android.content.Context;

import java.io.IOException;

/**
 * Created by Danh on 6/10/2016.
 */
public class IotConstant {

    public static String IP         = "192.168.238.239";
    public static String PORT       = "8080";
    public static String FP1        = "GPIO1"; // Pump
    public static String FP2        = "GPIO2"; // Light

    public static String TURN_ON_LIGHT_URL          = "http://" + IP + ":" + PORT + "/report1/mobile/api.php?" + FP1 + "=ON&id=1";
    public static String TURN_OFF_LIGHT_URL         = "http://" + IP + ":" + PORT + "/report1/mobile/api.php?" + FP1 + "=OFF&id=1";
    public static String TURN_ON_PUMP_URL           = "http://" + IP + ":" + PORT + "/report1/mobile/api.php?" + FP2 + "=ON&id=2";
    public static String TURN_OFF_PUMP_URL          = "http://" + IP + ":" + PORT + "/report1/mobile/api.php?" + FP2 + "=OFF&id=2";

    public final static String SETTINGS_FILE_NAME   = "settings.txt";
}

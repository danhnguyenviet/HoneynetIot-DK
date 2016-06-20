package honeynet.com.honeynetiotdk.constants;

/**
 * Created by Danh on 6/10/2016.
 */
// http://www.appifiedtech.net/2015/06/13/android-http-request-example/
public class IotConstant {

    public static String IP         = "203.162.51.92";
    public static String PORT       = "8080";
    public static String FP1        = "1"; // Pump
    public static String FP2        = "2"; // Light

    public static String ACTION_URL                 = "http://" + IP + "/mobie/api.php";
    public static String TURN_ON_LIGHT_PARA         = "gpio=" + FP1 + "&status=1&id=1";
    public static String TURN_OFF_LIGHT_PARA        = "gpio=" + FP1 + "&status=0&id=1";
    public static String TURN_ON_PUMP_PARA          = "gpio=" + FP2 + "&status=1&id=2";
    public static String TURN_OFF_PUMP_PARA         = "gpio=" + FP2 + "&status=0&id=2";

    public final static String SETTINGS_FILE_NAME   = "settings.txt";


    public static String URL_TEMPERATURE_CHART = "http://" + IP + "/mobie/get_temperature.php";


    /**
     * URL_GET_VALUE_TEMPERATURE_CHART
     * @param macDevice Mac of Device
     * @return String url
     */
    public static String URL_GET_VALUE_TEMPERATURE_CHART(String macDevice, String month, String year){
        return URL_TEMPERATURE_CHART + "?mac=" + macDevice + "&month=" + month + "&year=" + year;
    }

    public static void setUrlTemperatureChart(String ip){
        IotConstant.URL_TEMPERATURE_CHART = "http://" + ip + "/mobie/get_temperature.php";
    }

    public static void updateAfterReadingData() {
        ACTION_URL                 = "http://" + IP + "/mobie/api.php";
        TURN_ON_LIGHT_PARA         = "gpio=" + FP1 + "&status=1&id=1";
        TURN_OFF_LIGHT_PARA        = "gpio=" + FP1 + "&status=0&id=1";
        TURN_ON_PUMP_PARA          = "gpio=" + FP2 + "&status=1&id=2";
        TURN_OFF_PUMP_PARA         = "gpio=" + FP2 + "&status=0&id=2";
    }
}

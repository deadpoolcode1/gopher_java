package ATE_MAIN;

import LMDS_ICD.All_IP_Radio_Settings;
import LMDS_ICD.LM_Camera_Status;
import LMDS_ICD.Navigation_Data;
import org.jfree.data.xy.XYSeries;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.http.HttpRequest;
import java.util.concurrent.ArrayBlockingQueue;

public class IP_RADIO_GD {
    public XYSeries
    ANT1_RSSI, ANT2_RSSI,ANT3_RSSI,ANT4_RSSI, RAW_NOISE_PWR, SYNC_SIGNAL_PWR, SYNC_NOISE_PWR,
    CURRENT_TEMP, MAX_TEMP, CURRENT_VOLT, MIN_VOLT, MAX_VOLT;
    //public boolean have_HTTP_request = false;
    public All_IP_Radio_Settings AIRS = new All_IP_Radio_Settings();
    public String address_string = "", password="";
    public int streaming_listener_port = -1;
    //public String ip_radio_commands_chain = "", command_key="";
    // each Q element is a string array with 5 items:
    // [0]-commandKey, [1]-request, [2]-serverIP, [3]-paramsBefore, [4]-paramsAfter
    public ArrayBlockingQueue<String[]> HTTPMsgQueue = new ArrayBlockingQueue<>(20);
    public boolean logged_in = false;
    public int antenna_mask = 0xF, // = request for changes of current mask; 1111 binary; default - all four channels are active
                      current_antenna_mask = 0xF; // current setting of the four channels
    public static String
            Log_In="",
            Channel_Settings_1="",
            Channel_Settings_2="",
            Channel_Settings_3="",
            Channel_Settings_4="",
            Channel_Settings_5="",
            Start_Transmit="",
            Stop_Transmit="",
            Antennae_Mask="",
            Start_Streaming="",
            Radio_SW_Version="",
            command_1="", command_2="";
}

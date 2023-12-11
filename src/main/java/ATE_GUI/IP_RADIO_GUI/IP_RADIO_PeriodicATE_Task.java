package ATE_GUI.IP_RADIO_GUI;

import ATE_MAIN.CurlHttpResponse;
import ATE_MAIN.IP_RADIO_GD;
import ATE_MAIN.Streaming_UDP_Handler;
import ATE_MAIN.main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IP_RADIO_PeriodicATE_Task extends Thread {
    int time_tick=4000000;
    private IP_RADIO_GD iP_RADIO_GD = null;
    //String curlParamsBeforeRequest="", curlParamsAfterRequest="", radio_ip="";
    private IP_RADIO_Monitor host_IP_RADIO_Monitor;
    Streaming_UDP_Handler streaming_UDP_Handler;

    static final String curlParamsBeforeRequest_reg_msg = "-skL --fail-with-body -X POST -d ";
    static final String curlParamsBeforeRequest_login_msg = "-skL --fail-with-body ";
    String curlParamsAfterRequest_reg_msg_pw = "-c cookie%00.jar -b cookie%00.jar"; // for a password protected radio
    public static final String curlParamsAfterRequest_reg_msg_npw = ""; // for a radio without a password
    String curlParamsAfterRequest_login_msg = "-c cookie%00.jar";
    static final String get_all_radio_settings =
            "[{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"print_all_settings\\\",\\\"id\\\":\\\"101\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"nw_name\\\",\\\"id\\\":\\\"102\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"max_link_distance\\\",\\\"id\\\":\\\"103\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"enable_max_power\\\",\\\"id\\\":\\\"104\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"routing_proto\\\",\\\"id\\\":\\\"105\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"read_power_mw\\\",\\\"id\\\":\\\"106\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"routing_beacon_period\\\",\\\"id\\\":\\\"107\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"routing_beacon_mcs\\\",\\\"id\\\":\\\"108\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"aggr_thresh\\\",\\\"id\\\":\\\"109\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"max_speed\\\",\\\"id\\\":\\\"110\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"burst_time\\\",\\\"id\\\":\\\"111\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"num_retx\\\",\\\"id\\\":\\\"112\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"num_rts_retx\\\",\\\"id\\\":\\\"113\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"la_mode\\\",\\\"id\\\":\\\"114\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"radio_mode\\\",\\\"id\\\":\\\"115\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"var_gi_mode\\\",\\\"id\\\":\\\"116\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"var_gi_cyc_pre_len\\\",\\\"id\\\":\\\"117\\\"},"+
            "{\\\"jsonrpc\\\":\\\"2.0\\\",\\\"method\\\":\\\"beamform_enable\\\",\\\"id\\\":\\\"118\\\"}]" ;
    public IP_RADIO_PeriodicATE_Task(IP_RADIO_GD iP_RADIO_GD, IP_RADIO_Monitor host_IP_RADIO_Monitor) throws UnknownHostException, InterruptedException {
        this.iP_RADIO_GD = iP_RADIO_GD;
        this.host_IP_RADIO_Monitor = host_IP_RADIO_Monitor;
        streaming_UDP_Handler = new Streaming_UDP_Handler(iP_RADIO_GD.streaming_listener_port,
                iP_RADIO_GD.address_string, main.ate_radio_address_string,host_IP_RADIO_Monitor);
        streaming_UDP_Handler.start();
        Thread.sleep(1000);
        // set a special cookie name for this radio, to avoid cookie conflicts with the other one
        String cookie_suffix = "_" + String.valueOf(iP_RADIO_GD.streaming_listener_port); // use the listener port id for the suffix
        curlParamsAfterRequest_reg_msg_pw = curlParamsAfterRequest_reg_msg_pw.replaceAll("%00", cookie_suffix);
        curlParamsAfterRequest_login_msg = curlParamsAfterRequest_login_msg.replaceAll("%00", cookie_suffix);
    }
    public void run(){
        while(true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) { e.printStackTrace(); }
            // ********** 5 Hz period actions ***********
            // each Q element is a string array with 5 items:
            // [0]-commandKey, [1]-request, [2]-serverIP, [3]-paramsBefore, [4]-paramsAfter
            if(iP_RADIO_GD.HTTPMsgQueue.size() > 0) {
                // send pending HTTP request
                String[] msg;
                try {
                    msg = iP_RADIO_GD.HTTPMsgQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (msg[1].equals("NONE")) {
                    System.out.println("IP Radio Control - command not supported. Ignored");
                    continue;
                }
                // set for common requests
                msg[2] = iP_RADIO_GD.address_string;
                msg[3] = curlParamsBeforeRequest_reg_msg;
                msg[4] = curlParamsAfterRequest_reg_msg_pw;
                if (iP_RADIO_GD.password.equals("NONE"))
                    msg[4] = curlParamsAfterRequest_reg_msg_npw; // radio has no password
                switch (msg[0]) { // = commandKey
                    case "Log_In":
                        if (iP_RADIO_GD.logged_in) {
                            System.out.println("IP Radio Control - radio already logged in. Will start streaming.");
                            break;
                        }
                        msg[1] = msg[1].replace("%00", iP_RADIO_GD.address_string);
                        msg[1] = msg[1].replace("%01", iP_RADIO_GD.password);
                        msg[2] = "";
                        msg[3] = curlParamsBeforeRequest_login_msg;
                        msg[4] = curlParamsAfterRequest_login_msg;
                        break;
                    case "Channel_Settings_1":
                    case "Channel_Settings_2":
                    case "Channel_Settings_3":
                    case "Channel_Settings_4":
                    case "Channel_Settings_5":
                    case "Start_Transmit":
                    case "Stop_Transmit":
                    case "Radio_SW_Version":
                    case "Full Status After User Command":
                    case "Periodic Full Status":

                        break;
                    case "Antennae_Mask":
                        String mask_string = String.valueOf(iP_RADIO_GD.antenna_mask);
                        msg[1] = msg[1].replace("%01", mask_string); // use same mask for both TX and RX
                        msg[1] = msg[1].replace("%02", mask_string); // use same mask for both TX and RX
                        break;
                    case "Start_Streaming":
                        msg[1] = msg[1].replaceAll("%00", String.valueOf(iP_RADIO_GD.streaming_listener_port));
                        break;

                }
                // send the request to the radio
                CurlHttpResponse response = null;
                try {
                    response = SendMessageToRadio(msg);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // analyse results
                if (response == null)
                    continue; // bad response - ignored (the SendMessageToRadio method prints out the results in this case)
                int rsc = response.getResponseCode();
                // a bad response (e.g. 00) is handled by the sending methods
                if (rsc >= 200 && rsc < 400) {
                    if (msg[0].equals("Periodic Full Status"))
                        continue;
                    else {
                        System.out.println("IP_RADIO_PeriodicATE_Task: " + msg[0] + " success!");
                        if (msg[0].equals("Log_In"))
                            iP_RADIO_GD.logged_in = true;
                        if (msg[0].equals("Full Status After User Command"))
                            continue;
                        // refresh the full IP radio status
                        try {
                            GetIPRadioFullStatus("Full Status After User Command");
                        } catch (ExecutionException | ParseException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if(time_tick%5000==0) {
                // ********** 0.2 Hz period actions ***********
                // every 5 seconds refresh the full IP radio status of both radios
                try {
                    GetIPRadioFullStatus("Periodic Full Status");
                } catch (ExecutionException | ParseException e) { e.printStackTrace();
                } catch (InterruptedException e) { e.printStackTrace(); }
             }
            if(time_tick%1000==0)
                // ********** 1 Hz period actions ***********
                SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    host_IP_RADIO_Monitor.ShowData(iP_RADIO_GD.AIRS);
                }
            });
            time_tick+=200;
        }
    }

    private CurlHttpResponse SendMessageToRadio(String[] msg) throws ParseException, InterruptedException {
        CurlHttpResponse response = null;
                CompletableFuture<CurlHttpResponse> future = SendHTTPToRadio(msg);
        try {
            response = future.get(20, TimeUnit.SECONDS); // Timeout of ... seconds
        } catch (Exception e) {
            if (e instanceof TimeoutException)
                System.out.println("IP_RADIO_PeriodicATE_Task: Radio IP="+iP_RADIO_GD.address_string+"  Time Out waiting for an HTTP response for message: "+msg[0] );
            else
                e.printStackTrace();
        }
        if(response != null) {
            if(response.getResponseCode() >= 200 && response.getResponseCode() < 400) {// range of good HTTP responses
                switch(msg[0]) {
                    case "Periodic Full Status":
                        ProcessPeriodicRadioFullStatus(response);
                        return response;
                    case "Radio_SW_Version":
                        ProcessRadio_SW_Version(response);
                        return response;
                }
                System.out.println("IP Radio Control: " + msg[0] + " success ! Response code=" + response.getResponseCode());
            }
            else {
                System.out.println("IP Radio Control: " + msg[0] + " failure. Response code= " + response.getResponseCode() +
                        "  Response result=" + response.getResponseBody());
                response=null;
            }
        }
        return response;
    }

    private void ProcessRadio_SW_Version(CurlHttpResponse response) throws ParseException {
        // todo analyze the response and print the radio SW version
        if(response == null)
            return;
        String result = response.getResponseBody();
        if(InvalidResult(result))
            return; // radio not responding or invalid request
        JSONArray result_array = (JSONArray)new JSONParser().parse(result);
        JSONObject jitem = (JSONObject) result_array.get(0);
        int result_id = Integer.parseInt((String) jitem.get("id"));
        JSONArray val_array = (JSONArray) jitem.get("result");
        String radio_sw_version = new String((String) val_array.get(0));
        System.out.println("IP Radio Control: Radio IP=" +iP_RADIO_GD.address_string+", SW Version="+radio_sw_version);
    }

    private void GetIPRadioFullStatus(String Command) throws ExecutionException, InterruptedException, ParseException {
        if (!iP_RADIO_GD.logged_in)
            return; // do not send the periodic status request if the radio is not logged in yet
        // each Q element is a string array with 5 items:
        // [0]-commandKey, [1]-request, [2]-serverIP, [3]-paramsBefore, [4]-paramsAfter
        // request full status from the adt radio; ; wait for a response
        String[] msg = new String[5];
        msg[0] = Command;
        msg[1] = get_all_radio_settings;
        try {
            // add the message to the end of this radio HTTP msg Q; may block if the Q is full.
            host_IP_RADIO_Monitor.iP_RADIO_GD.HTTPMsgQueue.put(msg);
        } catch (InterruptedException ex) {throw new RuntimeException(ex);}
    }
    private void ProcessPeriodicRadioFullStatus(CurlHttpResponse response) throws ParseException {
        if(response == null)
            return;
        String result = response.getResponseBody();
        if(InvalidResult(result))
            return; // radio not responding or invalid request
        JSONArray result_array = (JSONArray)new JSONParser().parse(result);
        int gsize=result_array.size();
        for(int i=0; i<gsize; i++) {
            JSONObject jitem = (JSONObject) result_array.get(i);
            int result_id = Integer.parseInt((String)jitem.get("id"));
            JSONArray val_array= (JSONArray) jitem.get("result");
            switch (result_id) {
                case 101:  // print_all_settings result
                    int size = val_array.size();
                    ArrayList<String> key_list = new ArrayList<String>();
                    JSONArray[] lcl_val_array= new JSONArray[size/2];
                    for(int k=0; k<size; k+=2) {
                        key_list.add((String) val_array.get(k));
                        lcl_val_array[k/2] = (JSONArray) val_array.get(k+1);
                    }
                    for(int j=0; j<size/2; j++) {
                        switch(key_list.get(j)) {
                            case "freq": iP_RADIO_GD.AIRS.frequency = new String((String) lcl_val_array[j].get(0)); break;
                            case "bw": iP_RADIO_GD.AIRS.bandwidth = new String((String) lcl_val_array[j].get(0)); break;
                            case "power_dBm": iP_RADIO_GD.AIRS.total_transmit_power = new String((String) lcl_val_array[j].get(0)); break;
                            case "tx_ant_mask": iP_RADIO_GD.AIRS.antennae_mask = new String((String) lcl_val_array[j].get(0));
                                iP_RADIO_GD.current_antenna_mask=Integer.parseInt(iP_RADIO_GD.AIRS.antennae_mask); break;
                            case "rx_ant_mask": iP_RADIO_GD.AIRS.antennae_mask = new String((String) lcl_val_array[j].get(0));
                                iP_RADIO_GD.current_antenna_mask=Integer.parseInt(iP_RADIO_GD.AIRS.antennae_mask); break;// same mask value assumed for both RX & TX
                            case "mcs": iP_RADIO_GD.AIRS.MCS = new String((String) lcl_val_array[j].get(0)); break;
                            case "serial_config": break; // value not used
                            default:
                                System.out.println("IP Radio Control - illegal header in print_all_settings result. Ignored: "+key_list.get(j)); break;
                        }
                    }
                    break;
                // results of single parameter read requests; fill the global data RF settings structure
                case 102: iP_RADIO_GD.AIRS.network_id = new String((String) val_array.get(0)); break; //
                case 103: iP_RADIO_GD.AIRS.link_distance = new String((String) val_array.get(0)); break; //
                case 104: iP_RADIO_GD.AIRS.enable_max_power = new String((String) val_array.get(0)); break; //
                case 105: iP_RADIO_GD.AIRS.routing_mode = new String((String) val_array.get(0)); break; //
                case 106: String power_mw = new String((String) val_array.get(0)); break; // resrved TODO see if different from the power obtained by print_all_settings result
                case 107: iP_RADIO_GD.AIRS.routing_beacons_period = new String((String) val_array.get(0)); break; //
                case 108: iP_RADIO_GD.AIRS.routing_beacons_mcs = new String((String) val_array.get(0)); break; //
                case 109: iP_RADIO_GD.AIRS.fragmentation_threshold = new String((String) val_array.get(0)); break; //
                case 110: iP_RADIO_GD.AIRS.max_ground_speed = new String((String) val_array.get(0)); break; //
                case 111: iP_RADIO_GD.AIRS.burst_time = new String((String) val_array.get(0)); break; //
                case 112: iP_RADIO_GD.AIRS.number_of_retransmissions = new String((String) val_array.get(0)); break; //
                case 113: iP_RADIO_GD.AIRS.rts_retries = new String((String) val_array.get(0)); break; //
                case 114: iP_RADIO_GD.AIRS.la_mode = new String((String) val_array.get(0)); break; //
                case 115: iP_RADIO_GD.AIRS.radio_mode = new String((String) val_array.get(0)); break; //
                case 116: iP_RADIO_GD.AIRS.variable_gi_mode = new String((String) val_array.get(0)); break; //
                case 117: iP_RADIO_GD.AIRS.cyclic_prefix_length = new String((String) val_array.get(0)); break; //
                case 118: iP_RADIO_GD.AIRS.beam_forming = new String((String) val_array.get(0)); break; //
                default:
                    System.out.println("IP Radio Control - illegal result_id in one of get all radio settings results. Ignored: "+result_id);
            }
        }
    }
    private CompletableFuture<CurlHttpResponse> SendHTTPToRadio(String[] msg) {
        // sends the message at the head of the radio HTTP TX Q
        // each Q element is a string array with 5 items:
        // [0]-commandKey, [1]-request, [2]-serverIP, [3]-paramsBefore, [4]-paramsAfter
        // take the head msg from the Q
        String curlCommand;
        String FullServerURL = "\"http://" + msg[2] + "/streamscape_api\"";
        if (msg[2].isEmpty()) {
            FullServerURL = ""; // case of the special login message
            curlCommand = "curl.exe" + "  " + msg[3] + msg[1] + "  " + FullServerURL + "  " + msg[4];
        } else // any other message
            curlCommand = "curl.exe" + "  " + msg[3] + "  " + msg[1] + "  " + "  " + FullServerURL + "  " + msg[4];
        // System.out.println("CURL Command: " + curlCommand);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Process process = Runtime.getRuntime().exec(curlCommand);
                //String responseBody = readStream(process.getInputStream());
                int ProcessReturnCode = process.waitFor(); // wait here (block) for the system process to return; 0 code indicates normal return.
                String responseBody = readStream(process.getInputStream());
                int responseCode = ExtractResponseCode(responseBody);
                return new CurlHttpResponse(responseCode, responseBody);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return new CurlHttpResponse(-1, e.getMessage());
            }
        });
    }

    private int ExtractResponseCode(String responseBody) {
        int responseCode = 200; // default good HTTP return code
        int titleindex = responseBody.indexOf("<title>");
        if (titleindex != -1) {
            // found an error HTTP response        }
            // Get the length of the known sub-string ("<title>")
            // Get the beginning index of the following string
            int beginningIndex = titleindex + "<title>".length();
            // Get the following string using the known length
            int ErrorCodeLength = 3; // Change this to the actual known length
            String ErrorString = responseBody.substring(beginningIndex, beginningIndex + ErrorCodeLength);
            responseCode = Integer.parseInt(ErrorString);
        }
        int errorindex = responseBody.indexOf("error");
        if (errorindex != -1) {
            // found an error server (radio) response
            responseCode = 500; // some server / invalid data error
        }
        return responseCode;
    }

    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private boolean InvalidResult(String result) {
        if(result==null || result.length() < 5) {
            System.out.println("IP Radio Control - null or too short result returned for HTTP command");
            return true;
        }

        int errorindex = result.indexOf("error"); // specific error response from the silvus radio server
        if (errorindex != -1) {
            System.out.println("IP Radio Control - error result returned for HTTP command: "+result);
            return true;
        }
        return false;
    }
}

package ATE_MAIN;

import ATE_GUI.GUI_CMN.SetSerialPorts;
import ATE_GUI.IP_RADIO_GUI.IP_RADIO_Monitor;
import ATE_GUI.IP_RADIO_GUI.IP_RADIO_PeriodicATE_Task;
import ATE_GUI.JPM_GUI.JPM_PeriodicATE_Task;
import ATE_GUI.JPM_GUI.JPM_TestSelect;
import ATE_GUI.LUMO_GUI.LUMO_GPS_Monitor;
import ATE_GUI.LUMO_GUI.LUMO_PeriodicATE_Task;
import ATE_GUI.LUMO_GUI.LUMO_TestSelect;
import ATE_GUI.MPU_GUI.MPU_AHRS_Monitor;
import ATE_GUI.MPU_GUI.MPU_Camera_Monitor;
import ATE_GUI.MPU_GUI.MPU_PeriodicATE_Task;
import ATE_GUI.MPU_GUI.MPU_TestSelect;
import ATE_GUI.PCM_GUI.PCM_Battery_Monitor;
import ATE_GUI.PCM_GUI.PCM_Motor_Monitor;
import ATE_GUI.PCM_GUI.PCM_PeriodicATE_Task;
import ATE_GUI.PCM_GUI.PCM_TestSelect;
import LMDS_ICD.*;
import com.fazecast.jSerialComm.SerialPort;
import org.jfree.chart.ChartPanel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class main {
    final static int MSG_FRAME_SIZE = 32+8, LAN_PORT_ID=10; // size of header + tail=40
    static SerialPort[] comPorts;
    static ATE_JPM_LAN aTE_JPM_LAN;
    private static HttpClient httpClient;
    public static IP_RADIO_Monitor iP_RADIO_Monitor_ADT, iP_RADIO_Monitor_GDT;
    public static IP_RADIO_GD IP_RADIO_GD_ADT, IP_RADIO_GD_GDT;
    public static String jpm_ds_address_string = "", ate_address_string = "",
            ate_radio_address_string ="", test_radios = "NO" ;
    static JFrame frame;
    static int nports;
    public static JSONObject parameters=null;


    static public void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException, ParseException {
        IP_RADIO_GD_ADT = new IP_RADIO_GD();
        IP_RADIO_GD_GDT = new IP_RADIO_GD();
        /*
        // constructs an HttpClient, capable of sending requests to a WEB server and receiving responses
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // select HTTP version
                .connectTimeout(Duration.ofSeconds(10)) // set connect timeout
                .build();
        */
        InitParameters(); // read the common configuration data
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                frame = new JFrame("LMDS Testing Application - Aug. 14, 2023");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JTabbedPane tabbedPane = new JTabbedPane();
                LUMO_TestSelect lUMO_TestSelect = new LUMO_TestSelect(frame);
                tabbedPane.addTab("LUMO Main", null, new JScrollPane(lUMO_TestSelect),"LUMO Main test controls & results");
                LUMO_GPS_Monitor lUMO_GPS_Monitor = new LUMO_GPS_Monitor(frame);
                tabbedPane.addTab("GPS Monitor", null, new JScrollPane(lUMO_GPS_Monitor),"Monitors the UBX GPS (using specific NAV report from the GPS)");
                PCM_TestSelect PCM_TestSelect = new PCM_TestSelect(frame);
                tabbedPane.addTab("PCM Main", null, new JScrollPane(PCM_TestSelect),"PCM Main test controls & results");
                PCM_Battery_Monitor pCM_Battery_Monitor = new PCM_Battery_Monitor(frame);
                tabbedPane.addTab("Battery Monitor", null, new JScrollPane(pCM_Battery_Monitor),"Monitors the battery (using PCM reports)");
                PCM_Motor_Monitor pCM_Motor_Monitor = new PCM_Motor_Monitor(frame);
                tabbedPane.addTab("Motor Monitor", null, new JScrollPane(pCM_Motor_Monitor),"Monitors the Motor (using PCM reports)");
                MPU_TestSelect MPU_TestSelect = new MPU_TestSelect(frame);
                tabbedPane.addTab("MPU Main", null, new JScrollPane(MPU_TestSelect),"MPU Main test controls & results");
                MPU_Camera_Monitor mPU_Camera_Monitor = new MPU_Camera_Monitor(frame);
                tabbedPane.addTab("Camera Monitor", null, new JScrollPane(mPU_Camera_Monitor),"Monitors the camera (using MPU reports)");
                MPU_AHRS_Monitor mPU_AHRS_Monitor = new MPU_AHRS_Monitor(frame);
                tabbedPane.addTab("AHRS Monitor", null, new JScrollPane(mPU_AHRS_Monitor),"Monitors the AHRS (using MPU reports)");
                JPM_TestSelect JPM_TestSelect = new JPM_TestSelect(frame);
                tabbedPane.addTab("JPM Main", null, new JScrollPane(JPM_TestSelect),"JPM Main test controls & results");
                try {
                    iP_RADIO_Monitor_ADT = new IP_RADIO_Monitor(frame, IP_RADIO_GD_ADT);
                } catch (UnknownHostException | InterruptedException e) { throw new RuntimeException(e);}
                tabbedPane.addTab("IP Radio Monitor ADT", null, new JScrollPane(iP_RADIO_Monitor_ADT),"Monitors the IP Radio Status");
                try {
                    iP_RADIO_Monitor_GDT = new IP_RADIO_Monitor(frame, IP_RADIO_GD_GDT);
                } catch (UnknownHostException | InterruptedException e) { throw new RuntimeException(e); }
                tabbedPane.addTab("IP Radio Monitor GDT", null, new JScrollPane(iP_RADIO_Monitor_GDT),"Monitors the IP Radio Status");
                IP_RADIO_PeriodicATE_Task iP_RADIO_PeriodicATE_Task_ADT = null;
                frame.add(tabbedPane, BorderLayout.CENTER);
                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
        InitSerialCommPorts(); // find and set the service and other serial ports
        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
        InitPeriodicalTasks();
        InitLAN_Port();
        try { Thread.sleep(1000 * 3600); } catch (Exception e) { e.printStackTrace(); }
        // close all serial ports
        for(int i=0; i<nports; i++) {
            comPorts[i].removeDataListener();
            comPorts[i].closePort();
        }
        System.out.println("ATE Test Run terminated");
    }

    private static void InitPeriodicalTasks() {
        LUMO_PeriodicATE_Task LUMO_PeriodicATE_Task = new LUMO_PeriodicATE_Task();
        LUMO_PeriodicATE_Task.start();
        PCM_PeriodicATE_Task PCMPeriodicATE_Task = new PCM_PeriodicATE_Task();
        PCMPeriodicATE_Task.start();
        MPU_PeriodicATE_Task MPUPeriodicATE_Task = new MPU_PeriodicATE_Task();
        MPUPeriodicATE_Task.start();
        JPM_PeriodicATE_Task JPM_PeriodicATE_Task = new JPM_PeriodicATE_Task();
        JPM_PeriodicATE_Task.start();
        if(test_radios.equals("YES")) {
            IP_RADIO_PeriodicATE_Task iP_RADIO_PeriodicATE_Task_ADT;
            try {
                iP_RADIO_PeriodicATE_Task_ADT = new IP_RADIO_PeriodicATE_Task(IP_RADIO_GD_ADT, iP_RADIO_Monitor_ADT);
            } catch (UnknownHostException | InterruptedException e) { throw new RuntimeException(e); }
            iP_RADIO_PeriodicATE_Task_ADT.start();
            IP_RADIO_PeriodicATE_Task iP_RADIO_PeriodicATE_Task_GDT;
            try {
                iP_RADIO_PeriodicATE_Task_GDT = new IP_RADIO_PeriodicATE_Task(IP_RADIO_GD_GDT, iP_RADIO_Monitor_GDT);
            } catch (UnknownHostException | InterruptedException e) { throw new RuntimeException(e); }
            iP_RADIO_PeriodicATE_Task_GDT.start();
        }
    }

    private static void InitParameters() throws URISyntaxException, IOException, ParseException {
        // get the path of the JSON parameters file (under the "resources" directory
        URL res = main.class.getClassLoader().getResource("Parameters.json");
        File file = Paths.get(res.toURI()).toFile();
        String parameters_file_path = file.getAbsolutePath();
        // read in the parameters JSON object
        parameters = (JSONObject)new JSONParser().parse(new FileReader(parameters_file_path));
        // read in specific parameters
        test_radios = (String) parameters.get("TEST_RADIOS");
        jpm_ds_address_string = (String)parameters.get("JPM_DS_IP_address");
        IP_RADIO_GD_GDT.address_string = (String)parameters.get("GDT_IP_addres");
        IP_RADIO_GD_GDT.streaming_listener_port = Integer.parseInt((String)parameters.get("GDT_STREAMING_PORT"));
        IP_RADIO_GD_GDT.password = (String)parameters.get("GDT_PASSWORD");
        if(IP_RADIO_GD_GDT.password.equals("NONE")) IP_RADIO_GD_GDT.logged_in = true;
        ate_address_string = (String)parameters.get("ATE_IP_address");
        IP_RADIO_GD_ADT.address_string = jpm_ds_address_string.replace("10.", "172.");
        IP_RADIO_GD_ADT.streaming_listener_port = Integer.parseInt((String)parameters.get("ADT_STREAMING_PORT"));
        IP_RADIO_GD_ADT.password = (String)parameters.get("ADT_PASSWORD");
        if(IP_RADIO_GD_ADT.password.equals("NONE")) IP_RADIO_GD_ADT.logged_in = true;
        // an ATE IP address on the "172.20.x.x" subnet to allow streaming listeners
        ate_radio_address_string = (String)parameters.get("ATE_RADIO_IP_address");
        JSONObject radio_commands = (JSONObject) main.parameters.get("ip_radio_commands");
        IP_RADIO_GD.Log_In = (String) radio_commands.get("Log_In");
        IP_RADIO_GD.Channel_Settings_1 = (String) radio_commands.get("Channel_Settings_1");
        IP_RADIO_GD.Channel_Settings_2 = (String) radio_commands.get("Channel_Settings_2");
        IP_RADIO_GD.Channel_Settings_3 = (String) radio_commands.get("Channel_Settings_3");
        IP_RADIO_GD.Channel_Settings_4 = (String) radio_commands.get("Channel_Settings_4");
        IP_RADIO_GD.Channel_Settings_5 = (String) radio_commands.get("Channel_Settings_5");
        IP_RADIO_GD.Start_Transmit = (String) radio_commands.get("Start_Transmit");
        IP_RADIO_GD.Stop_Transmit = (String) radio_commands.get("Stop_Transmit");
        IP_RADIO_GD.Antennae_Mask = (String) radio_commands.get("Antennae_Mask");
        IP_RADIO_GD.Start_Streaming = (String) radio_commands.get("Start_Streaming");
        IP_RADIO_GD.Radio_SW_Version = (String) radio_commands.get("Radio_SW_Version");
        return;
    }

    private static void InitSerialCommPorts() {
        comPorts = SerialPort.getCommPorts();
        nports = comPorts.length;
        String[] port_names = new String[nports];
        MessageListener listeners[] = new MessageListener[nports];
        System.out.println("List of available serial comm ports:");
        for(int i=0; i<nports; i++) {
            comPorts[i].openPort();
            comPorts[i].setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            comPorts[i].setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 2000, 2000);
            listeners[i] = new MessageListener();
            comPorts[i].addDataListener(listeners[i]);
            port_names[i] = new String(comPorts[i].getSystemPortName());
            System.out.println("Serial port #" + i+",  "+comPorts[i].getDescriptivePortName()); // todo restore to service if not lan used for it
        }
        // this test program supports up to 2 ports. Manualy Identify the service and other port
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // now let the user select the serial service port and set the other, if any
                SetSerialPorts setSerialPorts = new SetSerialPorts( frame, "Set the Serial Ports", new Dimension(500,400), port_names);
            }
        });

    }

    private static void InitLAN_Port() {
        aTE_JPM_LAN=new ATE_JPM_LAN();
        aTE_JPM_LAN.start();
        //CMN_GD.ServicePort = 2; // TODO - patch for testing. Delete in real configuration
        //CMN_GD.OtherPort = CMN_GD.ServicePort; // TODO - patch for testing. Delete in real configuration
        //System.out.println("Service port was forced to LAN for debugging !!");
    }

    public static void SetLANPort() {
        // LAN socket (port) was initialized OK
        CMN_GD.LANPort = LAN_PORT_ID;
        System.out.println("LAN Port initialized OK");
    }

    public static void CopyStringToByteArray(String s, byte[] text) {
        byte[] sb = s.getBytes(StandardCharsets.UTF_8);
        int size = Math.min(text.length, s.length());
        for(int i=0; i<size; i++)
            text[i] = sb[i];
    }

    public static void SendMessage(int port_ix, Address dest_ad, Address send_ad,
                            EnDef.msg_code_ce msg_code, MessageBody body) {
        if(port_ix <0)
            return; // can't send - no valid port
        int len = MSG_FRAME_SIZE+body.GetSize();
        byte[] txbuf = new byte[len];
        LMDS_HDR LH = new LMDS_HDR();
        LH.length = len;
        LH.send_time_ms  = (int) (System.currentTimeMillis() % 86400000); // should be time from midnight;
        CMN_GD.IncrementTXnum();
        LH .serial_number =  CMN_GD.GetTXnum();
        LH.dest_address  =  GetNewAddress(dest_ad.host_name, dest_ad.process_name);
        LH.sender_address  =  GetNewAddress(send_ad.host_name, send_ad.process_name);
        LH.msg_code  =  msg_code;
        MsgBld MB = new MsgBld(txbuf);
        MB.Add(LH.GetBytes());
        MB.Add(body.GetBytes());
        MsgTail MT = new MsgTail();
        MT.SetChecksum(txbuf);
        MB.Add(MT.GetBytes());
        // up to 9 serial comm ports supported (=LAN_PORT_ID-1)
        if(port_ix < comPorts.length) {
            int return_code = comPorts[port_ix].writeBytes(txbuf, (long) len);
            if(return_code != len) {
                System.out.println("Main: Error sending to serial port: "+comPorts[port_ix].getDescriptivePortName()+"  Code="+return_code);
            }
            return;
        }
        if(port_ix == LAN_PORT_ID )
            // send to the adressee over the LAN
            aTE_JPM_LAN.Send(txbuf);

    }
    public static Address GetNewAddress(EnDef.host_name_ce host, EnDef.process_name_ce process) {
        Address address = new Address();
        address.host_name = host;
        address.process_name = process;
        return address;
    }
/*
    public static synchronized CompletableFuture<CurlHttpResponse> SendHTTPToRadio(String[] msg) {
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

    private static int ExtractResponseCode(String responseBody) {
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

    private static String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
*/
    public static synchronized String SendHTTPToRadio_With_HTTPClient(String target_radio_ip, String msg) throws ExecutionException, InterruptedException {
        String rsult=null;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(msg))
                .uri(URI.create("http://" + target_radio_ip + "/streamscape_api"))
                .setHeader("User-Agent", "ATE Server") // add request header
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            rsult = response.thenApply(HttpResponse::body).get(20, TimeUnit.SECONDS); // TODO long delay for response. Exceptions thrown at 15
        }
        catch (TimeoutException e) {
            System.out.println("main - IP Radio Control - time out waiting for a response from the radio: ");
            e.printStackTrace();
            return rsult;
        }
        // analyze the result for errors and print an error; if so, set the result to null
        if(rsult.contains("error")) {
            System.out.println("main - IP Radio Control - error response from the radio: "+rsult);
            rsult = null;
        }
        return rsult;
    }
    public static double GetDouble(String strNum, double min, double max) {
        // parse a string to get a real number in the range min to max; -999.999 returned on error
        double d = -999.999;
        if (strNum == null)  return d;
        try {
            d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return -999.999;
        }
        if(d < min || d > max)
            return -999.999;
        return d;
    }
}

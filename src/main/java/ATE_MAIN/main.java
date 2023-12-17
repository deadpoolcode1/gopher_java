package ATE_MAIN;

import ATE_GUI.GUI_CMN.SetSerialPorts;
import ATE_GUI.IP_RADIO_GUI.IP_RADIO_Monitor;
//import ATE_GUI.IP_RADIO_GUI.IP_RADIO_PeriodicATE_Task;
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
import main.java.ip_radio_interface.IP_RADIO_PeriodicATE_Task;
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
    /**
     * Main class and entry point to the ATE_SIM program.
     * The global ATE_SIM_MODE boolean flag (in CMN_GD) modifies the program behaviour to work in either ATE_SIM mode (true)
     * or GOPHER mode (false)
     */
    final static int MSG_FRAME_SIZE = 32+8, LAN_PORT_ID=10; // size of header + tail=40
    static SerialPort[] comPorts;
    static ATE_JPM_LAN aTE_JPM_LAN;
    private static HttpClient httpClient;
    public static IP_RADIO_Monitor iP_RADIO_Monitor_ADT, iP_RADIO_Monitor_GDT;
    //public static IP_RADIO_GD IP_RADIO_GD_ADT, IP_RADIO_GD_GDT;
    //public static IP_Radio_API iP_Radio_API_ADT, iP_Radio_API_GDT;
    public static ATE_Radio_Interface aTE_Interface_ADT, aTE_Interface_GDT;
    public static String jpm_ds_address_string = "", ate_address_string = "",
            test_radios = "NO", api_out_folder, adt_address_string,  gdt_address_string,
            adt_password, gdt_password, adt_ate_radio_address_string, gdt_ate_radio_address_string,
            adt_user_name="admin", gdt_user_name="admin";
    public static int adt_streaming_listener_port, gdt_streaming_listener_port;
    static JFrame frame;
    static int nports;
    public static JSONObject parameters=null;


    static public void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException, ParseException {
        InitParameters(); // read the common configuration data
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                frame = new JFrame("ATE_SIM: an LMDS Testing Application - Dec. 16, 2023");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                if (CMN_GD.ATE_SIM_MODE) {
                    JTabbedPane tabbedPane = new JTabbedPane();
                    LUMO_TestSelect lUMO_TestSelect = new LUMO_TestSelect(frame);
                    tabbedPane.addTab("LUMO Main", null, new JScrollPane(lUMO_TestSelect), "LUMO Main test controls & results");
                    LUMO_GPS_Monitor lUMO_GPS_Monitor = new LUMO_GPS_Monitor(frame);
                    tabbedPane.addTab("GPS Monitor", null, new JScrollPane(lUMO_GPS_Monitor), "Monitors the UBX GPS (using specific NAV report from the GPS)");
                    PCM_TestSelect PCM_TestSelect = new PCM_TestSelect(frame);
                    tabbedPane.addTab("PCM Main", null, new JScrollPane(PCM_TestSelect), "PCM Main test controls & results");
                    PCM_Battery_Monitor pCM_Battery_Monitor = new PCM_Battery_Monitor(frame);
                    tabbedPane.addTab("Battery Monitor", null, new JScrollPane(pCM_Battery_Monitor), "Monitors the battery (using PCM reports)");
                    PCM_Motor_Monitor pCM_Motor_Monitor = new PCM_Motor_Monitor(frame);
                    tabbedPane.addTab("Motor Monitor", null, new JScrollPane(pCM_Motor_Monitor), "Monitors the Motor (using PCM reports)");
                    MPU_TestSelect MPU_TestSelect = new MPU_TestSelect(frame);
                    tabbedPane.addTab("MPU Main", null, new JScrollPane(MPU_TestSelect), "MPU Main test controls & results");
                    MPU_Camera_Monitor mPU_Camera_Monitor = new MPU_Camera_Monitor(frame);
                    tabbedPane.addTab("Camera Monitor", null, new JScrollPane(mPU_Camera_Monitor), "Monitors the camera (using MPU reports)");
                    MPU_AHRS_Monitor mPU_AHRS_Monitor = new MPU_AHRS_Monitor(frame);
                    tabbedPane.addTab("AHRS Monitor", null, new JScrollPane(mPU_AHRS_Monitor), "Monitors the AHRS (using MPU reports)");
                    JPM_TestSelect JPM_TestSelect = new JPM_TestSelect(frame);
                    tabbedPane.addTab("JPM Main", null, new JScrollPane(JPM_TestSelect), "JPM Main test controls & results");
                    if (test_radios.equals("YES")) {
                        try {
                            iP_RADIO_Monitor_ADT = new IP_RADIO_Monitor(frame);
                        } catch (UnknownHostException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        tabbedPane.addTab("IP Radio Monitor ADT", null, new JScrollPane(iP_RADIO_Monitor_ADT), "Monitors the IP Radio Status");
                        try {
                            iP_RADIO_Monitor_GDT = new IP_RADIO_Monitor(frame);
                        } catch (UnknownHostException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        tabbedPane.addTab("IP Radio Monitor GDT", null, new JScrollPane(iP_RADIO_Monitor_GDT), "Monitors the IP Radio Status");
                        //IP_RADIO_PeriodicATE_Task iP_RADIO_PeriodicATE_Task_ADT = null;
                    }
                    frame.add(tabbedPane, BorderLayout.CENTER);
                }
                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });

        InitSerialCommPorts(); // find and set the service and other serial ports
        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
        if(CMN_GD.ATE_SIM_MODE)
            InitPeriodicalTasks();
        InitLAN_Port();
        if(! CMN_GD.ATE_SIM_MODE)
            InitGOPHER_Sender();
        //sleep for 10 hours and then exit
        try {
            Thread.sleep(1000 * 36000); } catch (Exception e) { e.printStackTrace();
        }
        // close all serial ports
        for(int i=0; i<nports; i++) {
            comPorts[i].removeDataListener();
            comPorts[i].closePort();
        }
        System.out.println("ATE_SIM Run terminated");
    }

    private static void InitGOPHER_Sender() {
        // GOPHER use
        // initalize a periodic task to read from the data base messages destined to the LMDS, decode them and send to the LMDS (see task creation example in InitLAN_Port() below)
        // the task should periodically scan the data base for out-going messages, convert them from JSON to LMDS_ICD class instance and use the "SendMessage" method to send it out
        // an out going message must define all header fields, a message body and a port
    }

    private static void InitPeriodicalTasks() throws URISyntaxException, IOException, ParseException {
        LUMO_PeriodicATE_Task LUMO_PeriodicATE_Task = new LUMO_PeriodicATE_Task();
        LUMO_PeriodicATE_Task.start();
        PCM_PeriodicATE_Task PCMPeriodicATE_Task = new PCM_PeriodicATE_Task();
        PCMPeriodicATE_Task.start();
        MPU_PeriodicATE_Task MPUPeriodicATE_Task = new MPU_PeriodicATE_Task();
        MPUPeriodicATE_Task.start();
        JPM_PeriodicATE_Task JPM_PeriodicATE_Task = new JPM_PeriodicATE_Task();
        JPM_PeriodicATE_Task.start();
        if(test_radios.equals("YES")) {
            aTE_Interface_ADT = new ATE_Radio_Interface(adt_address_string, adt_user_name, adt_password, adt_streaming_listener_port,
                    "ADT", api_out_folder, adt_ate_radio_address_string, iP_RADIO_Monitor_ADT);
            aTE_Interface_ADT.start();
            aTE_Interface_GDT = new ATE_Radio_Interface(gdt_address_string, gdt_user_name, gdt_password, gdt_streaming_listener_port,
                    "GDT", api_out_folder, gdt_ate_radio_address_string, iP_RADIO_Monitor_GDT);
            aTE_Interface_GDT.start();
        }
    }

    private static void InitParameters() throws URISyntaxException, IOException, ParseException {
        // get the path of the JSON parameters file (under the "current" directory
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("main:InitParameters - currentDirectory ="+ currentDirectory);
        parameters = (JSONObject)new JSONParser().parse(new FileReader(currentDirectory+"/Parameters.json"));
        // read in specific parameters
        api_out_folder = (String) parameters.get("API_Out_Folder");
        test_radios = (String) parameters.get("TEST_RADIOS");
        jpm_ds_address_string = (String)parameters.get("JPM_DS_IP_address");
        gdt_address_string = (String)parameters.get("GDT_IP_addres");
        gdt_streaming_listener_port = Integer.parseInt((String)parameters.get("GDT_STREAMING_PORT"));
        gdt_password = (String)parameters.get("GDT_PASSWORD");
        ate_address_string = (String)parameters.get("ATE_IP_address");
        adt_address_string = jpm_ds_address_string.replace("10.", "172.");
        adt_streaming_listener_port = Integer.parseInt((String)parameters.get("ADT_STREAMING_PORT"));
        adt_password = (String)parameters.get("ADT_PASSWORD");
        // an ATE IP address on the "172.20.x.x" subnet to allow streaming listeners
        adt_ate_radio_address_string = (String)parameters.get("ATE_RADIO_IP_address_ADT");
        gdt_ate_radio_address_string = (String)parameters.get("ATE_RADIO_IP_address_GDT");
        // add here an init for the FileWriters object to allow for json record writes to store sent out messages recordings (GOPHER use)
        // see CMN_GD for these object declarations
        return;
    }

    private static void InitSerialCommPorts() {
        comPorts = SerialPort.getCommPorts();
        nports = Math.min(comPorts.length, 10); // allow up to 10 ports, numbered 0-9
        String[] port_names = new String[nports];
        MessageListener listeners[] = new MessageListener[nports];
        System.out.println("List of available serial comm ports:");
        for(int i=0; i<nports; i++) {
            comPorts[i].openPort();
            comPorts[i].setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            comPorts[i].setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 2000, 2000);
            listeners[i] = new MessageListener(i);
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

    public static void SendMessage(int port_ix, Address dest_ad, Address send_ad, EnDef.msg_code_ce msg_code, MessageBody body) {
        // builds a standard ATE message and sends it over the deignated port. body may be null. Other parameters must be non-null.
        int len=0;
        if(port_ix <0)
            return; // can't send - no valid port
        if(body==null)
            len = MSG_FRAME_SIZE;
        else
            len = MSG_FRAME_SIZE+body.GetSize();
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
        if(body!=null)
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
        }
        if(port_ix == LAN_PORT_ID )
            // send to the adressee over the LAN
            aTE_JPM_LAN.Send(txbuf);
        if(CMN_GD.RECORD_SENT_MSGS_IN_JSON)
            Record_Out_Message_In_Json(port_ix, LH, body);
    }

    private static void Record_Out_Message_In_Json(int portIx, LMDS_HDR lh, MessageBody msg_body) {
        // GOPHER use - add code to record in Json sent out messages
    }

    public static Address GetNewAddress(EnDef.host_name_ce host, EnDef.process_name_ce process) {
        Address address = new Address();
        address.host_name = host;
        address.process_name = process;
        return address;
    }
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

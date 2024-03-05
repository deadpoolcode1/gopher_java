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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import main.java.ip_radio_interface.IP_RADIO_PeriodicATE_Task;
import org.jfree.chart.ChartPanel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import com.yourcompany.app.App;
import com.yourcompany.app.Database;
import com.yourcompany.app.MConfig;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            adt_user_name="admin", gdt_user_name="admin", GOPHER_Json_out_folder_string = "";
    public static int adt_streaming_listener_port, gdt_streaming_listener_port;
    public static To_LMDS_Json_Recorder to_LMDS_Json_Recorder;
    static JFrame frame;
    static int nports;
    public static JSONObject parameters=null;
    // GOPHER parameters
    public static String dbServer = null;
    public static String dbUsername = null;
    public static String dbPassword = null;
    public static boolean fakeDatabase = false;
    public static String txGopherFile = null;
    public static int txGopherLine = 0;

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                argMap.put(args[i].substring(2), args[i + 1]);
            }
        }
        return argMap;
    }

    
    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws URISyntaxException
     * @throws ParseException
     */
    static public void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException, ParseException {
        InitParameters(); // read the common configuration data
        if (!CMN_GD.ATE_SIM_MODE) {
            Map<String, String> argMap = parseArguments(args);


            MConfig.setAllParameters(dbServer, dbUsername, dbPassword, fakeDatabase);
            MConfig.initialize(dbServer, dbUsername, dbPassword, fakeDatabase);

            if (!fakeDatabase) 
            {
                if (!App.unittest()) {
                    System.exit(1); // Exit if the unit test fails
                }
            }
        }
        
        if (CMN_GD.ATE_SIM_MODE) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    frame = new JFrame("ATE_SIM: an LMDS Testing Application - Jan. 1, 2024");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                    frame.pack();
                    frame.setVisible(true); //Display the window.
                }
            });
        }
        InitSerialCommPorts(); // find and set the service and other serial ports
        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
        if(CMN_GD.ATE_SIM_MODE)
            InitPeriodicalTasks();
        InitLAN_Port();
        //SF to_LMDS_Json_Recorder moved here; must be done before calling InitGOPHER_Sender
        if(CMN_GD.RECORD_SENT_MSGS_IN_JSON) {
            to_LMDS_Json_Recorder = new To_LMDS_Json_Recorder(GOPHER_Json_out_folder_string);
        }
        if (!CMN_GD.ATE_SIM_MODE && txGopherFile != null && !txGopherFile.isEmpty()) {
            File file = new File(txGopherFile);
            if (file.exists() && !file.isDirectory()) {
                // File exists and is not a directory; proceed with simulation
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String lineData;
                    while ((lineData = reader.readLine()) != null) {
                        JsonObject jsonObj = new Gson().fromJson(lineData, JsonObject.class);
                        int lineIndex = jsonObj.get("line_index").getAsInt();
                        InitGOPHER_Sender(true, txGopherFile, lineIndex);
        
                        // Delay for 10 seconds before processing the next line
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // restore interrupted status
                            throw new RuntimeException("Thread interrupted while processing file", ie);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error processing simulation file: " + e.getMessage());
                }
            } else {
                System.err.println("Simulation file not found or is a directory: " + txGopherFile);
            }
        } 
        else if(! CMN_GD.ATE_SIM_MODE && !fakeDatabase)
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

    private static String readLineFromFile(String filePath, int lineNum) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.skip(lineNum).findFirst().orElse(null);
        }
    }

    private static void InitGOPHER_Sender() throws FileNotFoundException { //SF
        InitGOPHER_Sender(false, null, -1);
    }

    private static void InitGOPHER_Sender(boolean simulate, String filePath, int fileLine) throws FileNotFoundException {
        Gson gson = new Gson();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    
        Runnable task = () -> {
            try {
                if (simulate && filePath != null && fileLine >= 0) {
                    // Simulation mode: Read a specific line from the file and simulate sending a message
                    String lineData = readLineFromFile(filePath, fileLine);
                    JsonObject jsonObj = gson.fromJson(lineData, JsonObject.class);
                    processAndSendMessage(gson, jsonObj);
                } else {
                    // Regular operation: Query the database for outgoing messages
                    String query = "SELECT id, port_index, dest_host_name, dest_process_name, send_host_name, send_process_name, msg_code, body_content FROM write_data WHERE request_pending = 1";
                    List<String[]> messages = Database.executeQueryMulti("my_data", query, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
                    for (String[] dataParts : messages) {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.add("MESSAGE_BODY", gson.fromJson(dataParts[7], JsonObject.class));
    
                        processAndSendMessage(gson, jsonObj);
    
                        int id = Integer.parseInt(dataParts[0]);
                        String updateQuery = "UPDATE write_data SET request_pending = 0 WHERE id = " + id;
                        Database.executeNonQuery("my_data", updateQuery, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    
        if (simulate) {
            executorService.submit(task);
        } else {
            executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        }
    }
    
    private static void processAndSendMessage(Gson gson, JsonObject jsonObj) {
    try {
// Access the "port_ix" within the "MBDY" object now
int portIndex = jsonObj.getAsJsonObject("MESSAGE_BODY").get("port_ix").getAsInt();

// The "LMH" object is still within "MBDY", adjust the path accordingly
EnDef.host_name_ce destHostName = EnDef.host_name_ce.valueOf(
    jsonObj.getAsJsonObject("MESSAGE_BODY")
        .getAsJsonObject("LMH")
        .getAsJsonObject("dest_address")
        .get("host_name").getAsString());

EnDef.process_name_ce destProcessName = EnDef.process_name_ce.valueOf(
    jsonObj.getAsJsonObject("MESSAGE_BODY")
        .getAsJsonObject("LMH")
        .getAsJsonObject("dest_address")
        .get("process_name").getAsString());

EnDef.host_name_ce sendHostName = EnDef.host_name_ce.valueOf(
    jsonObj.getAsJsonObject("MESSAGE_BODY")
        .getAsJsonObject("LMH")
        .getAsJsonObject("sender_address")
        .get("host_name").getAsString());

EnDef.process_name_ce sendProcessName = EnDef.process_name_ce.valueOf(
    jsonObj.getAsJsonObject("MESSAGE_BODY")
        .getAsJsonObject("LMH")
        .getAsJsonObject("sender_address")
        .get("process_name").getAsString());

// "msg_code" is still directly under "LMH", so adjust the path but the retrieval remains the same
EnDef.msg_code_ce msgCode = EnDef.msg_code_ce.valueOf(
    jsonObj.getAsJsonObject("MESSAGE_BODY")
        .getAsJsonObject("LMH")
        .get("msg_code").getAsString());
    
        Address destAddress = new Address();
        destAddress.host_name = destHostName;
        destAddress.process_name = destProcessName;
    
        Address sendAddress = new Address();
        sendAddress.host_name = sendHostName;
        sendAddress.process_name = sendProcessName;
    
        Object body_class = GetBodyClassByMsgCode(msgCode);
    // Print the JSON string that is about to be parsed
    System.out.println("Parsing JSON for MessageBody: " + jsonObj.getAsJsonObject("MESSAGE_BODY").toString());

    // Attempt to parse the JSON to the specified body_class
    body_class = gson.fromJson(jsonObj.getAsJsonObject("MESSAGE_BODY").getAsJsonObject("MBDY"), (Type) body_class);

    // If parsing is successful, print a success message and the parsed object (optional)
    System.out.println("Parsing successful. Parsed MessageBody: " + body_class.toString());

        SendMessage(portIndex, destAddress, sendAddress, msgCode, (MessageBody)body_class);
    } catch (IOException e) {
        e.printStackTrace();
        }
    }
    
    private static JsonObject createAddressJson(String destHostName, String destProcessName, String sendHostName, String sendProcessName) {
        JsonObject lmh = new JsonObject();
        JsonObject destAddress = new JsonObject();
        destAddress.addProperty("host_name", destHostName);
        destAddress.addProperty("process_name", destProcessName);
        JsonObject sendAddress = new JsonObject();
        sendAddress.addProperty("host_name", sendHostName);
        sendAddress.addProperty("process_name", sendProcessName);
        lmh.add("dest_address", destAddress);
        lmh.add("sender_address", sendAddress);
        return lmh;
    }

    private static Object GetBodyClassByMsgCode(EnDef.msg_code_ce msgCode) {
        //SF
        switch(msgCode) {
            case MSG_CODE_DS_CNTRL:             return  ds_cntrl.class;
            case MSG_CODE_EO_CNTRL:             return  LM_Camera_Control.class;
            case MSG_CODE_PRPLSN_CNTRL:         return  LM_Propulsion_Control.class;
            case MSG_CODE_SER_COMM_TST:         return  srial_comms_tst.class;
            case MSG_CODE_PWM_CNTRL_STTS:       return  pwm_cntrl_stts.class;
            case MSG_CODE_DSCRT_CNTRL:          return  dscrt_cntrl.class;
            case MSG_CODE_GET_DS_REVSN:         return  null; // only message code is sent
            case MSG_CODE_WRITE_GET_CNFG_PARAM: return  config_param.class;
            case MSG_CODE_SAVE_ALL_CNFG_PARAMS: return  null; // only message code is sent
            case MSG_CODE_READ_CNFG_PARAM:      return  read_config_param.class;
            default: return null;
        }
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
        GOPHER_Json_out_folder_string = (String)parameters.get("GOPHER_Json_out_folder");
        // add here an init for the FileWriters object to allow for json record writes to store sent out messages recordings (GOPHER use)
        // see CMN_GD for these object declarations
        if (! CMN_GD.ATE_SIM_MODE) { //SF added !
            dbServer = (String) parameters.get("dbServer");
            dbUsername = (String) parameters.get("dbUsername");
            dbPassword = (String) parameters.get("dbPassword");
            fakeDatabase = (boolean) parameters.get("fakeDatabase");
            txGopherFile = (String) parameters.get("txGopherFile");
            txGopherLine = Integer.parseInt((String) parameters.get("txGopherLine")); //SF fixed bug
        }
        return;
    }

    private static int getBaudRateForPort(String portName) {
        String query = "SELECT BaudRate FROM Comm WHERE NAME LIKE '%" + portName + "%';";
        List<String> baudRateData = Database.executeQuery("my_data", query, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
        
        if (!baudRateData.isEmpty()) {
            try {
                return Integer.parseInt(baudRateData.get(0).trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid baud rate format for port " + portName);
                return -1; // Indicate error or invalid format
            }
        } else {
            return -1; // Indicate not found
        }
    }
    
    private static int getDataBitsForPort(String portName) {
        String query = "SELECT Data_Bits FROM Comm WHERE NAME LIKE '%" + portName + "%';";
        List<String> dataBitsData = Database.executeQuery("my_data", query, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
        
        if (!dataBitsData.isEmpty()) {
            try {
                return Integer.parseInt(dataBitsData.get(0).trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid data bits format for port " + portName);
                return -1; // Indicate error or invalid format
            }
        } else {
            return -1; // Indicate not found
        }
    }
    

    
    private static int getParityForPort(String portName) {
        String query = "SELECT Parity FROM Comm WHERE NAME LIKE '%" + portName + "%';";
        List<String> parityData = Database.executeQuery("my_data", query, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
        
        if (!parityData.isEmpty()) {
            String parity = parityData.get(0).trim().toUpperCase(); // Adjust here for case insensitivity and trim spaces
            switch (parity) {
                case "NONE":
                    return SerialPort.NO_PARITY;
                case "EVEN":
                    return SerialPort.EVEN_PARITY;
                case "ODD":
                    return SerialPort.ODD_PARITY;
                case "MARK":
                    return SerialPort.MARK_PARITY;
                case "SPACE":
                    return SerialPort.SPACE_PARITY;
                default:
                    System.err.println("Unknown parity setting for port " + portName + ": " + parity);
                    return -1;
            }
        } else {
            return -1; // Indicate not found
        }
    }
    

    private static int getStopBitsForPort(String portName) {
        String query = "SELECT Stop_Bits FROM Comm WHERE NAME LIKE '%" + portName + "%';";
        List<String> stopBitsData = Database.executeQuery("my_data", query, MConfig.getDBServer(), MConfig.getDBUsername(), MConfig.getDBPassword());
        
        if (!stopBitsData.isEmpty()) {
            String stopBits = stopBitsData.get(0).trim(); // Trim spaces
            switch (stopBits) {
                case "1":
                    return SerialPort.ONE_STOP_BIT;
                case "1.5":
                    return SerialPort.ONE_POINT_FIVE_STOP_BITS;
                case "2":
                    return SerialPort.TWO_STOP_BITS;
                default:
                    System.err.println("Unknown stop bits setting for port " + portName + ": " + stopBits);
                    return -1;
            }
        } else {
            return -1; // Indicate not found
        }
    }
    

    private static void InitSerialCommPorts() {
        comPorts = SerialPort.getCommPorts();
        nports = Math.min(comPorts.length, 30); // allow up to 10 ports, numbered 0-9
        String[] port_names = new String[nports];
        MessageListener[] listeners = new MessageListener[nports];
        System.out.println("List of available serial comm ports:");
        String comPort = "";
        for(int i=0; i<nports; i++) {
            String portName = comPorts[i].getDescriptivePortName();
            Pattern pattern = Pattern.compile("COM\\d{1,2}");
            Matcher matcher = pattern.matcher(portName);
            if (matcher.find()) {
                comPort = matcher.group(); // This will give you "COM16" for the example
                // Continue with your database query using this comPort value
            }
            // Fetch baud rate from the database for the current port
                int baudRate = getBaudRateForPort(comPort);
                int dataBits = getDataBitsForPort(comPort);
                int parity = getParityForPort(comPort);
                int stopBits = getStopBitsForPort(comPort);
           if (!(baudRate != -1 && dataBits != -1 && parity != -1 && stopBits != -1)) {
                System.out.println("Skipping " + comPort + " as it is not configured in the database.");
                continue;
            }
            else {
                System.out.println("opening " + comPort + " as it is configured in the database.");
            }


            if(comPorts[i].openPort()) {
                comPorts[i].setComPortParameters(baudRate, dataBits, stopBits, parity);
                comPorts[i].setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 2000, 2000);
                listeners[i] = new MessageListener(i);
                comPorts[i].addDataListener(listeners[i]);
                port_names[i] = comPort;
                System.out.println("Configured serial port #" + i + ", " + comPorts[i].getDescriptivePortName() + " with baud rate: " + baudRate);
            } else {
                System.out.println("Failed to open serial port " + portName);
            }
        }
        if (CMN_GD.ATE_SIM_MODE) {
            // this test program supports up to 2 ports. Manualy Identify the service and other port
            // when in GOPHER mode, the port on which each message to the LMDS has to be sent is commanded by the ATE.
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // now let the user select the serial service port and set the other, if any
                    SetSerialPorts setSerialPorts = new SetSerialPorts(frame, "Set the Serial Ports", new Dimension(500, 400), port_names);
                }
            });
        }
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

    public static void SendMessage
            (int port_ix, Address dest_ad, Address send_ad, EnDef.msg_code_ce msg_code, MessageBody body) throws IOException {
        // builds a standard ATE message and sends it over the deignated port. body may be null. Other parameters must be non-null.
        // when in Json out recording mode, also record the outgoing message to a Json logger
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
            to_LMDS_Json_Recorder.Add_Json_Record(port_ix, LH, body);
            //Record_Out_Message_In_Json(port_ix, LH, body);
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

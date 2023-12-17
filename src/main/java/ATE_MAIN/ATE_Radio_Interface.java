package ATE_MAIN;

import ATE_GUI.IP_RADIO_GUI.IP_RADIO_Monitor;
import main.java.ip_radio_interface.All_IP_Radio_Settings;
import main.java.ip_radio_interface.IP_Radio_Interface;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class ATE_Radio_Interface extends Thread {
    /*
    an example thread for reading messages from the IP radio Interface Object
    an example for sending to the radio may be taken from the IP_RADIO_Control class,
    the respective ADT, GDT class instances must be declared by the Main class.
    hardware / definitions setup:
    1. must have a pecific LAN port in the host computer for each radio
    2. The GDT port address must be set with a static address on the 172.20.x.x sub-net, e.g. 172.20.1.100
    3. The ADT address port must be set with a static address on the 172.20.x.x sub-net, e.g. 172.20.2.100
    4. The "Parameters.json" file must define these parameters accordingly, as well as the ADT/GDT passwords
    5. Using the windows admin powershell, define special routing for this radios, by:
         route -p add <gdt ip address> mask 255.255.255.255 <gdt port address>, when the GDT is connected to said port
         route -p add <adt ip address> mask 255.255.255.255 <adt port address>, when the ADT is connected to said port
    6. Streaming must be explicitly started manually after login to the radio (if it has a password)
    */
    FileWriter RadioResponse;
    FileWriter VOLT_Stream;
    FileWriter TEMP_Stream;
    FileWriter RSSI_Stream;
    String api_out_folder;
    public IP_Radio_Interface iP_Radio_Interface;
    int streaming_listener_port=-1;

    All_IP_Radio_Settings AIRS;
    IP_RADIO_Monitor iP_RADIO_Monitor;
    long time_tick = 0;
    public ATE_Radio_Interface(String radio_address_string, String user_name, String password, int streaming_listener_port, String radio_id,
                               String api_out_folder, String ATE_ip_address_string, IP_RADIO_Monitor iP_RADIO_Monitor) throws URISyntaxException, IOException, ParseException {
        this.api_out_folder = api_out_folder;
        this.streaming_listener_port = streaming_listener_port;
        this.iP_RADIO_Monitor = iP_RADIO_Monitor;
        iP_RADIO_Monitor.SetRadioInterface(this); // connect the radio monitor to this ATE radio interface
        // get the path of the JSON parameters file (under the "resources" directory
        //URL res = ATE_Radio_Interface.class.getClassLoader().getResource("IP_Radio_Parameters.json");
        //File file = Paths.get(res.toURI()).toFile();
        //String parameters_file_path = file.getAbsolutePath();
        String currentDirectory = System.getProperty("user.dir");
        iP_Radio_Interface = new IP_Radio_Interface(radio_address_string, user_name,
                password, ATE_ip_address_string, streaming_listener_port,
                currentDirectory+"/IP_Radio_Parameters.json");
        try {
            RadioResponse = new FileWriter(api_out_folder+"/RadioResponse"+"_"+radio_id+".csv", false) ;
            RadioResponse.write("sent msg id, txnum,response code,body\n");
            RadioResponse.flush();
            VOLT_Stream = new FileWriter(api_out_folder+"/VOLT_Stream"+"_"+radio_id+".csv", false) ;
            VOLT_Stream.write("current_volt,min_volt,max_volt, under_volt_count,over_volt_count\n");
            VOLT_Stream.flush();
            TEMP_Stream = new FileWriter(api_out_folder+"/TEMP_Stream"+"_"+radio_id+".csv", false) ;
            TEMP_Stream.write("current_temp,max_temp,overheat_count,node_ip\n");
            TEMP_Stream.flush();
            RSSI_Stream = new FileWriter(api_out_folder+"/RSSI_Stream"+"_"+radio_id+".csv", false) ;
            RSSI_Stream.write("ant_1_raw_rssi,ant_2_raw_rssi,ant_3_raw_rssi,ant_4_raw_rssi,raw_noise_pwr,sync_signal_pwr,sync_noise_pwr,node_ip\n");
            RSSI_Stream.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void run() {
        String[] msg;
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) { e.printStackTrace(); }
            // ************ 5 Hz tasks **************
            // fetch all waiting RX messages from the radio, write them to respective .csv files
            while((msg=iP_Radio_Interface.GetIP_Radio_Message()) != null){
                try {
                    switch(msg[0]) {
                        case "response": RadioResponse.write(msg[1]+"\n"); RadioResponse.flush(); break;
                        case "VOLT_Report": VOLT_Stream.write(msg[1]+"\n"); VOLT_Stream.flush(); UpdateVOLT_Graph(msg[1]); break;
                        case "TEMP_Report": TEMP_Stream.write(msg[1]+"\n"); TEMP_Stream.flush(); UpdateTEMP_Graph(msg[1]);break;
                        case "RSSI_Report": RSSI_Stream.write(msg[1]+"\n"); RSSI_Stream.flush(); UpdateRSSI_Graph(msg[1]); UpdateSYNC_Graph(msg[1]);break;
                        default:
                            System.out.println("ATE_MAIN.ATE_Interface: illegal RX message ID. Ignored." + msg[0]);
                    }
                } catch (IOException e) { throw new RuntimeException(e); }

            }
            // ************ 0.2 Hz tasks **************
            if(time_tick%5000==0) {
                // get a reference to current full radio status. Take care - just read it, DO NOT modify !!
                AIRS = iP_Radio_Interface.Get_IP_Radio_Status();
                iP_RADIO_Monitor.ShowData(AIRS);
            }
            time_tick+=200;
        }
    }

    private void UpdateSYNC_Graph(String report) {
        long time_now = System.currentTimeMillis();
        String[] sval = report.split(",");
        double sync_signal_pwr = Double.parseDouble(sval[5])/1000;
        double sync_noise_pwr = Double.parseDouble(sval[6])/1000;
        iP_RADIO_Monitor.iP_RADIO_Sync_Chart.UpdateDataset((int) (time_now % 86400000), sync_signal_pwr, sync_noise_pwr);
    }

    private void UpdateRSSI_Graph(String report) {
        long time_now = System.currentTimeMillis();
        String[] sval = report.split(",");
        double ant_1_raw_rssi = Double.parseDouble(sval[0]);
        double ant_2_raw_rssi = Double.parseDouble(sval[1]);
        double ant_3_raw_rssi = Double.parseDouble(sval[2]);
        double ant_4_raw_rssi = Double.parseDouble(sval[3]);
        double raw_noise_pwr = Double.parseDouble(sval[4]);
        iP_RADIO_Monitor.iP_RADIO_RSSI_Chart.UpdateDataset((int) (time_now % 86400000),
                ant_1_raw_rssi, ant_2_raw_rssi, ant_3_raw_rssi, ant_4_raw_rssi, raw_noise_pwr);
    }

    private void UpdateTEMP_Graph(String report) {
        long time_now = System.currentTimeMillis();
        String[] sval = report.split(",");
        double current_temp = Double.parseDouble(sval[0])/1000;
        double max_temp = Double.parseDouble(sval[1])/1000;
        iP_RADIO_Monitor.iP_RADIO_TEMP_Chart.UpdateDataset((int) (time_now % 86400000), current_temp, max_temp);
    }

    private void UpdateVOLT_Graph(String report) {
        long time_now = System.currentTimeMillis();
        String[] sval = report.split(",");
        double current_volt = Double.parseDouble(sval[0])/1000;
        double min_volt = Double.parseDouble(sval[1])/1000;
        double max_volt = Double.parseDouble(sval[2])/1000;
        iP_RADIO_Monitor.iP_RADIO_VOLT_Chart.UpdateDataset((int) (time_now % 86400000), current_volt, min_volt, max_volt);
    }
}
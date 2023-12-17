package ATE_MAIN;

import LMDS_ICD.LMDS_HDR;
import LMDS_ICD.MsgRdr;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class ATE_JPM_LAN extends Thread {
    static int jpm_ds_port = 56432, ate_port = 56433, bfr_length = 256;
    //static String jpm_ds_address_string = "127.0.0.1", ate_address_string = "127.0.0.1";
    //static String jpm_ds_address_string = "10.20.85.3", ate_address_string = "10.20.100.1";
    //static String parameters_file_path = "";
            //"C:\\Users\\shaif\\Documents\\SF Backup\\MSF Docs\\Programming\\JAVA_workspace\\LUMO_ATE_2\\src\\main\\java\\Config\\";
    static DatagramSocket socket=null;
    static InetAddress jpm_ds_address, ate_address;
    static boolean terminate = false;
    long wait_delay = 100;

    public void run() {
        try {
            /*
            // get the path of the JSON parameters file (under the "resources" directory
            URL res = this.getClass().getClassLoader().getResource("Parameters.json");
            File file = Paths.get(res.toURI()).toFile();
            String parameters_file_path = file.getAbsolutePath();
            // read in the parameters JSON object
            JSONObject parameters = (JSONObject)new JSONParser().parse(new FileReader(parameters_file_path));
            */
            jpm_ds_port = Integer.parseInt((String)main.parameters.get("JPM_DS_IP_Port"));
            ate_port = Integer.parseInt((String)main.parameters.get("ATE_IP_Port"));
            //jpm_ds_address_string = (String)main.parameters.get("JPM_DS_IP_address");
            //ate_address_string = (String)main.parameters.get("ATE_IP_address");
            // open a LAN socket
            socket = new DatagramSocket(ate_port);
            socket.setSoTimeout(1); // the RX shall wait up to 1 MS for new messages, preempt, wait and try again
            jpm_ds_address = InetAddress.getByAddress(IP_String_To_Binary(main.jpm_ds_address_string));
            ate_address = InetAddress.getByAddress(IP_String_To_Binary(main.ate_address_string));;
            main.SetLANPort(); // indicate successful LAN socket initialization
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(! terminate) {
            DatagramPacket packet = new DatagramPacket(new byte[bfr_length], bfr_length);
            try {
                socket.receive(packet); // block until Socket wait times out or a message received
                byte[] msg_body = packet.getData();
                if(JPM_GD.LAN_Active) // process a LAN input only if the LAN state flag is active
                    DecodeAndProcessMsg(msg_body);
            }
            catch (InterruptedIOException e) {
                // RX timed out; wait 100 ms before trying again
                try {
                    Thread.sleep(wait_delay); // wait  before polling again
                } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        socket.close();
    }

    private byte[] IP_String_To_Binary(String address_string) {
        String[] tokens = address_string.split("\\.");
        byte[] bin = new byte[4];
        bin[0] = (byte) Integer.parseInt(tokens[0]);
        bin[1] = (byte) Integer.parseInt(tokens[1]);
        bin[2] = (byte) Integer.parseInt(tokens[2]);
        bin[3] = (byte) Integer.parseInt(tokens[3]);
        return bin;
    }
    public void Terminate() {
        terminate = true;
    }
    public static boolean Send(byte[] msg_body) {
        if(socket == null || (!JPM_GD.LAN_Active))
            return false;
        DatagramPacket packet = new DatagramPacket(msg_body, msg_body.length, jpm_ds_address, jpm_ds_port);
        try {
            socket.send(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    void DecodeAndProcessMsg(byte[] msg) {
        String port_name = "LAN Port";
        int ml = msg.length;
        if (ml > bfr_length) {
            System.out.println(port_name + " LAN RX" + "Received msg too large, ignored. Length: " + msg.length);
        } else {
            MsgRdr MR = new MsgRdr(msg); // set for incoming msg serialization
            LMDS_HDR LM = new LMDS_HDR(MR); // read the header
            if (LM.length > ml) {
                System.out.println("MessageListener: "+port_name + " RX" +
                        "  Received msg length in header < buffer length. HDR Length=" + LM.length + "  RX Buffer length=" + ml);
            } else {
                MsgTail MT = new MsgTail(msg, LM.length - 8);
                if (MT.IsGoodChecksum(msg, LM.length)) {
                    if(CMN_GD.ATE_SIM_MODE)
                        MessageListener.Process_TF4_Msgs(MR,LM,msg,port_name);
                    else
                        // GOPHER usage
                        Decode_Msg_Into_Json_String(main.LAN_PORT_ID, MR, LM, msg);
                }
                else {
                    System.out.println(port_name + " RX " + "Received msg has bad checksum. ignored.");
                }
            }
        }
    }
    private void Decode_Msg_Into_Json_String(int portIndex, MsgRdr mr, LMDS_HDR lm, byte[] msg) {
        // GOPHER use
        // select the file writer for this port IAW with the port index
        // convert the lm header class instance to a json string and save it to the file
        // decode the message into its LMDS_ICD class instance using mr, msg
        // convert the class instance to a json string nd save it to the file
        // consider error conditions
    }


}

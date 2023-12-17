package ATE_MAIN;

import LMDS_ICD.EnDef;

import java.io.FileWriter;

public class CMN_GD {
    public static boolean ATE_SIM_MODE = true; // if set to false, many functions shall perform in a "GOPHER" mode
    public static boolean RECORD_SENT_MSGS_IN_JSON = false; // when true, shall record any out-going message to a file as a json string
    public static int ServicePort=-1, OtherPort=-1, LANPort=-1; // init to invalid port ids
    static int TXnum=0;
    public static String comm_channel_name;
    public static double comm_n_tx, comm_n_rx;
    public static int GetTXnum() { return TXnum;}
    public static void IncrementTXnum() { TXnum++;}
    public enum UUT_ID {UUT_UNKNOWN, UUT_LUMO, UUT_PCM, UUT_CBU};
    public static UUT_ID uut_id = UUT_ID.UUT_UNKNOWN;
    // GOPHER use; file writer objects to save json records of incoming messages per port
    // use FileWriter[0-9] for serial ports, FileWriter[10] for the LAN port
    public static FileWriter[] JsonFiles = new FileWriter[11];
}

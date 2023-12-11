package ATE_MAIN;

import LMDS_ICD.EnDef;

public class CMN_GD {
    public static EnDef.lmds_state_ce UUT_state = EnDef.lmds_state_ce.LMDS_ST_UNKNOWN;
    public static EnDef.UUT_cmnd_rspns_ce response_type = EnDef.UUT_cmnd_rspns_ce.UUT_CMND_RSPNS__UNKNOWN;
    public static String response_text="None yet...";
    public static int response_num;
    public static int ServicePort=-1, OtherPort=-1, LANPort=-1; // init to invalid port ids
    static int TXnum=0;
    public static String comm_channel_name;
    public static double comm_n_tx, comm_n_rx;
    //public static boolean comm_ports_valid[] = {false, false, false};

    public static int GetTXnum() { return TXnum;}
    public static void IncrementTXnum() { TXnum++;}
    public enum UUT_ID {UUT_UNKNOWN, UUT_LUMO, UUT_PCM, UUT_CBU};
    public static UUT_ID uut_id = UUT_ID.UUT_UNKNOWN;
}

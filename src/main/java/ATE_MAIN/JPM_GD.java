package ATE_MAIN;

import LMDS_ICD.EnDef;
import org.jfree.data.xy.XYSeries;

public class JPM_GD {
    public static XYSeries Temp0=null, Temp1=null, Temp2=null, Temp3=null;
    public static int
    MCU_HW_RST_INHIBIT,
    VPU_GP1,
    VPU_GP2,
    VPU_GP3,
    VPU_GP4,
    GPIO11, // just for testing!
    GPIO12; // just for testing!
    // this flag shall enable simulation of a LAN failure if false
    public static boolean LAN_Active = true;
    public static EnDef.lmds_state_ce UUT_state = EnDef.lmds_state_ce.LMDS_ST_UNKNOWN;
    public static EnDef.UUT_cmnd_rspns_ce response_type = EnDef.UUT_cmnd_rspns_ce.UUT_CMND_RSPNS__UNKNOWN;
    public static String response_text="None yet...";
    public static int response_num;
    public static String revision="";


}

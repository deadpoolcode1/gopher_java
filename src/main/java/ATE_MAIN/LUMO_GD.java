package ATE_MAIN;

import LMDS_ICD.EnDef;
import LMDS_ICD.GPS_UBX_Data;
import org.jfree.data.xy.XYSeries;

public class LUMO_GD {
    public static XYSeries LUMO_12V_Main=null;
    public static XYSeries LUMO_12V_ESAD=null;
    public static XYSeries LUMO_33V=null;
    public static XYSeries LUMO_5V=null;
    public static XYSeries LUMO_55V=null;
    public static XYSeries AirSpeed=null;
    public static XYSeries Altitude=null;
    public static XYSeries Temp1=null, Temp2=null, Temp3=null;
    public static int dip_switch=7, umbili_init=1, NG_ESAD_Status=0;
    // LUMO flash configuration params
    public static int pitot_gain, pitot_offset, alt_gain, alt_offset, para_close, para_open,para_rls,
    av_fuze_comb, cmra_brsit_pitch_offset, cmra_brsit_yaw_offset;
    //public static double on_board_temp, ext_temp1, ext_temp2;
    public static GPS_UBX_Data gPS_UBX_Data= new GPS_UBX_Data();
    public static EnDef.lmds_state_ce UUT_state = EnDef.lmds_state_ce.LMDS_ST_UNKNOWN;
    public static EnDef.UUT_cmnd_rspns_ce response_type = EnDef.UUT_cmnd_rspns_ce.UUT_CMND_RSPNS__UNKNOWN;
    public static String response_text="None yet...";
    public static int response_num;
    public static String revision="";


}

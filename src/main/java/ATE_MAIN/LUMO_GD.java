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
    //public static double on_board_temp, ext_temp1, ext_temp2;
    public static GPS_UBX_Data gPS_UBX_Data= new GPS_UBX_Data();

}

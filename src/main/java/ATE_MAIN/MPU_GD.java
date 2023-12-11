package ATE_MAIN;

import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Camera_Status;
import LMDS_ICD.Navigation_Data;
import org.jfree.data.xy.XYSeries;

public class MPU_GD {
    public static XYSeries MPU_1V2 = null;
    public static XYSeries MPU_1V8=null;
    public static XYSeries MPU_2V5=null;
    public static XYSeries MPU_5V=null;
    public static XYSeries MPU_8V2=null;
    public static XYSeries MPU_12_MAIN=null;
    public static XYSeries Altitude=null;
    public static XYSeries Temp1=null, Temp2=null, Temp3=null;
    public static int dscrt_in_MCU_HW_RST, dscrt_in_MPU_GPS_1PPS,
            dscrt_in_DISC_IN4_CON, dscrt_in_DISC_IN5_CON,
            dscrt_in_JPM_FORCE_OFF_OUT_3V3;
    public static String ICTR =""; // internal comms test results string
    public static Navigation_Data NAVD = new Navigation_Data();
    public static LM_Camera_Status LMCS = new LM_Camera_Status();

}

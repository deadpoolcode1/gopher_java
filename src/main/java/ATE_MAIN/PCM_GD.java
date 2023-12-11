package ATE_MAIN;

import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Propulsion_Status;
import org.jfree.data.xy.XYSeries;

public class PCM_GD {
    public static XYSeries PCM_12V=null;
    public static XYSeries PCM_8_2V=null;
    public static XYSeries PCM_BATV=null;
    public static XYSeries PCM_5V=null;
    public static XYSeries Motor_RPM=null;
    public static XYSeries Temp1=null, Temp2=null, Temp3=null;
    public static int dscrt_in_ack_fire, dscrt_in_LAUNCH_MICRO_SW, dscrt_in_ACK_8_2_OK, dscrt_in_PAT_PB, dscrt_in_LAUNCH_RS,
            dscrt_in_LAUNCH_PB, dscrt_in_SW_BIT_OK, dscrt_in_CPLD_DONE, dscrt_in_CUT_OFF;
    public static double on_board_temp, ext_temp1, ext_temp2, bat_internal_temp, bat_voltage, bat_current, motor_rpm;
    public static boolean bat_monitor_comm=false, bat_hi=false,bat_lo=false,bat_chg_inh=false;
    public static LM_Propulsion_Status lM_Propulsion_Status = new LM_Propulsion_Status();
}

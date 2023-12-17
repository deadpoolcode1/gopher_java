package ATE_GUI.PCM_GUI;

import ATE_GUI.JPM_GUI.JPM_TestSelect;
import ATE_MAIN.JPM_GD;
import ATE_MAIN.PCM_GD;
import ATE_MAIN.CMN_GD;
import LMDS_ICD.EnDef;

import javax.swing.*;

public class PCM_PeriodicATE_Task extends Thread {
    int time_tick=4000000;
    public void run(){
        while(true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    if(PCM_TestSelect.init_done) {
                        PCM_TestSelect.message_line.setText("message_line: "+
                                "Response# " + PCM_GD.response_num + "  " + PCM_GD.response_type + "  " + PCM_GD.response_text);
                        PCM_TestSelect.serial_test_line.setText( "serial_test_line: Port="+ CMN_GD.comm_channel_name+
                                "  TX="+ CMN_GD.comm_n_tx+"  RX="+ CMN_GD.comm_n_rx+"  RX/TX="+ CMN_GD.comm_n_rx/ CMN_GD.comm_n_tx);
                        PCM_TestSelect.in_discrete_line.setText("in_discrete_line: "+
                        "ackfire="+PCM_GD.dscrt_in_ack_fire+" L_MSW="+PCM_GD.dscrt_in_LAUNCH_MICRO_SW+
                        " ACK_8_2_OK="+PCM_GD.dscrt_in_ACK_8_2_OK +" PAT_PB="+PCM_GD.dscrt_in_PAT_PB+
                        " LAUNCH_RS="+PCM_GD.dscrt_in_LAUNCH_RS +" SW_BIT_OK="+PCM_GD.dscrt_in_SW_BIT_OK+
                        " LAUNCH_PB="+PCM_GD.dscrt_in_LAUNCH_PB +" CPLD_DONE="+PCM_GD.dscrt_in_CPLD_DONE+
                        " CUT_OFF="+PCM_GD.dscrt_in_CUT_OFF);
                    }
                    PCM_TestSelect.revision_state_line.setText(
                            "revision_state_line: State="+ PCM_GD.UUT_state +
                                    "  Revision: "+PCM_GD.revision
                    );
                    if(PCM_Battery_Monitor.init_done) {
                        // (double bat_internal_temp, double bat_voltage, double bat_current, boolean bat_monitor_comm, boolean bat_hi, boolean bat_lo, boolean bat_chg_inh )
                        PCM_Battery_Monitor.ShowData(PCM_GD.bat_internal_temp, PCM_GD.bat_voltage, PCM_GD.bat_current,
                                PCM_GD.bat_monitor_comm, PCM_GD.bat_hi, PCM_GD.bat_lo, PCM_GD.bat_chg_inh);
                    }
                    if(PCM_Motor_Monitor.init_done) {
                            //(EnDef.Motor_state_ce motor_state, double RPM, EnDef.Serviceability_ce motor_srv, int[] faults_list)
                        PCM_Motor_Monitor.ShowData(PCM_GD.lM_Propulsion_Status);
                    }
                }
            });
            time_tick+=200;
        }
    }

}

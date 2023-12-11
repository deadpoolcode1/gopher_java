package ATE_GUI.PCM_GUI;

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
                        PCM_TestSelect.message_line.setText(
                                "Response# " + CMN_GD.response_num + "  " + CMN_GD.response_type + "  " + CMN_GD.response_text);

                        //TestSelect.sinXY.UpdateDataset1();
                        PCM_TestSelect.serial_test_line.setText( "Port="+ CMN_GD.comm_channel_name+
                                "  TX="+ CMN_GD.comm_n_tx+"  RX="+ CMN_GD.comm_n_rx+"  RX/TX="+ CMN_GD.comm_n_rx/ CMN_GD.comm_n_tx);
                        PCM_TestSelect.in_discrete_line.setText(
                        "ackfire="+PCM_GD.dscrt_in_ack_fire+" L_MSW="+PCM_GD.dscrt_in_LAUNCH_MICRO_SW+
                        " ACK_8_2_OK="+PCM_GD.dscrt_in_ACK_8_2_OK +" PAT_PB="+PCM_GD.dscrt_in_PAT_PB+
                        " LAUNCH_RS="+PCM_GD.dscrt_in_LAUNCH_RS +" SW_BIT_OK="+PCM_GD.dscrt_in_SW_BIT_OK+
                        " LAUNCH_PB="+PCM_GD.dscrt_in_LAUNCH_PB +" CPLD_DONE="+PCM_GD.dscrt_in_CPLD_DONE+
                        " CUT_OFF="+PCM_GD.dscrt_in_CUT_OFF);
                         /**
                         // charts simulations
                        if(time_tick % 200 == 0) {
                            pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts();
                            PSM.pmsrmnt[0].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PCM_3_3_V_DC;
                            PSM.pmsrmnt[0].voltage = (float) (3.3 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[1].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PCM_5_5_V_DC;
                            PSM.pmsrmnt[1].voltage = (float) (5.5 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[2].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PCM_5_V_DC;
                            PSM.pmsrmnt[2].voltage = (float) (5.0 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[3].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PCM_12_V_DC_ESAD_LGC;
                            PSM.pmsrmnt[3].voltage = (float) (12.0 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[4].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PCM_12_V_DC_MAIN;
                            PSM.pmsrmnt[4].voltage = (float) (12.0 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[5].pwr_sgnal_id = EnDef.pwr_sgnal_ce.PWR_SGNAL_UNKNOWN;
                            PSM.pmsrmnt[5].voltage = (float) 0.0;
                            TestSelect.voltage_Chart.UpdateDataset(time_tick, PSM);
                            air_data AD = new air_data();
                            AD.air_speed = (float)(30.0+ (4.0 - Math.random()*8.0));
                            AD.altitude = (float)(300.0+ (40.0 - Math.random()*80.0));
                            TestSelect.airSpeedChart.UpdateDataset(time_tick, AD);
                            TestSelect.altitudeChart.UpdateDataset(time_tick, AD);
                        }
                    */
                    }
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

package ATE_GUI.LUMO_GUI;

import ATE_MAIN.LUMO_GD;
import ATE_MAIN.CMN_GD;

import javax.swing.*;

public class LUMO_PeriodicATE_Task extends Thread {
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
                    if(LUMO_TestSelect.init_done) {
                        LUMO_TestSelect.message_line.setText(
                                "Response# " + CMN_GD.response_num + "  " + CMN_GD.response_type + "  " + CMN_GD.response_text);

                        //TestSelect.sinXY.UpdateDataset1();
                        LUMO_TestSelect.serial_test_line.setText( "Port="+ CMN_GD.comm_channel_name+
                                "  TX="+ CMN_GD.comm_n_tx+"  RX="+ CMN_GD.comm_n_rx+"  RX/TX="+ CMN_GD.comm_n_rx/ CMN_GD.comm_n_tx);
                        LUMO_TestSelect.in_discrete_line.setText("Dip Switch="+ LUMO_GD.dip_switch+" umbili_init="+ LUMO_GD.umbili_init
                        +"  NG_ESAD_Status="+ LUMO_GD.NG_ESAD_Status);
                        /**
                         // charts simulations
                        if(time_tick % 200 == 0) {
                            pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts();
                            PSM.pmsrmnt[0].pwr_sgnal_id = EnDef.pwr_sgnal_ce.LUMO_3_3_V_DC;
                            PSM.pmsrmnt[0].voltage = (float) (3.3 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[1].pwr_sgnal_id = EnDef.pwr_sgnal_ce.LUMO_5_5_V_DC;
                            PSM.pmsrmnt[1].voltage = (float) (5.5 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[2].pwr_sgnal_id = EnDef.pwr_sgnal_ce.LUMO_5_V_DC;
                            PSM.pmsrmnt[2].voltage = (float) (5.0 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[3].pwr_sgnal_id = EnDef.pwr_sgnal_ce.LUMO_12_V_DC_ESAD_LGC;
                            PSM.pmsrmnt[3].voltage = (float) (12.0 + (1.0 - Math.random()*2.0));
                            PSM.pmsrmnt[4].pwr_sgnal_id = EnDef.pwr_sgnal_ce.LUMO_12_V_DC_MAIN;
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
                }
            });
            time_tick+=200;
        }
    }

}

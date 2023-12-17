package ATE_GUI.MPU_GUI;

import ATE_GUI.JPM_GUI.JPM_TestSelect;
import ATE_GUI.LUMO_GUI.LUMO_TestSelect;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.JPM_GD;
import ATE_MAIN.LUMO_GD;
import ATE_MAIN.MPU_GD;

import javax.swing.*;

public class MPU_PeriodicATE_Task  extends Thread {
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
                    if(MPU_TestSelect.init_done) {
                        MPU_TestSelect.message_line.setText(
                                "Response# " + MPU_GD.response_num + "  " + MPU_GD.response_type + "  " + MPU_GD.response_text);
                        MPU_TestSelect.serial_test_line.setText( "Port="+ CMN_GD.comm_channel_name+
                                "  TX="+ CMN_GD.comm_n_tx+"  RX="+ CMN_GD.comm_n_rx+"  RX/TX="+ CMN_GD.comm_n_rx/ CMN_GD.comm_n_tx);
                        String MPU_discrete_in_text = "";
                        MPU_discrete_in_text += "MCU_HW_RST="+MPU_GD.dscrt_in_MCU_HW_RST+", ";
                        MPU_discrete_in_text += "MPU_GPS_1PPS="+MPU_GD.dscrt_in_MPU_GPS_1PPS+", ";
                        MPU_discrete_in_text += "DISC_IN4_CON="+MPU_GD.dscrt_in_DISC_IN4_CON+", ";
                        MPU_discrete_in_text += "DISC_IN5_CON="+MPU_GD.dscrt_in_DISC_IN5_CON+", ";
                        MPU_discrete_in_text += "JPM_FORCE_OFF_OUT_3V3="+MPU_GD.dscrt_in_JPM_FORCE_OFF_OUT_3V3+", ";
                        MPU_TestSelect.in_discrete_line.setText("in_discrete_line: "+MPU_discrete_in_text);
                        MPU_TestSelect.internal_serial_test_line.setText("Internal LM comms test: "+MPU_GD.ICTR);
                        MPU_TestSelect.config_params_line1.setText("Config Params Line1: "+
                                "rdr_1_ofst="+ MPU_GD.rdr_1_ofst+
                                "  rdr_2_ofst="+ MPU_GD.rdr_2_ofst+
                                "  rdr_3_ofst="+ MPU_GD.rdr_3_ofst+
                                "  rdr_4_ofst="+ MPU_GD.rdr_4_ofst
                        );
                        MPU_TestSelect.config_params_line2.setText("Config Params Line2: "+
                                "ailrn_1_ofst="+ MPU_GD.ailrn_1_ofst+
                                "  ailrn_2_ofst="+ MPU_GD.ailrn_2_ofst+
                                "  ailrn_3_ofst="+ MPU_GD.ailrn_3_ofst+
                                "  ailrn_4_ofst="+ MPU_GD.ailrn_4_ofst
                        );
                        MPU_TestSelect.revision_state_line.setText(
                                "revision_state_line: State="+ MPU_GD.UUT_state +
                                        "  Revision: "+MPU_GD.revision
                        );
                        MPU_AHRS_Monitor.ShowData();
                        MPU_Camera_Monitor.ShowData(MPU_GD.LMCS);
                    }
                }
            });
            time_tick+=200;
        }
    }
}

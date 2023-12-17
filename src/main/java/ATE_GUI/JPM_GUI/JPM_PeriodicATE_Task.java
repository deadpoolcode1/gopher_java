package ATE_GUI.JPM_GUI;

import ATE_GUI.JPM_GUI.JPM_TestSelect;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.JPM_GD;

import javax.swing.*;

public class JPM_PeriodicATE_Task extends Thread {
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
                    if(JPM_TestSelect.init_done) {
                        JPM_TestSelect.message_line.setText(
                                "message_line: Response# " + JPM_GD.response_num + "  " + JPM_GD.response_type + "  " + JPM_GD.response_text);
                        JPM_TestSelect.serial_test_line.setText( "serial_test_line: Port="+ CMN_GD.comm_channel_name+
                                "  TX="+ CMN_GD.comm_n_tx+"  RX="+ CMN_GD.comm_n_rx+"  RX/TX="+ CMN_GD.comm_n_rx/ CMN_GD.comm_n_tx);
                        JPM_TestSelect.in_discrete_line.setText(
                              "in_discrete_line: MCU_HW_RST_INHIBIT: " + JPM_GD.MCU_HW_RST_INHIBIT +
                             "  VPU_GP1: " + JPM_GD.VPU_GP1 +
                             "  VPU_GP2: " + JPM_GD.VPU_GP2 +
                             "  VPU_GP3: " + JPM_GD.VPU_GP3 +
                             "  VPU_GP4: " + JPM_GD.VPU_GP4 +
                             "  GPIO11: " + JPM_GD.GPIO11 +
                             "  GPIO12: " + JPM_GD.GPIO12
                        );
                        JPM_TestSelect.revision_state_line.setText(
                                "revision_state_line: State="+JPM_GD.UUT_state +
                                "  Revision: "+JPM_GD.revision
                        );
                  }
                }
            });
            time_tick+=200;
        }
    }

}

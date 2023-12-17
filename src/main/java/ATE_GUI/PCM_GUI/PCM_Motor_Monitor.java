package ATE_GUI.PCM_GUI;

import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Propulsion_Status;

import javax.swing.*;
import java.awt.*;

public class PCM_Motor_Monitor extends JPanel {
    //private static JPanel PCMBM_Panel;
    public static JLabel message_line;
    JFrame HostFrame = null;
    public static boolean init_done = false;

    public PCM_Motor_Monitor(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(400, 200);
        message_line = SetLabel("Message area", new Dimension(390, 200));
        int[] dmyfl = {0};
        ShowData(new LM_Propulsion_Status());
        add(new JScrollPane(message_line));
        init_done = true;
    }
    public static void ShowData(LM_Propulsion_Status LPS) {
        String motor_stts =
        "<html>Motor Status: "+"<br/>"+
        " motor_state: %01 "+"<br/>"+
        " Input Voltage: %02 "+"<br/>"+
        " Ripple Input Voltage: %03 "+"<br/>"+
        " Input Current: %04 "+"<br/>"+
        " RPM Command: %05 "+"<br/>"+
        " Output Power: %06 "+"<br/>"+
        " RPM Electrical (Throttle %): %07 "+"<br/>"+
        " Temperature: %08 "+"<br/>"+
        " RPM Out: %09 "+"<br/>"+
        "<html>";
        motor_stts = motor_stts.replace("%01", String.valueOf(LPS.motor_state));
        motor_stts = motor_stts.replace("%02", String.valueOf(LPS.Input_Voltage));
        motor_stts = motor_stts.replace("%03", String.valueOf(LPS.Ripple_Input_Voltage));
        motor_stts = motor_stts.replace("%04", String.valueOf(LPS.Input_Current));
        motor_stts = motor_stts.replace("%05", String.valueOf(LPS.RPM_Command));
        motor_stts = motor_stts.replace("%06", String.valueOf(LPS.Output_Power));
        motor_stts = motor_stts.replace("%07", String.valueOf(LPS.RPM_Electrical));
        motor_stts = motor_stts.replace("%08", String.valueOf(LPS.Temperature));
        motor_stts = motor_stts.replace("%09", String.valueOf(LPS.RPM_Out));
        message_line.setText(motor_stts);

    }
    JLabel SetLabel(String text, Dimension dim) {
        JLabel label = new JLabel (text);
        label.setMinimumSize(dim);
        label.setPreferredSize(dim);
        label.setMaximumSize(dim);
        add(label );
        return label;
    }
}

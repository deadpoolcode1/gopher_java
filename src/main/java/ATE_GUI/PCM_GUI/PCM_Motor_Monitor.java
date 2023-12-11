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
        message_line = SetLabel("Message area", new Dimension(390, 140));
        int[] dmyfl = {0};
        ShowData(new LM_Propulsion_Status());
        add(new JScrollPane(message_line));
        init_done = true;
    }
    public static void ShowData(LM_Propulsion_Status LPS) {
        String motor_stts =
        "<html>Motor Status: "+"<br/>"+
        " motor_state: %1 "+"<br/>"+
        " Input Voltage: %2 "+"<br/>"+
        " Ripple Input Voltage: %3 "+"<br/>"+
        " Input Current: %4 "+"<br/>"+
        " RPM Command: %5 "+"<br/>"+
        " Output Power: %6 "+"<br/>"+
        " RPM Electrical: %7 "+"<br/>"+
        " Temperature: %8 "+"<br/>"+
        "<html>";
        motor_stts = motor_stts.replace("%1", String.valueOf(LPS.motor_state));
        motor_stts = motor_stts.replace("%2", String.valueOf(LPS.motor_state));
        motor_stts = motor_stts.replace("%3", String.valueOf(LPS.Input_Voltage));
        motor_stts = motor_stts.replace("%4", String.valueOf(LPS.Input_Current));
        motor_stts = motor_stts.replace("%5", String.valueOf(LPS.RPM_Command));
        motor_stts = motor_stts.replace("%6", String.valueOf(LPS.Output_Power));
        motor_stts = motor_stts.replace("%7", String.valueOf(LPS.RPM_Electrical));
        motor_stts = motor_stts.replace("%8", String.valueOf(LPS.Temperature));
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

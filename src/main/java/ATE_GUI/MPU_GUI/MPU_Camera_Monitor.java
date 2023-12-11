package ATE_GUI.MPU_GUI;

import ATE_GUI.PCM_GUI.PCM_SerialCommTest;
import LMDS_ICD.LM_Camera_Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MPU_Camera_Monitor extends JPanel {
    //private static JPanel PCMBM_Panel;
    public static JLabel message_line;
    JFrame HostFrame = null;
    public static boolean init_done = false;

    public MPU_Camera_Monitor(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(400, 200);
        JButton BCamera_Control = new JButton ("Camera Control");
        BCamera_Control.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                MPU_Camera_Control MPU_Camera_ControlDialog = new MPU_Camera_Control(
                        HostFrame, "Camera Control", new Dimension(400,600));
            }
        });
        add(BCamera_Control);
        message_line = SetLabel("Message area", new Dimension(390, 190));
        ShowData(new LM_Camera_Status() );
        add(new JScrollPane(message_line));
        init_done = true;
    }
    public static void ShowData(LM_Camera_Status LMCS) {
        String Camera_text =
                "<html>Camera Status: "+"<br/>"+
                        " camera_id: %00 "+"<br/>"+
                        " camera_mode: %01 "+"<br/>"+
                        " spectrum: %02 "+"<br/>"+
                        " LOS_body_attitude: %03 "+"<br/>"+
                        " FOVH: %04 "+"<br/>"+
                        " IR_Polarity: %05 "+"<br/>"+
                        " camera_serviceability: %06 "+"<br/>"+
                        " Camera_Faults_List: %07 "+"<br/>"+
                        "<html>";
        Camera_text = Camera_text.replace("%00", String.valueOf(LMCS.camera_id));
        Camera_text = Camera_text.replace("%01", String.valueOf(LMCS.camera_mode));
        Camera_text = Camera_text.replace("%02", String.valueOf(LMCS.spectrum));
        Camera_text = Camera_text.replace("%03", LMCS.LOS_body_attitude.ToString());
        Camera_text = Camera_text.replace("%04", String.valueOf(LMCS.FOVH));
        Camera_text = Camera_text.replace("%05", String.valueOf(LMCS.IR_Polarity));
        Camera_text = Camera_text.replace("%06", String.valueOf(LMCS.camera_serviceability));
        Camera_text = Camera_text.replace("%07", Arrays.toString(LMCS.Camera_Faults_List));
        //String Camera_text = "dummy Camera text";
        message_line.setText(Camera_text);
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

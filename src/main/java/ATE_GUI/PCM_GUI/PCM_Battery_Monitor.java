package ATE_GUI.PCM_GUI;

import javax.swing.*;
import java.awt.*;

public class PCM_Battery_Monitor extends JPanel {
    //private static JPanel PCMBM_Panel;
    public static JLabel message_line;
    JFrame HostFrame = null;
    public static boolean init_done = false;

    public PCM_Battery_Monitor(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(400, 200);
        message_line = SetLabel("Message area", new Dimension(390, 140));
        ShowData(0.0, 0.0, 0.0,
                false, false, false, false );
        add(new JScrollPane(message_line));
        init_done = true;
    }
    public static void ShowData(double bat_internal_temp, double bat_voltage, double bat_current,
       boolean bat_monitor_comm, boolean bat_hi, boolean bat_lo, boolean bat_chg_inh ) {
        String bat_stts = "<html>Battery Status: "+"<br/>"+" bat_monitor_comm: %1 "+"<br/>"+" bat_hi: %2 "+
                "<br/>"+" bat_lo: %3 "+"<br/>"+" bat_chg_inh: %4 "+"<br/>"+" bat_internal_temp: %5 "+
                "<br/>"+" bat_voltage: %6 "+"<br/>"+" bat_current: %7<html>";
        bat_stts = bat_stts.replace("%1", String.valueOf(bat_monitor_comm));
        bat_stts = bat_stts.replace("%2", String.valueOf(bat_hi));
        bat_stts = bat_stts.replace("%3", String.valueOf(bat_lo));
        bat_stts = bat_stts.replace("%4", String.valueOf(bat_chg_inh));
        bat_stts = bat_stts.replace("%5", String.valueOf(bat_internal_temp));
        bat_stts = bat_stts.replace("%6", String.valueOf(bat_voltage));
        bat_stts = bat_stts.replace("%7", String.valueOf(bat_current));
        message_line.setText(bat_stts);

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

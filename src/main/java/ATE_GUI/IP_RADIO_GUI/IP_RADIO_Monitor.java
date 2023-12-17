package ATE_GUI.IP_RADIO_GUI;

import ATE_MAIN.ATE_Radio_Interface;
import main.java.ip_radio_interface.All_IP_Radio_Settings;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class IP_RADIO_Monitor extends JPanel {
    public JLabel message_line;
    JFrame HostFrame;
    public boolean init_done = false;
    IP_RADIO_Monitor this_IP_RADIO_Monitor=this;
    public IP_RADIO_TEMP_Chart iP_RADIO_TEMP_Chart;
    public IP_RADIO_Sync_Chart iP_RADIO_Sync_Chart;
    public IP_RADIO_VOLT_Chart iP_RADIO_VOLT_Chart;
    public IP_RADIO_RSSI_Chart iP_RADIO_RSSI_Chart;
    public int current_antenna_mask=0xF;
    ATE_Radio_Interface aTE_Radio_Interface;
    public XYSeries
            ANT1_RSSI, ANT2_RSSI,ANT3_RSSI,ANT4_RSSI, RAW_NOISE_PWR, SYNC_SIGNAL_PWR, SYNC_NOISE_PWR,
            CURRENT_TEMP, MAX_TEMP, CURRENT_VOLT, MIN_VOLT, MAX_VOLT;
    public IP_RADIO_Monitor(JFrame HostFrame) throws UnknownHostException, InterruptedException {
        this.HostFrame = HostFrame;
        this.aTE_Radio_Interface = aTE_Radio_Interface;
        setSize(400, 400);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JButton Ip_Radio_Control = new JButton ("IP Radio Control");
        Ip_Radio_Control.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                IP_RADIO_Control IP_RADIO_ControlDialog = new IP_RADIO_Control(
                        HostFrame, this_IP_RADIO_Monitor,
                        "IP RADIO Control, IP="+aTE_Radio_Interface.iP_Radio_Interface.radio_ip_address, new Dimension(400,800));
            }
        });
        message_line = SetLabel("Message area", new Dimension(390, 390));
        //ShowData();
        add(new JScrollPane(message_line));
        add(Ip_Radio_Control);
        iP_RADIO_TEMP_Chart = new IP_RADIO_TEMP_Chart(this, 480,250,
                "Radio Temperatures", "Time (Seconds)", "Temperature (celsius)", this);
        iP_RADIO_Sync_Chart = new IP_RADIO_Sync_Chart(this, 480,250,
                "Radio Sync powers", "Time (Seconds)", "Sync powers (dBm)", this);
        iP_RADIO_VOLT_Chart = new IP_RADIO_VOLT_Chart(this, 480,250,
                "Radio Voltages", "Time (Seconds)", "Voltage (volts)", this);
        iP_RADIO_RSSI_Chart = new IP_RADIO_RSSI_Chart(this, 480,250,
                "Radio RSSI", "Time (Seconds)", "RSSI (dB)", this);
        init_done = true;
    }
    public void SetRadioInterface(ATE_Radio_Interface aTE_Radio_Interface){
        // allows adding a pointer to this radio interface object after this monitor construction
        this.aTE_Radio_Interface = aTE_Radio_Interface;
    }
    public void ShowData(All_IP_Radio_Settings AIRS) {
        if(aTE_Radio_Interface == null)
            return; // do not access the radio interface until initiated
        String RST = // radio status text
        "<html>Radio Settings:"+"<br/>"+
        " Radio IP address: "+aTE_Radio_Interface.iP_Radio_Interface.radio_ip_address+
                "  Logged In: "+AIRS.logged_in+"<br/>"+
        " frequency: %00 "+"<br/>"+
        " bandwidth: %01 "+"<br/>"+
        " network_id: %02 "+"<br/>"+
        " link_distance: %03 "+"<br/>"+
        " total_transmit_power: %04 "+"<br/>"+
        " routing_mode: %05 "+"<br/>"+
        " routing_beacons_period: %06 "+"<br/>"+
        " routing_beacons_mcs: %07 "+"<br/>"+
        " fragmentation_threshold: %08 "+"<br/>"+
        " max_ground_speed: %09 "+"<br/>"+
        " burst_time: %10 "+"<br/>"+
        " rts_retries: %11 "+"<br/>"+
        " number_of_retransmissions: %12 "+"<br/>"+
        " MCS: %13 "+"<br/>"+
        " antennae_mask: %14 "+"<br/>"+
        " variable_gi_mode: %15 "+"<br/>"+
        " cyclic_prefix_length: %16 "+"<br/>"+
        " beam_forming: %17 "+"<br/>"+
        " radio_mode: %18 "+"<br/>"+
        " enable_max_power: %19 "+"<br/>"+
        " la_mode: %20 "+"<br/>"+
        "<html>";
        RST = RST.replace("%00", AIRS.frequency);
        RST = RST.replace("%01", AIRS.bandwidth);
        RST = RST.replace("%02", AIRS.network_id);
        RST = RST.replace("%03", AIRS.link_distance);
        RST = RST.replace("%04", AIRS.total_transmit_power);
        RST = RST.replace("%05", AIRS.routing_mode);
        RST = RST.replace("%06", AIRS.routing_beacons_period);
        RST = RST.replace("%07", AIRS.routing_beacons_mcs);
        RST = RST.replace("%08", AIRS.fragmentation_threshold);
        RST = RST.replace("%09", AIRS.max_ground_speed);
        RST = RST.replace("%10", AIRS.burst_time);
        RST = RST.replace("%11", AIRS.rts_retries);
        RST = RST.replace("%12", AIRS.number_of_retransmissions);
        RST = RST.replace("%13", AIRS.MCS);
        RST = RST.replace("%14", AIRS.antennae_mask);
        if(AIRS.antennae_mask.isEmpty() || AIRS.antennae_mask.isBlank())
            current_antenna_mask = 0xf; // a default value, if yet unknown
        else
            current_antenna_mask = Integer.parseInt(AIRS.antennae_mask);
        RST = RST.replace("%15", AIRS.variable_gi_mode);
        RST = RST.replace("%16", AIRS.cyclic_prefix_length);
        RST = RST.replace("%17", AIRS.beam_forming);
        RST = RST.replace("%18", AIRS.radio_mode);
        RST = RST.replace("%19", AIRS.enable_max_power);
        RST = RST.replace("%20", AIRS.la_mode);
        message_line.setText(RST);
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

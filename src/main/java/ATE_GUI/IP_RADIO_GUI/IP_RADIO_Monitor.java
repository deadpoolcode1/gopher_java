package ATE_GUI.IP_RADIO_GUI;

import ATE_GUI.PCM_GUI.PCM_Voltage_Chart;
import ATE_MAIN.IP_RADIO_GD;
import ATE_MAIN.Streaming_UDP_Handler;
import ATE_MAIN.main;
import LMDS_ICD.All_IP_Radio_Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IP_RADIO_Monitor extends JPanel {
    public JLabel message_line;
    JFrame HostFrame;
    public boolean init_done = false;
    public IP_RADIO_GD iP_RADIO_GD;
    IP_RADIO_Monitor this_IP_RADIO_Monitor=this;
    IP_RADIO_TEMP_Chart iP_RADIO_TEMP_Chart;
    IP_RADIO_Sync_Chart iP_RADIO_Sync_Chart;
    IP_RADIO_VOLT_Chart iP_RADIO_VOLT_Chart;
    IP_RADIO_RSSI_Chart iP_RADIO_RSSI_Chart;
    // streaming data item codes (per the radio API)
    static final int RSSI_msg_type=5009, TEMP_msg_type=8, VOLT_msg_type=4001, end_of_report_type=1,
            min_streaming_dt=10; // the smallest allowed time gap between consecutive streaming reports of same type, milliseconds
    static final int RSSI_fld_type_ant_1_raw_rssi=5000,RSSI_fld_type_ant_2_raw_rssi=5001,RSSI_fld_type_ant_3_raw_rssi=5002,
            RSSI_fld_type_ant_4_raw_rssi=5003,RSSI_fld_type_raw_noise_pwr=5004,RSSI_fld_type_sync_signal_pwr=5005,
            RSSI_fld_type_sync_noise_pwr=5006,RSSI_fld_type_node_ip=5011;
    static final int TEMP_fld_type_current_temp=2,TEMP_fld_type_max_temp=3,TEMP_fld_type_overheat_count=4, TEMP_fld_type_node_ip=6;
    static final int VOLT_fld_type_current_volt=4004,VOLT_fld_type_min_volt=4005, VOLT_fld_type_max_volt=4006,
            VOLT_fld_type_under_volt_count=4007,VOLT_fld_type_over_volt_count=4008;
    long last_rssi_time=0, last_temp_time=0,last_volt_time=0;
    int rssi_ignore_count=0, temp_ignore_count=0,volt_ignore_count=0;
    public IP_RADIO_Monitor(JFrame HostFrame, IP_RADIO_GD iP_RADIO_GD) throws UnknownHostException, InterruptedException {
        this.HostFrame = HostFrame;
        this.iP_RADIO_GD = iP_RADIO_GD;
        setSize(400, 400);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.radio_ip_address = radio_ip_address;
        JButton Ip_Radio_Control = new JButton ("IP Radio Control");
        Ip_Radio_Control.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                IP_RADIO_Control IP_RADIO_ControlDialog = new IP_RADIO_Control(
                        HostFrame, this_IP_RADIO_Monitor,
                        "IP RADIO Control, IP="+iP_RADIO_GD.address_string, new Dimension(400,600));
            }
        });
        message_line = SetLabel("Message area", new Dimension(390, 390));
        //ShowData();
        add(new JScrollPane(message_line));
        add(Ip_Radio_Control);
        iP_RADIO_TEMP_Chart = new IP_RADIO_TEMP_Chart(this, 480,250,
                "Radio Temperatures", "Time (Seconds)", "Temperature (celsius)", iP_RADIO_GD);
        iP_RADIO_Sync_Chart = new IP_RADIO_Sync_Chart(this, 480,250,
                "Radio Sync powers", "Time (Seconds)", "Sync powers (dBm)", iP_RADIO_GD);
        iP_RADIO_VOLT_Chart = new IP_RADIO_VOLT_Chart(this, 480,250,
                "Radio Voltages", "Time (Seconds)", "Voltage (volts)", iP_RADIO_GD);
        iP_RADIO_RSSI_Chart = new IP_RADIO_RSSI_Chart(this, 480,250,
                "Radio RSSI", "Time (Seconds)", "RSSI (dB)", iP_RADIO_GD);
        init_done = true;
    }
    public void ShowData(All_IP_Radio_Settings AIRS) {
        String RST = // radio status text
        "<html>Radio Settings:"+"<br/>"+
        " Radio IP address: "+iP_RADIO_GD.address_string+"<br/>"+
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

    public void Decode_and_process_radio_streams(byte[] msg) {
        int report_type = ToInteger(msg,0);
        switch(report_type) {
            case RSSI_msg_type: Process_RSSI_Stream(msg); break;
            case TEMP_msg_type: Process_TEMP_Stream(msg); break;
            case VOLT_msg_type: Process_VOLT_Stream(msg); break;
            default:
                System.out.println("IP Radio Monitor: unknowm stream type. Ignored. "+report_type);
        }

    }

    private void Process_VOLT_Stream(byte[] msg) {
        int tlv_start =0, // start index of current type-length-value record
                tlv_type=0, // type of current type-length-value record
                tlv_value_length=0; // length of current value field in bytes
        double dval0 = 0,dval1 = 0,dval2 = 0,dval3,dval4;
        String str;
        // a time filter to protect against bursts of streaming reports TODO investigate reason!!
        long time_now = System.currentTimeMillis();
        if(time_now-last_volt_time < min_streaming_dt) {
            volt_ignore_count++;
            return;
        }
        last_volt_time = time_now;
        while((tlv_type !=end_of_report_type) && (tlv_start< msg.length)) {
            tlv_type = ToInteger(msg,tlv_start);
            tlv_value_length=ToInteger(msg,tlv_start+2);
            switch(tlv_type) {
                case VOLT_fld_type_current_volt:        dval0=ToDouble(msg,tlv_start+4)/1000; break;
                case VOLT_fld_type_min_volt:            dval1=ToDouble(msg,tlv_start+4)/1000; break;
                case VOLT_fld_type_max_volt:            dval2=ToDouble(msg,tlv_start+4)/1000; break;
                case VOLT_fld_type_under_volt_count:    dval3=ToDouble(msg,tlv_start+4); break; // TODO under_volt_count not used
                case VOLT_fld_type_over_volt_count:     dval4=ToDouble(msg,tlv_start+4); break; // TODO over_volt_count not used
            }
            tlv_start+=4+tlv_value_length;
        }
        iP_RADIO_VOLT_Chart.UpdateDataset((int) (time_now % 86400000), dval0, dval1, dval2);
        //System.out.println("VOLT Report of radio: "+iP_RADIO_GD.address_string);

    }

    private void Process_TEMP_Stream(byte[] msg) {
        int tlv_start =0, // start index of current type-length-value record
                tlv_type=0, // type of current type-length-value record
                tlv_value_length=0; // length of current value field in bytes
        double dval0 = 0,dval1 = 0,dval2;
        String str;
        // a time filter to protect against bursts of streaming reports TODO investigate reason!!
        long time_now = System.currentTimeMillis();
        if(time_now-last_temp_time < min_streaming_dt) {
            temp_ignore_count++;
            return;
        }
        last_temp_time = time_now;
        while((tlv_type !=end_of_report_type) && (tlv_start< msg.length)) {
            tlv_type = ToInteger(msg,tlv_start);
            tlv_value_length=ToInteger(msg,tlv_start+2);
            switch(tlv_type) {
                case TEMP_fld_type_current_temp:    dval0=ToDouble(msg,tlv_start+4)/1000; break;
                case TEMP_fld_type_max_temp:        dval1=ToDouble(msg,tlv_start+4)/1000; break;
                case TEMP_fld_type_overheat_count:  dval2=ToDouble(msg,tlv_start+4); break; // TODO overheat_count not used
                case TEMP_fld_type_node_ip:         str= ToString(msg,tlv_start+4); break; // TODO node IP not used
            }
            tlv_start+=4+tlv_value_length;
        }
        iP_RADIO_TEMP_Chart.UpdateDataset((int) (time_now % 86400000), dval0, dval1);
        //System.out.println("TEMP Report of radio: "+iP_RADIO_GD.address_string);
    }

    private void Process_RSSI_Stream(byte[] msg) {
        int tlv_start =0, // start index of current type-length-value record
                tlv_type=0, // type of current type-length-value record
                tlv_value_length=0; // length of current value field in bytes
        double dval0=0,dval1=0,dval2=0,dval3=0,dval4=0,dval5=0,dval6=0;
        String str;
        // a time filter to protect against bursts of streaming reports TODO investigate reason!!
        long time_now = System.currentTimeMillis();
        if(time_now-last_rssi_time < min_streaming_dt) {
            rssi_ignore_count++;
            return;
        }
        last_rssi_time = time_now;
        while((tlv_type !=end_of_report_type) && (tlv_start< msg.length)) {
            tlv_type = ToInteger(msg,tlv_start);
            tlv_value_length=ToInteger(msg,tlv_start+2);
            switch(tlv_type) {
                case RSSI_fld_type_ant_1_raw_rssi:  dval0=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_ant_2_raw_rssi:  dval1=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_ant_3_raw_rssi:  dval2=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_ant_4_raw_rssi:  dval3=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_raw_noise_pwr:   dval4=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_sync_signal_pwr: dval5=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_sync_noise_pwr:  dval6=ToDouble(msg,tlv_start+4); break;
                case RSSI_fld_type_node_ip:         str= ToString(msg, tlv_start+4); break; // TODO node IP not used
            }
            tlv_start+=4+tlv_value_length;
        }
        iP_RADIO_RSSI_Chart.UpdateDataset((int) (time_now % 86400000), dval0, dval1, dval2, dval3, dval4);
        iP_RADIO_Sync_Chart.UpdateDataset((int) (time_now % 86400000), dval5, dval6);
        //System.out.println("RSSI & Sync Reports of radio: "+iP_RADIO_GD.address_string);
    }

    static int ToInteger(byte[] bfr, int index) {
        int hi=0, lo=0;
        hi=Byte.toUnsignedInt(bfr[index]);
        lo=Byte.toUnsignedInt(bfr[index+1]);
        return hi*256+lo;
    }
    static String ToString(byte[] bfr, int index) {
        int count=1;
        for(int i=index; i<bfr.length; i++){
            if(bfr[i] == (byte)0) break;
            count++;
        }
        return new String(bfr, index, count);
    }
    static double ToDouble(byte[] bfr, int index) {
        double d=0.0;
        d=Double.parseDouble(ToString(bfr,index));
        return d;
    }
}

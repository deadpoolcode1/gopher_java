package ATE_GUI.LUMO_GUI;

import ATE_MAIN.LUMO_GD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LUMO_GPS_Monitor extends JPanel {
    public static JLabel message_line, TestHint;
    JFrame HostFrame = null;
    public static boolean init_done = false;

    public LUMO_GPS_Monitor(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        // Set the BoxLayout to be X_AXIS: from left to right
        BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxlayout);
        setSize(600, 400);
        /*
        TestHint = SetLabel("Press Sample A or Sample B or System to test receipt and decoding of these AHRS packets.", new Dimension(590, 20));
        JButton SampleA = new JButton ("Sample A");
        SampleA.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                TestSampleA();
            }
        });
        JButton SampleB = new JButton ("Sample B");
        SampleB.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                TestSampleB();
            }
        });
        JButton System_Btn = new JButton ("System");
        System_Btn.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                TestSystem();
            }
        });
        */
        message_line = SetLabel("Message area", new Dimension(590, 250));
        ShowData();
        //add(TestHint);
        //add(SampleA);
        //add(SampleB);
        //add(System_Btn);
        //add(new JScrollPane(message_line));
        add(message_line);
        init_done = true;
    }
/*
    private void TestSystem() {
        AHRS_packet_hdr APH = new AHRS_packet_hdr();
        AHRS_System AHS = new AHRS_System();
        APH.SYNC = (short)0xB562;
        APH.ID = (short)0x05AB;
        APH.length = (short)AHS.GetSize();
        byte[] SystemMsg = new byte[APH.GetSize()+AHS.GetSize()];
        System.arraycopy(APH.GetBytes(),0,SystemMsg,0, APH.GetSize());
        AHS.SN = 25123;
        AHS.status = (short)0xAB;
        AHS.temp = (short)3500;
        AHS.SW_ver_rev = (short)0x1234;
        System.arraycopy(AHS.GetBytes(),0,SystemMsg,APH.GetSize(), AHS.GetSize());
        MPU_GD.NAVD.Update(SystemMsg);
    }

    private void TestSampleB() {
        AHRS_packet_hdr APH = new AHRS_packet_hdr();
        AHRS_Sample_B ASB = new AHRS_Sample_B();
        APH.SYNC = (short)0xB562;
        APH.ID = (short)0x66AB;
        APH.length = (short) ASB.GetSize();
        byte[] SystemMsg = new byte[APH.GetSize()+ ASB.GetSize()];
        System.arraycopy(APH.GetBytes(),0,SystemMsg,0, APH.GetSize());
        ASB.roll = 500;
        ASB.pitch = 600;
        ASB.yaw = 1500;
        ASB.ax = 550;
        ASB.ay = 650;
        ASB.az = 750;
        ASB.ox = 320;
        ASB.oy = 330;
        ASB.oz = 340;
        ASB.mx = 1050;
        ASB.my = 1060;
        ASB.mz = 1070;
        ASB.mtime = 123123456;
        System.arraycopy( ASB.GetBytes(),0,SystemMsg,APH.GetSize(),  ASB.GetSize());
        MPU_GD.NAVD.Update(SystemMsg);
    }

    private void TestSampleA() {
        AHRS_packet_hdr APH = new AHRS_packet_hdr();
        AHRS_Sample_A ASA = new AHRS_Sample_A();
        APH.SYNC = (short)0xB562;
        APH.ID = (short)0x07AB;
        APH.length = (short)  ASA.GetSize();
        byte[] SystemMsg = new byte[APH.GetSize()+  ASA.GetSize()];
        System.arraycopy(APH.GetBytes(),0,SystemMsg,0, APH.GetSize());
        ASA.roll = 600;
        ASA.pitch = 700;
        ASA.yaw = -1500;
        ASA.ax = 650;
        ASA.ay = 750;
        ASA.az = 850;
        ASA.ox = 420;
        ASA.oy = 430;
        ASA.oz = 440;
        ASA.vn = 1060;
        ASA.ve = 1070;
        ASA.vd = 1080;
        ASA.lat = (int)(Math.toRadians(32.123)*10000000.0);
        ASA.lon = (int)(Math.toRadians(67.123)*10000000.0);;
        ASA.alt = 555;
        ASA.mtime = 456345123;
        System.arraycopy(  ASA.GetBytes(),0,SystemMsg,APH.GetSize(),   ASA.GetSize());
        MPU_GD.NAVD.Update(SystemMsg);
    }
*/
    public static void ShowData() {
        String GPS_text = LUMO_GD.gPS_UBX_Data.ToString();
        message_line.setText(GPS_text);

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

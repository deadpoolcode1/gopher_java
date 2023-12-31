package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Camera_Control;
import LMDS_ICD.pwm_cntrl_stts;
import LMDS_ICD.srial_comms_tst;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MPU_Camera_Control extends DialogTemplate {
    /*
        Camera Control Dialog:
        camera_mode: selection - Rate, pilot_window, stow, park
        spectrum: selection - visible, IR
        LOS Yaw - float
        LOS Pitch - float
        FOVH - float from either a selection list or edit field
        IR_Calibration - toggle button (ON/OFF)
        IR_Polarity - selection - hot_white, hot_black
    */
    private static final String[] camera_mode_listItems =  {
            "Rate", //0
            "Pilot window",//1
            "Stow", //2
            "Park" //3
    };
    private static final String[] spectrum_listItems =  {
            "visible", //0
            "IR" //1
    };
    private static final String[] IR_Polarity_listItems = {
            "hot_white", //0
             "hot_black" //1
    };
    private static final String[] Zoom_listItems =   {
            "50.66917484", "47.76055693", "45.01890558", "42.43463623", "39.99871452", "37.70262467",
            "35.53833978", "33.49829369", "31.5753546", "29.76280008", "28.0542936", "26.44386238",
            "24.19988009", "22.14631843", "20.26701859", "18.00698352", "15.99897163", "13.80085347",
            "11.90473743", "9.970030181", "8.106546908", "6.992778574", "6.032032225", "5.051732024",
            "3.987883909", "2.96736015", "2.020627871", "1.023835469"
    };
    private static JTextField JTF_LOS_Yaw, JTF_LOS_Pitch, JTF_FOVH;
    JButton BIR_Calibration;
    private static boolean IR_Calibration = false;

    public MPU_Camera_Control(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        item[0] = item[1] = item[2] = item[3] =-1; // make sure default (unknown) is returned if nothing is selected in any of the 4 selection lists
        JLabel l1 = SetLabel("select required camera controls and hit START", new Dimension(dimension.width-10, 20));
        AddStartActionListener();
        JLabel l5 = SetLabel("select camera operating mode:", new Dimension(dimension.width-10, 20));
        JScrollPane camera_mode_SL = SetSelectionList(camera_mode_listItems, 0);
        d.add(camera_mode_SL);
        JLabel l6 = SetLabel("select camera spectrum:", new Dimension(dimension.width-10, 20));
        JScrollPane spectrum_SL = SetSelectionList(spectrum_listItems, 1);
        d.add(spectrum_SL);
        JLabel l7 = SetLabel("select camera IR Polarity:", new Dimension(dimension.width-10, 20));
        JScrollPane IR_Polarity_SL = SetSelectionList(IR_Polarity_listItems, 2);
        d.add(IR_Polarity_SL);
        JLabel l2 = SetLabel("Camera LOS Yaw, degrees (-140 to +140)", new Dimension(dimension.width-10, 20));
        JTF_LOS_Yaw = new JTextField(7) ;
        JTF_LOS_Yaw.setMaximumSize(new Dimension(100,20));
        d.add(JTF_LOS_Yaw);
        JLabel l3 = SetLabel("Camera LOS Pitch, degrees (-45 to +80)", new Dimension(dimension.width-10, 20));
        JTF_LOS_Pitch = new JTextField(7) ;
        JTF_LOS_Pitch.setMaximumSize(new Dimension(100,20));
        d.add(JTF_LOS_Pitch);
        JLabel l8 = SetLabel("select desired FOVH (degrees, overrides the FOVH edit field):", new Dimension(dimension.width-10, 20));
        JScrollPane Zoom_SL = SetSelectionList(Zoom_listItems, 3);
        d.add(Zoom_SL);
        JLabel l4 = SetLabel("Camera FOVH, degrees (0.0 to 51.0, if not defined by the selction list)", new Dimension(dimension.width-10, 20));
        JTF_FOVH = new JTextField(12) ;
        JTF_FOVH.setMaximumSize(new Dimension(120,20));
        d.add(JTF_FOVH);
        JLabel l9 = SetLabel("Set IR Camera calibration (NUC) (On or Off, valid only in IR spectrum)", new Dimension(dimension.width-10, 20));
        BIR_Calibration = new JButton ("IR Calibration Off");
        BIR_Calibration.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                if(IR_Calibration) {
                    IR_Calibration = false;
                    BIR_Calibration.setText("IR Calibration Off");
                }
                else {
                    IR_Calibration = true;
                    BIR_Calibration.setText("IR Calibration  On");
                }
            }
        });
        d.add(BIR_Calibration);
        d.pack();
        d.setVisible(true);
    }
    @Override
    protected void AddStartActionListener() {
        // override this in any dialog
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                System.out.println("Camera Control Start!");
                LM_Camera_Control LCC = new LM_Camera_Control();
                // read camera_mode
                switch(item[0]) {
                    case 0: LCC.camera_mode = EnDef.camera_mode_ce.camera_mode_rate; break;
                    case 1: LCC.camera_mode = EnDef.camera_mode_ce.camera_mode_pilot_window; break;
                    case 2: LCC.camera_mode = EnDef.camera_mode_ce.camera_mode_stow; break;
                    case 3: LCC.camera_mode = EnDef.camera_mode_ce.camera_mode_park; break;
                    default: LCC.camera_mode = EnDef.camera_mode_ce.camera_mode_unknown;
                        System.out.println("Camera Control - camear mode unchanged. "+item[0]);
                    break;
                }
                // read spectrum
                switch(item[1]) {
                    case 0: LCC.spectrum = EnDef.spectrum_ce.spectrum_visible; break;
                    case 1: LCC.spectrum = EnDef.spectrum_ce.spectrum_IR; break;
                    default: LCC.spectrum = EnDef.spectrum_ce.spectrum_unknown;
                        System.out.println("Camera Control - spectrum unchanged. "+item[1]);
                        break;
                }
                LCC.LOS_body_attitude.roll = (float)0.0; // constant in the UAV Body reference system
                // read LOS Yaw
                LCC.LOS_body_attitude.yaw = (float) main.GetDouble(JTF_LOS_Yaw.getText(),-141.0,141.0);
                if(LCC.LOS_body_attitude.yaw == (float)(-999.999))
                    System.out.println("Camera Control - illegal camera yaw. -999.999 sent");
                // read LOS Pitch
                LCC.LOS_body_attitude.pitch = (float) main.GetDouble(JTF_LOS_Pitch.getText(),-45.0,80.0);
                if(LCC.LOS_body_attitude.pitch == (float)(-999.999))
                    System.out.println("Camera Control - illegal camera pitch. -999.999 sent");
                if(item[3] != -1) {
                    // get the FOVH from the selection list
                    int zoom_ix = Math.max(0, item[3]);
                    zoom_ix = Math.min(zoom_ix, Zoom_listItems.length - 1);
                    LCC.Set_FOVH = (float) main.GetDouble(Zoom_listItems[zoom_ix],0.0,51.0);
                }
                else {
                    // read FOVH from the edit field
                    LCC.Set_FOVH = (float) main.GetDouble(JTF_FOVH.getText(), 0.0, 51.0);
                }
                if (LCC.Set_FOVH == (float) (-999.999))
                    System.out.println("Camera Control - illegal camera FOVH. -999.999 sent");
                // read IR_Calibration
                if(LCC.spectrum == EnDef.spectrum_ce.spectrum_IR)
                    LCC.IR_Calibration = IR_Calibration;
                else {
                    LCC.IR_Calibration = false;
                    System.out.println("Camera Control - IR_Calibration (NUC) request is valid only under IR spectrum. Ignored.");
                }
                            // read IR_Polarity
                switch(item[2]) {
                    case 0: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_hot_white; break;
                    case 1: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_hot_black; break;
                    default: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_unknown;
                        System.out.println("Camera Control - IR_Polarity unchanged. "+item[2]);
                        break;
                }
                if(CMN_GD.ServicePort >=0)
                    try {
                        main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                            EnDef.msg_code_ce.MSG_CODE_EO_CNTRL, LCC);
                    } catch (IOException ex) { throw new RuntimeException(ex); }
                else
                    System.out.println("Camera_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}


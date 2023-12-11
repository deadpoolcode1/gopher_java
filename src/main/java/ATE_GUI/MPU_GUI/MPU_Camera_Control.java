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

public class MPU_Camera_Control extends DialogTemplate {
    /*
        Camera Control Dialog:
        camera_mode: selection - Rate, pilot_window, stow, park
        spectrum: selection - visible, IR
        LOS Yaw - float
        LOS Pitch - float
        FOVH - float
        IR_Calibration - toggle button (ON/OFF)
        IR_Polarity - selection - hot_white, hot_black
    */
    private static final String[] camera_mode_listItems =
            { "Rate", //0
              "Pilot window",//1
              "Stow", //2
              "Park" //3
            };
    private static final String[] spectrum_listItems =
            { "visible", //0
              "IR" //1
            };
    private static final String[] IR_Polarity_listItems =
            { "hot_white", //0
              "hot_black" //1
            };
    private static JTextField JTF_LOS_Yaw, JTF_LOS_Pitch, JTF_FOVH;
    JButton BIR_Calibration;
    private static boolean IR_Calibration = false;

    public MPU_Camera_Control(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
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
        JLabel l3 = SetLabel("Camera LOS Pitch, degrees (-135 to +45)", new Dimension(dimension.width-10, 20));
        JTF_LOS_Pitch = new JTextField(7) ;
        JTF_LOS_Pitch.setMaximumSize(new Dimension(100,20));
        d.add(JTF_LOS_Pitch);
        JLabel l4 = SetLabel("Camera Field Of View Height (FOVH), degrees (+2 to +20)", new Dimension(dimension.width-10, 20));
        JTF_FOVH = new JTextField(7) ;
        JTF_FOVH.setMaximumSize(new Dimension(100,20));
        d.add(JTF_FOVH);
        JLabel l8 = SetLabel("Set IR Camera calibration (On or Off)", new Dimension(dimension.width-10, 20));
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
                        System.out.println("Camera Control - illegal camear mode selection! "+item[0]);
                    break;
                }
                // read spectrum
                switch(item[1]) {
                    case 0: LCC.spectrum = EnDef.spectrum_ce.spectrum_visible; break;
                    case 1: LCC.spectrum = EnDef.spectrum_ce.spectrum_IR; break;
                    default: LCC.spectrum = EnDef.spectrum_ce.spectrum_unknown;
                        System.out.println("Camera Control - illegal spectrum selection! "+item[1]);
                        break;
                }
                LCC.LOS_body_attitude.roll = (float)0.0; // constant in the UAV Body reference system
                // read LOS Yaw
                LCC.LOS_body_attitude.yaw = (float) main.GetDouble(JTF_LOS_Yaw.getText(),-140.0,140.0);
                if(LCC.LOS_body_attitude.yaw == (float)(-999.999))
                    System.out.println("Camera Control - illegal camera yaw. -999.999 sent");
                // read LOS Pitch
                    LCC.LOS_body_attitude.pitch = (float) main.GetDouble(JTF_LOS_Pitch.getText(),-135.0,45.0);
                    if(LCC.LOS_body_attitude.pitch == (float)(-999.999))
                        System.out.println("Camera Control - illegal camera pitch. -999.999 sent");
                // read FOVH
                LCC.Set_FOVH = (float) main.GetDouble(JTF_FOVH.getText(),2.0,20.0);
                if(LCC.Set_FOVH == (float)(-999.999))
                    System.out.println("Camera Control - illegal camera FOVH. -999.999 sent");
                // read IR_Calibration
                LCC.IR_Calibration = IR_Calibration;
                // read IR_Polarity
                switch(item[2]) {
                    case 0: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_hot_white; break;
                    case 1: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_hot_black; break;
                    default: LCC.IR_Polarity = EnDef.IR_Polarity_ce.IR_Polarity_unknown;
                        System.out.println("Camera Control - illegal IR_Polarity selection! "+item[2]);
                        break;
                }

                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                            EnDef.msg_code_ce.MSG_CODE_EO_CNTRL, LCC);
                else
                    System.out.println("Camera_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}


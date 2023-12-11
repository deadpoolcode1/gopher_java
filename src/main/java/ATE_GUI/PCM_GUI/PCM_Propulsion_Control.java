package ATE_GUI.PCM_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Propulsion_Control;
import LMDS_ICD.pwm_cntrl_stts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PCM_Propulsion_Control extends DialogTemplate {
    JToggleButton MotorStateButton;
    private static JTextField RPM;
    EnDef.Motor_state_ce motor_state = EnDef.Motor_state_ce.Motor_state_unknown;
    public PCM_Propulsion_Control(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        JLabel l1 = SetLabel("Select motor state, enter RPM and hit START", new Dimension(400, 20));
        AddStartActionListener();
        MotorStateButton = new JToggleButton("Motor Off");
        ItemListener itemListener = new ItemListener() {
            // itemStateChanged() method is invoked automatically
            // whenever you click or unclick on the Button.
            public void itemStateChanged(ItemEvent itemEvent) // event is generated in button
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    MotorStateButton.setText("Motor On");
                    motor_state = EnDef.Motor_state_ce.Motor_state_on;
                    System.out.println("Motor on!");
                }
                else {
                    MotorStateButton.setText("Motor Off");
                    motor_state = EnDef.Motor_state_ce.Motor_state_off;
                    System.out.println("Motor off!");
                }
            }
        };
        // Attach Listeners
        MotorStateButton.addItemListener(itemListener);
        d.add(MotorStateButton, BorderLayout.NORTH);
        JLabel l2 = SetLabel("Motor Rotations Per Minute (RPM):", new Dimension(200, 20));
        RPM = new JTextField(7) ;
        RPM.setMaximumSize(new Dimension(100,20));
        RPM.setText("0.0"); // default RPM is 0
        d.add(RPM);
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
                System.out.println("PCM Propulsion Control Start!");
                LM_Propulsion_Control LPC = new LM_Propulsion_Control();
                LPC.motor_state = motor_state;
                if(motor_state == EnDef.Motor_state_ce.Motor_state_off) {
                    RPM.setText("0.0"); // force the motor RPM to 0.0 on a motor off command
                }
                LPC.motor_rpm = (float) main.GetDouble(RPM.getText(), 0.0,8000.0);
                if(LPC.motor_rpm == (float)(-999.999)) {
                    System.out.println("PCM_Propulsion_Control - bad RPM value (range is 0-8000). Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("PCM_Propulsion_Control - Service comm port inactive. Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_PCM, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF2),
                            EnDef.msg_code_ce.MSG_CODE_PRPLSN_CNTRL, LPC);
                else
                    System.out.println("PCM_Propulsion_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}

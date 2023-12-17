package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.pwm_cntrl_stts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MPU_PWM_Control_Test extends DialogTemplate {
    //private static JList SelectionList;
    private static final String[] listItems =
            {  "MPU PWM Out1", //0
               "MPU PWM Out2", //1
               "MPU PWM Out3", //2
               "MPU PWM Out4", //3
               "MPU PWM Out5", //4
               "MPU PWM Out6", //5
            };
    private static JTextField PWM_duty_cycle_percent;

    public MPU_PWM_Control_Test(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        JLabel l1 = SetLabel("Select PWM channel, enter duty cycle and hit START", new Dimension(400, 20));
        AddStartActionListener();
        JScrollPane LSP = SetSelectionList(listItems,0);
        d.add(LSP);
        JLabel l2 = SetLabel("PWM duty cycle, percent", new Dimension(200, 20));
        PWM_duty_cycle_percent = new JTextField(7) ;
        PWM_duty_cycle_percent.setMaximumSize(new Dimension(100,20));
        d.add(PWM_duty_cycle_percent);
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
                System.out.println("PWM Control Start!");
                boolean legal_selection=true;
                pwm_cntrl_stts PCS = new pwm_cntrl_stts();
                PCS.isCntrl = 1 ; // TODO change interface to boolean
                PCS.servo_duty_cycle = (float) main.GetDouble(PWM_duty_cycle_percent.getText(),0.0,100.0);
                if(PCS.servo_duty_cycle == (float)(-999.999)) {
                    System.out.println("PWM Control - PWM duty cycle is illegal. Can't send.");
                    return;
                }
                switch(item[0])
                {
                    case 0:
                        PCS.pwm_id = EnDef.PWM_channel_id_ce.MPU_PWM_OUT1;
                        break;
                    case 1:
                        PCS.pwm_id = EnDef.PWM_channel_id_ce.MPU_PWM_OUT2;
                        break;
                    default:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        System.out.println("PWM Control - PWM 3 to 6 are not supported. Ignore..");
                        return;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("PWM_Control - Service comm port inactive. Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                            EnDef.msg_code_ce.MSG_CODE_PWM_CNTRL_STTS, PCS);
                else
                    System.out.println("PWM_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}

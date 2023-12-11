package ATE_GUI.LUMO_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import LMDS_ICD.EnDef;
import LMDS_ICD.pwm_cntrl_stts;
import ATE_MAIN.LUMO_GD;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LUMO_PWM_Control_Test extends DialogTemplate {
    private static JList SelectionList;
    private static final String[] listItems =
            { "LUMO PWM parachute/EO Protect" };
    //          0
    //private static int item=0;
    private static JTextField PWM_duty_cycle_percent;

    public LUMO_PWM_Control_Test(JFrame OwnerFrame, String title, Dimension dimension) {
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
                        PCS.pwm_id = EnDef.PWM_channel_id_ce.LUMO_PWM_PRCHUT;
                        break;
                    default:
                        System.out.println("PWM_Control - illegal selection item. Can't send.  "+item[0]);
                        return;

                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("PWM_Control - Service comm port inactive. Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_LUMO, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF1),
                            EnDef.msg_code_ce.MSG_CODE_PWM_CNTRL_STTS, PCS);
            }
        });
    }
}

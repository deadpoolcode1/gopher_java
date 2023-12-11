package ATE_GUI.PCM_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import LMDS_ICD.EnDef;
import LMDS_ICD.dscrt_cntrl;
import ATE_MAIN.PCM_GD;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PCM_Discrete_Control extends DialogTemplate {
    private static JList SelectionList;
    private static final String[] listItems =
    {
            "GGC_FIRE",     //0
            "LED_RED_BAT",  //1
            "LED_GREEN",    //2
            "SW_BIT_OK",    //3
            "RST_CPLD"      //4
   };
    private static final String[] Discrete_States = { "Low", "High" };
    //                                                  0       1

    public PCM_Discrete_Control(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        JLabel l1 = SetLabel("Select Discrete, set desired state and hit START", new Dimension(400, 20));
        AddStartActionListener();
        JScrollPane LSP0 = SetSelectionList(listItems,0);
        d.add(LSP0);
        JLabel l2 = SetLabel("Desired Discrete State:", new Dimension(200, 20));
        JScrollPane LSP1 = SetSelectionList(Discrete_States,1);
        d.add(LSP1);
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
                System.out.println("Discrete Control Start!");
                boolean legal_selection=true;
                dscrt_cntrl DC = new dscrt_cntrl();
                switch(item[1]) {
                    case 0: DC.dscrt_state_cntrl.dscrt_val = EnDef.dscrt_val_ce.DSCRT_VAL_LOW; break;
                    case 1: DC.dscrt_state_cntrl.dscrt_val = EnDef.dscrt_val_ce.DSCRT_VAL_HIGH; break;
                }
                switch(item[0])
                {
                    case 0:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.PCM_DSCRT_OUT_GGC_FIRE;
                        break;
                    case 1:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.PCM_DSCRT_OUT_LED_RED_BAT;
                        break;
                    case 2:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.PCM_DSCRT_OUT_LED_GREEN;
                        break;
                    case 3:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.PCM_DSCRT_IN_SW_BIT_OK;
                        break;
                    case 4:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.PCM_DSCRT_OUT_RST_CPLD;
                        break;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_PCM, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF2),
                            EnDef.msg_code_ce.MSG_CODE_DSCRT_CNTRL, DC);
                else
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}


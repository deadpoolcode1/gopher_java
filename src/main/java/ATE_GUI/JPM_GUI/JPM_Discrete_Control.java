package ATE_GUI.JPM_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.dscrt_cntrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPM_Discrete_Control extends DialogTemplate {
    private static final String[] listItems =
            {
                    "JPM_FORCE_OFF_OUT_3V3 (Shutdown)", //0
                    "JPM_DSCRT_OUT_GPIO07", //1   for testing
                    "JPM_DSCRT_OUT_GPIO09" //2  for testing
            };
    private static final String[] Discrete_States = { "Low", "High" };
    //                                                  0       1

    public JPM_Discrete_Control(JFrame OwnerFrame, String title, Dimension dimension) {
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
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.JPM_DSCRT_OUT_JPM_FORCE_OFF_OUT_3V3;
                        break;
                    case 1:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.JPM_DSCRT_OUT_GPIO07;
                        break;
                    case 2:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.JPM_DSCRT_OUT_GPIO09;
                        break;
                }
                if(CMN_GD.ServicePort < 0 ) {
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");
                    return;
                }
                main.SendMessage(CMN_GD.ServicePort,
                        main.GetNewAddress(EnDef.host_name_ce.HOST_JPM, EnDef.process_name_ce.PR_TST_CMND),
                        main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF1),
                        EnDef.msg_code_ce.MSG_CODE_DSCRT_CNTRL, DC);

            }
        });
    }
}

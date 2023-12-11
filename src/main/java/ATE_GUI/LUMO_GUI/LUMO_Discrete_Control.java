package ATE_GUI.LUMO_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import LMDS_ICD.EnDef;
import LMDS_ICD.dscrt_cntrl;
import ATE_MAIN.LUMO_GD;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LUMO_Discrete_Control extends DialogTemplate {
    private static JList SelectionList;
    private static final String[] listItems =
    {
            "NG_ESAD_TF1",       //0
            "NG_ESAD_TF2",       //1
            "NG_ESAD_CMND_TRIG", //2
            "NG_ESAD_EN5V",      //3
            "SAASM_EN5V",        //4
            "SAASM_GPS_PRGRM",   //5
            "SAASM_UART_FNCTN",  //6
            "SAASM_RST",         //7
            "SAASM_KDP_PRGRM",   //8
            "SAASM_ZROIS",       //9
            "LD_GR",             //10
            "LD_RD",             //11
            "ESAD_LGC_PWR",      //12
            "URT2_PHY",          //13
            "PITOT_HTNG_CNTRL"   //14
   };
    private static final String[] Discrete_States = { "Low", "High" };
    //                                                  0       1

    public LUMO_Discrete_Control(JFrame OwnerFrame, String title, Dimension dimension) {
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
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_NG_ESAD_TF1;
                        break;
                    case 1:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_NG_ESAD_TF2;
                        break;
                    case 2:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_NG_ESAD_CMND_TRIG;
                        break;
                    case 3:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_NG_ESAD_EN5V;
                        break;
                    case 4:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_EN5V;
                        break;
                    case 5:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_GPS_PRGRM;
                        break;
                    case 6:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_UART_FNCTN;
                        break;
                    case 7:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_RST;
                        break;
                    case 8:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_KDP_PRGRM;
                        break;
                    case 9:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_SAASM_ZROIS;
                        break;
                    case 10:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_LD_GR;
                        break;
                    case 11:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_LD_RD;
                        break;
                    case 12:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_ESAD_LGC_PWR;
                        break;
                    case 13:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_URT2_PHY;
                        break;
                    case 14:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.LUMO_DSCRT_OUT_PITOT_HTNG_CNTRL;
                        break;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");
                    return;
                }
                if(CMN_GD.ServicePort >=0)
                    main.SendMessage(CMN_GD.ServicePort,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_LUMO, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF1),
                            EnDef.msg_code_ce.MSG_CODE_DSCRT_CNTRL, DC);
                else
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");

            }
        });
    }
}


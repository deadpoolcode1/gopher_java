package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.dscrt_cntrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MPU_Discrete_Control extends DialogTemplate {
    private static JList SelectionList;
    private static final String[] listItems =
            {
                    "MCU_DSP_RST",          //0
                    "LED_GREEN",            //1
                    "LED_RED",              //2
                    "MCU_HW_RST_INHIBIT",   //3
                    "HW_PWR_SW1",           //4
                    "VPU_GP1",              //5
                    "VPU_GP2",              //6
                    "VPU_GP3",              //7
                    "VPU_GP4",              //8
                    "DISC_OUT1_CON",        //9
                    "DISC_OUT2_CON",        //10
                    "DISC_OUT3_CON",        //11
                    "DISC_OUT_JPM_PWR_DIS", //12
                    "JETSON_PWR_ON_OFF",    //13
                    "CAMERA_ON_OFF",        //14
                    "MIPI_RSTN",            //15
                    "FAN_ON_OFF"            //16
            };
    private static final String[] Discrete_States = { "Low", "High" };
    //                                                  0       1

    public MPU_Discrete_Control(JFrame OwnerFrame, String title, Dimension dimension) {
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
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_MCU_DSP_RST;
                        break;
                    case 1:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_LED_GREEN;
                        break;
                    case 2:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_LED_RED;
                        break;
                    case 3:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_MCU_HW_RST_INHIBIT;
                        break;
                    case 4:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_HW_PWR_SW1;
                        break;
                    case 5:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_VPU_GP1;
                        break;
                    case 6:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_VPU_GP2;
                        break;
                    case 7:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_VPU_GP3;
                        break;
                    case 8:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_VPU_GP4;
                        break;
                    case 9:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_DISC_OUT1_CON;
                        break;
                    case 10:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_DISC_OUT2_CON;
                        break;
                    case 11:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_DISC_OUT3_CON;
                        break;
                    case 12:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_JPM_PWR_DIS;
                        break;
                    case 13:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_JETSON_PWR_ON_OFF;
                        break;
                    case 14:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_CAMERA_ON_OFF;
                        break;
                    case 15:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_MIPI_RSTN;
                        break;
                    case 16:
                        DC.dscrt_state_cntrl.dscrt_id = EnDef.dscrt_id_ce.MPU_DSCRT_OUT_FAN_ON_OFF;
                        break;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("Discrete_Control - Service comm port inactive. Can't send.");
                    return;
                }
                main.SendMessage(CMN_GD.ServicePort,
                        main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                        main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                        EnDef.msg_code_ce.MSG_CODE_DSCRT_CNTRL, DC);

            }
        });
    }
}

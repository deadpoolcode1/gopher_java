package ATE_GUI.GUI_CMN;

import ATE_MAIN.*;
import LMDS_ICD.Address;
import LMDS_ICD.EnDef;
import LMDS_ICD.ds_cntrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static LMDS_ICD.EnDef.host_name_ce.*;

public class DS_Control extends DialogTemplate {
    //private static JList SelectionList;
    EnDef.host_name_ce UUT_id;
    EnDef.process_name_ce sender_TF;
    private static final String[] listItems =
            { "IDLE", "SINGLE_UUT", "AV_ASSY", "INTEGRATED_LM", "LUMO_LP_NOGPS", "LUMO_LP_GPS_UBLOX", "LUMO_LP_GPS_SAASM"  };
    //          0           1            2           3                   4                  5                 6
    //private static int item=0;

    public DS_Control(JFrame OwnerFrame, String title, Dimension dimension, EnDef.host_name_ce UUT_id,
                      EnDef.process_name_ce sender_TF) {
        super(OwnerFrame, title, dimension);
        this.UUT_id = UUT_id;
        this.sender_TF = sender_TF;
        JLabel l1 = SetLabel("Select UUT state and hit START", new Dimension(200, 20));
        AddStartActionListener();
        JScrollPane LSP = SetSelectionList(listItems,0);
        d.add(LSP);
        d.setVisible(true);
    }
    @Override
    protected void AddStartActionListener() {
        // override this in any dialog
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                System.out.println("DS Control Start!");
                boolean legal_selection=true;
                ds_cntrl DSC = new ds_cntrl();
                DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_UNKNOWN;
                switch(item[0])
                {
                    case 0:
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_IDLE;
                        break;
                    case 1:
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_SINGLE_UUT;
                        break;
                    case 2:
                        if(UUT_id == EnDef.host_name_ce.HOST_LUMO) { legal_selection=false; break; }
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_AV_ASSY;
                        break;
                    case 3:
                        if(UUT_id == EnDef.host_name_ce.HOST_LUMO) { legal_selection=false; break; }
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_INTEGRATED_LM;
                        break;
                    case 4:
                        if(UUT_id != EnDef.host_name_ce.HOST_LUMO) { legal_selection=false; break; }
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_LUMO_LP_NOGPS;
                        break;
                    case 5:
                        if(UUT_id != EnDef.host_name_ce.HOST_LUMO) { legal_selection=false; break; }
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_LUMO_LP_GPS_UBLOX;
                        break;
                    case 6:
                        if(UUT_id != EnDef.host_name_ce.HOST_LUMO) { legal_selection=false; break; }
                        DSC.lmds_state = EnDef.lmds_state_ce.LMDS_ST_LUMO_LP_GPS_SAASM;
                        break;
                }
                if(!legal_selection) {
                    System.out.println("DS_Control - illegal item selected: " + item[0]);
                    return;
                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("DS_Control - Service comm port inactive. Can't send.");
                    return;
                }
                main.SendMessage(CMN_GD.ServicePort,
                    main.GetNewAddress(UUT_id, EnDef.process_name_ce.PR_TST_CMND),
                    main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, sender_TF),
                    EnDef.msg_code_ce.MSG_CODE_DS_CNTRL, DSC);
                // TODO the state is saved upon sending. may be invalid if rejected / not arrived by/to the UUT!
                switch(UUT_id) {
                    case HOST_MPU:
                        MPU_GD.UUT_state = DSC.lmds_state;
                        break;
                    case HOST_LUMO:
                        LUMO_GD.UUT_state = DSC.lmds_state;
                        break;
                    case HOST_JPM:
                        JPM_GD.UUT_state = DSC.lmds_state;
                        break;
                    case HOST_PCM:
                        PCM_GD.UUT_state = DSC.lmds_state;
                        break;
                    default:
                        System.out.println("DS_Control - Illegal UUT_ID. "+UUT_id);
                }
            }
        });
    }
}

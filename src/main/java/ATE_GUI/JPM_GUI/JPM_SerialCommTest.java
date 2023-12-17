package ATE_GUI.JPM_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.JPM_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.srial_comms_tst;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPM_SerialCommTest extends DialogTemplate {
    /*
    tests for channels MPU1, MPU2 are routed via the MPU, so that the MER reflects the sum of errors in both MPU-JPM link and ATE-MPU link
    tests for the LAN, USB0, USB1 are done directly with the ATE
     */
    private static final String[] listItems =
            { "Service (MPU 1)", "MPU 2", "Host LAN","ATE USB1","ATE USB2" };
    //            0               1          2           3           4
    //private static int item=0;
    private int port = CMN_GD.ServicePort;
    EnDef.process_name_ce SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
    EnDef.host_name_ce SenderHost = EnDef.host_name_ce.HOST_UNKNOWN;

    public JPM_SerialCommTest(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        AddStartActionListener();
        JButton BSend = new JButton ("Send!");
        BSend.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                srial_comms_tst SCT=null;
                if(port <0) {
                    System.out.println("SerialCommTest - Tested comm port inactive. Can't send.");
                    return;
                }
                if(port >=0) {
                    SCT = new srial_comms_tst();
                    for (int i = 0; i < SCT.bytes.length; i += 1) { // array length should be a multiple of 4!
                        SCT.bytes[i] = (byte) (i+16);
                    }
                    /*
                    for (int i = 0; i < SCT.bytes.length; i += 4) { // array length should be a multiple of 4!
                        SCT.bytes[i] = (byte) 0xAB;
                        SCT.bytes[i + 1] = (byte) 0xCD;
                        SCT.bytes[i + 2] = (byte) 0xEF;
                        SCT.bytes[i + 3] = (byte) 0xA7;
                    }
                    */
                    main.SendMessage(port,
                            main.GetNewAddress(EnDef.host_name_ce.HOST_JPM, EnDef.process_name_ce.PR_TST_CMND),
                            main.GetNewAddress(SenderHost, EnDef.process_name_ce.PR_ATE_TF4),
                            EnDef.msg_code_ce.MSG_CODE_SER_COMM_TST, SCT);
                    CMN_GD.comm_n_tx++;
                }
                else
                    System.out.println("SerialCommTest - Service comm port inactive. Can't send.");
            }
        });
        d.add(BSend);
        JButton BToggleLANState = new JButton ("");
        if(JPM_GD.LAN_Active)
            BToggleLANState.setText("Set LAN Off!");
        else
            BToggleLANState.setText("Set LAN On!");
        BToggleLANState.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                if(JPM_GD.LAN_Active) {
                    BToggleLANState.setText("Set LAN On!");
                    JPM_GD.LAN_Active = false;
                    System.out.println("SerialCommTest - LAN inactive");
                }
                else {
                    BToggleLANState.setText("Set LAN Off!");
                    JPM_GD.LAN_Active = true;
                    System.out.println("SerialCommTest - LAN active");
                }
            }
        });
        d.add(BToggleLANState);
        JLabel l1 = SetLabel("Select Port, hit START and then Send repeatedly",
                new Dimension(350, 20));
        String[] display_listItems = new String[listItems.length];
        for(int i=0; i<listItems.length; i++) display_listItems[i] = "";
        // for the JPM, serial comms tests for both channels are done over same service port
        if(CMN_GD.ServicePort >=0) {
            display_listItems[0] = listItems[0] + "-Active";
            display_listItems[1] = listItems[1] + "-Active";
            display_listItems[3] = listItems[3] + "-Active";
            display_listItems[4] = listItems[4] + "-Active";
        }
        else {
            display_listItems[0] = listItems[0] + "-Not Active";
            display_listItems[1] = listItems[1] + "-Not Active";
            display_listItems[3] = listItems[3] + "-Not Active";
            display_listItems[4] = listItems[4] + "-Not Active";
        }
        if(CMN_GD.LANPort >=0 && JPM_GD.LAN_Active)
            display_listItems[2] = listItems[2] + "-Active";
        else
            display_listItems[2] = listItems[2] + "-Not Active";
        JScrollPane LSP = SetSelectionList(display_listItems,0);
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
                System.out.println("SerialCommTest Start!");
                boolean legal_selection=true;
                CMN_GD.comm_n_rx = 0;
                CMN_GD.comm_n_tx = 0;
                CMN_GD.comm_channel_name = listItems[item[0]];
                switch(item[0])
                { // for the JPM, all serial comms tests are done over the service port
                    case 0:
                        port = CMN_GD.ServicePort; // to JPM - MPU 1 channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
                        SenderHost = EnDef.host_name_ce.HOST_JPM;
                        break;
                    case 1:  // to JPM - MPU 2 channel
                        port = CMN_GD.ServicePort;
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
                        SenderHost = EnDef.host_name_ce.HOST_JPM2;
                        break;
                    case 2:  // to JPM - Host LAN channel
                        port = CMN_GD.LANPort;
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
                        SenderHost = EnDef.host_name_ce.HOST_LAN_PRXY;
                        break;
                    case 3:  //  to JPM - ATE USB1 channel
                        port = CMN_GD.ServicePort;
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
                        SenderHost = EnDef.host_name_ce.HOST_JPM_USB1;
                        break;
                    case 4:  //  to JPM - ATE USB2 channel
                        port = CMN_GD.ServicePort;
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF4;
                        SenderHost = EnDef.host_name_ce.HOST_JPM_USB2;
                        break;
                }
            }
        });
    }
}

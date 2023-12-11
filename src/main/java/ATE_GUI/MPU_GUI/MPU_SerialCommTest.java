package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.srial_comms_tst;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MPU_SerialCommTest extends DialogTemplate {
    //private static JList SelectionList;
    private static final String[] listItems =
            { "Service", "PCM", "LUMO", "JPM 1", "Camera","MPU GPS","AHRS","MPU Altitude","JPM 2"};
    //          0          1       2      3       4       5         6          7            8
    //private static int item=0;
    private int port = CMN_GD.ServicePort;
    EnDef.process_name_ce SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
    EnDef.host_name_ce SenderHost = EnDef.host_name_ce.HOST_ATE_SERVER;

    public MPU_SerialCommTest(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        AddStartActionListener();
        JButton BSend = new JButton ("Send!");
        BSend.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                srial_comms_tst SCT=null;
                if(port <0) {
                    System.out.println("SerialCommTest - Service comm port inactive. Can't send.");
                    return;
                }
                SCT = new srial_comms_tst();
                for (int i = 0; i < SCT.bytes.length; i += 4) { // array length should be a multiple of 4!
                    SCT.bytes[i] = (byte) 0xAB;
                    SCT.bytes[i + 1] = (byte) 0xCD;
                    SCT.bytes[i + 2] = (byte) 0xEF;
                    SCT.bytes[i + 3] = (byte) 0xA7;
                }
                main.SendMessage(port,
                        main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                        main.GetNewAddress(SenderHost, EnDef.process_name_ce.PR_ATE_TF3),
                        EnDef.msg_code_ce.MSG_CODE_SER_COMM_TST, SCT);
                CMN_GD.comm_n_tx++;
        }
        });
        d.add(BSend);
        JLabel l1 = SetLabel("Select Port, hit START and then Send repeatedly",
                new Dimension(350, 20));
        String[] display_listItems = new String[listItems.length];
        for(int i=0; i<listItems.length; i++) display_listItems[i] = "";
        if(CMN_GD.ServicePort >=0)
            display_listItems[0] = listItems[0] + "-Active";
        else
            display_listItems[0] = listItems[0] + "-Not Active";
        for(int i=1; i<listItems.length; i++) {
            if (CMN_GD.OtherPort >= 0)
                display_listItems[i] = listItems[i] + "-Active";
            else
                display_listItems[i] = listItems[i] + "-Not Active";
        }
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
                {
                    case 0:
                        port = CMN_GD.ServicePort;
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_ATE_SERVER;
                        break;
                    case 1: // pcm
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_PCM; // fake sender host address to allow echo by the DS agent
                        break;
                    case 2: // lumo
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_LUMO;
                        break;
                    case 3: // JPM
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_JPM;
                        break;
                    case 4: // camera
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_CAMERA_PRXY;
                        break;
                    case 5: // mpu gps
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_GPS_MPU_PRXY;
                        break;
                    case 6: // ahrs
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_AHRS_PRXY;
                        break;
                    case 7: // mpu altitude
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_ALTITUDE_MPU_PRXY;
                        break;
                    case 8: // JPM 2 (second channel)
                        port = CMN_GD.OtherPort; //other, non-service channel
                        SenderProcess = EnDef.process_name_ce.PR_ATE_TF3;
                        SenderHost = EnDef.host_name_ce.HOST_JPM2;
                        break;
                    // may add here more ports
                }
            }
        });
    }
}

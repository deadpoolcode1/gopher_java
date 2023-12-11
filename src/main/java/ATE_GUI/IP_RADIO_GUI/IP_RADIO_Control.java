package ATE_GUI.IP_RADIO_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.IP_RADIO_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.LM_Camera_Control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class IP_RADIO_Control extends DialogTemplate {
    private static final String[] radio_commands_listItems =
            {
                "Log_In", //0
                "Channel_Settings_1",//1
                "Channel_Settings_2",//2
                "Channel_Settings_3",//3
                "Channel_Settings_4",//4
                "Channel_Settings_5",//5
                "Start_Transmit", //6
                "Stop_Transmit", //7
                "Antennae_Mask", //8
                "Start_Streaming", //9
                "Radio_SW_Version" //10
            };
    private static JToggleButton Antenna_1_TB, Antenna_2_TB, Antenna_3_TB, Antenna_4_TB;
    IP_RADIO_Monitor host_IP_RADIO_Monitor;

    public IP_RADIO_Control(JFrame OwnerFrame, IP_RADIO_Monitor host_IP_RADIO_Monitor, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        this.host_IP_RADIO_Monitor = host_IP_RADIO_Monitor;
        AddStartActionListener();
        JLabel l1 = SetLabel("select required radio controls and hit START", new Dimension(dimension.width-10, 20));
        JLabel l2 = SetLabel("For antennae mask selection, set or reset required antenna", new Dimension(dimension.width-10, 20));
        JScrollPane radio_command_SL = SetSelectionList(radio_commands_listItems, 0);
        d.add(radio_command_SL);
        Antenna_1_TB = new JToggleButton("Antenna_1");
        ItemListener Antenna_1_itemListener = new ItemListener() {
             public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask | 0x00000001;
                    Antenna_1_TB.setSelected(true);
                }
                else {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask & 0xfffffffe;
                    Antenna_1_TB.setSelected(false);
                }
            }
        };
        Antenna_1_TB.addItemListener(Antenna_1_itemListener);
        Antenna_1_TB.setSelected(PerCurrentAntennaMask(1));
        d.add(Antenna_1_TB, BorderLayout.NORTH);

        Antenna_2_TB = new JToggleButton("Antenna_2");
        ItemListener Antenna_2_itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask | 0x00000002;
                    Antenna_2_TB.setSelected(true);
                }
                else {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask & 0xfffffffd;
                    Antenna_2_TB.setSelected(false);
                }
            }
        };
        Antenna_2_TB.addItemListener(Antenna_2_itemListener);
        Antenna_2_TB.setSelected(PerCurrentAntennaMask(2));
        d.add(Antenna_2_TB, BorderLayout.NORTH);

        Antenna_3_TB = new JToggleButton("Antenna_3");
        ItemListener Antenna_3_itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask | 0x00000004 ;
                    Antenna_3_TB.setSelected(true);
                }
                else {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask & 0xfffffffb;
                    Antenna_3_TB.setSelected(false);
                }
            }
        };
        Antenna_3_TB.addItemListener(Antenna_3_itemListener);
        Antenna_3_TB.setSelected(PerCurrentAntennaMask(3));
        d.add(Antenna_3_TB, BorderLayout.NORTH);

        Antenna_4_TB = new JToggleButton("Antenna_4");
        ItemListener Antenna_4_itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask | 0x00000008;
                    Antenna_4_TB.setSelected(true);
                }
                else {
                    host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask = host_IP_RADIO_Monitor.iP_RADIO_GD.antenna_mask & 0xfffffff7;
                    Antenna_4_TB.setSelected(false);
                }
            }
        };
        Antenna_4_TB.addItemListener(Antenna_4_itemListener);
        Antenna_4_TB.setSelected(PerCurrentAntennaMask(4));
        d.add(Antenna_4_TB, BorderLayout.NORTH);

        d.pack();
        d.setVisible(true);
    }

    private boolean PerCurrentAntennaMask(int shift) {
        int mask = 1 << (shift-1);
        if((host_IP_RADIO_Monitor.iP_RADIO_GD.current_antenna_mask & mask) == 0)
            return false;
        else
            return true;
    }


    @Override
    protected void AddStartActionListener() {
        // override this in any dialog
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                System.out.println("IP Radio Control Start!");
                if((item[0] != 0) && (!host_IP_RADIO_Monitor.iP_RADIO_GD.logged_in)) {
                    System.out.println("IP Radio Control - must log in to the radio before any other command. Ignored.");
                    return;
                }
                // each Q element is a string array with 5 items:
                // [0]-commandKey, [1]-request, [2]-serverIP, [3]-paramsBefore, [4]-paramsAfter
                String[] MsgParams = new String[5];
                // get required command index and signal the periodic radio handler to send the selected messages chain
                switch(item[0]) {
                    case 0:
                        MsgParams[0] = "Log_In";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Log_In;;
                           break; // 0
                    case 1:
                        MsgParams[0] = "Channel_Settings_1";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Channel_Settings_1;
                            break; // 1
                    case 2:
                        MsgParams[0] = "Channel_Settings_2";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Channel_Settings_2;
                            break; // 2
                    case 3:
                        MsgParams[0] = "Channel_Settings_3";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Channel_Settings_3;
                           break; // 3
                    case 4:
                        MsgParams[0] = "Channel_Settings_4";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Channel_Settings_4;
                            break; // 4
                    case 5:
                        MsgParams[0] = "Channel_Settings_5";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Channel_Settings_5;
                            break; // 5
                    case 6:
                        MsgParams[0] = "Start_Transmit";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Start_Transmit;
                           break; // 6
                    case 7:
                        MsgParams[0] = "Stop_Transmit";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Stop_Transmit;
                           break; // 7
                    case 8:
                        MsgParams[0] = "Antennae_Mask";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Antennae_Mask;
                           break;
                    case 9:
                        MsgParams[0] = "Start_Streaming";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Start_Streaming;
                           break;
                    case 10:
                        MsgParams[0] = "Radio_SW_Version";
                        MsgParams[1] = host_IP_RADIO_Monitor.iP_RADIO_GD.Radio_SW_Version;
                           break;
                    default:
                        System.out.println("IP Radio Control - illegal command selection! "+item[0]);
                        return;
                }
                try {
                    // add the message to the end of this radio HTTP msg Q; may block if the Q is full.
                    host_IP_RADIO_Monitor.iP_RADIO_GD.HTTPMsgQueue.put(MsgParams);
                } catch (InterruptedException ex) { throw new RuntimeException(ex); }
            }
        });
    }
}


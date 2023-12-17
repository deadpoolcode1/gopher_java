package ATE_GUI.IP_RADIO_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;

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
                "Radio_SW_Version", //10
                "Frequency" //11
            };
    private static JTextField radio_frequency_control_SL;
    private static final String[] radio_frequencies_list =
            {"2210","2220","2240","2260","2280","2300","2320","2340","2360","2380","2385","2390","2420","2440",
            "2452","2480","2490","4410","4420","4440","4460","4480","4500","4520","4540","4560","4580","4600",
            "4620","4640","4660","4680","4700","4720","4740","4760","4780","4800","4820","4840","4860","4880",
            "4900","4920   "};
    private static JToggleButton Antenna_1_TB, Antenna_2_TB, Antenna_3_TB, Antenna_4_TB;
    IP_RADIO_Monitor host_IP_RADIO_Monitor;
    int antenna_mask = 0xF; // = request for changes of current mask; 1111 binary; default - all four channels are active

    public IP_RADIO_Control(JFrame OwnerFrame, IP_RADIO_Monitor host_IP_RADIO_Monitor, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        this.host_IP_RADIO_Monitor = host_IP_RADIO_Monitor;
        AddStartActionListener();
        JLabel l1 = SetLabel("select required radio control, related sub-control and hit START", new Dimension(dimension.width-10, 20));
        JScrollPane radio_command_SL = SetSelectionList(radio_commands_listItems, 0);
        d.add(radio_command_SL);
        JLabel l3 = SetLabel("For Frequency selection, select required frequency", new Dimension(dimension.width-10, 20));
        JScrollPane radio_frequency_control_SL = SetSelectionList(radio_frequencies_list, 1);
        d.add(radio_frequency_control_SL);
        JLabel l2 = SetLabel("For antennae mask selection, set or reset required antenna", new Dimension(dimension.width-10, 20));
        Antenna_1_TB = new JToggleButton("Antenna_1");
        ItemListener Antenna_1_itemListener = new ItemListener() {
             public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    antenna_mask = antenna_mask | 0x00000001;
                    Antenna_1_TB.setSelected(true);
                }
                else {
                    antenna_mask = antenna_mask & 0xfffffffe;
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
                    antenna_mask = antenna_mask | 0x00000002;
                    Antenna_2_TB.setSelected(true);
                }
                else {
                    antenna_mask = antenna_mask & 0xfffffffd;
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
                    antenna_mask = antenna_mask | 0x00000004 ;
                    Antenna_3_TB.setSelected(true);
                }
                else {
                    antenna_mask = antenna_mask & 0xfffffffb;
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
                    antenna_mask = antenna_mask | 0x00000008;
                    Antenna_4_TB.setSelected(true);
                }
                else {
                    antenna_mask = antenna_mask & 0xfffffff7;
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
        if((host_IP_RADIO_Monitor.current_antenna_mask & mask) == 0)
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
                System.out.println("IP Radio Control Start, item selected=!"+item[0]);
                if((item[0] != 0) && (!host_IP_RADIO_Monitor.aTE_Radio_Interface.iP_Radio_Interface.iP_RADIO_GD.AIRS.logged_in)) {
                    System.out.println("IP Radio Control - must log in to the radio before any other command. Ignored.");
                    return;
                }
                // each radio messag element is a string array with number of items IAW with messag type:
                // [0]-always the command Key, [1], [2] specific parameters
                String[] MsgParams;
                // get required command index, build a message and send to the radio interface
                switch(item[0]) {
                    case 0:
                        MsgParams = new String[3];
                        MsgParams[0] = "Log_In";
                        MsgParams[1] = "admin"; // TODO should replace by a parameter, to allow other user names
                        MsgParams[2] = host_IP_RADIO_Monitor.aTE_Radio_Interface.iP_Radio_Interface.radio_password;
                        break; // 0
                    case 1:
                        MsgParams = new String[1];
                        MsgParams[0] = "Channel_Settings_1";
                            break; // 1
                    case 2:
                        MsgParams = new String[1];
                        MsgParams[0] = "Channel_Settings_2";
                            break; // 2
                    case 3:
                        MsgParams = new String[1];
                        MsgParams[0] = "Channel_Settings_3";
                           break; // 3
                    case 4:
                        MsgParams = new String[1];
                        MsgParams[0] = "Channel_Settings_4";
                            break; // 4
                    case 5:
                        MsgParams = new String[1];
                        MsgParams[0] = "Channel_Settings_5";
                            break; // 5
                    case 6:
                        MsgParams = new String[1];
                        MsgParams[0] = "Start_Transmit";
                           break; // 6
                    case 7:
                        MsgParams = new String[1];
                        MsgParams[0] = "Stop_Transmit";
                           break; // 7
                    case 8:
                        MsgParams = new String[2];
                        MsgParams[0] = "Antennae_Mask";
                        MsgParams[1] = String.valueOf(antenna_mask);
                           break;
                    case 9:
                        MsgParams = new String[1];
                        MsgParams[0] = "Start_Streaming";
                           break;
                    case 10:
                        MsgParams = new String[1];
                        MsgParams[0] = "Radio_SW_Version";
                           break;
                    case 11:
                        MsgParams = new String[2];
                        MsgParams[0] = "Frequency";
                        MsgParams[1] = radio_frequencies_list[item[1]];
                        break;
                    default:
                        System.out.println("IP Radio Control - illegal command selection! "+item[0]);
                        return;
                }
                host_IP_RADIO_Monitor.aTE_Radio_Interface.iP_Radio_Interface.SendToIP_Radio(MsgParams);
            }
        });
    }
}


package ATE_GUI.JPM_GUI;

import ATE_GUI.GUI_CMN.DS_Control;
import ATE_GUI.JPM_GUI.*;
import LMDS_ICD.EnDef;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JPM_TestSelect extends JPanel {
    /**
     * Manages the UI for the JPM ATE
     * Title display: UUT state
     * Controls:
     *  - DS Control - set state (LMDS_ST_SINGLE_UUT, LMDS_ST_INTEGRATED_LM, LMDS_ST_AV_Stack)
     *      Show response type, text
     *  - Serial Comm Test - select channel (service, other); send N comm test messages; display N_Tx, N_Rx, N_Rx / N_Tx
     *  - Discrete out control - select discrete, send command; Show response type, text
     * Periodic display:
     *  - Temperatures (1);
     *
     *
     *
     *      GD: UUT State, response #, response type, response text; comm test data: channel, N_Tx, N_Rx;
     *          Temperatures (1);
     */
    private static JPanel TestSelectPanel;
    private static JList TestSelectionList;
    public static JPM_TemperatureChart JPMTemperatureChart;
    public static JLabel message_line, serial_test_line, in_discrete_line;
    public static boolean init_done = false;
    JFrame HostFrame = null;

    private static final String[] listItems =
            { "Set UUT State", "Serial Comm Test", "Discrete out control" };
    //            0               1                       2

    public JPM_TestSelect(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(1000, 700);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        message_line = SetLabel("Message area", new Dimension(1000,20));
        serial_test_line = SetLabel("Serial Test results", new Dimension(1000,20));
        in_discrete_line = SetLabel("in_discrete_line", new Dimension(1000,20));
        TestSelectPanel = new JPanel();
        TestSelectionList = new JList(listItems);
        TestSelectionList.setFixedCellHeight(15);
        TestSelectionList.setFixedCellWidth(150);
        TestSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TestSelectionList.setVisibleRowCount(5);
        TestSelectionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int item = TestSelectionList.getSelectedIndex();
                System.out.println("item="+item);
                switch(item) {
                    case 0:
                        DS_Control DS_ControlDialog = new DS_Control(
                                HostFrame, "JPM State Control", new Dimension(500,300), EnDef.host_name_ce.HOST_JPM, EnDef.process_name_ce.PR_ATE_TF4);
                        break;
                    case 1:
                        JPM_SerialCommTest JPMSerialCommTestDialog = new JPM_SerialCommTest(
                                HostFrame, "Serial Comm Test", new Dimension(500,300));
                        break;
                    case 2: JPM_Discrete_Control JPMDiscrete_Control_Dialog = new JPM_Discrete_Control(
                            HostFrame, "Discrete Control Test", new Dimension(400,550));
                        break;
                    default:
                        System.out.println("TestSelect - illegal item selected: "+item);
                }
            }
        });

        TestSelectPanel.add(new JScrollPane(TestSelectionList));
        add(TestSelectPanel);
        JPMTemperatureChart = new JPM_TemperatureChart(this, 480,200,
                "Temperatures", "Time (Seconds)", "T (Celsius)");
        init_done = true;
    }

    JLabel SetLabel(String text, Dimension dim) {
        JLabel label = new JLabel (text);
        label.setMinimumSize(dim);
        label.setPreferredSize(dim);
        label.setMaximumSize(dim);
        add(label );
        return label;
    }

}


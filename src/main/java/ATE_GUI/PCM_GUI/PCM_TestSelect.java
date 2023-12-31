package ATE_GUI.PCM_GUI;

import ATE_GUI.GUI_CMN.DS_Control;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.MessageBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class PCM_TestSelect extends JPanel{
    /**
     * Manages the UI for the PCM ATE
     * Title display: UUT state
     * Controls:
     *  - DS Control - set state (LMDS_ST_SINGLE_UUT, LMDS_ST_INTEGRATED_LM, LMDS_ST_AV_ASSY)
     *      Show response type, text
     *  - Serial Comm Test - select channel (service, other); send N comm test messages; display N_Tx, N_Rx, N_Rx / N_Tx
     *  - PWM Test - select channel; set angle; Show response type, text
     *  - Discrete out control - select discrete, send command; Show response type, text
     * Periodic display:
     *  - Power voltages (4), Temperatures (3); discrete in (9);
     *
     *
     *
     * GD: UUT State, response #, response type, response text; comm test data: channel, N_Tx, N_Rx;
     *     voltages (4), Temperatures (3); discrete in (9)
     */
    private static JPanel TestSelectPanel;
    private static JList TestSelectionList;
    //public static XYLineChartAWT sinXY;
    public static PCM_Voltage_Chart PCMVoltage_Chart;
    public static PCM_TemperatureChart PCMTemperatureChart;
    public static JLabel message_line, serial_test_line, in_discrete_line, revision_state_line;
    public static boolean init_done = false;
    JFrame HostFrame = null;

    private static final String[] listItems =
            { "Set UUT State", "Serial Comm Test", "PWM Test", "Discrete out control", "Propulsion Control", "Get Revision & State" };
    //          0               1                      2           3                       4                       5

    public PCM_TestSelect(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(1000, 700);
        message_line = SetLabel("Message area", new Dimension(1000,20));
        serial_test_line = SetLabel("Serial Test results", new Dimension(1000,20));
        in_discrete_line = SetLabel("Discrete IN", new Dimension(1000,20));
        revision_state_line = SetLabel("Revision & State line:", new Dimension(1000,20));
        TestSelectPanel = new JPanel();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
                                HostFrame, "PCM State Control", new Dimension(500,300), EnDef.host_name_ce.HOST_PCM, EnDef.process_name_ce.PR_ATE_TF2);
                        break;
                    case 1:
                        PCM_SerialCommTest PCMSerialCommTestDialog = new PCM_SerialCommTest(
                                HostFrame, "Serial Comm Test", new Dimension(500,300));
                        break;
                    case 2:
                        PCM_PWM_Control_Test PCMPWM_Control_Test_Dialog = new PCM_PWM_Control_Test(
                                HostFrame, "PWM Control Test", new Dimension(420,300));
                        break;
                    case 3: PCM_Discrete_Control PCMDiscrete_Control_Dialog = new PCM_Discrete_Control(
                            HostFrame, "Discrete Control Test", new Dimension(400,550));
                        break;
                    case 4: PCM_Propulsion_Control PCM_Propulsion_Control_Dialog = new PCM_Propulsion_Control(
                            HostFrame, "PCM Propulsion Control Test", new Dimension(300,300));
                        break;
                    case 5:
                        try {
                            main.SendMessage(CMN_GD.ServicePort,
                                main.GetNewAddress(EnDef.host_name_ce.HOST_PCM, EnDef.process_name_ce.PR_TST_CMND),
                                main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF2),
                                EnDef.msg_code_ce.MSG_CODE_GET_DS_REVSN, null);
                        } catch (IOException ex) { throw new RuntimeException(ex); }
                        break;
                    default:
                        System.out.println("TestSelect - illegal item selected: "+item);
                }
            }
        });

        TestSelectPanel.add(new JScrollPane(TestSelectionList));
        add(TestSelectPanel);
        PCMVoltage_Chart = new PCM_Voltage_Chart(this, 480,250,
                "PCM Voltages", "Time (Seconds)", "Voltage");
        PCMTemperatureChart = new PCM_TemperatureChart(this, 480,200,
                "PCM Temperatures", "Time (Seconds)", "T (Celsius)");
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

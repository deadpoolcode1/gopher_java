package ATE_GUI.LUMO_GUI;

import ATE_GUI.GUI_CMN.DS_Control;
import LMDS_ICD.EnDef;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LUMO_TestSelect extends JPanel{
    /**
     * Manages the UI for the LUMO ATE
     * Title display: UUT state
     * Controls:
     *  - DS Control - set state (LMDS_ST_SINGLE_UUT, LMDS_ST_INTEGRATED_LM, LMDS_ST_LUMO_LP)
     *      Show response type, text
     *  - Serial Comm Test - select channel (service, other); send N comm test messages; display N_Tx, N_Rx, N_Rx / N_Tx
     *  - PWM Test - select channel; set angle; Show response type, text
     *  - Discrete out control - select discrete, send command; Show response type, text
     *  - ESAD control; show ESAD report
     * Periodic display:
     *  - Power voltages ( 5), Temperatures (2); air data; GPS report;
     *
     *
     *
     *      GD: UUT State, response #, response type, response text; comm test data: channel, N_Tx, N_Rx; Power voltages ( 5)
     *          Temperatures (2); air data; GPS report; ESAD reports
     */
    private static JPanel TestSelectPanel;
    private static JList TestSelectionList;
    public static LUMO_Voltage_Chart LUMOVoltage_Chart;
    public static ATE_GUI.LUMO_GUI.LUMO_AirSpeedChart LUMOAirSpeedChart;
    public static LUMO_AltitudeChart LUMOAltitudeChart;
    public static LUMO_TemperatureChart LUMOTemperatureChart;
    public static JLabel message_line, serial_test_line, in_discrete_line;
    public static boolean init_done = false;
    JFrame HostFrame = null;

    private static final String[] listItems =
            { "Set UUT State", "Serial Comm Test", "PWM Test", "Discrete out control", "ESAD control" };
    //          0               1                   2           3                       4

    public LUMO_TestSelect(JFrame HostFrame) {
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
                                HostFrame, "LUMO State Control", new Dimension(500,300), EnDef.host_name_ce.HOST_LUMO, EnDef.process_name_ce.PR_ATE_TF1);
                        break;
                    case 1:
                        LUMO_SerialCommTest LUMOSerialCommTestDialog = new LUMO_SerialCommTest(
                                HostFrame, "Serial Comm Test", new Dimension(500,300));
                        break;
                    case 2:
                        LUMO_PWM_Control_Test LUMOPWM_Control_Test_Dialog = new LUMO_PWM_Control_Test(
                                HostFrame, "PWM Control Test", new Dimension(420,300));
                        break;
                    case 3: LUMO_Discrete_Control LUMODiscrete_Control_Dialog = new LUMO_Discrete_Control(
                            HostFrame, "Discrete Control Test", new Dimension(400,550));
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("TestSelect - illegal item selected: "+item);
                }
            }
        });

        TestSelectPanel.add(new JScrollPane(TestSelectionList));
        add(TestSelectPanel);
        LUMOVoltage_Chart = new LUMO_Voltage_Chart(this, 480,250,
                "LUMO Voltages", "Time (Seconds)", "Voltage");
        LUMOAirSpeedChart = new ATE_GUI.LUMO_GUI.LUMO_AirSpeedChart(this, 480,200,
                "Air Speed", "Time (Seconds)", "Air Speed (M/Sec)");
        LUMOAltitudeChart = new LUMO_AltitudeChart(this, 480,200,
                "Altitude", "Time (Seconds)", "Altitude (AMSL)");
        LUMOTemperatureChart = new LUMO_TemperatureChart(this, 480,200,
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

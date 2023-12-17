package ATE_GUI.MPU_GUI;
import ATE_GUI.GUI_CMN.DS_Control;
import ATE_GUI.LUMO_GUI.LUMO_Config_Params_Control;
import ATE_GUI.LUMO_GUI.LUMO_TemperatureChart;
import ATE_GUI.MPU_GUI.*;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.MessageBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MPU_TestSelect extends JPanel{
    /**
     * Manages the UI for the MPU ATE
     * Title display: UUT state
     * Controls:
     *  - DS Control - set state (LMDS_ST_SINGLE_UUT, LMDS_ST_INTEGRATED_LM, LMDS_ST_AV_ASSY)
     *      Show response type, text
     *  - Serial Comm Test - select channel (service, other); send N comm test messages; display N_Tx, N_Rx, N_Rx / N_Tx
     *  - Internal Comms Test - with LUMO, JPM, PCM (dependent on state); per slave PCB, receive and display N_Tx, N_Rx, N_Rx / N_Tx
     *  - PWM Test - select channel; set angle; Show response type, text
     *  - Discrete out control - select discrete, send command; Show response type, text
     *  - Camera control; select mode, spectrum, LOS attitude, FOVH, IR calibration, IR polarity
     *  - Config Params Control; select read/write mode, select parameters set or save all, parameters entry
     * Periodic display:
     *  - Power voltages ( 6); air data (altitude); AHRS report (separate TAB);
     *  - Camera status (another TAB): mode, spectrum, LOS attitude, FOVH, serviceability, IR polarity, faults list
     *  - config parameters
     *      GD: UUT State, response #, response type, response text; comm test data: channel, N_Tx, N_Rx; Power voltages ( 4)
     *         air data (altitude); AHRS report; Camera Status; Internal Comms results (N_Tx, N_Rx per slave PCB);
     *          config parameters
     */
    private static JPanel TestSelectPanel;
    private static JList TestSelectionList;
    public static MPU_Voltage_Chart MPUVoltage_Chart;
    public static MPU_AltitudeChart MPUAltitudeChart;
    public static MPU_TemperatureChart MPUTemperatureChart;
    public static JLabel message_line, serial_test_line, in_discrete_line, internal_serial_test_line,
            config_params_line1, config_params_line2, revision_state_line;
    public static boolean init_done = false;
    JFrame HostFrame = null;

    private static final String[] listItems =
            { "Set UUT State", "Serial Comm Test", "PWM Test", "Discrete out control", "Config Params Control" , "Get Revision & State"};
    //          0               1                   2                    3                   4                      5

    public MPU_TestSelect(JFrame HostFrame) {
        this.HostFrame = HostFrame;
        setSize(1000, 700);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        message_line = SetLabel("Message area", new Dimension(1000,20));
        serial_test_line = SetLabel("External Serial Test results", new Dimension(1000,20));
        internal_serial_test_line = SetLabel("Internal Serial Test results", new Dimension(1000,20));
        in_discrete_line = SetLabel("in_discrete_line", new Dimension(1000,20));
        revision_state_line = SetLabel("Revision & State line:", new Dimension(1000,20));
        config_params_line1 = SetLabel("config_params_line1: ", new Dimension(1000,20));
        config_params_line2 = SetLabel("config_params_line2: ", new Dimension(1000,20));
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
                                HostFrame, "MPU State Control", new Dimension(500,300), EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_ATE_TF3);
                        break;
                    case 1:
                        MPU_SerialCommTest MPUSerialCommTestDialog = new MPU_SerialCommTest(
                                HostFrame, "Serial Comm Test", new Dimension(500,300));
                        break;
                    case 2:
                        MPU_PWM_Control_Test MPUPWM_Control_Test_Dialog = new MPU_PWM_Control_Test(
                                HostFrame, "PWM Control Test", new Dimension(420,300));
                        break;
                    case 3: MPU_Discrete_Control MPUDiscrete_Control_Dialog = new MPU_Discrete_Control(
                            HostFrame, "Discrete Control Test", new Dimension(400,550));
                        break;
                    case 4:
                        MPU_Config_Params_Control MPU_Config_Params_Control_Dialog = new MPU_Config_Params_Control(
                                HostFrame, "Config Params Control", new Dimension(600,350));
                        break;
                    case 5:
                        main.SendMessage(CMN_GD.ServicePort,
                                main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                                main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                                EnDef.msg_code_ce.MSG_CODE_GET_DS_REVSN, null);
                        break;
                    default:
                        System.out.println("TestSelect - illegal item selected: "+item);
                }
            }
        });

        TestSelectPanel.add(new JScrollPane(TestSelectionList));
        add(TestSelectPanel);
        MPUVoltage_Chart = new MPU_Voltage_Chart(this, 480,250,
                "MPU Voltages", "Time (Seconds)", "Voltage");
        MPUAltitudeChart = new MPU_AltitudeChart(this, 480,200,
                "Altitude", "Time (Seconds)", "Altitude (AMSL)");
        MPUTemperatureChart = new MPU_TemperatureChart(this, 480,200,
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

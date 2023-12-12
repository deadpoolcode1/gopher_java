package ATE_MAIN;


import ATE_GUI.JPM_GUI.JPM_TestSelect;
import ATE_GUI.LUMO_GUI.LUMO_TestSelect;
import ATE_GUI.MPU_GUI.MPU_TestSelect;
import ATE_GUI.PCM_GUI.PCM_TestSelect;
import LMDS_ICD.*;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;

public class MessageListener implements SerialPortMessageListener {
    static final int MAX_LENGTH = 248;
    // a class based on the jSerial data comm package. Tests receiving and serializing UUT LMDS messages
    static int cnt = 0;
    int TEST_MSG_SIZE = new TestMsg().GetSize();
    int TEXT_MSG_SIZE = new TextMsg().GetSize();
    static byte[] delimiter = {(byte) 0xca, (byte) 0xfe, (byte) 0x2d, (byte) 0xad};

    public MessageListener() {
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public byte[] getMessageDelimiter() {
        return delimiter;
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        return true;
    }

    private MessageReceivedListener receivedListener;

    public MessageListener(MessageReceivedListener listener) {
        this.receivedListener = listener;
    }

    @Override
    public void serialEvent(SerialPortEvent event)
    // called back by jSerial when a message delimited by end sync bytes is received from the UUT
    {
        cnt++;
        byte[] msg = event.getReceivedData();
        String port_name = event.getSerialPort().getSystemPortName();
        int ml = msg.length;
        if (ml > MAX_LENGTH) {
            System.out.println(port_name + " RX" + cnt + "Received msg too large, ignored. Length: " + msg.length);
        } else {
            MsgRdr MR = new MsgRdr(msg); // set for incoming msg serialization
            LMDS_HDR LM = new LMDS_HDR(MR); // read the header
            if (LM.length > ml) {
                System.out.println("MessageListener: "+port_name + " RX" + cnt +
                        "  Received msg length in header < buffer length. HDR Length=" + LM.length + "  RX Buffer length=" + ml);
            } else {
                MsgTail MT = new MsgTail(msg, LM.length - 8);
                if (MT.IsGoodChecksum(msg, LM.length)) {
                    // a good one... send to destination process
                    switch (LM.dest_address.process_name) // those processes simulate ATE specific test functions
                    {
                        case PR_ATE_TF1:
                            // use this TF for messages arriving from the LUMO UUT
                            Process_TF1_Msgs(MR, LM, msg, port_name);
                            break;
                        case PR_ATE_TF2:
                            // use this TF for messages arriving from the PCM UUT
                            Process_TF2_Msgs(MR, LM, msg, port_name);
                            break;
                        case PR_ATE_TF3:
                            // use this TF for messages arriving from the MPU UUT
                            Process_TF3_Msgs(MR, LM, msg, port_name);
                            break;
                        case PR_ATE_TF4:
                            // use this TF for messages arriving from the JPM UUT
                            Process_TF4_Msgs(MR, LM, msg, port_name);
                            break;
                        case PR_UNKNOWN:
                        default: {
                            System.out.println(port_name + " RX" + cnt + "Received msg body has illegal process address=" +
                                    LM.dest_address.process_name);
                        }
                    }

                } else {
                    System.out.println(port_name + " RX" + cnt + "Received msg has bad checksum. ignored.");
                }
            }
        }
        String messageText = extractMessageText(event); // Implement this method based on your needs
        if (receivedListener != null && messageText != null && !messageText.isEmpty()) {
            receivedListener.onMessageReceived(messageText);
        }
    }

    private String extractMessageText(SerialPortEvent event) {
        byte[] msg = event.getReceivedData();
        MsgRdr MR = new MsgRdr(msg); // set for incoming msg serialization
        LMDS_HDR LM = new LMDS_HDR(MR); // read the header
    
        Gson gson = new Gson();
        String jsonMessage = "";
    
        // Classify and serialize based on the message code
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                jsonMessage = gson.toJson(UTR);
                break;
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts TM = new temp_msrmnts(MR);
                jsonMessage = gson.toJson(TM);
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts DST = new dscrt_stts(MR);
                jsonMessage = gson.toJson(DST);
                break;
            case MSG_CODE_PWR_MSRMNT:
                pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts(MR);
                jsonMessage = gson.toJson(PSM);
                break;
            case MSG_CODE_AIR_DATA:
                air_data AD = new air_data(MR);
                jsonMessage = gson.toJson(AD);
                break;
            case MSG_CODE_SER_COMM_TST:
                srial_comms_tst commTestMsg = new srial_comms_tst(MR);
                jsonMessage = gson.toJson(commTestMsg);
                break;
            case MSG_CODE_TEXT:
                TextMsg textMsg = new TextMsg(MR);
                jsonMessage = gson.toJson(textMsg);
                break;
            case MSG_CODE_TX_GPS_MPU:
                GPS_UBX_Data gpsTextMsg = new GPS_UBX_Data(MR);
                jsonMessage = gson.toJson(gpsTextMsg);
                break;
            case MSG_CODE_TX_AHRS:
                AHRS_packet_hdr ahrsTextMsg = new AHRS_packet_hdr(MR);
                jsonMessage = gson.toJson(ahrsTextMsg);
                break;
            case MSG_CODE_INT_COMM_TST_RSLTS:
                Internal_comms_test_results ICTR = new Internal_comms_test_results(MR);
                jsonMessage = gson.toJson(ICTR);
                break;
            case MSG_CODE_EO_STTS:
                LM_Camera_Status eoStatusMsg = new LM_Camera_Status(MR);
                jsonMessage = gson.toJson(eoStatusMsg);
                break;
            default:
                System.out.println("Unknown message type: " + LM.msg_code);
                break;
        }
    
        return jsonMessage;
    }
    

    
    public static void Process_TF4_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the JPM UUT; may be called by either the LAN Rx or Serial port RX
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                CMN_GD.response_num = LM.serial_number;
                CMN_GD.response_type = UTR.response_type;
                CMN_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_SER_COMM_TST:
                CMN_GD.comm_n_rx++;
                break;
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts TM = new temp_msrmnts(MR);
                JPM_TestSelect.JPMTemperatureChart.UpdateDataset(LM.send_time_ms, TM);
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts DST = new dscrt_stts(MR);
               for (int i = 0; i < DST.dscrt_states.length; i++) {
                    switch (DST.dscrt_states[i].dscrt_id) {
                        case JPM_DSCRT_IN_MCU_HW_RST_INHIBIT:
                            JPM_GD.MCU_HW_RST_INHIBIT = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case JPM_DSCRT_IN_VPU_GP1:
                            JPM_GD.VPU_GP1 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case JPM_DSCRT_IN_VPU_GP2:
                            JPM_GD.VPU_GP2 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case JPM_DSCRT_IN_VPU_GP3:
                            JPM_GD.VPU_GP3 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case JPM_DSCRT_IN_VPU_GP4:
                            JPM_GD.VPU_GP4 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                            // GPIO11, 12 used for testing only
                        case JPM_DSCRT_IN_GPIO11:
                            JPM_GD.GPIO11 = DST.dscrt_states[i].dscrt_val.ordinal();
                        case JPM_DSCRT_IN_GPIO12:
                            JPM_GD.GPIO12 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case DSCRT_UNKNOWN:
                            break; // Some fields may not be populated.
                        default:
                            System.out.println("Process_TF4_Msgs - unknown discrete report: "+DST.dscrt_states[i].dscrt_id);
                    }
                }
                break;
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(port_name + " RX" + cnt + "TF4 (JPM) Received msg body has illegal/unsupported msg code=" +
                        LM.msg_code);
            }
        }
    }

    private void Process_TF3_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the MPU UUT
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                CMN_GD.response_num = LM.serial_number;
                CMN_GD.response_type = UTR.response_type;
                CMN_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_PWR_MSRMNT:
                pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts(MR);
                MPU_TestSelect.MPUVoltage_Chart.UpdateDataset(LM.send_time_ms, PSM);
                break;
            case MSG_CODE_AIR_DATA:
                air_data AD = new air_data(MR);
                MPU_TestSelect.MPUAltitudeChart.UpdateDataset(LM.send_time_ms, AD);
                break;
            case MSG_CODE_SER_COMM_TST:
                CMN_GD.comm_n_rx++;
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts DST = new dscrt_stts(MR);
                for (int i = 0; i < DST.dscrt_states.length; i++) {
                    // TODO check for unknown discrete value
                    switch (DST.dscrt_states[i].dscrt_id) {
                        case DSCRT_UNKNOWN:
                            //System.out.println("Process_TF3_Msgs - unknown discrete report (1)");
                            break;
                        case MPU_DSCRT_IN_MCU_HW_RST:
                            MPU_GD.dscrt_in_MCU_HW_RST = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case MPU_DSCRT_IN_MPU_GPS_1PPS:
                            MPU_GD.dscrt_in_MPU_GPS_1PPS = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case MPU_DSCRT_IN_DISC_IN4_CON:
                            MPU_GD.dscrt_in_DISC_IN4_CON = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case MPU_DSCRT_IN_DISC_IN5_CON:
                            MPU_GD.dscrt_in_DISC_IN5_CON = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case MPU_DSCRT_IN_JPM_FORCE_OFF_OUT_3V3:
                            MPU_GD.dscrt_in_JPM_FORCE_OFF_OUT_3V3 = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        default:
                            System.out.println("Process_TF3_Msgs - unknown discrete report (2)");
                    }
                }
                break;
            case MSG_CODE_TX_GPS_MPU:
                // GPS message from the GPS on the NAV board controlled by the MPU
                byte[] gps_msg_body = MessageBody.GetBytes(LM, msg);
                // assume the GPS connected to the MPU (if any) is same as the one conected to the LUMO
                LUMO_GD.gPS_UBX_Data.Update(gps_msg_body);
                break;
            case MSG_CODE_TX_AHRS:
                // AHRS report message from the AHRS controlled by the MPU
                byte[] AHRS_msg_body = MessageBody.GetBytes(LM, msg);
                MPU_GD.NAVD.Update(AHRS_msg_body);
                break;
            case MSG_CODE_INT_COMM_TST_RSLTS:
                Internal_comms_test_results ICTR = new Internal_comms_test_results(MR);
                MPU_GD.ICTR = "";
                for (int i = 0; i < ICTR.int_comms_test_results.length; i++){
                    if(ICTR.int_comms_test_results[i].host_A == EnDef.host_name_ce.HOST_UNKNOWN)
                        continue;
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].host_A+"-";
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].host_B+": ";
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].MER+"; ";
                }
                    break;
            case MSG_CODE_EO_STTS:
                MPU_GD.LMCS = new LM_Camera_Status(MR);
                byte[] EO_msg_body = MessageBody.GetBytes(LM, msg);
                ProcessEOMessage(EO_msg_body);
                break;
            case MSG_CODE_TEXT: {
                // for now, we only have the same TX "TextMsg"
                if (rx_body_size != TEXT_MSG_SIZE) {
                    System.out.println(port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
                            rx_body_size + "class length=" + TEXT_MSG_SIZE);
                    break;
                }
                TextMsg TXsg = new TextMsg(MR);
                System.out.println(port_name + " RX" + cnt + " OK- " + TXsg.ToString());
                break;
            }
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts TM = new temp_msrmnts(MR);
                MPU_TestSelect.MPUTemperatureChart.UpdateDataset(LM.send_time_ms, TM);
                break;
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(port_name + " RX" + cnt + "TF3 (MPU) Received msg body has illegal/unsupported msg code=" +
                        LM.msg_code);
            }
        }
    }

    private void ProcessEOMessage(byte[] EO_msg_body) {
        // parse an EO status message (in ATE-LMDS ICD) and update the EO monitor display
    }

    private void Process_TF2_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the PCM UUT
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                CMN_GD.response_num = LM.serial_number;
                CMN_GD.response_type = UTR.response_type;
                CMN_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_PRPLSN_STTS:
                PCM_GD.lM_Propulsion_Status = new LM_Propulsion_Status(MR);
                break;
            case MSG_CODE_BATTERY_STTS:
                battery_stts BTS = new battery_stts(MR);
                PCM_GD.bat_chg_inh = BTS.CHG_INH ;
                PCM_GD.bat_current = BTS.Battery_Current ;
                PCM_GD.bat_hi = BTS.BATHI ;
                PCM_GD.bat_internal_temp = BTS.Internal_Temp ;
                PCM_GD.bat_lo = BTS.BATLO ;
                PCM_GD.bat_monitor_comm = BTS.Battery_monitor_comms_OK ;
                PCM_GD.bat_voltage = BTS.Battery_Voltage ;
                break;
            case MSG_CODE_PWR_MSRMNT:
                pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts(MR);
                PCM_TestSelect.PCMVoltage_Chart.UpdateDataset(LM.send_time_ms, PSM);
                break;
            case MSG_CODE_SER_COMM_TST:
                CMN_GD.comm_n_rx++;
                break;
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts TM = new temp_msrmnts(MR);
                PCM_TestSelect.PCMTemperatureChart.UpdateDataset(LM.send_time_ms, TM);
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts DST = new dscrt_stts(MR);
                for (int i = 0; i < DST.dscrt_states.length; i++) {
                    // TODO check for unknown discrete value
                    switch (DST.dscrt_states[i].dscrt_id) {
                        case DSCRT_UNKNOWN:
                            //System.out.println("Process_TF2_Msgs - unknown discrete report (1)");
                            break;
                        case PCM_DSCRT_IN_ACK_FIRE:
                            PCM_GD.dscrt_in_ack_fire = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_LAUNCH_MICRO_SW:
                            PCM_GD.dscrt_in_LAUNCH_MICRO_SW = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_ACK_8_2_OK:
                            PCM_GD.dscrt_in_ACK_8_2_OK = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_PAT_PB:
                            PCM_GD.dscrt_in_PAT_PB = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_LAUNCH_RS:
                            PCM_GD.dscrt_in_LAUNCH_RS = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_LAUNCH_PB:
                            PCM_GD.dscrt_in_LAUNCH_PB = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_SW_BIT_OK:
                            PCM_GD.dscrt_in_SW_BIT_OK = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_CPLD_DONE:
                            PCM_GD.dscrt_in_CPLD_DONE = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case PCM_DSCRT_IN_CUT_OFF:
                            PCM_GD.dscrt_in_CUT_OFF = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        default:
                            System.out.println("Process_TF2_Msgs - unknown discrete report (2)");
                    }
                }
                break;
            case MSG_CODE_TEXT: {
                // for now, we only have the same TX "TextMsg"
                if (rx_body_size != TEXT_MSG_SIZE) {
                    System.out.println(port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
                            rx_body_size + "class length=" + TEXT_MSG_SIZE);
                    break;
                }
                TextMsg TXsg = new TextMsg(MR);
                System.out.println(port_name + " RX" + cnt + " OK- " + TXsg.ToString());
                break;
            }
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(port_name + " RX" + cnt + "TF1 (LUMO) Received msg body has illegal/unsupported msg code=" +
                        LM.msg_code);
            }
        }
    }

    private void Process_TF1_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the LUMO UUT
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                CMN_GD.response_num = LM.serial_number;
                CMN_GD.response_type = UTR.response_type;
                CMN_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_PWR_MSRMNT:
                pwr_sgnals_msrmnts PSM = new pwr_sgnals_msrmnts(MR);
                LUMO_TestSelect.LUMOVoltage_Chart.UpdateDataset(LM.send_time_ms, PSM);
                break;
            case MSG_CODE_AIR_DATA:
                air_data AD = new air_data(MR);
                LUMO_TestSelect.LUMOAirSpeedChart.UpdateDataset(LM.send_time_ms, AD);
                LUMO_TestSelect.LUMOAltitudeChart.UpdateDataset(LM.send_time_ms, AD);
                break;
            case MSG_CODE_SER_COMM_TST:
                CMN_GD.comm_n_rx++;
                break;
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts TM = new temp_msrmnts(MR);
                LUMO_TestSelect.LUMOTemperatureChart.UpdateDataset(LM.send_time_ms, TM);
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts DST = new dscrt_stts(MR);
                LUMO_GD.dip_switch = 0;
                for (int i = 0; i < DST.dscrt_states.length; i++) {
                    // TODO check for unknown discrete value
                    switch (DST.dscrt_states[i].dscrt_id) {
                        case DSCRT_UNKNOWN:
                            //System.out.println("Process_TF1_Msgs - unknown discrete report (1)");
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_0:
                            LUMO_GD.dip_switch += DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_1:
                            LUMO_GD.dip_switch += 2*DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_2:
                            LUMO_GD.dip_switch += 4*DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_UMBILICAL_INIT:
                            LUMO_GD.umbili_init = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_NG_ESAD_STATUS:
                            LUMO_GD.NG_ESAD_Status = DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        default:
                            System.out.println("Process_TF1_Msgs - unknown discrete report (2)");
                    }
                }
                break;
            case MSG_CODE_TX_FUZE:
                byte[] fuze_msg_body = MessageBody.GetBytes(LM, msg);
                ProcessFuzeMessage(fuze_msg_body);
                break;
            case MSG_CODE_TX_GPS_UBLOX:
                byte[] gps_msg_body = MessageBody.GetBytes(LM, msg);
                LUMO_GD.gPS_UBX_Data.Update(gps_msg_body);
                break;
            case MSG_CODE_TX_SAASM:
                byte[] saasm_msg_body = MessageBody.GetBytes(LM, msg);
                ProcessSAASMMessage(saasm_msg_body);
                break;
            case MSG_CODE_TEXT: {
                // for now, we only have the same TX "TextMsg"
                if (rx_body_size != TEXT_MSG_SIZE) {
                    System.out.println(port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
                            rx_body_size + "class length=" + TEXT_MSG_SIZE);
                    break;
                }
                TextMsg TXsg = new TextMsg(MR);
                System.out.println(port_name + " RX" + cnt + " OK- " + TXsg.ToString());
                break;
            }
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(port_name + " RX" + cnt + "TF1 (LUMO) Received msg body has illegal/unsupported msg code=" +
                        LM.msg_code);
            }
        }
    }

    private void ProcessSAASMMessage(byte[] saasm_msg_body) {
    }

    private void ProcessFuzeMessage(byte[] fuze_msg_body) {
    }
}
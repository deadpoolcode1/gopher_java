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
import com.yourcompany.app.Database;
import com.yourcompany.app.MConfig;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.Map;
import java.util.LinkedList;

public class MessageListener implements SerialPortMessageListener {
    static final int MAX_LENGTH = 248;
    final static int MSG_FRAME_SIZE = 32 + 8; // header + tail
    static int cnt = 0;
    int TEST_MSG_SIZE = new TestMsg().GetSize();
    int TEXT_MSG_SIZE = new TextMsg().GetSize();
    int port_index = -1;
    static byte[] delimiter = { (byte) 0xca, (byte) 0xfe, (byte) 0x2d, (byte) 0xad };

    private static final ExecutorService messageProcessingService = Executors.newFixedThreadPool(10);
    private static final BlockingQueue<Runnable> writeQueue = new LinkedBlockingQueue<>();
    private static final ConcurrentMap<String, PrintWriter> fileWriters = new ConcurrentHashMap<>();

    public MessageListener(int port_index) {
        this.port_index = port_index;
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

    @Override
    public void serialEvent(SerialPortEvent event) {
        cnt++;
        byte[] msg = event.getReceivedData();
        String portName = event.getSerialPort().getSystemPortName();
        int ml = msg.length;

        // Handle invalid message lengths early
        if (ml > MAX_LENGTH || ml < MSG_FRAME_SIZE) {
            System.out.println(
                    portName + " RX" + cnt + " Received msg too large or too small, ignored. Length: " + ml);
            return;
        }

        MsgRdr MR = new MsgRdr(msg);
        LMDS_HDR LM = new LMDS_HDR(MR);

        // Check if message length is less than buffer length
        if (LM.length > ml) {
            System.out.println("MessageListener: " + portName + " RX" + cnt +
                    " Received msg length in header < buffer length. HDR Length=" + LM.length + "  RX Buffer length="
                    + ml);
            return;
        }

        MsgTail MT = new MsgTail(msg, LM.length - 8);
        if (!MT.IsGoodChecksum(msg, LM.length)) {
            System.out.println(portName + " RX" + cnt + " Received msg has bad checksum. ignored.");
            return;
        }

        if (CMN_GD.ATE_SIM_MODE) {
            processSimulatedModeMessages(MR, LM, msg, portName); // Define this method to handle simulated mode messages
        } else {
            processRealModeMessages(portName, MR, LM, msg); // Define this method to handle real mode messages
        }
    }

    private void processSimulatedModeMessages(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        switch (LM.dest_address.process_name) {
            case PR_ATE_TF1:
                Process_TF1_Msgs(MR, LM, msg, port_name);
                break;
            case PR_ATE_TF2:
                Process_TF2_Msgs(MR, LM, msg, port_name);
                break;
            case PR_ATE_TF3:
                Process_TF3_Msgs(MR, LM, msg, port_name);
                break;
            case PR_ATE_TF4:
                Process_TF4_Msgs(MR, LM, msg, port_name);
                break;
            case PR_UNKNOWN:
            default:
                System.out.println(port_name + " RX" + cnt + "Received msg body has illegal process address=" +
                        LM.dest_address.process_name);
        }
    }

    public static synchronized int getNextLogNumber(String fileName) {
        // Synchronized to avoid concurrent access issues
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return 1; // start from 1 if the file doesn't exist
            }
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return 1;
            }
            String lastLine = lines.get(lines.size() - 1);
            String[] parts = lastLine.split("=");
            int lastLogNumber = Integer.parseInt(parts[0].substring(11)); // extracts the number after "log_number:"
            return lastLogNumber + 1;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 1; // default to 1 on any error
        }
    }

    static {
        Thread fileWriterThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable writeTask = writeQueue.take();
                    writeTask.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        fileWriterThread.start();
    }

    private static boolean isFirstWrite = true; // Tracks if it's the first write operation since the software started

    private static void appendToLogFile(String fileName, String message) {
        fileWriters.computeIfAbsent(fileName, k -> {
            try {
                // Open the file in overwrite mode if it's the first write, otherwise in append
                // mode
                FileWriter fw = new FileWriter(k, !isFirstWrite);
                BufferedWriter bw = new BufferedWriter(fw);
                return new PrintWriter(bw);
            } catch (IOException e) {
                System.err.println("Error opening file: " + e.getMessage());
                return null;
            }
        });

        // Write the message to the file
        writeQueue.add(() -> {
            PrintWriter out = fileWriters.get(fileName);
            if (out != null) {
                // out.println(message + "\r\n"); //SF out.println already adds a carriage
                // return
                out.println(message);
                out.flush(); // Ensure the message is written immediately
            } else {
                System.err.println("FileWriter not initialized for: " + fileName);
            }
        });

        // After the first write, all subsequent writes should append to the file
        isFirstWrite = false;
    }

    public void shutdown() {
        messageProcessingService.shutdown(); // Shutdown the executor service
        // Close all file writers
        for (Map.Entry<String, PrintWriter> entry : fileWriters.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                System.err.println("Error closing file: " + entry.getKey() + " - " + e.getMessage());
            }
        }
    }

    private void processRealModeMessages(String portName, MsgRdr MR, LMDS_HDR LM, byte[] msg) {
        messageProcessingService.submit(() -> {
            if (MConfig.getFakeDatabase()) {
                Pair<String, String> decodedMessage = Decode_Msg_Into_Json_String(port_index, MR, LM, msg);
                String concatenatedJson = decodedMessage.getLeft() + decodedMessage.getRight();

                // Determine the file name
                String fileName = "msg" + portName + ".txt";

                appendToLogFile(fileName, concatenatedJson);
            } else {
                String checkRequestPendingQuery = "SELECT id FROM read_data WHERE com = '" + portName
                        + "' ORDER BY timestamp_pending_issued DESC";
                List<String> requestData = Database.executeQuery("my_data", checkRequestPendingQuery,
                        MConfig.getDBServer(),
                        MConfig.getDBUsername(), MConfig.getDBPassword());

                if (requestData.isEmpty()) {
                    return;
                }
                String readDataId = requestData.get(0);
                checkRequestPendingQuery = "SELECT request_pending FROM read_data WHERE com = '" + portName
                        + "' ORDER BY timestamp_pending_issued DESC";
                requestData = Database.executeQuery("my_data", checkRequestPendingQuery, MConfig.getDBServer(),
                        MConfig.getDBUsername(), MConfig.getDBPassword());

                if (requestData.isEmpty()) {
                    return;
                }
                String requestPending = requestData.get(0);

                if (!"1".equals(requestPending)) {
                    return;
                }

                Pair<String, String> decodedMessage = Decode_Msg_Into_Json_String(port_index, MR, LM, msg);
                int err_code = 0;
                if ("-1".equals(decodedMessage.getRight())) {
                    err_code = -1;
                }
                String concatenatedJson = decodedMessage.getLeft() + decodedMessage.getRight();
                String insertQuery = "INSERT INTO read_data_info (read_data_id, data) VALUES ('"
                        + readDataId + "', '"
                        + concatenatedJson +"')";
                Database.executeNonQuery("my_data", insertQuery, MConfig.getDBServer(), MConfig.getDBUsername(),
                        MConfig.getDBPassword());
                String insertQuery2 = "INSERT INTO read_data_info (read_data_id, error_code) VALUES ('"
                        + readDataId + "', '"
                        + err_code + "')";
                Database.executeNonQuery("my_data", insertQuery2, MConfig.getDBServer(), MConfig.getDBUsername(),
                        MConfig.getDBPassword());
            }
        });
    }

    private Pair<String, String> Decode_Msg_Into_Json_String(int portIndex, MsgRdr mr, LMDS_HDR lm, byte[] msg) {
        Gson gson = new Gson();
        String headerJsonString = gson.toJson(lm);
        String bodyJsonString = "{}"; // Default empty JSON object

        switch (lm.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns rspns = new UUT_rspns(mr);
                bodyJsonString = gson.toJson(rspns);
                break;
            case MSG_CODE_GET_DS_REVSN:
                DS_Revision dsRev = new DS_Revision(mr);
                bodyJsonString = gson.toJson(dsRev);
                break;
            case MSG_CODE_TEMP_MSRMNTS:
                temp_msrmnts tempMsrmnts = new temp_msrmnts(mr);
                bodyJsonString = gson.toJson(tempMsrmnts);
                break;
            case MSG_CODE_DSCRT_STTS:
                dscrt_stts dscrtStts = new dscrt_stts(mr);
                bodyJsonString = gson.toJson(dscrtStts);
                break;
            case MSG_CODE_PWR_MSRMNT:
                pwr_sgnals_msrmnts pwrMsrmnts = new pwr_sgnals_msrmnts(mr);
                bodyJsonString = gson.toJson(pwrMsrmnts);
                break;
            case MSG_CODE_AIR_DATA:
                air_data airData = new air_data(mr);
                bodyJsonString = gson.toJson(airData);
                break;
            case MSG_CODE_TEXT:
                TextMsg textMsg = new TextMsg(mr);
                bodyJsonString = gson.toJson(textMsg);
                break;
            case MSG_CODE_SER_COMM_TST:
                srial_comms_tst sct = new srial_comms_tst(mr);
                bodyJsonString = gson.toJson(sct);
                break;
            case MSG_CODE_INT_COMM_TST_RSLTS: // SF missing
                Internal_comms_test_results ICTR = new Internal_comms_test_results(mr);
                bodyJsonString = gson.toJson(ICTR);
                break;
            case MSG_CODE_EO_STTS:
                MPU_GD.LMCS = new LM_Camera_Status(mr); // SF missing
                bodyJsonString = gson.toJson(MPU_GD.LMCS);
                break;
            case MSG_CODE_WRITE_GET_CNFG_PARAM: // SF missing
                config_param CP = new config_param(mr);
                bodyJsonString = gson.toJson(CP);
                break;
            case MSG_CODE_TX_GPS_MPU: // SF missing
            case MSG_CODE_TX_GPS_UBLOX: // SF fixed bug
                byte[] gps_msg_body = MessageBody.GetBytes(lm, msg);
                LUMO_GD.gPS_UBX_Data.Update(gps_msg_body);
                // GPS_UBX_NAV gpsNav = new GPS_UBX_NAV(mr);
                bodyJsonString = gson.toJson(LUMO_GD.gPS_UBX_Data);
                break;
            case MSG_CODE_PRPLSN_STTS: // SF missing
                PCM_GD.lM_Propulsion_Status = new LM_Propulsion_Status(mr);
                bodyJsonString = gson.toJson(PCM_GD.lM_Propulsion_Status);
                break;
            case MSG_CODE_BATTERY_STTS: // SF missing
                battery_stts BTS = new battery_stts(mr);
                bodyJsonString = gson.toJson(BTS);
                break;
            case MSG_CODE_TX_AHRS: // SF fixed bug
                byte[] AHRS_msg_body = MessageBody.GetBytes(lm, msg);
                MPU_GD.NAVD.Update(AHRS_msg_body);
                bodyJsonString = gson.toJson(MPU_GD.NAVD);
                // AHRS_System ahrsSystem = new AHRS_System(mr);
                // bodyJsonString = gson.toJson(ahrsSystem);
                break;
            case MSG_CODE_UNKNOWN:
            default:
                System.out.println("Unknown or unhandled message code: " + lm.msg_code);
                bodyJsonString = "-1";
                break;
        }

        return new ImmutablePair<>(headerJsonString, bodyJsonString);
    }

    public static void Process_TF4_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the JPM UUT; may be called by either the LAN Rx or
        // Serial port RX
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                JPM_GD.response_num = LM.serial_number;
                JPM_GD.response_type = UTR.response_type;
                JPM_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_GET_DS_REVSN:
                DS_Revision DSR = new DS_Revision(MR);
                JPM_GD.revision = new String(DSR.DS_revision, StandardCharsets.UTF_8);
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
                            System.out.println(
                                    "Process_TF4_Msgs - unknown discrete report: " + DST.dscrt_states[i].dscrt_id);
                    }
                }
                break;
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(
                        port_name + " RX" + cnt + "TF4 (JPM) Received msg body has illegal/unsupported msg code=" +
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
                MPU_GD.response_num = LM.serial_number;
                MPU_GD.response_type = UTR.response_type;
                MPU_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_GET_DS_REVSN:
                DS_Revision DSR = new DS_Revision(MR);
                MPU_GD.revision = new String(DSR.DS_revision, StandardCharsets.UTF_8);
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
                            // System.out.println("Process_TF3_Msgs - unknown discrete report (1)");
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
                // assume the GPS connected to the MPU (if any) is same as the one conected to
                // the LUMO
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
                for (int i = 0; i < ICTR.int_comms_test_results.length; i++) {
                    if (ICTR.int_comms_test_results[i].host_A == EnDef.host_name_ce.HOST_UNKNOWN)
                        continue;
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].host_A + "-";
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].host_B + ": ";
                    MPU_GD.ICTR += ICTR.int_comms_test_results[i].MER + "; ";
                }
                break;
            case MSG_CODE_EO_STTS:
                MPU_GD.LMCS = new LM_Camera_Status(MR);
                // byte[] EO_msg_body = MessageBody.GetBytes(LM, msg);
                // ProcessEOMessage(EO_msg_body);
                break;
            case MSG_CODE_WRITE_GET_CNFG_PARAM:
                config_param CP = new config_param(MR);
                ProcessGetConfigParamMsg(CP);
                break;
            case MSG_CODE_TEXT: {
                // for now, we only have the same TX "TextMsg"
                if (rx_body_size != TEXT_MSG_SIZE) {
                    System.out.println(
                            port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
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
                System.out.println(
                        port_name + " RX" + cnt + "TF3 (MPU) Received msg body has illegal/unsupported msg code=" +
                                LM.msg_code);
            }
        }
    }

    private void ProcessEOMessage(byte[] EO_msg_body) {
        // parse an EO status message (in ATE-LMDS ICD) and update the EO monitor
        // display
    }

    private void Process_TF2_Msgs(MsgRdr MR, LMDS_HDR LM, byte[] msg, String port_name) {
        // process messages from the PCM UUT
        int rx_body_size = msg.length - (LM.GetSize() + 8);
        switch (LM.msg_code) {
            case MSG_CODE_CMND_RSPNS:
                UUT_rspns UTR = new UUT_rspns(MR);
                PCM_GD.response_num = LM.serial_number;
                PCM_GD.response_type = UTR.response_type;
                PCM_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_GET_DS_REVSN:
                DS_Revision DSR = new DS_Revision(MR);
                PCM_GD.revision = new String(DSR.DS_revision, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_PRPLSN_STTS:
                PCM_GD.lM_Propulsion_Status = new LM_Propulsion_Status(MR);
                break;
            case MSG_CODE_BATTERY_STTS:
                battery_stts BTS = new battery_stts(MR);
                PCM_GD.bat_chg_inh = BTS.CHG_INH;
                PCM_GD.bat_current = BTS.Battery_Current;
                PCM_GD.bat_hi = BTS.BATHI;
                PCM_GD.bat_internal_temp = BTS.Internal_Temp;
                PCM_GD.bat_lo = BTS.BATLO;
                PCM_GD.bat_monitor_comm = BTS.Battery_monitor_comms_OK;
                PCM_GD.bat_voltage = BTS.Battery_Voltage;
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
                            // System.out.println("Process_TF2_Msgs - unknown discrete report (1)");
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
                    System.out.println(
                            port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
                                    rx_body_size + "class length=" + TEXT_MSG_SIZE);
                    break;
                }
                TextMsg TXsg = new TextMsg(MR);
                System.out.println(port_name + " RX" + cnt + " OK- " + TXsg.ToString());
                break;
            }
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(
                        port_name + " RX" + cnt + "TF1 (LUMO) Received msg body has illegal/unsupported msg code=" +
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
                LUMO_GD.response_num = LM.serial_number;
                LUMO_GD.response_type = UTR.response_type;
                LUMO_GD.response_text = new String(UTR.response_text, StandardCharsets.UTF_8);
                break;
            case MSG_CODE_GET_DS_REVSN:
                DS_Revision DSR = new DS_Revision(MR);
                LUMO_GD.revision = new String(DSR.DS_revision, StandardCharsets.UTF_8);
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
                            // System.out.println("Process_TF1_Msgs - unknown discrete report (1)");
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_0:
                            LUMO_GD.dip_switch += DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_1:
                            LUMO_GD.dip_switch += 2 * DST.dscrt_states[i].dscrt_val.ordinal();
                            break;
                        case LUMO_DSCRT_IN_DIP_SW_2:
                            LUMO_GD.dip_switch += 4 * DST.dscrt_states[i].dscrt_val.ordinal();
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
            case MSG_CODE_WRITE_GET_CNFG_PARAM:
                config_param CP = new config_param(MR);
                ProcessGetConfigParamMsg(CP);
                break;
            case MSG_CODE_TEXT: {
                // for now, we only have the same TX "TextMsg"
                if (rx_body_size != TEXT_MSG_SIZE) {
                    System.out.println(
                            port_name + " RX" + cnt + "Received msg body has conflicting sizes. calculated Length=" +
                                    rx_body_size + "class length=" + TEXT_MSG_SIZE);
                    break;
                }
                TextMsg TXsg = new TextMsg(MR);
                System.out.println(port_name + " RX" + cnt + " OK- " + TXsg.ToString());
                break;
            }
            case MSG_CODE_UNKNOWN:
            default: {
                System.out.println(
                        port_name + " RX" + cnt + "TF1 (LUMO) Received msg body has illegal/unsupported msg code=" +
                                LM.msg_code);
            }
        }
    }

    private void ProcessGetConfigParamMsg(config_param cp) {
        switch (cp.config_params_id) {
            case CP_LUMO_PITOT_GN_OFST:
                LUMO_GD.pitot_gain = cp.data[0];
                LUMO_GD.pitot_offset = cp.data[1];
                break;
            case CP_LUMO_ALT_GN_OFST:
                LUMO_GD.alt_gain = cp.data[0];
                LUMO_GD.alt_offset = cp.data[1];
                break;
            case CP_LUMO_PARA_CLS_OPN_RLS_PWM:
                LUMO_GD.para_close = ToShort(cp.data[0], cp.data[1]);
                LUMO_GD.para_open = ToShort(cp.data[2], cp.data[3]);
                LUMO_GD.para_rls = ToShort(cp.data[4], cp.data[5]);
                break;
            case CP_LUMO_AV_FUZE_COMB:
                LUMO_GD.av_fuze_comb = cp.data[0];
                break;
            case CP_LUMO_CMRA_BORSIT_OFST:
                // TODO not supported 6-10-2023
                LUMO_GD.cmra_brsit_pitch_offset = ToShort(cp.data[0], cp.data[1]);
                LUMO_GD.cmra_brsit_yaw_offset = ToShort(cp.data[2], cp.data[3]);
                break;
            case CP_MPU_TAIL_OFST:
                MPU_GD.rdr_1_ofst = cp.data[0];
                MPU_GD.rdr_2_ofst = cp.data[1];
                MPU_GD.rdr_3_ofst = cp.data[2];
                MPU_GD.rdr_4_ofst = cp.data[3];
                break;
            case CP_MPU_AILRN_OFST:
                MPU_GD.ailrn_1_ofst = cp.data[0];
                MPU_GD.ailrn_2_ofst = cp.data[1];
                MPU_GD.ailrn_3_ofst = cp.data[2];
                MPU_GD.ailrn_4_ofst = cp.data[3];
                break;
            default:
                System.out.println("Message Listener - illegal parameter ID. Ignored.  " + cp.config_params_id);
                return;
        }
    }

    private short ToShort(byte lo, byte hi) {
        return (short) ((lo & 0xff) | ((hi & 0xff) << 8));
    }

    private void ProcessSAASMMessage(byte[] saasm_msg_body) {
    }

    private void ProcessFuzeMessage(byte[] fuze_msg_body) {
    }
}
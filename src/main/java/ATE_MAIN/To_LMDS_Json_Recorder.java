package ATE_MAIN;

import LMDS_ICD.LMDS_HDR;
import LMDS_ICD.MessageBody;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

public class To_LMDS_Json_Recorder {
    /**
     * records messages sent from the ATE SIM to the LMDS. Enabled externally.
     * Uses Google GSON class for decoding the message class instances into a Json string.
     * File path given as a parameter. The record is a Json string of the inner class "Record_Json_Object"
     */

    private class Record_Json_Object {
        int line_index;
        int port_ix;
        LMDS_HDR LMH;
        MessageBody MBDY;
        private Record_Json_Object(int line_index, int port_ix, LMDS_HDR LMH, MessageBody MBDY) {
           this.line_index = line_index;
           this.port_ix = port_ix;
           this.LMH = LMH;
           this.MBDY = MBDY;
        }
    }
    public static Gson gson;
    FileWriter Json_Recorder;
    int counter = 0;
    public To_LMDS_Json_Recorder(String recording_file_folder_path) throws IOException {
        gson = new Gson();
        Json_Recorder = new FileWriter(recording_file_folder_path+"/ToLMDS_JsonFromGopher.json", false);
    }
    public void Add_Json_Record(int port_ix, LMDS_HDR LMH, MessageBody MBDY) throws IOException {
        Record_Json_Object RJO = new Record_Json_Object(counter, port_ix, LMH, MBDY);
        String record = gson.toJson(RJO);
        Json_Recorder.write(record+"\n");
        Json_Recorder.flush();
        counter++;
    }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class LM_Camera_Status extends MessageBody {
   int Size;
   public byte[] camera_id;
   public EnDef.camera_mode_ce camera_mode;
   public EnDef.spectrum_ce spectrum;
   public attitude LOS_body_attitude;
   public float FOVH;
   public EnDef.IR_Polarity_ce IR_Polarity;
   public float Temperature;
   public EnDef.Serviceability_ce camera_serviceability;
   public int[] Camera_Faults_List;
   public LM_Camera_Status() {
       Size = 136;
       camera_id = new byte[20];
       LOS_body_attitude = new attitude();
       Camera_Faults_List = new int[20];
   }
   public int GetSize() {return Size;}
   public LM_Camera_Status Clone() {
       LM_Camera_Status copy = new LM_Camera_Status();
       copy.camera_id = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.camera_id[ix__] = camera_id[ix__];
       copy.camera_mode = camera_mode;
       copy.spectrum = spectrum;
       copy.LOS_body_attitude = LOS_body_attitude.Clone();
       copy.FOVH = FOVH;
       copy.IR_Polarity = IR_Polarity;
       copy.Temperature = Temperature;
       copy.camera_serviceability = camera_serviceability;
       copy.Camera_Faults_List = new int[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.Camera_Faults_List[ix__] = Camera_Faults_List[ix__];
       return copy;
   }
   public LM_Camera_Status(MsgRdr MR) {
       Size = 136;
       camera_id = new byte[20];
       Camera_Faults_List = new int[20];
       for(int ix__=0;ix__< 20; ix__++)
           camera_id[ix__] = MR.Read_byte();
       camera_mode = EnDef.camera_mode_ce.values()[MR.Read_int()];
       spectrum = EnDef.spectrum_ce.values()[MR.Read_int()];
       LOS_body_attitude = new attitude(MR);
       FOVH = MR.Read_float();
       IR_Polarity = EnDef.IR_Polarity_ce.values()[MR.Read_int()];
       Temperature = MR.Read_float();
       camera_serviceability = EnDef.Serviceability_ce.values()[MR.Read_int()];
       for(int ix__=0;ix__< 20; ix__++)
           Camera_Faults_List[ix__] = MR.Read_int();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(camera_id[ix__]));
        MB.Add(MB.GetBytes(camera_mode.ordinal()));
        MB.Add(MB.GetBytes(spectrum.ordinal()));
        MB.Add(LOS_body_attitude.GetBytes());
        MB.Add(MB.GetBytes(FOVH));
        MB.Add(MB.GetBytes(IR_Polarity.ordinal()));
        MB.Add(MB.GetBytes(Temperature));
        MB.Add(MB.GetBytes(camera_serviceability.ordinal()));
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(Camera_Faults_List[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "LM_Camera_Status::" ;
       s += " camera_id: "+ Arrays.toString(camera_id);
       s += " camera_mode: "+ camera_mode;
       s += " spectrum: "+ spectrum;
       s += " LOS_body_attitude: "+ LOS_body_attitude.ToString();
       s += " FOVH: "+ FOVH;
       s += " IR_Polarity: "+ IR_Polarity;
       s += " Temperature: "+ Temperature;
       s += " camera_serviceability: "+ camera_serviceability;
       s += " Camera_Faults_List: "+ Arrays.toString(Camera_Faults_List);
       return s;
 }
}

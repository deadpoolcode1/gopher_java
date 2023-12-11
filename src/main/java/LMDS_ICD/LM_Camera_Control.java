package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class LM_Camera_Control extends MessageBody {
   int Size;
   public byte[] camera_id;
   public EnDef.camera_mode_ce camera_mode;
   public EnDef.spectrum_ce spectrum;
   public attitude LOS_body_attitude;
   public float Set_FOVH;
   public boolean IR_Calibration;
   public EnDef.IR_Polarity_ce IR_Polarity;
   public LM_Camera_Control() {
       Size = 52;
       camera_id = new byte[20];
       LOS_body_attitude = new attitude();
   }
   public int GetSize() {return Size;}
   public LM_Camera_Control Clone() {
       LM_Camera_Control copy = new LM_Camera_Control();
       copy.camera_id = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.camera_id[ix__] = camera_id[ix__];
       copy.camera_mode = camera_mode;
       copy.spectrum = spectrum;
       copy.LOS_body_attitude = LOS_body_attitude.Clone();
       copy.Set_FOVH = Set_FOVH;
       copy.IR_Calibration = IR_Calibration;
       copy.IR_Polarity = IR_Polarity;
       return copy;
   }
   public LM_Camera_Control(MsgRdr MR) {
       Size = 52;
       camera_id = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           camera_id[ix__] = MR.Read_byte();
       camera_mode = EnDef.camera_mode_ce.values()[MR.Read_int()];
       spectrum = EnDef.spectrum_ce.values()[MR.Read_int()];
       LOS_body_attitude = new attitude(MR);
       Set_FOVH = MR.Read_float();
       IR_Calibration = MR.Read_boolean();
       IR_Polarity = EnDef.IR_Polarity_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(camera_id[ix__]));
        MB.Add(MB.GetBytes(camera_mode.ordinal()));
        MB.Add(MB.GetBytes(spectrum.ordinal()));
        MB.Add(LOS_body_attitude.GetBytes());
        MB.Add(MB.GetBytes(Set_FOVH));
        MB.Add(MB.GetBytes(IR_Calibration));
        MB.Add(MB.GetBytes(IR_Polarity.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "LM_Camera_Control::" ;
       s += " camera_id: "+ Arrays.toString(camera_id);
       s += " camera_mode: "+ camera_mode;
       s += " spectrum: "+ spectrum;
       s += " LOS_body_attitude: "+ LOS_body_attitude.ToString();
       s += " Set_FOVH: "+ Set_FOVH;
       s += " IR_Calibration: "+ IR_Calibration;
       s += " IR_Polarity: "+ IR_Polarity;
       return s;
 }
}

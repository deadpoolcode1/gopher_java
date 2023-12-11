package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class attitude extends MessageBody {
   int Size;
   public float roll;
   public float pitch;
   public float yaw;
   public attitude() {
       Size = 12;
   }
   public int GetSize() {return Size;}
   public attitude Clone() {
       attitude copy = new attitude();
       copy.roll = roll;
       copy.pitch = pitch;
       copy.yaw = yaw;
       return copy;
   }
   public attitude(MsgRdr MR) {
       Size = 12;
       roll = MR.Read_float();
       pitch = MR.Read_float();
       yaw = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(roll));
        MB.Add(MB.GetBytes(pitch));
        MB.Add(MB.GetBytes(yaw));
       return bfr;
   }
   public String ToString() {
       String s = "attitude::" ;
       s += " roll: "+ roll;
       s += " pitch: "+ pitch;
       s += " yaw: "+ yaw;
       return s;
 }
}

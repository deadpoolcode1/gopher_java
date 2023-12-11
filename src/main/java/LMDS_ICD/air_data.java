package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class air_data extends MessageBody {
   int Size;
   public float altitude;
   public float air_speed;
   public air_data() {
       Size = 8;
   }
   public int GetSize() {return Size;}
   public air_data Clone() {
       air_data copy = new air_data();
       copy.altitude = altitude;
       copy.air_speed = air_speed;
       return copy;
   }
   public air_data(MsgRdr MR) {
       Size = 8;
       altitude = MR.Read_float();
       air_speed = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(altitude));
        MB.Add(MB.GetBytes(air_speed));
       return bfr;
   }
   public String ToString() {
       String s = "air_data::" ;
       s += " altitude: "+ altitude;
       s += " air_speed: "+ air_speed;
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class AHRS_packet_hdr extends MessageBody {
   int Size;
   public short SYNC;
   public short ID;
   public short length;
   public AHRS_packet_hdr() {
       Size = 6;
   }
   public int GetSize() {return Size;}
   public AHRS_packet_hdr Clone() {
       AHRS_packet_hdr copy = new AHRS_packet_hdr();
       copy.SYNC = SYNC;
       copy.ID = ID;
       copy.length = length;
       return copy;
   }
   public AHRS_packet_hdr(MsgRdr MR) {
       Size = 6;
       SYNC = MR.Read_short();
       ID = MR.Read_short();
       length = MR.Read_short();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(SYNC));
        MB.Add(MB.GetBytes(ID));
        MB.Add(MB.GetBytes(length));
       return bfr;
   }
   public String ToString() {
       String s = "AHRS_packet_hdr::" ;
       s += " SYNC: "+ SYNC;
       s += " ID: "+ ID;
       s += " length: "+ length;
       return s;
 }
}

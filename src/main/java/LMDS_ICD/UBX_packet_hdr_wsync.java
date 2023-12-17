package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class UBX_packet_hdr_wsync extends MessageBody {
   int Size;
   public short SYNC;
   public short ID;
   public short length;
   public UBX_packet_hdr_wsync() {
       Size = 6;
   }
   public int GetSize() {return Size;}
   public UBX_packet_hdr_wsync Clone() {
       UBX_packet_hdr_wsync copy = new UBX_packet_hdr_wsync();
       copy.SYNC = SYNC;
       copy.ID = ID;
       copy.length = length;
       return copy;
   }
   public UBX_packet_hdr_wsync(MsgRdr MR) {
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
       String s = "UBX_packet_hdr_wsync::" ;
       s += " SYNC: "+ SYNC;
       s += " ID: "+ ID;
       s += " length: "+ length;
       return s;
 }
}

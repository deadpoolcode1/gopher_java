package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class UBX_packet_hdr_nsync extends MessageBody {
   int Size;
   public short ID;
   public short length;
   public UBX_packet_hdr_nsync() {
       Size = 4;
   }
   public int GetSize() {return Size;}
   public UBX_packet_hdr_nsync Clone() {
       UBX_packet_hdr_nsync copy = new UBX_packet_hdr_nsync();
       copy.ID = ID;
       copy.length = length;
       return copy;
   }
   public UBX_packet_hdr_nsync(MsgRdr MR) {
       Size = 4;
       ID = MR.Read_short();
       length = MR.Read_short();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(ID));
        MB.Add(MB.GetBytes(length));
       return bfr;
   }
   public String ToString() {
       String s = "UBX_packet_hdr_nsync::" ;
       s += " ID: "+ ID;
       s += " length: "+ length;
       return s;
 }
}

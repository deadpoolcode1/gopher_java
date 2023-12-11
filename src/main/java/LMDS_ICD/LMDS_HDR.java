package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class LMDS_HDR extends MessageBody {
   int Size;
   public int length;
   public int send_time_ms;
   public int serial_number;
   public Address dest_address;
   public Address sender_address;
   public EnDef.msg_code_ce msg_code;
   public LMDS_HDR() {
       Size = 32;
       dest_address = new Address();
       sender_address = new Address();
   }
   public int GetSize() {return Size;}
   public LMDS_HDR Clone() {
       LMDS_HDR copy = new LMDS_HDR();
       copy.length = length;
       copy.send_time_ms = send_time_ms;
       copy.serial_number = serial_number;
       copy.dest_address = dest_address.Clone();
       copy.sender_address = sender_address.Clone();
       copy.msg_code = msg_code;
       return copy;
   }
   public LMDS_HDR(MsgRdr MR) {
       Size = 32;
       length = MR.Read_int();
       send_time_ms = MR.Read_int();
       serial_number = MR.Read_int();
       dest_address = new Address(MR);
       sender_address = new Address(MR);
       msg_code = EnDef.msg_code_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(length));
        MB.Add(MB.GetBytes(send_time_ms));
        MB.Add(MB.GetBytes(serial_number));
        MB.Add(dest_address.GetBytes());
        MB.Add(sender_address.GetBytes());
        MB.Add(MB.GetBytes(msg_code.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "LMDS_HDR::" ;
       s += " length: "+ length;
       s += " send_time_ms: "+ send_time_ms;
       s += " serial_number: "+ serial_number;
       s += " dest_address: "+ dest_address.ToString();
       s += " sender_address: "+ sender_address.ToString();
       s += " msg_code: "+ msg_code;
       return s;
 }
}

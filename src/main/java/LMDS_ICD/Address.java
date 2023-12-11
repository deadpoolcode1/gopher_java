package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class Address extends MessageBody {
   int Size;
   public EnDef.host_name_ce host_name;
   public EnDef.process_name_ce process_name;
   public Address() {
       Size = 8;
   }
   public int GetSize() {return Size;}
   public Address Clone() {
       Address copy = new Address();
       copy.host_name = host_name;
       copy.process_name = process_name;
       return copy;
   }
   public Address(MsgRdr MR) {
       Size = 8;
       host_name = EnDef.host_name_ce.values()[MR.Read_int()];
       process_name = EnDef.process_name_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(host_name.ordinal()));
        MB.Add(MB.GetBytes(process_name.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "Address::" ;
       s += " host_name: "+ host_name;
       s += " process_name: "+ process_name;
       return s;
 }
}

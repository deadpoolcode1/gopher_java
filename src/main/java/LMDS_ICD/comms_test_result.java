package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class comms_test_result extends MessageBody {
   int Size;
   public EnDef.host_name_ce host_A;
   public EnDef.host_name_ce host_B;
   public float MER;
   public comms_test_result() {
       Size = 12;
   }
   public int GetSize() {return Size;}
   public comms_test_result Clone() {
       comms_test_result copy = new comms_test_result();
       copy.host_A = host_A;
       copy.host_B = host_B;
       copy.MER = MER;
       return copy;
   }
   public comms_test_result(MsgRdr MR) {
       Size = 12;
       host_A = EnDef.host_name_ce.values()[MR.Read_int()];
       host_B = EnDef.host_name_ce.values()[MR.Read_int()];
       MER = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(host_A.ordinal()));
        MB.Add(MB.GetBytes(host_B.ordinal()));
        MB.Add(MB.GetBytes(MER));
       return bfr;
   }
   public String ToString() {
       String s = "comms_test_result::" ;
       s += " host_A: "+ host_A;
       s += " host_B: "+ host_B;
       s += " MER: "+ MER;
       return s;
 }
}

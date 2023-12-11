package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class pwr_sgnal_msrmnt extends MessageBody {
   int Size;
   public EnDef.pwr_sgnal_ce pwr_sgnal_id;
   public float voltage;
   public float current;
   public pwr_sgnal_msrmnt() {
       Size = 12;
   }
   public int GetSize() {return Size;}
   public pwr_sgnal_msrmnt Clone() {
       pwr_sgnal_msrmnt copy = new pwr_sgnal_msrmnt();
       copy.pwr_sgnal_id = pwr_sgnal_id;
       copy.voltage = voltage;
       copy.current = current;
       return copy;
   }
   public pwr_sgnal_msrmnt(MsgRdr MR) {
       Size = 12;
       pwr_sgnal_id = EnDef.pwr_sgnal_ce.values()[MR.Read_int()];
       voltage = MR.Read_float();
       current = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(pwr_sgnal_id.ordinal()));
        MB.Add(MB.GetBytes(voltage));
        MB.Add(MB.GetBytes(current));
       return bfr;
   }
   public String ToString() {
       String s = "pwr_sgnal_msrmnt::" ;
       s += " pwr_sgnal_id: "+ pwr_sgnal_id;
       s += " voltage: "+ voltage;
       s += " current: "+ current;
       return s;
 }
}

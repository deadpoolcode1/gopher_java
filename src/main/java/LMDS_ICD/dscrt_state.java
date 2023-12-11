package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class dscrt_state extends MessageBody {
   int Size;
   public EnDef.dscrt_id_ce dscrt_id;
   public EnDef.dscrt_val_ce dscrt_val;
   public dscrt_state() {
       Size = 8;
   }
   public int GetSize() {return Size;}
   public dscrt_state Clone() {
       dscrt_state copy = new dscrt_state();
       copy.dscrt_id = dscrt_id;
       copy.dscrt_val = dscrt_val;
       return copy;
   }
   public dscrt_state(MsgRdr MR) {
       Size = 8;
       dscrt_id = EnDef.dscrt_id_ce.values()[MR.Read_int()];
       dscrt_val = EnDef.dscrt_val_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(dscrt_id.ordinal()));
        MB.Add(MB.GetBytes(dscrt_val.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "dscrt_state::" ;
       s += " dscrt_id: "+ dscrt_id;
       s += " dscrt_val: "+ dscrt_val;
       return s;
 }
}

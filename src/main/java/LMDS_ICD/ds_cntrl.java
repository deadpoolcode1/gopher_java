package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class ds_cntrl extends MessageBody {
   int Size;
   public EnDef.lmds_state_ce lmds_state;
   public ds_cntrl() {
       Size = 4;
   }
   public int GetSize() {return Size;}
   public ds_cntrl Clone() {
       ds_cntrl copy = new ds_cntrl();
       copy.lmds_state = lmds_state;
       return copy;
   }
   public ds_cntrl(MsgRdr MR) {
       Size = 4;
       lmds_state = EnDef.lmds_state_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(lmds_state.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "ds_cntrl::" ;
       s += " lmds_state: "+ lmds_state;
       return s;
 }
}

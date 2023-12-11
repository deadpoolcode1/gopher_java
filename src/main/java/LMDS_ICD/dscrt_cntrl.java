package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class dscrt_cntrl extends MessageBody {
   int Size;
   public dscrt_state dscrt_state_cntrl;
   public dscrt_cntrl() {
       Size = 8;
       dscrt_state_cntrl = new dscrt_state();
   }
   public int GetSize() {return Size;}
   public dscrt_cntrl Clone() {
       dscrt_cntrl copy = new dscrt_cntrl();
       copy.dscrt_state_cntrl = dscrt_state_cntrl.Clone();
       return copy;
   }
   public dscrt_cntrl(MsgRdr MR) {
       Size = 8;
       dscrt_state_cntrl = new dscrt_state(MR);
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(dscrt_state_cntrl.GetBytes());
       return bfr;
   }
   public String ToString() {
       String s = "dscrt_cntrl::" ;
       s += " dscrt_state_cntrl: "+ dscrt_state_cntrl.ToString();
       return s;
 }
}

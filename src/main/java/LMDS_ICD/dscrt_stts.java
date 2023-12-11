package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class dscrt_stts extends MessageBody {
   int Size;
   public dscrt_state[] dscrt_states;
   public dscrt_stts() {
       Size = 80;
       dscrt_states = new dscrt_state[10];
       for(int ix__=0;ix__< 10; ix__++)
           dscrt_states[ix__]  = new dscrt_state();
   }
   public int GetSize() {return Size;}
   public dscrt_stts Clone() {
       dscrt_stts copy = new dscrt_stts();
       copy.dscrt_states = new dscrt_state[10];
       for(int ix__=0;ix__< 10; ix__++)
           copy.dscrt_states[ix__]  = dscrt_states[ix__].Clone();
       return copy;
   }
   public dscrt_stts(MsgRdr MR) {
       Size = 80;
       dscrt_states = new dscrt_state[10];
       for(int ix__=0;ix__< 10; ix__++)
           dscrt_states[ix__]  = new dscrt_state(MR);
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 10; ix__++)
           MB.Add(dscrt_states[ix__].GetBytes());
       return bfr;
   }
   public String ToString() {
       String s = "dscrt_stts::" ;
       for(int ix__=0;ix__< 10; ix__++)
           s += " dscrt_states["+ix__+"]: "+ dscrt_states[ix__].ToString();
       return s;
 }
}

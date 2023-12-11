package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class srial_comms_tst extends MessageBody {
   int Size;
   public byte[] bytes;
   public srial_comms_tst() {
       Size = 40;
       bytes = new byte[40];
   }
   public int GetSize() {return Size;}
   public srial_comms_tst Clone() {
       srial_comms_tst copy = new srial_comms_tst();
       copy.bytes = new byte[40];
       for(int ix__=0;ix__< 40; ix__++)
           copy.bytes[ix__] = bytes[ix__];
       return copy;
   }
   public srial_comms_tst(MsgRdr MR) {
       Size = 40;
       bytes = new byte[40];
       for(int ix__=0;ix__< 40; ix__++)
           bytes[ix__] = MR.Read_byte();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 40; ix__++)
            MB.Add(MB.GetBytes(bytes[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "srial_comms_tst::" ;
       s += " bytes: "+ Arrays.toString(bytes);
       return s;
 }
}

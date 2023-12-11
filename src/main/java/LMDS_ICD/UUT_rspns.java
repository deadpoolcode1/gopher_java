package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class UUT_rspns extends MessageBody {
   int Size;
   public EnDef.UUT_cmnd_rspns_ce response_type;
   public byte[] response_text;
   public UUT_rspns() {
       Size = 36;
       response_text = new byte[32];
   }
   public int GetSize() {return Size;}
   public UUT_rspns Clone() {
       UUT_rspns copy = new UUT_rspns();
       copy.response_type = response_type;
       copy.response_text = new byte[32];
       for(int ix__=0;ix__< 32; ix__++)
           copy.response_text[ix__] = response_text[ix__];
       return copy;
   }
   public UUT_rspns(MsgRdr MR) {
       Size = 36;
       response_text = new byte[32];
       response_type = EnDef.UUT_cmnd_rspns_ce.values()[MR.Read_int()];
       for(int ix__=0;ix__< 32; ix__++)
           response_text[ix__] = MR.Read_byte();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(response_type.ordinal()));
        for(int ix__=0;ix__< 32; ix__++)
            MB.Add(MB.GetBytes(response_text[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "UUT_rspns::" ;
       s += " response_type: "+ response_type;
       s += " response_text: "+ Arrays.toString(response_text);
       return s;
 }
}

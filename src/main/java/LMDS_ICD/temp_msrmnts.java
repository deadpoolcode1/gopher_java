package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class temp_msrmnts extends MessageBody {
   int Size;
   public temp_msrmnt[] tmsrmnt;
   public temp_msrmnts() {
       Size = 32;
       tmsrmnt = new temp_msrmnt[4];
       for(int ix__=0;ix__< 4; ix__++)
           tmsrmnt[ix__]  = new temp_msrmnt();
   }
   public int GetSize() {return Size;}
   public temp_msrmnts Clone() {
       temp_msrmnts copy = new temp_msrmnts();
       copy.tmsrmnt = new temp_msrmnt[4];
       for(int ix__=0;ix__< 4; ix__++)
           copy.tmsrmnt[ix__]  = tmsrmnt[ix__].Clone();
       return copy;
   }
   public temp_msrmnts(MsgRdr MR) {
       Size = 32;
       tmsrmnt = new temp_msrmnt[4];
       for(int ix__=0;ix__< 4; ix__++)
           tmsrmnt[ix__]  = new temp_msrmnt(MR);
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 4; ix__++)
           MB.Add(tmsrmnt[ix__].GetBytes());
       return bfr;
   }
   public String ToString() {
       String s = "temp_msrmnts::" ;
       for(int ix__=0;ix__< 4; ix__++)
           s += " tmsrmnt["+ix__+"]: "+ tmsrmnt[ix__].ToString();
       return s;
 }
}

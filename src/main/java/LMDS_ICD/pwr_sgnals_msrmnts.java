package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class pwr_sgnals_msrmnts extends MessageBody {
   int Size;
   public pwr_sgnal_msrmnt[] pmsrmnt;
   public pwr_sgnals_msrmnts() {
       Size = 96;
       pmsrmnt = new pwr_sgnal_msrmnt[8];
       for(int ix__=0;ix__< 8; ix__++)
           pmsrmnt[ix__]  = new pwr_sgnal_msrmnt();
   }
   public int GetSize() {return Size;}
   public pwr_sgnals_msrmnts Clone() {
       pwr_sgnals_msrmnts copy = new pwr_sgnals_msrmnts();
       copy.pmsrmnt = new pwr_sgnal_msrmnt[8];
       for(int ix__=0;ix__< 8; ix__++)
           copy.pmsrmnt[ix__]  = pmsrmnt[ix__].Clone();
       return copy;
   }
   public pwr_sgnals_msrmnts(MsgRdr MR) {
       Size = 96;
       pmsrmnt = new pwr_sgnal_msrmnt[8];
       for(int ix__=0;ix__< 8; ix__++)
           pmsrmnt[ix__]  = new pwr_sgnal_msrmnt(MR);
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 8; ix__++)
           MB.Add(pmsrmnt[ix__].GetBytes());
       return bfr;
   }
   public String ToString() {
       String s = "pwr_sgnals_msrmnts::" ;
       for(int ix__=0;ix__< 8; ix__++)
           s += " pmsrmnt["+ix__+"]: "+ pmsrmnt[ix__].ToString();
       return s;
 }
}

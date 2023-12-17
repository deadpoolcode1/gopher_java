package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class DS_Revision extends MessageBody {
   int Size;
   public byte[] DS_revision;
   public DS_Revision() {
       Size = 16;
       DS_revision = new byte[16];
   }
   public int GetSize() {return Size;}
   public DS_Revision Clone() {
       DS_Revision copy = new DS_Revision();
       copy.DS_revision = new byte[16];
       for(int ix__=0;ix__< 16; ix__++)
           copy.DS_revision[ix__] = DS_revision[ix__];
       return copy;
   }
   public DS_Revision(MsgRdr MR) {
       Size = 16;
       DS_revision = new byte[16];
       for(int ix__=0;ix__< 16; ix__++)
           DS_revision[ix__] = MR.Read_byte();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 16; ix__++)
            MB.Add(MB.GetBytes(DS_revision[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "DS_Revision::" ;
       s += " DS_revision: "+ Arrays.toString(DS_revision);
       return s;
 }
}

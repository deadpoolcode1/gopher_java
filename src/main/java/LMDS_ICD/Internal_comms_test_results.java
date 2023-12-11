package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class Internal_comms_test_results extends MessageBody {
   int Size;
   public comms_test_result[] int_comms_test_results;
   public Internal_comms_test_results() {
       Size = 48;
       int_comms_test_results = new comms_test_result[4];
       for(int ix__=0;ix__< 4; ix__++)
           int_comms_test_results[ix__]  = new comms_test_result();
   }
   public int GetSize() {return Size;}
   public Internal_comms_test_results Clone() {
       Internal_comms_test_results copy = new Internal_comms_test_results();
       copy.int_comms_test_results = new comms_test_result[4];
       for(int ix__=0;ix__< 4; ix__++)
           copy.int_comms_test_results[ix__]  = int_comms_test_results[ix__].Clone();
       return copy;
   }
   public Internal_comms_test_results(MsgRdr MR) {
       Size = 48;
       int_comms_test_results = new comms_test_result[4];
       for(int ix__=0;ix__< 4; ix__++)
           int_comms_test_results[ix__]  = new comms_test_result(MR);
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 4; ix__++)
           MB.Add(int_comms_test_results[ix__].GetBytes());
       return bfr;
   }
   public String ToString() {
       String s = "Internal_comms_test_results::" ;
       for(int ix__=0;ix__< 4; ix__++)
           s += " int_comms_test_results["+ix__+"]: "+ int_comms_test_results[ix__].ToString();
       return s;
 }
}

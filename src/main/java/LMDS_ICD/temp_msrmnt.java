package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class temp_msrmnt extends MessageBody {
   int Size;
   public EnDef.temp_snsor_ce temp_snsor_id;
   public float temperature;
   public temp_msrmnt() {
       Size = 8;
   }
   public int GetSize() {return Size;}
   public temp_msrmnt Clone() {
       temp_msrmnt copy = new temp_msrmnt();
       copy.temp_snsor_id = temp_snsor_id;
       copy.temperature = temperature;
       return copy;
   }
   public temp_msrmnt(MsgRdr MR) {
       Size = 8;
       temp_snsor_id = EnDef.temp_snsor_ce.values()[MR.Read_int()];
       temperature = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(temp_snsor_id.ordinal()));
        MB.Add(MB.GetBytes(temperature));
       return bfr;
   }
   public String ToString() {
       String s = "temp_msrmnt::" ;
       s += " temp_snsor_id: "+ temp_snsor_id;
       s += " temperature: "+ temperature;
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class read_config_param extends MessageBody {
   int Size;
   public EnDef.config_params_ce config_params_id;
   public read_config_param() {
       Size = 4;
   }
   public int GetSize() {return Size;}
   public read_config_param Clone() {
       read_config_param copy = new read_config_param();
       copy.config_params_id = config_params_id;
       return copy;
   }
   public read_config_param(MsgRdr MR) {
       Size = 4;
       config_params_id = EnDef.config_params_ce.values()[MR.Read_int()];
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(config_params_id.ordinal()));
       return bfr;
   }
   public String ToString() {
       String s = "read_config_param::" ;
       s += " config_params_id: "+ config_params_id;
       return s;
 }
}

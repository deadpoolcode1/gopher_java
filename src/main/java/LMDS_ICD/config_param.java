package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class config_param extends MessageBody {
   int Size;
   public EnDef.config_params_ce config_params_id;
   public int length;
   public byte[] data;
   public config_param() {
       Size = 28;
       data = new byte[20];
   }
   public int GetSize() {return Size;}
   public config_param Clone() {
       config_param copy = new config_param();
       copy.config_params_id = config_params_id;
       copy.length = length;
       copy.data = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.data[ix__] = data[ix__];
       return copy;
   }
   public config_param(MsgRdr MR) {
       Size = 28;
       data = new byte[20];
       config_params_id = EnDef.config_params_ce.values()[MR.Read_int()];
       length = MR.Read_int();
       for(int ix__=0;ix__< 20; ix__++)
           data[ix__] = MR.Read_byte();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(config_params_id.ordinal()));
        MB.Add(MB.GetBytes(length));
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(data[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "config_param::" ;
       s += " config_params_id: "+ config_params_id;
       s += " length: "+ length;
       s += " data: "+ Arrays.toString(data);
       return s;
 }
}

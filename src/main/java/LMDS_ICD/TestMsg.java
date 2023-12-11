package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class TestMsg extends MessageBody {
   int Size;
   public float f1;
   public int int1;
   public int int2;
   public byte[] bytes;
   public EnDef.msg_code_ce enum1;
   public int bool1;
   public TestMsg() {
       Size = 32;
       bytes = new byte[12];
   }
   public int GetSize() {return Size;}
   public TestMsg Clone() {
       TestMsg copy = new TestMsg();
       copy.f1 = f1;
       copy.int1 = int1;
       copy.int2 = int2;
       copy.bytes = new byte[12];
       for(int ix__=0;ix__< 12; ix__++)
           copy.bytes[ix__] = bytes[ix__];
       copy.enum1 = enum1;
       copy.bool1 = bool1;
       return copy;
   }
   public TestMsg(MsgRdr MR) {
       Size = 32;
       bytes = new byte[12];
       f1 = MR.Read_float();
       int1 = MR.Read_int();
       int2 = MR.Read_int();
       for(int ix__=0;ix__< 12; ix__++)
           bytes[ix__] = MR.Read_byte();
       enum1 = EnDef.msg_code_ce.values()[MR.Read_int()];
       bool1 = MR.Read_int();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(f1));
        MB.Add(MB.GetBytes(int1));
        MB.Add(MB.GetBytes(int2));
        for(int ix__=0;ix__< 12; ix__++)
            MB.Add(MB.GetBytes(bytes[ix__]));
        MB.Add(MB.GetBytes(enum1.ordinal()));
        MB.Add(MB.GetBytes(bool1));
       return bfr;
   }
   public String ToString() {
       String s = "TestMsg::" ;
       s += " f1: "+ f1;
       s += " int1: "+ int1;
       s += " int2: "+ int2;
       s += " bytes: "+ Arrays.toString(bytes);
       s += " enum1: "+ enum1;
       s += " bool1: "+ bool1;
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class TextMsg extends MessageBody {
   int Size;
   public byte[] text;
   public TextMsg() {
       Size = 20;
       text = new byte[20];
   }
   public int GetSize() {return Size;}
   public TextMsg Clone() {
       TextMsg copy = new TextMsg();
       copy.text = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.text[ix__] = text[ix__];
       return copy;
   }
   public TextMsg(MsgRdr MR) {
       Size = 20;
       text = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           text[ix__] = MR.Read_byte();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(text[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "TextMsg::" ;
       s += " text: "+ Arrays.toString(text);
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class SampleMsg extends MessageBody {
   int Size;
   public float f1;
   public int i1;
   public int[] intarr;
   public Address address;
   public byte[] bytearr;
   public EnDef.host_name_ce enum1;
   public boolean bool1;
   public boolean[] boolarr;
   public Address[] addressarr;
   public short short1;
   public short[] shortarr;
   public SampleMsg() {
       Size = 156;
       intarr = new int[12];
       address = new Address();
       bytearr = new byte[20];
       boolarr = new boolean[5];
       addressarr = new Address[4];
       for(int ix__=0;ix__< 4; ix__++)
           addressarr[ix__]  = new Address();
       shortarr = new short[5];
   }
   public int GetSize() {return Size;}
   public SampleMsg Clone() {
       SampleMsg copy = new SampleMsg();
       copy.f1 = f1;
       copy.i1 = i1;
       copy.intarr = new int[12];
       for(int ix__=0;ix__< 12; ix__++)
           copy.intarr[ix__] = intarr[ix__];
       copy.address = address.Clone();
       copy.bytearr = new byte[20];
       for(int ix__=0;ix__< 20; ix__++)
           copy.bytearr[ix__] = bytearr[ix__];
       copy.enum1 = enum1;
       copy.bool1 = bool1;
       copy.boolarr = new boolean[5];
       for(int ix__=0;ix__< 5; ix__++)
           copy.boolarr[ix__] = boolarr[ix__];
       copy.addressarr = new Address[4];
       for(int ix__=0;ix__< 4; ix__++)
           copy.addressarr[ix__]  = addressarr[ix__].Clone();
       copy.short1 = short1;
       copy.shortarr = new short[5];
       for(int ix__=0;ix__< 5; ix__++)
           copy.shortarr[ix__] = shortarr[ix__];
       return copy;
   }
   public SampleMsg(MsgRdr MR) {
       Size = 156;
       intarr = new int[12];
       bytearr = new byte[20];
       boolarr = new boolean[5];
       shortarr = new short[5];
       f1 = MR.Read_float();
       i1 = MR.Read_int();
       for(int ix__=0;ix__< 12; ix__++)
           intarr[ix__] = MR.Read_int();
       address = new Address(MR);
       for(int ix__=0;ix__< 20; ix__++)
           bytearr[ix__] = MR.Read_byte();
       enum1 = EnDef.host_name_ce.values()[MR.Read_int()];
       bool1 = MR.Read_boolean();
       for(int ix__=0;ix__< 5; ix__++)
           boolarr[ix__] = MR.Read_boolean();
       addressarr = new Address[4];
       for(int ix__=0;ix__< 4; ix__++)
           addressarr[ix__]  = new Address(MR);
       short1 = MR.Read_short();
       for(int ix__=0;ix__< 5; ix__++)
           shortarr[ix__] = MR.Read_short();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(f1));
        MB.Add(MB.GetBytes(i1));
        for(int ix__=0;ix__< 12; ix__++)
            MB.Add(MB.GetBytes(intarr[ix__]));
        MB.Add(address.GetBytes());
        for(int ix__=0;ix__< 20; ix__++)
            MB.Add(MB.GetBytes(bytearr[ix__]));
        MB.Add(MB.GetBytes(enum1.ordinal()));
        MB.Add(MB.GetBytes(bool1));
        for(int ix__=0;ix__< 5; ix__++)
            MB.Add(MB.GetBytes(boolarr[ix__]));
        for(int ix__=0;ix__< 4; ix__++)
           MB.Add(addressarr[ix__].GetBytes());
        MB.Add(MB.GetBytes(short1));
        for(int ix__=0;ix__< 5; ix__++)
            MB.Add(MB.GetBytes(shortarr[ix__]));
       return bfr;
   }
   public String ToString() {
       String s = "SampleMsg::" ;
       s += " f1: "+ f1;
       s += " i1: "+ i1;
       s += " intarr: "+ Arrays.toString(intarr);
       s += " address: "+ address.ToString();
       s += " bytearr: "+ Arrays.toString(bytearr);
       s += " enum1: "+ enum1;
       s += " bool1: "+ bool1;
       s += " boolarr: "+ Arrays.toString(boolarr);
       for(int ix__=0;ix__< 4; ix__++)
           s += " addressarr["+ix__+"]: "+ addressarr[ix__].ToString();
       s += " short1: "+ short1;
       s += " shortarr: "+ Arrays.toString(shortarr);
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class AHRS_System extends MessageBody {
   int Size;
   public int SN;
   public short status;
   public short temp;
   public int reserved1;
   public short reserved2;
   public short SW_ver_rev;
   public AHRS_System() {
       Size = 16;
   }
   public int GetSize() {return Size;}
   public AHRS_System Clone() {
       AHRS_System copy = new AHRS_System();
       copy.SN = SN;
       copy.status = status;
       copy.temp = temp;
       copy.reserved1 = reserved1;
       copy.reserved2 = reserved2;
       copy.SW_ver_rev = SW_ver_rev;
       return copy;
   }
   public AHRS_System(MsgRdr MR) {
       Size = 16;
       SN = MR.Read_int();
       status = MR.Read_short();
       temp = MR.Read_short();
       reserved1 = MR.Read_int();
       reserved2 = MR.Read_short();
       SW_ver_rev = MR.Read_short();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(SN));
        MB.Add(MB.GetBytes(status));
        MB.Add(MB.GetBytes(temp));
        MB.Add(MB.GetBytes(reserved1));
        MB.Add(MB.GetBytes(reserved2));
        MB.Add(MB.GetBytes(SW_ver_rev));
       return bfr;
   }
   public String ToString() {
       String s = "AHRS_System::" ;
       s += " SN: "+ SN;
       s += " status: "+ status;
       s += " temp: "+ temp;
       s += " reserved1: "+ reserved1;
       s += " reserved2: "+ reserved2;
       s += " SW_ver_rev: "+ SW_ver_rev;
       return s;
 }
}

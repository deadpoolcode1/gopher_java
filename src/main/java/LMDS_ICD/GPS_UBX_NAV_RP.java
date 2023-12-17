package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class GPS_UBX_NAV_RP extends MessageBody {
   int Size;
   public int iTOW;
   public int lon;
   public int lat;
   public int height;
   public int hMSL;
   public int hAcc;
   public int vAcc;
   public GPS_UBX_NAV_RP() {
       Size = 28;
   }
   public int GetSize() {return Size;}
   public GPS_UBX_NAV_RP Clone() {
       GPS_UBX_NAV_RP copy = new GPS_UBX_NAV_RP();
       copy.iTOW = iTOW;
       copy.lon = lon;
       copy.lat = lat;
       copy.height = height;
       copy.hMSL = hMSL;
       copy.hAcc = hAcc;
       copy.vAcc = vAcc;
       return copy;
   }
   public GPS_UBX_NAV_RP(MsgRdr MR) {
       Size = 28;
       iTOW = MR.Read_int();
       lon = MR.Read_int();
       lat = MR.Read_int();
       height = MR.Read_int();
       hMSL = MR.Read_int();
       hAcc = MR.Read_int();
       vAcc = MR.Read_int();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(iTOW));
        MB.Add(MB.GetBytes(lon));
        MB.Add(MB.GetBytes(lat));
        MB.Add(MB.GetBytes(height));
        MB.Add(MB.GetBytes(hMSL));
        MB.Add(MB.GetBytes(hAcc));
        MB.Add(MB.GetBytes(vAcc));
       return bfr;
   }
   public String ToString() {
       String s = "GPS_UBX_NAV_RP::" ;
       s += " iTOW: "+ iTOW;
       s += " lon: "+ lon;
       s += " lat: "+ lat;
       s += " height: "+ height;
       s += " hMSL: "+ hMSL;
       s += " hAcc: "+ hAcc;
       s += " vAcc: "+ vAcc;
       return s;
 }
}

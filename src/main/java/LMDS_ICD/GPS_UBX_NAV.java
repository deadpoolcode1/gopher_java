package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class GPS_UBX_NAV extends MessageBody {
   int Size;
   public int ver_res_flags;
   public int iTOW;
   public int lon;
   public int lat;
   public int height;
   public int hMSL;
   public short lon_lat_HP;
   public short height_HP;
   public int hAcc;
   public int vAcc;
   public GPS_UBX_NAV() {
       Size = 36;
   }
   public int GetSize() {return Size;}
   public GPS_UBX_NAV Clone() {
       GPS_UBX_NAV copy = new GPS_UBX_NAV();
       copy.ver_res_flags = ver_res_flags;
       copy.iTOW = iTOW;
       copy.lon = lon;
       copy.lat = lat;
       copy.height = height;
       copy.hMSL = hMSL;
       copy.lon_lat_HP = lon_lat_HP;
       copy.height_HP = height_HP;
       copy.hAcc = hAcc;
       copy.vAcc = vAcc;
       return copy;
   }
   public GPS_UBX_NAV(MsgRdr MR) {
       Size = 36;
       ver_res_flags = MR.Read_int();
       iTOW = MR.Read_int();
       lon = MR.Read_int();
       lat = MR.Read_int();
       height = MR.Read_int();
       hMSL = MR.Read_int();
       lon_lat_HP = MR.Read_short();
       height_HP = MR.Read_short();
       hAcc = MR.Read_int();
       vAcc = MR.Read_int();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(ver_res_flags));
        MB.Add(MB.GetBytes(iTOW));
        MB.Add(MB.GetBytes(lon));
        MB.Add(MB.GetBytes(lat));
        MB.Add(MB.GetBytes(height));
        MB.Add(MB.GetBytes(hMSL));
        MB.Add(MB.GetBytes(lon_lat_HP));
        MB.Add(MB.GetBytes(height_HP));
        MB.Add(MB.GetBytes(hAcc));
        MB.Add(MB.GetBytes(vAcc));
       return bfr;
   }
   public String ToString() {
       String s = "GPS_UBX_NAV::" ;
       s += " ver_res_flags: "+ ver_res_flags;
       s += " iTOW: "+ iTOW;
       s += " lon: "+ lon;
       s += " lat: "+ lat;
       s += " height: "+ height;
       s += " hMSL: "+ hMSL;
       s += " lon_lat_HP: "+ lon_lat_HP;
       s += " height_HP: "+ height_HP;
       s += " hAcc: "+ hAcc;
       s += " vAcc: "+ vAcc;
       return s;
 }
}

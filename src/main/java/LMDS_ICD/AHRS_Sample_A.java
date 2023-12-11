package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class AHRS_Sample_A extends MessageBody {
   int Size;
   public short roll;
   public short pitch;
   public short yaw;
   public short ax;
   public short ay;
   public short az;
   public short ox;
   public short oy;
   public short oz;
   public short vn;
   public short ve;
   public short vd;
   public int lat;
   public int lon;
   public float alt;
   public int mtime;
   public AHRS_Sample_A() {
       Size = 40;
   }
   public int GetSize() {return Size;}
   public AHRS_Sample_A Clone() {
       AHRS_Sample_A copy = new AHRS_Sample_A();
       copy.roll = roll;
       copy.pitch = pitch;
       copy.yaw = yaw;
       copy.ax = ax;
       copy.ay = ay;
       copy.az = az;
       copy.ox = ox;
       copy.oy = oy;
       copy.oz = oz;
       copy.vn = vn;
       copy.ve = ve;
       copy.vd = vd;
       copy.lat = lat;
       copy.lon = lon;
       copy.alt = alt;
       copy.mtime = mtime;
       return copy;
   }
   public AHRS_Sample_A(MsgRdr MR) {
       Size = 40;
       roll = MR.Read_short();
       pitch = MR.Read_short();
       yaw = MR.Read_short();
       ax = MR.Read_short();
       ay = MR.Read_short();
       az = MR.Read_short();
       ox = MR.Read_short();
       oy = MR.Read_short();
       oz = MR.Read_short();
       vn = MR.Read_short();
       ve = MR.Read_short();
       vd = MR.Read_short();
       lat = MR.Read_int();
       lon = MR.Read_int();
       alt = MR.Read_float();
       mtime = MR.Read_int();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(roll));
        MB.Add(MB.GetBytes(pitch));
        MB.Add(MB.GetBytes(yaw));
        MB.Add(MB.GetBytes(ax));
        MB.Add(MB.GetBytes(ay));
        MB.Add(MB.GetBytes(az));
        MB.Add(MB.GetBytes(ox));
        MB.Add(MB.GetBytes(oy));
        MB.Add(MB.GetBytes(oz));
        MB.Add(MB.GetBytes(vn));
        MB.Add(MB.GetBytes(ve));
        MB.Add(MB.GetBytes(vd));
        MB.Add(MB.GetBytes(lat));
        MB.Add(MB.GetBytes(lon));
        MB.Add(MB.GetBytes(alt));
        MB.Add(MB.GetBytes(mtime));
       return bfr;
   }
   public String ToString() {
       String s = "AHRS_Sample_A::" ;
       s += " roll: "+ roll;
       s += " pitch: "+ pitch;
       s += " yaw: "+ yaw;
       s += " ax: "+ ax;
       s += " ay: "+ ay;
       s += " az: "+ az;
       s += " ox: "+ ox;
       s += " oy: "+ oy;
       s += " oz: "+ oz;
       s += " vn: "+ vn;
       s += " ve: "+ ve;
       s += " vd: "+ vd;
       s += " lat: "+ lat;
       s += " lon: "+ lon;
       s += " alt: "+ alt;
       s += " mtime: "+ mtime;
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class AHRS_Sample_B extends MessageBody {
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
   public short mx;
   public short my;
   public short mz;
   public int reserved1;
   public int reserved2;
   public int mtime;
   public AHRS_Sample_B() {
       Size = 36;
   }
   public int GetSize() {return Size;}
   public AHRS_Sample_B Clone() {
       AHRS_Sample_B copy = new AHRS_Sample_B();
       copy.roll = roll;
       copy.pitch = pitch;
       copy.yaw = yaw;
       copy.ax = ax;
       copy.ay = ay;
       copy.az = az;
       copy.ox = ox;
       copy.oy = oy;
       copy.oz = oz;
       copy.mx = mx;
       copy.my = my;
       copy.mz = mz;
       copy.reserved1 = reserved1;
       copy.reserved2 = reserved2;
       copy.mtime = mtime;
       return copy;
   }
   public AHRS_Sample_B(MsgRdr MR) {
       Size = 36;
       roll = MR.Read_short();
       pitch = MR.Read_short();
       yaw = MR.Read_short();
       ax = MR.Read_short();
       ay = MR.Read_short();
       az = MR.Read_short();
       ox = MR.Read_short();
       oy = MR.Read_short();
       oz = MR.Read_short();
       mx = MR.Read_short();
       my = MR.Read_short();
       mz = MR.Read_short();
       reserved1 = MR.Read_int();
       reserved2 = MR.Read_int();
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
        MB.Add(MB.GetBytes(mx));
        MB.Add(MB.GetBytes(my));
        MB.Add(MB.GetBytes(mz));
        MB.Add(MB.GetBytes(reserved1));
        MB.Add(MB.GetBytes(reserved2));
        MB.Add(MB.GetBytes(mtime));
       return bfr;
   }
   public String ToString() {
       String s = "AHRS_Sample_B::" ;
       s += " roll: "+ roll;
       s += " pitch: "+ pitch;
       s += " yaw: "+ yaw;
       s += " ax: "+ ax;
       s += " ay: "+ ay;
       s += " az: "+ az;
       s += " ox: "+ ox;
       s += " oy: "+ oy;
       s += " oz: "+ oz;
       s += " mx: "+ mx;
       s += " my: "+ my;
       s += " mz: "+ mz;
       s += " reserved1: "+ reserved1;
       s += " reserved2: "+ reserved2;
       s += " mtime: "+ mtime;
       return s;
 }
}

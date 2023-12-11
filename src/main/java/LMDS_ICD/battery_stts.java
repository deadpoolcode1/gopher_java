package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class battery_stts extends MessageBody {
   int Size;
   public boolean Battery_monitor_comms_OK;
   public boolean BATHI;
   public boolean BATLO;
   public boolean CHG_INH;
   public float Internal_Temp;
   public float Battery_Voltage;
   public float Battery_Current;
   public battery_stts() {
       Size = 28;
   }
   public int GetSize() {return Size;}
   public battery_stts Clone() {
       battery_stts copy = new battery_stts();
       copy.Battery_monitor_comms_OK = Battery_monitor_comms_OK;
       copy.BATHI = BATHI;
       copy.BATLO = BATLO;
       copy.CHG_INH = CHG_INH;
       copy.Internal_Temp = Internal_Temp;
       copy.Battery_Voltage = Battery_Voltage;
       copy.Battery_Current = Battery_Current;
       return copy;
   }
   public battery_stts(MsgRdr MR) {
       Size = 28;
       Battery_monitor_comms_OK = MR.Read_boolean();
       BATHI = MR.Read_boolean();
       BATLO = MR.Read_boolean();
       CHG_INH = MR.Read_boolean();
       Internal_Temp = MR.Read_float();
       Battery_Voltage = MR.Read_float();
       Battery_Current = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(Battery_monitor_comms_OK));
        MB.Add(MB.GetBytes(BATHI));
        MB.Add(MB.GetBytes(BATLO));
        MB.Add(MB.GetBytes(CHG_INH));
        MB.Add(MB.GetBytes(Internal_Temp));
        MB.Add(MB.GetBytes(Battery_Voltage));
        MB.Add(MB.GetBytes(Battery_Current));
       return bfr;
   }
   public String ToString() {
       String s = "battery_stts::" ;
       s += " Battery_monitor_comms_OK: "+ Battery_monitor_comms_OK;
       s += " BATHI: "+ BATHI;
       s += " BATLO: "+ BATLO;
       s += " CHG_INH: "+ CHG_INH;
       s += " Internal_Temp: "+ Internal_Temp;
       s += " Battery_Voltage: "+ Battery_Voltage;
       s += " Battery_Current: "+ Battery_Current;
       return s;
 }
}

package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class LM_Propulsion_Control extends MessageBody {
   int Size;
   public EnDef.Motor_state_ce motor_state;
   public float motor_rpm;
   public LM_Propulsion_Control() {
       Size = 8;
   }
   public int GetSize() {return Size;}
   public LM_Propulsion_Control Clone() {
       LM_Propulsion_Control copy = new LM_Propulsion_Control();
       copy.motor_state = motor_state;
       copy.motor_rpm = motor_rpm;
       return copy;
   }
   public LM_Propulsion_Control(MsgRdr MR) {
       Size = 8;
       motor_state = EnDef.Motor_state_ce.values()[MR.Read_int()];
       motor_rpm = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(motor_state.ordinal()));
        MB.Add(MB.GetBytes(motor_rpm));
       return bfr;
   }
   public String ToString() {
       String s = "LM_Propulsion_Control::" ;
       s += " motor_state: "+ motor_state;
       s += " motor_rpm: "+ motor_rpm;
       return s;
 }
}

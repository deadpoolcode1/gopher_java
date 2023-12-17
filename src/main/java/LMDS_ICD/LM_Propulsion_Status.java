package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class LM_Propulsion_Status extends MessageBody {
   int Size;
   public EnDef.Motor_state_ce motor_state;
   public float Input_Voltage;
   public float Ripple_Input_Voltage;
   public float Input_Current;
   public float RPM_Command;
   public float Output_Power;
   public float RPM_Electrical;
   public float Temperature;
   public float RPM_Out;
   public LM_Propulsion_Status() {
       Size = 36;
   }
   public int GetSize() {return Size;}
   public LM_Propulsion_Status Clone() {
       LM_Propulsion_Status copy = new LM_Propulsion_Status();
       copy.motor_state = motor_state;
       copy.Input_Voltage = Input_Voltage;
       copy.Ripple_Input_Voltage = Ripple_Input_Voltage;
       copy.Input_Current = Input_Current;
       copy.RPM_Command = RPM_Command;
       copy.Output_Power = Output_Power;
       copy.RPM_Electrical = RPM_Electrical;
       copy.Temperature = Temperature;
       copy.RPM_Out = RPM_Out;
       return copy;
   }
   public LM_Propulsion_Status(MsgRdr MR) {
       Size = 36;
       motor_state = EnDef.Motor_state_ce.values()[MR.Read_int()];
       Input_Voltage = MR.Read_float();
       Ripple_Input_Voltage = MR.Read_float();
       Input_Current = MR.Read_float();
       RPM_Command = MR.Read_float();
       Output_Power = MR.Read_float();
       RPM_Electrical = MR.Read_float();
       Temperature = MR.Read_float();
       RPM_Out = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(motor_state.ordinal()));
        MB.Add(MB.GetBytes(Input_Voltage));
        MB.Add(MB.GetBytes(Ripple_Input_Voltage));
        MB.Add(MB.GetBytes(Input_Current));
        MB.Add(MB.GetBytes(RPM_Command));
        MB.Add(MB.GetBytes(Output_Power));
        MB.Add(MB.GetBytes(RPM_Electrical));
        MB.Add(MB.GetBytes(Temperature));
        MB.Add(MB.GetBytes(RPM_Out));
       return bfr;
   }
   public String ToString() {
       String s = "LM_Propulsion_Status::" ;
       s += " motor_state: "+ motor_state;
       s += " Input_Voltage: "+ Input_Voltage;
       s += " Ripple_Input_Voltage: "+ Ripple_Input_Voltage;
       s += " Input_Current: "+ Input_Current;
       s += " RPM_Command: "+ RPM_Command;
       s += " Output_Power: "+ Output_Power;
       s += " RPM_Electrical: "+ RPM_Electrical;
       s += " Temperature: "+ Temperature;
       s += " RPM_Out: "+ RPM_Out;
       return s;
 }
}

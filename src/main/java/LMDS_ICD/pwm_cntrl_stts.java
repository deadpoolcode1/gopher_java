package LMDS_ICD;
//
// ******* Auto-Generated Code - DO NOT Modify !! ********
//
//import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class pwm_cntrl_stts extends MessageBody {
   int Size;
   public int isCntrl;
   public EnDef.PWM_channel_id_ce pwm_id;
   public float servo_duty_cycle;
   public pwm_cntrl_stts() {
       Size = 12;
   }
   public int GetSize() {return Size;}
   public pwm_cntrl_stts Clone() {
       pwm_cntrl_stts copy = new pwm_cntrl_stts();
       copy.isCntrl = isCntrl;
       copy.pwm_id = pwm_id;
       copy.servo_duty_cycle = servo_duty_cycle;
       return copy;
   }
   public pwm_cntrl_stts(MsgRdr MR) {
       Size = 12;
       isCntrl = MR.Read_int();
       pwm_id = EnDef.PWM_channel_id_ce.values()[MR.Read_int()];
       servo_duty_cycle = MR.Read_float();
   }
    public byte[] GetBytes() {
        byte[] bfr = new byte[Size];
        MsgBld MB = new MsgBld(bfr);
        MB.Add(MB.GetBytes(isCntrl));
        MB.Add(MB.GetBytes(pwm_id.ordinal()));
        MB.Add(MB.GetBytes(servo_duty_cycle));
       return bfr;
   }
   public String ToString() {
       String s = "pwm_cntrl_stts::" ;
       s += " isCntrl: "+ isCntrl;
       s += " pwm_id: "+ pwm_id;
       s += " servo_duty_cycle: "+ servo_duty_cycle;
       return s;
 }
}

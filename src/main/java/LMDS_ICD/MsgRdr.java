package LMDS_ICD;

public class MsgRdr {
    // a set of methods to translate a sub-set array from a message byte array into JAVA primitives
    int ptr=0, max;
    byte[] in_buf;

    public MsgRdr(byte[] in_buf_in) {
        ptr=0;
        max = in_buf_in.length;
        in_buf = in_buf_in;
    }
    // set of reading methods
    public byte Read_byte() {
        byte x = in_buf[ptr];
        ptr += 1;
        return x;
    }
    public boolean Read_boolean() {
       int x = this.Read_int();
        if(x != 0)
            return true;
        else
            return false;
    }
    public char     Read_char() {
        char x = (char)this.Read_short();
        return x;
    }
    public short Read_short() {
        short x = 0;
        x = in_buf[ptr+1];
        x = (short) (x << 8);
        x = (short) (x | (in_buf[ptr+0] & 0xff));
        ptr += 2;
        return x;
    }
    public int Read_int() {
        int x = 0;
        x = in_buf[ptr+3];
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+2] & 0xff));
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+1] & 0xff));
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+0] & 0xff));
        ptr += 4;
        return x;
    }
    public float Read_float() {
        int x = 0;
        x = in_buf[ptr+3];
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+2] & 0xff));
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+1] & 0xff));
        x = (int) (x << 8); x = (int) (x | (in_buf[ptr+0] & 0xff));
        float xf = Float.intBitsToFloat(x);
        ptr += 4;
        return xf;
    }
    public long     Read_long() {
        long x = 0;
        x = in_buf[ptr+7];
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+6] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+5] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+4] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+3] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+2] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+1] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+0] & 0xff));
        ptr += 8;
        return x;
    }
    public double   Read_double() {
        long x = 0;
        x = in_buf[ptr+7];
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+6] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+5] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+4] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+3] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+2] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+1] & 0xff));
        x = ( long) (x << 8); x = ( long) (x | (in_buf[ptr+0] & 0xff));
        double xf = Double.longBitsToDouble(x);
        ptr += 8;
        return xf;
    }
    public void     ReadBytesArray(byte[] bar) {
        int i=0;
        for( ;i<bar.length; i++)
             bar[i] = in_buf[ptr+i];
        ptr += i;
    }
}

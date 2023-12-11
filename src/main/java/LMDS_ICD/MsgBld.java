package LMDS_ICD;

public class MsgBld {
    // enables translation of the data of a POJ class to byte array which may be read as a C struct
    // by the STM, using simple cast from the message body. Note that Java strings are not supported, as they have variable length
    int ptr=0, max;
    byte[] out_buf;
    public MsgBld(byte[] out_buf_in) {
        // init the reader
        ptr=0;
        max = out_buf_in.length;
        out_buf = out_buf_in;
    }
    public boolean Add(byte[] item) {
        // add a serialized data item to the message buffer
        if(item.length <= (max-ptr)) {
            int i=0;
            for(; i<item.length; i++) {
                out_buf[ptr + i] = item[i];
            }
            ptr += i;
            return true;
        }
        return false;
    }
    public int GetMsgSize() {
        return ptr;
    }
    // methods for serializing primitive JAVA items to byte arrays
    public  byte[] GetBytes( byte pr_item) {
        byte[] bfr = new byte[1];
        bfr[0] = pr_item;
        return bfr;
    }
    public  byte[] GetBytes( boolean pr_item) {
        byte[] bfr = new byte[4];
        bfr[1]=bfr[2]=bfr[3]=0;
        if(pr_item)
            bfr[0] = 1;
        else
            bfr[0] = 0;
        return bfr;
    }
    public  byte[] GetBytes( char pr_item) {
        // java char is a 16 bit unicode value
        byte[] bfr = new byte[2];
        bfr[1] = (byte) ((pr_item >>> 8) & 0xff);
        bfr[0] = (byte) ((pr_item >>> 0) & 0xff);
        ptr+=2;
        return bfr;
    }
    public  byte[] GetBytes( short pr_item) {
        byte[] bfr = new byte[2];
        bfr[1] = (byte) ((pr_item >>> 8) & 0xff);
        bfr[0] = (byte) ((pr_item >>> 0) & 0xff);
        return bfr;
    }
    public  byte[] GetBytes( int pr_item) {
        byte[] bfr = new byte[4];
        bfr[3] = (byte) ((byte)(pr_item >>> 24 ) & 0xff);
        bfr[2] = (byte) ((byte)(pr_item >>> 16 ) & 0xff);
        bfr[1] = (byte) ((byte)(pr_item >>>  8 ) & 0xff);
        bfr[0] = (byte) ((byte)(pr_item >>>  0 ) & 0xff);
        return bfr;
    }
    public  byte[] GetBytes( float pr_item_f) {
        byte[] bfr = new byte[4];
        int pr_item = Float.floatToRawIntBits(pr_item_f);
        bfr[3] = (byte) ((byte)(pr_item >>> 24 ) & 0xff);
        bfr[2] = (byte) ((byte)(pr_item >>> 16 ) & 0xff);
        bfr[1] = (byte) ((byte)(pr_item >>>  8 ) & 0xff);
        bfr[0] = (byte) ((byte)(pr_item >>>  0 ) & 0xff);
        return bfr;
    }
    public  byte[] GetBytes( long pr_item) {
        byte[] bfr = new byte[8];
        bfr[7] = (byte) ((byte)(pr_item >>> 56 ) & 0xff);
        bfr[6] = (byte) ((byte)(pr_item >>> 48 ) & 0xff);
        bfr[5] = (byte) ((byte)(pr_item >>> 40 ) & 0xff);
        bfr[4] = (byte) ((byte)(pr_item >>> 32 ) & 0xff);
        bfr[3] = (byte) ((byte)(pr_item >>> 24 ) & 0xff);
        bfr[2] = (byte) ((byte)(pr_item >>> 16 ) & 0xff);
        bfr[1] = (byte) ((byte)(pr_item >>>  8 ) & 0xff);
        bfr[0] = (byte) ((byte)(pr_item >>>  0 ) & 0xff);
        return bfr;
    }
    public  byte[] GetBytes( double pr_item_f) {
        byte[] bfr = new byte[8];
        long pr_item = Double.doubleToRawLongBits(pr_item_f);
        bfr[7] = (byte) ((byte)(pr_item >>> 56 ) & 0xff);
        bfr[6] = (byte) ((byte)(pr_item >>> 48 ) & 0xff);
        bfr[5] = (byte) ((byte)(pr_item >>> 40 ) & 0xff);
        bfr[4] = (byte) ((byte)(pr_item >>> 32 ) & 0xff);
        bfr[3] = (byte) ((byte)(pr_item >>> 24 ) & 0xff);
        bfr[2] = (byte) ((byte)(pr_item >>> 16 ) & 0xff);
        bfr[1] = (byte) ((byte)(pr_item >>>  8 ) & 0xff);
        bfr[0] = (byte) ((byte)(pr_item >>>  0 ) & 0xff);
        return bfr;
    }

}

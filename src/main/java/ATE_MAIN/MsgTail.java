package ATE_MAIN;

public class MsgTail {
    // handles LMDS protocol message tail functions
    private byte[] tail = {0,0,0,0, (byte) 0xca, (byte) 0xfe, (byte) 0xc2d, (byte) 0xad}; // bytes 0-3 for checksum; 4-7 are sync bytes
    private static final int Size=8;

    public MsgTail(byte[] bfr, int start) {
        // use in RX - get a tail object before setting a MsgRdr
        for(int i=0; i<8; i++)
            tail[i] = bfr[start+i];
    }
    // this constructor may be used on TX
    public MsgTail (){
        tail[4] = (byte) 0xca;
        tail[5] = (byte) 0xfe;
        tail[6] = (byte) 0x2d;
        tail[7] = (byte) 0xad;}
    public static int GetSize() {return Size;}
    public boolean IsGoodChecksum(byte[] msg, int length) {
        // use in RX
        int msg_chksum = GetMsgChecksum(msg, length - 8);
        int rx_chksum  = GetRxMsgChecksum(tail);
        return msg_chksum == rx_chksum;
    }

    private int GetRxMsgChecksum(byte[] tail) {
        int x = 0;
        x = tail[0];
        x = (int) (x << 8); x = (int) (x | (tail[1] & 0xff));
        x = (int) (x << 8); x = (int) (x | (tail[2] & 0xff));
        x = (int) (x << 8); x = (int) (x | (tail[3] & 0xff));
        return x;
    }

    private int GetMsgChecksum(byte[] msg, int size) {
        int cs=0;
        for(int i=0;i<size;i++) {
            int k = (int)(msg[i]&0xff);
            cs +=k;
        }
        return cs;
    }
    public void SetChecksum(byte[] msg){
        // use on TX
        int cksum = GetMsgChecksum(msg, msg.length - 8);
        tail[0] = (byte)((cksum >>> 24)&0xff);
        tail[1] = (byte)((cksum >>> 16)&0xff);
        tail[2] = (byte)((cksum >>> 8)&0xff);
        tail[3] = (byte)(cksum&0xff);
    }
    public byte[] GetBytes() {
        return tail;
    }
/*
    public boolean IsGoodChecksum(byte[] hdr_bytes, byte[] pckt) {
        byte[] msg = new byte[32+pckt.length];
        for(int i=0; i<32; i++) {
            msg[i] = hdr_bytes[i];
        }
        for(int i=32; i<msg.length; i++) {
            msg[i] = pckt[i-32];
        }
        return IsGoodChecksum(msg);
    }
    */
}

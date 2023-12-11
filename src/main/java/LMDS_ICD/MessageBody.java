package LMDS_ICD;

import ATE_MAIN.MsgTail;

public class MessageBody {
    // base empty class for all message bodies
    public int GetSize() {return 0;}
    public byte[] GetBytes() {return null;}
    public static byte[] GetBytes(LMDS_HDR LM, byte[] msg) {
        int LM_Size = LM.GetSize();
        int body_size = msg.length - MsgTail.GetSize() - LM_Size;
        if(body_size <= 0)
            return null;
        byte[] body = new byte[body_size];
        for(int i=0; i<body.length; i++)
            body[i] = msg[LM_Size+i];
        return body;
    }
}

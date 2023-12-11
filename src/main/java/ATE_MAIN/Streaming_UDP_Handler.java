package ATE_MAIN;

import ATE_GUI.IP_RADIO_GUI.IP_RADIO_Monitor;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Streaming_UDP_Handler extends Thread {
    static int bfr_length = 200;
    int listener_port = 0;
    DatagramSocket socket=null;
    public InetAddress listener_address, radio_address;
    public boolean terminate = false, LAN_Active=false;
    static long wait_delay = 100;
    IP_RADIO_Monitor iP_RADIO_Monitor=null;

    public Streaming_UDP_Handler(int listener_port, String radio_address_string, String listener_address_string, IP_RADIO_Monitor iP_RADIO_Monitor) throws UnknownHostException {
        this.listener_port = listener_port;
        this.iP_RADIO_Monitor = iP_RADIO_Monitor;
        radio_address = InetAddress.getByAddress(IP_String_To_Binary(radio_address_string));
        listener_address = InetAddress.getByAddress(IP_String_To_Binary(listener_address_string));
    }
    public void run() {
        try {
            // open a LAN socket
            socket = new DatagramSocket(listener_port, listener_address);
            socket.setSoTimeout(1); // the RX shall wait up to 1 MS for new messages, preempt, wait and try again
            LAN_Active=true;
        } catch (IOException e) {
            if (e instanceof java.net.BindException) {
                System.out.println("Streaming_UDP_Handler: Radio IP="+iP_RADIO_Monitor.iP_RADIO_GD.address_string+"  Error=Socket bind failed: " + e.getMessage());
            } else {
                System.out.println("Streaming_UDP_Handler: Radio IP="+iP_RADIO_Monitor.iP_RADIO_GD.address_string+"  Error occurred: " + e.getMessage());
            }
            if(socket!=null)
                socket.close(); // free the port
            return; // terminate the thread in this case - radio must be on before running
        }
        System.out.println("Streaming_UDP_Handler: Radio IP="+iP_RADIO_Monitor.iP_RADIO_GD.address_string+"  Streaming Socket established OK.");
        while(! terminate) {
            DatagramPacket packet = new DatagramPacket(new byte[bfr_length], bfr_length);
            try {
                socket.receive(packet); // block until Socket wait times out or a message received
                byte[] msg_body = packet.getData();
                if(LAN_Active) // process a LAN input only if the LAN state flag is active
                    iP_RADIO_Monitor.Decode_and_process_radio_streams(msg_body);
            }
            catch (InterruptedIOException e) {
                // RX timed out; wait 100 ms before trying again
                try {
                    Thread.sleep(wait_delay); // wait  before polling again
                } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        socket.close();
    }

    private byte[] IP_String_To_Binary(String address_string) {
        String[] tokens = address_string.split("\\.");
        byte[] bin = new byte[4];
        bin[0] = (byte) Integer.parseInt(tokens[0]);
        bin[1] = (byte) Integer.parseInt(tokens[1]);
        bin[2] = (byte) Integer.parseInt(tokens[2]);
        bin[3] = (byte) Integer.parseInt(tokens[3]);
        return bin;
    }
    public void Terminate() {
        terminate = true;
    }
    public boolean Send(byte[] msg_body, InetAddress dest_address, int dest_port) {
        if(socket == null || (!LAN_Active))
            return false;
        DatagramPacket packet = new DatagramPacket(msg_body, msg_body.length, dest_address, dest_port);
        try {
            socket.send(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}


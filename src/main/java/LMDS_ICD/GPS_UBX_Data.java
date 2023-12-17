package LMDS_ICD;

public class GPS_UBX_Data {
    // maintains GPS Ublocks Navigation data per the UBX-NAV-HPPOSLLH (0x01 0x14) message
    // (High Precision Geodetic Position Solution)
    final short ID_UBX_NAV_HP = (short) 0x1401; // High Precision NAV message ID & class
    final short ID_UBX_NAV_RP = (short) 0x0201; // Regular Precision NAV message ID & class
    final short ID_UBX_NAK= (short) 0x0005; // NAK message ID & class
    final short ID_UBX_ACK = (short) 0x0105; // ACK message ID & class
    public int
            ver_res_flags, // 0   four bytes: 0-version, 1,2-reserved, 3-flag bits (bit 0 indicates invalid NAV)
            iTOW, // 1 GPS time of week, milliseconds
            lon_lat_HP, // 2    2 bytes for lon/lat high precision; 0-lon add, 1-lat add (see ICD)
            height_HP; // 3    2 bytes for height high precision; 0-height add, 1-hMSL add (see ICD)
    public double
            lon, // 4  degrees * 1e7
            lat, // 5  degrees * 1e7
            height, // 6  meters above ellipsoid * 1000
            hMSL, // 7  meters above ellipsoid * 1000
            hAcc, // 8  Horizontal accuracy estimate, meters, *10000
            vAcc; // 9  Vertical accuracy estimate, meters, *10000
    public byte ack_nak_class, ack_nak_id;
    public String ack_or_nak = "ACK for class/ID: ";

    public void Update(byte[] gPS_UBX_data) {
        /**
         * input: GPS UBX message data, including header, without the checksum
         *        this may contain one of many messages sent by the device;
         *        the function decodes only the UBX-NAV-HPPOSLLH message and populates the above public fields
         */
        MsgRdr MR = new MsgRdr(gPS_UBX_data); // set for incoming msg serialization
        UBX_packet_hdr_nsync APH = new UBX_packet_hdr_nsync(MR); // use a header without sync chars
        // assume the SYNC, checksum and length fields are OK, and were validated by the LMDS before sending it to the ATE
        switch (APH.ID) {
            case ID_UBX_NAV_HP: // high precision NAV data
                DecodeUBX_NAV_HPPOSLLH(MR);
                break;
            case ID_UBX_NAV_RP: // regular precision NAV data
                DecodeUBX_NAV_RPPOSLLH(MR);
                break;
            case ID_UBX_NAK:
                ack_or_nak = "NAK for class/ID: ";
                ack_nak_class = gPS_UBX_data[4];
                ack_nak_id = gPS_UBX_data[5];
                break;
            case ID_UBX_ACK:
                ack_or_nak = "ACK for class/ID: ";
                ack_nak_class = gPS_UBX_data[4];
                ack_nak_id = gPS_UBX_data[5];
                break;
            default: // ignore all other messages
        }
    }

    private void DecodeUBX_NAV_HPPOSLLH(MsgRdr mr) {
        GPS_UBX_NAV_HP gPS_UBX_NAV = new GPS_UBX_NAV_HP(mr);
        // int variables
        ver_res_flags = gPS_UBX_NAV.ver_res_flags;
        iTOW = gPS_UBX_NAV.iTOW;
        lon_lat_HP = gPS_UBX_NAV.lon_lat_HP;
        height_HP = gPS_UBX_NAV.height_HP;
        // double variables
        lon = ((double) gPS_UBX_NAV.lon) / 10000000.0;
        lat = ((double) gPS_UBX_NAV.lat) / 10000000.0;
        height = ((double) gPS_UBX_NAV.height) / 1000.0;
        hMSL = ((double) gPS_UBX_NAV.hMSL) / 1000.0;
        hAcc = ((double) gPS_UBX_NAV.hAcc) / 10000.0;
        vAcc = ((double) gPS_UBX_NAV.vAcc) / 10000.0;
    }
    private void DecodeUBX_NAV_RPPOSLLH(MsgRdr mr) {
        GPS_UBX_NAV_RP gPS_UBX_NAV = new GPS_UBX_NAV_RP(mr);
        // int variables
        iTOW = gPS_UBX_NAV.iTOW;
        // double variables
        lon = ((double) gPS_UBX_NAV.lon) / 10000000.0;
        lat = ((double) gPS_UBX_NAV.lat) / 10000000.0;
        height = ((double) gPS_UBX_NAV.height) / 1000.0;
        hMSL = ((double) gPS_UBX_NAV.hMSL) / 1000.0;
        hAcc = ((double) gPS_UBX_NAV.hAcc) / 10000.0;
        vAcc = ((double) gPS_UBX_NAV.vAcc) / 10000.0;
    }

    public String ToString() {
        String GPS_stts =
                "<html>GPS Status: " + "<br/>" +
                        " ver_res_flags: %00 " + "<br/>" +
                        " iTOW: %01 " + "<br/>" +
                        " lon_lat_HP: %02 " + "<br/>" +
                        " height_HP: %03 " + "<br/>" +
                        " lon: %04 " + "<br/>" +
                        " lat: %05 " + "<br/>" +
                        " height: %06 " + "<br/>" +
                        " hMSL: %07 " + "<br/>" +
                        " hAcc: %08 " + "<br/>" +
                        " vAcc: %09 " + "<br/>" +
                        "<html>";
        GPS_stts = GPS_stts.replace("%00", Integer.toBinaryString(ver_res_flags));
        GPS_stts = GPS_stts.replace("%01", String.valueOf(iTOW));
        GPS_stts = GPS_stts.replace("%02", Integer.toHexString(lon_lat_HP));
        GPS_stts = GPS_stts.replace("%03", Integer.toHexString(height_HP));
        GPS_stts = GPS_stts.replace("%04", String.valueOf(lon));
        GPS_stts = GPS_stts.replace("%05", String.valueOf(lat));
        GPS_stts = GPS_stts.replace("%06", String.valueOf(height));
        GPS_stts = GPS_stts.replace("%07", String.valueOf(hMSL));
        GPS_stts = GPS_stts.replace("%08", String.valueOf(hAcc));
        GPS_stts = GPS_stts.replace("%09", String.valueOf(vAcc));
        return GPS_stts;
    }
}
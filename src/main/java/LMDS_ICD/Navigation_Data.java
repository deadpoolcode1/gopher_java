package LMDS_ICD;

public class Navigation_Data {
    // maintains navigation data received from the UUT (AHRS data, GPS data, altimeter etc.)
    final short ID_sampleA = (short)0x07AB, ID_sampleB = (short)0x66AB, ID_system=(short)0x05AB;
    public int
            AHRS_time, // 0 milliseconds
            status, // 1 status bits (low 16)
            SW_ver_rev, // 2 software version, revision (low 8 +8)
            SN; // 3 serial number
    public double
            // AHRS inputs
            roll, pitch, yaw, // 4,5,6 degrees
            ax,ay,az, // 7,8,9 degrees
            ox,oy,oz, // 10,11,12 degrees / second
            vn,ve,vd, // 13,14,15 meters / second
            mx,my,mz; // 16, 17, 18 magnetic field, milliGauss
    public double
            AHRS_lat, AHRS_lon, AHRS_alt, // 19, 20,21 lat: degrees (0 to +- 90); lon: degrees (0 to +- 180); alt: meters AMSL
            temp; // 22 temperature, degrees
    public void Update(byte[] AHRS_data){
        /**
         * input: AHRS message data, including header, without the checksum
         *        this may contain one of the three messages: Sample A, Sample B or status
         *        The function decodes the message, and populates the above public fields
         */
        MsgRdr MR = new MsgRdr(AHRS_data); // set for incoming msg serialization
        //UBX_packet_hdr_wsync APH = new UBX_packet_hdr_wsync(MR);
        UBX_packet_hdr_nsync APH = new UBX_packet_hdr_nsync(MR);
        // assume the SYNC, checksum and length fields are OK, and were validated by the LMDS before sending it to the ATE
        switch(APH.ID) {
            case ID_sampleA:
                DecodeSampleA(MR);
                break;
            case ID_sampleB:
                DecodeSampleB(MR);
                break;
            case ID_system:
                DecodeSystem(MR);
                break;
            default:
                System.out.println("Navigation_Data decoding. Illegal packet ID. Ignored.="+APH.ID);
        }
    }

    private void DecodeSystem(MsgRdr mr) {
        AHRS_System AHS = new AHRS_System(mr);
        SN = AHS.SN; 		// module serial number
        status = AHS.status; 	// status bits (see ATOM Manual, low 16 bits)
        temp = ((double)AHS.temp)/100.0; 		// degrees *100 --> deg
        SW_ver_rev = AHS.SW_ver_rev; 			// software version (high byte) & revision (low byte)
    }

    private void DecodeSampleB(MsgRdr mr) {
        AHRS_Sample_B ASB = new AHRS_Sample_B(mr);
        roll  = ((double)ASB.roll)/100.0; 	// deg *100 --> deg
        pitch = ((double)ASB.pitch)/100.0; // deg *100 --> deg
        yaw = ((double)ASB.yaw)/100.0; 	// deg *100 --> deg
        ax = ((double)ASB.ax)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        ay = ((double)ASB.ay)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        az = ((double)ASB.az)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        ox = ((double)ASB.ox)/10.0; 	// deg/sec *10 --> deg
        oy = ((double)ASB.oy)/10.0; 	// deg/sec *10 --> deg
        oz = ((double)ASB.oz)/10.0; 	// deg/sec *10 --> deg
        mx = (double)ASB.mx; 	// mGauss
        my = (double)ASB.my; 	// mGauss
        mz = (double)ASB.mz; 	// mGauss
        AHRS_time = ASB.mtime; // milliseconds (since ??)
    }

    private void DecodeSampleA(MsgRdr mr) {
        AHRS_Sample_A ASA = new AHRS_Sample_A(mr);
        roll  = ((double)ASA.roll)/100.0; 	// deg *100 --> deg
        pitch = ((double)ASA.pitch)/100.0; // deg *100 --> deg
        yaw = ((double)ASA.yaw)/100.0; 	// deg *100 --> deg
        ax = ((double)ASA.ax)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        ay = ((double)ASA.ay)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        az = ((double)ASA.az)/1000.0; 	// cm/sec^2 *10 --> meters/sec^2
        ox = ((double)ASA.ox)/10.0; 	// deg/sec *10 --> deg
        oy = ((double)ASA.oy)/10.0; 	// deg/sec *10 --> deg
        oz = ((double)ASA.oz)/10.0; 	// deg/sec *10 --> deg
        vn = ((double)ASA.vn)/100.0; 	// cm/sec --> meters/sec
        ve = ((double)ASA.ve)/100.0; 	// cm/sec --> meters/sec
        vd = ((double)ASA.vd)/100.0; 	// cm/sec --> meters/sec
        AHRS_lat = Math.toDegrees(((double)ASA.lat)/10000000.0); // radians * 1e7 --> deg
        AHRS_lon = Math.toDegrees(((double)ASA.lon)/10000000.0); 	// radians * 1e7 --> deg
        AHRS_alt = (double)ASA.alt; // meters AMSL
        AHRS_time = ASA.mtime; // milliseconds (since ??)
    }

    public String ToString() {
        String AHRS_stts =
                "<html>AHRS Status: "+"<br/>"+
                        " AHRS_time: %00 "+"<br/>"+
                        " status: %01 "+"<br/>"+
                        " SW_ver_rev: %02 "+"<br/>"+
                        " SN: %03 "+"<br/>"+
                        " roll, pitch, yaw: %04, %05, %06 "+"<br/>"+
                        " ax,ay,az: %07, %08, %09"+"<br/>"+
                        " ox,oy,oz: %10, %11, %12 "+"<br/>"+
                        " vn,ve,vd: %13, %14, %15  "+"<br/>"+
                        " mx,my,mz: %16, %17, %18 "+"<br/>"+
                        " AHRS_lat, AHRS_lon, AHRS_alt: %19, %20, %21  "+"<br/>"+
                        " temp: %22 "+"<br/>"+
                        "<html>";
        AHRS_stts = AHRS_stts.replace("%00", String.valueOf(AHRS_time));
        AHRS_stts = AHRS_stts.replace("%01", Integer.toBinaryString(status));
        AHRS_stts = AHRS_stts.replace("%02", Integer.toHexString(SW_ver_rev));
        AHRS_stts = AHRS_stts.replace("%03", String.valueOf(SN));
        AHRS_stts = AHRS_stts.replace("%04", String.valueOf(roll));
        AHRS_stts = AHRS_stts.replace("%05", String.valueOf(pitch));
        AHRS_stts = AHRS_stts.replace("%06", String.valueOf(yaw));
        AHRS_stts = AHRS_stts.replace("%07", String.valueOf(ax));
        AHRS_stts = AHRS_stts.replace("%08", String.valueOf(ay));
        AHRS_stts = AHRS_stts.replace("%09", String.valueOf(az));
        AHRS_stts = AHRS_stts.replace("%10", String.valueOf(ox));
        AHRS_stts = AHRS_stts.replace("%11", String.valueOf(oy));
        AHRS_stts = AHRS_stts.replace("%12", String.valueOf(oz));
        AHRS_stts = AHRS_stts.replace("%13", String.valueOf(vn));
        AHRS_stts = AHRS_stts.replace("%14", String.valueOf(ve));
        AHRS_stts = AHRS_stts.replace("%15", String.valueOf(vd));
        AHRS_stts = AHRS_stts.replace("%16", String.valueOf(mx));
        AHRS_stts = AHRS_stts.replace("%17", String.valueOf(my));
        AHRS_stts = AHRS_stts.replace("%18", String.valueOf(mz));
        AHRS_stts = AHRS_stts.replace("%19", String.valueOf(AHRS_lat));
        AHRS_stts = AHRS_stts.replace("%20", String.valueOf(AHRS_lon));
        AHRS_stts = AHRS_stts.replace("%21", String.valueOf(AHRS_alt));
        AHRS_stts = AHRS_stts.replace("%22", String.valueOf(temp));
        return AHRS_stts;
    }
}

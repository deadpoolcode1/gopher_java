package LMDS_ICD;

public class All_IP_Radio_Settings {
    // relevant to the Silvus IP radio, SC4200/SC4400.
    public String
    // ******* basic *********
    frequency="", // MHz
    bandwidth="", // MHz
    network_id="", // a name string
    link_distance="", // meters
    total_transmit_power="", // total power of the TX signal (power is divided equally between the radio antenna ports)
    routing_mode="", // default is "large network"
    // ******* advanced  *********
    routing_beacons_period="", // milliseconds - period of sending routing beacons packets. for small networks default = 100
    routing_beacons_mcs="", // MCS for the routing beacons packets
    fragmentation_threshold="", // determine the minimum over-the-air packet size in bytes
    max_ground_speed="", // miles / hour; set to 0 for the ATE (defined by the "training_symbols" UI field)
    burst_time="", // milliseconds="", determines the maximum amount of time each node is allowed to transmit at once. range - 1-20 ?
    rts_retries="", // number of RTS / CTS retries before a packet is dropped; packet sent only after receiving CTS (UI only allows enable/disable of retransmissions)
    number_of_retransmissions="", // number of times a lost packet is retransmitted before being dropped. Default value is 20. (UI only allows enable/disable of retransmissions)
    MCS="", // modulation and coding scheme; see user manual
    antennae_mask="", // actual settings of the four channels (antennae), coded binary in the range 0-15
    variable_gi_mode="", // variable guard interval to allow for longer delay spread; see user manual p. 59
    cyclic_prefix_length="", // combined with the variable_gi_mode; see user manual
    beam_forming="", // enable / disable beam forming to have more gain to a specific direction
    radio_mode="", // Switch between Network mode and PHY Diagnostics
    enable_max_power="", // allow the radio to use max power automatically
    la_mode="" // mode of settings the guard interval (GI); see API / user manual
    ;
}

package ATE_GUI.IP_RADIO_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class IP_RADIO_RSSI_Chart extends XY_Graph_Template {
    IP_RADIO_Monitor iP_RADIO_Monitor;
    public IP_RADIO_RSSI_Chart(JPanel owner, int width, int height, String chartTitle,
                               String xLabel, String yLabel, IP_RADIO_Monitor iP_RADIO_Monitor) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
        this.iP_RADIO_Monitor = iP_RADIO_Monitor;
        iP_RADIO_Monitor.ANT1_RSSI = new XYSeries("ANT1");  dataset.addSeries(iP_RADIO_Monitor.ANT1_RSSI);
        iP_RADIO_Monitor.ANT2_RSSI = new XYSeries("ANT2");  dataset.addSeries(iP_RADIO_Monitor.ANT2_RSSI);
        iP_RADIO_Monitor.ANT3_RSSI = new XYSeries("ANT3");  dataset.addSeries(iP_RADIO_Monitor.ANT3_RSSI);
        iP_RADIO_Monitor.ANT4_RSSI = new XYSeries("ANT4");  dataset.addSeries(iP_RADIO_Monitor.ANT4_RSSI);
        iP_RADIO_Monitor.RAW_NOISE_PWR = new XYSeries("RAW_NOISE_PWR");  dataset.addSeries(iP_RADIO_Monitor.RAW_NOISE_PWR);

    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
    }

    public void UpdateDataset(int timein, double ant1_rssi, double ant2_rssi, double ant3_rssi, double ant4_rssi, double raw_noise_pwr) {
        double time = (double)timein / 1000.0;
        iP_RADIO_Monitor.ANT1_RSSI.add(time, ant1_rssi);
        if(iP_RADIO_Monitor.ANT1_RSSI.getItemCount() > 100) iP_RADIO_Monitor.ANT1_RSSI.remove(0);
        iP_RADIO_Monitor.ANT2_RSSI.add(time, ant2_rssi);
        if(iP_RADIO_Monitor.ANT2_RSSI.getItemCount() > 100) iP_RADIO_Monitor.ANT2_RSSI.remove(0);
        iP_RADIO_Monitor.ANT3_RSSI.add(time, ant3_rssi);
        if(iP_RADIO_Monitor.ANT3_RSSI.getItemCount() > 100) iP_RADIO_Monitor.ANT3_RSSI.remove(0);
        iP_RADIO_Monitor.ANT4_RSSI.add(time, ant4_rssi);
        if(iP_RADIO_Monitor.ANT4_RSSI.getItemCount() > 100) iP_RADIO_Monitor.ANT4_RSSI.remove(0);
        iP_RADIO_Monitor.RAW_NOISE_PWR.add(time, raw_noise_pwr);
        if(iP_RADIO_Monitor.RAW_NOISE_PWR.getItemCount() > 100) iP_RADIO_Monitor.RAW_NOISE_PWR.remove(0);
    }
}

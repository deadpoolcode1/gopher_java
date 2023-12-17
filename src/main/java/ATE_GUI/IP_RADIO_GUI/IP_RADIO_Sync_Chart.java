package ATE_GUI.IP_RADIO_GUI;
import ATE_GUI.GUI_CMN.XY_Graph_Template;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class IP_RADIO_Sync_Chart extends XY_Graph_Template {
    IP_RADIO_Monitor iP_RADIO_Monitor;
    public IP_RADIO_Sync_Chart(JPanel owner, int width, int height, String chartTitle,
                               String xLabel, String yLabel, IP_RADIO_Monitor iP_RADIO_Monitor) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
        this.iP_RADIO_Monitor = iP_RADIO_Monitor;
        iP_RADIO_Monitor.SYNC_SIGNAL_PWR = new XYSeries("SYNC_SIGNAL_PWR");  dataset.addSeries(iP_RADIO_Monitor.SYNC_SIGNAL_PWR);
        iP_RADIO_Monitor.SYNC_NOISE_PWR = new XYSeries("SYNC_NOISE_PWR");  dataset.addSeries(iP_RADIO_Monitor.SYNC_NOISE_PWR);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
    }

    public void UpdateDataset(int timein, double sync_signal_pwr, double sync_noise_pwr) {
        double time = (double)timein / 1000.0;
        iP_RADIO_Monitor.SYNC_SIGNAL_PWR.add(time, sync_signal_pwr);
        if(iP_RADIO_Monitor.SYNC_SIGNAL_PWR.getItemCount() > 100) iP_RADIO_Monitor.SYNC_SIGNAL_PWR.remove(0);
        iP_RADIO_Monitor.SYNC_NOISE_PWR.add(time, sync_noise_pwr);
        if(iP_RADIO_Monitor.SYNC_NOISE_PWR.getItemCount() > 100) iP_RADIO_Monitor.SYNC_NOISE_PWR.remove(0);
    }
}

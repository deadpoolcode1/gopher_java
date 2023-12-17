package ATE_GUI.IP_RADIO_GUI;
import ATE_GUI.GUI_CMN.XY_Graph_Template;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
public class IP_RADIO_VOLT_Chart extends XY_Graph_Template {
    IP_RADIO_Monitor iP_RADIO_Monitor;
    public IP_RADIO_VOLT_Chart(JPanel owner, int width, int height, String chartTitle,
                               String xLabel, String yLabel, IP_RADIO_Monitor iP_RADIO_Monitor) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
        this.iP_RADIO_Monitor = iP_RADIO_Monitor;
        iP_RADIO_Monitor.CURRENT_VOLT = new XYSeries("CURRENT_VOLT");  dataset.addSeries(iP_RADIO_Monitor.CURRENT_VOLT);
        iP_RADIO_Monitor.MIN_VOLT = new XYSeries("MIN_VOLT");  dataset.addSeries(iP_RADIO_Monitor.MIN_VOLT);
        iP_RADIO_Monitor.MAX_VOLT = new XYSeries("MAX_VOLT");  dataset.addSeries(iP_RADIO_Monitor.MAX_VOLT);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
    }

    public void UpdateDataset(int timein, double current_volt, double min_volt, double max_volt) {
        double time = (double)timein / 1000.0;
        iP_RADIO_Monitor.CURRENT_VOLT.add(time, current_volt);
        if(iP_RADIO_Monitor.CURRENT_VOLT.getItemCount() > 100) iP_RADIO_Monitor.CURRENT_VOLT.remove(0);
        iP_RADIO_Monitor.MIN_VOLT.add(time, min_volt);
        if(iP_RADIO_Monitor.MIN_VOLT.getItemCount() > 100) iP_RADIO_Monitor.MIN_VOLT.remove(0);
        iP_RADIO_Monitor.MAX_VOLT.add(time, max_volt);
        if(iP_RADIO_Monitor.MAX_VOLT.getItemCount() > 100) iP_RADIO_Monitor.MAX_VOLT.remove(0);
    }
}

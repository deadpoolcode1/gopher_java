package ATE_GUI.IP_RADIO_GUI;
import ATE_GUI.GUI_CMN.XY_Graph_Template;
import ATE_MAIN.IP_RADIO_GD;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class IP_RADIO_TEMP_Chart extends XY_Graph_Template {
    IP_RADIO_GD iP_RADIO_GD=null;
    public IP_RADIO_TEMP_Chart(JPanel owner, int width, int height, String chartTitle,
                               String xLabel, String yLabel, IP_RADIO_GD iP_RADIO_GD) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
        this.iP_RADIO_GD = iP_RADIO_GD;
        iP_RADIO_GD.CURRENT_TEMP = new XYSeries("CURRENT_TEMP");  dataset.addSeries(iP_RADIO_GD.CURRENT_TEMP);
        iP_RADIO_GD.MAX_TEMP = new XYSeries("MAX_TEMP");  dataset.addSeries(iP_RADIO_GD.MAX_TEMP);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
    }

    public void UpdateDataset(int timein, double current_temp, double max_temp) {
        double time = (double)timein / 1000.0;
        iP_RADIO_GD.CURRENT_TEMP.add(time, current_temp);
        if(iP_RADIO_GD.CURRENT_TEMP.getItemCount() > 100) iP_RADIO_GD.CURRENT_TEMP.remove(0);
        iP_RADIO_GD.MAX_TEMP.add(time, max_temp);
        if(iP_RADIO_GD.MAX_TEMP.getItemCount() > 100) iP_RADIO_GD.MAX_TEMP.remove(0);
    }
}

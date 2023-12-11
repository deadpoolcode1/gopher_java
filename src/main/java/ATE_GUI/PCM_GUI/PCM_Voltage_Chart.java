package ATE_GUI.PCM_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import LMDS_ICD.pwr_sgnals_msrmnts;
import ATE_MAIN.PCM_GD;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class PCM_Voltage_Chart extends XY_Graph_Template {
    public PCM_Voltage_Chart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        PCM_GD.PCM_5V = new XYSeries("5V");
        dataset.addSeries(PCM_GD.PCM_5V);
        PCM_GD.PCM_12V = new XYSeries("12V");
        dataset.addSeries(PCM_GD.PCM_12V);
        PCM_GD.PCM_8_2V = new XYSeries("12V Main");
        dataset.addSeries(PCM_GD.PCM_8_2V);
        PCM_GD.PCM_BATV = new XYSeries("3.3V");
        dataset.addSeries(PCM_GD.PCM_BATV);
    }

    public void UpdateDataset(int timein, pwr_sgnals_msrmnts PSM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < PSM.pmsrmnt.length; i++) {
            switch (PSM.pmsrmnt[i].pwr_sgnal_id) {
                case PCM_5V_DC:
                    PCM_GD.PCM_5V.add(time, PSM.pmsrmnt[i].voltage);
                    if(PCM_GD.PCM_5V.getItemCount() > 100)
                        PCM_GD.PCM_5V.remove(0);
                    break;
                case PCM_12V_DC:
                    PCM_GD.PCM_12V.add(time, PSM.pmsrmnt[i].voltage);
                    if(PCM_GD.PCM_12V.getItemCount() > 100)
                        PCM_GD.PCM_12V.remove(0);
                    break;
                case PCM_8_2V_DC:
                    PCM_GD.PCM_8_2V.add(time, PSM.pmsrmnt[i].voltage);
                    if(PCM_GD.PCM_8_2V.getItemCount() > 100)
                        PCM_GD.PCM_8_2V.remove(0);
                    break;
                case PCM_BAT_V_DC:
                    PCM_GD.PCM_BATV.add(time, PSM.pmsrmnt[i].voltage);
                    if(PCM_GD.PCM_BATV.getItemCount() > 100)
                        PCM_GD.PCM_BATV.remove(0);
                    break;
                case PWR_SGNAL_UNKNOWN:
                    continue;
                default:
                    break;

            }
        }
    }
}

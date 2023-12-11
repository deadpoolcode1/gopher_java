package ATE_GUI.PCM_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import LMDS_ICD.temp_msrmnts;
import ATE_MAIN.PCM_GD;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class PCM_TemperatureChart extends XY_Graph_Template {
    public PCM_TemperatureChart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        PCM_GD.Temp1 = new XYSeries("On Board Temp #1");         dataset.addSeries(PCM_GD.Temp1);
        PCM_GD.Temp2 = new XYSeries("On Board Temp #2");            dataset.addSeries(PCM_GD.Temp2);
        PCM_GD.Temp3 = new XYSeries("Ext Temp#1");            dataset.addSeries(PCM_GD.Temp3);
    }

    public void UpdateDataset(int timein, temp_msrmnts TM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < TM.tmsrmnt.length; i++) {
            switch (TM.tmsrmnt[i].temp_snsor_id) {
                case PCM_TEMP_ONBRD1:
                    PCM_GD.Temp1.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(PCM_GD.Temp1.getItemCount() > 100)
                        PCM_GD.Temp1.remove(0);
                    break;
                case PCM_TEMP_ONBRD2:
                    PCM_GD.Temp2.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(PCM_GD.Temp2.getItemCount() > 100)
                        PCM_GD.Temp2.remove(0);
                    break;
                case PCM_TEMP_EXT:
                    PCM_GD.Temp3.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(PCM_GD.Temp3.getItemCount() > 100)
                        PCM_GD.Temp3.remove(0);
                    break;
                case TEMP_SNSOR_UNKNOWN:
                    continue;
                default:
                    break;

            }
        }
    }
}


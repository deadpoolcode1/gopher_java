package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import ATE_MAIN.MPU_GD;
import LMDS_ICD.temp_msrmnts;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class MPU_TemperatureChart extends XY_Graph_Template {
    public MPU_TemperatureChart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        MPU_GD.Temp1 = new XYSeries("On Board Temp");         dataset.addSeries(MPU_GD.Temp1);
    }

    public void UpdateDataset(int timein, temp_msrmnts TM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < TM.tmsrmnt.length; i++) {
            switch (TM.tmsrmnt[i].temp_snsor_id) {
                case MPU_TEMP_ONBRD:
                    MPU_GD.Temp1.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(MPU_GD.Temp1.getItemCount() > 100)
                        MPU_GD.Temp1.remove(0);
                    break;
                case TEMP_SNSOR_UNKNOWN:
                    continue;
                default:
                    break;

            }
        }
    }
}

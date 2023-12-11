package ATE_GUI.JPM_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import ATE_MAIN.JPM_GD;
import LMDS_ICD.temp_msrmnts;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class JPM_TemperatureChart extends XY_Graph_Template {
    public JPM_TemperatureChart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        JPM_GD.Temp0 = new XYSeries("Temp0");         dataset.addSeries(JPM_GD.Temp0);
        JPM_GD.Temp1 = new XYSeries("Temp1");         dataset.addSeries(JPM_GD.Temp1);
        JPM_GD.Temp2 = new XYSeries("Temp2");         dataset.addSeries(JPM_GD.Temp2);
        JPM_GD.Temp3 = new XYSeries("Temp3");         dataset.addSeries(JPM_GD.Temp3);
    }

    public void UpdateDataset(int timein, temp_msrmnts TM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < TM.tmsrmnt.length; i++) {
            switch (TM.tmsrmnt[i].temp_snsor_id) {
                case JPM_TEMP_ONBRD0:
                    JPM_GD.Temp0.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(JPM_GD.Temp0.getItemCount() > 100)
                        JPM_GD.Temp0.remove(0);
                    break;
                case JPM_TEMP_ONBRD1:
                    JPM_GD.Temp1.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(JPM_GD.Temp1.getItemCount() > 100)
                        JPM_GD.Temp1.remove(0);
                    break;
                case JPM_TEMP_ONBRD2:
                    JPM_GD.Temp2.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(JPM_GD.Temp2.getItemCount() > 100)
                        JPM_GD.Temp2.remove(0);
                    break;
                case JPM_TEMP_ONBRD3:
                    JPM_GD.Temp3.add(time, (double)TM.tmsrmnt[i].temperature);
                    if(JPM_GD.Temp3.getItemCount() > 100)
                        JPM_GD.Temp3.remove(0);
                    break;
                case TEMP_SNSOR_UNKNOWN:
                    break;
                default:
                    break;

            }
        }
    }
}

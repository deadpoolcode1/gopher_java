package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import ATE_MAIN.LUMO_GD;
import ATE_MAIN.MPU_GD;
import LMDS_ICD.pwr_sgnals_msrmnts;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class MPU_Voltage_Chart extends XY_Graph_Template {
    public MPU_Voltage_Chart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        MPU_GD.MPU_1V2 = new XYSeries("1.2V");          dataset.addSeries(MPU_GD.MPU_1V2);
        MPU_GD.MPU_1V8 = new XYSeries("1.8V");          dataset.addSeries(MPU_GD.MPU_1V8);
        MPU_GD.MPU_2V5 = new XYSeries("2.5V");          dataset.addSeries(MPU_GD.MPU_2V5);
        MPU_GD.MPU_5V = new XYSeries("5V");             dataset.addSeries(MPU_GD.MPU_5V);
        MPU_GD.MPU_8V2 = new XYSeries("8.2V");          dataset.addSeries(MPU_GD.MPU_8V2);
        MPU_GD.MPU_12_MAIN = new XYSeries("12V Main");  dataset.addSeries(MPU_GD.MPU_12_MAIN);
    }

    public void UpdateDataset(int timein, pwr_sgnals_msrmnts PSM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < PSM.pmsrmnt.length; i++) {
            switch (PSM.pmsrmnt[i].pwr_sgnal_id) {
                case MPU_1V2_DC:
                    MPU_GD.MPU_1V2.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_1V2.getItemCount() > 100)
                        MPU_GD.MPU_1V2.remove(0);
                    break;
                case MPU_1V8_DC:
                    MPU_GD.MPU_1V8.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_1V8.getItemCount() > 100)
                        MPU_GD.MPU_1V8.remove(0);
                    break;
                case MPU_2V5_DC:
                    MPU_GD.MPU_2V5.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_2V5.getItemCount() > 100)
                        MPU_GD.MPU_2V5.remove(0);
                    break;
                case MPU_5V_DC:
                    MPU_GD.MPU_5V.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_5V.getItemCount() > 100)
                        MPU_GD.MPU_5V.remove(0);
                    break;
                case MPU_8V2_DC:
                    MPU_GD.MPU_8V2.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_8V2.getItemCount() > 100)
                        MPU_GD.MPU_8V2.remove(0);
                    break;
                case MPU_12V_DC:
                    MPU_GD.MPU_12_MAIN.add(time, PSM.pmsrmnt[i].voltage);
                    if(MPU_GD.MPU_12_MAIN.getItemCount() > 100)
                        MPU_GD.MPU_12_MAIN.remove(0);
                    break;
                case PWR_SGNAL_UNKNOWN:
                    continue;
                default:
                    break;

            }
        }
    }
}

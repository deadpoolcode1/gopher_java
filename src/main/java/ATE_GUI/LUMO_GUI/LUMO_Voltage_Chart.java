package ATE_GUI.LUMO_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import LMDS_ICD.pwr_sgnals_msrmnts;
import ATE_MAIN.LUMO_GD;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class LUMO_Voltage_Chart extends XY_Graph_Template {
    public LUMO_Voltage_Chart(JPanel owner, int width, int height, String chartTitle,
                              String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        LUMO_GD.LUMO_5V = new XYSeries("5.0V");            dataset.addSeries(LUMO_GD.LUMO_5V);
        LUMO_GD.LUMO_12V_ESAD = new XYSeries("12V ESAD");  dataset.addSeries(LUMO_GD.LUMO_12V_ESAD);
        LUMO_GD.LUMO_12V_Main = new XYSeries("12V Main");  dataset.addSeries(LUMO_GD.LUMO_12V_Main);
        LUMO_GD.LUMO_33V = new XYSeries("3.3V");           dataset.addSeries(LUMO_GD.LUMO_33V);
        LUMO_GD.LUMO_55V = new XYSeries("5.5V");           dataset.addSeries(LUMO_GD.LUMO_55V);
    }

    public void UpdateDataset(int timein, pwr_sgnals_msrmnts PSM) {
        double time = (double)timein / 1000.0;
        for (int i = 0; i < PSM.pmsrmnt.length; i++) {
            switch (PSM.pmsrmnt[i].pwr_sgnal_id) {
                case LUMO_12_V_DC_MAIN:
                    LUMO_GD.LUMO_12V_Main.add(time, PSM.pmsrmnt[i].voltage);
                    if(LUMO_GD.LUMO_12V_Main.getItemCount() > 100)
                        LUMO_GD.LUMO_12V_Main.remove(0);
                    break;
                case LUMO_12_V_DC_ESAD_LGC:
                    LUMO_GD.LUMO_12V_ESAD.add(time, PSM.pmsrmnt[i].voltage);
                    if(LUMO_GD.LUMO_12V_ESAD.getItemCount() > 100)
                        LUMO_GD.LUMO_12V_ESAD.remove(0);
                    break;
                case LUMO_3_3_V_DC:
                    LUMO_GD.LUMO_33V.add(time, PSM.pmsrmnt[i].voltage);
                    if(LUMO_GD.LUMO_33V.getItemCount() > 100)
                        LUMO_GD.LUMO_33V.remove(0);
                    break;
                case LUMO_5_V_DC:
                    LUMO_GD.LUMO_5V.add(time, PSM.pmsrmnt[i].voltage);
                    if(LUMO_GD.LUMO_5V.getItemCount() > 100)
                        LUMO_GD.LUMO_5V.remove(0);
                    break;
                case LUMO_5_5_V_DC:
                    LUMO_GD.LUMO_55V.add(time, PSM.pmsrmnt[i].voltage);
                    if(LUMO_GD.LUMO_55V.getItemCount() > 100)
                        LUMO_GD.LUMO_55V.remove(0);
                    break;
                case PWR_SGNAL_UNKNOWN:
                    continue;
                default:
                    break;

            }
        }
    }
}

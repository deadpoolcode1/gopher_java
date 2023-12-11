package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import ATE_MAIN.LUMO_GD;
import ATE_MAIN.MPU_GD;
import LMDS_ICD.air_data;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class MPU_AltitudeChart extends XY_Graph_Template {
    public MPU_AltitudeChart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        MPU_GD.Altitude = new XYSeries("Altitude (AMSL)");
        dataset.addSeries(MPU_GD.Altitude);
    }

    public void UpdateDataset(int timein, air_data AD) {
        double time = (double)timein / 1000.0;
        MPU_GD.Altitude.add(time, AD.altitude);
        if(MPU_GD.Altitude.getItemCount() > 100)
            MPU_GD.Altitude.remove(0);
    }
}

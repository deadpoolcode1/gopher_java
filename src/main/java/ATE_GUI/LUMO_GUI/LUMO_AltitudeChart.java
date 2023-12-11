package ATE_GUI.LUMO_GUI;

import ATE_GUI.GUI_CMN.XY_Graph_Template;
import LMDS_ICD.air_data;
import ATE_MAIN.LUMO_GD;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class LUMO_AltitudeChart extends XY_Graph_Template {
    public LUMO_AltitudeChart(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        super(owner, width, height,chartTitle, xLabel, yLabel);
    }

    @Override
    protected void CreateDataset() {
        dataset = new XYSeriesCollection();
        LUMO_GD.Altitude = new XYSeries("Altitude (AMSL)");
        dataset.addSeries(LUMO_GD.Altitude);
    }

    public void UpdateDataset(int timein, air_data AD) {
        double time = (double)timein / 1000.0;
        LUMO_GD.Altitude.add(time, AD.altitude);
        if(LUMO_GD.Altitude.getItemCount() > 100)
            LUMO_GD.Altitude.remove(0);
    }
}


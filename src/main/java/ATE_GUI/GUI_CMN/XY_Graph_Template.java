package ATE_GUI.GUI_CMN;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class XY_Graph_Template {
    private static String chartTitle, xLabel, yLabel;
    public static XYSeriesCollection dataset = null;
    public static ChartPanel chartPanel=null;

    public XY_Graph_Template(JPanel owner, int width, int height, String chartTitle, String xLabel, String yLabel) {
        chartPanel = new ChartPanel(
                null,width,height,width,height,width,height,
                false,false,false,false,false,false);
        this.chartTitle =  chartTitle;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        CreateDataset();
        chartPanel.setChart(runGraph());
        //Font MF = new Font("SansSerif", Font.PLAIN, 12);
        //LegendTitle LT = chartPanel.getChart().getLegend();  LT.setItemFont(MF);
        //TextTitle TT = chartPanel.getChart().getTitle();  TT.setFont(MF);
        //CategoryPlot plot = (CategoryPlot)chartPanel.getChart().getCategoryPlot(); // TODO class cast exception; unclear.
        //plot.getDomainAxis().setLabelFont(MF);
        //plot.getRangeAxis().setLabelFont(MF);
        owner.add(chartPanel);
    }
    protected void CreateDataset() {
        // override this in any instance
    }
    public JFreeChart runGraph() {
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, false, false);
        final XYPlot plot = xylineChart.getXYPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        plot.setRenderer(renderer);
        Font MFT = new Font("SansSerif", Font.BOLD, 14);
        Font MFL = new Font("SansSerif", Font.PLAIN, 12);
        LegendTitle LT = xylineChart.getLegend();  LT.setItemFont(MFL);
        TextTitle TT = xylineChart.getTitle();  TT.setFont(MFT);
        plot.getDomainAxis().setLabelFont(MFL);
        plot.getRangeAxis().setLabelFont(MFL);
        return xylineChart;
    }
}

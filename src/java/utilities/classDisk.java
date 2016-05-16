/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author Nataniel
 */
public class classDisk {
    public String StorageIndex;
    public String StorageType;
    public String StorageDescr;
    public long StorageAllocationUnits;
    public long StorageSize;
    public long StorageUsed;
    public long StorageAllocationFailures;
    public classDisk(String a){
        StorageIndex = a;
    }
    
    public classDisk(){
        
    }
    
    public JFreeChart createChart(PieDataset dataset) {
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Gráfica de memoria. Memoria total = " + StorageSize + "Gb",  // chart title
            dataset,             // data
            true,               // include legend
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
        
    }
    
    public PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Memoria libre = " + (StorageSize - StorageUsed) + " Gb", StorageSize - StorageUsed );
        dataset.setValue("Memoria en uso = " + StorageUsed + " Gb", StorageUsed);
        //dataset.setValue("Memoria total = " + StorageSize + " Gb", StorageSize );
        
        return dataset;        
    }
    
    
    
}

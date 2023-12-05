package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;


public class alignCFG extends JFrame implements PropertyChangeListener{
	JPanel panel;
	CardLayout cardLayout;
	public alignCFG(String name) {
		
		setTitle("Diet Visualizer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        
        JButton userLabel = new JButton("User's Diet");
        
        userLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	DefaultPieDataset dataset = fetchDataFromDatabase();

                JFreeChart chart = ChartFactory.createPieChart(
                        "Average Plate",
                        dataset,
                        true,
                        true,
                        false
                );

                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setSectionPaint("Fruits and Vegetables", Color.GREEN);
                plot.setSectionPaint("Protein Foods", Color.RED);
                plot.setSectionPaint("Whole Grains", Color.YELLOW);
                plot.setSectionPaint("Dairy", Color.BLUE);

                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(500, 500));
                setContentPane(chartPanel);
                setVisible(true);
            }
        });

        setLayout(new GridLayout(2, 1));
        panel.add(userLabel);
  
        JButton change = new JButton("Back to profile");
        add(panel);
        change.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Profile();
                dispose();
            }
        });
        panel.add(change);
        
        setVisible(true);
	}

	
	
	private DefaultPieDataset fetchDataFromDatabase() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Connection con = null; 
        con = connection.connectDB();
        try {

            String query = "SELECT * FROM meal_nutrient";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            double protein = 0.0;
            double fats = 0.0;
            double carb = 0.0;

            while (resultSet.next()) {
            	if(resultSet.getString("nutrient_name").equals("PROTEIN (g)")) {
            		protein += resultSet.getDouble("total_amount");
            	}
            	if(resultSet.getString("nutrient_name").equals("FAT (TOTAL LIPIDS) (g)")) {
            		fats += resultSet.getDouble("total_amount");
            	}
            	if(resultSet.getString("nutrient_name").equals("CARBOHYDRATE, TOTAL (BY DIFFERENCE) (g)")) {
            		carb += resultSet.getDouble("total_amount");
            	}      
            }
            protein /= (protein+fats+carb);
            dataset.setValue("Proteins", protein);
            fats /= (protein+fats+carb);
            dataset.setValue("Fats", fats);
            carb /= (protein+fats+carb);
            dataset.setValue("Carbohydrates", carb);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }
	@Override
	public void propertyChange(PropertyChangeEvent evt) {}
}

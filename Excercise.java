package statsVisualiser.gui;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.sql.*;  




public class Excercise extends JFrame implements PropertyChangeListener{
    private String date;
    private String time;
    private String type;
    private String intensity;
    private int durationMinutes;
    private double calBurned;
    private String name;
    JFormattedTextField  textField = new JFormattedTextField(DateFormat.getDateInstance(DateFormat.SHORT));
    
    public Excercise(final String name, final String sex, final int height, final int weight, final String date,
			final String units) {
    		this.name = name;

    	 setTitle("Exercise Log");
         setSize(1000, 1000);    
         ExcerciseView(name, sex, height, weight, date, units);
    	
         
    }
	private void ExcerciseView(final String name, final String sex, final int height, final int weight, final String date, final String units) {
		JPanel panel = new JPanel();
        String[] ExerciseType = {"Walk", "Run", "Cycling", "Elliptical", "Rower", "Stair Stepper", "HIIT", "Hiking", "Yoga", "Functional Strength Training", "Dance", "Cooldown", "Core Training", "Pilates", "Tai Chi", "Swimming", "Wheelchair", "Multisport", "Kickboxing" };
    	JComboBox<String> ExerciseList = new JComboBox<String>(ExerciseType);
    	ExerciseList.setSelectedIndex(ExerciseType.length-1);
    	ExerciseList.addActionListener(ExerciseList);
    	
    	ExerciseList.addActionListener(new ActionListener() {
 	        public void actionPerformed(ActionEvent e) {
 	            JComboBox<String> cb1 = (JComboBox<String>) e.getSource();
 	            String ExType = (String) cb1.getSelectedItem();
 	            setType(ExType);
 	        }
 	    });
    	
    	String[] intensity = {"Low", "Medium", "High", "Very High"};
    	JComboBox<String> intensityList = new JComboBox<String>(intensity);
    	intensityList.setSelectedIndex(intensity.length-1);
    	intensityList.addActionListener(intensityList);
    	
    	intensityList.addActionListener(new ActionListener() {
 	        public void actionPerformed(ActionEvent e) {
 	            JComboBox<String> cb2 = (JComboBox<String>) e.getSource();
 	            String intenLevel = (String) cb2.getSelectedItem();
 	            setInten(intenLevel);
 	        }
 	    });

    	JButton time = new JButton("save time");
    	 final JTextField timeField = new JTextField(5); 
    	JLabel timeLabel = new JLabel("Time:");
    	timeLabel.setLabelFor(timeField);
        timeField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || c == ':' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume(); 
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        time.addActionListener(new ActionListener() {
       	 public void actionPerformed(ActionEvent e) {
       		String enteredTime = timeField.getText();
            if (isValidTime(enteredTime)) {
                setTime(enteredTime);
            } 
            else {
           	 JOptionPane.showMessageDialog(null, 
     	                "Invalid Time Format. Try again",
     	                "Error", 
     	                JOptionPane.WARNING_MESSAGE);
            } 
       	 }
           
        });
         
        JButton b = new JButton("save duration");
         
        final JTextField t = new JTextField(5);
        JLabel durationLabel = new JLabel("Duration(minutes):");
     	durationLabel.setLabelFor(t);
        
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
 				durationMinutes(t);
            }
        });
    	panel.add(ExerciseList);
    	panel.add(intensityList);
    	panel.add(timeLabel);
       panel.add(timeField);
       panel.add(time);
       panel.add(durationLabel);
       panel.add(t);
       panel.add(b);

    	add(panel);
 	 	setVisible(true);
 	 	
 	 	Container cp = getContentPane();
		FlowLayout flowLayout = new FlowLayout();
		cp.setLayout(flowLayout);			
		 			
		 
		textField.setValue(new Date());
		textField.setPreferredSize(new Dimension(130, 30));
		    
		final CalendarWindow calendarWindow = new CalendarWindow(); 		    
		calendarWindow.addPropertyChangeListener(this);				
		JButton calendarButton = new JButton("Pick a Date");				
		calendarButton.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			calendarWindow.setLocation(textField.getLocationOnScreen().x, (textField.getLocationOnScreen().y + textField.getHeight()));
			Date d = (Date)textField.getValue();
			
			calendarWindow.resetSelection(d);				
			calendarWindow.setUndecorated(true);
		    calendarWindow.setVisible(true);
		  }
		});		
		cp.add(textField);
		cp.add(calendarButton);
		cp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
       JButton log = new JButton("Save workout");
		JButton results = new JButton("Results");
		JButton saved = new JButton("Saved Workouts");
		panel.add(log);
		panel.add(results);
		panel.add(saved);
		add(panel);
	 	setVisible(true);
	 	
	 	log.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
           	setCals(calculateCaloriesBurnt(name, sex, height, weight, date,units));
				Log(e);
           }
       });	
	 	results.addActionListener(new ActionListener() {
	 		public void actionPerformed(ActionEvent e) {	 			
	 			JOptionPane.showMessageDialog(null, 
   	                "Calories burned " + calBurned, 
   	                "Results", 
   	                JOptionPane.WARNING_MESSAGE);
	 			
	 		}
	 	});
	 	saved.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
				new Exercise(name, sex, height, weight, date, units);
				dispose();
           }
       });
	}
	
    private boolean isValidTime(String input) {
    	if (input.matches("\\d{2}:\\d{2}")) {
            
            String[] parts = input.split(":");
            if (parts.length == 2) {
                try {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        return false;
    }
    
    private void Log(ActionEvent e) {
    	
    	   Connection con = null;
           PreparedStatement ps = null;
    
          
           con = connection.connectDB();
    
           try {
        	    
        	    String sql = "INSERT INTO exercise_log (`date`, `time`, `type_exercise`, `duration`, `intensity`, `calories_burned`, `name`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        	    ps = con.prepareStatement(sql);
        	   
	        	    ps.setString(1, this.date);
	        	    ps.setString(2, this.time);
	        	    ps.setString(3, this.type);
	        	    ps.setInt(4, this.durationMinutes);
	        	    ps.setString(5, this.intensity);
	        	    ps.setDouble(6, this.calBurned);
	        	    ps.setString(7, this.name);
    
        	    int rowsAffected = ps.executeUpdate();
	
	        	if (rowsAffected > 0) {
	        	 	JOptionPane.showMessageDialog(null, 
	        	             "Workout saved", 
	        	             "Success", 
	        	           JOptionPane.WARNING_MESSAGE);
	        	} else {	        	        
	        	 	JOptionPane.showMessageDialog(null, 
	        	           "Unable to save Workout", 
	        	           "Error", 
	        	           JOptionPane.WARNING_MESSAGE);
	        	}
        	} catch (SQLException ex) {
        	    
        	    ex.printStackTrace();
        	} finally {
        	    
        	    try {
        	        if (con != null) {
        	            con.close();
        	        }
        	        if (ps != null) {
        	            ps.close();
        	        }
        	    } catch (SQLException ex) {
        	        ex.printStackTrace(); 
        	    }
        	}
        	

	}

    public double calculateCaloriesBurnt(final String name, final String sex, final int height, final int weight, final String date, final String units) {
    	Calculations calculation = new Calculations();
    	double BMR = calculation.BMR(name, sex, height, weight, date, units);
    	double met = calculation.met(this);
    	double duration = getDurationMinutes();
    	double caloriesBurned = calculation.caloriesBurnt(BMR, met, duration);
    	
    	return Math.round(caloriesBurned);
    }

    public void propertyChange(PropertyChangeEvent event) {
		
		if (event.getPropertyName().equals("selectedDate")) {
            
			java.util.Calendar cal = (java.util.Calendar)event.getNewValue();
			Date selDate =  cal.getTime();
			textField.setValue(selDate);
			setDate(selDate);
        }
		
	}
    
    
    public String getDate() {
    	return "Date: " + this.date;
    }
    public String getTime() {
    	return "Time: " + this.time.toString();
    }
    public String getTypeExercise() {
    	return this.type;
    }
    public int getDurationMinutes() {
    	return this.durationMinutes;
    }
    public String getInten() {
    	return this.intensity;
    }
    
    public void setDate(Date date) {
    	String dateFormat = "yyyy-MM-dd"; 

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

       
        String dateString = sdf.format(date);
    	this.date = dateString;
    }
    public void setTime(String time) {
    	this.time = time;
    }
    public void setType(String type) {
    	this.type = type;
    }
    public void setInten(String intensity) {
    	this.intensity = intensity;
    }
    public void setCals(double cals) {
    	this.calBurned = cals;
    }
    public void durationMinutes(JTextField t) {
    	String text = t.getText();
    	int n = Integer.parseInt(text);
    	if(n<0) {
    		JOptionPane.showMessageDialog(null, 
                    "Duration not valid, please try again", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
    	}
    	else
    		this.durationMinutes = n;
    }
    
    
}

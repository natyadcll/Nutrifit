package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeightToLose extends JFrame {
    private JFrame source;
    private String name;
    private JPanel panel;
    private JFormattedTextField dateField;
    private JTextArea textArea;
    Date selectedDate;

    public WeightToLose() {
        // get the profile_id from the name

        panel = new JPanel();
        add(panel);

        displayForm();

        setTitle("Weight to Lose");
        setSize(500, 300);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public WeightToLose(String name, JFrame source) {
        this();
        this.source = source;
        this.name = name;
    }

    private void displayForm() {
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel selectDateLabel = new JLabel("Select date (yyyy-MM-dd): ");
        topPanel.add(selectDateLabel);

        dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setColumns(10);
        topPanel.add(dateField);

        JButton submitButton = new JButton("Submit");
        topPanel.add(submitButton);
        submitButton.addActionListener(e -> {
            try {
                processData(dateField.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);

        // add a textarea in the center. it should be scrollable but not edittable
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));

        panel.add(scrollPane, BorderLayout.CENTER);

        // add a button in the south to close the window
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (source != null) source.setVisible(true);
                dispose();
            }
        });
        panel.add(closeButton, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }

    private boolean validateDate(String date) {
        // check if date is valid
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            selectedDate = sdf.parse(date);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please enter a valid date.");
            dateField.requestFocus();
            return false;
        }

        // check if date is in the future
        Date today = Date.from(java.time.LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        if (!selectedDate.after(today)) {
            JOptionPane.showMessageDialog(null, "Date should be in the future. Please enter a valid date.");
            dateField.requestFocus();
            return false;
        }

        return true;
    }

    private void processData(String date) throws SQLException {
        // validate the date
        if (!validateDate(date))
            return;

        // get the total calorie intake of this name
        double intake = DbConn.getOne("AVG(calories) AS avg_intake", "meal_log", "name = '" + this.name + "'");

        // get the total calorie burned of this name
        double burned = DbConn.getOne("AVG(calories_burned) AS avg_burned", "exercise_log", "name = '" + this.name + "'");

        // calculate date difference between today and the selected date
        Date today = Date.from(java.time.LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        long diff = selectedDate.getTime() - today.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        // calculate the weight to lose
        double weightToLose = (intake - burned) * diffDays / 7700;

        // display the result in the textArea
        String output = "Selected date: " + date + "\n\n";
        output += "Total calorie intake: " + intake + " kcal\n";
        output += "Total calories burned: " + burned + " kcal\n\n";
        output += "Weight to lose: " + weightToLose + " kg";

        textArea.setText(output);
    }


    public static void main(String[] args) {
        new WeightToLose();
    }
}



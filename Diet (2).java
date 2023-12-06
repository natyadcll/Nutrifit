package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

public class Diet extends JFrame implements ActionListener {
    private final JFrame source;
    private final JFrame thisFrame = this;
    private JPanel panel;
    private HashMap<Integer, Double> addedFoods = new HashMap<>();
    private JPanel topPanel = new JPanel();
    private JLabel dateLabel = new JLabel("Date (yyyy-MM-dd)");
    private JTextField dateField = new JTextField();
    private JTextField quantityField = new JTextField();
    private JLabel typeLabel = new JLabel("Type");
    private JComboBox<String> typeField = new JComboBox<>();
    private JComboBox<String> foodField = new JComboBox<>();
    private JLabel quantityLabel = new JLabel("Quantity");
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list = new JList<>(listModel);
    private JPanel bottomPanel = new JPanel();
    private JButton saveButton = new JButton("Save");
    private JButton addFoodButton = new JButton("Add Food");
    private JButton cancelButton = new JButton("Cancel");
    private final String name;

    public Diet(String name, JFrame source) throws SQLException {
        this.source = source;
        this.name = name;

        panel = new JPanel();
        add(panel);

        setTitle("Diet Logs");
        setSize(500, 550);

        viewMainForm();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void viewMainForm() throws SQLException {
        // clear the controls in the panel

        panel.removeAll();
        panel.setLayout(new FlowLayout());

        // prepare the table model. there will be 3 columns: date, type, and calories (calculated as sum of all food calories)
        Vector<String> columns = new Vector<>();
        columns.add("ID");
        columns.add("Date");
        columns.add("Type");
        columns.add("Calories");

        Vector<Vector<String>> data = new Vector<>();
        Connection con = DbConn.getConnection();

        // get all the diet logs of this profile_id from the database
        ResultSet resultSet = con.prepareStatement("SELECT * FROM meal_log WHERE name = '" + this.name + "'")
                                    .executeQuery();

        // for each diet log, get the date, type, and calories
        while (resultSet.next()) {
            Vector<String> row = new Vector<>();
            row.add(resultSet.getString("id"));
            row.add(resultSet.getString("date"));
            row.add(resultSet.getString("type"));
            row.add(resultSet.getDouble("calories") + " kcal");
            data.add(row);
        }


        JTable table = new JTable(data, columns);
        // make table uneditable
        table.setDefaultEditor(Object.class, null);
        // hide first column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        panel.add(new JScrollPane(table));

        JButton addDietLogButton = new JButton("Add Diet Log");
        panel.add(addDietLogButton);
        addDietLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DisplayDietLogScreen();
            }
        });

        JButton viewNutrientsButton = new JButton("View Nutrients");
        panel.add(viewNutrientsButton);
        viewNutrientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the date and type from the selected row
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Please select a diet log",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                long id = Long.parseLong(table.getValueAt(row, 0).toString());
                DisplayNutrientsScreen(id);
            }
        });

        JButton closeButton = new JButton("Close");
        panel.add(closeButton);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisFrame.dispose();
                source.setVisible(true);
            }
        });

        panel.revalidate();
        panel.repaint();
    }

    private void DisplayNutrientsScreen(long id) {
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        // prepare the table model. there will be 2 columns: Nutrient and Amount
        Vector<String> columns = new Vector<>();
        columns.add("Nutrient");
        columns.add("Amount");

        Vector<Vector<String>> data = new Vector<>();
        HashMap<String, Double> nutrients = new HashMap<>();
        Connection con;
        try {
            con = DbConn.getConnection();

            ResultSet resultSet = con.prepareStatement("SELECT * FROM meal_log WHERE id = " + id).executeQuery();
            resultSet.next();
            String type = resultSet.getString(3);
            String date = resultSet.getString(2);

            panel.add(new JLabel("Nutrient contents for " + type + " on " + date + "."), BorderLayout.NORTH);

            // retrieve the contents of the meal_nutrient table for this id
            ResultSet mealNutrients = con.prepareStatement("SELECT * FROM meal_nutrient WHERE meal_content_id = " + id + " ORDER BY nutrient_name").executeQuery();
            while (mealNutrients.next()) {
                Vector<String> row = new Vector<>();
                row.add(mealNutrients.getString(2));
                row.add(String.valueOf(mealNutrients.getDouble(3)));
                data.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JTable table = new JTable(data, columns);
        // make table uneditable
        table.setDefaultEditor(Object.class, null);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // close button at the bottom
        JButton closeButton = new JButton("Close");
        panel.add(closeButton, BorderLayout.SOUTH);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    viewMainForm();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.revalidate();
        panel.repaint();
    }

    private void DisplayDietLogScreen() {
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        topPanel.setLayout(new GridLayout(5, 2));

        topPanel.add(dateLabel);
        topPanel.add(dateField);
        topPanel.add(typeLabel);

        // create the typeField as a JComboBox with the following options: Breakfast, Lunch, Dinner, Snack
        typeField.addItem("Breakfast");
        typeField.addItem("Lunch");
        typeField.addItem("Dinner");
        typeField.addItem("Snack");
        topPanel.add(typeField);

        JLabel foodLabel = new JLabel("Food/Ingredient");
        topPanel.add(foodLabel);

        // fill-in the foodField with all the food names
        try {
            Connection con = DbConn.getConnection();
            ResultSet resultSet = con.prepareStatement("SELECT * FROM food_name").executeQuery();
            while (resultSet.next()) {
                foodField.addItem(resultSet.getString("description"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        topPanel.add(foodField);
        topPanel.add(quantityLabel);
        topPanel.add(quantityField);
        topPanel.add(new JLabel()); // skip a cell
        // add the food to the diet log
        topPanel.add(addFoodButton);
        addFoodButton.addActionListener(this::addFood);

        // create a JList to display the foods in the diet log. It should take 2 columns
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(-1);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bottomPanel.setLayout(new GridLayout(1, 2));

        // save button
        bottomPanel.add(saveButton);
        saveButton.addActionListener(this::saveLog);

        // cancel button
        bottomPanel.add(cancelButton);
        cancelButton.addActionListener(this::cancelLog);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(list));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }

    private void cancelLog(ActionEvent actionEvent) {
        try {
            viewMainForm();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isLogValid() {
        try {
            // validate that the dateField is a valid date, that the typeField has a selected index, and that the list is not empty
            if (!validate(dateField, new DateValidator(), "Please enter a valid date in yyyy-MM-dd format.", Optional.empty()) ||
                    !hasSelected(typeField, "Please select a type") ||
                    listModel.isEmpty()) {
                return false;
            }

            Connection con = DbConn.getConnection();

            // additional validation for non-snack meal: there could only be one
            if (!typeField.getSelectedItem().toString().equals("Snack")) {
                ResultSet resultSet = con.prepareStatement("SELECT * FROM meal_log WHERE name = '" + name +
                                "' AND date = '" + dateField.getText() + "' AND type = '" + typeField.getSelectedItem().toString() + "'")
                        .executeQuery();
                if (resultSet.next()) {
                    // already existing
                    JOptionPane.showMessageDialog(null,
                            "You have already logged your " + typeField.getSelectedItem().toString().toLowerCase() + " for this day.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    typeField.requestFocus();
                    return false;
                }
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveLog(ActionEvent actionEvent) {
        if (!isLogValid()) return;

        // save filled-in fields to the meal_log table and store the new id to a variable
        try {
            Connection con = DbConn.getConnection();
            con.prepareStatement("INSERT INTO meal_log (name, date, type) VALUES ('" + name + "', '" + dateField.getText() + "', '" + typeField.getSelectedItem().toString() + "')").executeUpdate();
            ResultSet resultSet = con.prepareStatement("SELECT * FROM meal_log WHERE name = '" + name + "' ORDER BY id DESC;").executeQuery();
            resultSet.next();
            int meal_id = resultSet.getInt("id");
            double calories = 0;
            HashMap<String, Double> nutrients = new HashMap<>();

            // save the foods in the diet log to the hashmap
            for (int food_id : addedFoods.keySet()) {
                // retrieve the nutrients of this food_id from the nutrient_amount table
                ResultSet nutrientResult = con.prepareStatement("SELECT * FROM nutrient_amount WHERE food_id = " + food_id).executeQuery();
                // for each nutrient, save the nutrient_name and amount to the hashmap
                while (nutrientResult.next()) {
                    // retrieve nutrient name of nutrient_id
                    ResultSet nutrientNameResult = con.prepareStatement("SELECT * FROM nutrient_name WHERE id = " + nutrientResult.getInt("nutrient_id")).executeQuery();
                    nutrientNameResult.next();
                    String nutrientName = nutrientNameResult.getString("name") + " (" + nutrientNameResult.getString("unit") + ")";

                    // add the nutrient to the hashmap
                    if (nutrients.containsKey(nutrientName)) {
                        nutrients.put(nutrientName, nutrients.get(nutrientName) + nutrientResult.getDouble("amount") * addedFoods.get(food_id));
                    } else {
                        nutrients.put(nutrientName, nutrientResult.getDouble("amount") * addedFoods.get(food_id));
                    }

                    // check if this nutrient is Calories (208) and add to the total calories
                    if (nutrientResult.getInt("nutrient_id") == 208) {
                        calories += nutrientResult.getDouble("amount") * addedFoods.get(food_id);
                    }
                }
            }

            // save nutrients to the meal_nutrient table
            for (String nutrient : nutrients.keySet()) {
                // skip zero values
                if (nutrients.get(nutrient) < 0.001)
                    continue;
                // save the nutrient_id, meal_content_id, and total_amount to the meal_nutrient table
                con.prepareStatement("INSERT INTO meal_nutrient (nutrient_name, meal_content_id, total_amount) VALUES ('" + nutrient + "', " + meal_id + ", " + nutrients.get(nutrient) + ")").executeUpdate();
            }

            // update meal_log to have the calories
            con.prepareStatement("UPDATE meal_log SET calories = " + calories + " WHERE id = " + meal_id).executeUpdate();

            // show a success message
            JOptionPane.showMessageDialog(null,
                    "Diet log saved",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // go back to the main form
            viewMainForm();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void addFood(ActionEvent actionEvent) {
        try {
            // validate that foodField has a selected index and that quantityField is a number greater than 0
            if (!hasSelected(foodField, "Please select a food") ||
                    !validate(quantityField, new NumberValidator(), "Please enter a quantity greater than 0", Optional.of(0.0)))
                return;

            Connection con = DbConn.getConnection();
            ResultSet resultSet = con.prepareStatement("SELECT * FROM food_name WHERE description = '" + foodField.getSelectedItem() + "' ORDER BY description;").executeQuery();
            resultSet.next();
            int food_id = resultSet.getInt("id");

            // add the food id to the list of added food ids
            addedFoods.put(food_id, Double.parseDouble(quantityField.getText()));

            // update the list of foods in the diet log
            DefaultListModel<String> listModel = (DefaultListModel<String>) ((JList<String>) ((JScrollPane) panel.getComponent(1)).getViewport().getView()).getModel();
            listModel.addElement(foodField.getSelectedItem() + " (" + quantityField.getText() + ")");

            // clear the fields
            foodField.setSelectedIndex(0);
            quantityField.setText("");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private boolean hasSelected(JComboBox<String> comboBox, String errorMessage) {
    	if (comboBox.getSelectedIndex() == -1) {
    		JOptionPane.showMessageDialog(null,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            comboBox.requestFocus();
    		return false;
    	}
    	return true;
    }

    private boolean validate(JTextField textField, Validator validator, String message, Optional<Double> minimum) {
        return validator.validate(textField, message, minimum);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}


interface Validator {
    boolean validate(JTextField textField, String message, Optional<Double> minimum);
}

class TextValidator implements Validator {
    @Override
    public boolean validate(JTextField textField, String message, Optional<Double> minimum) {
        if (textField.getText().equals("")) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            textField.requestFocus();
            return false;
        }
        return true;
    }
}


class NumberValidator implements Validator {

    @Override
    public boolean validate(JTextField textField, String message, Optional<Double> minimum) {
        double value = 0;
        if (textField.getText().equals("")) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            textField.requestFocus();
            return false;
        }
        try {
            value = Double.parseDouble(textField.getText());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            textField.requestFocus();
            return false;
        }
        if (minimum.isPresent() && value < minimum.get()) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            textField.requestFocus();
            return false;
        }
        return true;
    }
}

class DateValidator implements Validator {

    @Override
    public boolean validate(JTextField textField, String message, Optional<Double> minimum) {
        String date = textField.getText();
        // Check if a date string is in the correct format (YYYY-MM-DD)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException | ClassCastException e) { //makes sure can be casted as type date
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
package statsVisualiser.gui;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;

import java.sql.*;

public class Profile extends JFrame {

	private JPanel panel1;

	private Profile() {
		// Constructor
		setTitle("Main Page");
		setSize(500, 500);

		panel1 = new JPanel();
		// For choosing a profile or creating an account
		JButton login = new JButton("Choose Profile");
		panel1.add(login);
		JButton signup = new JButton("Create Account");
		panel1.add(signup);
		add(panel1);
		setVisible(true);
		// Add action listeners to the buttons
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Subsplash();
			}
		});

		signup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// triggers create profile case
				AccountManager();
			}
		});

	}

	public boolean checkNameExists(final String name) {
		// Check if name already exists in the database
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT COUNT(*) FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count > 0; // this means name exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle any database connection or query errors.
		}

		return false; // Return false in case of an error.
	}

	public boolean weightHeightValid(String entry) {
		// Check if a string can be parsed as a valid weight or height
		try {
			double entry1 = Double.parseDouble(entry);

		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public boolean dateCorrect(String date) {
		 // Check if a date string is in the correct format (YYYY-MM-DD)
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //this is the format we want it
		dateFormat.setLenient(false);
		try {
			Date newdate = (Date) dateFormat.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(newdate);
			int year = calendar.get(Calendar.YEAR);
	        int month = calendar.get(Calendar.MONTH) + 1; // this makes sure that month is 0# format
	        int day = calendar.get(Calendar.DAY_OF_MONTH);
	        if (year >= 1 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
	            return true; 
	        }
		} catch (ParseException | ClassCastException e) { //makes sure can be casted as type date
			return false;
		}
		return false;
	}

	public String getSex(final String name) {
		//associated with name
		String sex = null;
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT * FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				sex = resultSet.getString("sex");
				con1.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sex;
	}

	public int getHeight(final String name) {
		//associated with name
		int height = 0;
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT * FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				height = resultSet.getInt("height");
				con1.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return height;
	}

	public int getWeight(final String name) {
		//associated with name
		int weight = 0;
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT * FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				weight = resultSet.getInt("weight");
				con1.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return weight;
	}

	public String getDate(final String name) {
		String date = null;
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT * FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				date = resultSet.getString("date");
				con1.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return date;
	}

	public String getunit(final String name) {
		String sex = null;
		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT * FROM new_schema.profile WHERE name = ?";
			PreparedStatement ps = con1.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				sex = resultSet.getString("units");
				con1.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sex;
	}

	public void AccountManager() {
		
		panel1 = new JPanel(new GridLayout(7, 1));

		JLabel NameLabel = new JLabel("Name:");
		panel1.add(NameLabel);
		final JTextField NameField = new JTextField();
		panel1.add(NameField);

		JLabel sLabel = new JLabel("Sex:");
		panel1.add(sLabel);
		String[] sexOptions = { "M", "F" };
		// final JTextField sField = new JTextField();
		final JComboBox<String> sFeild = new JComboBox<>(sexOptions);
		panel1.add(sFeild);

		JLabel HeightLabel = new JLabel("Height:");
		panel1.add(HeightLabel);
		final JTextField HeightField = new JTextField();
		panel1.add(HeightField);

		JLabel WeightLabel = new JLabel("Weight:");
		panel1.add(WeightLabel);
		final JTextField WeightField = new JTextField();
		panel1.add(WeightField);

		JLabel BirthLabel = new JLabel("Date of birth YYYY-MM-DD:");
		panel1.add(BirthLabel);
		final JTextField BirthField = new JTextField();
		panel1.add(BirthField);

		JLabel uLabel = new JLabel("Imperial(I) or Metric(M):");
		panel1.add(uLabel);
		String[] uOptions = { "I", "M" };

		final JComboBox<String> uFeild = new JComboBox<>(uOptions);
		panel1.add(uFeild);

		JButton enterButton = new JButton("Enter");
		panel1.add(enterButton);

		getContentPane().removeAll();
		add(panel1);
		setVisible(true);

		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// AccountManager();
				String name = NameField.getText();
				String sex = (String) sFeild.getSelectedItem();
				String height = HeightField.getText();
				String weight = WeightField.getText();
				String date = BirthField.getText();
				String units = (String) uFeild.getSelectedItem();
				if (name.isEmpty() || sex.isEmpty() || height.isEmpty() || weight.isEmpty() || date.isEmpty()
						|| units.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please fill in all fields.");
					AccountManager();
				} else {

					if (checkNameExists(name) == true) {
						JOptionPane.showMessageDialog(null, "Name already exists");
						AccountManager();
					}
					if (weightHeightValid(weight) == false && weightHeightValid(height) == false
							|| dateCorrect(date) == false) {
						JOptionPane.showMessageDialog(null, "please enter a valid height, weight, and date");
						AccountManager();
					} else {
						AddData(name, sex, Integer.parseInt(height), Integer.parseInt(height), date, units);
						// Call AddData to add a new profile to the database
					}

				}

			}
		});

	}

	private void AddData(String name, String sex, int height, int weight, String date, String units) {
		Connection con = connection.connectDB();
		//connect to database
		PreparedStatement ps = null;
		try {
			// prepare an SQL statement to insert the data
			String sql = "INSERT INTO new_schema.profile (`name`, `sex`, `height`, `weight`, `date`, `units`) VALUES (?, ?, ?, ?, ?, ?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, sex);
			ps.setInt(3, height);
			ps.setInt(4, weight);
			ps.setString(5, date);
			ps.setString(6, units);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				JOptionPane.showMessageDialog(null, "Profile saved", "Success", JOptionPane.WARNING_MESSAGE);
			}
			// close the database connection
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// call ProfileView to display the newly created profile
		ProfileView(name);

	}

	private void Subsplash() {
		// Create a drop-down menu with existing profile names
		panel1 = new JPanel();
		JComboBox<String> nameDropdown = new JComboBox<String>();
		getContentPane().removeAll();
		add(panel1);
		panel1.add(nameDropdown);

		Connection con1 = connection.connectDB();
		try {

			String sql = "SELECT name FROM new_schema.profile";

			PreparedStatement ps = con1.prepareStatement(sql);
			Statement statement = con1.createStatement();

			ResultSet resultSet = statement.executeQuery(sql);

			while (resultSet.next()) {
				nameDropdown.addItem(resultSet.getString("name"));
			}

			con1.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		
		panel1.setVisible(true);

		nameDropdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> source = (JComboBox<String>) e.getSource();
				String selectedName = (String) source.getSelectedItem();
				ProfileView(selectedName);
			}
		});

		// once done, user can edit profile or select account then sign in
	}

	public void ProfileView(final String name) {
		// display the profile data and provide an option to edit
		final String sex = getSex(name);
		final int height = getHeight(name);
		final int weight = getWeight(name);
		final String date = getDate(name);
		final String units = getunit(name);
		String w = "lbs";
		String h = "in";
		if (units.equals("M")) {
			w = "kg";
			h = "cm";
		}

		panel1.removeAll();

		JPanel panel3 = new JPanel(new GridLayout(7, 1));
		JLabel NameLabel = new JLabel("Name: " + name);
		panel1.add(NameLabel);

		JLabel sLabel = new JLabel("Sex: " + sex);
		panel1.add(sLabel);

		JLabel HeightLabel = new JLabel("Height: " + height + " " + h);
		panel1.add(HeightLabel);

		JLabel WeightLabel = new JLabel("Weight: " + weight + " " + w);
		panel1.add(WeightLabel);

		JLabel BirthLabel = new JLabel("Date of birth: " + date);
		panel1.add(BirthLabel);

		JButton editButton = new JButton("Edit Profile");
		panel1.add(editButton);
		
		getContentPane().revalidate();
		getContentPane().repaint();

		panel1.setVisible(true);

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditProfile(name, sex, height, weight, date, units);
			}
		});

		// establish a connection
		// search for name and info about the name
		// print all profile info on screen
		// displays data of profile

	}

	private void EditProfile(final String name, final String sex, final int height, final int weight, final String date,
			final String units) {
		// display the profile editing interface
		panel1.removeAll();
		panel1 = new JPanel(new GridLayout(7, 1));
		

		String w = "lbs";
		String h = "in";
		if (units.equals("M")) {
			w = "kg";
			h = "cm";
		}

		JLabel NameLabel = new JLabel("Name: " + name);
		panel1.add(NameLabel);
		final JTextField NameField = new JTextField();
		panel1.add(NameField);

		JLabel sLabel = new JLabel("Sex: " + sex);
		panel1.add(sLabel);
		String[] sexOptions = { "M", "F" };
		final JComboBox<String> sFeild = new JComboBox<>(sexOptions);
		panel1.add(sFeild);

		JLabel HeightLabel = new JLabel("Height: " + height + " " + h);
		panel1.add(HeightLabel);
		final JTextField HeightField = new JTextField();
		panel1.add(HeightField);

		JLabel WeightLabel = new JLabel("Weight: " + weight + " " + w);
		panel1.add(WeightLabel);
		final JTextField WeightField = new JTextField();
		panel1.add(WeightField);

		JLabel BirthLabel = new JLabel("Date of birth: " + date);
		panel1.add(BirthLabel);
		final JTextField BirthField = new JTextField();
		panel1.add(BirthField);

		JLabel uLabel = new JLabel("Imperial(I) or Metric(M):");
		panel1.add(uLabel);
		String[] uOptions = { "I", "M" };

		final JComboBox<String> uFeild = new JComboBox<>(uOptions);
		panel1.add(uFeild);

		JButton enterButton = new JButton("Enter");
		panel1.add(enterButton);

		getContentPane().removeAll();
		add(panel1);
		setVisible(true);

		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// validate input and take appropriate actions
				String name1 = NameField.getText();
				String sex1 = (String) sFeild.getSelectedItem();
				String height1 = HeightField.getText();
				String weight1 = WeightField.getText();
				String date1 = BirthField.getText();
				String units1 = (String) uFeild.getSelectedItem();
				if (name1.isEmpty()) {
					name1 = name;
				}
				if (height1.isEmpty() || !weightHeightValid(height1)) {
					height1 = Integer.toString(height);
				}
				if (weight1.isEmpty() || !weightHeightValid(weight1)) {
					weight1 = Integer.toString(weight);
				}
				if (date1.isEmpty() || !dateCorrect(date)) {
					date1 = date;
				}
				// call sendForReview to update the profile data
				sendForReview(name, name1, sex1, height1, weight1, date1, units1);
			}

		});
	}

	private void sendForReview(String name, String name1, String sex, String height, String weight, String date,
			String units) {
		// update and review the user's profile information
		Connection con = connection.connectDB();
		if (!name.equals(name1)) {
			PreparedStatement ps = null;
			try {
				// update the profile name if it has changed
				String sql = "UPDATE `new_schema`.`profile` SET `name` = ? WHERE (`name` = ?)";
				ps = con.prepareStatement(sql);
				ps.setString(1, name1);
				ps.setString(2, name);

				int rowsAffected = ps.executeUpdate();
				if (rowsAffected > 0) {

				}
				con.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
			name = name1;
		}
		try {
			Connection con1 = connection.connectDB();
			PreparedStatement ps = null;
			String sql = "UPDATE `new_schema`.`profile` SET `sex` = ?, `height` = ?, `weight` = ?, `date` = ?, `units` = ? WHERE (`name` = ?)";
			ps = con1.prepareStatement(sql);
			ps.setString(1, sex);
			ps.setInt(2, Integer.parseInt(height));
			ps.setInt(3, Integer.parseInt(weight));
			ps.setString(4, date);
			ps.setString(5, units);
			ps.setString(6, name);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				// Update successful
			}
			con1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// call ProfileView to display the updated profile
		ProfileView(name);
	}

	public static void main(String[] args) {
		new Profile();
	}

}
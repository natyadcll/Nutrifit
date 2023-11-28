package statsVisualiser.gui;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileGetandSet {
	
	public static boolean checkNameExists(final String name) {
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

	public static boolean weightHeightValid(String entry) {
		// Check if a string can be parsed as a valid weight or height
		try {
			double entry1 = Double.parseDouble(entry);

		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public static boolean dateCorrect(String date) {
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

	public static String getSex(final String name) {
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

	public static int getHeight(final String name) {
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

	public static int getWeight(final String name) {
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

	public static String getDate(final String name) {
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

	public static String getunit(final String name) {
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

}

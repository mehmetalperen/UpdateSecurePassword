import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class VerifyPassword {

	public static void main(String[] args) {
		try {
			System.out.println("Expected: true. Got: " + verifyCustomerCredentials("a@email.com", "a2"));
			System.out.println("Expected: false. Got: " + verifyCustomerCredentials("a@email.com", "a3"));
			System.out.println("Expected: true. Got: " + verifyEmployeeCredentials("classta@email.edu", "classta"));
			System.out.println("Expected: false. Got: " + verifyEmployeeCredentials("classta@email.edu", "classta"));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Connection getConnection() throws SQLException, ClassNotFoundException {
		String loginUser = "admin";
		String loginPasswd = "admin";
		String loginUrl = "jdbc:mysql://localhost:3307/moviedb";

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	}

	private static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean verifyCustomerCredentials(String email, String password) throws SQLException, ClassNotFoundException {
		String query = String.format("SELECT * from customers where email='%s'", email);
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				String encryptedPassword = rs.getString("password");
				return new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
			}
		} finally {
			closeConnection(connection, statement, rs);
		}
		return false;
	}

	private static boolean verifyEmployeeCredentials(String email, String password) throws SQLException, ClassNotFoundException {
		String query = String.format("SELECT * from employees where email='%s'", email);
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				String encryptedPassword = rs.getString("password");
				return new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
			}
		} finally {
			closeConnection(connection, statement, rs);
		}
		return false;
	}
}

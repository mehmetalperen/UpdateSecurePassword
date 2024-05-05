import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateSecurePassword {

    /*
     *
     * This program updates your existing moviedb customers and employees tables to change the
     * plain text passwords to encrypted passwords.
     *
     * You should only run this program **once**, because this program uses the
     * existing passwords as real passwords, then replaces them. If you run it more
     * than once, it will treat the encrypted passwords as real passwords and
     * generate wrong values.
     *
     */
    public static void main(String[] args) throws Exception {

        String loginUser = "admin";
        String loginPasswd = "admin";
        String loginUrl = "jdbc:mysql://localhost:3307/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        String alterCustomerQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        int alterCustomerResult = statement.executeUpdate(alterCustomerQuery);
        System.out.println("altering customers table schema completed, " + alterCustomerResult + " rows affected");


        String customerQuery = "SELECT id, password from customers";
        ResultSet customerRs = statement.executeQuery(customerQuery);

        String alterEmployeeQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";

        Statement stmt = connection.createStatement();
        int alterEmployeeResult = stmt.executeUpdate(alterEmployeeQuery);
        System.out.println("altering employees table schema completed, " + alterEmployeeResult + " rows affected");

        String employeeQuery = "SELECT email, password FROM employees";
        ResultSet employeeRs = stmt.executeQuery(employeeQuery);


        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();


        ArrayList<String> updateQueryList = new ArrayList<>();

        int i = 0;
        System.out.println(customerRs);
        while (customerRs.next()) {

            String id = customerRs.getString("id");
            String password = customerRs.getString("password");

            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            String updateQuery = String.format("UPDATE customers SET password='%s' WHERE id=%s;", encryptedPassword, id);
            updateQueryList.add(updateQuery);
        }
        customerRs.close();

        System.out.println("encrypting employee passwords (this might take a while)");
        while (employeeRs.next()) {
            String email = employeeRs.getString("email");
            String password = employeeRs.getString("password");

            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s';", encryptedPassword, email);
            updateQueryList.add(updateQuery);
        }
        employeeRs.close();

        System.out.println("updating passwords");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            int updateResult = statement.executeUpdate(updateQuery);
            count += updateResult;
        }
        System.out.println("updating passwords completed, " + count + " rows affected");

        statement.close();
        connection.close();

        System.out.println("finished");
    }
}

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

//        connect with DB MySQL
        String userName = "root";
        String password = "pass";
//        String connectionUrl = "jdbc:mysql://localhost:3306/trading";
        String connectionUrl = "jdbc:mysql://localhost/trading?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
             Statement statement = connection.createStatement()) {

//            delete table from db in MySQL
//            statement.executeUpdate("DROP TABLE RUNS");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS accountBalance(" +
                    "accountBalance_id INT NOT NULL AUTO_INCREMENT," +
                    "accountBalance_date VARCHAR(12)," +
                    "balance VARCHAR(12)," +
                    "equity VARCHAR(12)," +
                    "margin VARCHAR(12)," +
                    "freeMargin VARCHAR(12)," +
                    "PRIMARY KEY (accountBalance_id))");
            statement.executeUpdate("INSERT INTO accountBalance (balance) VALUES('Test')");
            statement.executeUpdate("INSERT INTO accountBalance SET balance='Test_2', equity='new_value'");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM accountBalance");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("accountBalance_id"));
                System.out.println(resultSet.getString("balance"));
                System.out.println(resultSet.getString("equity"));
                System.out.println("------------------------------------------");
            }

            System.out.println("****************************************");
            ResultSet resultSet1 = statement.executeQuery("SELECT balance FROM accountBalance WHERE accountBalance_id = 1");
            while (resultSet1.next()) {
                System.out.println(resultSet1.getString(1));
            }

        }

//        Get list of file in PC directory
        File directory = new File("C:\\Users\\roman.stetsiuk\\Documents\\VK\\XTB_raports");
        File[] filesList = directory.listFiles();

    }
}

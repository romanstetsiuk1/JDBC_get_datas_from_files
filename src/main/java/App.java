import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String userName = "root";
        String password = "pass";
        String connectionUrl = "jdbc:mysql://localhost:3306/trading";
        Class.forName("com.mysql.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password)) {
            System.out.println("We are connected");
        }

        File directory = new File("C:\\Users\\roman.stetsiuk\\Documents\\VK\\XTB_raports");
        File[] filesList = directory.listFiles();

    }
}

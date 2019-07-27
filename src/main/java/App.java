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

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS accountBalance(" +
                    "accountBalance_id INT NOT NULL AUTO_INCREMENT," +
                    "accountBalance_date VARCHAR(12)," +
                    "balance VARCHAR(12)," +
                    "equity VARCHAR(12)," +
                    "margin VARCHAR(12)," +
                    "freeMargin VARCHAR(12)," +
                    "PRIMARY KEY (accountBalance_id))");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS closedTransactions(" +
                    "closedTransaction_id INT NOT NULL AUTO_INCREMENT," +
                    "raportDate VARCHAR(12)," +
                    "ticket VARCHAR(15)," +
                    "openTimeTransactions VARCHAR(25)," +
                    "typeTransactions VARCHAR(5)," +
                    "lots VARCHAR(5)," +
                    "symbol VARCHAR(10)," +
                    "exchangeCode VARCHAR(5)," +
                    "assetClass VARCHAR(20)," +
                    "openPrice VARCHAR(10)," +
                    "closeTime VARCHAR(25)," +
                    "closePrise VARCHAR(10)," +
                    "conversionRate VARCHAR(15)," +
                    "commissions VARCHAR(10)," +
                    "swap VARCHAR(10)," +
                    "profit VARCHAR(10)," +
                    "PRIMARY  KEY (closedTransaction_id))");


            statement.executeUpdate("CREATE TABLE IF NOT EXISTS openTransactions(" +
                    "openTransactions_id INT NOT NULL AUTO_INCREMENT," +
                    "raportDate VARCHAR(12)," +
                    "ticket VARCHAR(15)," +
                    "openTimeTransactions VARCHAR(25)," +
                    "typeTransactions VARCHAR(5)," +
                    "lots VARCHAR(5)," +
                    "symbol VARCHAR(10)," +
                    "exchangeCode VARCHAR(5)," +
                    "assetClass VARCHAR(20)," +
                    "openPrice VARCHAR(10)," +
                    "marketPrise VARCHAR(10)," +
                    "conversionRate VARCHAR(15)," +
                    "commissions VARCHAR(10)," +
                    "swap VARCHAR(10)," +
                    "profit VARCHAR(10)," +
                    "PRIMARY  KEY (openTransactions_id))");

            statement.executeUpdate("CREATE TABLE  IF NOT EXISTS totalValues(" +
                    "totalValues_id INT NOT NULL AUTO_INCREMENT," +
                    "raportDate VARCHAR(12)," +
                    "closedTransactions_commission VARCHAR(10)," +
                    "closedTransactions_swap VARCHAR(10)," +
                    "closedTransactions_profit VARCHAR(10)," +
                    "closedTransactions_closedTrade VARCHAR(10)," +
                    "openTransactions_commission VARCHAR(10)," +
                    "openTransactions_swap VARCHAR(10)," +
                    "openTransactions_profit VARCHAR(10)," +
                    "openTransactions_floating VARCHAR(10)," +
                    "PRIMARY KEY (totalValues_id))");

        }

//        Get list of file in PC directory
        File directory = new File("C:\\Users\\roman.stetsiuk\\Documents\\VK\\XTB_raports");
        File[] filesList = directory.listFiles();

    }
}

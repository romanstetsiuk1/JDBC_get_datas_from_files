import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String endOfDay = "";


        final Logger logger = Logger.getLogger(App.class);

//        connect with DB MySQL
        String userName = "root";
        String password = "pass";
//        String connectionUrl = "jdbc:mysql://localhost:3306/trading";
        String connectionUrl = "jdbc:mysql://localhost/trading?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
             Statement statement = connection.createStatement()) {

            CreteTableInMySql createTables = new CreteTableInMySql();
            createTables.createTableAccountBalance(statement);
            createTables.createTableTotalValues(statement);
            createTables.createTableClosedTransactions(statement);
            createTables.createTableOpenTransactions(statement);
            createTables.createTableDepositsWithdrawals(statement);

        }

        String filesDirectory = "/home/roman/Roman/tradeDoc/reports";
        String filesDoneDirectory = "/home/roman/Roman/tradeDoc/reports/DONE";

        //        Get list of file in PC directory
        File directory = new File(filesDirectory);
        File[] filesList = directory.listFiles();

        //            Read lines from files
        for (File file : filesList) {
            if (file.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String currentLine;

//                        Get Date report and write this information in LogFile
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        if (currentLine.contains("End of day")) {
                            endOfDay = currentLine;
                            endOfDay.trim();
                            logger.info("\n**********************************************************\n" +
                                    endOfDay +
                                    "\n**********************************************************\n");
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }

}

import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class App {

    private static boolean containsNumberValue(String line) {
        String[] numbersValue = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String checkLine : numbersValue) {
            if (line.contains(checkLine)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

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
            int typeAnaliseOperation = 0;
            String actualDayValue = "";
            if (file.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String currentLine;

//                        Get Date report and write this information in LogFile
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        currentLine.trim();

                        if (currentLine.contains("End of day")) {
                            logger.info("\n**********************************************************\n" +
                                    currentLine.trim() +
                                    "\n**********************************************************\n");
                            String[] splitEndOfDay = currentLine.trim().split(" ");

                            StringBuilder actualDayReport = new StringBuilder();
                            for (int i = 3; i < splitEndOfDay.length; i++) {
                                actualDayReport.append(splitEndOfDay[i] + " ");
                            }
                            actualDayValue = actualDayReport.toString().trim();
                        }

                        if (currentLine.contains("Account Balance")) {
                            typeAnaliseOperation++;
                        }
                        if (typeAnaliseOperation == 1 && containsNumberValue(currentLine)) {
                            String[] splitAccountBalanceValues = currentLine.split("\t");
                            for (String valueAccountBalance : splitAccountBalanceValues) {
                                if (!containsNumberValue(valueAccountBalance)) {
                                    logger.warn("You have wrong value in AccountBalance");
                                }
                            }
                            String balanceAB = splitAccountBalanceValues[0].trim();
                            String equityAB = splitAccountBalanceValues[1].trim();
                            String marginAB = splitAccountBalanceValues[2].trim();
                            String freeMarginAB = splitAccountBalanceValues[3].trim();

                            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                                 Statement statement = connection.createStatement()) {
                                PreparedStatement fieldAccountBalance = connection.prepareStatement(
                                        "INSERT INTO accountBalance " +
                                                "(accountBalance_date, balance, equity, margin, freeMargin) VALUES " +
                                                "(?,?,?,?,?)");
                                fieldAccountBalance.setString(1, actualDayValue);
                                fieldAccountBalance.setString(2, balanceAB);
                                fieldAccountBalance.setString(3, equityAB);
                                fieldAccountBalance.setString(4, marginAB);
                                fieldAccountBalance.setString(5, freeMarginAB);
                                fieldAccountBalance.execute();

                                logger.info(":) OK :)");
                            }
                        }

                        if (currentLine.contains("Closed Transaction")) {
                            typeAnaliseOperation++;
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }

}

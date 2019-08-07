import org.apache.log4j.Logger;

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
            int closedTransactionsInOneDay = 0;
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

//                        Set values in accountBalance table
                        if (currentLine.contains("Account Balance")) {
                            typeAnaliseOperation++;
                        }

//                        Insert data into accountBalance table
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

//                                Insert values in accountBalance table.
                                PreparedStatement fillAccountBalance = connection.prepareStatement(
                                        "INSERT INTO accountBalance " +
                                                "(accountBalance_date, balance, equity, margin, freeMargin) VALUES " +
                                                "(?,?,?,?,?)");
                                fillAccountBalance.setString(1, actualDayValue);
                                fillAccountBalance.setString(2, balanceAB);
                                fillAccountBalance.setString(3, equityAB);
                                fillAccountBalance.setString(4, marginAB);
                                fillAccountBalance.setString(5, freeMarginAB);
                                fillAccountBalance.execute();

//                                Check result after add new value in DB
                                PreparedStatement checkAddResultInAccountBalance = connection.prepareStatement(
                                        "SELECT count(accountBalance_date) FROM accountBalance " +
                                                "WHERE accountBalance_date LIKE ?;");
                                checkAddResultInAccountBalance.setString(1, actualDayValue);
                                int resultAddAccountBalance = 0;
                                ResultSet resultAddRowInAccountBalance = checkAddResultInAccountBalance.executeQuery();
                                while (resultAddRowInAccountBalance.next()) {
                                    resultAddAccountBalance = resultAddRowInAccountBalance.getInt(
                                            "count(accountBalance_date)");
                                }

//                                Add information in log file about addition process in DB
                                if (resultAddAccountBalance == 1) {
                                    logger.info("Field accountBalance table:\n" +
                                            "\n\tDate = " + actualDayValue + "\n\tBalance = " + balanceAB +
                                            "\n\tEquity = " + equityAB + "\n\tMargin = " + marginAB +
                                            "\n\tFree margin = " + freeMarginAB);
                                } else if (resultAddAccountBalance > 1) {
                                    logger.warn("\n!!! Warning.\nYou have date x" + resultAddAccountBalance +
                                            " in day: " + actualDayValue);
                                } else if (resultAddAccountBalance < 1) {
                                    logger.error("\n!!! Error.\nDate from day " + actualDayValue + " is not save in DataBase");
                                } else {
                                    logger.fatal("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                                            "FATAL ERROR try save date in accountBalance table" +
                                            "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                                }

                            }
                        }

//                        Insert data into accountBalance table
                        if (currentLine.contains("Closed Transaction")) {
                            typeAnaliseOperation++;
                        }
                        if (typeAnaliseOperation == 2 && containsNumberValue(currentLine)) {
                            String[] splitClosetTransactionValues = currentLine.split("\t");

//                            Declaration & initialization closedTransactions values
                            String ticketCT = splitClosetTransactionValues[0].trim();
                            String openTimeTransactionsCT = splitClosetTransactionValues[1].trim();
                            String typeTransactionsCT = splitClosetTransactionValues[2].trim();
                            String lotsCT = splitClosetTransactionValues[3].trim();
                            String symbolCT = splitClosetTransactionValues[4].trim();
                            String exchangeCodeCT = splitClosetTransactionValues[5].trim();
                            String assetsClassCT = splitClosetTransactionValues[6].trim();
                            String openPriceCT = splitClosetTransactionValues[7].trim();
                            String closeTimeCT = splitClosetTransactionValues[8].trim();
                            String closePriceCT = splitClosetTransactionValues[9].trim();
                            String conversionRateCT = splitClosetTransactionValues[10].trim();
                            String commissionsCT = splitClosetTransactionValues[11].trim();
                            String swapCT = splitClosetTransactionValues[12].trim();
                            String profitCT = splitClosetTransactionValues[13].trim();

//                            Insert values in closedTransaction table
                            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                                 Statement statement = connection.createStatement()) {
                                PreparedStatement fillClosedTransactions = connection.prepareStatement(
                                        "INSERT INTO closedTransactions (raportDate, ticket, openTimeTransactions, " +
                                                "typeTransactions, lots, symbol, exchangeCode, assetClass, openPrice, " +
                                                "closeTime, closePrise, conversionRate, commissions, swap, profit) " +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                                fillClosedTransactions.setString(1, actualDayValue);
                                fillClosedTransactions.setString(2, ticketCT);
                                fillClosedTransactions.setString(3, openTimeTransactionsCT);
                                fillClosedTransactions.setString(4, typeTransactionsCT);
                                fillClosedTransactions.setString(5, lotsCT);
                                fillClosedTransactions.setString(6, symbolCT);
                                fillClosedTransactions.setString(7, exchangeCodeCT);
                                fillClosedTransactions.setString(8, assetsClassCT);
                                fillClosedTransactions.setString(9, openPriceCT);
                                fillClosedTransactions.setString(10, closeTimeCT);
                                fillClosedTransactions.setString(11, closePriceCT);
                                fillClosedTransactions.setString(12, conversionRateCT);
                                fillClosedTransactions.setString(13, commissionsCT);
                                fillClosedTransactions.setString(14, swapCT);
                                fillClosedTransactions.setString(15, profitCT);
                                fillClosedTransactions.execute();

                                closedTransactionsInOneDay++;
                                logger.info("Closed transaction is added");
                            }

                        }

                        if (typeAnaliseOperation == 2 && currentLine.contains("Open Transaction")) {
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

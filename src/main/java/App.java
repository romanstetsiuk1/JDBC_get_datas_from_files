import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//        for (File file : filesList) {
//
//            int typeAnaliseOperation = 0;
//            int closedTransactionsInOneDay = 0;
//            int openTransactionsInOneDay = 0;
//            int depositsWithdrawalsInOneDay = 0;
//            String actualDayValue = "";
//
////            Values to add in totalValues table from closed transactions
//            String closedTransactionsCommission = "";
//            String closedTransactionsSwap = "";
//            String closedTransactionsProfit = "";
//            String closedTransactionsClosedTrade = "";
//
////            Values to add in totalValues table from open transactions
//            String openTransactionsCommission = "";
//            String openTransactionsSwap = "";
//            String openTransactionsProfit = "";
//            String openTransactionsFloating = "";
//
//            String depositWithdrawalTotal = "";
//
//            if (file.isFile()) {
//                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
//                    String currentLine;
//
////                        Get Date report and write this information in LogFile
//                    while ((currentLine = bufferedReader.readLine()) != null) {
//                        currentLine.trim();
//
//                        if (currentLine.contains("End of day")) {
//                            logger.info("\n**********************************************************\n" +
//                                    currentLine.trim() +
//                                    "\n**********************************************************\n");
//                            String[] splitEndOfDay = currentLine.trim().split(" ");
//
//                            StringBuilder actualDayReport = new StringBuilder();
//                            for (int i = 3; i < splitEndOfDay.length; i++) {
//                                actualDayReport.append(splitEndOfDay[i] + " ");
//                            }
//                            actualDayValue = actualDayReport.toString().trim();
//                        }
//
////                        Set values in accountBalance table
//                        if (currentLine.contains("Account Balance")) {
//                            typeAnaliseOperation = 1;
//                        }
//
////                        Insert data into accountBalance table
//                        if (typeAnaliseOperation == 1 && containsNumberValue(currentLine)) {
//                            String[] splitAccountBalanceValues = currentLine.trim().split("\t");
//                            for (String valueAccountBalance : splitAccountBalanceValues) {
//                                if (!containsNumberValue(valueAccountBalance)) {
//                                    logger.warn("You have wrong value in AccountBalance");
//                                }
//                            }
//                            String balanceAB = splitAccountBalanceValues[0].trim();
//                            String equityAB = splitAccountBalanceValues[1].trim();
//                            String marginAB = splitAccountBalanceValues[2].trim();
//                            String freeMarginAB = splitAccountBalanceValues[3].trim();
//
//                            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
//                                 Statement statement = connection.createStatement()) {
//
////                                Insert values in accountBalance table.
//                                PreparedStatement fillAccountBalance = connection.prepareStatement(
//                                        "INSERT INTO accountBalance " +
//                                                "(accountBalance_date, balance, equity, margin, freeMargin) VALUES " +
//                                                "(?,?,?,?,?)");
//                                fillAccountBalance.setString(1, actualDayValue);
//                                fillAccountBalance.setString(2, balanceAB);
//                                fillAccountBalance.setString(3, equityAB);
//                                fillAccountBalance.setString(4, marginAB);
//                                fillAccountBalance.setString(5, freeMarginAB);
//                                fillAccountBalance.execute();
//
//                                fillAccountBalance.close();
//
////                                Check result after add new value in DB
//                                PreparedStatement checkAddResultInAccountBalance = connection.prepareStatement(
//                                        "SELECT count(accountBalance_date) FROM accountBalance " +
//                                                "WHERE accountBalance_date LIKE ?;");
//                                checkAddResultInAccountBalance.setString(1, actualDayValue);
//                                int resultAddAccountBalance = 0;
//                                ResultSet resultAddRowInAccountBalance = checkAddResultInAccountBalance.executeQuery();
//                                while (resultAddRowInAccountBalance.next()) {
//                                    resultAddAccountBalance = resultAddRowInAccountBalance.getInt(
//                                            "count(accountBalance_date)");
//                                }
//
////                                Add information in log file about addition process in DB
//                                if (resultAddAccountBalance == 1) {
//                                    logger.info("Field accountBalance table:\n" +
//                                            "\n\tDate = " + actualDayValue + "\n\tBalance = " + balanceAB +
//                                            "\n\tEquity = " + equityAB + "\n\tMargin = " + marginAB +
//                                            "\n\tFree margin = " + freeMarginAB);
//                                } else if (resultAddAccountBalance > 1) {
//                                    logger.warn("\n!!! Warning.\nYou have date x" + resultAddAccountBalance +
//                                            " in day: " + actualDayValue);
//                                } else if (resultAddAccountBalance < 1) {
//                                    logger.error("\n!!! Error.\nDate from day " + actualDayValue + " is not save in DataBase");
//                                } else {
//                                    logger.fatal("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
//                                            "FATAL ERROR try save date in accountBalance table" +
//                                            "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
//                                }
//
//                            }
//                        }
//
////                        Insert data into closedTransactions table
//                        if (currentLine.contains("Closed Transaction")) {
//                            typeAnaliseOperation = 2;
//                        }
//                        if (typeAnaliseOperation == 2 && containsNumberValue(currentLine)) {
//                            String[] splitClosetTransactionValues = currentLine.trim().split("\t");
//
////                            Insert values in closedTransaction table
//                            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
//                                 Statement statement = connection.createStatement()) {
//                                PreparedStatement fillClosedTransactions = connection.prepareStatement(
//                                        "INSERT INTO closedTransactions (raportDate, ticket, openTimeTransactions, " +
//                                                "typeTransactions, lots, symbol, exchangeCode, assetClass, openPrice, " +
//                                                "closeTime, closePrise, conversionRate, commissions, swap, profit) " +
//                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//                                fillClosedTransactions.setString(1, actualDayValue);
//                                fillClosedTransactions.setString(2, splitClosetTransactionValues[0].trim());
//                                fillClosedTransactions.setString(3, splitClosetTransactionValues[1].trim());
//                                fillClosedTransactions.setString(4, splitClosetTransactionValues[2].trim());
//                                fillClosedTransactions.setString(5, splitClosetTransactionValues[3].trim());
//                                fillClosedTransactions.setString(6, splitClosetTransactionValues[4].trim());
//                                fillClosedTransactions.setString(7, splitClosetTransactionValues[5].trim());
//                                fillClosedTransactions.setString(8, splitClosetTransactionValues[6].trim());
//                                fillClosedTransactions.setString(9, splitClosetTransactionValues[7].trim());
//                                fillClosedTransactions.setString(10, splitClosetTransactionValues[8].trim());
//                                fillClosedTransactions.setString(11, splitClosetTransactionValues[9].trim());
//                                fillClosedTransactions.setString(12, splitClosetTransactionValues[10].trim());
//                                fillClosedTransactions.setString(13, splitClosetTransactionValues[11].trim());
//                                fillClosedTransactions.setString(14, splitClosetTransactionValues[12].trim());
//                                fillClosedTransactions.setString(15, splitClosetTransactionValues[13].trim());
//                                fillClosedTransactions.execute();
//
//                                fillClosedTransactions.close();
//
//                                closedTransactionsInOneDay++;
//                                logger.info("Added record nr " + closedTransactionsInOneDay +
//                                        " to the closed transaction table");
//                            }
//
//                        }
//
////                        Get data from Total line in closed Transactions
//                        if (typeAnaliseOperation == 2 && currentLine.contains("Total")) {
//                            typeAnaliseOperation = 3;
//                            String[] splitClosedTransactionTotal = currentLine.trim().split("\t");
//                            closedTransactionsCommission = splitClosedTransactionTotal[11].trim();
//                            closedTransactionsSwap = splitClosedTransactionTotal[12].trim();
//                            closedTransactionsProfit = splitClosedTransactionTotal[13].trim();
//                            System.out.println("commission = " + closedTransactionsCommission + "\nswap = " + closedTransactionsSwap +
//                                    "\nprofit = " + closedTransactionsProfit);
//                        }
//
////                        Get data from Closed Trade line in closed Transactions
//                        if (typeAnaliseOperation == 3 && currentLine.contains("Closed Trade")) {
//                            typeAnaliseOperation = 4;
//                            String[] splitClosedTransactionClosedTrade = currentLine.trim().split("\t");
//                            closedTransactionsClosedTrade = splitClosedTransactionClosedTrade[2].trim();
//                            System.out.println("closed trade = " + closedTransactionsClosedTrade);
//                        }
//
////                        Insert data in OpenTransactions table
//                        if (typeAnaliseOperation == 4 && currentLine.contains("Open Transactions")) {
//                            typeAnaliseOperation = 5;
//                        }
//
//                        if (typeAnaliseOperation == 5 && containsNumberValue(currentLine)) {
//                            String[] splitOpenTransactionsValues = currentLine.trim().split("\t");
//
////                            Insert values in OpenTransactions table
//                            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
//                                 Statement statement = connection.createStatement()) {
//                                PreparedStatement fillOpenTransactions = connection.prepareStatement(
//                                        "INSERT INTO openTransactions (raportDate, ticket, openTimeTransactions, " +
//                                                "typeTransactions, lots, symbol, exchangeCode, assetClass, openPrice, " +
//                                                "marketPrise, conversionRate, commissions, swap, profit) " +
//                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//                                fillOpenTransactions.setString(1, actualDayValue);
//                                fillOpenTransactions.setString(2, splitOpenTransactionsValues[0].trim());
//                                fillOpenTransactions.setString(3, splitOpenTransactionsValues[1].trim());
//                                fillOpenTransactions.setString(4, splitOpenTransactionsValues[2].trim());
//                                fillOpenTransactions.setString(5, splitOpenTransactionsValues[3].trim());
//                                fillOpenTransactions.setString(6, splitOpenTransactionsValues[4].trim());
//                                fillOpenTransactions.setString(7, splitOpenTransactionsValues[5].trim());
//                                fillOpenTransactions.setString(8, splitOpenTransactionsValues[6].trim());
//                                fillOpenTransactions.setString(9, splitOpenTransactionsValues[7].trim());
//                                fillOpenTransactions.setString(10, splitOpenTransactionsValues[8].trim());
//                                fillOpenTransactions.setString(11, splitOpenTransactionsValues[9].trim());
//                                fillOpenTransactions.setString(12, splitOpenTransactionsValues[10].trim());
//                                fillOpenTransactions.setString(13, splitOpenTransactionsValues[11].trim());
//                                fillOpenTransactions.setString(14, splitOpenTransactionsValues[12].trim());
//                                fillOpenTransactions.execute();
//
//                                fillOpenTransactions.close();
//
//                                openTransactionsInOneDay++;
//
//                                logger.info("Add record nr " + openTransactionsInOneDay +
//                                        " to the openTransactions table");
//                            }
//                        }
//
//                        //                        Get data from Total line in closed Transactions
//                        if (typeAnaliseOperation == 5 && currentLine.contains("Total")) {
//                            typeAnaliseOperation = 6;
//                            String[] splitOpenTransactionTotal = currentLine.trim().split("\t");
//                            openTransactionsCommission = splitOpenTransactionTotal[10].trim();
//                            openTransactionsSwap = splitOpenTransactionTotal[11].trim();
//                            openTransactionsProfit = splitOpenTransactionTotal[12].trim();
//                            System.out.println("commission = " + openTransactionsCommission + "\nswap = " + openTransactionsSwap +
//                                    "\nprofit = " + openTransactionsProfit);
//                        }
//
////                        Get data from Closed Trade line in closed Transactions
//                        if (typeAnaliseOperation == 6 && currentLine.contains("Floating")) {
//                            typeAnaliseOperation = 7;
//                            String[] splitOpenTransactionClosedTrade = currentLine.trim().split("\t");
//                            openTransactionsFloating = splitOpenTransactionClosedTrade[1].trim();
//                            System.out.println("closed trade = " + openTransactionsFloating);
//                        }
//
////                        Fill deposits/Withdrawals table
//                        if (typeAnaliseOperation == 7) {
//                            System.out.println("-----------------------" + currentLine);
//                        }
//
//
//                    }
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }

//        Declaration lists to getting data from files
        List<String> dataForAccountBalance = new ArrayList<>();
        List<String> dataForClosedTransactions = new ArrayList<>();
        List<String> dataForDepositsWithdrawals = new ArrayList<>();
        List<String> dataForOpenTransactions = new ArrayList<>();
        List<String> dataForTotalValues = new ArrayList<>();

        int typeAnalise = 0;
        int filesWasAnalise = 0;
        int getAccountBalanceData = 0;
        int getClosedTransactionsData = 0;
        int getDepositsWithdrawalsData = 0;
        int getOpenTransactionsData = 0;
        int getTotalValuesData = 0;

//        get list of report to analise
        for (File file : filesList) {
            String actualDayValue = "";
            String totalClosedTransactions = "";
            String closedTradeClosedTransactions = "";
            String totalOpenTransactions = "";
            String floatingOpenTransactions = "";
            String totalDepositWithdrawal = "";

            if (file.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String currentLine;

//                    Get data from files line by line, analyse, sorted and add to appropriate list. Add note to the log file.
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        currentLine.trim();

//                        get actualDayValue
                        if (currentLine.contains("End of day")) {
                            String[] splitEndOfDay = currentLine.trim().split(" ");
                            StringBuilder actualDayReport = new StringBuilder();
                            for (int i = 3; i < splitEndOfDay.length; i++) {
                                actualDayReport.append(splitEndOfDay[i] + " ");
                            }
                            actualDayValue = actualDayReport.toString().trim();
                        }

//                        get accountBalance data
                        if (currentLine.contains("Account Balance")) {
                            typeAnalise = 1;
                        }
                        if (typeAnalise == 1 && containsNumberValue(currentLine)) {
                            String datewithAccountBalance = actualDayValue + "\t" + currentLine;
                            dataForAccountBalance.add(datewithAccountBalance);
                            getAccountBalanceData++;
                        }

//                        get closedTransactions data
                        if (currentLine.contains("Closed Transactions")) {
                            typeAnalise = 2;
                        }
                        if (typeAnalise == 2 && containsNumberValue(currentLine)) {
                            if (currentLine.contains("Total")) {
                                totalClosedTransactions = currentLine;
                            } else if (currentLine.contains("Closed Trade")) {
                                closedTradeClosedTransactions = currentLine;
                            } else {
                                dataForClosedTransactions.add(currentLine);
                                getClosedTransactionsData++;
                            }
                        }

//                        get openTransactions data
                        if (currentLine.contains("Open Transactions")) {
                            typeAnalise = 3;
                        }
                        if (typeAnalise == 3 && containsNumberValue(currentLine)) {
                            if (currentLine.contains("Total")) {
                                totalOpenTransactions = currentLine;
                            } else if (currentLine.contains("Floating")) {
                                floatingOpenTransactions = currentLine;
                            } else {
                                dataForOpenTransactions.add(currentLine);
                                getOpenTransactionsData++;
                            }
                        }

//                        get Deposits/Withdrawals data
                        if (currentLine.contains("Deposits")) {
                            typeAnalise = 4;
                        }
                        if (typeAnalise == 4) {
                            if (containsNumberValue(currentLine)) {
                                String[] splitDepositData = currentLine.split("\t");
                                if (containsNumberValue(splitDepositData[0].trim()) &&
                                        !currentLine.contains("End of day")) {
                                    dataForDepositsWithdrawals.add(currentLine);
                                    getDepositsWithdrawalsData++;
                                }
                                if (splitDepositData[0].length() < 5) {
                                    totalDepositWithdrawal = splitDepositData[6];
                                }
                            }
                        }
                    }

//                    Collect data for totalValues table
                    String[] splitTotalCT = totalClosedTransactions.split("\t");
                    String[] splitTradeClosedCT = closedTradeClosedTransactions.split("\t");
                    String[] splitTotalOT = totalOpenTransactions.split("\t");
                    String[] splitFloatingOT = floatingOpenTransactions.split("\t");

                    if (splitTotalCT.length == 14 && splitTradeClosedCT.length == 14 &&
                            splitTotalOT.length == 13 && splitFloatingOT.length == 13) {
                        String addToTotalValuesList = actualDayValue + "\t" + splitTotalCT[11].trim() + "\t" +
                                splitTotalCT[12].trim() + "\t" + splitTotalCT[13].trim() + "\t" +
                                splitTradeClosedCT[13].trim() + "\t" +
                                splitTotalOT[10].trim() + "\t" + splitTotalOT[11].trim() + "\t" +
                                splitTotalOT[12].trim() + "\t" +
                                splitFloatingOT[12].trim() + "\t" + totalDepositWithdrawal;
                        dataForTotalValues.add(addToTotalValuesList);
                        getTotalValuesData++;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            filesWasAnalise++;
        }


        System.out.println("----------------------");
        for (String el : dataForTotalValues) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getTotalValuesData);

    }

}

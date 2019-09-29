import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        final Logger logger = Logger.getLogger(App.class);

//        connect with DB MySQL
        String userName = "root";
        String password = "pass";
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
        String filesDoneDirectory = "/home/roman/Roman/tradeDoc/reports/DONE/";

        //        Get list of file in PC directory
        File directory = new File(filesDirectory);
        File[] filesList = directory.listFiles();

//        Declaration lists to getting data from files
        List<String> dataForAccountBalance = new ArrayList<>();
        List<String> dataForClosedTransactions = new ArrayList<>();
        List<String> dataForDepositsWithdrawals = new ArrayList<>();
        List<String> dataForOpenTransactions = new ArrayList<>();
        List<String> dataForTotalValues = new ArrayList<>();

        List<String> checkReportDateExist = new ArrayList<>();

        int filesWasAnalise = 0;
        int moveFileInDoneDirectory = 0;
        int getAccountBalanceData = 0, getClosedTransactionsData = 0, getDepositsWithdrawalsData = 0,
                getOpenTransactionsData = 0, getTotalValuesData = 0;

        int putRecordsToAccountBalance = 0, putRecordsToClosedTransactions = 0, putRecordsToDepositWithdrawals = 0,
                putRecordsToOpenTransactions = 0, putRecordsToTotalValues = 0;

        String actualDayValue = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String addToDataBase = formatter.format(date);

//        get list of report to analise
        for (File file : filesList) {
            int typeAnalise = 0;

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

//                        Check if report do not analise before
                        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                             Statement statement = connection.createStatement()) {
                            PreparedStatement getExistData = connection.prepareStatement("SELECT " +
                                    "accountBalance_date FROM accountBalance;");
                            ResultSet resultSet = getExistData.executeQuery();
                            while (resultSet.next()) {
                                checkReportDateExist.add(resultSet.getString("accountBalance_date"));
                            }
                        }
                        if (checkReportDateExist.contains(actualDayValue)) {
                            typeAnalise = 5;
                        }

//                        get accountBalance data
                        if (currentLine.contains("Account Balance")) {
                            typeAnalise = 1;
                        }
                        if (typeAnalise == 1 && DataAnalise.containsNumberValue(currentLine)) {
                            String datewithAccountBalance = actualDayValue + "\t" + currentLine;
                            dataForAccountBalance.add(datewithAccountBalance);
                            getAccountBalanceData++;
                        }

//                        get closedTransactions data
                        if (currentLine.contains("Closed Transactions")) {
                            typeAnalise = 2;
                        }
                        if (typeAnalise == 2 && DataAnalise.containsNumberValue(currentLine)) {
                            if (currentLine.contains("Total")) {
                                totalClosedTransactions = currentLine;
                            } else if (currentLine.contains("Closed Trade")) {
                                closedTradeClosedTransactions = currentLine;
                            } else {
                                dataForClosedTransactions.add(currentLine);
                                getClosedTransactionsData++;
                            }
                        }
//                        get line when closedTransaction no have values
                        if (typeAnalise == 2 && currentLine.contains("Total") &&
                                !DataAnalise.containsNumberValue(currentLine)) {
                            totalClosedTransactions = currentLine;

                        }
                        if (typeAnalise == 2 && currentLine.contains("Closed Trade") &&
                                !DataAnalise.containsNumberValue(currentLine)) {
                            closedTradeClosedTransactions = currentLine;
                        }

//                        get openTransactions data
                        if (currentLine.contains("Open Transactions")) {
                            typeAnalise = 3;
                        }
                        if (typeAnalise == 3 && DataAnalise.containsNumberValue(currentLine)) {
                            if (currentLine.contains("Total")) {
                                totalOpenTransactions = currentLine;
                            } else if (currentLine.contains("Floating")) {
                                floatingOpenTransactions = currentLine;
                            } else {
                                dataForOpenTransactions.add(currentLine);
                                getOpenTransactionsData++;
                            }
                        }
//                        get line when closedTransaction no have values
                        if (typeAnalise == 3 && currentLine.contains("Total") &&
                                !DataAnalise.containsNumberValue(currentLine)) {
                            totalOpenTransactions = currentLine;

                        }
                        if (typeAnalise == 3 && currentLine.contains("Floating") &&
                                !DataAnalise.containsNumberValue(currentLine)) {
                            closedTradeClosedTransactions = currentLine;
                        }

//                        get Deposits/Withdrawals data
                        if (currentLine.contains("Deposits")) {
                            typeAnalise = 4;
                        }
                        if (typeAnalise == 4) {
                            if (DataAnalise.containsNumberValue(currentLine)) {
                                String[] splitDepositData = currentLine.split("\t");
                                if (DataAnalise.containsNumberValue(splitDepositData[0].trim()) &&
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

//                    Get data for totalValues List if closedTransactions && openTransactions table is not empty
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
//                    Get data for totalValues List if closedTransactions is empty
                    if (splitTotalCT.length == 11 && splitTradeClosedCT.length == 13 &&
                            splitTotalOT.length == 13 && splitFloatingOT.length == 13) {
                        String addToTotalValuesList = actualDayValue + "\t" + splitTotalCT[8].trim() + "\t" +
                                splitTotalCT[9].trim() + "\t" + splitTotalCT[10].trim() + "\t" +
                                splitTradeClosedCT[12].trim() + "\t" +
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

                if (typeAnalise == 5) {
                    logger.warn("!!! You have data from report in end of day: " + actualDayValue);
                }

//                Move file in the DONE Directory
                try {
                    file.renameTo(new File(filesDoneDirectory + "DONE_" + file.getName()));
                    moveFileInDoneDirectory++;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                filesWasAnalise++;
            }
        }

        logger.info("\n===========================================================================================\n" +
                "Upload reports " + formatter.format(date) +
                "\n\nYou open for get data: " + filesWasAnalise + " files\n" +
                "You move to the DONE Directory " + moveFileInDoneDirectory + " files\n\n" +
                "You get " + getAccountBalanceData + " lines for fill accountBalance table\n" +
                "You get " + getClosedTransactionsData + " lines for fill closedTransactions table\n" +
                "You get " + getDepositsWithdrawalsData + " lines for fill depositsWithdrawals table\n" +
                "You get " + getOpenTransactionsData + " lines for fill openTransactions table\n" +
                "You get " + getTotalValuesData + " lines for fill totalValues table\n" +
                "------------------------------------------------------------------------------------------\n");

//        Put data from getAccountBalanceData list into accountBalance table in data base
        for (String accountBalanceData : dataForAccountBalance) {
            String[] splitAccountBalanceData = accountBalanceData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillAccountBalanceTable = connection.prepareStatement("INSERT INTO " +
                        "accountBalance (accountBalance_date, balance, equity, margin, freeMargin, addToDataBase) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
                DataAnalise.fillAccountBalance(splitAccountBalanceData, fillAccountBalanceTable,
                        1, 0, 2, splitAccountBalanceData[1], 3, splitAccountBalanceData[2], 4,
                        splitAccountBalanceData[3], 5, splitAccountBalanceData[4], 6, addToDataBase);

                putRecordsToAccountBalance++;
            } catch (Exception e) {
                logger.error("!!! You have Exception when you try add data in accountBalance table. Line nr " +
                        putRecordsToAccountBalance);
            }
        }
        if (getAccountBalanceData != putRecordsToAccountBalance) {
            logger.warn("!!!!! Warning You add not all(or to much) data in MySQL!!!!!");
        }
        logger.info("\nYou add " + putRecordsToAccountBalance + " records to the accountBalance table in MySQL;\n");

//        Put data from dataForClosedTransactions list into closedTransactions table in data base
        for (String closedTransactionData : dataForClosedTransactions) {
            String[] splitClosedTransactionTable = closedTransactionData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillClosedTransactionsTable = connection.prepareStatement("INSERT INTO " +
                        "closedTransactions (raportDate, ticket, openTimeTransactions, typeTransactions, lots, " +
                        "symbol, exchangeCode, assetClass, openPrice, closeTime, closePrise, conversionRate, " +
                        "commissions, swap, profit, addToDataBase) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                DataAnalise.fillClosedTransactions(actualDayValue, splitClosedTransactionTable,
                        fillClosedTransactionsTable, addToDataBase);

                putRecordsToClosedTransactions++;
            } catch (Exception e) {
                logger.error("!!! You have Exception when you try add data in closedTransactions table. Line nr " +
                        putRecordsToClosedTransactions);
            }
        }
        if (getClosedTransactionsData != putRecordsToClosedTransactions) {
            logger.warn("!!!!! Warning You add not all(or to much) data in MySQL!!!!!");
        }
        logger.info("\nYou add " + putRecordsToClosedTransactions +
                " records to the closedTransactions table in MySQL;\n");

//        Put data from dataForDepositsWithdrawals list into depositsWithdrawals table in data base
        for (String depositsWithdrawalsData : dataForDepositsWithdrawals) {
            String[] splitDepositsWithdrawalsData = depositsWithdrawalsData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillDepositsWithdrawalsTable = connection.prepareStatement("INSERT INTO " +
                        "depositsWithdrawals (raportDate, ticket, openTime, typeOperation, comment, deposit, " +
                        "withdraw, netDeposit, addToDataBase) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                DataAnalise.fillDepositsWithdrawals(actualDayValue, fillDepositsWithdrawalsTable, 1, 2,
                        splitDepositsWithdrawalsData[0], 3, splitDepositsWithdrawalsData[1], 4,
                        splitDepositsWithdrawalsData[2], 5, splitDepositsWithdrawalsData[3], 6,
                        splitDepositsWithdrawalsData[4], 7, splitDepositsWithdrawalsData[5], 8,
                        splitDepositsWithdrawalsData[6], 9, addToDataBase);

                putRecordsToDepositWithdrawals++;
            } catch (Exception e) {
                logger.error("!!! You have Exception when you try add data in depositsWithdrawals table. Line nr " +
                        putRecordsToDepositWithdrawals);
            }
        }
        if (getDepositsWithdrawalsData != putRecordsToDepositWithdrawals) {
            logger.warn("!!!!! Warning You add not all(or to much) data in MySQL!!!!!");
        }
        logger.info("\nYou add " + putRecordsToDepositWithdrawals +
                " records to the depositsWithdrawals table in MySQL;\n");

//        Put data from dataForOpenTransactions list into openTransactions table in data base
        for (String openTransactionsData : dataForOpenTransactions) {
            String[] splitOpenTransactionsData = openTransactionsData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillOpenTransactionsTable = connection.prepareStatement("INSERT INTO " +
                        "openTransactions (raportDate, ticket, openTimeTransactions, typeTransactions, lots, " +
                        "symbol, exchangeCode, assetClass, openPrice, marketPrise, conversionRate, commissions, " +
                        "swap, profit, addToDataBase) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                DataAnalise.fillOpenTransactions(actualDayValue, splitOpenTransactionsData,
                        fillOpenTransactionsTable, 1, 2, 0, 3, 4, 5, 6, 7, 8,
                        9, 10, 11, 12, 13, 14, 15, addToDataBase);

                putRecordsToOpenTransactions++;
            } catch (Exception e) {
                logger.error("!!! You have Exception when you try add data in openTransactions table. Line nr " +
                        putRecordsToOpenTransactions);
            }
        }
        if (getOpenTransactionsData != putRecordsToOpenTransactions) {
            logger.warn("!!!!! Warning You add not all(or to much) data in MySQL!!!!!");
        }
        logger.info("\nYou add " + putRecordsToOpenTransactions +
                " records to the openTransactions table in MySQL;\n");

//        Put data from dataForTotalValues list into totalValue table in data base
        for (String totalValuesData : dataForTotalValues) {
            String[] splitTotalValuesData = totalValuesData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillTotalValuesTable = connection.prepareStatement("INSERT INTO " +
                        "totalValues (raportDate, closedTransactions_commission, closedTransactions_swap, " +
                        "closedTransactions_profit, closedTransactions_closedTrade, openTransactions_commission, " +
                        "openTransactions_swap, openTransactions_profit, openTransactions_floating, " +
                        "depositWithdrawal, addToDataBase) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                DataAnalise.fillTotalValues(splitTotalValuesData, fillTotalValuesTable, addToDataBase);

                putRecordsToTotalValues++;
            } catch (Exception e) {
                logger.error("!!! You have Exception when you try add data in totalValues table. Line nr " +
                        putRecordsToTotalValues);
            }
        }
        logger.info("\nYou add " + putRecordsToTotalValues +
                " records to the totalValues table in MySQL;\n" +
                "\n===========================================================================================\n\n\n");


    }

}

package ver_2_0;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetDatasApp {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        final Logger logger = Logger.getLogger(GetDatasApp.class);

//        connect with DB MySQL
        String userName = "root";
        String password = "pass";
        String connectionUrl = "jdbc:mysql://localhost/trading?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
             Statement statement = connection.createStatement()) {

            CreateMySqlSchemas createMySqlSchemas = new CreateMySqlSchemas();
            createMySqlSchemas.createAccountBalanceSchema(statement);
            createMySqlSchemas.createClosedTransactionsSchema(statement);
            createMySqlSchemas.createCloseTransactionsTotalSchema(statement);
            createMySqlSchemas.createOpenTransactionsSchema(statement);
            createMySqlSchemas.createOpenTransactionsTotalSchema(statement);
            createMySqlSchemas.createDepositsWithdrawalsSchema(statement);

        }

        String filesDirectory = "/home/roman/Roman/tradeDoc/reports";
        String filesDoneDirectory = "/home/roman/Roman/tradeDoc/reports/DONE/";

        int filesWasAnalise = 0;
        int moveFileInDoneDirectory = 0;

        String actualDayValue = "";

//        Declaration lists with load data from files
        List<String> loadDataForAccountBalance = new ArrayList<>();
        List<String> loadDataForClosedTransactions = new ArrayList<>();
        List<String> loadDataForTotalClosedTransactions = new ArrayList<>();
        List<String> loadDataForOpenTransactions = new ArrayList<>();
        List<String> loadDataForTotalOpenTransactions = new ArrayList<>();
        List<String> loadDataForDepositsWithdrawals = new ArrayList<>();

        List<String> checkReportDateExist = new ArrayList<>();

        int loadAccountBalanceLines = 0, addAccountBalanceLines = 0,
                loadClosedTransactionsLines = 0, addClosedTransactionsLines = 0,
                loadTotalClosedTransactionsLines = 0, addTotalClosedTransactionsLines = 0,
                loadOpenTranactionsLines = 0, addOpenTransactionsLines = 0,
                loadTotalOpenTransactionsLines = 0, addTotalOpenTransactionsLines = 0,
                loadDepositsWithdrawalsLines = 0, addDepositsWithdrawalsLines = 0;

        File directory = new File(filesDirectory);
        File[] filesList = directory.listFiles();

//        get list of report to analise
        for (File file : filesList) {
            int typeAnalise = 0;
            String addToTotalCT = "";
            String addToTotalOpTr = "";
            String addToDepositsWithdrawals = "";

            if (file.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String currentLine;

//Get data from files line by line, analyse, sorted and add to appropriate list. Add note to the log file.
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        currentLine.trim();

//                        get actualDayValue
                        if (currentLine.contains("End of day")) {
                            actualDayValue = AnaliseData.getActualDayValue(actualDayValue, currentLine);
                            actualDayValue = AnaliseData.convertDataToMySqlFormat(actualDayValue);
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
                            typeAnalise = 8;
                        }

//                        typeAnalise = 1 -- in this state I get data for accountBalance schema
                        if (currentLine.contains("Account Balance")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 1 && AnaliseData.containsNumberValue(currentLine)) {
                            String addDataToAccountBalance = actualDayValue + "\t" + currentLine;
                            loadDataForAccountBalance.add(addDataToAccountBalance);
                            loadAccountBalanceLines++;
                        }

//                        typeAnalise = 2 -- in this state I get data for closedTransactions schema
                        if (currentLine.contains("Closed Transaction")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 2 && AnaliseData.containsNumberValue(currentLine)
                                && !currentLine.contains("Total")) {
                            String addDataToClosedTransaction = actualDayValue + "\t" + currentLine;
                            loadDataForClosedTransactions.add(addDataToClosedTransaction);
                            loadClosedTransactionsLines++;
                        }

//                        typeAnalise = 3 -- in this state I get data for totalClosedTransactions schema
                        if (typeAnalise == 2 && currentLine.contains("Total")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 3) {
                            if (currentLine.contains("Total") && AnaliseData.containsNumberValue(currentLine)) {
                                addToTotalCT += actualDayValue + "\t" + currentLine;
                            }
                            if (currentLine.contains("Closed Trade") && AnaliseData.containsNumberValue(currentLine)) {
                                addToTotalCT += currentLine;
                                loadDataForTotalClosedTransactions.add(addToTotalCT);
                                loadTotalClosedTransactionsLines++;
                            }
                        }

//                        typeAnalise = 4 -- in this state I get data for openTransactions schema
                        if (typeAnalise == 3 && currentLine.contains("Open Transactions")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 4 && AnaliseData.containsNumberValue(currentLine)
                                && !currentLine.contains("Total")) {
                            String addDataToOpenTransaction = actualDayValue + "\t" + currentLine;
                            loadDataForOpenTransactions.add(addDataToOpenTransaction);
                            loadOpenTranactionsLines++;
                        }

//                        typeAnalise = 5 -- in this state I get data for totalOpenTransactions
                        if (typeAnalise == 4 && currentLine.contains("Total")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 5) {
                            if (currentLine.contains("Total") && AnaliseData.containsNumberValue(currentLine)) {
                                addToTotalOpTr += actualDayValue + "\t" + currentLine;
                            }
                            if (currentLine.contains("Floating") && AnaliseData.containsNumberValue(currentLine)) {
                                addToTotalOpTr += currentLine;
                                loadDataForTotalOpenTransactions.add(addToTotalOpTr);
                                loadTotalOpenTransactionsLines++;
                            }
                        }

//                        typeAnalise = 6 -- in this state I get data for depositsWithdrawals schema
                        if (typeAnalise == 5 && currentLine.contains("Deposits/Withdrawals")) {
                            typeAnalise++;
                        }
                        if (typeAnalise == 6 && AnaliseData.containsNumberValue(currentLine) &&
                                !currentLine.contains("Deposit/Withdrawal")) {

                            addToDepositsWithdrawals += actualDayValue + "\t" + currentLine;
                            loadDataForDepositsWithdrawals.add(addToDepositsWithdrawals);
                            addToDepositsWithdrawals = "";
                            loadDepositsWithdrawalsLines++;
                        }
                        if (typeAnalise == 6 && currentLine.contains("Deposit/Withdrawal")) {
                            typeAnalise++;
                        }

//                        typeAnalise = 7 -- end of analise in this report
                        if (typeAnalise == 7) {
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filesWasAnalise++;
//                typeAnalise = 8 -- report of this day was analised
                if (typeAnalise == 8) {
                    logger.warn("!!! You have data from report in end of day: " + actualDayValue);
                }

//                Move file in the DONE Directory
                try {
                    file.renameTo(new File(filesDoneDirectory + "DONE_" + file.getName()));
                    moveFileInDoneDirectory++;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            logger.info("End of day " + actualDayValue + ": You get\n\t"
                    + loadAccountBalanceLines + " lines for accountBalance schema\n\t"
                    + loadClosedTransactionsLines + " lines for closedTransactions schema\n\t"
                    + loadTotalClosedTransactionsLines + " lines for totalClosedTransactions schema\n\t"
                    + loadOpenTranactionsLines + " lines for openTransactions schema\n\t"
                    + loadTotalOpenTransactionsLines + " lines for totalOpenTransactions schema\n\t"
                    + loadDepositsWithdrawalsLines + " lines for depositsWithdrawals schema\n");

            loadAccountBalanceLines = 0;
            loadClosedTransactionsLines = 0;
            loadTotalClosedTransactionsLines = 0;
            loadOpenTranactionsLines = 0;
            loadTotalOpenTransactionsLines = 0;
            loadDepositsWithdrawalsLines = 0;
        }
        logger.info("You analise " + filesWasAnalise + " files");

//        put records from list into accountBalance schema
        for (String accountBalanceData : loadDataForAccountBalance) {
            String[] splitAccountBalanceData = accountBalanceData.trim().split("\t");
            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillAccountBalanceSchema = connection.prepareStatement("INSERT INTO " +
                        "accountBalance (accountBalance_date, balance, equity, margin, freeMargin) " +
                        "VALUES (?, ?, ?, ?, ?)");
                AnaliseData.fillAccountBalance(splitAccountBalanceData, fillAccountBalanceSchema);
                addAccountBalanceLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL ACCOUNTBALANCE SCHEMA-----");
            }
        }

//        put records from list into closedTransactions schema
        for (String closedTransactionsData : loadDataForClosedTransactions) {
            String[] splitClosedTransactionsData = closedTransactionsData.trim().split("\t");

            String openDate = AnaliseData.convertDate(splitClosedTransactionsData[2]);
            String openTime = AnaliseData.convertTime(splitClosedTransactionsData[2]);
            String closeDate = AnaliseData.convertDate(splitClosedTransactionsData[9]);
            String closeTransactionTime = AnaliseData.convertTime(splitClosedTransactionsData[9]);

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillClosedTransactionSchema = connection.prepareStatement("INSERT INTO " +
                        "closedTransactions (raportDate, ticket, openTimeTransactions, typeTransactions, lots, " +
                        "symbol, exchangeCode, assetClass, openPrice, closeTime, closePrise, conversionRate, " +
                        "commissions, swap, profit, openDate, openTime, closeDate, closeTransactionTime) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                AnaliseData.fillClosedTransactions(splitClosedTransactionsData, openDate, openTime, closeDate, closeTransactionTime, fillClosedTransactionSchema);
                addClosedTransactionsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL CLOSEDTRANSACTION SCHEMA-----");
            }
        }

//        put records from list into totalClosedTransactions schema
        for (String totalClosedTransactionsData : loadDataForTotalClosedTransactions) {
            String[] splitTotalClosedTransactionsData = totalClosedTransactionsData.trim().split("\t");
            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillTotalClosedTransactionsSchema = connection.prepareStatement(
                        "INSERT INTO totalClosedTransactions (raportDate, commission, swap, profit, closedTrade) " +
                                "VALUES (?, ?, ?, ?, ?)");
                AnaliseData.fillTotalClosedTransactions(splitTotalClosedTransactionsData, fillTotalClosedTransactionsSchema);
                addTotalClosedTransactionsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL TOTALCLOSEDTRANSACTION SCHEMA-----");
            }
        }

//        put records from list into OpenTransactions schema
        for (String openTransactionsData : loadDataForOpenTransactions) {
            String[] splitOpenTransactionData = openTransactionsData.trim().split("\t");

            String openDate = AnaliseData.convertDate(splitOpenTransactionData[2]);
            String openTime = AnaliseData.convertTime(splitOpenTransactionData[2]);

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillOpenTransactionsSchema = connection.prepareStatement("INSERT INTO " +
                        "openTransactions (raportDate, ticket, openTimeTransactions, openDate, openTime, " +
                        "typeTransactions, lots, symbol, exchangeCode, assetClass, openPrice, marketPrise, " +
                        "conversionRate, commissions, swap, profit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                        "?, ?, ?, ?, ?) ");
                AnaliseData.fillOpenTransactionsSchema(splitOpenTransactionData, openDate, openTime, fillOpenTransactionsSchema);
                addOpenTransactionsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL OPENTRANSACTION SCHEMA-----");
            }
        }

//        put records from list into totalOpenTransactions schema
        for (String totalOpenTransactionsData : loadDataForTotalOpenTransactions) {
            String[] splitTotalOpenTranactionsData = totalOpenTransactionsData.trim().split("\t");

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillTotalOpenTransactionsSchema = connection.prepareStatement("INSERT " +
                        "INTO totalOpenTransactions (raportDate, commission, swap, profit, floating) " +
                        "VALUES (?, ?, ?, ?, ?)");
                AnaliseData.fillTotalOpenTransactions(splitTotalOpenTranactionsData, fillTotalOpenTransactionsSchema);
                addTotalOpenTransactionsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL TOTALOPENTRANSACTION SCHEMA-----");
            }

        }

//        put records from list into depositsWithdrawals schema
        for (String depositsWithdrawalsData : loadDataForDepositsWithdrawals) {
            String[] splitDepositsWithdrawalsData = depositsWithdrawalsData.trim().split("\t");

            String openData = AnaliseData.convertDate(splitDepositsWithdrawalsData[2]);
            String openTime = AnaliseData.convertTime(splitDepositsWithdrawalsData[2]);

            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillDepositsWithdrawalsSchema = connection.prepareStatement("INSERT INTO " +
                        "depositsWithdrawals (raportDate, ticket, openDateTime, openDate, openTime, typeOperation, " +
                        "comment, deposit, withdraw, netDeposit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                AnaliseData.fillDepositsWithrawals(splitDepositsWithdrawalsData, openData, openTime, fillDepositsWithdrawalsSchema);
                addDepositsWithdrawalsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL DEPOSITSWITHDRAWALS SCHEMA-----");
            }
        }

        logger.info("\nYou have " + loadDataForAccountBalance.size() + " lines for accountBalance schema.\n" +
                "You have " + loadDataForClosedTransactions.size() + " lines for closedTransactions schema.\n" +
                "You have " + loadDataForTotalClosedTransactions.size() + " lines for totalClosedTransactions schema.\n" +
                "You have " + loadDataForOpenTransactions.size() + " lines for openTransactions schema.\n" +
                "You have " + loadDataForTotalOpenTransactions.size() + " lines for totalOpenTransactions schema.\n" +
                "You have " + loadDataForDepositsWithdrawals.size() + " lines for depositsWithdrawals schema.\n");

        logger.info("\nYou add " + addAccountBalanceLines + " lines to accountBalance schema\n" +
                "You add " + addClosedTransactionsLines + " lines to closedTransactions schema\n" +
                "You add " + addTotalClosedTransactionsLines + " lines to totalClosedTransactions schema\n" +
                "You add " + addOpenTransactionsLines + " lines to openTransactions schema\n" +
                "You add " + addTotalOpenTransactionsLines + " lines to totalOpenTransactions schema\n" +
                "You add " + addDepositsWithdrawalsLines + " lines to depositsWithdrawals schema\n");

        logger.info(moveFileInDoneDirectory + " files was moved in Done directory\n\n" +
                "--------------------------------------------------------------------------------------------\n");


    }


}
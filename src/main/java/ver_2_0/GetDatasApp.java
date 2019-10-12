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

        int loadAccountBalanceLines = 0, addAccountBalanceLines = 0,
                loadClosedTransactionsLines = 0, addClosedTransactionsLines = 0;

        File directory = new File(filesDirectory);
        File[] filesList = directory.listFiles();

//        get list of report to analise
        for (File file : filesList) {
            int typeAnalise = 0;

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
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filesWasAnalise++;
            }
            logger.info("End of day " + actualDayValue + ": You get\n\t"
                    + loadAccountBalanceLines + " lines for accountBalance schema\n"
                    + loadClosedTransactionsLines + " lines for closedTransactions schema\n");

            loadAccountBalanceLines = 0;
            loadClosedTransactionsLines = 0;
        }
        logger.info("You analise " + filesWasAnalise + " files");

//        put records from list into accountBalance schema
        for (String accountBalanceDatas : loadDataForAccountBalance) {
            String[] splitAccountBalanceData = accountBalanceDatas.trim().split("\t");
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
        for (String closedTransactionsDatas : loadDataForClosedTransactions) {
            String[] splitClosedTransactionsData = closedTransactionsDatas.trim().split("\t");
            try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement fillClosedTransactionSchema = connection.prepareStatement("INSERT INTO " +
                        "closedTransactions (raportDate, ticket, openTimeTransactions, typeTransactions, lots, " +
                        "symbol, exchangeCode, assetClass, openPrice, closeTime, closePrise, conversionRate, " +
                        "commissions, swap, profit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                AnaliseData.fillClosedTransactions(splitClosedTransactionsData, fillClosedTransactionSchema);
                addClosedTransactionsLines++;
            } catch (Exception e) {
                logger.error("-----UPS. ERROR IN FILL CLOSEDTRANSACTION SCHEMA-----");
            }
        }

        logger.info("\nYou have " + loadDataForAccountBalance.size() + " lines for accountBalance schema.\n" +
                "You have " + loadDataForClosedTransactions.size() + " lines for closedTransactions schema.\n");

        logger.info("\nYou add " + addAccountBalanceLines + " lines to accountBalance schema\n" +
                "You add " + addClosedTransactionsLines + " lines to closedTransactions schema");


    }


}

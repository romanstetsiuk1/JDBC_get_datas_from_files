import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        int filesWasAnalise = 0;
        int moveFileInDoneDirectory = 0;
        int getAccountBalanceData = 0;
        int getClosedTransactionsData = 0;
        int getDepositsWithdrawalsData = 0;
        int getOpenTransactionsData = 0;
        int getTotalValuesData = 0;

        int putRecordsToAccountBalance = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

//        get list of report to analise
        for (File file : filesList) {
            int typeAnalise = 0;

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
                        "accountBalance (accountBalance_date, balance, equity, margin, freeMargin) " +
                        "VALUES (?, ?, ?, ?, ?)");
                fillAccountBalanceTable.setString(1, splitAccountBalanceData[0]);
                fillAccountBalanceTable.setString(2, splitAccountBalanceData[1]);
                fillAccountBalanceTable.setString(3, splitAccountBalanceData[2]);
                fillAccountBalanceTable.setString(4, splitAccountBalanceData[3]);
                fillAccountBalanceTable.setString(5, splitAccountBalanceData[4]);
                fillAccountBalanceTable.execute();
                fillAccountBalanceTable.close();
            }
        }

    }

}

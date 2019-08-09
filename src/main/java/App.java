import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
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
            }
            filesWasAnalise++;
        }


        System.out.println("----------------------");
        for (String el : dataForAccountBalance) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getAccountBalanceData);

        System.out.println("----------------------");
        for (String el : dataForClosedTransactions) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getClosedTransactionsData);

        System.out.println("----------------------");
        for (String el : dataForDepositsWithdrawals) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getDepositsWithdrawalsData);

        System.out.println("----------------------");
        for (String el : dataForOpenTransactions) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getOpenTransactionsData);

        System.out.println("----------------------");
        for (String el : dataForTotalValues) {
            System.out.println(el);
        }
        System.out.println("**********************");
        System.out.println("Total = " + getTotalValuesData);

        System.out.println("Move files: " + moveFileInDoneDirectory);
    }

}

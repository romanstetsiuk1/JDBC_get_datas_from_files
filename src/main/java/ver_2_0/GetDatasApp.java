package ver_2_0;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

        int loadAccountBalanceLines = 0;

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

//                        typeAnalise = 2 -- in thi state I get data for closedTransactions schema
                        if (currentLine.contains("Closed Transaction")) {
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
            logger.info("End of day " + actualDayValue + ": You get " + loadAccountBalanceLines +
                    " lines for accountBalance schema");

            loadAccountBalanceLines = 0;
        }
        logger.info("You analise " + filesWasAnalise + " files");
    }


}

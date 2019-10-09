package ver_2_0;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

        File directory = new File(filesDirectory);
        File[] filesList = directory.listFiles();

//        get list of report to analise
        for (File file : filesList) {

            if (file.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String currentLine;

//Get data from files line by line, analyse, sorted and add to appropriate list. Add note to the log file.
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        currentLine.trim();
//                        System.out.println(currentLine);

//                        get actualDayValue
                        if (currentLine.contains("End of day")) {
                            String[] splitEndOfDay = currentLine.trim().split(" ");
                            StringBuilder actualDayReport = new StringBuilder();
                            for (int i = 3; i < splitEndOfDay.length; i++) {
                                if (i == 5 && splitEndOfDay[i].length() == 1) {
                                    String tmpString = splitEndOfDay[i];
                                    splitEndOfDay[i] = "0" + tmpString;
                                }
                                actualDayReport.append(splitEndOfDay[i] + " ");
                            }
                            actualDayValue = actualDayReport.toString().trim()
                                    .replace(" ", "-");
                            actualDayValue = AnaliseData.convertDataToMySqlFormat(actualDayValue);

                        }
                        System.out.println(actualDayValue);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filesWasAnalise++;
            }
        }
        logger.info("You analise " + filesWasAnalise + " files");
    }
}

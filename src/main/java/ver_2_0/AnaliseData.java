package ver_2_0;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AnaliseData {

    public static String convertDataToMySqlFormat(String toConvert) {
        if (toConvert.contains("January")) {
            return toConvert.replace("January", "01");
        }
        if (toConvert.contains("February")) {
            return toConvert.replace("February", "02");
        }
        if (toConvert.contains("March")) {
            return toConvert.replace("March", "03");
        }
        if (toConvert.contains("April")) {
            return toConvert.replace("April", "04");
        }
        if (toConvert.contains("May")) {
            return toConvert.replace("May", "05");
        }
        if (toConvert.contains("June")) {
            return toConvert.replace("June", "06");
        }
        if (toConvert.contains("July")) {
            return toConvert.replace("July", "07");
        }
        if (toConvert.contains("August")) {
            return toConvert.replace("August", "08");
        }
        if (toConvert.contains("September")) {
            return toConvert.replace("September", "09");
        }
        if (toConvert.contains("October")) {
            return toConvert.replace("October", "10");
        }
        if (toConvert.contains("November")) {
            return toConvert.replace("November", "11");
        }
        if (toConvert.contains("December")) {
            return toConvert.replace("December", "12");
        }
        return "";
    }

    public static String getActualDayValue(String actualDayValue, String currentLine) {
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
        return actualDayValue;
    }

    public static boolean containsNumberValue(String line) {
        String[] numberValue = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String checkLine : numberValue) {
            if (line.contains(checkLine)) {
                return true;
            }
        }
        return false;
    }

//    Fill DB:
    public static void fillAccountBalance(String[] splitAccountBalanceData,
                                          PreparedStatement fillAccountBalanceSchema) throws SQLException {
        fillAccountBalanceSchema.setString(1, splitAccountBalanceData[0].trim());
        fillAccountBalanceSchema.setString(2, splitAccountBalanceData[1].trim());
        fillAccountBalanceSchema.setString(3, splitAccountBalanceData[2].trim());
        fillAccountBalanceSchema.setString(4, splitAccountBalanceData[3].trim());
        fillAccountBalanceSchema.setString(5, splitAccountBalanceData[4].trim());
        fillAccountBalanceSchema.execute();
        fillAccountBalanceSchema.close();
    }

    public static void fillClosedTransactions
            (String[] splitClosedTransactionsData, PreparedStatement fillClosedTransactionSchema)
            throws SQLException {
        fillClosedTransactionSchema.setString(1, splitClosedTransactionsData[0]);
        fillClosedTransactionSchema.setString(2, splitClosedTransactionsData[1]);
        fillClosedTransactionSchema.setString(3, splitClosedTransactionsData[2]);
        fillClosedTransactionSchema.setString(4, splitClosedTransactionsData[3]);
        fillClosedTransactionSchema.setString(5, splitClosedTransactionsData[4]);
        fillClosedTransactionSchema.setString(6, splitClosedTransactionsData[5]);
        fillClosedTransactionSchema.setString(7, splitClosedTransactionsData[6]);
        fillClosedTransactionSchema.setString(8, splitClosedTransactionsData[7]);
        fillClosedTransactionSchema.setString(9, splitClosedTransactionsData[8]);
        fillClosedTransactionSchema.setString(10, splitClosedTransactionsData[9]);
        fillClosedTransactionSchema.setString(11, splitClosedTransactionsData[10]);
        fillClosedTransactionSchema.setString(12, splitClosedTransactionsData[11]);
        fillClosedTransactionSchema.setString(13, splitClosedTransactionsData[12]);
        fillClosedTransactionSchema.setString(14, splitClosedTransactionsData[13]);
        fillClosedTransactionSchema.setString(15, splitClosedTransactionsData[14]);
        fillClosedTransactionSchema.execute();
        fillClosedTransactionSchema.close();
    }

}

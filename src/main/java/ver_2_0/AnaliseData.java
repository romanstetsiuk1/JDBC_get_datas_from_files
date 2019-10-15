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

    public static String convertDate(String inputDate) {
        String[] splitDateTime = inputDate.split(" ");
        String[] convertDate = splitDateTime[0].split("/");
        String result = convertDate[2] + "-" + convertDate[1] + "-" + convertDate[0];
        return result;
    }

    public static String convertTime(String inputDate) {
        String[] splitDateTime = inputDate.split(" ");
        return splitDateTime[1];
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


    public static void fillClosedTransactions(String[] splitClosedTransactionsData, String openDate, String openTime, String closeDate, String closeTransactionTime, PreparedStatement fillClosedTransactionSchema) throws SQLException {
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
        fillClosedTransactionSchema.setString(16, openDate);
        fillClosedTransactionSchema.setString(17, openTime);
        fillClosedTransactionSchema.setString(18, closeDate);
        fillClosedTransactionSchema.setString(19, closeTransactionTime);
        fillClosedTransactionSchema.execute();
        fillClosedTransactionSchema.close();
    }

    public static void fillTotalClosedTransactions(String[] splitTotalClosedTransactionsData, PreparedStatement fillTotalClosedTransactionsSchema) throws SQLException {
        fillTotalClosedTransactionsSchema.setString(1, splitTotalClosedTransactionsData[0]);
        fillTotalClosedTransactionsSchema.setString(2, splitTotalClosedTransactionsData[12]);
        fillTotalClosedTransactionsSchema.setString(3, splitTotalClosedTransactionsData[13]);
        fillTotalClosedTransactionsSchema.setString(4, splitTotalClosedTransactionsData[14]);
        fillTotalClosedTransactionsSchema.setString(5, splitTotalClosedTransactionsData[27]);
        fillTotalClosedTransactionsSchema.execute();
        fillTotalClosedTransactionsSchema.close();
    }

    public static void fillOpenTransactionsSchema(String[] splitOpenTransactionData, String openDate, String openTime, PreparedStatement fillOpenTransactionsSchema) throws SQLException {
        fillOpenTransactionsSchema.setString(1, splitOpenTransactionData[0]);
        fillOpenTransactionsSchema.setString(2, splitOpenTransactionData[1]);
        fillOpenTransactionsSchema.setString(3, splitOpenTransactionData[2]);
        fillOpenTransactionsSchema.setString(4, openDate);
        fillOpenTransactionsSchema.setString(5, openTime);
        fillOpenTransactionsSchema.setString(6, splitOpenTransactionData[3]);
        fillOpenTransactionsSchema.setString(7, splitOpenTransactionData[4]);
        fillOpenTransactionsSchema.setString(8, splitOpenTransactionData[5]);
        fillOpenTransactionsSchema.setString(9, splitOpenTransactionData[6]);
        fillOpenTransactionsSchema.setString(10, splitOpenTransactionData[7]);
        fillOpenTransactionsSchema.setString(11, splitOpenTransactionData[8]);
        fillOpenTransactionsSchema.setString(12, splitOpenTransactionData[9]);
        fillOpenTransactionsSchema.setString(13, splitOpenTransactionData[10]);
        fillOpenTransactionsSchema.setString(14, splitOpenTransactionData[11]);
        fillOpenTransactionsSchema.setString(15, splitOpenTransactionData[12]);
        fillOpenTransactionsSchema.setString(16, splitOpenTransactionData[13]);
        fillOpenTransactionsSchema.execute();
        fillOpenTransactionsSchema.close();
    }

    public static void fillTotalOpenTransactions(String[] splitTotalOpenTranactionsData, PreparedStatement fillTotalOpenTransactionsSchema) throws SQLException {
        fillTotalOpenTransactionsSchema.setString(1, splitTotalOpenTranactionsData[0]);
        fillTotalOpenTransactionsSchema.setString(2, splitTotalOpenTranactionsData[11]);
        fillTotalOpenTransactionsSchema.setString(3, splitTotalOpenTranactionsData[12]);
        fillTotalOpenTransactionsSchema.setString(4, splitTotalOpenTranactionsData[13]);
        fillTotalOpenTransactionsSchema.setString(5, splitTotalOpenTranactionsData[25]);
        fillTotalOpenTransactionsSchema.execute();
        fillTotalOpenTransactionsSchema.close();
    }

}

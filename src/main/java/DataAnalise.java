import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataAnalise {
    protected static boolean containsNumberValue(String line) {
        String[] numbersValue = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String checkLine : numbersValue) {
            if (line.contains(checkLine)) {
                return true;
            }
        }
        return false;
    }


    protected static void fillTotalValues(String[] splitTotalValuesData, PreparedStatement fillTotalValuesTable,
                                          String addToDataBase)
            throws SQLException {
        fillTotalValuesTable.setString(1, splitTotalValuesData[0]);
        fillTotalValuesTable.setString(2, splitTotalValuesData[1]);
        fillTotalValuesTable.setString(3, splitTotalValuesData[2]);
        fillTotalValuesTable.setString(4, splitTotalValuesData[3]);
        fillTotalValuesTable.setString(5, splitTotalValuesData[4]);
        fillTotalValuesTable.setString(6, splitTotalValuesData[5]);
        fillTotalValuesTable.setString(7, splitTotalValuesData[6]);
        fillTotalValuesTable.setString(8, splitTotalValuesData[7]);
        fillTotalValuesTable.setString(9, splitTotalValuesData[8]);
        if (splitTotalValuesData.length == 10) {
            fillTotalValuesTable.setString(10, splitTotalValuesData[9]);
        }
        if (splitTotalValuesData.length == 9) {
            fillTotalValuesTable.setString(10, null);
        }
        fillTotalValuesTable.setString(11, addToDataBase);
        fillTotalValuesTable.execute();
        fillTotalValuesTable.close();
    }

    protected static void fillOpenTransactions(String actualDayValue, String[] splitOpenTransactionsData,
                                               PreparedStatement fillOpenTransactionsTable, int i2, int i3, int i4,
                                               int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12,
                                               int i13, int i14, int i15, int i16, int i17, String addToDataBase)
            throws SQLException {
        fillOpenTransactionsTable.setString(i2, actualDayValue);
        fillOpenTransactionsTable.setString(i3, splitOpenTransactionsData[i4].trim());
        fillOpenTransactionsTable.setString(i5, splitOpenTransactionsData[i2].trim());
        fillOpenTransactionsTable.setString(i6, splitOpenTransactionsData[i3].trim());
        fillOpenTransactionsTable.setString(i7, splitOpenTransactionsData[i5].trim());
        fillOpenTransactionsTable.setString(i8, splitOpenTransactionsData[i6].trim());
        fillOpenTransactionsTable.setString(i9, splitOpenTransactionsData[i7].trim());
        fillOpenTransactionsTable.setString(i10, splitOpenTransactionsData[i8].trim());
        fillOpenTransactionsTable.setString(i11, splitOpenTransactionsData[i9].trim());
        fillOpenTransactionsTable.setString(i12, splitOpenTransactionsData[i10].trim());
        fillOpenTransactionsTable.setString(i13, splitOpenTransactionsData[i11].trim());
        fillOpenTransactionsTable.setString(i14, splitOpenTransactionsData[i12].trim());
        fillOpenTransactionsTable.setString(i15, splitOpenTransactionsData[i13].trim());
        fillOpenTransactionsTable.setString(i16, splitOpenTransactionsData[i14].trim());
        fillOpenTransactionsTable.setString(i17, addToDataBase.trim());
        fillOpenTransactionsTable.execute();
        fillOpenTransactionsTable.close();
    }

    protected static void fillDepositsWithdrawals(String actualDayValue, PreparedStatement
            fillDepositsWithdrawalsTable, int i2, int i3, String splitDepositsWithdrawalsDatum,
                                                  int i4, String splitDepositsWithdrawalsDatum2,
                                                  int i5, String splitDepositsWithdrawalsDatum3,
                                                  int i6, String splitDepositsWithdrawalsDatum4,
                                                  int i7, String splitDepositsWithdrawalsDatum5,
                                                  int i8, String splitDepositsWithdrawalsDatum6,
                                                  int i9, String splitDepositsWithdrawalsDatum7,
                                                  int i10, String addToDataBase) throws SQLException {
        fillDepositsWithdrawalsTable.setString(i2, actualDayValue);
        fillDepositsWithdrawalsTable.setString(i3, splitDepositsWithdrawalsDatum);
        fillDepositsWithdrawalsTable.setString(i4, splitDepositsWithdrawalsDatum2);
        fillDepositsWithdrawalsTable.setString(i5, splitDepositsWithdrawalsDatum3);
        fillDepositsWithdrawalsTable.setString(i6, splitDepositsWithdrawalsDatum4);
        fillDepositsWithdrawalsTable.setString(i7, splitDepositsWithdrawalsDatum5);
        fillDepositsWithdrawalsTable.setString(i8, splitDepositsWithdrawalsDatum6);
        fillDepositsWithdrawalsTable.setString(i9, splitDepositsWithdrawalsDatum7);
        fillDepositsWithdrawalsTable.setString(i10, addToDataBase);
        fillDepositsWithdrawalsTable.execute();
        fillDepositsWithdrawalsTable.close();
    }

    protected static void fillClosedTransactions(String actualDayValue, String[] splitClosedTransactionTable,
                                                 PreparedStatement fillClosedTransactionsTable, String addToDataBase)
            throws SQLException {
        fillClosedTransactionsTable.setString(1, actualDayValue);
        fillClosedTransactionsTable.setString(2, splitClosedTransactionTable[0].trim());
        fillClosedTransactionsTable.setString(3, splitClosedTransactionTable[1].trim());
        fillClosedTransactionsTable.setString(4, splitClosedTransactionTable[2].trim());
        fillClosedTransactionsTable.setString(5, splitClosedTransactionTable[3].trim());
        fillClosedTransactionsTable.setString(6, splitClosedTransactionTable[4].trim());
        fillClosedTransactionsTable.setString(7, splitClosedTransactionTable[5].trim());
        fillClosedTransactionsTable.setString(8, splitClosedTransactionTable[6].trim());
        fillClosedTransactionsTable.setString(9, splitClosedTransactionTable[7].trim());
        fillClosedTransactionsTable.setString(10, splitClosedTransactionTable[8].trim());
        fillClosedTransactionsTable.setString(11, splitClosedTransactionTable[9].trim());
        fillClosedTransactionsTable.setString(12, splitClosedTransactionTable[10].trim());
        fillClosedTransactionsTable.setString(13, splitClosedTransactionTable[11].trim());
        fillClosedTransactionsTable.setString(14, splitClosedTransactionTable[12].trim());
        fillClosedTransactionsTable.setString(15, splitClosedTransactionTable[13].trim());
        fillClosedTransactionsTable.setString(16, addToDataBase.trim());
        fillClosedTransactionsTable.execute();
        fillClosedTransactionsTable.close();
    }

    protected static void fillAccountBalance(String[] splitAccountBalanceData, PreparedStatement
            fillAccountBalanceTable, int i2, int i3, int i4, String splitAccountBalanceDatum,
                                             int i5, String splitAccountBalanceDatum2,
                                             int i6, String splitAccountBalanceDatum3,
                                             int i7, String splitAccountBalanceDatum4,
                                             int i8, String addToDataBase) throws SQLException {
        fillAccountBalanceTable.setString(i2, splitAccountBalanceData[i3].trim());
        fillAccountBalanceTable.setString(i4, splitAccountBalanceDatum.trim());
        fillAccountBalanceTable.setString(i5, splitAccountBalanceDatum2.trim());
        fillAccountBalanceTable.setString(i6, splitAccountBalanceDatum3.trim());
        fillAccountBalanceTable.setString(i7, splitAccountBalanceDatum4.trim());
        fillAccountBalanceTable.setString(i8, addToDataBase.trim());
        fillAccountBalanceTable.execute();
        fillAccountBalanceTable.close();
    }

}

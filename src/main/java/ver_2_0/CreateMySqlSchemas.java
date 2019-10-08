package ver_2_0;

import java.sql.SQLException;
import java.sql.Statement;

public class CreateMySqlSchemas {

    void createAccountBalanceSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS accountBalance(" +
                "accountBalance_id INT NOT NULL AUTO_INCREMENT," +
                "accountBalance_date DATE," +
                "balance VARCHAR(12)," +
                "equity VARCHAR(12)," +
                "margin VARCHAR(12)," +
                "freeMargin VARCHAR(12)," +
                "PRIMARY KEY (accountBalance_id))");
    }

    void createClosedTransactionsSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS closedTransactions(" +
                "closedTransaction_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate DATE," +
                "ticket VARCHAR(15)," +
                "openTimeTransactions DATETIME, " +
                "typeTransactions VARCHAR(5)," +
                "lots VARCHAR(5)," +
                "symbol VARCHAR(10)," +
                "exchangeCode VARCHAR(5)," +
                "assetClass VARCHAR(20)," +
                "openPrice VARCHAR(10)," +
                "closeTime DATETIME," +
                "closePrise VARCHAR(10)," +
                "conversionRate VARCHAR(15)," +
                "commissions VARCHAR(10)," +
                "swap VARCHAR(10)," +
                "profit VARCHAR(10)," +
                "PRIMARY  KEY (closedTransaction_id))");
    }

    void createCloseTransactionsTotalSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS totalClosedTransactions(" +
                "totalClosedTransaction_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate DATE," +
                "commission VARCHAR(10)," +
                "swap VARCHAR(10)," +
                "profit VARCHAR(10)," +
                "closedTrade VARCHAR(10), " +
                "PRIMARY KEY (totalClosedTransaction_id))");
    }

    void createOpenTransactionsSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS openTransactions(" +
                "openTransactions_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate DATE," +
                "ticket VARCHAR(15)," +
                "openTimeTransactions DATETIME," +
                "typeTransactions VARCHAR(5)," +
                "lots VARCHAR(5)," +
                "symbol VARCHAR(10)," +
                "exchangeCode VARCHAR(5)," +
                "assetClass VARCHAR(20)," +
                "openPrice VARCHAR(10)," +
                "marketPrise VARCHAR(10)," +
                "conversionRate VARCHAR(15)," +
                "commissions VARCHAR(10)," +
                "swap VARCHAR(10)," +
                "profit VARCHAR(10)," +
                "PRIMARY  KEY (openTransactions_id))");
    }

    void createOpenTransactionsTotalSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS totalOpenTransactions(" +
                "totalOpenTransaction_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate DATE," +
                "commission VARCHAR(10)," +
                "swap VARCHAR(10)," +
                "profit VARCHAR(10)," +
                "floating VARCHAR(10), " +
                "PRIMARY KEY (totalOpenTransaction_id))");
    }

    void createDepositsWithdrawalsSchema(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE  IF NOT EXISTS depositsWithdrawals(" +
                "depositsWithdrawals_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate DATE," +
                "ticket VARCHAR(12)," +
                "openTime DATETIME, " +
                "typeOperation VARCHAR(15)," +
                "comment VARCHAR(150)," +
                "deposit VARCHAR(10)," +
                "withdraw VARCHAR(10)," +
                "netDeposit VARCHAR(10)," +
                "PRIMARY KEY (depositsWithdrawals_id))");

    }

}

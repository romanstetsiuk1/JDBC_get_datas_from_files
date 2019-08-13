import Interfaces.CreateTableInterface;

import java.sql.SQLException;
import java.sql.Statement;

public class CreteTableInMySql implements CreateTableInterface {

    void createTableTotalValues(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE  IF NOT EXISTS totalValues(" +
                "totalValues_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate VARCHAR(12)," +
                "closedTransactions_commission VARCHAR(10)," +
                "closedTransactions_swap VARCHAR(10)," +
                "closedTransactions_profit VARCHAR(10)," +
                "closedTransactions_closedTrade VARCHAR(10)," +
                "openTransactions_commission VARCHAR(10)," +
                "openTransactions_swap VARCHAR(10)," +
                "openTransactions_profit VARCHAR(10)," +
                "openTransactions_floating VARCHAR(10)," +
                "depositWithdrawal VARCHAR(10)," +
                "addToDataBase VARCHAR(25)," +
                "PRIMARY KEY (totalValues_id))");

    }

    void createTableDepositsWithdrawals(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE  IF NOT EXISTS depositsWithdrawals(" +
                "depositsWithdrawals_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate VARCHAR(12)," +
                "ticket VARCHAR(12)," +
                "openTime VARCHAR(25)," +
                "typeOperation VARCHAR(15)," +
                "comment VARCHAR(150)," +
                "deposit VARCHAR(10)," +
                "withdraw VARCHAR(10)," +
                "netDeposit VARCHAR(10)," +
                "addToDataBase VARCHAR(25)," +
                "PRIMARY KEY (depositsWithdrawals_id))");

    }

    void createTableOpenTransactions(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS openTransactions(" +
                "openTransactions_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate VARCHAR(12)," +
                "ticket VARCHAR(15)," +
                "openTimeTransactions VARCHAR(25)," +
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
                "addToDataBase VARCHAR(25)," +
                "PRIMARY  KEY (openTransactions_id))");

    }

    void createTableClosedTransactions(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS closedTransactions(" +
                "closedTransaction_id INT NOT NULL AUTO_INCREMENT," +
                "raportDate VARCHAR(12)," +
                "ticket VARCHAR(15)," +
                "openTimeTransactions VARCHAR(25)," +
                "typeTransactions VARCHAR(5)," +
                "lots VARCHAR(5)," +
                "symbol VARCHAR(10)," +
                "exchangeCode VARCHAR(5)," +
                "assetClass VARCHAR(20)," +
                "openPrice VARCHAR(10)," +
                "closeTime VARCHAR(25)," +
                "closePrise VARCHAR(10)," +
                "conversionRate VARCHAR(15)," +
                "commissions VARCHAR(10)," +
                "swap VARCHAR(10)," +
                "profit VARCHAR(10)," +
                "addToDataBase VARCHAR(25)," +
                "PRIMARY  KEY (closedTransaction_id))");

    }


    void createTableAccountBalance(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS accountBalance(" +
                "accountBalance_id INT NOT NULL AUTO_INCREMENT," +
                "accountBalance_date VARCHAR(12)," +
                "balance VARCHAR(12)," +
                "equity VARCHAR(12)," +
                "margin VARCHAR(12)," +
                "freeMargin VARCHAR(12)," +
                "addToDataBase VARCHAR(25)," +
                "PRIMARY KEY (accountBalance_id))");
    }

}

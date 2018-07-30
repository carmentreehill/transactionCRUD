package com.project.application.dao;

import com.project.application.model.Transaction;
import com.project.application.model.TransactionSum;
import com.project.application.utils.TransactionUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionDAO {

    private static final String CREATE_TRANSACTION = "INSERT INTO `Transaction`(id,amount,description,date,user_id) values(?,?,?,?,?);";
    private static final String LIST_TRANSACTION_BY_USER_AND_TRANSACTION = "SELECT * FROM `Transaction` where id=? and user_id= ?";
    private static final String LIST_TRANSACTIONS_BY_USER = "SELECT * FROM `Transaction` where user_id= ? ORDER BY date(date)";
    private static final String SUM_TRANSACTIONS_BY_USER = " SELECT user_id, SUM(amount) as sum FROM `Transaction` where user_id=? group by user_id";

    private Connection connect() {
        String url = "jdbc:sqlite:transaction.db";
        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Optional<Transaction> create(Transaction transaction) {

        UUID uuid = transaction.getId() == null ? UUID.randomUUID() : transaction.getId();
        transaction.setId(uuid);

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(CREATE_TRANSACTION)) {

            statement.setString(1, String.valueOf(transaction.getId()));
            statement.setDouble(2, transaction.getAmount());
            statement.setString(3, transaction.getDescription());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = dateFormat.format(transaction.getDate());
            statement.setString(4, dateString);
            statement.setLong(5, transaction.getUserId());

            int resultExecution = statement.executeUpdate();
            return resultExecution > 0 ? Optional.of(transaction) : Optional.empty();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<Transaction>> getTransactionByTransactionIdOrUser(Transaction transaction) {
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(LIST_TRANSACTION_BY_USER_AND_TRANSACTION)) {

            if (Objects.nonNull(transaction.getId())) {
                statement.setString(1, String.valueOf(transaction.getId()));
            }

            if (Objects.nonNull(transaction.getUserId())) {
                statement.setLong(2, transaction.getUserId());
            }

            ResultSet resultExecution = statement.executeQuery();
            List<Transaction> transactions = mapResulsetToTransaction(resultExecution);

            return transactions.size() == 1 ? Optional.of(transactions) : Optional.empty();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<Transaction>> getTransactionsByUserId(Long userId) {
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(LIST_TRANSACTIONS_BY_USER)) {

            statement.setLong(1, userId);

            ResultSet resultExecution = statement.executeQuery();
            List<Transaction> transactions = mapResulsetToTransaction(resultExecution);

            return transactions.size() > 0 ? Optional.of(transactions) : Optional.empty();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TransactionSum> sumTransactionByUserId(Long userId) {
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(SUM_TRANSACTIONS_BY_USER)) {

            statement.setLong(1, userId);

            ResultSet resultExecution = statement.executeQuery();
            List<TransactionSum> transactions = mapResulsetToTransactionSum(resultExecution);

            return transactions.size() == 1 ? Optional.of(transactions.get(0)) : Optional.empty();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    private List<Transaction> mapResulsetToTransaction(ResultSet resultSet) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction;

        while(resultSet.next()) {
            transaction = new Transaction();
            transaction.setId(UUID.fromString(resultSet.getString("id")));
            String dateString = resultSet.getString("date");
            Date date = TransactionUtils.convertStringToDate(dateString);
            transaction.setDate(date);
            transaction.setUserId(resultSet.getLong("user_id"));
            transaction.setAmount(resultSet.getDouble("amount"));
            transaction.setDescription(resultSet.getString("description"));
            transactions.add(transaction);
        }
        return transactions;
    }

    private List<TransactionSum> mapResulsetToTransactionSum(ResultSet resultSet) throws SQLException {
        List<TransactionSum> transactions = new ArrayList<>();
        TransactionSum transaction = new TransactionSum();

        while(resultSet.next()) {
            transaction.setUserId(resultSet.getLong("user_id"));
            transaction.setSum(resultSet.getDouble("sum"));
            transactions.add(transaction);
        }
        return transactions;
    }
}

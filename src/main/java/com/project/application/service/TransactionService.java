package com.project.application.service;

import com.project.application.dao.TransactionDAO;
import com.project.application.model.Transaction;
import com.project.application.model.TransactionSum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private TransactionDAO transactionDAO;

    public TransactionService(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }

    public Optional<Transaction> create(Transaction transaction) {
        return transactionDAO.create(transaction);
    }

    public Optional<List<Transaction>> getTransactionByTransactionIdAndUser(Transaction transaction) {
        return transactionDAO.getTransactionByTransactionIdOrUser(transaction);
    }

    public Optional<List<Transaction>> getTransactionByUser(Transaction transaction) {
        return transactionDAO.getTransactionsByUserId(transaction.getUserId());
    }

    public Optional<TransactionSum> sumTransactionByUserId(Long userId) {
        return transactionDAO.sumTransactionByUserId(userId);
    }
}

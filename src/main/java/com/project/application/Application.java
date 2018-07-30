package com.project.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.application.model.Transaction;
import com.project.application.model.TransactionSum;
import com.project.application.service.TransactionService;
import com.project.application.utils.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(Application.class);
    private TransactionService transactionService;


    public Application(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        if (args.length > 1) {
            String operador = args[1].trim().toUpperCase();
            ObjectMapper mapper = new ObjectMapper();

            switch (operador) {

                case "ADD":
                    Transaction transaction = TransactionUtils.convertJsonToTransaction(args[2]);
                    Long userId = Long.valueOf(args[0]);
                    transaction.setUserId(userId);
                    Optional<Transaction> transactionCreated = transactionService.create(transaction);
                    transactionCreated.ifPresent(objectCreated -> {
                        try {
                            logger.info(mapper.writeValueAsString(objectCreated));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                    break;

                case "LIST":
                    userId = Long.valueOf(args[0]);
                    Transaction transactionDetails = new Transaction();
                    transactionDetails.setUserId(userId);

                    List<Transaction> transactionByUser =
                            transactionService.getTransactionByUser(transactionDetails).orElse(null);

                    try {
                        logger.info(Objects.nonNull(transactionByUser) ?
                                mapper.writeValueAsString(transactionByUser) : "[]");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case "SUM":
                    userId = Long.valueOf(args[0]);
                    TransactionSum sumAmounts =
                            transactionService.sumTransactionByUserId(userId).orElse(null);

                    try {
                        logger.info(Objects.nonNull(sumAmounts) ?
                                mapper.writeValueAsString(sumAmounts) : "{\"user_id\":"+ userId +",\"sum\":0}");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    String transactionId = args[0];
                    userId = Long.valueOf(args[1]);
                    transactionDetails = new Transaction();
                    transactionDetails.setId(UUID.fromString(transactionId));
                    transactionDetails.setUserId(userId);

                    List<Transaction> transactionByIdAndUser =
                            transactionService.getTransactionByTransactionIdAndUser(transactionDetails).orElse(null);

                    try {
                        logger.info(Objects.nonNull(transactionByIdAndUser) ?
                                mapper.writeValueAsString(transactionByIdAndUser.get(0)) : "Transaction not found");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    break;
            }

        } else {
            logger.error("Please, add data");
        }
        System.exit(0);
    }

}

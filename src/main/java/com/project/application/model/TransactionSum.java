package com.project.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionSum {

    private Long userId;

    private Double sum;

    @JsonProperty("user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @JsonProperty("sum")
    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}

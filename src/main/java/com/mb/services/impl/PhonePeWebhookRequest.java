package com.mb.services.impl;

import com.mb.controller.PhonePePaymentController;

import lombok.Data;

public record PhonePeWebhookRequest(String transactionId, String merchantTransactionId, String status, String amount) {

}

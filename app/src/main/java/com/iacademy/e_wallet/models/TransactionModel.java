package com.iacademy.e_wallet.models;

public class TransactionModel {

    //sender information
    private double amountSent;
    private String senderName, senderNumber;

    //receiver information
    private double amountReceived;
    private String receiverName, receiverNumber;

    //transaction history
    private String timeAndDate;
    private double senderNewBalance;
    private double receiverNewBalance;

    //type of transaction
    private String transactionType;


    //constructor
    public TransactionModel(){};

    //1. SEND MONEY
    public TransactionModel(double amountSent, String receiverName, String receiverNumber, String timeAndDate, double senderNewBalance) {
        //you have sent PHP AMOUNTSENT to RECEIVERNAME (RECEIVERNUMBER) on DATAANDTIME.
        //your new balance is PHP SENDERBALANCE
        this.amountSent = amountSent;
        this.receiverName = receiverName;
        this.receiverNumber = receiverNumber;
        this.timeAndDate = timeAndDate;
        this.senderNewBalance = senderNewBalance;
    }

    //2. RECEIVE MONEY
    public TransactionModel(String senderName, String senderNumber, String timeAndDate, double receiverNewBalance, double amountReceived) {
        //you have received PHP AMOUNTSENT from SENDERNAME (SENDERNUMBER) on DATAANDTIME.
        //your new balance is PHP RECEIVERBALANCE
        this.senderName = senderName;
        this.senderNumber = senderNumber;
        this.timeAndDate = timeAndDate;
        this.senderNewBalance = receiverNewBalance;
        this.amountReceived = amountReceived;
    }

    //3. DEPOSIT MONEY
    public TransactionModel(double amountSent, String timeAndDate, double senderNewBalance) {
        //you have deposited PHP AMOUNTSENT on DATAANDTIME.
        //your new balance is PHP SENDERBALANCE
        this.amountSent = amountSent;
        this.timeAndDate = timeAndDate;
        this.senderNewBalance = senderNewBalance;
    }


    //setters and getters
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderNumber() { return senderNumber; }
    public void setSenderNumber(String senderNumber) { this.senderNumber = senderNumber; }

    public double getAmountSent() { return amountSent; }
    public void setAmountSent(double amountSent) { this.amountSent = amountSent; }

    public double getAmountReceived() { return amountReceived; }
    public void setAmountReceived(double amountReceived) { this.amountReceived = amountReceived; }

    public String getTimeAndDate() { return timeAndDate; }
    public void setTimeAndDate(String timeAndDate) { this.timeAndDate = timeAndDate; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverNumber() { return receiverNumber; }
    public void setReceiverNumber(String receiverNumber) { this.receiverNumber = receiverNumber; }

    public double getSenderNewBalance() { return senderNewBalance; }
    public void setSenderNewBalance(double senderNewBalance) { this.senderNewBalance = senderNewBalance; }

    public double getReceiverNewBalance() { return receiverNewBalance; }
    public void setReceiverNewBalance(double receiverNewBalance) { this.receiverNewBalance = receiverNewBalance; }

    public String getTransactionType() { return transactionType; }

    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }


}

package model;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private int bookID;
    private int userID;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fineAmount;

    // Book object for easy reporting (optional, used by DAO)
    private Book book;

    public Transaction() {}

    public Transaction(int transactionID, int bookID, int userID,
                       LocalDate borrowDate, LocalDate returnDate, double fineAmount) {
        this.transactionID = transactionID;
        this.bookID = bookID;
        this.userID = userID;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    // Getters & Setters
    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public int getBookID() { return bookID; }
    public void setBookID(int bookID) { this.bookID = bookID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionID + ", bookID=" + bookID +
               ", userID=" + userID + ", borrowDate=" + borrowDate +
               ", returnDate=" + (returnDate != null ? returnDate : "Not returned") +
               ", fine=" + fineAmount + "}";
    }
}

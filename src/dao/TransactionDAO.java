package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.Transaction;


public class TransactionDAO {

    // Borrow book method (already implemented)
    public static boolean borrowBook(int bookID, int userID) {
        String sql = "INSERT INTO transactions(bookID, userID, borrowDate) VALUES(?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return book method
    public static boolean returnBook(int bookID, int userID) {
        String selectSql = "SELECT borrowDate FROM transactions WHERE bookID = ? AND userID = ? AND returnDate IS NULL";
        String updateSql = "UPDATE transactions SET returnDate = CURDATE(), fineAmount = ? " +
                           "WHERE bookID = ? AND userID = ? AND returnDate IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, bookID);
            selectStmt.setInt(2, userID);

            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                LocalDate returnDate = LocalDate.now();

                // Calculate fine (e.g., 5 days allowed, 10 per extra day)
                long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate);
                int allowedDays = 5;
                double fine = 0;
                if (daysBorrowed > allowedDays) {
                    fine = (daysBorrowed - allowedDays) * 10; // 10 currency units per extra day
                }

                // Update transaction with return date and fine
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, fine);
                    updateStmt.setInt(2, bookID);
                    updateStmt.setInt(3, userID);
                    int rowsUpdated = updateStmt.executeUpdate();
                    return rowsUpdated > 0;
                }

            } else {
                System.out.println("No active borrow record found for this book/user.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //calculate fine
    public static double calculateFine(int bookID, int userID) {
        String sql = "SELECT borrowDate FROM transactions WHERE bookID = ? AND userID = ? AND returnDate IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            stmt.setInt(2, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                LocalDate today = LocalDate.now();

                long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, today);
                int allowedDays = 5;       // number of days allowed without fine
                double finePerDay = 10.0;  // fine per extra day

                if (daysBorrowed > allowedDays) {
                    return (daysBorrowed - allowedDays) * finePerDay;
                } else {
                    return 0.0;
                }
            } else {
                System.out.println("No active borrow record found for this book/user.");
                return 0.0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    //get borrowed books
    public static List<Book> getBorrowedBooks(int userID) {
        List<Book> borrowedBooks = new ArrayList<>();
        String sql = "SELECT b.id, b.title, b.author, b.category " +
                     "FROM books b " +
                     "JOIN transactions t ON b.id = t.bookID " +
                     "WHERE t.userID = ? AND t.returnDate IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                borrowedBooks.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return borrowedBooks;
    }
    //get transaction history
    public static List<Transaction> getTransactionHistory(int userID) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.id AS transactionID, t.bookID, t.userID, t.borrowDate, t.returnDate, t.fineAmount, " +
                     "b.title, b.author, b.category " +
                     "FROM transactions t " +
                     "JOIN books b ON t.bookID = b.id " +
                     "WHERE t.userID = ? " +
                     "ORDER BY t.borrowDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction tx = new Transaction();
                tx.setTransactionID(rs.getInt("transactionID"));
                tx.setBookID(rs.getInt("bookID"));
                tx.setUserID(rs.getInt("userID"));
                tx.setBorrowDate(rs.getDate("borrowDate").toLocalDate());
                if (rs.getDate("returnDate") != null) {
                    tx.setReturnDate(rs.getDate("returnDate").toLocalDate());
                }
                tx.setFineAmount(rs.getDouble("fineAmount"));

                // Optional: store book details in transaction for easier reporting
                Book book = new Book();
                book.setId(rs.getInt("bookID"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                tx.setBook(book);

                transactions.add(tx);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }
//get overdue transactions(borrowed>allowed days and not return)
public static List<Transaction> getOverdueTransactions() {
    List<Transaction> overdueTransactions = new ArrayList<>();
    String sql = "SELECT t.id AS transactionID, t.bookID, t.userID, t.borrowDate, b.title, b.author, b.category " +
                 "FROM transactions t " +
                 "JOIN books b ON t.bookID = b.id " +
                 "WHERE t.returnDate IS NULL";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
            LocalDate today = LocalDate.now();
            long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, today);

            int allowedDays = 5; // allowed days without fine
            double finePerDay = 10.0; // fine per extra day

            if (daysBorrowed > allowedDays) {
                Transaction tx = new Transaction();
                tx.setTransactionID(rs.getInt("transactionID"));
                tx.setBookID(rs.getInt("bookID"));
                tx.setUserID(rs.getInt("userID"));
                tx.setBorrowDate(borrowDate);

                // Store book details
                Book book = new Book();
                book.setId(rs.getInt("bookID"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                tx.setBook(book);

                // Fine calculation
                tx.setFineAmount((daysBorrowed - allowedDays) * finePerDay);
                overdueTransactions.add(tx);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return overdueTransactions;
}


}

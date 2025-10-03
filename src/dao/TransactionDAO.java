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

    // Borrow book
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

    // Return book
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

                long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate);
                int allowedDays = 5;
                double fine = 0;
                if (daysBorrowed > allowedDays) {
                    fine = (daysBorrowed - allowedDays) * 10;
                }

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

    // Calculate fine without returning
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
                int allowedDays = 5;
                double finePerDay = 10.0;

                return (daysBorrowed > allowedDays) ? (daysBorrowed - allowedDays) * finePerDay : 0.0;
            } else {
                System.out.println("No active borrow record found for this book/user.");
                return 0.0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Get borrowed books for a user
    public static List<Book> getBorrowedBooks(int userID) {
        List<Book> borrowedBooks = new ArrayList<>();
        String sql = "SELECT b.bookID, b.title, b.author, b.category " +
                     "FROM books b " +
                     "JOIN transactions t ON b.bookID = t.bookID " +
                     "WHERE t.userID = ? AND t.returnDate IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("bookID"));
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

    // Get transaction history for a user
    public static List<Transaction> getTransactionHistory(int userID) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.transactionID, t.bookID, t.userID, t.borrowDate, t.returnDate, t.fineAmount, " +
                     "b.title, b.author, b.category " +
                     "FROM transactions t " +
                     "JOIN books b ON t.bookID = b.bookID " +
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

    // Get overdue transactions (books not returned after allowed days)
    public static List<Transaction> getOverdueTransactions() {
        List<Transaction> overdueTransactions = new ArrayList<>();
        String sql = "SELECT t.transactionID, t.bookID, t.userID, t.borrowDate, b.title, b.author, b.category " +
                     "FROM transactions t " +
                     "JOIN books b ON t.bookID = b.bookID " +
                     "WHERE t.returnDate IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate borrowDate = rs.getDate("borrowDate").toLocalDate();
                LocalDate today = LocalDate.now();
                long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, today);

                int allowedDays = 5;
                double finePerDay = 10.0;

                if (daysBorrowed > allowedDays) {
                    Transaction tx = new Transaction();
                    tx.setTransactionID(rs.getInt("transactionID"));
                    tx.setBookID(rs.getInt("bookID"));
                    tx.setUserID(rs.getInt("userID"));
                    tx.setBorrowDate(borrowDate);

                    Book book = new Book();
                    book.setId(rs.getInt("bookID"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setCategory(rs.getString("category"));
                    tx.setBook(book);

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

package controller;
import java.util.List;

public class LibrarySystem {

    // Register a new user
    public String registerUser(String name, String email, String password, String role) {
        // Optional: You can implement a check here if user already exists using UserDAO (not shown in current DAO)
        boolean success = UserDAO.registerUser(name, email, password, role);
        if (success) {
            return "User registered successfully.";
        } else {
            return "Failed to register user.";
        }
    }

    // Search books by keyword/title
    public List<Book> searchCatalog(String keyword) {
        return BookDAO.searchBook(keyword);
    }

    // Borrow a book with all necessary validations
    public String borrowBook(int userId, int bookId) {
        // Check if user has unpaid fines on any borrowed book
        List<Book> borrowedBooks = TransactionDAO.getBorrowedBooks(userId);
        for (Book b : borrowedBooks) {
            double fine = TransactionDAO.calculateFine(b.getId(), userId);
            if (fine > 0) {
                return "Please pay your outstanding fine of ₹" + fine + " before borrowing.";
            }
        }

        // Check borrow limit (max 5 books)
        if (borrowedBooks.size() >= 5) {
            return "You have reached the maximum borrow limit (5 books).";
        }

        // Check if book is available
        List<Book> allBooks = BookDAO.searchBook("");
        boolean isAvailable = false;
        for (Book b : allBooks) {
            if (b.getId() == bookId) {
                // Here you may want to check availability in DB if you track it; 
                // currently assuming all found books are available.
                isAvailable = true;
                break;
            }
        }
        if (!isAvailable) {
            return "Book is not available.";
        }

        // Borrow the book
        boolean success = TransactionDAO.borrowBook(bookId, userId);
        if (success) {
            return "Book borrowed successfully.";
        } else {
            return "Failed to borrow the book.";
        }
    }

    // Return a book and calculate fines if any
    public String returnBook(int userId, int bookId) {
        boolean success = TransactionDAO.returnBook(bookId, userId);
        if (!success) {
            return "Failed to return the book or no active borrow found.";
        }

        double fine = TransactionDAO.calculateFine(bookId, userId);
        if (fine > 0) {
            return "Book returned successfully. Please pay a fine of ₹" + fine + ".";
        } else {
            return "Book returned successfully. No fine.";
        }
    }

    // Generate various reports based on reportType
    public String generateReport(String reportType, int userId) {
        switch (reportType.toLowerCase()) {
            case "history":
                List<Transaction> history = TransactionDAO.getTransactionHistory(userId);
                return formatTransactionList(history);

            case "overdue":
                List<Transaction> overdue = TransactionDAO.getOverdueTransactions();
                return formatTransactionList(overdue);

            // You can add more report types like "issued", "fines" if needed by adding DAO methods

            default:
                return "Invalid report type.";
        }
    }

    // Helper method to format a list of transactions into a readable string
    private String formatTransactionList(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return "No records found.";
        }
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactions) {
            sb.append("Book: ").append(tx.getBook().getTitle())
              .append(" | Borrowed: ").append(tx.getBorrowDate())
              .append(" | Returned: ").append(tx.getReturnDate() != null ? tx.getReturnDate() : "Not yet")
              .append(" | Fine: ₹").append(tx.getFineAmount())
              .append("\n");
        }
        return sb.toString();
    }
}

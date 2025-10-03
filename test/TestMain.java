import java.util.List;

// Import model classes
import model.Book;
import model.Transaction;
import model.User;
import model.Librarian;

// Import DAO classes
import dao.UserDAO;
import dao.BookDAO;
import dao.TransactionDAO;

import dao.DBConnection;


public class TestMain {
    public static void main(String[] args) {

        // Test DB connection
        if (DBConnection.getConnection() != null) {
            System.out.println("✅ Database connected successfully!");
        } else {
            System.out.println("❌ Failed to connect.");
            return;
        }

        // Test user registration
        boolean reg = UserDAO.registerUser("Sree", "sree@example.com", "1234", "student");
        System.out.println("User registered: " + reg);

        // Test login
        User user = UserDAO.login("sree@example.com", "1234");
        System.out.println("Login successful: " + (user != null));


        // Test adding a book
        boolean bookAdded = BookDAO.addBook("Java Programming", "Author Name", "Programming");
        System.out.println("Book added: " + bookAdded);

        // Test searching books
        List<Book> books = BookDAO.searchBook("Java");
        System.out.println("Search results:");
        for (Book b : books) {
            System.out.println(b.getId() + ": " + b.getTitle() + " by " + b.getAuthor());
        }

        // Test updating a book
        if (!books.isEmpty()) {
            int bookId = books.get(0).getId();
            boolean updated = BookDAO.updateBook(bookId, "Advanced Java", "New Author", "Programming");
            System.out.println("Book updated: " + updated);

            // Test removing the same book
            boolean removed = BookDAO.removeBook(bookId);
            System.out.println("Book removed: " + removed);
        }

        // Borrow a book
        TransactionDAO.borrowBook(3, 1);

        // Return a book
        TransactionDAO.returnBook(3, 1);

        // Calculate fine
        double fine = TransactionDAO.calculateFine(3, 1);
        System.out.println("Fine: " + fine);

        // Get borrowed books
        List<Book> borrowed = TransactionDAO.getBorrowedBooks(1);
        borrowed.forEach(b -> System.out.println("Borrowed: " + b.getTitle()));

        // Get transaction history
        List<Transaction> history = TransactionDAO.getTransactionHistory(1);
        history.forEach(tx -> System.out.println("History: " + tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

        // Get overdue transactions
        List<Transaction> overdue = TransactionDAO.getOverdueTransactions();
        overdue.forEach(tx -> System.out.println("Overdue: " + tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

        // ------------------------------------------------
        // 🔹 Extra: Directly testing core model classes
        // ------------------------------------------------
        User student = new User(101, "Alice", "alice@example.com", "pass123", "student");
        Librarian librarian = new Librarian(201, "Bob", "bob@example.com", "lib123");

        System.out.println("Created User: " + student.getName() + " (" + student.getRole() + ")");
        System.out.println("Created Librarian: " + librarian.getName() + " (" + librarian.getEmail() + ")");
    }
}

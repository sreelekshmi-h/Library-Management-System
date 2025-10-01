import java.util.List;

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
        boolean login = UserDAO.login("sree@example.com", "1234");
        System.out.println("Login successful: " + login);

        // Test adding a book
        boolean bookAdded = BookDAO.addBook("Java Programming", "Author Name", "Programming");
        System.out.println("Book added: " + bookAdded);

        // Test searching books
        List<Book> books = BookDAO.searchBook("Java");
        System.out.println("Search results:");
        for (Book b : books) {
            System.out.println(b.getId() + ": " + b.getTitle() + " by " + b.getAuthor());
        }

        // Test updating a book (use a valid book ID from search results)
        if (!books.isEmpty()) {
            int bookId = books.get(0).getId();
            boolean updated = BookDAO.updateBook(bookId, "Advanced Java", "New Author", "Programming");
            System.out.println("Book updated: " + updated);
        }

        // Test removing a book (use a valid book ID)
        if (!books.isEmpty()) {
            int bookId = books.get(0).getId();
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
borrowed.forEach(b -> System.out.println(b.getTitle()));

// Get transaction history
List<Transaction> history = TransactionDAO.getTransactionHistory(1);
history.forEach(tx -> System.out.println(tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

// Get overdue transactions
List<Transaction> overdue = TransactionDAO.getOverdueTransactions();
overdue.forEach(tx -> System.out.println(tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

    }
}

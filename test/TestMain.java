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
        if (user != null) {
            System.out.println("Login successful: true | UserID: " + user.getUserID());
        } else {
            System.out.println("Login failed");
            return;
        }

        // Test adding a book
        boolean bookAdded = BookDAO.addBook("Java Programming", "Author Name", "Programming");
        System.out.println("Book added: " + bookAdded);

        // Fetch the added book dynamically
        List<Book> books = BookDAO.searchBook("Java");
        if (books.isEmpty()) {
            System.out.println("No books found to test further.");
            return;
        }
        Book book = books.get(0); // Take the first matched book
        System.out.println("Book fetched for testing: " + book.getId() + " - " + book.getTitle());

        // Test updating the book
        boolean updated = BookDAO.updateBook(book.getId(), "Advanced Java", "New Author", "Programming");
        System.out.println("Book updated: " + updated);

        // Borrow the book
        boolean borrowed = TransactionDAO.borrowBook(book.getId(), user.getUserID());
        System.out.println("Book borrowed: " + borrowed);

        // Calculate fine (should be 0 if just borrowed)
        double fine = TransactionDAO.calculateFine(book.getId(), user.getUserID());
        System.out.println("Fine: " + fine);

        // Return the book
        boolean returned = TransactionDAO.returnBook(book.getId(), user.getUserID());
        System.out.println("Book returned: " + returned);

        // Get borrowed books (should be empty now)
        List<Book> borrowedBooks = TransactionDAO.getBorrowedBooks(user.getUserID());
        borrowedBooks.forEach(b -> System.out.println("Borrowed: " + b.getTitle()));

        // Get transaction history
        List<Transaction> history = TransactionDAO.getTransactionHistory(user.getUserID());
        history.forEach(tx -> System.out.println("History: " + tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

        // Get overdue transactions
        List<Transaction> overdue = TransactionDAO.getOverdueTransactions();
        overdue.forEach(tx -> System.out.println("Overdue: " + tx.getBook().getTitle() + " | Fine: " + tx.getFineAmount()));

        // Remove the test book
        boolean removed = BookDAO.removeBook(book.getId());
        System.out.println("Book removed: " + removed);

        // ------------------------------------------------
        // 🔹 Extra: Directly testing core model classes
        // ------------------------------------------------
        User student = new User(user.getUserID(), user.getName(), user.getEmail(), "1234", "student");
        Librarian librarian = new Librarian(201, "Bob", "bob@example.com", "lib123", "E001");

        System.out.println("Created User: " + student.getName() + " (" + student.getRole() + ")");
        System.out.println("Created Librarian: " + librarian.getName() + " (" + librarian.getEmail() + ")");
    }
}

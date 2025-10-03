package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Book;

public class BookDAO {

    public static boolean addBook(String title, String author, String category) {
        String sql = "INSERT INTO books(title, author, category) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // You can add methods like searchBook, updateBook, removeBook here

    
    // Method to search books by title (partial match)
    public static List<Book> searchBook(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%"); // Partial match using LIKE
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id")); // Assuming your table has an 'id' column
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
    //method to updateBook
    public static boolean updateBook(int id, String newTitle, String newAuthor, String newCategory) {
    String sql = "UPDATE books SET title = ?, author = ?, category = ? WHERE id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, newTitle);
        stmt.setString(2, newAuthor);
        stmt.setString(3, newCategory);
        stmt.setInt(4, id);

        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0; // returns true if at least one row was updated

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
//method to removeBook
public static boolean removeBook(int id) {
    String sql = "DELETE FROM books WHERE id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        int rowsDeleted = stmt.executeUpdate();
        return rowsDeleted > 0; // returns true if a row was deleted

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}


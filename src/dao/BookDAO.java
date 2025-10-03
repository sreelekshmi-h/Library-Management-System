package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Book;

public class BookDAO {

    public static boolean addBook(String title, String author, String category) {
        String sql = "INSERT INTO books(title, author, category, availability) VALUES(?, ?, ?, 1)";
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

    public static List<Book> searchBook(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            stmt.setString(1, search);
            stmt.setString(2, search);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("bookID"));  // ✅ use correct column
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

    public static boolean updateBook(int bookID, String newTitle, String newAuthor, String newCategory) {
        String sql = "UPDATE books SET title = ?, author = ?, category = ? WHERE bookID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newTitle);
            stmt.setString(2, newAuthor);
            stmt.setString(3, newCategory);
            stmt.setInt(4, bookID);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeBook(int bookID) {
        String sql = "DELETE FROM books WHERE bookID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

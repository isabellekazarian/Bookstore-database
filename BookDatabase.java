import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class BookDatabase {

    public static Book getBookFromIsbn(Statement statement, String isbn) throws SQLException {


        // Get book result ----------------------
        ResultSet bookResult = statement.executeQuery("SELECT * FROM books WHERE isbn = " + isbn);

        if(!bookResult.next()) {
            return null;
        }

        Book book = new Book(
                bookResult.getString("isbn"),
                bookResult.getString("title"),
                bookResult.getString("author"),
                bookResult.getString("genre"),
                bookResult.getInt("qty")
        );

        bookResult.close();
        return book;

    }

    public static int setQuantity(Statement statement, Book book, int newQuantity) throws SQLException {

        return statement.executeUpdate("UPDATE books SET qty = " + newQuantity + " WHERE isbn = " + book.getIsbn());
    }

    public static int updateValue(Statement statement, Book book, String columnName, String newValue) throws SQLException {

        return statement.executeUpdate("UPDATE books SET " + columnName + " = '" + newValue + "' WHERE isbn = " + book.getIsbn());
    }



    public static int addBookToTable(Statement statement, Book book) throws SQLException {

        return statement.executeUpdate("INSERT INTO books VALUES ('"
                + book.getIsbn() + "', '" + book.getTitle() + "', '"
                + book.getAuthor() + "', " + book.getQuantity() + ", '"
                + book.getGenre() + "')");
    }
}

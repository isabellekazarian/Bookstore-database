import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EBookstore {

    public static void main (String[] args) {

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ebookstore_db?useSSL=false",
                    "test",
                    "test"
            );

            // Create a direct line to the database
            Statement statement = connection.createStatement();
            Scanner inputScan = new Scanner(System.in);


            System.out.println("\nWelcome to the bookshop database!\n");

            while(true) {
                System.out.println("\nPlease select from the following options: ");
                System.out.println("1    - Record Sale");
                System.out.println("2    - Search Books");
                System.out.println("3    - Add New Book");
                System.out.println("4    - Update Book Details");
                System.out.println("5    - Remove Book");
                System.out.println("6    - View Low Stock");
                System.out.println("0    - Exit");
                System.out.println();
                System.out.print("Please choose:  ");

                String userInput = inputScan.nextLine().strip();

                System.out.println("\n---------------------------------------------");

                if(userInput.equals("0")) {
                    System.out.println("Goodbye!");
                    break;
                }

                switch(userInput){
                    case "1": recordSale(statement, inputScan);
                        break;
                    case "2": searchBook(statement, inputScan);
                        break;
                    case "3": addNewBook(statement, inputScan);
                        break;
                    case "4": updateBookDetails(statement, inputScan);
                        break;
                    case "5": removeBook(statement, inputScan);
                        break;
                    case "6": viewLowStock(statement, inputScan);
                        break;
                    default:
                        System.out.println("Error: invalid input.");
                        User.confirmContinue(inputScan);
                }

            }

            statement.close();
            connection.close();
            inputScan.close();

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

    }

    public static void recordSale(Statement statement, Scanner inputScan) throws SQLException {

        Book book = null;


        // Get valid book record -------------------------------------
        while(book == null) {

            String isbn = User.getIsbn(inputScan);
            if(isbn.equals("quit")) { return; }

            book = BookDatabase.getBookFromIsbn(statement, isbn);

            if(book == null){
                System.out.println("Error:  ISBN " + isbn + " does not match any records.");
                continue;
            }

            if(!User.isVerifiedBook(inputScan, book)){
                book = null;
            }
        }

        int numCopiesAvailable = book.getQuantity();


        // Verify number of copies to sell -------------------------------------
        if(numCopiesAvailable < 1){

            System.out.print("Error:  No copies available.\n");
            User.confirmContinue(inputScan);
            return;
        }

        System.out.print("Enter quantity to sell (" + numCopiesAvailable + " available):  ");
        int numCopiesToSell = User.getPositiveInt(inputScan);

        if(numCopiesToSell > numCopiesAvailable){
            System.out.print("Error:  Only " + numCopiesAvailable + " copies available.\n");
            User.confirmContinue(inputScan);
            return;
        }


        // Confirm sale w user -------------------------------------
        System.out.print("Confirm sale of " + numCopiesToSell + " copies (Y/N):  ");
        String userInput = inputScan.nextLine().strip();

        if(!userInput.toLowerCase().equals("y")){
            System.out.print("No sale.\n");
            User.confirmContinue(inputScan);
            return;
        }


        // Process sale -------------------------------------
        int newQuantity = numCopiesAvailable - numCopiesToSell;
        int rowsAffected = BookDatabase.setQuantity(statement, book, newQuantity);

        System.out.println(rowsAffected + " title(s) updated.\n" );

        System.out.println("Sale processed successfully. ");
        System.out.println("Sold " + numCopiesToSell + " copies of '"
                + book.getTitle() + "' by '"
                + book.getAuthor() + "'.");
        System.out.println(newQuantity + " copies remaining.");

        User.confirmContinue(inputScan);

    }

    public static void searchBook(Statement statement, Scanner inputScan) throws SQLException {

        System.out.println("\nThis menu will search title, author, and ISBN.\n");

        // Get search input ------------------------------------------
        String userSearch;
        while (true) {

            System.out.print("Enter search terms (Q to quit):  ");
            userSearch = inputScan.nextLine().toLowerCase();

            if (userSearch.equals("q")) {
                return;
            }

            if (userSearch.length() < 2) {
                System.out.println("Error:  Please enter at least 2 characters.\n");
                continue;
            }

            break;
        }


        // Create list of results ------------------------------------
        ResultSet searchResults;
        searchResults = statement.executeQuery(
                "SELECT * FROM books WHERE LOWER(isbn) LIKE '%" + userSearch + "%'" +
                        " OR LOWER(title) LIKE '%" + userSearch + "%'" +
                        " OR LOWER(author) LIKE '%" + userSearch + "%'"
        );

        List<Book> bookList = new ArrayList<>();
        while (searchResults.next()) {
            Book book = new Book (
                    searchResults.getString("isbn"),
                    searchResults.getString("title"),
                    searchResults.getString("author"),
                    searchResults.getString("genre"),
                    searchResults.getInt("qty")
            );

            bookList.add(book);
        }

        searchResults.close();


        // User output ----------------------------------------------
        if(bookList.isEmpty()) {
            System.out.println("Error:  Search does not match any records.");
        }

        else {
            for(Book book: bookList){
                book.print();
            }
        }

        User.confirmContinue(inputScan);

    }

    public static void addNewBook(Statement statement, Scanner inputScan) throws SQLException {

        System.out.println("\nWelcome to the book wizard!");
        System.out.println();

        System.out.print("Let's begin!");

        // Get & validate ISBN -------------------------------------------------
        String isbn = User.getIsbn(inputScan);
        if(isbn == "quit") {
            return;
        }

        Book book = BookDatabase.getBookFromIsbn(statement, isbn);

        if(book != null) {
            System.out.println("\nError:  ISBN " + isbn + " already exists in database.");
            System.out.println();
            book.print();
            System.out.println();

            User.confirmContinue(inputScan);
            return;
        }


        // Get title -------------------------------------------------
        String title = User.getTitle(inputScan);

        // Get author -------------------------------------------------
        String author = User.getAuthor(inputScan);

        // Get genre -------------------------------------------------
        String genre = User.getGenre(inputScan);

        // Get quantity -------------------------------------------------
        System.out.print("Please enter the quantity:  ");
        int quantity = User.getPositiveInt(inputScan);


        // Create book & add to database
        Book newBook = new Book(isbn, title, author, genre, quantity);
        System.out.println();
        newBook.print();

        System.out.println("Confirm new title (Y/N):  ");

        if(!inputScan.nextLine().toLowerCase().equals("y")) {
            System.out.println("Cannot confirm title. Please try again.");
        }

        else {
            int rowsAffected = BookDatabase.addBookToTable(statement, newBook);
            System.out.println("\n" + rowsAffected + " title(s) updated." );
            System.out.println("Book added successfully.");
        }


        User.confirmContinue(inputScan);

    }

    public static void updateBookDetails(Statement statement, Scanner inputScan) throws SQLException {
        System.out.println("\nUpdate Book Details: ");


        // Get & validate ISBN -------------------------------------------------
        String isbn = User.getIsbn(inputScan);
        if(isbn == "quit") {
            return;
        }

        Book book = BookDatabase.getBookFromIsbn(statement, isbn);
        if(book == null){
            System.out.println("Error:  No record found for ISBN " + isbn + ".");
            User.confirmContinue(inputScan);
            return;
        }

        System.out.println();
        System.out.println("Details for ISBN " + isbn + ":");
        book.print();


        while(true) {

            // User selects what to update -----------------------------------------
            System.out.println("Select to modify: ");
            System.out.println("1    - Title");
            System.out.println("2    - Author");
            System.out.println("3    - ISBN");
            System.out.println("4    - Genre");
            System.out.println("5    - Quantity");
            System.out.println("0    - Exit");
            System.out.println();
            System.out.print("Please choose:  ");
            String userInput = inputScan.nextLine().strip();

            if (userInput.equals("0")) {
                return;
            }

            switch (userInput) {
                case "1": // TITLE
                    String newTitle = User.getTitle(inputScan);

                    BookDatabase.updateValue(statement, book, "title", newTitle);

                    System.out.println("Title updated successfully.");

                    break;

                case "2": // AUTHOR
                    String newAuthor = User.getAuthor(inputScan);

                    BookDatabase.updateValue(statement, book, "author", newAuthor);

                    System.out.println("Author updated successfully.");
                    break;

                case "3": // ISBN
                    String newIsbn = User.getIsbn(inputScan);
                    Book existingBook = BookDatabase.getBookFromIsbn(statement, newIsbn);

                    if(existingBook != null){
                        System.out.println("\nError:  ISBN " + isbn + " belongs to another title.\n");
                        existingBook.print();
                        break;
                    }

                    BookDatabase.updateValue(statement, book, "isbn", newIsbn);

                    book.setIsbn(newIsbn);

                    System.out.println("ISBN updated successfully.");
                    break;

                case "4": // GENRE
                    System.out.println();
                    String newGenre = User.getGenre(inputScan);

                    BookDatabase.updateValue(statement, book, "genre", newGenre);

                    System.out.println("Genre updated successfully.");

                    break;

                case "5": // QUANTITY
                    System.out.print("\nEnter new quantity:  ");
                    int newQuantity = User.getPositiveInt(inputScan);

                    BookDatabase.setQuantity(statement, book, newQuantity);
                    System.out.println("Quantity updated successfully.");

                    break;

                default:
                    System.out.println("Error: invalid input.");

            }
            User.confirmContinue(inputScan);
            book = BookDatabase.getBookFromIsbn(statement, book.getIsbn());
            book.print();
        }

    }

    public static void removeBook(Statement statement, Scanner inputScan) throws SQLException{
        String isbn = User.getIsbn(inputScan);
        Book book = BookDatabase.getBookFromIsbn(statement, isbn);

        if(book == null){
            System.out.println("ISBN " + isbn + " does not match any records.");
            User.confirmContinue(inputScan);
            return;
        }

        System.out.println();
        book.print();

        System.out.println("WARNING: This action will remove ALL records of this title.");
        System.out.print("Are you sure you wish to continue? (Y/N):  ");

        String userInput = inputScan.nextLine().toLowerCase().strip();

        if(userInput.equals("y")){
            statement.executeUpdate("DELETE FROM books WHERE isbn = " + isbn);
            System.out.println("\nRecord " + isbn + " deleted.");
        }

        else{
            System.out.println("\nAction canceled. No changes have been saved.");
        }

        User.confirmContinue(inputScan);

    }

    public static void viewLowStock(Statement statement, Scanner inputScan) throws SQLException {

        final int LOW_STOCK_VALUE = 10;

        ResultSet lowStockResults = statement.executeQuery("SELECT * FROM books WHERE qty < " + LOW_STOCK_VALUE);

        // Create list of results ------------------------------------------
        List<Book> lowStockList = new ArrayList<>();
        while (lowStockResults.next()) {
            Book book = new Book (
                    lowStockResults.getString("isbn"),
                    lowStockResults.getString("title"),
                    lowStockResults.getString("author"),
                    lowStockResults.getString("genre"),
                    lowStockResults.getInt("qty")
            );

            lowStockList.add(book);
        }

        lowStockResults.close();


        // User output ----------------------------------------------
        if(lowStockList.isEmpty()) {
            System.out.println("There are currently no titles with fewer than " + LOW_STOCK_VALUE + " copies.");
        }

        else {
            for(Book book: lowStockList){
                book.print();
            }
        }

        User.confirmContinue(inputScan);


    }
}

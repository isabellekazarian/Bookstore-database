public class Book {

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int quantity;

    public Book(){
        String null_string = "No data";

        this.isbn = null_string;
        this.title = null_string;
        this.author = null_string;
        this.genre = "Uncategorized";
        this.quantity = -1;
    }


    public Book(String isbn, String title, String author, String genre, int quantity) {
        this.isbn = isbn;
        this.quantity = quantity;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }


    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getQuantity() {
        return quantity;
    }

    public void print() {
        System.out.println(
                  "Title:    " + title + "\n"
                + "Author:   " + author + "\n"
                + "ISBN:     " + isbn + "\n"
                + "Genre:    " + genre + "     Qty Available:  " + quantity + "\n"
        );
    }

}

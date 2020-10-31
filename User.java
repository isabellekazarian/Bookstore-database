import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Character.isDigit;

public class User {

    public static String getIsbn(Scanner inputScan) {

        final int ISBN_LENGTH = 13;


        while(true){

            System.out.print("\nEnter ISBN (Q to quit):  ");
            String isbn = inputScan.nextLine().strip();

            if (isbn.toLowerCase().equals("q")) {
                return "quit";
            }

            if (!isNumericString(isbn)) {
                System.out.println("Error:  Please enter a valid ISBN.");
                continue;
            }

            if (isbn.length() != ISBN_LENGTH) {
                System.out.println("Error:  ISBN must contain " + ISBN_LENGTH + " digits.");
                continue;
            }

            return isbn;

        }
    }

    public static int getPositiveInt(Scanner inputScan){

        while(true) {
            String userInput = inputScan.nextLine().strip();


            if (!isNumericString(userInput)) {
                System.out.println("\nError:  Please enter a number:  ");
                continue;
            }

            if(userInput.length() > 9){
                System.out.println("\nError:  Number too large.\n");
                System.out.println("Please enter a number:  ");
                continue;
            }

            int integer = Integer.parseInt(userInput);

            if (integer < 1) {
                System.out.println("\nPlease enter positive number:  ");
                continue;
            }

            return integer;

        }
    }

    public static void confirmContinue(Scanner inputScan){
        System.out.print("\nEnter any key to continue:  ");
        inputScan.nextLine();
    }

    public static boolean isNumericString(String str) {
        int length = str.length();
        for (int i = 0; i < length; ++i) {
            if (!isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isVerifiedBook(Scanner inputScan, Book book) {
        // Verify with user ----------------------
        System.out.println();
        book.print();

        System.out.println("Confirm title (Y/N):  ");

        if(inputScan.nextLine().toLowerCase().equals("y")) {
            return true;
        }

        else {
            System.out.println("Cannot confirm title. Please try again.");
            return false;
        }
    }

    public static String getGenre(Scanner inputScan) {
        List<String> genresList = Arrays.asList(
                "Classics",
                "Childrens",
                "Sci-Fi",
                "Mystery",
                "Biography",
                "Romance",
                "Poetry"
        );

        System.out.println("\nPlease select from the following genres: ");

        for (int i = 0; i < genresList.size(); i++){
            System.out.println((i + 1) + "    - " + genresList.get(i));
        }
        System.out.println("0    - Uncategorized");
        System.out.println();

        String genre = null;
        while(genre == null) {
            System.out.print("Please choose:  ");

            String userInput = inputScan.nextLine().strip();

            if(userInput.equals("0")) {
                genre = "Uncategorized";
                break;
            }


            if((!isNumericString(userInput)) || (userInput.length() > 2)){
                System.out.println("Error:  Please select a genre from the menu.");
                continue;
            }

            int selection = Integer.parseInt(userInput);
            if(selection > genresList.size()){
                System.out.println("Error:  Please select a genre from the menu.");
                continue;
            }

            genre = genresList.get(selection - 1);
        }
        return genre;
    }

    public static String getAuthor(Scanner inputScan) {
        String author = null;
        while(author == null) {
            System.out.println("\nEnter author:  ");

            author = inputScan.nextLine().strip();

            if(author == "" || author == null){
                author = null;
                System.out.println("\nError:  No input.");
            }
        }
        return author;
    }

    public static String getTitle(Scanner inputScan) {
        String title = null;
        while(title == null) {
            System.out.println("\nEnter book title:  ");

            title = inputScan.nextLine().strip();

            if(title == "" || title == null){
                title = null;
                System.out.println("\nError:  No input.");
            }

        }
        return title;
    }
}


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DateParser {
    public static void main(String[] args) {
       check();
    }
    static void check() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Paste date pattern. Example: M/d/yyyy h:m");
        String pattern = scanner.nextLine().trim();
        System.out.println("Paste date. Example: 8/30/2017 11:47");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String input = scanner.nextLine().trim();
        try {
            Date date = simpleDateFormat.parse(input);
            System.out.println("Date was parsed. Compare the output: " + date);
        } catch (ParseException e) {
            System.out.println("Date was not parsed! Wrong pattern for date!");
            e.printStackTrace();
            check();
        }

    }
}

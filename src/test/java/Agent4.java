import java.util.Random;
import java.util.Scanner;

public class Agent4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("START SOFT 140 MID MID LOW");

        while (true) {
            String input = scanner.nextLine();
            System.err.println(input);
            int activePlayer = Integer.parseInt(input);
            for (int i = 0; i < activePlayer; i++)
            {   
                input = scanner.nextLine();
                System.err.println(input);
            }
            System.out.println("PUSH MID MID LOW");
        }
    }
}

import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        
        
        input = scanner.nextLine();
        System.err.println(input);
        // input = scanner.nextLine();
        // System.err.println(input);

        int loop = 0;
        while (true) {
            input = scanner.nextLine();
            System.err.println(input);
            int activePlayer = Integer.parseInt(input);
            for (int i = 0; i < activePlayer; i++)
            {   
                input = scanner.nextLine();
                System.err.println(input);
            }

            if (loop == 0)
            {
                System.out.println("START MEDIUM 140 MID MID MID");
            } else {
                System.out.println("PUSH MID MID MID");
            }

            loop++;
        }
    }
}

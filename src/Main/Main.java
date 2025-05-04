package Main;

import java.util.Scanner;

public class Main {
	
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("NaviGhost");
        System.out.println(" ");
        
        // Prompt the user for the base URL
        System.out.print("Enter the base URL (e.g., https://example.com): ");
        String baseUrl = scanner.nextLine();

        // Prompt the user for the path to the wordlist
        System.out.print("Enter the path to your wordlist (e.g., wordlist.txt): ");
        String wordlistPath = scanner.nextLine();

        // Prompt the user for hiding 400-level status codes
        System.out.print("Do you want to hide 400-level status codes? (yes/no): ");
        String hide400Input = scanner.nextLine();
        boolean hide400 = hide400Input.equalsIgnoreCase("yes");

        // Prompt the user for hiding 500-level status codes
        System.out.print("Do you want to hide 500-level status codes? (yes/no): ");
        String hide500Input = scanner.nextLine();
        boolean hide500 = hide500Input.equalsIgnoreCase("yes");
        
        System.out.print("Enter a custom User-Agent (or press Enter to use default): ");
        String userAgent = scanner.nextLine();

        // Perform dirbusting
        if (userAgent.trim().isEmpty()) {
            Dirbuster.dirbust(baseUrl, wordlistPath, hide400, hide500);
        } else {
            Dirbuster.dirbust(baseUrl, wordlistPath, hide400, hide500, userAgent);
        }

        // Close the scanner
        scanner.close();
    }
}
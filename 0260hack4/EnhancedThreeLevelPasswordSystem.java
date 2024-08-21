package enhancedthreelevelpasswordsystem;

import java.util.Scanner;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EnhancedThreeLevelPasswordSystem {
    private static String textualPassword;
    private static String graphicalPassword;
    private static long behavioralTiming;
    private static String securityAnswer;
    private static int failedAttempts = 0;
    private static LocalDateTime passwordCreationTime;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Password creation process
        createTextualPassword(scanner);
        createGraphicalPassword(scanner);
        createBehavioralPassword(scanner);
        createSecurityQuestion(scanner);

        System.out.println("\nPasswords successfully created! Now, let's log in.");

        // Check for password expiry and prompt reset if necessary
        if (isPasswordExpired()) {
            System.out.println("Your password has expired. Please reset your passwords.");
            resetPasswords(scanner);
        }

        // Authentication process
        if (authenticateUser(scanner)) {
            System.out.println("Access granted! You have successfully logged in.");
        } else {
            System.out.println("Access denied! Authentication failed.");
        }

        scanner.close();
    }

    private static void createTextualPassword(Scanner scanner) {
        System.out.println("Create your textual password:");
        textualPassword = scanner.nextLine();
        while (!isValidPassword(textualPassword)) {
            System.out.println("Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.");
            textualPassword = scanner.nextLine();
        }
        passwordCreationTime = LocalDateTime.now();
        System.out.println("Textual password created successfully.");
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            System.out.println("Password strength: Weak");
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        int strengthScore = 0;
        if (hasUpper) strengthScore++;
        if (hasLower) strengthScore++;
        if (hasDigit) strengthScore++;
        if (hasSpecial) strengthScore++;

        switch (strengthScore) {
            case 1:
                System.out.println("Password strength: Weak");
                break;
            case 2:
                System.out.println("Password strength: Fair");
                break;
            case 3:
                System.out.println("Password strength: Good");
                break;
            case 4:
                System.out.println("Password strength: Strong");
                break;
        }

        return strengthScore >= 3;
    }

    private static void createGraphicalPassword(Scanner scanner) {
        System.out.println("Create your graphical password by choosing a sequence of numbers (e.g., 1 3 7 9):");
        graphicalPassword = scanner.nextLine();
        System.out.println("Graphical password created successfully.");
    }

    private static void createBehavioralPassword(Scanner scanner) {
        System.out.println("Create your behavioral password by typing 'Secure' as quickly as possible.");
        long startTime = System.currentTimeMillis();
        String typedPassword = scanner.nextLine();
        long endTime = System.currentTimeMillis();
        behavioralTiming = endTime - startTime;
        while (!typedPassword.equals("Secure")) {
            System.out.println("Incorrect input. Please type 'Secure' as quickly as possible.");
            startTime = System.currentTimeMillis();
            typedPassword = scanner.nextLine();
            endTime = System.currentTimeMillis();
            behavioralTiming = endTime - startTime;
        }
        System.out.println("Behavioral password created successfully.");
    }

    private static void createSecurityQuestion(Scanner scanner) {
        System.out.println("Set your security question: What is your favorite color?");
        securityAnswer = scanner.nextLine();
        System.out.println("Security question set successfully.");
    }

    private static boolean authenticateUser(Scanner scanner) {
        // CAPTCHA Verification
        if (!verifyCaptcha(scanner)) {
            System.out.println("Captcha verification failed.");
            return false;
        }

        // Level 1: Textual Password Authentication
        System.out.println("Enter your textual password:");
        String inputTextualPassword = scanner.nextLine();
        if (!textualPassword.equals(inputTextualPassword)) {
            System.out.println("Incorrect textual password.");
            logFailedAttempt();
            return false;
        }

        // Level 2: Graphical Password Authentication
        System.out.println("Enter your graphical password pattern (e.g., 1 3 7 9):");
        String inputGraphicalPassword = scanner.nextLine();
        if (!graphicalPassword.equals(inputGraphicalPassword)) {
            System.out.println("Incorrect graphical password.");
            logFailedAttempt();
            return false;
        }

        // Level 3: Behavioral Password Authentication
        System.out.println("Behavioral Password Check: Type 'Secure' as quickly as possible and press Enter.");
        long startTime = System.currentTimeMillis();
        String inputBehavioralPassword = scanner.nextLine();
        long endTime = System.currentTimeMillis();
        long inputTiming = endTime - startTime;

        if (!inputBehavioralPassword.equals("Secure") || inputTiming > behavioralTiming + 500) {
            System.out.println("Behavioral password did not match.");
            System.out.println("Security Question: What is your favorite color?");
            String inputSecurityAnswer = scanner.nextLine();
            if (securityAnswer.equalsIgnoreCase(inputSecurityAnswer)) {
                System.out.println("Security question answered correctly. Access granted.");
                return true;
            } else {
                logFailedAttempt();
                return false;
            }
        }

        // Multi-Factor Authentication (MFA)
        if (!verifyOTP(scanner)) {
            System.out.println("OTP verification failed.");
            logFailedAttempt();
            return false;
        }

        return true;
    }

    private static void logFailedAttempt() {
        failedAttempts++;
        System.out.println("Failed attempt #" + failedAttempts);
        if (failedAttempts >= 3) {
            System.out.println("Multiple failed attempts detected. Account locked temporarily.");
            System.exit(1);
        }
    }

    private static boolean verifyCaptcha(Scanner scanner) {
        Random rand = new Random();
        int num1 = rand.nextInt(10);
        int num2 = rand.nextInt(10);
        int correctAnswer = num1 + num2;

        System.out.println("Captcha Verification: What is " + num1 + " + " + num2 + "?");
        int userAnswer = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        return userAnswer == correctAnswer;
    }

    private static boolean verifyOTP(Scanner scanner) {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        System.out.println("OTP sent to your registered email: " + otp);

        System.out.println("Enter the OTP:");
        int userOTP = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        return otp == userOTP;
    }

    private static boolean isPasswordExpired() {
        LocalDateTime now = LocalDateTime.now();
        long daysSinceCreation = ChronoUnit.DAYS.between(passwordCreationTime, now);
        return daysSinceCreation >= 30; // Password expires after 30 days
    }

    private static void resetPasswords(Scanner scanner) {
        System.out.println("You need to reset your passwords due to expiry.");
        createTextualPassword(scanner);
        createGraphicalPassword(scanner);
        createBehavioralPassword(scanner);
        createSecurityQuestion(scanner);
    }
}

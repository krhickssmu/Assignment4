import java.io.*;
import java.util.*;

public class StrongPasswordCheck {

    private static final int M_CHAINING = 1000;
    private static final int M_PROBING = 20000;

    // Hash table for separate chaining
    private static List<String>[] hashTableChaining;

    // Hash table for linear probing
    private static String[] hashTableProbing;

    public static void main(String[] args) {

        Set<String> dictionary = loadDictionary();

        hashTableChaining = initializeChainingTable();
        hashTableProbing = new String[M_PROBING];

        // Insert dictionary into hash tables
        for (String word : dictionary) {
            insertChaining(word);
            insertProbing(word);
        }



        while (true) {
            StdOut.println("Enter a password to test the strength, or type 'exit' to quit.");
            String password = StdIn.readLine().trim();

            if ("exit".equalsIgnoreCase(password)) {
                StdOut.println("Exiting the program.");
                break;
            }
            // Check password strength
            boolean isStrong = isStrongPassword(password, dictionary);
            System.out.println("Is the password strong? " + isStrong);

            // Report costs
            System.out.println("Separate Chaining Comparisons: " + searchChaining(password));
            System.out.println("Linear Probing Comparisons: " + searchProbing(password));
        }
    }

    private static Set<String> loadDictionary()  {

        Set<String> dictionary = new HashSet<>();

        In in = new In("Words.txt");
        while (!in.isEmpty()) {
            String word = in.readLine();
            dictionary.add(word.trim().toLowerCase());
        }
        return dictionary;
    }

    private static List<String>[] initializeChainingTable() {
        List<String>[] table = new LinkedList[M_CHAINING];
        for (int i = 0; i < M_CHAINING; i++) {
            table[i] = new LinkedList<>();
        }
        return table;
    }

    private static int hashCodeOld(String key) {
        int hash = 0;
        int skip = Math.max(1, key.length() / 8);
        for (int i = 0; i < key.length(); i += skip) {
            hash = (hash * 37) + key.charAt(i);
        }
        return hash;
    }

    private static int hashCodeNew(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31) + key.charAt(i);
        }
        return hash;
    }

    private static void insertChaining(String word) {
        int hash = Math.abs(hashCodeNew(word)) % M_CHAINING;
        hashTableChaining[hash].add(word);
    }

    private static void insertProbing(String word) {
        int hash = Math.abs(hashCodeNew(word)) % M_PROBING;
        while (hashTableProbing[hash] != null) {
            hash = (hash + 1) % M_PROBING;
        }
        hashTableProbing[hash] = word;
    }

    private static int searchChaining(String word) {
        int hash = Math.abs(hashCodeNew(word)) % M_CHAINING;
        List<String> bucket = hashTableChaining[hash];
        int comparisons = 0;
        for (String s : bucket) {
            comparisons++;
            if (s.equals(word)) break;
        }
        return comparisons;
    }

    private static int searchProbing(String word) {
        int hash = Math.abs(hashCodeNew(word)) % M_PROBING;
        int comparisons = 0;
        while (hashTableProbing[hash] != null) {
            comparisons++;
            if (hashTableProbing[hash].equals(word)) break;
            hash = (hash + 1) % M_PROBING;
        }
        return comparisons;
    }

    private static boolean isStrongPassword(String password, Set<String> dictionary) {
        // Rule (i): At least 8 characters long
        if (password.length() < 8) {
            return false;
        }

        // Rule (ii): Not a dictionary word
        if (dictionary.contains(password.toLowerCase())) {
            return false;
        }

        // Rule (iii): Not a dictionary word followed by a digit
        for (int i = 0; i <= 9; i++) {
            if (dictionary.contains(password.toLowerCase().replaceAll("\\d+$", ""))) {
                return false;
            }
        }

        return true;
    }
}


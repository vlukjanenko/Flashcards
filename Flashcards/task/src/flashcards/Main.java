package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    final public static Scanner scanner = new Scanner(System.in);
    public static ArrayList<String> log = new ArrayList<>();
    public static String iprt = null;
    //public static String expt = null;

    public enum Action {
        ADD, REMOVE, IMPORT, EXPORT, ASK, EXIT, LOG, HARDEST_CARD, RESET_STATS
    }

    public static Action getAction() throws IllegalArgumentException {
        Action input;
        String inputstr;
        try {
            inputstr = scanner.nextLine();
            log.add(inputstr);
            input = Action.valueOf(inputstr.toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
           return null;
        }
        return input;
    }
    public static String getTerm(LinkedHashMap<String, String> cards) {

        loggedPrint("The card:");
        String term = scanner.nextLine();
        log.add(term);
        if (cards.containsKey(term)) {
            loggedPrint("The card \"" + term + "\" already exists.");
            return null;
            }
    return term;
    }

    public static String getDef(LinkedHashMap<String, String> cards) {

        System.out.println("The definition of the card:");
        String def = scanner.nextLine();
        log.add(def);

            if (cards.containsKey(def)) {
                loggedPrint("The definition \"" + def + "\" already exists.");
                return null;
            }
        return def;
    }

    public static void addCard(LinkedHashMap<String, String> cards, LinkedHashMap<String, String> cards2, LinkedHashMap<String, Integer> misCards) {

        String term = null;
        String def = null;

        if ((term = getTerm(cards)) == null || (def = getDef(cards2)) == null) {
            return;
        }
        cards.put(term, def);
        cards2.put(def, term);
        misCards.put(term, 0);
        loggedPrint("The pair (\""+ term + "\":\"" + def + "\") has been added.");
        loggedPrint();
    }
    public static void removeCard(LinkedHashMap<String, String> cards, LinkedHashMap<String, String> cards2, LinkedHashMap<String, Integer> misCards) {

        String term;
        String def;

        loggedPrint("The card:");
        term = scanner.nextLine();
        log.add(term);
        if (cards.get(term) == null) {
            loggedPrint("Can't remove \"" + term + "\": there is no such card.");
            return;
        }
        def = cards.get(term);
        cards.remove(term);
        cards2.remove(def);
        misCards.remove(term);
        loggedPrint("The card has been removed.");
        loggedPrint();
    }

    public static void askCard(LinkedHashMap<String, String> cards, LinkedHashMap<String, String> cards2, LinkedHashMap<String, Integer> misCards) {

       String inputstr;
        loggedPrint("How many times to ask?");
       int n = Integer.parseInt(inputstr = scanner.nextLine());
       log.add(inputstr);
       int j = 0;
       int size = cards.keySet().size();
       int rnd;
       String def;
       Random random = new Random();

       for (int i = 0; i < n; i++) {
           rnd = random.nextInt(size);
           j = 0;
           for (String key : cards.keySet()) {
               if (j == rnd) {
                   loggedPrint("Print the definition of \"" + key + "\":");
                   def = scanner.nextLine();
                   log.add(def);
                   if (def.equals(cards.get(key))) {
                       loggedPrint("Correct answer.");
                       break;
                   } else if (cards.containsValue(def)) {
                       loggedPrint("Wrong answer. The correct one is \"" + cards.get(key) + "\", you've just written the definition of \"" + cards2.get(def) + "\".");
                       misCards.put(key, misCards.get(key) + 1);
                       break;
                   } else {
                       loggedPrint("Wrong answer. The correct one is \"" + cards.get(key) + "\"");
                       misCards.put(key, misCards.get(key) + 1);
                       break;
                   }
                  // System.out.println();

               }
               j++;
           }
       }
    }

    public static void exportCard(LinkedHashMap<String, String> cards,  LinkedHashMap<String, Integer> misCards, String path) throws IOException {

        String filename;
        int n = 0;

        if (path == null) {
            loggedPrint("File name:");
            filename = scanner.nextLine();
            log.add(filename);
        } else {
            filename = path;
        }
        try (FileWriter writer = new FileWriter(filename)) {
            for (String key : cards.keySet()) {
                writer.write(key + "\n");
                writer.write(cards.get(key) + "\n");
                writer.write(misCards.get(key) + "\n");
                n++;
            }
        } catch (IOException e) {
            loggedPrint("An exception occurs");
        }
        loggedPrint(n + " cards have been saved.");
        //loggedPrint();
    }
    public static void importCard(LinkedHashMap<String, String> cards, LinkedHashMap<String, String> cards2, LinkedHashMap<String, Integer> misCards) {

        String term;
        String def;
        int mistakes;
        int n = 0;
        String pathToFile;
        if (iprt == null) {
            loggedPrint("File name:");
            pathToFile = scanner.nextLine();
            log.add(pathToFile);
        } else {
            pathToFile = iprt;
            iprt = null;
        }
        File file = new File(pathToFile);
        try (Scanner filescanner = new Scanner(file)) {
            while (filescanner.hasNext()) {
                term = filescanner.nextLine();
                def = filescanner.nextLine();
                mistakes = Integer.parseInt(filescanner.nextLine());
                cards.put(term, def);
                cards2.put(def, term);
                misCards.put(term, mistakes);
                n++;
            }
        } catch (FileNotFoundException e) {
            loggedPrint("File not found: " + pathToFile);
            return;
        }
        loggedPrint(n + " cards have been loaded.");
        loggedPrint();
    }
        public static void loggedPrint(String str){
            System.out.println(str);
            log.add(str);
        }
        public static void loggedPrint(){
        System.out.println();
        log.add("");
    }

    public  static void logWrite() {
        String filename;
        int n = 0;

        loggedPrint("File name:");
        filename = scanner.nextLine();
        log.add(filename);
        try (FileWriter writer = new FileWriter(filename)) {
            for (String key : log) {
                writer.write(key + "\n");
            }
        } catch (IOException e) {
            loggedPrint("An exception occurs");
            return;
        }
        loggedPrint("The log has been saved.");
        loggedPrint();
    }
       public static void hardestCard(LinkedHashMap<String, Integer> misCards) {

           int max = 0;
           ArrayList<String> hardest = new ArrayList<>();

           for (Integer i : misCards.values()) {
               if (i > max) {
                   max = i;
               }
           }
           if (max == 0) {
               loggedPrint("There are no cards with errors.\n");
               return;
           }
           for (String term : misCards.keySet()) {
               if (max == misCards.get(term)) {
                   hardest.add(term);
               }
           }
           if (hardest.size() == 1) {
               loggedPrint("The hardest card is \"" + hardest.get(0) +"\". You have "+ max +" errors answering it.");
           } else {
               String result = "";
               for(int i = 0; i < hardest.size(); i++) {
                   result = result + "\"" + hardest.get(i) + "\"";
                   if (i < hardest.size() - 1) {
                       result = result + ", ";
                   } else {
                       result = result + ".";
                   }
               }
               loggedPrint("The hardest cards are " + result + " You have "+ max +" errors answering them.");
           }

       }

       public static void resetStats(LinkedHashMap<String, Integer> misCards) {
        for (String key : misCards.keySet()) {
            misCards.put(key, 0);
        }
        loggedPrint("Card statistics has been reset.\n");
       }
    public static String findParam(String param, String[] args){

        int i = 0;

        while (i < args.length) {
            if (param.equals(args[i])) {
                return args[i + 1];
            }
            i++;
        }
        return null;
    }

        public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        LinkedHashMap<String, String> cards = new LinkedHashMap<>();
        LinkedHashMap<String, String> cards2 = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> misCards = new LinkedHashMap<>();
        Action action = null;

        String expt = null;

        iprt = findParam("-import", args);
        expt = findParam("-export" , args);
        if (iprt != null) {
            importCard(cards, cards2, misCards);
        }
        
        while (true) {
            do {
                loggedPrint("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            } while ((action = getAction()) == null);
            switch (action) {
                case ADD:
                    addCard(cards, cards2, misCards);
                    break;
                case REMOVE:
                    removeCard(cards, cards2, misCards);
                    break;
                case IMPORT:
                    importCard(cards, cards2, misCards);
                    break;
                case EXPORT:
                    exportCard(cards, misCards, null);
                    break;
                case ASK:
                    askCard(cards, cards2, misCards);
                    break;
                case HARDEST_CARD:
                    hardestCard(misCards);
                    break;
                case LOG:
                    logWrite();
                    break;
                case RESET_STATS:
                    resetStats(misCards);
                    break;
                case EXIT:
                    loggedPrint("Bye bye!");
                    if (expt != null)
                        exportCard(cards, misCards, expt);
                    return;
            }
        }
    }
}

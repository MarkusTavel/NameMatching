import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        // Most names generated at
        // https://www.behindthename.com/random/?gender=both&number=2&sets=1&surname=&usage_eng=1
        // Make lists for blacklisted names, names to check and noise words
        List<String> blacklistedNames = readFromfile("blacklistednames.txt");
        List<String> namesToCheck = readFromfile("names.txt");
        List<String> inputNoise = readFromfile("noise_file.txt");
        int levenshteinDistance = 3;
        // First run through all names from checklist
        for (int i = 0; i < namesToCheck.size(); i++) {
            List<String> separatedName = createNameVariables(namesToCheck.get(i));
            //System.out.println(separatedName);
            // If name is blacklisted check the input from noise file
            if(blacklistedNames.contains(namesToCheck.get(i))){
                //System.out.println(namesToCheck.get(i) + " is blacklisted");
                // Run through all lines from noise file
                for (int j = 0; j < inputNoise.size(); j++) {
                    // If precise match is not found check also with levenshtein distance
                    if(!findMatch(separatedName, namesToCheck.get(i), inputNoise.get(j))){
                        if(LevenshteinDistanceDP.compute_Levenshtein_distanceDP(cleanInput(namesToCheck.get(i)), cleanInput(inputNoise.get(j))) < levenshteinDistance) {
                            System.out.println("Close match found: " + namesToCheck.get(i) + ". From line: " + inputNoise.get(j));
                        }
                    }
                }
            }
        }
    }

    public static boolean findMatch(List<String> separatedName, String name, String input){
        // Clean input string
        String cleanedInput = cleanInput(input);
        // If input sentence contains at least first and last name continue with search
        if (cleanedInput.contains(separatedName.get(0).toLowerCase()) &&
            cleanedInput.contains(separatedName.get(separatedName.size()-1).toLowerCase())) {
            //System.out.println("Searching for match...");
        // Otherwise cancel search
        } else {
            //System.out.println("Match not found");
            //System.out.println("From line: " + input);
            return false;
        }
        // Clean noise words
        cleanedInput = cleanNoise(cleanedInput, separatedName);
        // If middle name(s) exists check with them
        if (separatedName.size() > 2) {
            // Check permutations with middle names
            findAllMatches(separatedName,0, name, cleanedInput, input);
        }
        // Check permutations just first and last name
        // Because when input doesn't have middle name it still uses it
        // when checking permutations with full name
        List<String> firstAndLastNames = new ArrayList<>();
        firstAndLastNames.add(separatedName.get(0));
        firstAndLastNames.add(separatedName.get(separatedName.size()-1));
        findAllMatches(firstAndLastNames,0, name, cleanedInput, input);
        return true;
    }

    public static void findAllMatches(List<String> names, int k, String name, String input, String originalInput){
        // https://stackoverflow.com/questions/2920315/permutation-of-array
        // Recursion to look all permutations of given name
        String newName = "";
        for (int i = k; i<names.size(); i++){
            java.util.Collections.swap(names,i, k);
            findAllMatches(names, k+1, name, input, originalInput);
            java.util.Collections.swap(names,k ,i);
        }
        if(k == names.size() - 1){
            //System.out.println(names);
            newName = "";
            for (int i = 0; i<names.size();i++){
                newName += names.get(i);
                //System.out.println(newName);
            }
            // Use pattern to find if match exists
            Pattern pattern = Pattern.compile(newName, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            boolean matchFound = matcher.find();
            // Print out when match is found and from what line
            if(matchFound) {
                System.out.println("Match found: " + name + ". From line: " + originalInput);
            }

        }
    }

    public static String cleanInput(String input){
        // clean string from special characters and lowercase
        String cleanedString = input.replaceAll("[-+,.:';0-9\\s]","").toLowerCase();
        return cleanedString;
    }

    public static String cleanNoise(String input, List<String> name){
        String cleanedInput = "";
        // check if input contains name strings and add to new clean input
        for (int i = 0; i < name.size(); i++) {
            //input = input.replaceAll(name.get(i).toLowerCase(),"");
            if( input.contains(name.get(i).toLowerCase())){
                cleanedInput += name.get(i);
            }
        }
        //System.out.println("new clean input string: " + cleanedInput);
        return cleanedInput;
    }

    public static List<String> createNameVariables(String name){
        // Make new list for separating the name
        List<String> separatedNames = new ArrayList<String>();
        // Separate name variables
        String firstName = name.substring(0, name.indexOf(" "));
        String lastName = name.substring(name.lastIndexOf(" ") + 1);
        // Add first name
        separatedNames.add(cleanInput(firstName));
        // Remove first and last name from string and trim
        // Middle name(s) are handled as one complete string
        // to avoid unnecessary permutations later
        name = name.replace(firstName,"");
        name = name.replace(lastName, "");
        name = name.trim();
        // If middle name(s) exists add to List
        if(name.length() > 0){
            separatedNames.add(cleanInput(name));
        }
        // Add last name
        separatedNames.add(cleanInput(lastName));
        // Return new separated name list
        return separatedNames;
    }

    public static List<String> readFromfile(String fileName){
        // https://www.w3schools.com/java/java_files_read.asp
        List<String> listFromFile = new ArrayList<>();
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                listFromFile.add(data);
                //System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return listFromFile;
    }

}

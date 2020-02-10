package hw2cs3310_Khan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This program reads DNA/RNA sequences from a file and checks them (along with five hard coded sequences) for genetic
 * palindromes. It also prints unsorted versions, sorts using insertion sort and then prints the sorted versions of
 * first and last stored sequences.
 *
 * @author M. Salman Khan
 */
public class Main {

    private static Scanner kbd = new Scanner(System.in);
    private static Scanner input;
    private static String lineRead;
    private static List[] gSeqs;
    private static List[] foundPlndrms;
    private static int[][] occurrences;
    private static List foundPlndrmsCombined;
    private static int[] cumulativeCount;
    private static long stTime;
    private static long enTime;

    /**
     * Main method takes inputs, calls other methods to accomplish certain tasks and prints outputs
     * @param args not used
     * @throws FileNotFoundException can be thrown if input file is missing in directory
     */
    public static void main(String[] args) throws FileNotFoundException {

        System.out.print("Please enter the file name containing DNA/RNA Sequences: ");
        String toRead = kbd.nextLine();
        try {
            input = new Scanner(new File(toRead));
        } catch(FileNotFoundException e) {
            System.out.println("No file of such name found in source directory\n" +
                    "The program will therefore exit");
            System.exit(4);
        }

        // color codes obtained from:
        // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println/6957561
        System.out.println("\033[0;34m\t.. read " + toRead + "\u001B[0m");

        int count = 0;
        while(input.hasNext()) {
            count++;
            input.nextLine();
        }
        input = new Scanner(new File(toRead));

        // Additional instructions by Dr. Gupta through e-mail
        String[] rawLines = new String[count];
        for(int i = 0; i < rawLines.length; i++) {
            rawLines[i] = input.nextLine();
        }
        String toChk;
        for(int i = 0; i < rawLines.length; i++) {
            toChk = rawLines[i];
            if(rawLines[i].equals("X"))
                continue;
            for(int j= 0; j < rawLines.length; j++) {
                if(i != j && rawLines[j].equals(toChk)) {
                    count--;
                    rawLines[j] = "X";
                }
            }
        }

        input = new Scanner(new File(toRead));
        gSeqs = new List[count];
        foundPlndrms = new List[count];
        occurrences = new int[count][];

        // Points #2 & #4
        stTime = System.nanoTime();
        for(int i = 0, k = 0; i < gSeqs.length; i++, k++) {
            gSeqs[i] = new List(null);
            while (rawLines[k].equals("X")) {
                input.nextLine();
                k++;
            }
            lineRead = input.nextLine();
            gSeqs[i].storeStrand(lineRead, false);
        }
        enTime = System.nanoTime();

        System.out.printf("time to create linked lists from sequences: %d milliseconds\n", (enTime - stTime) * 1000000);

        input = new Scanner(new File(toRead));
        gSeqs = new List[count + 5];
        foundPlndrms = new List[count + 5];
        occurrences = new int[count + 5][];

        for(int i = 0, k = 0; i < gSeqs.length; i++, k++) {
            // The five mandatory cases mentioned in 'Design Requirements' section of the assignment paper
            if (i == gSeqs.length - 5) {
                lineRead = "TAGUCTA";
            } else if (i == gSeqs.length - 4) {
                lineRead = "CTAGUCTAA";
            } else if (i == gSeqs.length - 3) {
                lineRead = "TTAAATTCCTTTAGG";
            }  else if (i == gSeqs.length - 2) {
                lineRead = "AGTCCGATCCGT";
            }  else if (i == gSeqs.length - 1) {
                lineRead = "AAAAAATTTTTTGGGGGGCCCCCC";
            } else {
                while (rawLines[k].equals("X")) {
                    input.nextLine();
                    k++;
                }
                lineRead = input.nextLine();
            }
            gSeqs[i] = new List(null);
            System.out.printf("\nDNA sequence %d: %s\n", i + 1, lineRead);
            if(gSeqs[i].storeStrand(lineRead, true)) {
                System.out.print("\n");

                // Point #7

                stTime = System.nanoTime();

                if(isGeneticPalindrome(gSeqs[i], 0, (gSeqs[i].length() - 1))) {
                    System.out.printf("\"%s\" is a genetic palindrome. ", lineRead);
                } else {
                    System.out.printf("\"%s\" is not a palindromic sequence. ", lineRead);
                }

                enTime = System.nanoTime();

                long timeSequenceTest = (enTime - stTime) * 1000000;

                // Points #5 and #6
                occurrences[i] = new int[gSeqs[i].length()];
                for(int j = 0; j < occurrences[i].length; j++)
                    occurrences[i][j] = 0;

                stTime = System.nanoTime();

                foundPlndrms[i] = findGeneticPalindromes(gSeqs[i], i);

                enTime = System.nanoTime();

                if(foundPlndrms[i].length() == 0 ||
                        (isGeneticPalindrome(gSeqs[i], 0, gSeqs[i].length() - 1) &&
                                foundPlndrms[i].length() == gSeqs[i].length())) {
                    System.out.print("\nThis sequence does not contain any genetic palindromes.");
                } else {
                    System.out.print("\n");
                }
                if(foundPlndrms[i].length() != 0)
                    System.out.print("Contents of palindrome storing linked-list: ");
                for(int j = 0; j < foundPlndrms[i].length(); j++)
                    System.out.print(foundPlndrms[i].getNth(j));
                System.out.print("\n");

                System.out.printf("\ntime to test whether a genomic subsequence in Sequence %d is palindromic: " +
                                "%d milliseconds\ntime to find all palindromic subsequences of length [4, 17] in " +
                                "Sequence %d: %d milliseconds", i + 1, timeSequenceTest, i + 1,
                        (enTime - stTime) * 1000000);

                System.out.print("\n");
            } else {
                gSeqs[i] = new List(new Node('X', null, null));
            }
        }


        stTime = System.nanoTime();

        countSimilarGeneticPalindromes();

        enTime = System.nanoTime();

        System.out.print("\n\n");

        int cCountIndex = 0;
        int loopTimes = foundPlndrmsCombined.getNth(foundPlndrmsCombined.length() - 1) == '-' ?
                foundPlndrmsCombined.length() - 1 : foundPlndrmsCombined.length();

        StringBuilder toPrint = new StringBuilder("\"");

        for(int i = 0; i < loopTimes; i++) {
            if (foundPlndrmsCombined.getNth(i) != '-') {
                toPrint.append(foundPlndrmsCombined.getNth(i));
            } else {
                if (cumulativeCount[cCountIndex] != 1) {
                    System.out.printf("%s\" has %d similar sequences in the file, and\n" +
                                    "it took %d milliseconds to determine that.\n\n", toPrint.toString(),
                            cumulativeCount[cCountIndex], (enTime - stTime) * 1000000);
                }
                toPrint = new StringBuilder("\"");
                cCountIndex++;
            }
            if (i == loopTimes - 1) {
                if (cumulativeCount[cCountIndex] != 1) {
                    System.out.printf("%s\" has %d similar sequences in the file, and\n" +
                                    "it took %d milliseconds to determine that.\n", toPrint.toString(),
                            cumulativeCount[cCountIndex], (enTime - stTime) * 1000000);
                }
            }

        }

        // Point #9
        System.out.print("\nFirst Genomic Sequence (Before Sorting): ");
        int firstIndex = 0;
        for(int i = 0; i < gSeqs.length; i++) {
            if (gSeqs[i].getNth(0) != 'X') {
                firstIndex = i;
                break;
            }
        }
        for (int i = 0; i < gSeqs[firstIndex].length(); i++) {
            System.out.print(gSeqs[firstIndex].getNth(i));
        }

        System.out.print("\nFirst Genomic Sequence (After Sorting):  ");

        gSeqs[firstIndex].insertSort();

        for (int i = 0; i < gSeqs[firstIndex].length(); i++) {
            System.out.print(gSeqs[firstIndex].getNth(i));
        }


        System.out.print("\n\nLast Genomic Sequence (Before Sorting): ");
        int lastIndex = 0;
        for(int i = gSeqs.length - 6; i >= 0; i--) {
            if (gSeqs[i].getNth(0) != 'X') {
                lastIndex = i;
                break;
            }
        }
        for (int i = 0; i < gSeqs[lastIndex].length(); i++) {
            System.out.print(gSeqs[lastIndex].getNth(i));
        }

        System.out.print("\nLast Genomic Sequence (After Sorting):  ");

        gSeqs[lastIndex].insertSort();

        for (int i = 0; i < gSeqs[lastIndex].length(); i++) {
            System.out.print(gSeqs[lastIndex].getNth(i));
        }

    }

    /**
     * Tests a chunk from a list of characters and determines whether the chunk is a genetic palindrome or not
     * @param toCheck receives the list (part of) which has to be tested with genetic palindrome criteria
     * @param first receives the chunk to be tested's starting index
     * @param last receives the chunk to be tested's ending index
     * @return true if the chunk is a genetic palindrome; false otherwise
     */
    private static boolean isGeneticPalindrome(List toCheck, int first, int last) {
        if(((first + last + 1) % 2 != 0) || last < first)
            return false;
        for(int i = first; i <= ((first + last) / 2); i++) {
            if(toCheck.getNth(i) == 'A') {
                if (!(toCheck.getNth(last - i + first) == 'T' || toCheck.getNth(last - i + first) == 'U'))
                    return false;
            }
            if(toCheck.getNth(i) == 'T' || toCheck.getNth(i) == 'U') {
                if (!(toCheck.getNth(last - i + first) == 'A'))
                    return false;
            }
            if(toCheck.getNth(i) == 'C') {
                if (!(toCheck.getNth(last - i + first) == 'G'))
                    return false;
            }
            if(toCheck.getNth(i) == 'G') {
                if (!(toCheck.getNth(last - i + first) == 'C'))
                    return false;
            }
        }
        return true;
    }

    /**
     * Finds all genetic palindromic subsequences of length [4, 17] in the received list
     * @param toFindFrom receives the list from which genetic palindromes are to be found
     * @param indexInMain receives index of gSeqs array which is received in toFindFrom
     * @return a list containing all genetic palindromic subsequences of length [4, 17] separated by '-' (dashes)
     */
    private static List findGeneticPalindromes(List toFindFrom, int indexInMain) {
        if (toFindFrom == null || indexInMain < 0) {
            System.out.println("Unexpected arguments passed to findGeneticPalindrom Method\n" +
                    "The program will therefore exit");
            System.exit(4);
        }
        List toReturn = new List(null);
        boolean lenPrinted;
        boolean msgPrinted = false;
        int dashesInToRet = 0;
        for(int i = 4; i < 17; i += 2) {
            lenPrinted = false;
            for(int j = 0; j < toFindFrom.length() - i + 1; j++) {
                if(isGeneticPalindrome(toFindFrom, j, j + i - 1)) {
                    if(!msgPrinted && toFindFrom.length() != 4) {
                        System.out.print("However, it has the following genetic palindromes of length ");
                        msgPrinted = true;
                    }
                    int existnceIndex = existsAlready(toReturn, toFindFrom, j, j + i - 1);
                    if(existnceIndex != -1) {
                            countSimilarPlndrmsInASingleGenome(existnceIndex, indexInMain);
                        continue;
                    }
                    if(toReturn.length() != 0) {
                        toReturn.append(new List(new Node('-', null, null)));
                        dashesInToRet++;
                    }
                    countSimilarPlndrmsInASingleGenome(dashesInToRet, indexInMain);
                    if(!lenPrinted && toFindFrom.length() != 4 && (toFindFrom.length() != 16 || i != 16)) {
                        System.out.printf("> %d: ", i);
                        lenPrinted = true;
                    }
                    for(int k = j; k < j + i; k++) {
                        char thisChar = toFindFrom.getNth(k);
                        toReturn.append(new List(new Node(thisChar, null, null)));
                        if(toFindFrom.length() != 4 && (toFindFrom.length() != 16 || i != 16))
                            System.out.print(thisChar);
                    }
                    if(lenPrinted)
                        System.out.print(" ");
                }
            }
        }
        return toReturn;
    }

    /**
     * Counts (adds up) no. of similar genetic palindromes among lists containing genetic palindromes for individual
     * sequences
     */
    private static void countSimilarGeneticPalindromes() {

        foundPlndrmsCombined = new List(null);
        int determinedSize = 0;
        for (int i = 0; i < foundPlndrms.length; i++) {
            if (foundPlndrms[i] == null)
                break;
            determinedSize += foundPlndrms[i].length();
        }
        cumulativeCount = new int[determinedSize];

        int st;
        int existnceIndex;
        int ocrnceIIndex;
        int cCountIndex = 0;
        List toAppend = new List(null);

        for (int i = 0; i < foundPlndrms.length; i++) {
            if(foundPlndrms[i] != null) {
                ocrnceIIndex = 0;
                st = 0;
                for (int j = 0; j < foundPlndrms[i].length(); j++) {
                    if (foundPlndrms[i].getNth(j) == '-') {
                        existnceIndex = existsAlready(foundPlndrmsCombined, foundPlndrms[i], st, j - 1);
                        st = j + 1;
                        if (existnceIndex == -1) {
                            cumulativeCount[cCountIndex] += occurrences[i][ocrnceIIndex];
                            cCountIndex++;
                            toAppend.append(new List(new Node('-', null, null)));
                            foundPlndrmsCombined.append(toAppend);
                            toAppend = new List(null);
                        } else {
                            cumulativeCount[existnceIndex] += occurrences[i][ocrnceIIndex];
                            toAppend = new List(null);
                        }
                        ocrnceIIndex++;
                    }
                    if (j == foundPlndrms[i].length() - 1) {
                        toAppend.append(new List(new Node(foundPlndrms[i].getNth(j), null, null)));
                        existnceIndex = existsAlready(foundPlndrmsCombined, foundPlndrms[i], st, j);
                        if (existnceIndex == -1) {
                            cumulativeCount[cCountIndex] += occurrences[i][ocrnceIIndex];
                            cCountIndex++;
                            if (i != foundPlndrms.length -1) {
                                if (foundPlndrms[i + 1] != null)
                                    toAppend.append(new List(new Node('-', null, null)));
                            }
                            foundPlndrmsCombined.append(toAppend);
                            toAppend = new List(null);
                        } else {
                            cumulativeCount[existnceIndex] += occurrences[i][ocrnceIIndex];
                            toAppend = new List(null);
                        }
                    }
                    if (j != foundPlndrms[i].length() - 1 && foundPlndrms[i].getNth(j) != '-')
                        toAppend.append(new List(new Node(foundPlndrms[i].getNth(j), null, null)));
                }
            }
        }

    }

    /**
     * Checks for existence of a chuck from a list in another list
     * @param toChkIn receives the list which has to be checked for existence of a genetic palindrome from toMtchFrom
     * @param toMtchFrom receives the list (part of) which has to be checked for existence in toChkIn
     * @param first receives the chunk to be tested's starting index
     * @param last receives the chunk to be tested's ending index
     * @return if found, no. of dashes encountered in toChkIn before the matching fragment; -1 otherwise
     */
    private static int existsAlready(List toChkIn, List toMtchFrom, int first, int last) {
        if (toChkIn == null || toMtchFrom == null || last < first) {
            System.out.println("Unexpected arguments passed to existsAlready Method\n" +
                    "The program will therefore exit");
            System.exit(6);
        }

        StringBuilder toMtchWithBuilder = new StringBuilder("");
        for(int i = first; i <= last; i++) {
            toMtchWithBuilder.append(toMtchFrom.getNth(i));
        }
        String toMtchWith = toMtchWithBuilder.toString();

        int dashesInToChkIn = 0;
        boolean match;
        char inToMtchWith;
        StringBuilder chunkBuilder = new StringBuilder("");
        String chunk;

        for (int i = 0; i < toChkIn.length(); i++) {
            if (toChkIn.getNth(i) == '-' || i == toChkIn.length() - 1) {
                if(i == toChkIn.length() - 1 && toChkIn.getNth(i) != '-')
                    chunkBuilder.append(toChkIn.getNth(i));
                chunk = chunkBuilder.toString();
                if(toMtchWith.length() == chunk.length()) {
                    if(toMtchWith.equals(chunk))
                        return dashesInToChkIn;
                    match = true;
                    for (int j = 0; j < chunk.length(); j++) {
                        if(!((toMtchWith.charAt(j) == 'T' && chunk.charAt(j) == 'U') ||
                                (toMtchWith.charAt(j) == 'U' && chunk.charAt(j) == 'T') ||
                                (toMtchWith.charAt(j) == chunk.charAt(j)))) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) {
                        match = true;
                        for (int j = 0; j < chunk.length(); j++) {
                            if (!(((toMtchWith.charAt(j) == 'A') && (chunk.charAt(j) == 'T' || chunk.charAt(j) == 'U')) ||
                                    ((toMtchWith.charAt(j) == 'T' || toMtchWith.charAt(j) == 'U') &&
                                            (chunk.charAt(j) == 'A')) || (toMtchWith.charAt(j) == 'C' &&
                                    chunk.charAt(j) == 'G') || (chunk.charAt(j) == 'C' && toMtchWith.charAt(j) == 'G'))) {
                                match = false;
                                break;
                            }
                        }
                    }
                    if(match)
                        return dashesInToChkIn;
                }
                chunkBuilder = new StringBuilder("");
                dashesInToChkIn++;
            } else {
                chunkBuilder.append(toChkIn.getNth(i));
            }
        }



        return -1;

    }

    /**
     * Increments an index in occurrences global array
     * @param indexToIncrmnt receives the no. of dashes in foundPlndrms before the palindrome that this index represents
     * @param indexInMain receives the index of foundPlndrms that holds palindromes from a specific genetic sequence
     */
    private static void countSimilarPlndrmsInASingleGenome(int indexToIncrmnt, int indexInMain) {
        if (indexInMain < 0 || indexToIncrmnt < 0) {
            System.out.println("Unexpected arguments passed to countSimilarPlndrmsInASingleGenome Method\n" +
                    "The program will therefore exit");
            System.exit(5);
        }

        occurrences[indexInMain][indexToIncrmnt]++;
    }

}

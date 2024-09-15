import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Random;

public class MotifFinder {
    public static void main(String[] args) throws Exception {
        Random random = new Random();
        int n = 1000 + random.nextInt(2000000 - 1000 + 1);
        int m = 5 + random.nextInt(100 - 5 + 1);
        double[] probabilities = { 0.25, 0.25, 0.25, 0.25 };
        int s = random.nextInt(7) + 4;

        ArrayList<String> dataBase = generateSequences(n, m, probabilities);
        try {
            saveSequencesInTxt(dataBase, "dataBaseSequences.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> motifs = findMotif(dataBase, s);

        System.out.println(motifs);

        double maxEntropy = 0.0;

        String maxEntropySequence = ""; 
        for (String sequence : dataBase) {
            double entropy = calculateEntropy(sequence);
            if (entropy > maxEntropy) {
                maxEntropy = entropy;
                maxEntropySequence = sequence;
            }
        }
        System.out.println("Sequence with highest entropy: " + maxEntropySequence + ", Entropy: " + maxEntropy);

    }

    public static ArrayList<String> generateSequences(int n, int m, double[] probabilities) {
        Random random = new Random();
        ArrayList<String> dataBase = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            StringBuilder sequence = new StringBuilder();
            for (int j = 0; j < m; j++) {
                int baseIndex = getIndexBase(random, probabilities);
                char base = getBase(baseIndex);
                sequence.append(base);
            }
            dataBase.add(sequence.toString());

        }
        return dataBase;

    }

    public static int getIndexBase(Random random, double[] probabilities) {
        double rand = random.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (rand <= sum) {
                return i;
            }
        }
        return -1;

    }

    public static char getBase(int baseIndex) {
        switch (baseIndex) {
            case 0:
                return 'A';
            case 1:
                return 'C';
            case 2:
                return 'G';
            case 3:
                return 'T';
            default:
                return 'N';
        }

    }

    public static void saveSequencesInTxt(ArrayList<String> database, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName); // Save the file
        try {
            for (String sequence : database) {
                writer.write(sequence + "\n");
            }
        } finally {
            writer.close();
        }
    }

    public static ArrayList<String> loadSequencesFromTxt(String fileName) throws IOException {

        ArrayList<String> database = new ArrayList<>();
        FileReader reader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            database.add(line);
        }
        bufferedReader.close();
        return database;
    }

    public static ArrayList<String> findMotif(ArrayList<String> database, int s) {
        HashMap<String, Integer> motifCounts = new HashMap<>();
        for (String sequence : database) {
            for (int i = 0; i <= sequence.length() - s; i++) {
                String motif = sequence.substring(i, i + s);
                motifCounts.put(motif, motifCounts.getOrDefault(motif, 0) + 1);
            }
        }

        int maxCount = getMaxCount(motifCounts);
        ArrayList<String> motifs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : motifCounts.entrySet()) {
            if (entry.getValue() == maxCount) {
                motifs.add(entry.getKey());
            }
        }
        return motifs;
    }

    public static int getMaxCount(HashMap<String, Integer> motifCounts) {
        int maxCount = 0;
        for (int count : motifCounts.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return maxCount;
    }

    public static double calculateEntropy(String sequence) {
        int[] counts = new int[4]; // A, C, G, T
        for (char base : sequence.toCharArray()) {
            switch (base) {
                case 'A':
                    counts[0]++;
                    break;
                case 'C':
                    counts[1]++;
                    break;
                case 'G':
                    counts[2]++;
                    break;
                case 'T':
                    counts[3]++;
                    break;
            }
        }

        double entropy = 0.0;
        int length = sequence.length();
        for (int count : counts) {
            if (count > 0) {
                double frequency = (double) count / length;
                entropy -= frequency * Math.log(frequency) / Math.log(2);
            }
        }
        return entropy;
    }

}
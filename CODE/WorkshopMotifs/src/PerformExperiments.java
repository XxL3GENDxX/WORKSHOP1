import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformExperiments {
    public static void main(String[] args) {
        List<ExperimentResult> results = new ArrayList<>();
        for (int n : new int[]{1000, 5000, 10000}) {
            for (int m : new int[]{50, 100}) {
                for (double[] probabilities : new double[][]{{0.25, 0.25, 0.25, 0.25}, {0.3, 0.2, 0.3, 0.2}}) {
                    for (int s : new int[]{4, 5, 6}) {
                        ArrayList<String> database = MotifFinder.generateSequences(n, m, probabilities);
                        
                        long startTime = System.nanoTime();
                        ArrayList<String> motifs = MotifFinder.findMotif(database, s);
                        long endTime = System.nanoTime();
                        
                        long elapsedTime = endTime - startTime;
                        
                        int[] motifCounts = getMotifCounts(database, motifs);
                        results.add(new ExperimentResult(n, m, probabilities, s, motifs, motifCounts, elapsedTime));
                    }
                }
            }
        }
        
        try {
            saveResultsToCSV(results, "experiment_results.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ExperimentResult {
        public int dataBaseSize;
        public int sequenceLength;
        public double[] probabilities;
        public int motifSize;
        public ArrayList<String> motifs;
        public int[] motifCounts;
        public long elapsedTime;

        public ExperimentResult(int dataBaseSize, int sequenceLength, double[] probabilities, int motifSize,
                                ArrayList<String> motifs, int[] motifCounts, long elapsedTime) {
            this.dataBaseSize = dataBaseSize;
            this.sequenceLength = sequenceLength;
            this.probabilities = probabilities;
            this.motifSize = motifSize;
            this.motifs = motifs;
            this.motifCounts = motifCounts;
            this.elapsedTime = elapsedTime;
        }
    }

    public static int[] getMotifCounts(ArrayList<String> database, ArrayList<String> motifs) {
        int[] motifCounts = new int[motifs.size()];
        for (int i = 0; i < motifs.size(); i++) {
            motifCounts[i] = countMotifOccurrences(database, motifs.get(i));
        }
        return motifCounts;
    }

    public static int countMotifOccurrences(ArrayList<String> database, String motif) {
        int count = 0;
        for (String sequence : database) {
            count += countMotifOccurrencesInSequence(sequence, motif);
        }
        return count;
    }

    public static int countMotifOccurrencesInSequence(String sequence, String motif) {
        int count = 0;
        int index = sequence.indexOf(motif);
        while (index != -1) {
            count++;
            index = sequence.indexOf(motif, index + 1);
        } 
        return count;
    }

    public static void saveResultsToCSV(List<ExperimentResult> results, String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("Database Size,Sequence Length,Probabilities,Motif Size,Motifs,Motif Counts,Elapsed Time (ns)\n");
        for (ExperimentResult result : results) {
            writer.write(result.dataBaseSize + "," + result.sequenceLength + "," + arrayToString(result.probabilities) + "," +
                    result.motifSize + ",\"" + listToString(result.motifs) + "\",\"" + arrayToString(result.motifCounts) + "\"," +
                    result.elapsedTime + "\n");
        }
        writer.close();
    }

    public static String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        for (double d : array) {
            sb.append(d).append(" ");
        }
        return sb.toString().trim();
    }

    public static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i : array) {
            sb.append(i).append(" ");
        }
        return sb.toString().trim();
    }

    public static String listToString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }
}
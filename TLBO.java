/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlbo;

import static java.util.Arrays.stream;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import static java.util.concurrent.ThreadLocalRandom.current;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author User
 */
public class TLBO {

    /**
     * @param args the command line arguments
     */
    int PopulationCount;
    int MaxIteration;
    private double solveTime;
    private List<Student> students;
    private Student currentBestSolution = null;
    private double currentBestSolutionFitValue = Double.MAX_VALUE;
    Random random = new Random();
    private double RangeMin;
    private double RangeMax;
    private double F_Min;
    private int NumberOf_Dimensions;
    private int Iteration = 0;

    public static void main(String[] args) throws Exception {
        double sum_best_sol = 0;
        double sum_best_sol_STD = 0;
        double[] best_Array_STD = new double[100];
        for (int i = 0; i < 100; i++) {
            TLBO T = new TLBO(10, 1000, 100, -100, 100, 0);
            double temp = T.runAlgorythm();
            sum_best_sol = sum_best_sol + temp;
            best_Array_STD[i] = temp;
            System.out.println(i + " cost of best solu is (" + temp + ")");
            
        }
          double Maen = sum_best_sol / 100;
        for (int i = 0; i < 100; i++) {
         sum_best_sol_STD = sum_best_sol_STD + Math.pow((best_Array_STD[i] - Maen), 2);
        }
      
        double STD = Math.sqrt(sum_best_sol_STD / 100);
        System.out.println("------------------------");
        System.out.println(" Maen =  (" + Maen + ")");
        System.out.println("**************************** ");
        System.out.println(" STD =   (" + STD + ")");
        System.out.println("**************************** ");

    }

    public TLBO(int PopulationCount, int MaxIteration, int NumberOf_Dimensions, double RangeMin, double RangeMax, double F_Min) {

        students = Stream.generate(() -> new Student(NumberOf_Dimensions, RangeMin, RangeMax, F_Min)).limit(PopulationCount).collect(toList());
        this.PopulationCount = PopulationCount;
        this.MaxIteration = MaxIteration;
        this.RangeMax = RangeMax;
        this.RangeMin = RangeMin;
        this.F_Min = F_Min;
        this.NumberOf_Dimensions = NumberOf_Dimensions;

    }

    public double runAlgorythm() throws Exception {

        final long startTime = System.currentTimeMillis();

        currentBestSolution = findBestStudent();
        currentBestSolutionFitValue = currentBestSolution.getCost();

        while (Iteration < MaxIteration) {

            assert currentBestSolution != null;
            teacherPhase();

            learnerPhase();
            
            Iteration++;
        }
        setSolveTime((System.currentTimeMillis() - startTime) / 1000.0);
        //printResults();

        return currentBestSolution.getCost();
    }

    private void teacherPhase() {
        Random randomGenerator = new Random();
        IntStream.range(0, getPopulationCount()).forEach(i -> {
            double solutionfit = students.get(i).getCost();
            Student newSolution = new Student(NumberOf_Dimensions, RangeMin, RangeMax, F_Min);
            double Means[] = calculateMean();
            IntStream.range(0, getNumberOf_Dimensions()).forEach(j -> {
                double Tf = randomIntInRange(1, 2);
                double differenceMean = randomGenerator.nextDouble() * (currentBestSolution.getSolution()[j] - Tf * Means[j]);
                newSolution.updateSolution(j, students.get(i).getSolution()[j] + differenceMean); // jomaa test

            });
            newSolution.recomputeCost();
            double newSolutionFit = newSolution.getCost();
            if (newSolutionFit < solutionfit) {

                students.set(i, newSolution);
                if (newSolutionFit < currentBestSolutionFitValue) {
                    currentBestSolution = newSolution;

                    currentBestSolutionFitValue = newSolutionFit;
                }
            }

        });
    }

    private double[] calculateMean() {
        double[] means = new double[NumberOf_Dimensions];

        IntStream.range(0, getPopulationCount())
                .forEach(i -> IntStream.range(0, NumberOf_Dimensions)
                        .forEach(j -> {
                            double[] solution = students.get(i).getSolution();
                            means[j] = means[j] + solution[j];
                        }));
        return stream(means).map(d -> d / getPopulationCount()).toArray();
        //jomaa tsest
    }

    private void learnerPhase() {
        Random randomGenerator = new Random();
        for (int i = 0; i < getPopulationCount(); i++) {
            Student solution = students.get(i);
            Student randomSolu;
            while ((randomSolu = students.get(random.nextInt(getPopulationCount()))) == solution) ;
            final Student randomSolution = randomSolu;

            double randomSolutionFitValue = randomSolution.getCost();
            double currentSolutionFitValue = solution.getCost();
            Student newSolution = new Student(NumberOf_Dimensions, RangeMin, RangeMax, F_Min);
            if (randomSolutionFitValue < currentSolutionFitValue) {
                forEachSolution.accept((j)
                        -> newSolution.updateSolution(j, solution.getSolution()[j] + randomGenerator.nextDouble() * (randomSolution.getSolution()[j] - (solution.getSolution()[j]))));
            } else {
                forEachSolution.accept((j)
                        -> newSolution.updateSolution(j, solution.getSolution()[j] + randomGenerator.nextDouble() * (solution.getSolution()[j] - randomSolution.getSolution()[j])));
            }
            newSolution.recomputeCost();
            double newSolutionFit = newSolution.getCost();

            if (newSolutionFit < currentSolutionFitValue) {
                students.set(i, newSolution);
                if (newSolutionFit < currentBestSolutionFitValue) {
                    currentBestSolution = newSolution;

                    currentBestSolutionFitValue = newSolutionFit;
                }
            }
        }
    }
    private Consumer<Consumer<Integer>> forEachSolution = (consumer) -> IntStream.range(0, getNumberOf_Dimensions()).forEach(consumer::accept);

    private Student findBestStudent() throws Exception {

        return students.stream()
                .map(p -> new Object[]{p.getCost(), p})
                .min(Comparator.comparingDouble(o -> (Double) o[0]))
                .map(o -> {
                    currentBestSolutionFitValue = (Double) o[0];
                    return (Student) o[1];
                })
                .orElseThrow(Exception::new);

    }

    public void printResults() {
        System.out.println("bestSolutionCost: " + (currentBestSolutionFitValue) + "   fitness = " + (1.00 / (currentBestSolutionFitValue)));
        System.out.println("bestSolutionSoFar: " + currentBestSolution.getSolution().toString());
        System.out.println("Time of bestSolution: " + getSolveTime() + "s");
        /* for (int i = 0; i < getNumberOf_Dimensions(); i++) {
         System.out.println("VM : " + i + " Cloudlets : " + currentBestSolution.getSolution()[i]);
         }*/

    }

    private Integer randomIntInRange(int from, int to) {
        return current().nextInt(from, to + 1);
    }

    public int getPopulationCount() {
        return PopulationCount;
    }

    public void setPopulationCount(int PopulationCount) {
        this.PopulationCount = PopulationCount;
    }

    public int getMaxIteration() {
        return MaxIteration;
    }

    public double getSolveTime() {
        return solveTime;
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student getCurrentBestSolution() {
        return currentBestSolution;
    }

    public Double getCurrentBestSolutionFitValue() {
        return currentBestSolutionFitValue;
    }

    public Random getRandom() {
        return random;
    }

    public double getRangeMin() {
        return RangeMin;
    }

    public double getRangeMax() {
        return RangeMax;
    }

    public double getF_Min() {
        return F_Min;
    }

    public int getNumberOf_Dimensions() {
        return NumberOf_Dimensions;
    }

    public void setMaxIteration(int MaxIteration) {
        this.MaxIteration = MaxIteration;
    }

    public void setSolveTime(double solveTime) {
        this.solveTime = solveTime;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setCurrentBestSolution(Student currentBestSolution) {
        this.currentBestSolution = currentBestSolution;
    }

    public void setCurrentBestSolutionFitValue(Double currentBestSolutionFitValue) {
        this.currentBestSolutionFitValue = currentBestSolutionFitValue;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setRangeMin(double RangeMin) {
        this.RangeMin = RangeMin;
    }

    public void setRangeMax(double RangeMax) {
        this.RangeMax = RangeMax;
    }

    public void setF_Min(double F_Min) {
        this.F_Min = F_Min;
    }

    public void setNumberOf_Dimensions(int NumberOf_Dimensions) {
        this.NumberOf_Dimensions = NumberOf_Dimensions;
    }

}

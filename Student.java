/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlbo;

import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Clock;
import java.util.Random;
import java.util.stream.IntStream;
import static jdk.nashorn.internal.objects.NativeMath.round;

/**
 *
 * @author User
 */
public class Student {

    private double[] Solution;
    private double RangeMin;
    private double RangeMax;
    private double F_Min;
    private int NumberOf_Dimensions;
    private Random random;
    private double LastCost;

    public Student(int NumberOf_Dimensions, double RangeMin, double RangeMax, double F_Min) {
        this.Solution = new double[NumberOf_Dimensions];
        this.RangeMin = RangeMin;
        this.RangeMax = RangeMax;
        this.F_Min = F_Min;
        this.NumberOf_Dimensions = NumberOf_Dimensions;
        Random random = new Random();
        generateSolution();
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Sphere() {
        double sum = 0;
        for (int i = 0; i < NumberOf_Dimensions; i++) {
            sum = (sum + Math.pow(Solution[i], 2));
        }
        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Quadric() {
        double sum = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            double temp = 0;
            for (int j = 0; j < i; j++) {

                temp = (temp + Solution[j]);

            }

            sum = sum + Math.pow(temp, 2);
        }

        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_SumSquare() {
        double sum = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            sum = sum + (Math.pow(Solution[i], 2) * (i + 1));
        }

        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Zakharove() {
        double sum = 0, temp1 = 0, temp2 = 0, temp3 = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            temp1 = temp1 + (Math.pow(Solution[i], 2));
        }
        for (int i = 0; i < NumberOf_Dimensions; i++) {
            temp2 = temp2 + (0.5 * (i + 1) * Solution[i]);
        }
        temp2 = Math.pow(temp2, 2);

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            temp3 = temp3 + (0.5 * (i + 1) * Solution[i]);
        }
        temp3 = Math.pow(temp2, 4);
        sum = temp1 + temp2 + temp3;
        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Rosenbrock() {
        double sum = 0, temp1 = 0, temp2 = 0;

        for (int i = 0; i < NumberOf_Dimensions - 1; i++) {
            temp1 = 0;
            temp2 = 0;
            temp1 = 100 * (Math.pow(Math.pow(Solution[i], 2) - Solution[i + 1], 2));
            temp2 = Math.pow(Solution[i] - 1, 2);
            sum = sum + temp1 + temp2;

        }

        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Ackley() {
        double sum = 0, temp1 = 0, temp2 = 0;

        for (int i = 0; i < NumberOf_Dimensions - 1; i++) {
            temp1 = temp1 + Math.pow(Solution[i], 2);
            temp2 = temp2 + Math.cos(2 * (22 / 7) * Solution[i]);

        }
        double temp1_1 = (Math.exp((-1 / 5) * (Math.sqrt(temp1 / NumberOf_Dimensions))));
        double temp2_2 = Math.exp(temp2 / NumberOf_Dimensions);
        sum = 20 - 20 * temp1_1 - temp2_2 + 2.7;
        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Rastrigin() {
        double sum = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {

            sum = sum + (Math.pow(Solution[i], 2) - (10 * Math.cos(2 * (22 / 7) * Solution[i])) + 10);
        }

        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Weierstrass() {
        double sum = 0, temp1 = 0, temp2 = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            temp1 = 0;
            for (int k = 0; k < 20; k++) {
                temp1 = temp1 + Math.pow(0.5, k) * Math.cos(2 * (22 / 7) * Math.pow(3, k) * (Solution[i] + 0.5));
            }
            sum = sum + temp1;
        }

        for (int k = 0; k < 20; k++) {
            temp2 = temp2 + Math.pow(0.5, k) * Math.cos(2 * (22 / 7) * Math.pow(3, k) * (0.5));
        }
        sum = sum - NumberOf_Dimensions * temp2;
        return sum;
    }

    /**
     * ****************************************************************************
     */
    public double Polynomials_Griewank() {
        double sum = 0, temp1 = 0, temp2 = 0;

        for (int i = 0; i < NumberOf_Dimensions; i++) {
            temp1 = temp1 + (Math.pow(Solution[i], 2) / 4000);
            temp2 = temp2 + (Math.cos(Solution[i] / Math.sqrt(i + 1)));
        }
        sum = temp1 + temp2 + 1;
        return sum;
    }

    /**
     * ****************************************************************************
     */
    public void updateSolution(int Index_Cloudlet, double Value) {

        if (Value < RangeMin || Value > RangeMax) {
            double tempvalue_VM = randomDouble(RangeMin, RangeMax);
            Solution[Index_Cloudlet] = tempvalue_VM;
        } else {

            Solution[Index_Cloudlet] = Value;

        }

    }

    public double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    private void generateSolution() {

        IntStream.range(0, NumberOf_Dimensions)
                .forEach(j -> {
                    double randomvalue = randomDouble(RangeMin, RangeMax);

                    Solution[j] = randomvalue;

                });
        recomputeCost();

    }

    public void recomputeCost() {
        LastCost = Polynomials_Sphere();
        //LastCost = Polynomials_Quadric();
        //LastCost = Polynomials_SumSquare();
        //LastCost = Polynomials_Zakharove();
        //LastCost = Polynomials_Rosenbrock();
        //LastCost = Polynomials_Ackley();
        //LastCost = Polynomials_Rastrigin();
        

//LastCost = Polynomials_Weierstrass();
        //LastCost = Polynomials_Griewank();
    }

    public double getCost() {
        return LastCost;
    }

    public double[] getSolution() {
        return Solution;
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

    public double getNumberOf_Dimensions() {
        return NumberOf_Dimensions;
    }

}

package com.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Generates a realistic college: 10,000+ students with unique 8-digit registration numbers.
// Format: YY (admission year) + BB (programme code) + SSSS (serial inside that year and programme)
final class SampleData {

    private static final String[] FIRST = {"Ananya", "Rohit", "Sneha", "Amit", "Priya", "Subham", "Smruti", "Debasish",
            "Lipsa", "Biswajit", "Pritam", "Sasmita", "Manas", "Itishree", "Rakesh", "Swati", "Soumya", "Abhisek",
            "Pragyan", "Jyoti", "Chinmay", "Rashmi", "Satyajit", "Madhusmita", "Tapan", "Nandini", "Bikash", "Kavya",
            "Arjun", "Meera", "Rahul", "Pooja", "Karan", "Divya", "Sandeep", "Anjali", "Vikram", "Neha", "Ritesh", "Sonal"};
    private static final String[] LAST = {"Mishra", "Das", "Patra", "Nayak", "Sahoo", "Behera", "Panda", "Mohanty",
            "Swain", "Pradhan", "Rout", "Jena", "Dash", "Samal", "Parida", "Mahapatra", "Sethi", "Tripathy", "Biswal",
            "Satpathy", "Sharma", "Singh", "Kumar", "Gupta", "Roy"};
    private static final String[] COURSES = {"BTech", "BCA", "MBA", "MCA", "BBA"};
    private static final String[] CODES = {"02", "01", "04", "03", "05"};
    private static final double[] WEIGHTS = {0.38, 0.22, 0.16, 0.14, 0.10};
    private static final int[] YEARS = {23, 24, 25, 26};

    private SampleData() { }

    static List<Student> generate(int count) {
        Random rnd = new Random(42);   // fixed seed: same data every time
        Map<String, Integer> serials = new HashMap<>();
        List<Student> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int b = pickBranch(rnd);
            int yy = YEARS[rnd.nextInt(YEARS.length)];
            int serial = serials.merge(yy + CODES[b], 1, Integer::sum);

            String regNo = String.format("%02d%s%04d", yy, CODES[b], serial);
            String first = FIRST[rnd.nextInt(FIRST.length)];
            String last = LAST[rnd.nextInt(LAST.length)];
            String email = (first + "." + last + "." + regNo + "@students.example.edu").toLowerCase();
            int sem = 2 * (26 - yy) + 1;   // 2026 joiners are in semester 1, 2023 joiners in semester 7
            double cgpa = Math.round(clamp(7.4 + rnd.nextGaussian(), 4.5, 10) * 100) / 100.0;
            int attendance = (int) clamp(Math.round(82 + rnd.nextGaussian() * 10), 40, 100);

            list.add(new Student(regNo, first + " " + last, email, COURSES[b], sem, cgpa, attendance));
        }
        return list;
    }

    private static int pickBranch(Random rnd) {
        double r = rnd.nextDouble(), sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            sum += WEIGHTS[i];
            if (r < sum) return i;
        }
        return 0;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}

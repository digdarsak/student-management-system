package com.sms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Numbers for the dashboard cards and charts
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    public record Stats(long total, double avgCgpa, double avgAttendance, long lowAttendance,
                        Map<String, Long> byCourse, List<Long> cgpaBuckets, List<Student> toppers) { }

    private final StudentRepository repo;

    public StatsController(StudentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Stats stats() {
        Map<String, Long> byCourse = new LinkedHashMap<>();
        for (Object[] row : repo.countByCourse()) {
            byCourse.put((String) row[0], (Long) row[1]);
        }
        // CGPA bands: below 5, 5-6, 6-7, 7-8, 8-9, 9-10
        double[] edges = {0, 5, 6, 7, 8, 9, 10.01};
        List<Long> buckets = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            buckets.add(repo.countByCgpaGreaterThanEqualAndCgpaLessThan(edges[i], edges[i + 1]));
        }
        Double cgpa = repo.avgCgpa();
        Double att = repo.avgAttendance();
        return new Stats(repo.count(), cgpa == null ? 0 : cgpa, att == null ? 0 : att,
                repo.countByAttendanceLessThan(75), byCourse, buckets, repo.findTop5ByOrderByCgpaDesc());
    }
}

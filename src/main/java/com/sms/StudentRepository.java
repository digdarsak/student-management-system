package com.sms;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByRegNo(String regNo);

    boolean existsByRegNoAndIdNot(String regNo, Long id);

    // One query handles search + programme filter + "attendance alert" filter + paging + sorting.
    // The database returns only ONE page (e.g. 15 rows), never all 10,000.
    @Query("""
            select s from Student s
            where (:q = '' or lower(s.name) like concat('%', :q, '%')
                   or lower(s.regNo) like concat('%', :q, '%')
                   or lower(s.email) like concat('%', :q, '%'))
              and (:course = '' or s.course = :course)
              and (:low = false or s.attendance < 75)
            """)
    Page<Student> search(@Param("q") String q, @Param("course") String course,
                         @Param("low") boolean low, Pageable pageable);

    // Statistics for the dashboard (the database does the counting, not Java)
    @Query("select s.course, count(s) from Student s group by s.course")
    List<Object[]> countByCourse();

    @Query("select avg(s.cgpa) from Student s")
    Double avgCgpa();

    @Query("select avg(s.attendance) from Student s")
    Double avgAttendance();

    long countByAttendanceLessThan(int value);

    long countByCgpaGreaterThanEqualAndCgpaLessThan(double low, double high);

    List<Student> findTop5ByOrderByCgpaDesc();
}

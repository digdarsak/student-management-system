package com.sms;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    public record PageResponse(List<Student> content, long total, int totalPages, int page) { }

    private static final Set<String> SORTABLE = Set.of("regNo", "name", "cgpa", "attendance", "sem");

    private final StudentRepository repo;

    public StudentController(StudentRepository repo) {
        this.repo = repo;
    }

    // GET /api/students?page=0&size=15&q=ananya&course=BCA&lowAtt=false&sort=cgpa&dir=desc
    @GetMapping
    public PageResponse list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "15") int size,
                             @RequestParam(defaultValue = "") String q,
                             @RequestParam(defaultValue = "") String course,
                             @RequestParam(defaultValue = "false") boolean lowAtt,
                             @RequestParam(defaultValue = "regNo") String sort,
                             @RequestParam(defaultValue = "asc") String dir) {
        if (!SORTABLE.contains(sort)) sort = "regNo";   // only allow known columns
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        // "id" as a second sort key keeps page order stable when many students have equal values
        Sort order = Sort.by(direction, sort).and(Sort.by("id"));
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), order);

        Page<Student> result = repo.search(q.trim().toLowerCase(), course, lowAtt, pageable);
        return new PageResponse(result.getContent(), result.getTotalElements(), result.getTotalPages(), result.getNumber());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody Student student) {
        if (repo.existsByRegNo(student.getRegNo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That registration number already belongs to another student.");
        }
        student.setId(null);
        return repo.save(student);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student student) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }
        if (repo.existsByRegNoAndIdNot(student.getRegNo(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That registration number already belongs to another student.");
        }
        student.setId(id);
        return repo.save(student);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }
        repo.deleteById(id);
    }
}

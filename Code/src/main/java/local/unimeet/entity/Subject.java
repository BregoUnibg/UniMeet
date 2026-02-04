package local.unimeet.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // L'anno in cui viene erogata la materia (1, 2, 3, 4, 5)
    @Column(nullable = false)
    private Integer studyYear;

    // Relazione: Molte materie appartengono a un Corso di Laurea
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_course_id", nullable = false)
    private StudyCourse studyCourse;
    public Subject() {}

    public Subject(String name, StudyCourse studyCourse, Integer year) {
        this.name = name;
        this.studyCourse = studyCourse;
        this.studyYear = year;
    }

    // GETTER E SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStudyYear() { return studyYear; }
    public void setStudyYear(Integer studyYear) { this.studyYear = studyYear; }

    public StudyCourse getStudyCourse() { return studyCourse; }
    public void setStudyCourse(StudyCourse studyCourse) { this.studyCourse = studyCourse; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

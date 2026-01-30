package local.unimeet.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "study_courses")
public class StudyCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Relazione: Molti corsi appartengono a un Dipartimento
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Specifica se è BACHELOR, MASTER o Ciclo Unico
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DegreeType degreeType;

    public StudyCourse() {}

    public StudyCourse(String name, Department department, DegreeType degreeType) {
        this.name = name;
        this.department = department;
        this.degreeType = degreeType;
    }

    // GETTER E SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public DegreeType getDegreeType() { return degreeType; }
    public void setDegreeType(DegreeType degreeType) { this.degreeType = degreeType; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyCourse that = (StudyCourse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

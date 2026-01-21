package local.unimeet.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Relazione: Molti dipartimenti appartengono a una Università
    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    public Department() {}

    public Department(String name, University university) {
        this.name = name;
        this.university = university;
    }

    // GETTER E SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public University getUniversity() { return university; }
    public void setUniversity(University university) { this.university = university; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

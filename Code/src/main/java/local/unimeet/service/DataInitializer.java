package local.unimeet.service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.repository.DepartmentRepository;
import local.unimeet.repository.StudyCourseRepository;
import local.unimeet.repository.SubjectRepository;
import local.unimeet.repository.UniversityRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UniversityRepository uniRepo;
    private final DepartmentRepository depRepo;
    private final StudyCourseRepository courseRepo;
    private final SubjectRepository subjectRepo;

    public DataInitializer(UniversityRepository uniRepo, DepartmentRepository depRepo, 
    		StudyCourseRepository courseRepo, SubjectRepository subjectRepo) {
        this.uniRepo = uniRepo;
        this.depRepo = depRepo;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (uniRepo.count() > 0) return; // Se c'è già roba, non fare nulla

        // 1. Università
        University unime = new University("Università degli Studi di Messina");
        University unict = new University("Università degli Studi di Catania");
        uniRepo.saveAll(List.of(unime, unict));

        // 2. Dipartimenti
        Department mift = new Department("MIFT (Scienze Matematiche, Informatiche, Fisiche e della Terra)", unime);
        Department dicam = new Department("DICAM (Civiltà Antiche e Moderne)", unime);
        depRepo.saveAll(List.of(mift, dicam));

        // 3. Corsi di Laurea
        StudyCourse info = new StudyCourse("Informatica", mift, DegreeType.TRIENNALE);
        StudyCourse fisica = new StudyCourse("Fisica", mift, DegreeType.MAGISTRALE);
        StudyCourse lettere = new StudyCourse("Lettere", dicam, DegreeType.CICLO_UNICO);
        courseRepo.saveAll(List.of(info, fisica, lettere));

        // 4. Materie (Subject)
        subjectRepo.saveAll(List.of(
            new Subject("Programmazione I", 1, info),
            new Subject("Analisi Matematica I", 1, info),
            new Subject("Basi di Dati", 1, info),
            new Subject("Fisica Generale", 1, info),
            new Subject("Letteratura Latina", 3, lettere)
        ));

        System.out.println("✅ Database popolato con successo!");
    }
}

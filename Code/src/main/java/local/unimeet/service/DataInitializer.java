package local.unimeet.service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.DepartmentRepository;
import local.unimeet.repository.RoomRepository;
import local.unimeet.repository.StudyCourseRepository;
import local.unimeet.repository.StudySessionRepository; // AGGIUNTO
import local.unimeet.repository.StudyTableRepository; // AGGIUNTO
import local.unimeet.repository.SubjectRepository;
import local.unimeet.repository.UniversityRepository;
import local.unimeet.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UniversityRepository uniRepo;
    private final DepartmentRepository depRepo;
    private final StudyCourseRepository courseRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepo;
    private final RoomRepository roomRepo;
    private final StudyTableRepository tableRepo; // Serve per cancellare i tavoli
    private final StudySessionRepository sessionRepo; // Serve per cancellare le sessioni
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UniversityRepository uniRepo, DepartmentRepository depRepo, 
            StudyCourseRepository courseRepo, SubjectRepository subjectRepo,
            UserRepository userRepo, RoomRepository roomRepo, 
            StudyTableRepository tableRepo, StudySessionRepository sessionRepo,
            PasswordEncoder passwordEncoder) {
        this.uniRepo = uniRepo;
        this.depRepo = depRepo;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
        this.userRepo = userRepo;
        this.roomRepo = roomRepo;
        this.tableRepo = tableRepo;
        this.sessionRepo = sessionRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. PULIZIA TOTALE (L'ordine è FONDAMENTALE per non violare vincoli)
        
        sessionRepo.deleteAll(); // <--- PRIMA CANCELLIAMO LE SESSIONI (figlie dei tavoli)
        tableRepo.deleteAll();   // <--- POI I TAVOLI (figli delle aule)
        
        subjectRepo.deleteAll();
        courseRepo.deleteAll(); 
        roomRepo.deleteAll();    
        depRepo.deleteAll();    
        userRepo.deleteAll();   
        uniRepo.deleteAll();    

        System.out.println("🧹 Database ripulito correttamente.");

        // 2. CREAZIONE DATI BASE
        
        // Università
        University sapienza = new University("Sapienza");
        University polimi = new University("Polimi");
        uniRepo.saveAll(List.of(sapienza, polimi));

        // Utenti
        createUser("super_admin", "admin", Role.ADMIN, null);
        createUser("rettore_sapienza", "pass", Role.UNI_ADMIN, sapienza);
        createUser("rettore_polimi", "pass", Role.UNI_ADMIN, polimi);
        createUser("studente", "user", Role.USER, sapienza);

        System.out.println("✅ Database pronto!");
    }

    private void createUser(String username, String rawPassword, Role role, University university) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setUniversity(university);
        userRepo.save(user);
    }
}
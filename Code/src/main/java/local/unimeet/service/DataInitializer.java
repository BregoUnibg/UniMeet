/*
package local.unimeet.service;


import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.BuildingRepository;
import local.unimeet.repository.DepartmentRepository;
import local.unimeet.repository.RoomRepository;
import local.unimeet.repository.StudyCourseRepository;
import local.unimeet.repository.StudyTableRepository;
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
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final BuildingRepository buiRepo;
    private final RoomRepository roomRepo;
    private final StudyTableRepository tabRepo;

    public DataInitializer(UniversityRepository uniRepo, DepartmentRepository depRepo, 
                           StudyCourseRepository courseRepo, SubjectRepository subjectRepo, 
                           UserRepository userRepo, ProfileService profileService,
                           PasswordEncoder passwordEncoder, BuildingRepository buiRepo,
                           RoomRepository roomRepo, StudyTableRepository tabRepo) {
        this.uniRepo = uniRepo;
        this.depRepo = depRepo;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
        this.userRepo = userRepo;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
        this.buiRepo = buiRepo;
        this.roomRepo = roomRepo;
        this.tabRepo = tabRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        //Populate only if the database is empty
        if (uniRepo.count() > 0) return;

        System.out.println("Start populating UniMeet database...");

        //Creation of University Study Hierarchy
        populateUniversityStudyData();
        
        //Creation of University Buildings Hierarchy
        populateUniversityBuildingData();

        //Creation of Mock users for test
        populateTestData();

        System.out.println("Database ready to use!");
    }

    private void populateUniversityStudyData() {
        //--- UNIVERSITIES ---
        University unibg = uniRepo.save(new University("Università degli Studi di Bergamo"));

        //--- DEPARTMENTS UNIBG ---
        Department dg = depRepo.save(new Department("Dipartimeneto di Giurisprudenza", unibg));
        Department dlfc = depRepo.save(new Department("Dipartimento di Lettere, Filosofia, Comunicazione", unibg));
        Department dllc = depRepo.save(new Department("Dipartimento di Lingue, Letterature, Culture straniere", unibg));
        Department dsa = depRepo.save(new Department("Dipartimento di Scienze Aziendali", unibg));
        Department dse = depRepo.save(new Department("Dipartimento di Scienze Economiche", unibg));
        Department dsus = depRepo.save(new Department("Dipartimento di Scienze Umane e Sociali", unibg));
        Department si = depRepo.save(new Department("Scuola di Ingegneria", unibg));

        //--- STUDY COURSES DG ---
        StudyCourse di = courseRepo.save(new StudyCourse("[19-R-DIN] Dirittto per l'impresa nazionale e internazionale", dg, DegreeType.BACHELOR));
        StudyCourse du = courseRepo.save(new StudyCourse("[166-R] Diritti umani, migrazione e cooperazione internazionale", dg, DegreeType.MASTER));
        StudyCourse g = courseRepo.save(new StudyCourse("[65-R] Giurisprudenza", dg, DegreeType.SINGLE_CICLE_MASTER));
        StudyCourse gr = courseRepo.save(new StudyCourse("[65GDF-R] Giurisprudenza (Riservato agli allievi della guardia di finanza)", dg, DegreeType.SINGLE_CICLE_MASTER));

        //--- STUDY COURSES DLFC ---
        StudyCourse f = courseRepo.save(new StudyCourse("[106-R] Filosofia", dlfc, DegreeType.BACHELOR));
        StudyCourse l = courseRepo.save(new StudyCourse("[24-R] Lettere", dlfc, DegreeType.BACHELOR));
        StudyCourse sc = courseRepo.save(new StudyCourse("[92-R] Scienze della comunicazione", dlfc, DegreeType.BACHELOR));
        StudyCourse cie = courseRepo.save(new StudyCourse("[93-R] Comunicazione, informazione, editoria", dlfc, DegreeType.MASTER));
        StudyCourse cu = courseRepo.save(new StudyCourse("[191-270] Culture umanistiche", dlfc, DegreeType.MASTER));
        StudyCourse fss = courseRepo.save(new StudyCourse("[181-R] Filosofia, scienze e società", dlfc, DegreeType.MASTER));
        StudyCourse phfma = courseRepo.save(new StudyCourse("[180-R-EN] Philosophical knowledge: foundations, methods, applications", dlfc, DegreeType.BACHELOR));
        StudyCourse vpcmi = courseRepo.save(new StudyCourse("[176-R] Valorizzazione del patrimonio culturale materiale e immateriale", dlfc, DegreeType.MASTER));
        
        //--- STUDY COURSES DLLC ---
        StudyCourse llsm = courseRepo.save(new StudyCourse("[13-R] Lingue e lettere straniere moderne", dllc, DegreeType.BACHELOR));
        StudyCourse geo = courseRepo.save(new StudyCourse("[156-R] Geourbanistica. Analisi e pianificazione territoriale, urbana, ambientale e valorizzazione del paesaggio", dllc, DegreeType.MASTER));
        StudyCourse isll = courseRepo.save(new StudyCourse("[57-R-EN] Intercultural studies in languages and literatures", dllc, DegreeType.MASTER));
        StudyCourse lmcci = courseRepo.save(new StudyCourse("[28-R] Lingue moderne per la comunicazione e la cooperazione internazionale", dllc, DegreeType.MASTER));
        StudyCourse pmts = courseRepo.save(new StudyCourse("[44-R-EN] Planning and management of tourism systems", dllc, DegreeType.MASTER));
        
        //--- STUDY COURSES DSA ---
        StudyCourse ez = courseRepo.save(new StudyCourse("[87-R] Economia aziondale", dsa, DegreeType.BACHELOR));
        StudyCourse ags = courseRepo.save(new StudyCourse("[179-R-EN] Accounting, governance and sustainability", dsa, DegreeType.MASTER));
        StudyCourse ezdap = courseRepo.save(new StudyCourse("[90-R] Economia aziendale, direzione amministrativa e professionale", dsa, DegreeType.MASTER));
        StudyCourse ef = courseRepo.save(new StudyCourse("[162-R-EN] Economics and finance", dsa, DegreeType.MASTER));
        StudyCourse imm = courseRepo.save(new StudyCourse("[184-R-EN] International management and marketing", dsa, DegreeType.MASTER));
        StudyCourse mif = courseRepo.save(new StudyCourse("[165-R] Management, innovazione e finanza", dsa, DegreeType.MASTER));
        StudyCourse wmis = courseRepo.save(new StudyCourse("[98-270R] Welfare management e innovazione sociale", dsa, DegreeType.MASTER));
        
        //--- STUDY COURSES DSE ---
        StudyCourse daetd = courseRepo.save(new StudyCourse("[97-270R] Data analytics, economia e tecnologia digitali", dse, DegreeType.BACHELOR));
        StudyCourse e = courseRepo.save(new StudyCourse("[86-R] Economia", dse, DegreeType.BACHELOR));
        StudyCourse spsg = courseRepo.save(new StudyCourse("[SPSG] Scienze politiche e strategie globali", dse, DegreeType.BACHELOR));
        StudyCourse eda = courseRepo.save(new StudyCourse("[149-R-EN] Economics and data analysis", dse, DegreeType.MASTER));
        StudyCourse gesg = courseRepo.save(new StudyCourse("[190-270] Geopolitica, economia e strategie globali", dse, DegreeType.MASTER));
        
        //--- STUDY COURSES DSUC ---
        StudyCourse se = courseRepo.save(new StudyCourse("[25-R] Scienze dell'educazione", dsus, DegreeType.BACHELOR));
        StudyCourse sms = courseRepo.save(new StudyCourse("[158-R] Scienze motorie e sportive", dsus, DegreeType.BACHELOR));
        StudyCourse sps = courseRepo.save(new StudyCourse("[40-R] Scienze psicologiche", dsus, DegreeType.BACHELOR));
        StudyCourse cpifo = courseRepo.save(new StudyCourse("[64-R-EN] Clinical psychology for individuals, families and organizations", dsus, DegreeType.MASTER));
        StudyCourse pcvai = courseRepo.save(new StudyCourse("[99-270R] Progettazione di contesti di vita accessibili ed inclusivi", dsus, DegreeType.MASTER));
        StudyCourse pc = courseRepo.save(new StudyCourse("[64-R] Psicologia clinica", dsus, DegreeType.MASTER));
        StudyCourse spe = courseRepo.save(new StudyCourse("[84-R] Scienze pedagogiche", dsus, DegreeType.MASTER));
        StudyCourse smdas = courseRepo.save(new StudyCourse("[178-R] Scienze, metodi e didattiche delle attività sportive", dsus, DegreeType.MASTER));
        StudyCourse sfp = courseRepo.save(new StudyCourse("[139-270] Scienze della formazione primaria", dsus, DegreeType.SINGLE_CICLE_MASTER));
        
        //--- STUDY COURSES SI ---
        StudyCourse ite = courseRepo.save(new StudyCourse("[20-R-TE] Ingegneria delle tecnologie per l'edilizia", si, DegreeType.BACHELOR));
        StudyCourse itea = courseRepo.save(new StudyCourse("[96-270R] Ingegneria delle tecnologie per l'elettronica e l'automazione", si, DegreeType.BACHELOR));
        StudyCourse its = courseRepo.save(new StudyCourse("[95-R] Ingegneria delle tecnologie per la salute", si, DegreeType.BACHELOR));
        StudyCourse itsea = courseRepo.save(new StudyCourse("[174-R] Ingegneria delle tecnologie per la sostenibilità energetica e ambientale", si, DegreeType.BACHELOR));
        StudyCourse ig = courseRepo.save(new StudyCourse("[22-R] Ingegneria gestionale", si, DegreeType.BACHELOR));
        StudyCourse ii = courseRepo.save(new StudyCourse("[21-R] Ingegneria informatica", si, DegreeType.BACHELOR));
        StudyCourse im = courseRepo.save(new StudyCourse("[23-R] Ingegneria meccanica", si, DegreeType.BACHELOR));
        StudyCourse ice = courseRepo.save(new StudyCourse("[60-270-CE] Ingegneria delle costruzioni edili", si, DegreeType.MASTER));
        StudyCourse igM = courseRepo.save(new StudyCourse("[37-270] Ingegneria gestionale", si, DegreeType.MASTER));
        StudyCourse iiM = courseRepo.save(new StudyCourse("[38-270] Ingegneria informatica", si, DegreeType.MASTER));
        StudyCourse imM = courseRepo.save(new StudyCourse("[39-270] Ingegneria meccanica", si, DegreeType.MASTER));
        StudyCourse mae = courseRepo.save(new StudyCourse("[37-270-EN] Management engineering ", si, DegreeType.MASTER));
        StudyCourse mste = courseRepo.save(new StudyCourse("[161-270-EN] Mechatronics and smart technology engineering ", si, DegreeType.MASTER));
        StudyCourse mee = courseRepo.save(new StudyCourse("[175-R-EN] Medical engineering ", si, DegreeType.MASTER));
        StudyCourse ms = courseRepo.save(new StudyCourse("[H4102D] Medical and surgery ", si, DegreeType.SINGLE_CICLE_MASTER));
        
        //--- SUBJECTS II ----
        // --- YEAR 1 ---
        addSubjectsToCourse(ii, 1, 
            "[21010] Chimica",
            "[21011] Fisica generale (Modulo I + Modulo II)",
            "[21011-1] Modulo di fisica generale I",
            "[21011-2] Modulo di fisica generale II",
            "[21012] Informatica (Modulo di programmazione + Modulo di calcolatori)",
            "[21054] Geometria e algebra lineare",
            "[21055] Analisi matematica I",
            "[218516] Economia ed organizzazione aziendale",
            "[218517] Programmazione ad oggetti"
        );
        // --- YEAR 2 ---
        addSubjectsToCourse(ii, 2, 
            "[21015] Analisi matematica II",
            "[21017] Elettrotecnica",
            "[21018] Fondamenti di automatica",
            "[21020] Fondamenti di elettronica",
            "[21024] Fondamenti di reti e telecomunicazioni",
            "[21060] Statistica",
            "[21061] C.I. Sistemi operativi + Basi di dati",
            "[21061-1] Sistemi operativi",
            "[21061-2] Basi di dati"
        );
        // --- YEAR 3 ---
        addSubjectsToCourse(ii, 3, 
            "[21034] Ingegneria del software",
            "[21063] C.I. Embedded and real time systems + Programmazione WEB",
            "[21063-1] Embedded and real time systems",
            "[21063-2] Programmazione WEB",
            "[21028] Algebra e logica",
            "[21033] Gestione della produzione industriale",
            "[21037] Sistemi di controllo di gestione",
            "[21052] C.I. Automazione industriale + Elettronica industriale",
            "[21052-1] Automazione industriale",
            "[21052-2] Elettronica industriale",
            "[21053-EN] C.I. Database II + Multimedia internet",
            "[21053-E1] Database II",
            "[21053-E2] Multimedia internet",
            "[21036] Progettazione di sistemi elettronici",
            "[21065-EN] Control system technology",
            "[21066] Controllo avanzato multivariabile",
            "[21068-EN] Introduction to machine learning",
            "[21069] Piattaforme cloud e applicazioni mobili",
            "[22009] Disegno tecnico industriale",
            "[22014] Fisica tecnica",
            "[22023] Economia del cambiamento tecnologico",
            "[22026-EN] Information management",
            "[95014] Sistemi di gestione per la qualità"
        );
    }
    
    private void populateUniversityBuildingData() {
    	//--- UNIVERSITIES ---
    	University unibg = uniRepo.findByName("Università degli Studi di Bergamo").get(0);
    	
    	//--- BUILDINGS ---
    	Building a = buiRepo.save(new Building("Edificio A", "Via Albert Einstein, 2, 24044 Dalmine BG", unibg));
    	Building b = buiRepo.save(new Building("Edificio B", "Viale G. Marconi, 5, 24044 Dalmine BG", unibg));
    	Building c = buiRepo.save(new Building("Edificio C", "Via Pasubio, 7b, 24044 Dalmine BG", unibg));
    	Building d = buiRepo.save(new Building("Edificio D", "Via Pasubio, 3, 24044 Dalmine BG", unibg));
    	
    	//--- ROOMS ---
    	//--- BUILDING A---
    	Room a1 = roomRepo.save(new Room(1, a));
    	//--- BUILDING B---
    	Room b1 = roomRepo.save(new Room(1, b));
    	Room b2 = roomRepo.save(new Room(2, b));
    	Room b3 = roomRepo.save(new Room(3, b));
    	//--- BUILDING C---
    	Room c1 = roomRepo.save(new Room(1, c));
    	Room c2 = roomRepo.save(new Room(2, c));
    	//--- BUILDING D---
    	Room d1 = roomRepo.save(new Room(2, d));
    	
    	//--- TABLES ---
    	tabRepo.saveAll(List.of(new StudyTable(1, 4, a1),
    			new StudyTable(2, 4, a1),
    			new StudyTable(3, 4, a1),
    			new StudyTable(4, 4, a1),
    			new StudyTable(1, 4, b1),
    			new StudyTable(2, 4, b1),
    			new StudyTable(3, 6, b1),
    			new StudyTable(4, 10, b1),
    			new StudyTable(1, 4, b2),
    			new StudyTable(2, 4, b2),
    			new StudyTable(1, 8, b3),
    			new StudyTable(2, 6, b3),
    			new StudyTable(1, 4, c1),
    			new StudyTable(2, 4, c1),
    			new StudyTable(3, 4, c1),
    			new StudyTable(4, 4, c1),
    			new StudyTable(5, 8, c1),
    			new StudyTable(6, 8, c1),
    			new StudyTable(1, 4, c2),
    			new StudyTable(2, 4, c2),
    			new StudyTable(3, 4, c2),
    			new StudyTable(4, 4, c2),
    			new StudyTable(5, 4, c2),
    			new StudyTable(6, 4, c2),
    			new StudyTable(1, 4, d1),
    			new StudyTable(2, 4, d1),
    			new StudyTable(3, 4, d1),
    			new StudyTable(4, 4, d1),
    			new StudyTable(5, 12, d1)));
    }

    private void populateTestData() {
        StudyCourse ii = courseRepo.findByName("[21-R] Ingegneria informatica").get(0);
        Subject fisica2 = subjectRepo.findByName("[21011-2] Modulo di fisica generale II").get(0);
        Subject erts = subjectRepo.findByName("[21063-1] Embedded and real time systems").get(0);
        Subject scg = subjectRepo.findByName("[21037] Sistemi di controllo di gestione").get(0);
        Subject reti = subjectRepo.findByName("[21024] Fondamenti di reti e telecomunicazioni").get(0);
        
        //Super Admin
        User user1 = createUser("s.vecchi", "s.vecchi");
        user1.setRole(Role.ADMIN);
        UserProfile profile1 = profileService.getOrCreateProfile(user1);
        fillProfile(profile1, "Samuele", "Vecchi", "Futuro dev.", ii, 3, 4.2);
        profile1.getPreferredCourses().add(reti);
        profile1.getPreferredCourses().add(erts);
        profile1.getDifficultCourses().add(fisica2);
        profile1.getPassedExams().add(reti);
        profile1.getPassedExams().add(scg);
        profile1.getPendingExams().add(fisica2);
        profileService.saveProfile(profile1);
        
        //Admin locale
        User user2 = createUser("g.bregolin", "g.bregolin");
        user2.setRole(Role.UNI_ADMIN);
        UserProfile profile2 = profileService.getOrCreateProfile(user2);
        fillProfile(profile2, "Gabriele", "Bregolin", "Mi piace programmare.", ii, 3, 4.6);
        profile2.getPreferredCourses().add(reti);
        profile2.getDifficultCourses().add(erts);
        profile2.getDifficultCourses().add(fisica2);
        profile2.getPassedExams().add(scg);
        profile2.getPassedExams().add(reti);
        profile2.getPendingExams().add(fisica2);
        profileService.saveProfile(profile2);
        
        User user3 = createUser("l.leggeri", "l.leggeri");
        user3.setRole(Role.UNI_ADMIN);
        UserProfile profile3 = profileService.getOrCreateProfile(user3);
        fillProfile(profile3, "Leonardo", "Leggeri", "Ho scelto il percorso industriale.", ii, 3, 4.3);
        profile3.getPreferredCourses().add(reti);
        profile3.getPreferredCourses().add(erts);
        profile3.getDifficultCourses().add(fisica2);
        profile3.getPassedExams().add(fisica2);
        profile3.getPendingExams().add(scg);
        profile3.getPendingExams().add(reti);
        profileService.saveProfile(profile3);
        
        //User
        User user4 = createUser("f.cremonesi", "f.cremonesi");
        user4.setRole(Role.USER);
        UserProfile profile4 = profileService.getOrCreateProfile(user4);
        fillProfile(profile4, "Federico", "Cremonesi", "Mi piace la fisica.", ii, 3, 3.8);
        profile4.getPreferredCourses().add(fisica2);
        profile4.getDifficultCourses().add(erts);
        profile4.getDifficultCourses().add(scg);
        profile4.getPassedExams().add(fisica2);
        profile4.getPendingExams().add(scg);
        profile4.getPendingExams().add(reti);
        profileService.saveProfile(profile4);
        
        User user5 = createUser("r.beltramelli", "r.beltramelli");
        user5.setRole(Role.USER);
        UserProfile profile5 = profileService.getOrCreateProfile(user5);
        fillProfile(profile5, "Raul", "Beltramelli", "Questa è la mia Bio.", ii, 3, 3.8);
        profile5.getPreferredCourses().add(fisica2);
        profile5.getPreferredCourses().add(erts);
        profile5.getPreferredCourses().add(scg);
        profile5.getPassedExams().add(fisica2);
        profile5.getPendingExams().add(scg);
        profile5.getPendingExams().add(reti);
        profileService.saveProfile(profile5);
    }

    // Helper methods
    private void addSubjectsToCourse(StudyCourse course, int year, String... names) {
        List<Subject> subjects = Arrays.stream(names)
                .map(name -> new Subject(name, course, year))
                .toList();
        subjectRepo.saveAll(subjects);
    }

    private User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    private void fillProfile(UserProfile p, String n, String s, String b, StudyCourse c, int y, double r) {
        p.setFirstName(n);
        p.setLastName(s);
        p.setBio(b);
        p.setUniversity(c.getDepartment().getUniversity());
        p.setDepartment(c.getDepartment());
        p.setDegreeType(c.getDegreeType());
        p.setCourseName(c);
        p.setStudyYear(y);
        p.setReputation(r);
        p.setTotVoters(5);
    }
}
*/
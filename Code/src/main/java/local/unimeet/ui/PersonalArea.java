package local.unimeet.ui;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserRepository;
import local.unimeet.service.ProfileService;

@Route(value = "personal-area", layout = MainLayout.class)
@PageTitle("Profilo Personale")
@PermitAll
public class PersonalArea extends VerticalLayout {
	
	private final DelegationInterface delegate;

    public PersonalArea(ProfileService profileService, UserRepository userRepository, DelegationInterface delegate) {
    	this.delegate = delegate;
    	
        // Impostazioni generali della pagina
        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px"); // Teniamo il contenuto centrato e leggibile
        getElement().getStyle().set("margin", "auto");
        
        // RECUPERO DATI
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        UserProfile profile = profileService.getOrCreateProfile(currentUser);
        
        add(new H2("Area personale"));

        // --- SEZIONE ANAGRAFE ---
        add(createAnagrafeSection(profile));

        add(new Hr()); // Linea di separazione

        // --- SEZIONE PERCORSO DI STUDIO ---
        add(new H3("PERCORSO DI STUDIO"));
        add(createStudyPathSection(profile));

        add(new Hr());

        // --- SEZIONE CARRIERA (Corsi e Esami) ---
        add(new H3("CARRIERA"));
        add(createCareerSection(profile));

        // --- BOTTONE MODIFICA ---
        Button editButton = new Button("Modifica profilo", e -> getUI().ifPresent(ui -> ui.navigate(EditProfile.class)));
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.getStyle().set("margin-top", "2em");
        
        HorizontalLayout footer = new HorizontalLayout(editButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        add(footer);
    }

	private HorizontalLayout createAnagrafeSection(UserProfile profile) {
        // Immagine profilo (Placeholder)
        Image profileImg;

		if (profile.getProfilePicture() != null) {
		    // Se c'è una foto nel database, creiamo la risorsa dai byte
		    StreamResource resource = new StreamResource("profile-pic",
		            () -> new ByteArrayInputStream(profile.getProfilePicture()));
		    profileImg = new Image(resource, "Foto profilo");
		} else {
		    // Altrimenti usiamo il placeholder
		    profileImg = new Image("images/default-avatar.jpeg", "Foto profilo predefinita");
		}

		profileImg.setWidth("100px");
        profileImg.setHeight("120px");
        profileImg.getStyle().set("border-radius", "8px");
        profileImg.getStyle().set("object-fit", "cover");
		profileImg.getStyle().set("border", "2px solid #ccc");
		

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        // Campi Anagrafe
        info.add(delegate.createDataRow("Nome:", (profile.getFirstName() != null && !profile.getFirstName().isEmpty()) ? profile.getFirstName() : "Non specificato"));
        info.add(delegate.createDataRow("Cognome:", (profile.getLastName() != null && !profile.getLastName().isEmpty()) ? profile.getLastName() : "Non specificato"));
        
        Span labelBio = new Span("Bio: ");
        labelBio.setWidth("150px");
        labelBio.getStyle().set("font-weight", "bold");
        Span bio = new Span((profile.getBio() != null && !profile.getBio().isEmpty()) ? profile.getBio() : "Nessuna biografia inserita.");
        //bio.getStyle().set("white-space", "pre-wrap");
        bio.setWidth("394px");
        info.add(new HorizontalLayout(labelBio, bio));

        // Reputazione
        double reputetion = profile.getReputation() != null ? profile.getReputation().doubleValue() : 0;
        int totVoters = profile.getTotVoters() != null ? profile.getTotVoters().intValue() : 0;
        info.add(delegate.createDataRow("Reputazione: ", delegate.createReputationBar(reputetion, totVoters)));

        HorizontalLayout anagrafeLayout = new HorizontalLayout(profileImg, info);
        anagrafeLayout.setAlignItems(Alignment.START);
        return anagrafeLayout;
    }

    private VerticalLayout createStudyPathSection(UserProfile profile) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        
        layout.add(delegate.createDataRow("Università:", profile.getUniversity() != null ? profile.getUniversity().getName() : "Non specificata"));
        layout.add(delegate.createDataRow("Dipartimento:", profile.getDepartment() != null ? profile.getDepartment().getName() : "Non specificato"));
        layout.add(delegate.createDataRow("Tipo Laurea:", profile.getDegreeType() != null ? delegate.stringNormalization(profile.getDegreeType().toString()) : "Non specificato"));
        layout.add(delegate.createDataRow("Nome Laurea:", profile.getCourseName() != null ? profile.getCourseName().getName() : "Non specificato"));
        layout.add(delegate.createDataRow("Anno di corso:", profile.getStudyYear() != null ? profile.getStudyYear().toString() : "Non specificato"));

        return layout;
    }

    private VerticalLayout createCareerSection(UserProfile profile) {
        // Colonne per Corsi Preferiti e Difficoltà
        VerticalLayout pref;
        if (profile.getPreferredCourses() != null && !profile.getPreferredCourses().isEmpty()) {
            pref = createLabeledList("Corsi preferiti:", profile.getPreferredCourses());
        } else {
        	pref = createLabeledList("Corsi preferiti:", "Nessun corso selezionato");
        }
        VerticalLayout diff;
        if (profile.getDifficultCourses() != null && !profile.getDifficultCourses().isEmpty()) {
            diff = createLabeledList("Corsi in cui ho difficoltà:", profile.getDifficultCourses());
        } else {
        	diff = createLabeledList("Corsi preferiti:", "Nessun corso selezionato");
        }

        // Colonne per Esami Superati e Da Sostenere
        VerticalLayout passed;
        if (profile.getPassedExams() != null && !profile.getPassedExams().isEmpty()) {
        	passed = createLabeledList("Esami superati:", profile.getPassedExams());
        } else {
        	passed = createLabeledList("Esami superati:", "Nessun esame registrato");
        }
        VerticalLayout pending;
        if (profile.getPendingExams() != null && !profile.getPendingExams().isEmpty()) {
            pending = createLabeledList("Esami da sostenere:", profile.getPendingExams());
        } else {
        	pending = createLabeledList("Esami da sostenere:", "Nessun esame registrato");
        }

        HorizontalLayout courses = new HorizontalLayout(pref, diff);
        courses.setWidthFull();
        
        HorizontalLayout exams = new HorizontalLayout(passed, pending);
        exams.setWidthFull();
        
        VerticalLayout layout = new VerticalLayout(courses, exams);
        layout.setPadding(false);
        
        return layout;
    }

    // Metodo di utility per creare righe etichetta: valore
    
    
    private VerticalLayout createLabeledList(String title, Set<Subject> list) {
        VerticalLayout container = new VerticalLayout(spanBold(title));
        
        list.forEach(subject -> 
        	container.add(new Span("• " + subject.getName())));
        
        container.setSpacing(false); // Avvicina il titolo alla lista
        container.setPadding(false);
        return container;
    }
    
    private VerticalLayout createLabeledList(String title, String s) {
    	VerticalLayout container = new VerticalLayout(spanBold(title), new Span(s));
        container.setSpacing(false); // Avvicina il titolo alla lista
        container.setPadding(false);
        return container;
    }
    
    private Span spanBold(String label) {
    	Span l = new Span(label);
        l.getStyle().set("font-weight", "bold");
        return l;
    }
}

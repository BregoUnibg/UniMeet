package local.unimeet.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserRepository;
import local.unimeet.service.DataService;
import local.unimeet.service.ProfileService;

@Route(value = "edit-profile", layout = MainLayout.class)
@PageTitle("Profilo Personale | Edit")
@PermitAll
public class EditProfile extends VerticalLayout {
    
    private final ProfileService profileService;
    private final DataService dataService;
    private final DelegationInterface delegate;
    
    private final Binder<UserProfile> binder = new Binder<>(UserProfile.class);

    // Componenti UI
    private Image avatarPreview = new Image();
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload = new Upload(buffer);
    private Button removeImgBtn = new Button("Rimuovi Foto");
    
    // Campi Anagrafe
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextArea bio = new TextArea();
    
    // Percorso di Studio
    private ComboBox<University> university = new ComboBox<>();
    private ComboBox<Department> department = new ComboBox<>();
    private ComboBox<DegreeType> degreeType = new ComboBox<>();
    private ComboBox<StudyCourse> courseName = new ComboBox<>();
    private ComboBox<Integer> studyYear = new ComboBox<>();
    
    // Carriera
    private MultiSelectListBox<Subject> preferredCourses = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> difficultCourses = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> passedExams = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> pendingExams = new MultiSelectListBox<>();
    
    public EditProfile(ProfileService profileService, DataService dataService, UserRepository userRepository, DelegationInterface delegate) {
        this.profileService = profileService;
        this.dataService = dataService;
        this.delegate = delegate;
        
        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        getElement().getStyle().set("margin", "auto");
        
        // Recupero utente
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        UserProfile currentProfile = profileService.getOrCreateProfile(currentUser);
        
        add(new H2("Area personale"));
        
        // Sezioni
        add(createAnagrafeSection(currentProfile));
        add(new Hr()); 
        add(new H3("PERCORSO DI STUDIO"));
        add(createStudyPathSection());
        add(new Hr());
        add(new H3("CARRIERA"));
        add(createCareerSection());
        
        // Bottoni
        Button cancelBtn = new Button("Annulla", e -> getUI().ifPresent(ui -> ui.navigate(PersonalArea.class)));
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelBtn.getStyle().set("margin-top", "2em");

        Button saveButton = new Button("Salva Modifiche", e -> saveProfile(currentProfile));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-top", "2em");
        
        HorizontalLayout footer = new HorizontalLayout(cancelBtn, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        add(footer);
        
        // Configurazione Logica
        configureMenu();
        setupLimitLogic();
        setupExclusionLogic(preferredCourses, difficultCourses);
        setupExclusionLogic(passedExams, pendingExams);

        // Binding
        binder.forField(firstName)
            .asRequired("Il nome è obbligatorio")
            .bind(UserProfile::getFirstName, UserProfile::setFirstName);
    
        binder.forField(lastName)
            .asRequired("Il cognome è obbligatorio")
            .bind(UserProfile::getLastName, UserProfile::setLastName);
        
        binder.forField(bio)
            .withValidator(text -> text == null || text.length() <= UserProfile.length, "La bio non può superare i " + UserProfile.length + " caratteri")
            .bind(UserProfile::getBio, UserProfile::setBio);
        
        binder.bindInstanceFields(this);
        
       
        initComboBoxData(currentProfile);
        
        binder.readBean(currentProfile);
    }
    
    
    //ho messo la combox per gestire la scelta nel profilo
     private void initComboBoxData(UserProfile profile) {
        university.setItems(dataService.findAllUniversities());

        if (profile.getUniversity() != null) {
            department.setItems(dataService.findDepartmentsByUniversity(profile.getUniversity()));
            department.setEnabled(true);
        }
        if (profile.getDepartment() != null) {
            degreeType.setItems(DegreeType.values());
            degreeType.setItemLabelGenerator(subject -> delegate.stringNormalization(subject.name()));
            degreeType.setEnabled(true);
        }
        if (profile.getDegreeType() != null) {
            courseName.setItems(dataService.findCourseByDegreeType(profile.getDegreeType()));
            courseName.setEnabled(true);
            studyYear.setItems(updateYearOptions(profile.getDegreeType()));
        }
        if (profile.getCourseName() != null && profile.getStudyYear() != null) {
            studyYear.setEnabled(true);
            List<Subject> subjects = dataService.findSubjects(profile.getCourseName(), profile.getStudyYear());
            if (!subjects.isEmpty()) {
                preferredCourses.setItems(subjects);
                difficultCourses.setItems(subjects);
                passedExams.setItems(subjects);
                pendingExams.setItems(subjects);
                setVisibleLists(true);
            }
        }
    }

    private void configureMenu() {
        department.setEnabled(false);
        courseName.setEnabled(false);
        degreeType.setEnabled(false);
        studyYear.setEnabled(false);
        
        university.setItems(dataService.findAllUniversities());
        
        university.addValueChangeListener(event -> {
            University val = event.getValue();
            department.clear(); department.setEnabled(false);
            degreeType.clear(); degreeType.setEnabled(false);
            courseName.clear(); courseName.setEnabled(false);
            studyYear.clear(); studyYear.setEnabled(false);
            setVisibleLists(false);
            
            if (val != null) {
                department.setItems(dataService.findDepartmentsByUniversity(val));
                department.setEnabled(true);
            }
        });
        
        department.addValueChangeListener(event -> {
            Department val = event.getValue();
            degreeType.clear(); degreeType.setEnabled(false);
            courseName.clear(); courseName.setEnabled(false);
            studyYear.clear(); studyYear.setEnabled(false);
            setVisibleLists(false);

            if (val != null) {
                degreeType.setItems(DegreeType.values());
                degreeType.setItemLabelGenerator(s -> delegate.stringNormalization(s.name()));
                degreeType.setEnabled(true);
            }
        });
        
        degreeType.addValueChangeListener(event -> {
            DegreeType val = event.getValue();
            courseName.clear(); courseName.setEnabled(false);
            studyYear.clear(); studyYear.setEnabled(false);
            setVisibleLists(false);
            
            if (val != null) {
                courseName.setItems(dataService.findCourseByDegreeType(val));
                courseName.setEnabled(true);
                studyYear.setItems(updateYearOptions(val));
            }
        });
        
        courseName.addValueChangeListener(event -> {
            StudyCourse val = event.getValue();
            studyYear.clear(); studyYear.setEnabled(false);
            setVisibleLists(false);
            
            if (val != null) {
                studyYear.setHelperText("Selezionare 0 se Fuori Corso");
                studyYear.setEnabled(true);
            }
        });
        
        studyYear.addValueChangeListener(event -> {
            
            Integer selectedYear = event.getValue(); 
            
            if (selectedYear == null) {
                setVisibleLists(false);
            } else {
               
                List<Subject> materie = dataService.findSubjects(courseName.getValue(), selectedYear);
                
                if(materie.isEmpty()) {
                    Notification.show("Nessuna Materia disponibile", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {
                    preferredCourses.setItems(materie);
                    difficultCourses.setItems(materie);
                    passedExams.setItems(materie);
                    pendingExams.setItems(materie);
                    setVisibleLists(true);
                }
            }
        });
    }

    private void setVisibleLists(boolean visible) {
        preferredCourses.setVisible(visible);
        difficultCourses.setVisible(visible);
        passedExams.setVisible(visible);
        pendingExams.setVisible(visible);
    }
    
    
    
    private HorizontalLayout createAnagrafeSection(UserProfile currentProfile) {
        avatarPreview.setWidth("100px");
        avatarPreview.setHeight("120px");
        avatarPreview.getStyle().set("border-radius", "8px");
        avatarPreview.getStyle().set("object-fit", "cover");
        avatarPreview.getStyle().set("border", "2px solid #ccc");
        
        if (currentProfile.getProfilePicture() != null && currentProfile.getProfilePicture().length > 0) {
            updatePreview(currentProfile.getProfilePicture());
        } else {
            avatarPreview.setSrc("images/default-avatar.jpeg");
        }

        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFileSize(5 * 1024 * 1024);
        upload.setUploadButton(new Button("Carica nuova foto"));
        upload.setWidth("190px");
        upload.getStyle().setAlignItems(AlignItems.CENTER);

        upload.addSucceededListener(event -> {
            try {
                byte[] originalBytes = buffer.getInputStream().readAllBytes();
                byte[] optimizedBytes = resizeImage(originalBytes);
                currentProfile.setProfilePicture(optimizedBytes); 
                updatePreview(optimizedBytes);
                Notification.show("Foto caricata correttamente!");
            } catch (IOException e) {
                Notification.show("Errore nel caricamento del file");
            }
        });
        
        removeImgBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        removeImgBtn.addClickListener(e -> {
            currentProfile.setProfilePicture(null);
            avatarPreview.setSrc("images/default-avatar.jpeg");
            Notification.show("Foto rimossa.");
        });

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false); info.setPadding(false);

        info.add(delegate.createDataRow("Nome: ", firstName));
        info.add(delegate.createDataRow("Cognome:", lastName));
        
        Span labelBio = new Span("Bio: ");
        labelBio.setWidth("150px");
        labelBio.getStyle().set("font-weight", "bold");
        labelBio.getStyle().setPaddingTop("10px");
        bio.setWidth("394px");
        HorizontalLayout h = new HorizontalLayout(labelBio, bio);
        info.add(h);

        double reputetion = currentProfile.getReputation() != null ? currentProfile.getReputation().doubleValue() : 0;
        int totVoters = currentProfile.getTotVoters() != null ? currentProfile.getTotVoters().intValue() : 0;
        info.add(delegate.createDataRow("Reputazione: ", delegate.createReputationBar(reputetion, totVoters)));

        VerticalLayout imageLayout = new VerticalLayout(avatarPreview, upload, removeImgBtn);
        imageLayout.setSpacing(true); imageLayout.setPadding(false); imageLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout anagrafeLayout = new HorizontalLayout(imageLayout, info);
        anagrafeLayout.setAlignItems(Alignment.START);
        return anagrafeLayout;
    }
    
    private VerticalLayout createStudyPathSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false); layout.setPadding(false);
        layout.add(delegate.createDataRow("Università:", university));
        layout.add(delegate.createDataRow("Dipartimento:", department));
        layout.add(delegate.createDataRow("Tipo Laurea:", degreeType));
        layout.add(delegate.createDataRow("Nome Laurea:", courseName));
        layout.add(delegate.createDataRow("Anno di corso:", studyYear));
        return layout;
    }

    private VerticalLayout createCareerSection() {
        VerticalLayout pref = createLabeledList("Corsi preferiti:", preferredCourses);
        VerticalLayout diff = createLabeledList("Corsi in cui ho difficoltà:", difficultCourses);
        VerticalLayout passed = createLabeledList("Esami superati:", passedExams);
        VerticalLayout pending = createLabeledList("Esami da sostenere:", pendingExams);
        
        HorizontalLayout courses = new HorizontalLayout(pref, diff);
        courses.setWidthFull();
        HorizontalLayout exams = new HorizontalLayout(passed, pending);
        exams.setWidthFull();

        VerticalLayout v = new VerticalLayout(courses, exams);
        v.setPadding(false);
        return v;
    }

    private void setupLimitLogic() {
        addSelectionLimit(preferredCourses, "forza");
        addSelectionLimit(difficultCourses, "debolezza");
    }

    private void addSelectionLimit(MultiSelectListBox<Subject> listBox, String tipo) {
        listBox.addSelectionListener(event -> {
            if (event.getAllSelectedItems().size() > 3) {
                Notification.show("Puoi selezionare al massimo 3 materie di " + tipo + "!");
                listBox.deselect(event.getAddedSelection().iterator().next());
            }
        });
    }
    
    private VerticalLayout createLabeledList(String title, MultiSelectListBox<Subject> listBox) {
        Span label = new Span(title);
        label.getStyle().set("font-weight", "bold");
        VerticalLayout container = new VerticalLayout(label, listBox);
        container.setSpacing(false); container.setPadding(false);
        return container;
    }
    
    private byte[] resizeImage(byte[] originalImage) {
        try {
            InputStream is = new ByteArrayInputStream(originalImage);
            BufferedImage src = ImageIO.read(is);
            if (src == null) return originalImage;

            int targetWidth = 120; int targetHeight = 120;
            int srcWidth = src.getWidth(); int srcHeight = src.getHeight();
            int x = 0, y = 0, width = srcWidth, height = srcHeight;

            if (srcWidth > srcHeight) {
                width = srcHeight; x = (srcWidth - srcHeight) / 2;
            } else {
                height = srcWidth; y = (srcHeight - srcWidth) / 2;
            }

            BufferedImage cropped = src.getSubimage(x, y, width, height);
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(cropped, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return originalImage;
        }
    }
    
    private void updatePreview(byte[] bytes) {
        StreamResource resource = new StreamResource("profile-pic", () -> new ByteArrayInputStream(bytes));
        avatarPreview.setSrc(resource);
    }

    private List<Integer> updateYearOptions(DegreeType type) {
        List<Integer> years = new ArrayList<>();
        if (type == null) return years;
        int max = switch (type) {
            case TRIENNALE -> 3; case MAGISTRALE -> 2; case CICLO_UNICO -> 5;
        };
        for (int i = 0; i <= max; i++) years.add(i);
        return years;
    }

    private void setupExclusionLogic(MultiSelectListBox<Subject> c1, MultiSelectListBox<Subject> c2) {
        c1.addSelectionListener(e -> e.getAllSelectedItems().forEach(item -> c2.deselect(item)));
        c2.addSelectionListener(e -> e.getAllSelectedItems().forEach(item -> c1.deselect(item)));
    }
    
    private void saveProfile(UserProfile profile) {
        try {
            binder.writeBean(profile);
            profileService.saveProfile(profile);
            Notification.show("Profilo aggiornato con successo!", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate(PersonalArea.class));
        } catch (ValidationException e) {
            Notification.show("Per favore, correggi gli errori nel modulo.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Errore tecnico durante il salvataggio: " + e.getMessage(), 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
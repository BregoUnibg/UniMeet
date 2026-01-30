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
@PageTitle("Personal Profile | Edit")
@PermitAll
public class EditProfile extends VerticalLayout {
	
	private final ProfileService profileService;
	private final DataService dataService;
	private final DelegationInterface delegate;
	
	private final Binder<UserProfile> binder = new Binder<>(UserProfile.class);

	//UI component
	//Profile picture
	private Image avatarPreview = new Image();
	private MemoryBuffer buffer = new MemoryBuffer();
	private Upload upload = new Upload(buffer);
	private Button removeImgBtn = new Button("Rimuovi Foto");
	
    //Personal details fields
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextArea bio = new TextArea();
    
    //Study path
    private ComboBox<University> university = new ComboBox<>();
    private ComboBox<Department> department = new ComboBox<>();
    private ComboBox<DegreeType> degreeType = new ComboBox<>();
    private ComboBox<StudyCourse> courseName = new ComboBox<>();
    private ComboBox<Integer> studyYear = new ComboBox<>();
    
    //Career section (Courses and Exams)
    private MultiSelectListBox<Subject> preferredCourses = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> difficultCourses = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> passedExams = new MultiSelectListBox<>();
    private MultiSelectListBox<Subject> pendingExams = new MultiSelectListBox<>();
	
    public EditProfile(ProfileService profileService, DataService dataService, UserRepository userRepository, DelegationInterface delegate) {
    	this.profileService = profileService;
    	this.dataService = dataService;
    	this.delegate = delegate;
    	
    	//General page settings
        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        getElement().getStyle().set("margin", "auto");
        
    	//Logic to recover the logged user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        UserProfile currentProfile = profileService.getOrCreateProfile(currentUser);
        
        add(new H2("Personal area | Edit"));
        
        //--- PERSONAL DETAILS SECTION ---
        add(createAnagrafeSection(currentProfile));

        add(new Hr());
        
        //--- STUDY PATH SECTION ---
        add(new H3("STUDY PATH"));
        add(createStudyPathSection());

        add(new Hr());

        //--- CAREER SECTION (Courses and Exams) ---
        add(new H3("CAREER"));
        add(createCareerSection());
        
        //--- CANCEL BUTTON ---
        Button cancelBtn = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate(PersonalArea.class)));
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelBtn.getStyle().set("margin-top", "2em");

        // --- SAVE BUTTON ---
        Button saveButton = new Button("Save Changes", e -> saveProfile(currentProfile));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-top", "2em");
        
        HorizontalLayout footer = new HorizontalLayout(cancelBtn, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        add(footer);
        
        //Drop-down menu configuration
        configureMenu();
        
        //Limit logic 3 courses
        setupLimitLogic();
        
        //Mutual Exclusion Logic Courses and Exams
        setupExclusionLogic(preferredCourses, difficultCourses);
        setupExclusionLogic(passedExams, pendingExams);

        //Binding configurations
        binder.forField(firstName)
	        .asRequired("First name is required")
	        .bind(UserProfile::getFirstName, UserProfile::setFirstName);
	
	    binder.forField(lastName)
	        .asRequired("Last name is required")
	        .bind(UserProfile::getLastName, UserProfile::setLastName);
	    
	    binder.forField(bio)
		    .withValidator(text -> text.length() <= UserProfile.length, "The biography cannot exceed " + UserProfile.length + " characters")
		    .bind(UserProfile::getBio, UserProfile::setBio);
	    
        binder.bindInstanceFields(this);
        
        binder.readBean(currentProfile);
    }

    //Heper methods
    private HorizontalLayout createAnagrafeSection(UserProfile currentProfile) {
    	//Profile picture (Placeholder)
        //Preview configuration
    	avatarPreview.setWidth("100px");
    	avatarPreview.setHeight("120px");
    	avatarPreview.getStyle().set("border-radius", "8px");
    	avatarPreview.getStyle().set("object-fit", "cover");
    	avatarPreview.getStyle().set("border", "2px solid #ccc");
    	
    	if (currentProfile.getProfilePicture() != null&& currentProfile.getProfilePicture().length > 0) {
    	    updatePreview(currentProfile.getProfilePicture());
    	} else {
    	    avatarPreview.setSrc("images/default-avatar.jpeg");
    	}

    	//Upload configuration
    	upload.setAcceptedFileTypes("image/jpeg", "image/png");
    	upload.setMaxFileSize(5 * 1024 * 1024); //5MB Limit
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
    	    Notification.show("Foto rimossa. Verrà usata l'immagine predefinita.");
    	});

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        //Personal Data Fields
        info.add(delegate.createDataRow("Firs Name: ", firstName));
        info.add(delegate.createDataRow("Last Name:", lastName));
        
        Span labelBio = new Span("Bio: ");
        labelBio.setWidth("150px");
        labelBio.getStyle().set("font-weight", "bold");
        labelBio.getStyle().setPaddingTop("10px");
        bio.setWidth("394px");
        HorizontalLayout h = new HorizontalLayout(labelBio, bio);
        info.add(h);

        //Reputation
        double reputetion = currentProfile.getReputation() != null ? currentProfile.getReputation().doubleValue() : 0;
        int totVoters = currentProfile.getTotVoters() != null ? currentProfile.getTotVoters().intValue() : 0;
        info.add(delegate.createDataRow("Reputation: ", delegate.createReputationBar(reputetion, totVoters)));

        VerticalLayout imageLayout = new VerticalLayout(avatarPreview, upload, removeImgBtn);
        imageLayout.setSpacing(true);
        imageLayout.setPadding(false);
        imageLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout anagrafeLayout = new HorizontalLayout(imageLayout, info);
        anagrafeLayout.setAlignItems(Alignment.START);
        return anagrafeLayout;
    }
    
    private VerticalLayout createStudyPathSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);

        layout.add(delegate.createDataRow("University:", university));
        layout.add(delegate.createDataRow("Department:", department));
        layout.add(delegate.createDataRow("Degree Type:", degreeType));
        layout.add(delegate.createDataRow("Degree Name:", courseName));
        layout.add(delegate.createDataRow("Year of Study:", studyYear));

        return layout;
    }

    private VerticalLayout createCareerSection() {
        // Colonne per Corsi Preferiti e Difficoltà
    	VerticalLayout pref = createLabeledList("Preferred courses:", preferredCourses);
    	VerticalLayout diff = createLabeledList("Difficult courses:", difficultCourses);
    	
        // Colonne per Esami Superati e Da Sostenere
    	VerticalLayout passed = createLabeledList("Passed exams:", passedExams);
    	VerticalLayout pending = createLabeledList("Pending exams:", pendingExams);
        
        HorizontalLayout courses = new HorizontalLayout(pref, diff);
        courses.setWidthFull();
        
        HorizontalLayout exams = new HorizontalLayout(passed, pending);
        exams.setWidthFull();

        VerticalLayout v = new VerticalLayout(courses, exams);
        v.setPadding(false);
        
        return v;
    }
    
    private void configureMenu() {
    	department.setEnabled(false);
        courseName.setEnabled(false);
        degreeType.setEnabled(false);
        studyYear.setEnabled(false);
        
        university.setItems(dataService.findAllUniversities());
        university.addValueChangeListener(event -> {
        	University university = event.getValue();
        	if (university == null) {
        		department.clear();
        		department.setEnabled(false);
            } else {
                //Dynamic filter
                department.setItems(dataService.findDepartmentsByUniversity(university));
                department.setEnabled(true);
            }
        });
        
        department.addValueChangeListener(event -> {
        	Department department = event.getValue();
        	if (department == null) {
        		degreeType.clear();
        		degreeType.setEnabled(false);
            } else {
            	degreeType.setItems(DegreeType.values());
            	degreeType.setItemLabelGenerator(subject -> 
            		delegate.stringNormalization(subject.name())
	            );
            	degreeType.setEnabled(true);
            }
        });
        
        degreeType.addValueChangeListener(event -> {
        	DegreeType degreeType = event.getValue();
        	if (degreeType == null) {
        		courseName.clear();
        		courseName.setEnabled(false);
            } else {
            	//Dynamic filter
            	courseName.setItems(dataService.findCourseByDegreeType(degreeType));
            	courseName.setEnabled(true);
            	studyYear.setItems(updateYearOptions(degreeType));
            }
        });
        
        courseName.addValueChangeListener(event -> {
        	StudyCourse courseName = event.getValue();
        	if (courseName == null) {
        		studyYear.clear();
        		studyYear.setEnabled(false);
            } else {
                studyYear.setHelperText("Select 0 if beyond standard years");
            	studyYear.setEnabled(true);
            }
        });
        
        studyYear.addValueChangeListener(event -> {
        	Integer studyYear = event.getValue();
        	if (studyYear == null) {
        		preferredCourses.setVisible(false);
                difficultCourses.setVisible(false);
                passedExams.setVisible(false);
                pendingExams.setVisible(false);
        	} else {
        		//Dynamic filter
                List<Subject> materieFiltrate = dataService.findSubjects(courseName.getValue(), studyYear);
                
                if(materieFiltrate.isEmpty())
                	Notification.show("No subjects available", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                else {
	                //Update the graphic lists
	                preferredCourses.setItems(materieFiltrate);
	                difficultCourses.setItems(materieFiltrate);
	                passedExams.setItems(materieFiltrate);
	                pendingExams.setItems(materieFiltrate);
	                
	                preferredCourses.setVisible(true);
	                difficultCourses.setVisible(true);
	                passedExams.setVisible(true);
	                pendingExams.setVisible(true);
	                
	                Notification.show("Viewing subjects up to year " + studyYear);
                }
        	}
        });
    }
    
    private void setupLimitLogic() {
        addSelectionLimit(preferredCourses, "preferred courses");
        addSelectionLimit(difficultCourses, "difficult courses");
    }

    private void addSelectionLimit(MultiSelectListBox<Subject> listBox, String tipo) {
        listBox.addSelectionListener(event -> {
            if (event.getAllSelectedItems().size() > 3) {
                Notification.show("You can select a maximum of 3 subjects of " + tipo + "!");
                //Remove last selection
                listBox.deselect(event.getAddedSelection().iterator().next());
            }
        });
    }
    
    private VerticalLayout createLabeledList(String title, MultiSelectListBox<Subject> listBox) {
        Span label = new Span(title);
        label.getStyle().set("font-weight", "bold");
        
        VerticalLayout container = new VerticalLayout(label, listBox);
        container.setSpacing(false);
        container.setPadding(false);
        return container;
    }
    
    private byte[] resizeImage(byte[] originalImage) {
        try {
            InputStream is = new ByteArrayInputStream(originalImage);
            BufferedImage src = ImageIO.read(is);
            if (src == null) return originalImage;

            int targetWidth = 120;
            int targetHeight = 120;

            //Calculate the proportions for the center crop
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int x = 0, y = 0, width = srcWidth, height = srcHeight;

            if (srcWidth > srcHeight) {
                width = srcHeight;
                x = (srcWidth - srcHeight) / 2;
            } else {
                height = srcWidth;
                y = (srcHeight - srcWidth) / 2;
            }

            //Center crop
            BufferedImage cropped = src.getSubimage(x, y, width, height);

            //Resized to 120x120
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(cropped, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            //Reconvert in byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return originalImage;
        }
    }
    
    private void updatePreview(byte[] bytes) {
        StreamResource resource = new StreamResource("profile-pic", 
            () -> new ByteArrayInputStream(bytes));
        avatarPreview.setSrc(resource);
    }

    private List<Integer> updateYearOptions(DegreeType type) {
        List<Integer> years = new ArrayList<>();
        if (type == null) return years;
        int max = switch (type) {
            case BACHELOR -> 3;
            case MASTER -> 2;
            case SINGLE_CICLE_MASTER -> 5;
        };
        for (int i = 0; i <= max; i++) years.add(i);
        return years;
    }

    private void setupExclusionLogic(MultiSelectListBox<Subject> c1, MultiSelectListBox<Subject> c2) {
        c1.addSelectionListener(e -> {
        	//If an exam is preferred/passed, it cannot be difficult/pending
            e.getAllSelectedItems().forEach(item -> c2.deselect(item));
        });
        c2.addSelectionListener(e -> {
            e.getAllSelectedItems().forEach(item -> c1.deselect(item));
        });
    }
    
    private void saveProfile(UserProfile profile) {
	    try {
	        binder.writeBean(profile);
	        profileService.saveProfile(profile);
	
	        Notification notification = Notification.show("Profile updated successfully!", 3000, Notification.Position.TOP_CENTER);
	        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	
	        getUI().ifPresent(ui -> ui.navigate(PersonalArea.class));
	
	    } catch (ValidationException e) {
	        //UI data failed Binder validation
	        Notification.show("Please, correct errors in the form.", 3000, Notification.Position.MIDDLE)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        
	    } catch (Exception e) {
	        //Technical error
	        Notification.show("Technical error while saving: " + e.getMessage(), 5000, Notification.Position.BOTTOM_START)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
    }
}

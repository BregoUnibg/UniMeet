package local.unimeet.entity;

public enum CourseSubject {
	
	CALCULUS_I(DegreeName.INFORMATICA),
	CALCULUS_II(DegreeName.INFORMATICA),
	LINEAR_ALGEBRA(DegreeName.INFORMATICA),
	FOUNDATION_OF_AUTOMATICS(DegreeName.INFORMATICA),
	// Corsi Informatica
    INFORMATICA(DegreeName.INFORMATICA),
    FISICA(DegreeName.INFORMATICA),
    MATEMATICA(DegreeName.INFORMATICA),
    ECONOMIA(DegreeName.INFORMATICA),
    
    // Corsi Economia
    DATA_SCIENCE(DegreeName.ECONOMIA),
    CYBERSECURITY(DegreeName.ECONOMIA),
    INTELLIGENZA_ARTIFICIALE(DegreeName.ECONOMIA);

    private final DegreeName name;

    // Costruttore dell'Enum
    CourseSubject(DegreeName name) {
        this.name = name;
    }

    // Getter per filtrare
    public DegreeName getName() {
        return name;
    }
	
}

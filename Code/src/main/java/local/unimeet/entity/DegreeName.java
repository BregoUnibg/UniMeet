package local.unimeet.entity;

public enum DegreeName {

	// Corsi Triennali
    INFORMATICA(DegreeType.BACHELOR),
    FISICA(DegreeType.BACHELOR),
    MATEMATICA(DegreeType.BACHELOR),
    ECONOMIA(DegreeType.BACHELOR),
    
    // Corsi Magistrali
    DATA_SCIENCE(DegreeType.MASTER),
    CYBERSECURITY(DegreeType.MASTER),
    INTELLIGENZA_ARTIFICIALE(DegreeType.MASTER);

    private final DegreeType level;

    // Costruttore dell'Enum
    DegreeName(DegreeType level) {
        this.level = level;
    }

    // Getter per filtrare
    public DegreeType getLevel() {
        return level;
    }
	
}

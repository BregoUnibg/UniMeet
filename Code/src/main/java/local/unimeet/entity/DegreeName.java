package local.unimeet.entity;

public enum DegreeName {

	// Corsi Triennali
    INFORMATICA(DegreeType.TRIENNALE),
    FISICA(DegreeType.TRIENNALE),
    MATEMATICA(DegreeType.TRIENNALE),
    ECONOMIA(DegreeType.TRIENNALE),
    
    // Corsi Magistrali
    DATA_SCIENCE(DegreeType.MAGISTRALE),
    CYBERSECURITY(DegreeType.MAGISTRALE),
    INTELLIGENZA_ARTIFICIALE(DegreeType.MAGISTRALE);

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

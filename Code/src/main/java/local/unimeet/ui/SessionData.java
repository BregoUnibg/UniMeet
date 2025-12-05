package local.unimeet.ui; // O local.unimeet.entity se preferisci

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

// we create the data for the insert of new session here without creating any table in the H2 database
public class SessionData {
    public UUID id = UUID.randomUUID();
    public String materia;
    public LocalDate data;
    public LocalTime orario;
    public String stato;

    public SessionData(String materia, LocalDate data, LocalTime orario, String stato) {
        this.materia = materia;
        this.data = data;
        this.orario = orario;
        this.stato = stato;
    }
}
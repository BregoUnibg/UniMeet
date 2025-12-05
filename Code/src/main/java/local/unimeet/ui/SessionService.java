package local.unimeet.ui;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//this class stand the purpose of let the service run for both homeview and session view when they have to do something they communicate to this service and get what they need done
@Service
public class SessionService {
    private final List<SessionItem> sessions = new ArrayList<>();

    public SessionService() {
        // Dati di prova
        sessions.add(new SessionItem("Analisi Matematica 1", LocalDate.now(), LocalTime.of(14, 30), "Attivo"));
    }

    public List<SessionItem> getAll() { return sessions; }
    public void add(SessionItem s) { sessions.add(0, s); }
    public void delete(SessionItem s) { sessions.remove(s); }

    public static class SessionItem {
        public UUID id = UUID.randomUUID();
        public String materia;
        public LocalDate data;
        public LocalTime orario;
        public String stato;

        public SessionItem(String materia, LocalDate data, LocalTime orario, String stato) {
            this.materia = materia;
            this.data = data;
            this.orario = orario;
            this.stato = stato;
        }
    }
}
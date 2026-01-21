package local.unimeet.ui;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

@Component
public class Delegate implements DelegationInterface {

	@Override
    public HorizontalLayout createReputationBar(double score, int totVoters) {
        ProgressBar bar = new ProgressBar(0, 5, score);
        bar.setWidth("200px");

        // Logica colore barra basata sul punteggio
        String color;
        if (score < 1.5) color = "red";
        else if (score < 3) color = "orange";
        else if (score < 4) color = "lightgreen";
        else color = "green";
        
        bar.getElement().getStyle().set("--lumo-primary-color", color);

        // Etichetta a parole basata sulle 5 tacche
        String label;
        if (score <= 1) label = "Pessimo";
        else if (score <= 2) label = "Insufficiente";
        else if (score <= 3) label = "Valido";
        else if (score <= 4) label = "Buono";
        else label = "Eccellente";

        Span scoreText = new Span(score + "/5 " + totVoters + " " + label);
        scoreText.getStyle().set("font-size", "0.9em");

        HorizontalLayout layout = new HorizontalLayout(bar, scoreText);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }
	
	@Override
	public String stringNormalization(String s) {
    	return s.substring(0, 1).toUpperCase()+s.substring(1).replace("_", " ").toLowerCase();
    }
	
	@Override
	public HorizontalLayout createDataRow(String label, String value) {
        Span l = new Span(label);
        l.setWidth("150px");
        l.getStyle().set("font-weight", "bold");
        Span v = new Span(value);
        return new HorizontalLayout(l, v);
    }

	@Override
	public HorizontalLayout createDataRow(String label, com.vaadin.flow.component.Component value) {
		Span l = new Span(label);
        l.setWidth("150px");
        l.getStyle().set("font-weight", "bold");
        HorizontalLayout h = new HorizontalLayout(l, value);
        h.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        return h;
	}
	
}

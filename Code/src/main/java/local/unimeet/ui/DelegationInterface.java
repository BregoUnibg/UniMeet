package local.unimeet.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface DelegationInterface {

	HorizontalLayout createReputationBar(double score, int totVoters);
	String stringNormalization(String s);
	HorizontalLayout createDataRow(String label, String value);
	HorizontalLayout createDataRow(String label, Component value);
	
}

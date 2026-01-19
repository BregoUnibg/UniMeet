package local.unimeet.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "university")
public class University {
	
	@Id
	private String name;
	
	@OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Building> buildings = new ArrayList<>();
	
	public University() {
	}
	
	public void addBuilding(Building building) {
        buildings.add(building);
        building.setUniversity(this);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.setUniversity(null);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Building> getBuildings() {
		return buildings;
	}
    
}

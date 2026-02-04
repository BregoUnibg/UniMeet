package local.unimeet.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "university")
public class University {
	
	@Id
	private String name;
	
	@OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Building> buildings = new ArrayList<>();
	
	@OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Department> departments;
	
	public University() {
	}
	
	public University(String name) {
        this.name = name;
    }
	
	public void addBuilding(Building building) {
        buildings.add(building);
        building.setUniversity(this);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.setUniversity(null);
    }
    
    public void addDepartment(Department department) {
    	departments.add(department);
    	department.setUniversity(this);
    }

    public void removeDepartment(Department department) {
    	departments.remove(department);
    	department.setUniversity(null);
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
	
	public List<Department> getDepartments() {
        return departments;
    }
	
	// Fondamentale per far funzionare correttamente le ComboBox di Vaadin
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        University that = (University) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}


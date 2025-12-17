package ru.kurs.petrovkurs.model;

import jakarta.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "maintenance_acts", schema = "public")
public class MaintenanceActs {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machines machines;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private MaintenanceTypes maintenanceTypes;
    @Column(name = "date", nullable = false)
    private LocalDate date_;
    @Column(name = "engineer", nullable = false)
    private String engineer;
    @Column(name = "notes", nullable = false)
    private String notes;
    @Column(name = "signed", nullable = false)
    private Boolean signed;


    public MaintenanceActs() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceActs that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(machines, that.machines)&& Objects.equals(maintenanceTypes, that.maintenanceTypes)&& Objects.equals(date_, that.date_)&& Objects.equals(engineer, that.engineer)&& Objects.equals(notes, that.notes)&& Objects.equals(signed, that.signed);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id,machines,maintenanceTypes,date_,engineer,notes,signed);
    }

    @Override
    public String toString() {
        return engineer;
    }

    public Long getMaintenanceActsId() {

        return id;
    }
    public StringProperty getMachineModel(){
        return new SimpleStringProperty(machines.getModel());
    }
    public StringProperty getTypeName(){
        return new SimpleStringProperty(maintenanceTypes.getName_());
    }
    public void setMaintenanceActsId(Long pickupPointId) {

        this.id = id;
    }

    public Machines getMachines() {

        return machines;
    }
    public void setMachines(Machines machines){
        this.machines = machines;
    }
    public MaintenanceTypes getType() {

        return maintenanceTypes;
    }
    public void setMaintenanceTypes(MaintenanceTypes maintenanceTypes){
        this.maintenanceTypes = maintenanceTypes;
    }
    public LocalDate getDate_() {
        return date_;
    }
    public void setDate_(LocalDate date_ ) {
        this.date_ = date_;
    }

    public String getEngineer() {
        return engineer;
    }
    public void setEngineer(String engineer){
        this.engineer = engineer;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes){
        this.notes = notes;
    }
    public Boolean getSigned(){
        return signed;
    }
    public void setSigned(Boolean signed){
        this.signed = signed;
    }
    public StringProperty getPropertyEngineer() {
        return new SimpleStringProperty(this.engineer);
    }
    public StringProperty getPropertyNotes() {
        return new SimpleStringProperty(this.notes);
    }
    public StringProperty getPropertySigned() {
        return new SimpleStringProperty(this.signed.toString());
    }
    public StringProperty getPropertyDate_() {
        return new SimpleStringProperty(this.date_ != null ? this.date_.toString() : "");
    }

}

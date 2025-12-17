package ru.kurs.petrovkurs.model;

import jakarta.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "maintenance_types", schema = "public")
public class MaintenanceTypes {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name_;
    @Column(name = "interval_days", nullable = false)
    private Long intervalDays;



    public MaintenanceTypes() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceTypes that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name_, that.name_)&& Objects.equals(intervalDays, that.intervalDays);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id,name_,intervalDays);
    }

    @Override
    public String toString() {
        return name_;
    }

    public Long getMaintenanceTypesId() {
        return id;
    }

    public void setMaintenanceTypesId(Long id) {
        this.id = id;
    }

    public String getName_() {
        return name_;
    }
    public StringProperty getPropertyName() {
        return new SimpleStringProperty(this.name_);
    }
    public StringProperty getPropertyIntervalDays() {
        return new SimpleStringProperty(this.intervalDays.toString());
    }
    public void setName_(String name_){
        this.name_ =name_;
    }
    public Long getIntervalDays (){
        return  intervalDays;
    }
    public void setIntervalDays(Long intervalDays){
        this.intervalDays =intervalDays;
    }
}

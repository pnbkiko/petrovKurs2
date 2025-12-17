package ru.kurs.petrovkurs.model;

import jakarta.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "machines", schema = "public")
public class Machines {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "model", nullable = false)
    private String model;
    @Column(name = "inv_number", nullable = false)
    private String invNumber;
    @Column(name = "commissioned_at", nullable = false)
    private LocalDate commissionedAt;


    public Machines() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Machines that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        final int hashCode = 17 * id.hashCode() + 31 * model.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return model;
    }

    public Long getMachinesId() {
        return id;
    }

    public void setMachinesId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }
    public LocalDate getCommissionedAt() {
        return commissionedAt;
    }
    public String getInvNumber() {
        return invNumber;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public void setInvNumber(String invNumber) {
        this.invNumber = invNumber;
    }
    public void setCommissionedAt(LocalDate commissionedAt ) {
        this.commissionedAt = commissionedAt;
    }
    public StringProperty getPropertyModel() {
        return new SimpleStringProperty(this.model);
    }
    public StringProperty getPropertyInvNumber() {
        return new SimpleStringProperty(this.invNumber);
    }
    public StringProperty getPropertyCommissionedAt() {
        return new SimpleStringProperty(this.commissionedAt != null ? this.commissionedAt.toString() : "");
    }

}

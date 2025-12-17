module ru.trade.tradeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.desktop;
    requires javafx.swing;
    requires org.hibernate.validator;
    requires org.postgresql.jdbc;
    requires java.management;

    requires itextpdf;
    opens ru.kurs.petrovkurs to javafx.fxml;
    opens ru.kurs.petrovkurs.model to org.hibernate.orm.core, javafx.base;
    exports ru.kurs.petrovkurs;
    exports ru.kurs.petrovkurs.controller;
    opens ru.kurs.petrovkurs.util to org.hibernate.orm.core;
    opens ru.kurs.petrovkurs.controller to javafx.base, javafx.fxml, org.hibernate.orm.core;
}
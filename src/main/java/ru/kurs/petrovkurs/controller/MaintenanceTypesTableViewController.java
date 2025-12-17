package ru.kurs.petrovkurs.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.kurs.petrovkurs.HelloApplication;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.model.MaintenanceTypes;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceActsService;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;
import ru.kurs.petrovkurs.service.MaintenanceTypesService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.kurs.petrovkurs.util.Manager.*;

public class MaintenanceTypesTableViewController implements Initializable {

    private int itemsCount;

    private MaintenanceTypesService maintenanceTypesService = new MaintenanceTypesService();
    @FXML
    private TableColumn<MaintenanceTypes, String> TableColumnName,TableColumnIntervalDays;
    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;
    @FXML
    private TableView<MaintenanceTypes> TableViewMaintenanceTypes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initController();
    }

    public void initController() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String todayDate = LocalDate.now().format(formatter);
        LabelDate.setText("Сегодня: " + todayDate);
        setCellValueFactories();
        filterData();
    }



    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-types-edit-view.fxml"));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setTitle("Изменить данные");
        newWindow.initOwner(Manager.secondStage);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.setScene(scene);
        Manager.currentStage = newWindow;
        newWindow.showAndWait();
        Manager.currentStage = null;
        filterData();
    }
    void filterData() {
        List<MaintenanceTypes> maintenanceTypes =maintenanceTypesService.findAll();
        itemsCount = maintenanceTypes.size();



        TableViewMaintenanceTypes.getItems().clear();
        for (MaintenanceTypes mt : maintenanceTypes) {
            TableViewMaintenanceTypes.getItems().add(mt);
        }
        int filteredItemsCount = maintenanceTypes.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }

    private void setCellValueFactories() {



        TableColumnName.setCellValueFactory(cellData -> cellData.getValue().getPropertyName());
        TableColumnIntervalDays.setCellValueFactory(cellData -> cellData.getValue().getPropertyIntervalDays());


    }

    @FXML
    void MenuItemAddAction(ActionEvent event) {
       Manager.currentMaintenanceTypes = null;
        ShowEditProductWindow();
        filterData();
    }

    @FXML
    void MenuItemBackAction(ActionEvent event) {
        if (Manager.secondStage != null) {
            Manager.secondStage.close();
        }
        Manager.mainStage.show();
    }



    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        MaintenanceTypes maintenanceTypes = TableViewMaintenanceTypes.getSelectionModel().getSelectedItem();


        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            maintenanceTypesService.delete(maintenanceTypes);
            filterData();
        }
    }



}


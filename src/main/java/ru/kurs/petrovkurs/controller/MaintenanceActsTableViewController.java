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
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import ru.kurs.petrovkurs.HelloApplication;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceActsService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.kurs.petrovkurs.util.Manager.*;

public class MaintenanceActsTableViewController implements Initializable {

    private int itemsCount;

    private MaintenanceActsService maintenanceActsService = new MaintenanceActsService();
    @FXML
    private DatePicker DatePickerFilter;

    @FXML
    private TableColumn<MaintenanceActs, String> TableColumnMachines, TableColumnTypes, TableColumnDate, TableColumnEngineer, TableColumnNotes, TableColumnSigned;

    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;
    @FXML
    private TableView<MaintenanceActs> TableViewMaintenanceActs;

    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-acts-edit-view.fxml"));

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
        filterData(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initController();
    }

    public void initController() {
        // Устанавливаем русский формат даты для метки
        setupRussianDateFormat();

        // Настраиваем DatePicker с русским форматом
        setupRussianDatePicker();

        // Инициализируем таблицу
        setCellValueFactories();

        // Настраиваем фильтр
        DatePickerFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

        filterData(null);
    }

    private void setupRussianDateFormat() {
        // Устанавливаем текущую дату в русском формате
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));
        String todayDate = LocalDate.now().format(formatter);
        LabelDate.setText("Дата: " + todayDate);
    }

    private void setupRussianDatePicker() {
        // Устанавливаем русский формат для DatePicker
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));

        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };

        DatePickerFilter.setConverter(converter);
        DatePickerFilter.setPromptText("дд.мм.гггг");

        // Устанавливаем русские названия месяцев в календаре
        DatePickerFilter.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() == 10) {
                try {
                    LocalDate date = LocalDate.parse(newValue, formatter);
                    DatePickerFilter.setValue(date);
                } catch (DateTimeParseException e) {
                    // Оставляем как есть, если введен неправильный формат
                }
            }
        });
    }

    void filterData(LocalDate dateFilter) {
        List<MaintenanceActs> maintenanceActs = maintenanceActsService.findAll();
        itemsCount = maintenanceActs.size();

        List<MaintenanceActs> filteredList = maintenanceActs.stream()
                .filter(act -> {
                    if (dateFilter != null) {
                        return act.getDate_().equals(dateFilter);
                    }
                    return true; // если дата не выбрана, показываем все
                })
                .collect(Collectors.toList());

        TableViewMaintenanceActs.getItems().setAll(filteredList);

        int filteredItemsCount = filteredList.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }

    private void setCellValueFactories() {
        TableColumnMachines.setCellValueFactory(cellData -> cellData.getValue().getMachineModel());
        TableColumnTypes.setCellValueFactory(cellData -> cellData.getValue().getTypeName());

        // Устанавливаем форматирование даты в таблице
        TableColumnDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate_();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
                return new SimpleStringProperty(formatter.format(date));
            } else {
                return new SimpleStringProperty("");
            }
        });

        TableColumnEngineer.setCellValueFactory(cellData -> cellData.getValue().getPropertyEngineer());
        TableColumnNotes.setCellValueFactory(cellData -> cellData.getValue().getPropertyNotes());
        TableColumnSigned.setCellValueFactory(cellData -> cellData.getValue().getPropertySigned());
    }

    @FXML
    void MenuItemAddAction(ActionEvent event) {
        Manager.currentMaintenanceActs = null;
        ShowEditProductWindow();
        filterData(null);
    }




    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        MaintenanceActs maintenanceActs = TableViewMaintenanceActs.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            maintenanceActsService.delete(maintenanceActs);
            filterData(null);
        }
    }


}
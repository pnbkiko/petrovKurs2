package ru.kurs.petrovkurs.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import ru.kurs.petrovkurs.HelloApplication;
import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceActsService;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import static ru.kurs.petrovkurs.util.Manager.*;

public class MaintenanceScheduleTableViewController implements Initializable {

    private int itemsCount;
    private MaintenanceScheduleService maintenanceScheduleService = new MaintenanceScheduleService();

    @FXML
    private DatePicker DatePickerFilter;

    @FXML
    private TableColumn<MaintenanceSchedule, String> TableColumnMachines, TableColumnTypes, TableColumnLastDone;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> TableColumnNextDue;

    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;


    @FXML
    private TableView<MaintenanceSchedule> TableViewMaintenanceSchedule;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initController();
    }

    public void initController() {
        // Устанавливаем русский формат даты
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
        // Устанавливаем текущую дату в русском формате с месяцем прописью
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
    }

    @FXML
    private void clearFilter(ActionEvent event) {
        DatePickerFilter.setValue(null);
        filterData(null);
    }

    void filterData(LocalDate dateFilter) {
        List<MaintenanceSchedule> maintenanceSchedules = maintenanceScheduleService.findAll();
        itemsCount = maintenanceSchedules.size();

        List<MaintenanceSchedule> filteredList = maintenanceSchedules.stream()
                .filter(act -> {
                    if (dateFilter != null) {
                        return act.getLastDone().equals(dateFilter);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        TableViewMaintenanceSchedule.getItems().setAll(filteredList);

        int filteredItemsCount = filteredList.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }

    private void setCellValueFactories() {
        TableColumnMachines.setCellValueFactory(cellData -> cellData.getValue().getMachineModel());
        TableColumnTypes.setCellValueFactory(cellData -> cellData.getValue().getTypeName());

        // Для колонки "Следующее ТО"
        TableColumnNextDue.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNextDue()));
        TableColumnNextDue.setCellFactory(column -> new TableCell<MaintenanceSchedule, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(formatter.format(item));
                    LocalDate today = LocalDate.now();
                    if (item.isBefore(today)) {
                        // Просроченные
                        setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: black;"); // красный
                    } else if (item.equals(today)) {
                        // Сегодня
                        setStyle("-fx-background-color: #ffff66; -fx-text-fill: black;"); // желтый
                    } else if (item.equals(today.plusDays(1))) {
                        // Завтра
                        setStyle("-fx-background-color: #66ffff; -fx-text-fill: black;"); // голубой
                    } else if (item.equals(today.plusDays(2))) {
                        // Послезавтра
                        setStyle("-fx-background-color: #99ff99; -fx-text-fill: black;"); // зеленый
                    } else {
                        setStyle("-fx-text-fill: black;"); // черный текст без фона
                    }
                }
            }
        });

        // Для колонки "Последнее ТО"
        TableColumnLastDone.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getLastDone();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
                return new SimpleStringProperty(formatter.format(date));
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    @FXML
    void MenuItemAddAction(ActionEvent event) {
        Manager.currentMaintenanceSchedule = null;
        ShowEditProductWindow();
        filterData(null);
    }

    @FXML
    void MenuItemBackAction(ActionEvent event) {
        if (Manager.secondStage != null) {
            Manager.secondStage.close();
        }
        Manager.mainStage.show();
    }

    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-schedule-edit-view.fxml"));

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

    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        MaintenanceSchedule maintenanceSchedule = TableViewMaintenanceSchedule.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            maintenanceScheduleService.delete(maintenanceSchedule);
            filterData(null);
        }
    }
}
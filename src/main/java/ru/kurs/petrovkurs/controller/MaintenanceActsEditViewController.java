package ru.kurs.petrovkurs.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.kurs.petrovkurs.model.*;
import ru.kurs.petrovkurs.service.*;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MaintenanceActsEditViewController implements Initializable {
    @FXML
    private Button BtnCancel;
    @FXML
    private Button BtnSave;
    private MaintenanceTypesService maintenanceTypesService = new MaintenanceTypesService();
    private MaintenanceActsService maintenanceActsService = new MaintenanceActsService();
    private MachinesService machinesService = new MachinesService();

    @FXML
    private TextField TextFieldEngineer;  // Оставляем как TextField

    @FXML
    private TextArea TextFieldNotes;      // ИЗМЕНИЛИ: TextArea вместо TextField

    @FXML
    private DatePicker DatePickerDateWork;

    @FXML
    private ComboBox<MaintenanceTypes> ComboBoxType;

    @FXML
    private ComboBox<Machines> ComboBoxMachine;

    @FXML
    private CheckBox CheckBoxSigned;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Настраиваем ComboBox для отображения
        setupComboBoxes();

        if (Manager.currentMaintenanceActs != null) {
            loadExistingData();
        } else {
            Manager.currentMaintenanceActs = new MaintenanceActs();
            // Устанавливаем сегодняшнюю дату по умолчанию для новой записи
            DatePickerDateWork.setValue(LocalDate.now());
        }
    }

    private void setupComboBoxes() {
        // Загружаем данные
        ComboBoxType.setItems(FXCollections.observableArrayList(maintenanceTypesService.findAll()));
        ComboBoxMachine.setItems(FXCollections.observableArrayList(machinesService.findAll()));

        // Настраиваем отображение для ComboBox
        ComboBoxType.setCellFactory(param -> new ListCell<MaintenanceTypes>() {
            @Override
            protected void updateItem(MaintenanceTypes item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Используем toString() или другой доступный метод
                    setText(item.toString());
                }
            }
        });

        ComboBoxMachine.setCellFactory(param -> new ListCell<Machines>() {
            @Override
            protected void updateItem(Machines item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        // Настраиваем отображение в выбранном элементе
        ComboBoxType.setButtonCell(new ListCell<MaintenanceTypes>() {
            @Override
            protected void updateItem(MaintenanceTypes item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        ComboBoxMachine.setButtonCell(new ListCell<Machines>() {
            @Override
            protected void updateItem(Machines item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
    }

    private void loadExistingData() {
        try {
            ComboBoxMachine.setValue(Manager.currentMaintenanceActs.getMachines());
            ComboBoxType.setValue(Manager.currentMaintenanceActs.getType());
            TextFieldEngineer.setText(Manager.currentMaintenanceActs.getEngineer());

            // Теперь используем TextArea
            String notes = Manager.currentMaintenanceActs.getNotes();
            if (notes != null) {
                TextFieldNotes.setText(notes);
            }

            DatePickerDateWork.setValue(Manager.currentMaintenanceActs.getDate_());
            CheckBoxSigned.setSelected(Manager.currentMaintenanceActs.getSigned());

        } catch (Exception e) {
            MessageBox("Ошибка загрузки",
                    "Не удалось загрузить данные",
                    e.getMessage(),
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    void BtnCancelAction(ActionEvent event) {
        Stage stage = (Stage) BtnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void BtnSaveAction(ActionEvent event) throws IOException {
        String error = checkFields().toString();
        if (!error.isEmpty()) {
            MessageBox("Ошибка", "Заполните поля", error, Alert.AlertType.ERROR);
            return;
        }

        Manager.currentMaintenanceActs.setMachines(ComboBoxMachine.getValue());
        Manager.currentMaintenanceActs.setMaintenanceTypes(ComboBoxType.getValue());
        Manager.currentMaintenanceActs.setEngineer(TextFieldEngineer.getText());
        Manager.currentMaintenanceActs.setSigned(CheckBoxSigned.isSelected());

        // Теперь используем TextArea
        Manager.currentMaintenanceActs.setNotes(TextFieldNotes.getText());

        LocalDate date = DatePickerDateWork.getValue();
        if (date != null) {
            Manager.currentMaintenanceActs.setDate_(date);
        }

        if (Manager.currentMaintenanceActs.getMaintenanceActsId() == null) {
            maintenanceActsService.save(Manager.currentMaintenanceActs);
            MessageBox("Информация", "", "Данные сохранены успешно", Alert.AlertType.INFORMATION);
        } else {
            maintenanceActsService.update(Manager.currentMaintenanceActs);
            MessageBox("Информация", "", "Данные обновлены успешно", Alert.AlertType.INFORMATION);
        }
        Stage stage = (Stage) BtnSave.getScene().getWindow();
        stage.close();
    }

    StringBuilder checkFields() {
        StringBuilder error = new StringBuilder();
        if (ComboBoxMachine.getValue() == null) {
            error.append("• Выберите станок\n");
            ComboBoxMachine.getStyleClass().add("error-field");
        } else {
            ComboBoxMachine.getStyleClass().remove("error-field");
        }

        if (ComboBoxType.getValue() == null) {
            error.append("• Выберите тип ТО\n");
            ComboBoxType.getStyleClass().add("error-field");
        } else {
            ComboBoxType.getStyleClass().remove("error-field");
        }

        if (TextFieldEngineer.getText().isEmpty()) {
            error.append("• Укажите инженера\n");
            TextFieldEngineer.getStyleClass().add("error-field");
        } else {
            TextFieldEngineer.getStyleClass().remove("error-field");
        }

        if (DatePickerDateWork.getValue() == null) {
            error.append("• Укажите дату\n");
            DatePickerDateWork.getStyleClass().add("error-field");
        } else {
            DatePickerDateWork.getStyleClass().remove("error-field");
        }

        if (!CheckBoxSigned.isSelected()) {
            error.append("• Подтвердите согласие\n");
            CheckBoxSigned.getStyleClass().add("error-field");
        } else {
            CheckBoxSigned.getStyleClass().remove("error-field");
        }

        return error;
    }
}
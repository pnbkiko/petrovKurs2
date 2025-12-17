package ru.kurs.petrovkurs.controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MachinesEditViewController implements Initializable {

    @FXML private TextField TextFieldModel, TextFieldInvNumber;
    @FXML private DatePicker DatePickerCommissionedAt;
    @FXML private Button BtnCancel, BtnSave;
    @FXML private GridPane mainGrid;
    @FXML private Label errorLabel;

    private MachinesService machinesService = new MachinesService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Загружаем данные если редактируем существующий станок
        if (Manager.currentMachines != null) {
            loadExistingData();
        } else {
            Manager.currentMachines = new Machines();
            // Устанавливаем сегодняшнюю дату по умолчанию для новой записи
            DatePickerCommissionedAt.setValue(LocalDate.now());
        }

        // Настраиваем валидацию
        setupValidation();
    }

    private void loadExistingData() {
        try {
            TextFieldModel.setText(Manager.currentMachines.getModel());
            TextFieldInvNumber.setText(Manager.currentMachines.getInvNumber());

            LocalDate date = Manager.currentMachines.getCommissionedAt();
            if (date != null) {
                DatePickerCommissionedAt.setValue(date);
            }
        } catch (Exception e) {
            showError("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    private void setupValidation() {
        // Валидация при изменении значений
        TextFieldModel.textProperty().addListener((obs, oldVal, newVal) -> validateField(TextFieldModel));
        TextFieldInvNumber.textProperty().addListener((obs, oldVal, newVal) -> validateField(TextFieldInvNumber));
        DatePickerCommissionedAt.valueProperty().addListener((obs, oldVal, newVal) -> validateField(DatePickerCommissionedAt));
    }

    private void validateField(Control field) {
        // Убираем стиль ошибки при валидации
        field.getStyleClass().remove("error-field");

        if (field instanceof TextField) {
            TextField textField = (TextField) field;
            if (textField.getText() == null || textField.getText().trim().isEmpty()) {
                field.getStyleClass().add("error-field");
            }
        } else if (field instanceof DatePicker) {
            DatePicker datePicker = (DatePicker) field;
            if (datePicker.getValue() == null) {
                field.getStyleClass().add("error-field");
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Проверяем каждое поле
        if (TextFieldModel.getText() == null || TextFieldModel.getText().trim().isEmpty()) {
            TextFieldModel.getStyleClass().add("error-field");
            errorMessage.append("• Укажите модель станка\n");
            isValid = false;
        } else {
            TextFieldModel.getStyleClass().remove("error-field");
        }

        if (TextFieldInvNumber.getText() == null || TextFieldInvNumber.getText().trim().isEmpty()) {
            TextFieldInvNumber.getStyleClass().add("error-field");
            errorMessage.append("• Укажите инвентарный номер\n");
            isValid = false;
        } else {
            TextFieldInvNumber.getStyleClass().remove("error-field");
        }

        if (DatePickerCommissionedAt.getValue() == null) {
            DatePickerCommissionedAt.getStyleClass().add("error-field");
            errorMessage.append("• Укажите дату ввода\n");
            isValid = false;
        } else {
            DatePickerCommissionedAt.getStyleClass().remove("error-field");
        }

        // Показываем ошибки если есть
        if (!isValid && errorLabel != null) {
            errorLabel.setText(errorMessage.toString());
            errorLabel.setVisible(true);

            // Анимация появления ошибки
            FadeTransition ft = new FadeTransition(Duration.millis(300), errorLabel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } else if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

        return isValid;
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(300), errorLabel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    @FXML
    void BtnCancelAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void BtnSaveAction(ActionEvent event) throws IOException {
        if (!validateForm()) {
            MessageBox("Ошибка валидации",
                    "Не все обязательные поля заполнены",
                    "Пожалуйста, заполните все поля, отмеченные звездочкой (*)",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            Manager.currentMachines.setModel(TextFieldModel.getText().trim());
            Manager.currentMachines.setInvNumber(TextFieldInvNumber.getText().trim());

            LocalDate date = DatePickerCommissionedAt.getValue();
            if (date != null) {
                Manager.currentMachines.setCommissionedAt(date);
            }

            if (Manager.currentMachines.getMachinesId() == null) {
                machinesService.save(Manager.currentMachines);
                MessageBox("Успешно",
                        "Станок сохранен",
                        "Данные успешно сохранены в систему",
                        Alert.AlertType.INFORMATION);
            } else {
                machinesService.update(Manager.currentMachines);
                MessageBox("Успешно",
                        "Станок обновлен",
                        "Данные успешно обновлены",
                        Alert.AlertType.INFORMATION);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            MessageBox("Ошибка",
                    "Не удалось сохранить данные",
                    "Произошла ошибка при сохранении: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
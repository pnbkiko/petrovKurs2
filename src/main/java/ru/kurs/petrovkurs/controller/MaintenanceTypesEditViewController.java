package ru.kurs.petrovkurs.controller;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.kurs.petrovkurs.model.MaintenanceTypes;
import ru.kurs.petrovkurs.service.MaintenanceTypesService;
import ru.kurs.petrovkurs.util.Manager;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MaintenanceTypesEditViewController implements Initializable {

    @FXML private TextField TextFieldName;
    @FXML private TextField TextFieldIntervalDays;
    @FXML private ComboBox<String> ComboBoxIntervalPreset;
    @FXML private Button BtnCancel, BtnSave;
    @FXML private Label errorLabel;

    private MaintenanceTypesService maintenanceTypesService = new MaintenanceTypesService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Инициализируем ComboBox с шаблонами интервалов
        ObservableList<String> intervalPresets = FXCollections.observableArrayList(
                "Ежедневно (1 день)",
                "Еженедельно (7 дней)",
                "Ежемесячно (30 дней)",
                "Ежеквартально (90 дней)",
                "Раз в полгода (180 дней)",
                "Ежегодно (365 дней)"
        );
        ComboBoxIntervalPreset.setItems(intervalPresets);

        // Настраиваем слушатель для ComboBox с шаблонами
        ComboBoxIntervalPreset.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applyPresetInterval(newVal);
            }
        });

        // Настраиваем валидацию
        setupValidation();

        // Загружаем данные если редактируем существующий вид ТО
        if (Manager.currentMaintenanceTypes != null) {
            loadExistingData();
        } else {
            Manager.currentMaintenanceTypes = new MaintenanceTypes();
        }
    }

    private void applyPresetInterval(String preset) {
        switch (preset) {
            case "Ежедневно (1 день)":
                TextFieldIntervalDays.setText("1");
                break;
            case "Еженедельно (7 дней)":
                TextFieldIntervalDays.setText("7");
                break;
            case "Ежемесячно (30 дней)":
                TextFieldIntervalDays.setText("30");
                break;
            case "Ежеквартально (90 дней)":
                TextFieldIntervalDays.setText("90");
                break;
            case "Раз в полгода (180 дней)":
                TextFieldIntervalDays.setText("180");
                break;
            case "Ежегодно (365 дней)":
                TextFieldIntervalDays.setText("365");
                break;
        }
    }

    private void loadExistingData() {
        try {
            TextFieldName.setText(Manager.currentMaintenanceTypes.getName_());

            Long intervalDays = Manager.currentMaintenanceTypes.getIntervalDays();
            if (intervalDays != null) {
                TextFieldIntervalDays.setText(intervalDays.toString());

                // Устанавливаем соответствующий шаблон в ComboBox
                setPresetFromInterval(intervalDays);
            }

        } catch (Exception e) {
            showError("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    private void setPresetFromInterval(Long intervalDays) {
        if (intervalDays == null) return;

        String preset = "";
        switch (intervalDays.intValue()) {
            case 1: preset = "Ежедневно (1 день)"; break;
            case 7: preset = "Еженедельно (7 дней)"; break;
            case 30: preset = "Ежемесячно (30 дней)"; break;
            case 90: preset = "Ежеквартально (90 дней)"; break;
            case 180: preset = "Раз в полгода (180 дней)"; break;
            case 365: preset = "Ежегодно (365 дней)"; break;
        }

        if (!preset.isEmpty()) {
            ComboBoxIntervalPreset.setValue(preset);
        }
    }

    private void setupValidation() {
        // Валидация при изменении значений
        TextFieldName.textProperty().addListener((obs, oldVal, newVal) -> validateField(TextFieldName));
        TextFieldIntervalDays.textProperty().addListener((obs, oldVal, newVal) -> {
            validateField(TextFieldIntervalDays);
            validateInterval(newVal);
        });
    }

    private void validateField(TextField field) {
        // Убираем стиль ошибки при валидации
        field.getStyleClass().remove("error-field");

        if (field.getText() == null || field.getText().trim().isEmpty()) {
            field.getStyleClass().add("error-field");
        }
    }

    private void validateInterval(String intervalText) {
        try {
            if (intervalText != null && !intervalText.trim().isEmpty()) {
                Long interval = Long.parseLong(intervalText);
                if (interval <= 0) {
                    TextFieldIntervalDays.getStyleClass().add("error-field");
                }
            }
        } catch (NumberFormatException e) {
            TextFieldIntervalDays.getStyleClass().add("error-field");
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Проверяем название
        if (TextFieldName.getText() == null || TextFieldName.getText().trim().isEmpty()) {
            TextFieldName.getStyleClass().add("error-field");
            errorMessage.append("• Введите название вида ТО\n");
            isValid = false;
        } else {
            TextFieldName.getStyleClass().remove("error-field");
        }

        // Проверяем интервал
        if (TextFieldIntervalDays.getText() == null || TextFieldIntervalDays.getText().trim().isEmpty()) {
            TextFieldIntervalDays.getStyleClass().add("error-field");
            errorMessage.append("• Введите интервал в днях\n");
            isValid = false;
        } else {
            try {
                Long interval = Long.parseLong(TextFieldIntervalDays.getText().trim());
                if (interval <= 0) {
                    TextFieldIntervalDays.getStyleClass().add("error-field");
                    errorMessage.append("• Интервал должен быть больше 0\n");
                    isValid = false;
                } else {
                    TextFieldIntervalDays.getStyleClass().remove("error-field");
                }
            } catch (NumberFormatException e) {
                TextFieldIntervalDays.getStyleClass().add("error-field");
                errorMessage.append("• Интервал должен быть числом\n");
                isValid = false;
            }
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
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (!validateForm()) {
            MessageBox("Ошибка валидации",
                    "Не все обязательные поля заполнены",
                    "Пожалуйста, заполните все поля, отмеченные звездочкой (*)",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            // Устанавливаем значения
            Manager.currentMaintenanceTypes.setName_(TextFieldName.getText().trim());

            try {
                Long intervalDays = Long.parseLong(TextFieldIntervalDays.getText().trim());
                Manager.currentMaintenanceTypes.setIntervalDays(intervalDays);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Неверный формат интервала");
            }

            // Сохраняем или обновляем
            try {
                maintenanceTypesService.update(Manager.currentMaintenanceTypes);
                MessageBox("Успешно",
                        "Вид ТО обновлен",
                        "Данные успешно обновлены",
                        Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                // Если не удалось обновить, пробуем сохранить как новую запись
                maintenanceTypesService.save(Manager.currentMaintenanceTypes);
                MessageBox("Успешно",
                        "Вид ТО сохранен",
                        "Данные успешно сохранены в систему",
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
package ru.kurs.petrovkurs.controller;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.model.MaintenanceTypes;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;
import ru.kurs.petrovkurs.service.MaintenanceTypesService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MaintenanceScheduleEditViewController implements Initializable {

    @FXML private ComboBox<Machines> ComboBoxMachine;
    @FXML private ComboBox<MaintenanceTypes> ComboBoxType;
    @FXML private DatePicker DatePickerLastDone;
    @FXML private DatePicker DatePickerNextDue;
    @FXML private Button BtnCancel, BtnSave;
    @FXML private GridPane mainGrid;
    @FXML private Label errorLabel;
    @FXML private Label intervalInfoLabel; // Для отображения информации о периодичности

    private MachinesService machinesService = new MachinesService();
    private MaintenanceTypesService maintenanceTypesService = new MaintenanceTypesService();
    private MaintenanceScheduleService maintenanceScheduleService = new MaintenanceScheduleService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Загружаем данные в ComboBox
        setupComboBoxes();

        // Загружаем данные если редактируем существующий график
        if (Manager.currentMaintenanceSchedule != null) {
            loadExistingData();
        } else {
            Manager.currentMaintenanceSchedule = new MaintenanceSchedule();
            // Устанавливаем сегодняшнюю дату по умолчанию для новой записи
            DatePickerLastDone.setValue(LocalDate.now());
            // Автоматически рассчитываем дату следующего ТО
            calculateNextDueDate();
        }

        // Настраиваем валидацию
        setupValidation();

        // Настраиваем слушатели для автоматического расчета даты
        setupListeners();
    }

    private void setupComboBoxes() {
        // Загружаем данные в ComboBox
        ComboBoxMachine.setItems(FXCollections.observableArrayList(machinesService.findAll()));
        ComboBoxType.setItems(FXCollections.observableArrayList(maintenanceTypesService.findAll()));

        // Настраиваем отображение элементов в ComboBox
        ComboBoxMachine.setCellFactory(param -> new ListCell<Machines>() {
            @Override
            protected void updateItem(Machines item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Используем модель станка для отображения
                    setText(item.getModel());
                }
            }
        });

        ComboBoxType.setCellFactory(param -> new ListCell<MaintenanceTypes>() {
            @Override
            protected void updateItem(MaintenanceTypes item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Отображаем название и интервал в днях
                    String intervalText = getIntervalText(item.getIntervalDays());
                    setText(item.getName_() + " (" + intervalText + ")");
                }
            }
        });

        // Настраиваем отображение в выбранном элементе
        ComboBoxMachine.setButtonCell(new ListCell<Machines>() {
            @Override
            protected void updateItem(Machines item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getModel());
            }
        });

        ComboBoxType.setButtonCell(new ListCell<MaintenanceTypes>() {
            @Override
            protected void updateItem(MaintenanceTypes item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String intervalText = getIntervalText(item.getIntervalDays());
                    setText(item.getName_() + " (" + intervalText + ")");
                }
            }
        });
    }

    private String getIntervalText(Long intervalDays) {
        if (intervalDays == null || intervalDays == 0) {
            return "период не задан";
        }

        if (intervalDays == 30) {
            return "ежемесячно";
        } else if (intervalDays == 90) {
            return "ежеквартально";
        } else if (intervalDays == 180) {
            return "раз в полгода";
        } else if (intervalDays == 365) {
            return "ежегодно";
        } else {
            return intervalDays + " дней";
        }
    }

    private void setupListeners() {
        // При изменении даты последнего ТО, автоматически рассчитываем дату следующего ТО
        DatePickerLastDone.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                calculateNextDueDate();
                updateIntervalInfo();
            }
        });

        // При изменении вида ТО, пересчитываем дату следующего ТО
        ComboBoxType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                calculateNextDueDate();
                updateIntervalInfo();
            }
        });
    }

    private void calculateNextDueDate() {
        LocalDate lastDone = DatePickerLastDone.getValue();
        MaintenanceTypes type = ComboBoxType.getValue();

        if (lastDone != null && type != null) {
            // Используем intervalDays из модели MaintenanceTypes
            Long intervalDays = type.getIntervalDays();

            if (intervalDays != null && intervalDays > 0) {
                // Добавляем количество дней из intervalDays
                DatePickerNextDue.setValue(lastDone.plusDays(intervalDays));
            } else {
                // Если intervalDays не задан или равен 0, используем значение по умолчанию (6 месяцев)
                DatePickerNextDue.setValue(lastDone.plusMonths(6));
            }
        } else if (lastDone != null) {
            // Если тип ТО не выбран, используем значение по умолчанию
            DatePickerNextDue.setValue(lastDone.plusMonths(6));
        }
    }

    private void updateIntervalInfo() {
        MaintenanceTypes type = ComboBoxType.getValue();

        if (type != null && intervalInfoLabel != null) {
            Long intervalDays = type.getIntervalDays();
            String intervalText = getIntervalText(intervalDays);

            if (intervalDays != null && intervalDays > 0) {
                intervalInfoLabel.setText("Периодичность: " + intervalText);
                intervalInfoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px;");
            } else {
                intervalInfoLabel.setText("Периодичность не задана (используется 6 месяцев по умолчанию)");
                intervalInfoLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-size: 12px;");
            }
            intervalInfoLabel.setVisible(true);
        } else if (intervalInfoLabel != null) {
            intervalInfoLabel.setVisible(false);
        }
    }

    private void loadExistingData() {
        try {
            ComboBoxMachine.setValue(Manager.currentMaintenanceSchedule.getMachines());
            ComboBoxType.setValue(Manager.currentMaintenanceSchedule.getType());
            DatePickerLastDone.setValue(Manager.currentMaintenanceSchedule.getLastDone());
            DatePickerNextDue.setValue(Manager.currentMaintenanceSchedule.getNextDue());

            // Обновляем информацию о периодичности
            updateIntervalInfo();
        } catch (Exception e) {
            showError("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    private void setupValidation() {
        // Валидация при изменении значений
        ComboBoxMachine.valueProperty().addListener((obs, oldVal, newVal) -> validateField(ComboBoxMachine));
        ComboBoxType.valueProperty().addListener((obs, oldVal, newVal) -> validateField(ComboBoxType));
        DatePickerLastDone.valueProperty().addListener((obs, oldVal, newVal) -> validateField(DatePickerLastDone));
        DatePickerNextDue.valueProperty().addListener((obs, oldVal, newVal) -> validateField(DatePickerNextDue));
    }

    private void validateField(Control field) {
        // Убираем стиль ошибки при валидации
        field.getStyleClass().remove("error-field");

        if (field instanceof ComboBox) {
            ComboBox<?> combo = (ComboBox<?>) field;
            if (combo.getValue() == null) {
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
        if (ComboBoxMachine.getValue() == null) {
            ComboBoxMachine.getStyleClass().add("error-field");
            errorMessage.append("• Выберите станок\n");
            isValid = false;
        } else {
            ComboBoxMachine.getStyleClass().remove("error-field");
        }

        if (ComboBoxType.getValue() == null) {
            ComboBoxType.getStyleClass().add("error-field");
            errorMessage.append("• Выберите вид ТО\n");
            isValid = false;
        } else {
            ComboBoxType.getStyleClass().remove("error-field");
        }

        if (DatePickerLastDone.getValue() == null) {
            DatePickerLastDone.getStyleClass().add("error-field");
            errorMessage.append("• Укажите дату последнего ТО\n");
            isValid = false;
        } else {
            DatePickerLastDone.getStyleClass().remove("error-field");
        }

        if (DatePickerNextDue.getValue() == null) {
            DatePickerNextDue.getStyleClass().add("error-field");
            errorMessage.append("• Укажите дату следующего ТО\n");
            isValid = false;
        } else {
            DatePickerNextDue.getStyleClass().remove("error-field");
        }

        // Проверяем, что дата следующего ТО позже даты последнего ТО
        if (DatePickerLastDone.getValue() != null && DatePickerNextDue.getValue() != null) {
            if (!DatePickerNextDue.getValue().isAfter(DatePickerLastDone.getValue())) {
                DatePickerNextDue.getStyleClass().add("error-field");
                errorMessage.append("• Дата следующего ТО должна быть позже даты последнего ТО\n");
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
    void handleSave(ActionEvent event) throws IOException {
        if (!validateForm()) {
            MessageBox("Ошибка валидации",
                    "Не все обязательные поля заполнены",
                    "Пожалуйста, заполните все поля, отмеченные звездочкой (*)",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            // Устанавливаем значения
            Manager.currentMaintenanceSchedule.setMachines(ComboBoxMachine.getValue());
            Manager.currentMaintenanceSchedule.setType(ComboBoxType.getValue());
            Manager.currentMaintenanceSchedule.setLastDone(DatePickerLastDone.getValue());
            Manager.currentMaintenanceSchedule.setNextDue(DatePickerNextDue.getValue());

            // Сохраняем или обновляем
            try {
                maintenanceScheduleService.update(Manager.currentMaintenanceSchedule);
                MessageBox("Успешно",
                        "График ТО обновлен",
                        "Данные успешно обновлены",
                        Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                // Если не удалось обновить, пробуем сохранить как новую запись
                maintenanceScheduleService.save(Manager.currentMaintenanceSchedule);
                MessageBox("Успешно",
                        "График ТО сохранен",
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
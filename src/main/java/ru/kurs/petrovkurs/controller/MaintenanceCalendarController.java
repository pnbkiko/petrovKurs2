package ru.kurs.petrovkurs.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.kurs.petrovkurs.model.MaintenanceCalendarItem;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MaintenanceCalendarController implements Initializable {

    @FXML
    private Label monthYearLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button closeButton;

    @FXML
    private VBox legendBox;

    private YearMonth currentYearMonth;
    private MaintenanceScheduleService maintenanceScheduleService = new MaintenanceScheduleService();
    private List<MaintenanceSchedule> allSchedules;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentYearMonth = YearMonth.now();
        allSchedules = maintenanceScheduleService.findAll();

        updateCalendar();
        createModernLegend();

        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }

    private void updateCalendar() {
        // Обновляем заголовок
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru"));
        monthYearLabel.setText(currentYearMonth.format(formatter));

        // Очищаем grid
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        // Создаем колонки равной ширины
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            col.setHgrow(Priority.ALWAYS);
            calendarGrid.getColumnConstraints().add(col);
        }

        // Добавляем заголовки дней недели
        String[] dayNames = {"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("day-header");

            // Выделяем выходные
            if (i >= 5) {
                dayLabel.getStyleClass().add("day-header-weekend");
            }

            calendarGrid.add(dayLabel, i, 0);
        }

        // Получаем первый день месяца
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        LocalDate today = LocalDate.now();

        // Заполняем календарь
        int row = 1;
        int col = dayOfWeek - 1;

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = currentYearMonth.atDay(day);

            // Создаем ячейку календаря
            VBox dayCell = new VBox(3);
            dayCell.getStyleClass().add("day-cell");

            // Выделяем выходные
            if (currentDate.getDayOfWeek().getValue() >= 6) {
                dayCell.getStyleClass().add("day-cell-weekend");
            }

            // Выделяем сегодняшний день
            if (currentDate.equals(today)) {
                dayCell.getStyleClass().add("day-cell-today");
            }

            dayCell.setAlignment(Pos.TOP_CENTER);
            dayCell.setPadding(new Insets(8, 5, 5, 5));

            // Номер дня
            Label dayNumber = new Label(String.valueOf(day));
            dayNumber.getStyleClass().add("day-number");

            if (currentDate.equals(today)) {
                // Создаем круг для сегодняшнего дня
                StackPane numberContainer = new StackPane();
                Circle circle = new Circle(16);
                circle.setFill(Color.web("#4caf50"));
                circle.setStroke(Color.web("#388e3c"));
                circle.setStrokeWidth(1);

                dayNumber.getStyleClass().add("day-number-today");
                numberContainer.getChildren().addAll(circle, dayNumber);
                dayCell.getChildren().add(numberContainer);
            } else {
                dayCell.getChildren().add(dayNumber);
            }

            // Получаем данные для этой даты
            MaintenanceCalendarItem calendarItem = getCalendarItemForDate(currentDate);

            // Добавляем индикаторы ТО
            int totalCount = calendarItem.getTotalCount();
            if (totalCount > 0) {
                HBox indicators = new HBox(3);
                indicators.getStyleClass().add("day-indicators");

                // Добавляем цветные индикаторы для каждого типа ТО
                if (calendarItem.getOverdueCount() > 0) {
                    addIndicator(indicators, "indicator-overdue", calendarItem.getOverdueCount());
                }
                if (calendarItem.getTodayCount() > 0) {
                    addIndicator(indicators, "indicator-today", calendarItem.getTodayCount());
                }
                if (calendarItem.getTomorrowCount() > 0) {
                    addIndicator(indicators, "indicator-tomorrow", calendarItem.getTomorrowCount());
                }
                if (calendarItem.getDayAfterTomorrowCount() > 0) {
                    addIndicator(indicators, "indicator-day-after-tomorrow", calendarItem.getDayAfterTomorrowCount());
                }
                if (calendarItem.getFutureCount() > 0) {
                    addIndicator(indicators, "indicator-future", calendarItem.getFutureCount());
                }

                dayCell.getChildren().add(indicators);
            }

            // Добавляем обработчик клика для показа деталей ТО
            dayCell.setOnMouseClicked(e -> {
                if (calendarItem.getTotalCount() > 0) {
                    showMaintenanceDetails(currentDate, calendarItem);
                }
            });

            // Добавляем всплывающую подсказку
            Tooltip tooltip = new Tooltip(calendarItem.getTooltipText());
            tooltip.setShowDelay(javafx.util.Duration.millis(300));
            Tooltip.install(dayCell, tooltip);

            // Размещаем в grid
            calendarGrid.add(dayCell, col, row);

            // Переходим к следующему дню
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private MaintenanceCalendarItem getCalendarItemForDate(LocalDate date) {
        MaintenanceCalendarItem item = new MaintenanceCalendarItem(date);

        // Фильтруем ТО для этой даты
        List<MaintenanceSchedule> schedulesForDate = allSchedules.stream()
                .filter(ms -> ms.getNextDue() != null && ms.getNextDue().equals(date))
                .collect(Collectors.toList());

        if (!schedulesForDate.isEmpty()) {
            LocalDate today = LocalDate.now();

            // Разделяем по категориям
            for (MaintenanceSchedule schedule : schedulesForDate) {
                LocalDate nextDue = schedule.getNextDue();

                if (nextDue.isBefore(today)) {
                    item.setOverdueCount(item.getOverdueCount() + 1);
                } else if (nextDue.equals(today)) {
                    item.setTodayCount(item.getTodayCount() + 1);
                } else if (nextDue.equals(today.plusDays(1))) {
                    item.setTomorrowCount(item.getTomorrowCount() + 1);
                } else if (nextDue.equals(today.plusDays(2))) {
                    item.setDayAfterTomorrowCount(item.getDayAfterTomorrowCount() + 1);
                } else {
                    item.setFutureCount(item.getFutureCount() + 1);
                }
            }
        }

        return item;
    }

    private void addIndicator(HBox container, String styleClass, int count) {
        StackPane indicator = new StackPane();
        indicator.setMinSize(20, 20);
        indicator.setMaxSize(20, 20);

        Circle circle = new Circle(8);
        circle.getStyleClass().add(styleClass);

        if (count > 1) {
            Label countLabel = new Label(String.valueOf(count));
            countLabel.setFont(Font.font(9));
            countLabel.setTextFill(Color.WHITE);
            indicator.getChildren().addAll(circle, countLabel);
        } else {
            indicator.getChildren().add(circle);
        }

        container.getChildren().add(indicator);
    }

    private void showMaintenanceDetails(LocalDate date, MaintenanceCalendarItem calendarItem) {
        // Создаем модальное окно
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.initStyle(StageStyle.UTILITY);
        detailsStage.setTitle("Детали ТО");

        // Получаем все ТО для этой даты
        List<MaintenanceSchedule> schedulesForDate = allSchedules.stream()
                .filter(ms -> ms.getNextDue() != null && ms.getNextDue().equals(date))
                .collect(Collectors.toList());

        VBox dialogContent = new VBox(15);
        dialogContent.getStyleClass().add("details-dialog");
        dialogContent.setPadding(new Insets(20));
        dialogContent.setMinWidth(500);
        dialogContent.setMinHeight(400);

        // Заголовок
        Label titleLabel = new Label("📋 Детали технического обслуживания");
        titleLabel.getStyleClass().add("details-title");

        // Дата
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));
        Label dateLabel = new Label("📅 " + date.format(dateFormatter));
        dateLabel.getStyleClass().add("details-date");

        // Статистика
        Label statsLabel = new Label(String.format(
                "Всего ТО: %d (Просрочено: %d, Сегодня: %d, Завтра: %d)",
                calendarItem.getTotalCount(),
                calendarItem.getOverdueCount(),
                calendarItem.getTodayCount(),
                calendarItem.getTomorrowCount()
        ));
        statsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Список ТО
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox maintenanceList = new VBox(8);
        maintenanceList.getStyleClass().add("details-list");
        maintenanceList.setPadding(new Insets(10));

        for (MaintenanceSchedule schedule : schedulesForDate) {
            VBox itemBox = new VBox(5);
            itemBox.getStyleClass().add("details-item");
            itemBox.setPadding(new Insets(10));

            // Название станка
            Label machineLabel = new Label("🏭 Станок: " +
                    (schedule.getMachines() != null ? schedule.getMachines().getModel() : "Не указан"));
            machineLabel.getStyleClass().add("details-item-title");

            // Тип ТО
            Label typeLabel = new Label("🔧 Тип ТО: " +
                    (schedule.getType() != null ? schedule.getTypeNames() : "Не указан"));
            typeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

            // Статус
            Label statusLabel = new Label();
            LocalDate today = LocalDate.now();

            if (schedule.getNextDue().isBefore(today)) {
                statusLabel.setText("❌ ПРОСРОЧЕНО");
                statusLabel.getStyleClass().addAll("details-item-status", "details-item-overdue");
            } else if (schedule.getNextDue().equals(today)) {
                statusLabel.setText("⚠️ НА СЕГОДНЯ");
                statusLabel.getStyleClass().addAll("details-item-status", "details-item-today");
            } else {
                statusLabel.setText("✅ Запланировано");
                statusLabel.getStyleClass().addAll("details-item-status", "details-item-future");
            }

            // Последнее выполнение
            Label lastDoneLabel = new Label("📅 Последнее выполнение: " +
                    (schedule.getLastDone() != null ? schedule.getLastDone().toString() : "Не выполнялось"));
            lastDoneLabel.setStyle("-fx-font-size: 12px;");

            // Следующее выполнение
            Label nextDueLabel = new Label("⏰ Следующее выполнение: " +
                    (schedule.getNextDue() != null ? schedule.getNextDue().toString() : "Не указано"));
            nextDueLabel.setStyle("-fx-font-size: 12px;");

            itemBox.getChildren().addAll(machineLabel, typeLabel, statusLabel, lastDoneLabel, nextDueLabel);
            maintenanceList.getChildren().add(itemBox);
        }

        scrollPane.setContent(maintenanceList);

        // Кнопка закрытия
        Button closeDetailsButton = new Button("Закрыть");
        closeDetailsButton.getStyleClass().add("details-close-button");
        closeDetailsButton.setOnAction(e -> detailsStage.close());

        HBox buttonBox = new HBox(closeDetailsButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        dialogContent.getChildren().addAll(titleLabel, dateLabel, statsLabel, scrollPane, buttonBox);

        // Сцена
        javafx.scene.Scene scene = new javafx.scene.Scene(dialogContent);
        try {
            // Попробуем загрузить CSS из той же папки, что и FXML
            URL cssUrl = getClass().getResource("calendar.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Если не найден, попробуем из корня
                cssUrl = getClass().getResource("/calendar.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить CSS файл: " + e.getMessage());
        }

        detailsStage.setScene(scene);
        detailsStage.sizeToScene();
        detailsStage.show();
    }

    private void createModernLegend() {
        legendBox.getChildren().clear();

        // Заголовок легенды
        Label legendTitle = new Label("📊 Легенда");
        legendTitle.getStyleClass().add("legend-title");
        legendBox.getChildren().add(legendTitle);

        // Элементы легенды
        String[][] legendItems = {
                {"#ff5252", "Просроченные ТО", "Требуют немедленного внимания"},
                {"#ffeb3b", "ТО на сегодня", "Запланировано на сегодня"},
                {"#29b6f6", "ТО на завтра", "Будет завтра"},
                {"#66bb6a", "ТО на послезавтра", "Через 2 дня"},
                {"#9fa8da", "Будущие ТО", "Более чем через 2 дня"},
                {"#bdbdbd", "Нет ТО", "Нет запланированных работ"}
        };

        for (String[] item : legendItems) {
            HBox legendItem = new HBox(12);
            legendItem.getStyleClass().add("legend-item");
            legendItem.setAlignment(Pos.CENTER_LEFT);

            // Цветной индикатор
            Pane colorBox = new Pane();
            colorBox.getStyleClass().add("legend-color");
            colorBox.setStyle("-fx-background-color: " + item[0] + ";");

            // Текстовая часть
            VBox textBox = new VBox(2);

            Label titleLabel = new Label(item[1]);
            titleLabel.getStyleClass().add("legend-text");

            Label descLabel = new Label(item[2]);
            descLabel.getStyleClass().add("legend-description");

            textBox.getChildren().addAll(titleLabel, descLabel);

            legendItem.getChildren().addAll(colorBox, textBox);
            legendBox.getChildren().add(legendItem);
        }

        // Добавляем разделитель
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        legendBox.getChildren().add(separator);

        // Инструкция
        Label instruction = new Label("💡 Нажмите на день с ТО, чтобы увидеть подробности");
        instruction.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true;");
        legendBox.getChildren().add(instruction);
    }
}
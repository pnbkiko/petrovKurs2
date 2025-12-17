package ru.kurs.petrovkurs.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private StackPane rootPane;

    @FXML
    private VBox mainContent;

    private double progress = 0;
    private Runnable onLoadingComplete;
    private Map<String, Parent> loadedPanes = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Анимация появления
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Запускаем процесс загрузки
        startLoading();
    }

    public void setOnLoadingComplete(Runnable onLoadingComplete) {
        this.onLoadingComplete = onLoadingComplete;
    }

    public Map<String, Parent> getLoadedPanes() {
        return loadedPanes;
    }

    private void startLoading() {
        Timeline timeline = new Timeline();

        // Этап 1: Инициализация системы (0-20%)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), e -> {
            progress = 0.2;
            progressBar.setProgress(progress);
            progressLabel.setText("20%");
            statusLabel.setText("Инициализация системы...");
        }));

        // Этап 2: Подключение к базе данных (20-40%)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e -> {
            progress = 0.4;
            progressBar.setProgress(progress);
            progressLabel.setText("40%");
            statusLabel.setText("Загрузка станков...");

            // Загружаем страницу станков в фоне
            loadPaneInBackground("/ru/kurs/petrovkurs/machines-table-view.fxml", "machines");
        }));

        // Этап 3: Загрузка конфигурации (40-60%)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1500), e -> {
            progress = 0.6;
            progressBar.setProgress(progress);
            progressLabel.setText("60%");
            statusLabel.setText("Загрузка актов ТО...");

            // Загружаем страницу актов ТО
            loadPaneInBackground("/ru/kurs/petrovkurs/maintenance-acts-table-view.fxml", "acts");
        }));

        // Этап 4: Подготовка интерфейса (60-80%)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), e -> {
            progress = 0.8;
            progressBar.setProgress(progress);
            progressLabel.setText("80%");
            statusLabel.setText("Загрузка графика ТО...");

            // Загружаем страницу графика ТО
            loadPaneInBackground("/ru/kurs/petrovkurs/maintenance-schedule-table-view.fxml", "schedule");

            // Загружаем страницу видов ТО
            loadPaneInBackground("/ru/kurs/petrovkurs/maintenance-types-table-view.fxml", "types");
        }));

        // Этап 5: Завершение (80-100%)
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2500), e -> {
            progress = 1.0;
            progressBar.setProgress(progress);
            progressLabel.setText("100%");
            statusLabel.setText("Завершение загрузки...");

            // Показываем анимацию завершения
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), rootPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                // Вызываем callback когда загрузка завершена
                if (onLoadingComplete != null) {
                    onLoadingComplete.run();
                }
            });
            fadeOut.play();
        }));

        timeline.play();
    }

    private void loadPaneInBackground(String fxmlPath, String paneName) {
        new Thread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent pane = loader.load();

                // Сохраняем загруженную панель
                loadedPanes.put(paneName, pane);

                // Обновляем прогресс в UI потоке
                Platform.runLater(() -> {
                    statusLabel.setText("Загружено: " + paneName);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Ошибка загрузки: " + paneName);
                });
            }
        }).start();
    }
}
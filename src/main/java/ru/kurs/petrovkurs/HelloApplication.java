package ru.kurs.petrovkurs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.kurs.petrovkurs.controller.HelloController;
import ru.kurs.petrovkurs.controller.SplashScreenController;

import java.util.Map;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Создаем и показываем окно загрузки
            Stage splashStage = new Stage();
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/ru/kurs/petrovkurs/splash-screen.fxml"));
            Parent splashRoot = splashLoader.load();
            SplashScreenController splashController = splashLoader.getController();

            // Устанавливаем callback для завершения загрузки
            splashController.setOnLoadingComplete(() -> {
                Platform.runLater(() -> {
                    try {
                        // Закрываем окно загрузки
                        splashStage.close();

                        // Получаем загруженные панели
                        Map<String, Parent> loadedPanes = splashController.getLoadedPanes();

                        // Открываем главное окно
                        loadMainWindow(primaryStage, loadedPanes);

                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorDialog("Ошибка", "Не удалось загрузить приложение");
                    }
                });
            });

            Scene splashScene = new Scene(splashRoot);

            // Настройка окна загрузки
            splashStage.setScene(splashScene);
            splashStage.initStyle(StageStyle.UNDECORATED);
            splashStage.setResizable(false);
            splashStage.centerOnScreen();

            // Установка иконки для окна загрузки
            try {
                Image icon = new Image(getClass().getResource("/images/icon_main.png").toExternalForm());
                splashStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Иконка для окна загрузки не найдена");
            }

            splashStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Критическая ошибка", "Не удалось запустить приложение");
        }
    }

    private void loadMainWindow(Stage primaryStage, Map<String, Parent> loadedPanes) {
        try {
            // Загружаем главное окно
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kurs/petrovkurs/hello-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Получаем контроллер главного окна
            HelloController helloController = loader.getController();

            // Передаем загруженные панели в контроллер
            if (helloController != null) {
                // Здесь нужно добавить метод в HelloController для приема панелей
                // Например: helloController.setPreloadedPanes(loadedPanes);

                // Или сразу загружаем панели в contentStack
                if (loadedPanes.containsKey("machines")) {
                    Platform.runLater(() -> {
                        // Загружаем первую панель (станки)
                        helloController.loadPane("/ru/kurs/petrovkurs/machines-table-view.fxml");
                    });
                }
            }

            // Настройка главного окна
            primaryStage.setTitle("ТО Машин - Система управления техническим обслуживанием");
            primaryStage.setScene(scene);

            // Установка иконки
            try {
                Image icon = new Image(getClass().getResource("/images/icon_main.png").toExternalForm());
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Иконка для главного окна не найдена");
            }

            // Настройка размеров окна
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);

            // Центрирование окна
            primaryStage.centerOnScreen();

            // Показываем главное окно
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Ошибка загрузки главного окна", e.getMessage());
        }
    }

    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Ошибка запуска приложения");
            alert.setContentText(message);
            alert.showAndWait();
            System.exit(1);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
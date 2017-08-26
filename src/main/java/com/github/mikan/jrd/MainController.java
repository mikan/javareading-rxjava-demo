package com.github.mikan.jrd;

import com.github.mikan.jrd.model.DroneTracker;
import io.reactivex.Flowable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    private static final double DRONE_SIZE_PX = 20L;
    private static final long REFRESH_MS = 200L;
    private static final int INITIAL_POSITION_SCALE = 8;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private Button mainButton;

    @FXML
    private Label velocityLabel;

    @FXML
    private ListView<String> logListView;

    private ObservableList<String> logItems = FXCollections.observableArrayList();

    private Image mapImage;
    private ObjectProperty<Point2D> positionProperty = new SimpleObjectProperty<>();
    private DroneTracker droneTracker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logListView.setItems(logItems);
        mapImage = new Image(getClass().getClassLoader().getResourceAsStream("map.png"),
                mapCanvas.getWidth(), mapCanvas.getHeight(), false, false);
        positionProperty.addListener((obs, ov, nv) -> drawMap());
        Point2D point = new Point2D(mapCanvas.getWidth() / INITIAL_POSITION_SCALE, mapCanvas.getHeight() / INITIAL_POSITION_SCALE);
        positionProperty.set(point);
        droneTracker = new DroneTracker(point, REFRESH_MS, TimeUnit.MILLISECONDS);
        log("初期化完了");
    }

    @FXML
    private void handleButtonAction(MouseEvent event) {
        Flowable<Point2D> publisher = droneTracker.getPublisher().publish().autoConnect(2);
        publisher.subscribe(positionProperty::set, System.err::println);
        publisher.scan(new Pair<>(Point2D.ZERO, Point2D.ZERO), (pair, point) -> new Pair<>(pair.getValue(), point))
            .map(this::calcVelocity)
            .subscribe(d -> Platform.runLater(() -> velocityLabel.setText(String.format("%1.1f", d))));
        mainButton.setDisable(true);
        log("subscribe しました");
    }

    private double calcVelocity(Pair<Point2D, Point2D> pair) {
        return pair.getKey().distance(pair.getValue()) / 1.0;
    }

    private void drawMap() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.drawImage(mapImage, 0, 0);
        gc.setFill(Color.RED);
        Point2D point = positionProperty.get();
        gc.fillOval(point.getX(), point.getY(), DRONE_SIZE_PX, DRONE_SIZE_PX);
    }

    private void log(String message) {
        logItems.add(0, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " " + message);
    }
}

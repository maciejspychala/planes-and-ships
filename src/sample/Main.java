package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.WorldHelper;


public class Main extends Application {


    private Timeline timeline = new Timeline();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(createContent());
        primaryStage.setTitle("TopGun");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Parent createContent() {
        Pane pane = new Pane();
        pane.setPrefSize(WorldHelper.WIDTH, WorldHelper.HEIGHT);
        WorldHelper.generateWorld(pane);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.016), event -> WorldHelper.live(pane));
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return pane;
    }
}
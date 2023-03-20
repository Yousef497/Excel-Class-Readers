package Advanced;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
        Scene scene = new Scene(root, 600, 160);
        stage.setTitle("Advanced Project");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.show();


    }

    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

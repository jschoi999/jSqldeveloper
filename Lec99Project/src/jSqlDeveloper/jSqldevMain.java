package jSqlDeveloper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class jSqldevMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		FXMLLoader loader = new FXMLLoader(getClass().getResource("jSqldevMainRoot.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root);
		
	
		jSqldevMainRootController controller = loader.getController();
		controller.setPrimaryStage(primaryStage);
		
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		
		primaryStage.setScene(scene);
		
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.setTitle("jSqlDeveloper");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}

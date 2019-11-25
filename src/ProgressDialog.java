import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressDialog extends Stage {
	
	private Task task;
	
	private AnchorPane content;
	private Scene scene_Main;
	
	@FXML private ProgressBar progressBar;
	@FXML private Button bttn_Cancel;
	
	public ProgressDialog(String title, Task task){
		super();
		this.setTitle(title);
		this.task = task;
		try {
			content = new AnchorPane();
			
			FXMLLoader loader = new FXMLLoader(ProgressDialog.class.getResource("/fxml/ProgressDialog.fxml"));
			loader.setRoot(content);
			loader.setController(this);
			loader.load();
			
			scene_Main = new Scene(content);
			this.setScene(scene_Main);
			
			task.setOnSucceeded(l -> {this.close();});
			task.setOnCancelled(l -> this.close());
			task.setOnFailed(l -> this.close());
			progressBar.progressProperty().bind(task.progressProperty());
			
			initModality(Modality.APPLICATION_MODAL);
		} catch (IOException e) {
			System.err.println("Error loading ProgressDialog");
		}
		
	}
	
	@Override
	public void showAndWait() {
		super.show();
	}
	
}

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProgressDialog extends Stage {
	
	private CopyTask task;
	
	//Stuff defined in "ProgressDialog.fxml"
	@FXML private ProgressBar progress_File;
	@FXML private ProgressBar progress_Total;
	
	@FXML private Label label_Status;
	@FXML private Label label_fileProgress;
	@FXML private Label label_totalProgress;
	
	@FXML private Button bttn_Cancel;
	
	public ProgressDialog(String title, List<File> files, Window owner){
		super();
		
		this.task = new CopyTask(files);
		try {
			AnchorPane content = new AnchorPane();
			
			FXMLLoader loader = new FXMLLoader(ProgressDialog.class.getResource("/fxml/ProgressDialog.fxml"));
			loader.setRoot(content);
			loader.setController(this);
			loader.load();
			
			Scene scene_Main = new Scene(content);
			setScene(scene_Main);
			sizeToScene();
			
			//Initialize owner window and modality
			initOwner(owner);
			initModality(Modality.APPLICATION_MODAL);
			
			label_Status.setTextFill(Color.BLUE);
			label_Status.setText(" ");
			progress_File.setProgress(0);
			
			//Add handler for when the dialog is shown
			addEventHandler(WindowEvent.WINDOW_SHOWN, l -> {
				//Bind Properties
				progress_File.progressProperty().unbind();
				progress_File.progressProperty().bind(task.progressProperty());
				
				progress_Total.progressProperty().unbind();
				progress_Total.progressProperty().bind(task.totalProgressProperty());
				
				label_Status.textProperty().unbind();
				label_Status.textProperty().bind(task.messageProperty());
				
				label_fileProgress.textProperty().unbind();
				label_fileProgress.textProperty().bind(task.progressProperty().multiply(100).asString("%.2f%%"));
				
				label_totalProgress.textProperty().unbind();
				label_totalProgress.textProperty().bind(
						//This should format the value as "{copied}/{requested}"
						task.filesCopiedProperty().asString("%.0f")
							.concat("/")
							.concat(task.filesRequestedProperty().asString("%.0f"))
				);
				
				//Start copy task
				Thread t = new Thread(this.task);
				t.setDaemon(true);
				t.start();
			});
			
			//Add an event handler for when the task completes
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, l->{
				List<File> copied = task.getValue();
				//Update text label
				label_Status.textProperty().unbind();
				label_Status.setText("Copied: " + copied.size());
				label_Status.setTextFill(Color.DARKGREEN);
				
				//Disable the cancel button
				bttn_Cancel.setDisable(true);
			});
			
			//Add handler for cancel button
			bttn_Cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, l -> {
				//Disable the cancel button
				bttn_Cancel.setDisable(true);
				//Cancel the task
				task.cancel(true);
				//Unbind properties
				progress_File.progressProperty().unbind();
				label_fileProgress.textProperty().unbind();
				label_totalProgress.textProperty().unbind();
				label_Status.textProperty().unbind();
				//Update Status label
				label_Status.setText("Copy Cancelled");
				label_Status.setTextFill(Color.RED);
			});
			
			setTitle(title);
			setResizable(false);
			
		} catch (IOException e) {
			System.err.println("Error loading ProgressDialog");
		}
		
	}
	
	private void initLabelFormat(){
		
		StringConverter<Double> percentConverter = new StringConverter<Double>() {
			@Override
			public String toString(Double object) {
				return String.format("%.2f%%", object);
			}
			
			@Override
			public Double fromString(String string) {
				return null;
			}
		};
		
		
		
	}
	
}

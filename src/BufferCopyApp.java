import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferCopyApp extends Application {
	
	private BrowseHandler browseHandler;
	private Scene scene;
	
	
	@FXML private Button bttn_Browse, bttn_Confirm, bttn_Cancel;
	@FXML private TextField field_File;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/FileCopyGUI.fxml"));
		loader.setController(this);
		
		Parent parent = loader.load();
		scene = new Scene(parent);
		
		browseHandler = new BrowseHandler(primaryStage, field_File, "Select files to copy...");
		
		ScaleTransition anim_Grow = new ScaleTransition(Duration.seconds(0.125), bttn_Browse);
		anim_Grow.setFromX(1);
		anim_Grow.setFromY(1);
		anim_Grow.setToX(1.1);
		anim_Grow.setToY(1.1);
		
		ScaleTransition anim_Shrink = new ScaleTransition(Duration.seconds(0.125), bttn_Browse);
		anim_Shrink.setFromX(1.1);
		anim_Shrink.setFromY(1.1);
		anim_Shrink.setToX(1);
		anim_Shrink.setToY(1);
		
		bttn_Browse.setOnMouseEntered(l -> anim_Grow.playFromStart());
		bttn_Browse.setOnMouseExited(l -> anim_Shrink.playFromStart());
		
		bttn_Browse.setOnAction(browseHandler);
		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setMinHeight(137);
		primaryStage.setMaxHeight(137);
		primaryStage.setMinWidth(550 + 16);
		primaryStage.setTitle("File Copy");
		primaryStage.show();
	}
	
	/**
	 * Called when the user clicks the {@code bttn_Confirm} {@link Button button}.
	 * <br>
	 * This method checks whether any files have been selected to copy and, if so,
	 * creates a new {@link ProgressDialog} and shows it, starting the copy process.
	 */
	public final void onConfirm(ActionEvent e) {
		if (browseHandler.hasSelection()) {
			ProgressDialog dialog = new ProgressDialog("Copying Files...", browseHandler.getSelection(), scene.getWindow());
			dialog.show();
		}
	}
}

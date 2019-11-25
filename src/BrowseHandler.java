import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class BrowseHandler implements EventHandler<ActionEvent> {
	
	private static FileChooser fileChooser = new FileChooser();
	
	private Window ownerWindow;
	private String dialogTitle;
	private TextField output;
	
	private List<File> result;
	
	public BrowseHandler(Window owner, TextField output, String title){
		ownerWindow = owner;
		dialogTitle = title;
		result = null;
		this.output = output;
	}
	
	public BrowseHandler(Window owner, String title){
		this(owner, null, title);
	}
	
	@Override
	public void handle(ActionEvent event) {
		result = fileChooser.showOpenMultipleDialog(ownerWindow);
		if(output != null && hasSelection()){
			if(result.size() > 1){
				StringBuffer buff = new StringBuffer();
				for (File file : result) {
					buff.append(file.getPath());
					buff.append("; ");
				}
				buff.deleteCharAt(buff.length() - 1);
				output.setText(buff.toString());
			}else{
				output.setText(result.get(0).getPath());
			}
		}
	}
	
	public boolean hasSelection(){
		return result != null;
	}
	
	public List<File> getSelection(){
		return result;
	}
	
}

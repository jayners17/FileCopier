import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CopyTask extends Task<List<File>> {
	
	private AtomicReference<Update> totalProgressUpdate = new AtomicReference<>(null);
	
	private final List<File> src;
	
	private long FILES_REQUESTED;
	
	private RandomAccessFile original;
	private RandomAccessFile copy;
	private RandomFileBuffer2 buff;
	
	private DoubleProperty totalProgress = new SimpleDoubleProperty(this, "totalProgress");
	
	private DoubleProperty filesCopied;
	private DoubleProperty filesRequested;
	
	public CopyTask(List<File> src){
		this.src = src;
		filesCopied = new SimpleDoubleProperty(this, "filesCopied", -1);
		filesRequested = new SimpleDoubleProperty(this, "filesRequested", -1);
		
		FILES_REQUESTED = src.size();
	}
	
	@Override
	protected List<File> call() throws Exception {
		//Create list to store result
		List<File> copied = new LinkedList<>();
		//Update/Init Progress
		updateTotalProgress(0, src.size());
		for (File f : src){
			//Update message for each file
			updateProgress(0, 1);
			updateMessage("Copying: " + f.getPath());
			
			try {
				//Setup files and buffer
				original = new RandomAccessFile(f, "r");
				copy = new RandomAccessFile("COPY_" + f.getName(), "rw");
				buff = new RandomFileBuffer2(copy, 6400, "Copy");
				
			} catch (FileNotFoundException e) {
				if(src.size() > 1){
					continue;
				}else {
					updateMessage("Copy Failed: " + f.getPath());
					throw e;
				}
			}
			
			long startTime = System.currentTimeMillis();
			
			for (long i = 0; i < original.length(); i++) {
				try{
					buff.append(original.readByte());
				}catch (EOFException e){
					//Flush buffer & break the loop
					buff.flush();
					break;
				}
				//Update Progress
				updateProgress(i, original.length());
				
				if(isCancelled()){
					buff.flush();
					break;
				}
			}
			buff.flush();
			
			long endTime = System.currentTimeMillis();
			System.out.printf("[%s] Elapsed Time: %f second(s)%n", f.getName(), (endTime - startTime) / 1000.0);
			
			//Close files
			original.close();
			copy.close();
			
			//Sleep for a bit to free up processor
			try{
				Thread.sleep(200);
			}catch (InterruptedException e){
				if(isCancelled()){
					break;
				}
			}
			
			copied.add(f);
			//Updates the total progress (# of files copied/# of files requested)
			updateTotalProgress(copied.size(), FILES_REQUESTED);
			
			if(isCancelled()){
				break;
			}
		}
		
		return copied;
	}
	
	/**
	 * Updates the progress of the copy operation by updating the values of the
	 * {@code filesCopied} and {@code filesRequested} properties. This method
	 * can be called from any thread.
	 *
	 * @param filesCopied the number of files successfully copied
	 * @param filesRequested this number of files successfully copied
	 * @see CopyTask#updateTotalProgress(double, double)
	 */
	private void updateTotalProgress(long filesCopied, long filesRequested){
		updateTotalProgress((double)filesCopied, (double)filesRequested);
	}
	
	/**
	 * Updates the progress of the copy operation by updating the values of the
	 * {@code filesCopied} and {@code filesRequested} properties. This method
	 * can be called from any thread.
	 * <p>
	 * This method mimics the function of {@link Task#updateProgress(double, double)}
	 * but updates the {@code totalProgress} property of this {@code CopyTask} instead.
	 *
	 * @param filesCopied the number of files successfully copied
	 * @param filesRequested the total numbers of files to be copied
	 * @see CopyTask#updateTotalProgress(long, long)
	 */
	private void updateTotalProgress(double filesCopied, double filesRequested){
		//Check that the arguments are actual, usable values
		if(Double.isInfinite(filesCopied) || Double.isNaN(filesCopied)){
			filesCopied = -1;
		}
		if(Double.isInfinite(filesRequested) || Double.isNaN(filesRequested)){
			filesRequested = -1;
		}
		
		if(filesRequested < 0){
			filesRequested = -1;
		}
		//Caps the number of files copied to the number requested
		if(filesCopied > filesRequested){
			filesCopied = filesRequested;
		}
		
		//Check if this is being run on the FXApplication thread
		if(Platform.isFxApplicationThread()){
			//We can only modify the properties on the FXApplication thread because
			//they are bound to the GUI. We'll get errors otherwise.
			updateCopyProgress(filesCopied, filesRequested);
		}else if(totalProgressUpdate.getAndSet(new Update(filesCopied, filesRequested)) == null){
			//Run on the FXApplication thread
			Platform.runLater(() ->{
				final Update update = totalProgressUpdate.getAndSet(null);
				updateCopyProgress(update.filesCopied, update.filesRequested);
			});
		}
	}
	
	private void updateCopyProgress(double filesCopied, double filesRequested){
		setFilesCopied(filesCopied);
		setFilesRequested(filesRequested);
		
		if(filesCopied == -1){
			setTotalProgress(-1);
		}else{
			setTotalProgress(filesCopied/filesRequested);
		}
	}
	
	private void setFilesCopied(double filesCopied){
		this.filesCopied.setValue(filesCopied);
	}
	
	private void setFilesRequested(double filesRequested){
		this.filesRequested.setValue(filesRequested);
	}
	
	private void setTotalProgress(double totalProgress){
		this.totalProgress.setValue(totalProgress);
	}
	
	public final ReadOnlyDoubleProperty totalProgressProperty(){
		return totalProgress;
	}
	
	public final ReadOnlyDoubleProperty filesCopiedProperty(){
		return filesCopied;
	}
	
	public final ReadOnlyDoubleProperty filesRequestedProperty(){
		return filesRequested;
	}
	
	private static class Update{
		public double filesCopied, filesRequested;
		
		public Update(double filesCopied, double filesRequested){
			this.filesCopied = filesCopied;
			this.filesRequested = filesRequested;
		}
	}
	
}

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.List;

public class CopyTask extends Task<Void> {
	
	private final List<File> src;
	private ProgressDialog dialog;
	
	private long totalWork;
	private double step;
	
	public CopyTask(List<File> src){
		this.src = src;
		//Calculate total number of bytes to copy
		for(File f : src){
			totalWork += f.length();
		}
		updateProgress(0.0, 1.0);
		
		step = 1.0 / totalWork;
		
		this.dialog = new ProgressDialog("Copying Files...", this);
		this.dialog.show();
	}
	
	@Override
	protected Void call() throws Exception {
		System.out.println("Started Copy");
		double progress = 0;
		for (File f : src){
			//Check if task is canceled
			if(isCancelled()){
				updateMessage("Cancelled");
				return null;
			}
			updateMessage(String.format("File: %s", f.getName()));
			try {
				//Setup files and buffer
				RandomAccessFile original = new RandomAccessFile(f, "r");
				RandomAccessFile copy = new RandomAccessFile("COPY_" + f.getName(), "rw");
				RandomFileBuffer2 buff = new RandomFileBuffer2(copy, 800, "Copy");
				
				boolean isEOF = false;
				
				//Loop through each file
				while(!isEOF){
					byte[] readBuff = new byte[10];
					if(isCancelled()){
						//Return immediately if canceled
						updateMessage("Cancelled");
						original.close();
						copy.close();
						return null;
					}
					try{
						//Copy byte
						original.read(readBuff);
						for (int i = 0; i < readBuff.length; i++) {
							buff.append(readBuff[1]);
							progress += step;
						}
						Thread.sleep(1000);
						
					}catch (EOFException e){
						isEOF = true;
					}
				}
				
				//Flush buffer
				buff.flush();
				//Close files
				original.close();
				copy.close();
				//Update task progress
				updateProgress(progress, 1.0);
				
				System.out.printf("Copy Progress: %.2f%%", getProgress() * 100);
				
				try{
					Thread.sleep(100);
				}catch (InterruptedException e){
					if(isCancelled()){
						updateMessage("Cancelled");
					}
				}
				
			} catch (FileNotFoundException e) {
				this.failed();
			} catch (IOException e){
				this.failed();
			}
		}
		
		this.succeeded();
		
		return null;
	}
	
	@Override
	protected void succeeded() {
		super.succeeded();
		System.out.println("Copied Files!!");
		updateMessage("Copying done!!");
		dialog.close();
	}
	
	@Override
	protected void cancelled() {
		super.cancelled();
		updateMessage("Canceled");
		dialog.close();
	}
	
	@Override
	protected void failed() {
		super.failed();
		updateMessage("Copy Operation Failed");
	}
	
}

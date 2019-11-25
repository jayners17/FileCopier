import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.List;

public class CopyTask extends Task<Void> {
	
	private final List<File> src;
	
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
	}
	
	@Override
	protected Void call() throws Exception {
		System.out.println("Started Copy");
		double progress = 0;
		System.out.println(src);
		for (File f : src){
			try {
				System.err.println();
				
				//Setup files and buffer
				RandomAccessFile original = new RandomAccessFile(f, "r");
				RandomAccessFile copy = new RandomAccessFile("COPY_" + f.getName(), "rw");
				RandomFileBuffer2 buff = new RandomFileBuffer2(copy, 6400, "Copy");
				
				long startTime = System.currentTimeMillis();
				
				//Loop through each file
				while(true){
					
					try{
						//Copy byte to buffer
						buff.append(original.readByte());
						System.out.println("Wrote Byte");
						System.out.printf("Copy Progress: %.2f%%", getProgress() * 100);
						//Free processor up for a bit
						Thread.sleep(100);
					}catch (EOFException e){
						//Flush buffer
						buff.flush();
						//Stop loop once eof is reached
						break;
					}
				}
				
				System.out.printf("[%s] Elapsed Time: %.2f second(s)", f.getName(), (startTime - System.currentTimeMillis()) / 10e8);
				
				//Close files
				original.close();
				copy.close();
				//Update task progress
				updateProgress(progress, 1.0);
				
				
				
				try{
					Thread.sleep(100);
				}catch (InterruptedException e){
					if(isCancelled()){
						updateMessage("Cancelled");
					}
				}
				
			} catch (FileNotFoundException e) {
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
	}
	
	@Override
	protected void cancelled() {
		super.cancelled();
		updateMessage("Canceled");
	}
	
	@Override
	protected void failed() {
		super.failed();
		updateMessage("Copy Operation Failed");
	}
	
}

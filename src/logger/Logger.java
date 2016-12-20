package logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class for writing a log.
 * 
 * @author Brian
 */
public class Logger {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
	
	public static final String INFO = "INFO";
	public static final String WARNING = "WARNING";
	public static final String ERROR = "ERROR";
	
	private File logFile;
	private PrintWriter logWriter;
	
	/**
	 * @param logFile The path to the log file.
	 * @throws IOException If the log file cannot be created.
	 */
	public Logger(File logFile) throws IOException {
		this.logFile = logFile;
		this.logFile.createNewFile();
		logWriter = new PrintWriter(this.logFile);
	}
	
	public void writeEntry(String status, String entry) {
		String dateTime = formatter.format(ZonedDateTime.now());
		logWriter.write(dateTime + " : " + status + " : " + entry + '\n');
	}
	
}

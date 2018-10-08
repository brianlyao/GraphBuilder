package logger;

import java.io.*;
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

	private static File logFile;
	private static PrintWriter logWriter;

	/**
	 * Set this logger to use
	 *
	 * @param logFile The path to the log file.
	 * @throws IOException If the log file cannot be created.
	 */
	public static void setLogFile(String logFile) throws IOException {
		Logger.logFile = new File(logFile);
		Logger.logFile.createNewFile();
		FileWriter fwriter = new FileWriter(logFile, true);
		BufferedWriter bwriter = new BufferedWriter(fwriter);
		logWriter = new PrintWriter(bwriter);
	}

	/**
	 * Write a single entry to the log file.
	 *
	 * @param status The status level of the entry.
	 * @param entry  The message which describes the entry.
	 */
	public static void writeEntry(String status, String entry) {
		String dateTime = formatter.format(ZonedDateTime.now());
		logWriter.write(dateTime + " : " + status + " : " + entry + '\n');
	}

	/**
	 * Flush the current log to disk.
	 */
	public static void flush() {
		logWriter.flush();
	}

}

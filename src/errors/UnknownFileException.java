package errors;

import java.io.File;

import tools.Log;

@SuppressWarnings("serial")
public class UnknownFileException extends Exception {

	public UnknownFileException(Log log, File dir) {
		
		log.addLine("There is an error with reading files from " 
				+ dir.getAbsolutePath());
		log.addLine("\t*check that you have the correct flags in your file names");
		//TODO: Include correct wiki page reference
		log.addLine("\t*and go to wiki (https://github.com/jbelyeu/mySelecT/wiki) for parameter descriptions");
	}
	
	public UnknownFileException(Log log, File dir, String msg) {
		log.addLine("There is an error with reading files from " 
				+ dir.getAbsolutePath());
		log.addLine("\t*" + msg);
		log.addLine("\t*check that you have the correct flags in your file names");
		//TODO: Include correct wiki page reference 
		log.addLine("\t*and go to wiki (https://github.com/jbelyeu/mySelecT/wiki) for parameter descriptions");
	}
}

package errors;

import tools.Log;

@SuppressWarnings("serial")
public class IllegalInputException extends Exception {
	
	public IllegalInputException() {}
	
	public IllegalInputException(Log log, String message) {
		log.addLine("\n");
		log.addLine(message);
		log.addLine("\t*Make sure all arguments are correct" );
		//TODO: Include correct wiki page reference
		log.addLine("\t*Go to wiki (https://github.com/jbelyeu/mySelecT/wiki) for parameter descriptions");
	}

}

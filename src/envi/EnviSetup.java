package envi;

import java.util.HashMap;

import envi.SetupDriver;
import errors.IllegalInputException;
import tools.Log;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.internal.HelpScreenException;

public class EnviSetup {

	/** Hyper-Parallelized Composite of Multiple Signals (CMS) Java implementation GWAS and Local study version 1.0
	 * This program sets up the environment needed for stats calculation
	 * @author Hayden Smith
	 * 
	 * @param Required	Phased and Ancestral Data Files Directory Path (.hap/.legend -OR- .vcf)
	 * @param Required	Genetic Map directory
	 * @param Required	Start chromosome number
	 * @param Required	End chromosome number 
	 * @param Required	Species name
	 * @param Required	Target Population
	 * @param Required	Cross Population
	 * @param Optional	Outgroup Population (-op flag; default is same as cross population)
	 * @param Optional	Place for SelecT workspace (-wd flag; default is current directory)
	 * @param Optional	Window Size in Mb (-ws flag; default is 0.5Mb)
	 */
	public static void main(String[] args) {
		
		Log log = new Log(Log.type.envi);
		
		try {
			HashMap<String, Object> arg_map = setupArgs(args, log);
			
			String select_logo = " ____       _          _____ \n"
					+ "/ ___|  ___| | ___  __|_   _|\n"
					+ "\\___ \\ / _ \\ |/ _ \\/ __|| |  \n"
					+ " ___) |  __/ |  __/ (__ | |  \n"
					+ "|____/ \\___|_|\\___|\\___||_|  \n";
			System.out.println(select_logo);
		
			SetupDriver dv = new SetupDriver(arg_map, log);
			dv.runSetup();
			
			System.out.println("Setup Finished");
			log.addLine("\n\nYour environment has been set up successfully!");
			log.addLine("Phase one of SelecT is complete.");
			
		} catch (Exception e) {
			
			System.out.println("SelecT Died Prematurely." 
					+ " Check log output for troubleshooting.");
			
			log.addLine("\n\nSelecT Died Prematurely. Error in computation.");
			
			e.printStackTrace();
		}
	}
	
    private static HashMap<String, Object> setupArgs(String[] args, Log log)
            throws IllegalInputException {

    	ArgumentParser parser = ArgumentParsers.newArgumentParser("EnviSetup")
    				.defaultHelp(true)
                    .description("Set up the environment for SelecT");
    	
    	//Creating required arguments
    	parser.addArgument("data_dir").type(Arguments.fileType().verifyIsDirectory()
                    .verifyCanRead()).help("Directory with data files");

    	parser.addArgument("map_dir").type(Arguments.fileType().verifyIsDirectory()
                    .verifyCanRead()).help("Directory with map file");
    	
    	//TODO: Don't hardcode the number
    	parser.addArgument("start_chr").type(Integer.class).help("Starting chromosome number. "
    			+ "Must be equal to or greater than 1.");

    	//TODO: Still don't hardcode the number
    	parser.addArgument("end_chr").type(Integer.class).help("Ending chromosome number. "
    			+ "Must be equal to or greater than starting chromosome number.");
    	
    	//TODO: DONE. Check to see if the species is passed correctly
    	parser.addArgument("species").type(String.class)
    			.help("Species name has to match the following format: genus_species");
    	
    	//TODO: Hardcoding removed. Need to include correct wiki page ref
    	parser.addArgument("target_pop").help("Target population. See wiki at https://github.com/jbelyeu/mySelecT/wiki for details");
    	
    	//TODO: Hardcoding removed. Need to include correct wiki page ref
    	parser.addArgument("cross_pop").help("Cross population. See wiki at https://github.com/jbelyeu/mySelecT/wiki for details");
    	
    	//Creating optional arguments
    	//TODO: Hardcoding removed. Need to include correct wiki page ref
    	parser.addArgument("-op", "--out_pop").help("Outgroup population. If not included, defaults to equal cross population. "
    			+ "See wiki at https://github.com/jbelyeu/mySelecT/wiki for details");

    	parser.addArgument("-wd", "--working_dir").type(Arguments.fileType().verifyIsDirectory()
                    .verifyCanRead()).setDefault(System.getProperty("user.dir"))
                    .help("Defines the directory where SelecT will create a new working directory. "
                    		+ "If not set, defaults to the current working directory");

    	parser.addArgument("-ws", "--win_size").type(Double.class).setDefault(0.5)
                    .help("Window size in megabases. If not included, defaults to 0.5 megabases");

    	//Parsing user-inputed arguments
    	HashMap<String, Object> parsedArgs = new HashMap<String, Object>();
    	
    	//Checking to make sure input is correct
    	try {parser.parseArgs(args, parsedArgs);}
    	catch (HelpScreenException e)
		{
			//this shows up as an ArgumentParserException, so we catch it here to avoid a stack trace output
			System.exit(0);
		}
    	catch (ArgumentParserException e) {
            e.printStackTrace();
            String msg = "Error: Failed to parse arguments.\n" 
            		+ "\t*" + e.getMessage();
            throw new IllegalInputException (log, msg);
        }

    	//require start_chr to be positive
    	int start_chr = (Integer)parsedArgs.get("start_chr");
	    if (start_chr < 1) {
	    	String msg = "Error: Start chromosome must be 1 or greater.";
	        throw new IllegalInputException(log, msg);
	    } 
    	
	    //default end_chr to start_chr if not set
	    if (parsedArgs.get("end_chr") == null) {
	        Object start = parsedArgs.get("start_chr");
	        parsedArgs.put("end_chr", start);
	    } else if ((Integer) parsedArgs.get("end_chr") < (Integer) parsedArgs.get("start_chr")){
	    	String msg = "Error: End Chromosome comes before start chromosome";
	        throw new IllegalInputException(log, msg);
	    }
	
	    //default out_pop to cross population if not set otherwise
	    if (parsedArgs.get("out_pop") == null) {
	        Object XP = parsedArgs.get("cross_pop");
	        parsedArgs.put("out_pop", XP);
	    }
	
	    //make sure the out-group pop and the cross pop are not the same as the target pop
	    if (parsedArgs.get("cross_pop").equals(parsedArgs.get("target_pop") ) ) {
	    	String msg = "Error: Cross population is same as target population";
	        throw new IllegalInputException(log, msg);
	    } else if (parsedArgs.get("out_pop").equals(parsedArgs.get("target_pop"))) {
	    	String msg = "Error: Out-group population (-op) is same as target population";
	        throw new IllegalInputException(log, msg);
	    }
	    
	    //Check if species name is provided and of the correct format
	    if (parsedArgs.get("species") == null) {
	    	String msg = "Error: Species name is not provided. "
	    				+ "Please add genus_species to the list of arguments";
	    	throw new IllegalInputException(log, msg);
	    } else if (!((String)parsedArgs.get("species")).matches("\\w+_\\w+")) {
	    	String msg = "Error: Species name is of the wrong format. "
	    				+ "The correct format should be: genus_species";
	    	throw new IllegalInputException(log, msg);
	    }
    
	    log.addLine("Working Parameters");
	    log.addLine("Data Dir:\t\t" + parsedArgs.get("data_dir"));
	    log.addLine("Map Dir:\t\t" + parsedArgs.get("map_dir"));
	    log.addLine("Envi Output Dir:\t" + parsedArgs.get("working_dir"));
	    log.addLine("Species\t\t" + parsedArgs.get("species")); // check to see if this works
	    log.addLine("Target Pop:\t\t" + parsedArgs.get("target_pop"));
	    log.addLine("Cross Pop:\t\t" + parsedArgs.get("cross_pop"));
	    log.addLine("Outgroup Pop:\t\t" + parsedArgs.get("out_pop"));
	    log.addLine("Chr Range:\t\t" + parsedArgs.get("start_chr") + "-" + parsedArgs.get("end_chr"));
	    log.addLine("Window Size:\t\t" + parsedArgs.get("win_size") + " Mb");
	
	    return parsedArgs;
    }
}

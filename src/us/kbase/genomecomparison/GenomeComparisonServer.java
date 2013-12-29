package us.kbase.genomecomparison;

//BEGIN_HEADER
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
//END_HEADER

/**
 * <p>Original spec-file module name: GenomeComparison</p>
 * <pre>
 * </pre>
 */
public class GenomeComparisonServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    private final String configPath;
    private TaskHolder taskHolder = null;
    
    private TaskHolder getTaskHolder() throws Exception {
    	if (taskHolder == null) {
    		Properties props = new Properties();
    		props.load(new FileInputStream(new File(configPath)));
    		int threadCount = 1;
    		if (props.containsKey("thread.count"))
    			threadCount = Integer.parseInt(props.getProperty("thread.count"));
    		File tempDir = new File(".");
    		if (props.containsKey("temp.dir"))
    			tempDir = new File(props.getProperty("temp.dir"));
    		File blastBin = null;
    		if (props.contains("blast.bin"))
    			blastBin = new File(props.getProperty("blast.bin"));
    		taskHolder = new TaskHolder(threadCount, tempDir, blastBin);
    	}
    	return taskHolder;
    }
    //END_CLASS_HEADER

    public GenomeComparisonServer() throws Exception {
        super("GenomeComparison");
        //BEGIN_CONSTRUCTOR
        configPath = getInitParameter("config_file");
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: blast_proteomes</p>
     * <pre>
     * </pre>
     * @param   input   instance of type {@link us.kbase.genomecomparison.BlastProteomesParams BlastProteomesParams} (original type "blast_proteomes_params")
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "GenomeComparison.blast_proteomes")
    public String blastProteomes(BlastProteomesParams input) throws Exception {
        String returnVal = null;
        //BEGIN blast_proteomes
        returnVal = getTaskHolder().addTask(input, null);
        //END blast_proteomes
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new GenomeComparisonServer().startupServer(Integer.parseInt(args[0]));
    }
}

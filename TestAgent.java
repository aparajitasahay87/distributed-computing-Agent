import Mobile.Agent;

/**
 * TestAgent is a test mobile agent that is injected to the 1st Mobile.Place
 * platform to print the breath message, migrates to all the platform to write/read data such as "Hello" in each platform's queue Each migration to a given Place writes or reads data from/to the queue in Place and prints Queues message to console "Hello" 
 *
 * @author  Aparajita Sahay
 *
 * @since   1.0
 */
public class TestAgent extends Agent {
    public int hopCount = 0;
    public String[] destination = null;
    public String operation = null;
    public int numberOfMachines = 0;
    
    /**
     * The consturctor receives a String array as an argument from 
     * Mobile.Inject.
     *
     * @param args arguments passed from Mobile.Inject to this constructor
     */
    public TestAgent( String[] args ) throws Exception {
	destination = args;
	//1. Validate - minimum 2 arguments.
	if(destination.length < 2 )
	{
		throw new Exception("Please pass atleast two argument containing machine name and operation: reader or writer");
	}	
	
	//2.Last argument is Reader or Writer 
	String opr = destination[destination.length-1].toLowerCase();
	System.out.println("Operation received " + opr);
	if(opr.equals("reader"))
	{
		//set operation variable to reader
		operation = opr;
		
	}
	else if (opr.equals("writer"))
	{
		//set operation varible to writer
		operation = opr;
		
	}
	else
	{
		throw new Exception("Please enter valid operation: reader or writer");
	}
	
	//total number of machine in destination array is 1 less than it;s length. Last element is the operation user want to have
	//reader or writer.
	numberOfMachines = destination.length - 1;
		
 }
    
    /**
     * init( ) is the default method called upon an agent inject.
     */
    public void init( ) {
    	
	System.out.println( "agent( " + agentId + ") invoked init: " +
			    "hop count = " + hopCount +
			    ", next dest = " + destination[hopCount] );
	
	//sets argument value.
	String[] args = new String[1];
	args[0] = "Hello";
	//Call run method and pass arguments to run
	run(args);	
    
    }
    
    /**
     * run( ) is invoked upon an agent migration to destination[hopCount] after 
     * init()'s call run( ) and that calls hop( ).

     *Each machine calls hop method to migrate agent over all machines where Place is running 
     * @param args arguments passed from init( )'s run() method.
     */
    public void run(String[] message)
    {
    	
    	System.out.println( "agent( " + agentId + ") invoked another hop: " +
			    "hop count = " + hopCount +
			    ", next dest = " + destination[hopCount] );
    	
    	//pop data from each place machine if operation is reader -> destination[hopCount]
    	if(operation.equals("reader"))
    	{
    		getPlace().popData();
    	}
    	else
    	{
    		//if operation is writer push data to each queue in a Place machine -> destination[hopCount]
    		getPlace().pushData(message[0] + " " );
    	}
    	/*hop method will be executed for number of machines (argumens TestAgent) times. 
    	 * Each time same method is executed on different Place by agent with different arguments i.e. different destination but execute same mathod on different machine.
    	 */
    	if(hopCount < numberOfMachines)
    	{   		
    		//args[0] = "Hello" ;
    		hopCount++;
    		hop( destination[hopCount-1], "run", message );
    		
    	}
    }
}

package Mobile;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;

/**
 * Mobile.Place is the our mobile-agent execution platform that accepts an
 * agent transferred by Mobile.Agent.hop( ), deserializes it, and resumes it
 * as an independent thread.
 *
 * @author  Munehiro Fukuda
 * @version %I% %G$
 * @since   1.0
 */
public class Place extends UnicastRemoteObject implements PlaceInterface {
    private AgentLoader loader = null;  // a loader to define a new agent class
    private int agentSequencer = 0;     // a sequencer to give a unique agentId
    private Queue<String> queue = new LinkedList<String>(); //Shared Queue, to implements inter agent communication feature.

    /**
     * This constructor instantiates a Mobiel.AgentLoader object that
     * is used to define a new agen class coming from remotely.
     */
    public Place( ) throws RemoteException {
	super( );
	loader = new AgentLoader( );
	
	System.out.println("Place ready...");
    }

    /**
     * deserialize( ) deserializes a given byte array into a new agent.
     *
     * @param buf a byte array to be deserialized into a new Agent object.
     * @return a deserialized Agent object
     */
    private Agent deserialize( byte[] buf ) 
	throws IOException, ClassNotFoundException {
	// converts buf into an input stream
        ByteArrayInputStream in = new ByteArrayInputStream( buf );

	// AgentInputStream identify a new agent class and deserialize
	// a ByteArrayInputStream into a new object
        AgentInputStream input = new AgentInputStream( in, loader );
        return ( Agent )input.readObject();
    }

    /**
     * transfer( ) accepts an incoming agent and launches it as an independent
     * thread.
     *
     * @param classname The class name of an agent to be transferred.
     * @param bytecode  The byte code of  an agent to be transferred.
     * @param entity    The serialized object of an agent to be transferred.
     * @return true if an agent was accepted in success, otherwise false.
     */
    public boolean transfer( String classname, byte[] bytecode, byte[] entity ) {
		
    	try
    	{
    		//Register this calling agents classname and bytecode into AgentLoader
    		loader.loadClass(classname, bytecode);
    		
    		//Deserialize this agents entity.
    		Agent agent =deserialize(entity);
    		//Set reference of a place using Agents setPlace method
    		agent.setPlace(this);   
    		int id = agent.getId();
    		//Set this agents identifier if it has not yet been set Check if the agents id has
    		//already been set if not then set it
    		if(id == -1)
    		{
    			
    			int ipaddress = InetAddress.getLocalHost().hashCode();
    			agentSequencer++;
    			int newid = ipaddress + agentSequencer ;
    			//Set agent's id to a unique id.
    			agent.setId(newid);
    		}
    		
    		//For each request to transfer control to another machine create a thread t execute the function on another m/c
    		Thread thread = new Thread(agent);
			thread.start();
			System.out.println("Transfer success:" + agent.getId());
    	}
    	catch (Exception e)
    	{
    		//If transfer method is not successfull then return false else return true. 
    		return false;
    	}

    	return true;
    }

    /**
     * main( ) starts an RMI registry in local, instantiates a Mobile.Place
     * agent execution platform, and registers it into the registry.
     *
     * @param args receives a port, (i.e., 5001-65535).
     * @throws Exception 
     */
    public static void main( String args[] ) throws Exception {

    	//Validate the arguments 
    if( args == null ||
    	args.length == 0)
    {
    	throw new Exception("Please pass atleast one argument containing port number");
    }
    
	int port = Integer.parseInt(args[0]);
	//Validate the range of port number 
	if( port < 5001 && port > 65535)
	{
    	throw new Exception("port should be between 5001-65535.");
	}
	
	//Invoke startRegistry
	startRegistry(port);
	
	//Instantiate Place object 
	Place place = new Place();
	//Register it into rmiregistry through Naming.rebind( ).
	String name = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/place";
	Naming.rebind(name, place);
	
    }
    
    /**
     * startRegistry( ) starts an RMI registry process in local to this Place.
     * 
     * @param port the port to which this RMI should listen.
     */
    private static void startRegistry( int port ) throws RemoteException {
        try {
            Registry registry =
                LocateRegistry.getRegistry( port );
            registry.list( );
        }
        catch ( RemoteException e ) {
            Registry registry =
                LocateRegistry.createRegistry( port );
        }
    }
    
    /*Following implementation is for addition feature requirement.
     * Created a Queue, Now Test.java method will run two operations 1.writer and reader.
     * Writer method , each agent will write data to each Place upon migration. sMessage will be written in Queue
i
     * Reader method , each agent will read data from the each Place upon migration. 
     * And since the queue is a shared queue - multiple agent can access the same place's queue at one time. To avoid reace condition, used synchronize method.
    */
    public synchronized void pushData(String message)
    {
    	System.out.println("Place:Push:" + message);
    	queue.add(message);
    }
    
    public synchronized String popData()
    {
	if(queue.isEmpty())
	{
		System.out.println("Queue is empty:please write to Queue using Write operation:");
		String result = "";
		return result;	
	}
	else
	{	
    		String result = queue.remove();
    		System.out.println("Place:Pop:" + result);
    		return result;
	}
    }
}

# distributed-computing-AgentName: Aparajita Sahay
Assignment 3 
Date: 12/1/2015
1.	Documentation: Discussion on Agent.java and Place.java
a.	Initially when the program runs runPlace.sh, main () method in Place.java start RMI registry in local to this Place. Then instantiate the place object and register the place into rmiregistry through naming.rebind. 
b.	Agent call place’s transfer method on machine 1 local machine and set unique agent id to each incoming agent. 
c.	Init () method in MyAgent.java is called upon Agent. Inject. Init calls hop method in Agent.java. Hop () migrates agent to a given destination machine specified in hop as argument. Each migration will resume its execution as an independent thread and invokes a function specified in hop at a given destination machine/place. 
d.	Each agent has a reference to each place using rmiregistry. Each place register to RMI registry using naming.rebind() in main () in Place.java. And each agents gets a reference to a remote places by using Naming.lookup() in Agent.java hop().
e.	Hop method loads the agents byte code into memory using getBytecode() and serialize the byte code.
f.	Hop() receives hostname i.e. destination machine/place and looks for the particular place using naming.lookup(). If the machine exists then invoke RMI call using transfer method on Place’s reference. 
a.	Transfer method accepts incoming agent. 
b.	Transfer method in Place.java makes sure to give unique id to each agent’s request. For the very first when agent in invoked, transfer method make sure if the id is assigned to the agent. If id is not set then it creates new id by concatenating ipaddress and agentsequencer number, this will always generate a unique id and each agent is assigned a unique id. 
c.	Transfer() in Place.java creates independent thread for each hop call. Each thread handles execution of function in a destination machine or place. Each thread executes run method in Agent .java.
d.	Run()  finds the method/function to execute using reflection and invoke the method using method.invoke. Once method is executed successfully, then thread to run this agent is stopped.  
e.	Using MyAgent.java and similar implementation, programmer can invoke hop method i.e. migrate agent to any machine where Place is running.
f.	Using serialization method each agent’s object is converted into byte array and can be transferred over the network to different machine and using deserialization in transfer method at place.java agent’s reference can be decoded back.

2.	Discussion on TestAgent.java additional feature
a.	To execute TestAgent.java additional arguments i.e. reader or writer should be passed as arguments along with machines. “./runTestAgent.java uw1-320-04 uw1-320-06 writer” 
b.	Indirect Inter agent communication – using Queue
c.	Queue is created in each Place. First, one agent writes data to local machine place’s queue and then hop to next destination and do the same thing. Another agent reads data from each Place’s queue and hops to next destination to read from the queue. By using this procedure multiple agent can access the common Queue in Place to communicate with each other. One agent can put data in Queue and another agent can get data from Queue.
d.	To make sure that each agent either write or read data to queue in Place, one additional argument is passed by the user i.e. “writer” or “reader” as an argument to TestAgent.java along with machines name.
e.	There can be multiple agent accessing the place’s queue at same time so to avoid race condition both push and pop method in place are kept inside synchronized block. Therefore, at a time now only one agent can read or write in each place’s queue.
f.	Before adding additional feature, agent didn’t have a reference to each place. Therefore to set reference of each place on an agent, setplace() method of Agent.java is invoked from transfer() in Place.java. it sets the place reference at agent. And using getPlace method in TestAgent.java.java , agent receives the reference of each place and perform push or pop operation on place’s queue. 
g.	Agent.java class objects gets serialized and transferred to Place.java, in this process _place variable in Agent.java also gets serialized. Program wants to avoid serialization of _place variable therefore used “transient” keyword at _place variable initialization. Using transient keyword whole class can be serialized except those variables which are transient. 
h.	In TestAgent.java, each agent hops to all the machines recursively.
i.	Now two agents can write different data to Queue and some another agent will read data from Queue. 
j.	“writer” push data to Queue and “reader” pop data from queue i.e. removes data from Queue after reading it.
k.	 Limitation and Improvement: Currently, there is only one Queue for each Place. Each message in Queue can be read by all the agents. In future, we can make sure that each agent can have their own Queue in Place. And there will be a hash table with agent id as key and Queue as value. Using this technique now agent can put data in a Queue corresponding to a particular agent id. If agent A wants to transfer some data to agent B then agent A will write data to agent’s B queue and agent B will read its Queue and can read data.
Using this technique, now each agents will read only their own private Queue.  
It will maintain security in each Place because now multiple agents have an access to read only their own Queue and can write to any agent queue by accessing hash table (agent-id as key)

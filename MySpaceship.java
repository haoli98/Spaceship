package student;

import java.util.ArrayList; 
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;

import controllers.Driver;
import models.Board;
import models.NodeStatus;
import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;
import student.Heap;
import student.Paths;


/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {
	public HashSet<Long> visited = new HashSet<Long>();//visited
	public LinkedList<Long> paths = new LinkedList<Long>();
	public HashSet<Long> unreachable = new HashSet<Long>();
	
		// TODO Auto-generated constructor stub
	

	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * 
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */
	// TODO : Find the missing spaceship
	@Override
	public void rescue(RescueStage state) {
		
		try{
		rescueHelper(state);
		}
		catch (ArrayIndexOutOfBoundsException e) {	
			return;
		}
	}
	
	/** rescueHelper determines the optimal path to Planet X using
	 * the ping of neighboring planets
	 */
	public void rescueHelper(RescueStage state) {
		
		 Object[] a = state.neighbors().toArray();//stores an array of all the neigbors 
		 Arrays.sort(a); //sorts them in acending order based pings
		
				//create a arraylist sort it and go in order of ping
				long currentState = state.currentLocation();// id 
				visited.add(currentState);
				if (state.foundSpaceship()) throw new ArrayIndexOutOfBoundsException();
				long l = state.currentLocation();
				for (int i = 0; i<a.length; i++){
					if(!visited.contains(((NodeStatus) a[i]).getId())){
						state.moveTo(((NodeStatus) a[i]).getId());
						//if there are no more unvisited neighbors and 
						//need to backtrack
						rescueHelper(state);
						state.moveTo(l);					
					}
				}
	}

	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ScramState. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth
	
		List<models.Node> shortPath = Paths.shortestPath(state.currentNode(), state.getEarth()); 	
	
		for (int x=1; x< shortPath.size(); x++) {
			state.moveTo(shortPath.get(x));
			if (shortPath.get(x).hasSpeedUpgrade()) {
				state.grabSpeedUpgrade();
			}
		}
		}
	}
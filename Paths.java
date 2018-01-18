
package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Edge;
import models.Node;

import models.ReturnStage;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {
	
    /** Return the shortest path with respect to time from start to end, 
     * or the empty list if a path
     * does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
	
    public static List<Node> shortestPath(Node start, Node end) {
    	
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides

        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part 
        // of the map contains the TData instance
        // that describes the path's characteristics such as time 
        //taken or number of hostiles encountered while on the path
        
        
        HashMap<Node, TData> map= new HashMap<Node, TData>();

        F.add(start, 0);
        map.put(start,  new TData(null, null, 0,0,1));
        
        while (F.size() != 0) {
        Node f = F.poll();
        
        if (f==end) return constructPath(end,map);
           TData ftdata = map.get(f);
        	double fTime= map.get(f).time;
            double fSpeed = map.get(f).speed;
            int fHostile = map.get(f).numHostiles;
            for (Edge e : f.getExits()) {
            	Node w= e.getOther(f);
            	
            	double newWtime = fTime + e.length/fSpeed;

            	if (fHostile >=1  && w.isHostile()) {newWtime = 999999999;}
            		            	
            	TData wData= map.get(w);
                               
                if (wData == null) { 
                	if (w.isHostile() && !w.hasSpeedUpgrade()) {
                		fSpeed = fSpeed - 0.2;
                		if (fSpeed < 1) {
                			fSpeed = 1;
                		}
                    map.put(w, new TData(f,ftdata ,newWtime, fHostile + 1, fSpeed ));
                	}
                	else if (!w.isHostile() && !w.hasSpeedUpgrade()) {
                		map.put(w,  new TData(f, ftdata, newWtime, fHostile, fSpeed));
                	}
                	else if(!w.isHostile() && w.hasSpeedUpgrade()) {
                		map.put(w, new TData(f, ftdata, newWtime, fHostile, fSpeed + 0.2));
                	}
                	else if (w.isHostile() && w.hasSpeedUpgrade()) {
                		if (fSpeed == 1) {
                		map.put(w, new TData(f, ftdata, newWtime, fHostile + 1, fSpeed +0.2 ));
                		}
                		else if (fSpeed >1) {
                			map.put(w,  new TData(f, ftdata, newWtime, fHostile + 1, fSpeed-0.2));
                		}
                	}                    
                    F.add(w, newWtime);                          
                } 
                else if (newWtime < wData.time) {
                    wData.time= newWtime;
                    wData.backPointer= f;
                    if (w.isHostile()) {wData.numHostiles = wData.numHostiles + 1;}
                    if (w.hasSpeedUpgrade()) {wData.speed = wData.speed + 0.2;}
                    F.updatePriority(w, newWtime);
                }
            }
        }
        // no path from start to end
        return new LinkedList<Node>();
    }
    

    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, TData> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }
   
    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getConnect(q).length;
                p= q;
            }
            return s;
        }
    }

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private int distance; // distance from start node to this one
        

        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer;
        }
    }
    
    /** An instance contains information about a node: the previous node
     * on a shortest time path from the start node to this node, the TData of
     * the backpointer, the speed of the node, the time it takes to reach this 
     * node, and the number of hostiles it crosses to reach the node 
     * @author hl98
     *
     */
    private static class TData {     
    	private Node backPointer;	//Backpointer on the path (null if start node)
        private TData back;// TData instance that represents the backpointer node
        private double time;//time it takes from start node to this one ... is equal to distance/speed
        private int numHostiles;	//number of hostiles the node passes from start to this node
        private double speed;  //speed that the node is traveling at
        /** Constructor: an instance with distance d from the start node and
       *  backpointer p.*/
       
        private TData(Node p, TData a, double t, int n, double s) {
            backPointer= p; // Backpointer on the path (null if start node)
            back = a;
            time = t;
            numHostiles = n;
            speed = s;         
        }
        }
}

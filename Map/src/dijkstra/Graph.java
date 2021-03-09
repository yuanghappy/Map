package dijkstra;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph<E> {
	
	HashMap<E, Vertex> vertices;
	HashSet<Edge> connections;

	public Graph() {
		vertices = new HashMap<E, Vertex>();
		connections = new HashSet<Edge>();
	}
	
	public void addVertex(E info, int x, int y) {
		vertices.put(info, new Vertex(info, x, y));
	}
	
	public void removeVertex(E info){
		//target vertex to remove
		Vertex deleteV = vertices.get(info);
		//go through every neighbor of the target vertex
		for(Edge neighborE : deleteV.neighbors){
			Vertex neighborV = neighborE.getneighbor(deleteV);
			//go through the neighbors hashset and remove the edge that connects
			//that neighbor with the target vertex
			for(Edge e : neighborV.neighbors){
				if(e.getneighbor(neighborV).equals(deleteV)){
					neighborV.neighbors.remove(e);
					//remove the edge from connections hash set
					if(connections.contains(e)){
						connections.remove(e);
					}
					break;
				}
			}
		}
		//remove the target vertex
		vertices.remove(info, deleteV);
		System.out.print("removed");

	}
	
	public void connect(E info1, E info2) {
		Vertex v1 = vertices.get(info1);
		Vertex v2 = vertices.get(info2);
		
		Edge e = new Edge(v1, v2);
		v1.neighbors.add(e);
		v2.neighbors.add(e);
		connections.add(e);
	}
	
	public class Edge {
		int label;
		Vertex v1, v2;
		
		public Edge(Vertex v1, Vertex v2){
			this.v1 = v1; this.v2 = v2;
			//calculate distance between two vertices and store it in Edge label
			this.label = (int) Math.hypot(v1.x-v2.x, v1.y-v2.y);
		}
		
		public Vertex getneighbor(Vertex v){
			if(v.info.equals(v1.info)){
				return v2;
			}else{
				return v1;
			}
		}
	}

	public class Vertex {
		E info;
		int x, y;
		HashSet<Edge> neighbors;
		
		public Vertex(E info, int x, int y) {
			this.info = info;
			this.x = x;
			this.y = y;
			neighbors = new HashSet<Edge>();
		}
		
	}
	
	public ArrayList<Object> search(E start, E target) {
		
		Vertex startV = vertices.get(start);
		Vertex targetV = vertices.get(target);

		
		//Priority Queue toVisit setup
		PriorityQueue<Vertex> toVisit = new PriorityQueue<Vertex>();
		toVisit.put(startV, 0);
		
		//Dist from start map setup
		HashMap<Vertex, Integer>DistFromStartMap = new HashMap<Vertex, Integer>();
		for(Vertex tempv : vertices.values()){
			if(tempv.equals(startV)){
				DistFromStartMap.put(tempv, 0);
			}else{
				DistFromStartMap.put(tempv, Integer.MAX_VALUE);
			}
		}
		
		//Visited hashset setup
		HashSet<Vertex> visited = new HashSet<Vertex>();
		//visited.add(startV);
		
		//Leads to map setup
		HashMap<Vertex, Edge> leadsTo = new HashMap<Vertex, Edge>();
		
		//SEARCH
		while (toVisit.size() != 0) {
			//pop the vertex with smallest priority number in toVisit
			Vertex curr = (Graph<E>.Vertex) toVisit.pop().info;
			
			//check if the vertex popped is the target
			if(curr.equals(targetV)){
				return backtrace(curr, leadsTo);
			}
			
			//
			for (Edge e : curr.neighbors){
				Vertex v = e.getneighbor(curr);
				if (visited.contains(v)) continue;
				int distanceFromStart = DistFromStartMap.get(curr) + e.label;
				//if the new route's distance from start is smaller or there is no old route:
				if(distanceFromStart < DistFromStartMap.get(v)){
					//update distance from start map
					DistFromStartMap.put(v, distanceFromStart);
					toVisit.put(v, distanceFromStart);
					leadsTo.put(v, e);
				}
			}
			visited.add(curr);
		}
		return null;
	}
	
	public ArrayList<Object> backtrace(Vertex target, HashMap<Vertex, Edge> leadsTo) {
		
		Vertex curr = target;
		ArrayList<Object> path = new ArrayList<Object>();
		path.add(0, curr.info.toString());

		while (leadsTo.get(curr) != null) {
			path.add(0, leadsTo.get(curr).getneighbor(curr).info.toString());
			curr = leadsTo.get(curr).getneighbor(curr);
		}

		return path;	
	}
		
	public static void main(String[] args) {
		
		Graph<String> g = new Graph<String>();
				g.addVertex("A", 1, 1);
				g.addVertex("B", 3, 8);
				g.addVertex("C", 5, 1);
				g.addVertex("D", 2, -2);
				g.addVertex("E", 3, 0);
				g.connect("A", "B");
				g.connect("C", "B");
				g.connect("A", "D");
				g.connect("D", "E");
				g.connect("C", "E");
				System.out.println(g.search("A", "C"));
				g.removeVertex("D");
				System.out.println("D removed");
				System.out.println(g.search("A", "C"));

	}
}
package graphs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph<E, T> {
	
	HashMap<E, Vertex> vertices;

	public Graph() {
		vertices = new HashMap<E, Vertex>();
	}
	
	public void addVertex(E info) {
		vertices.put(info, new Vertex(info));
	}
	
	public void connect(E info1, E info2, T label) {
		Vertex v1 = vertices.get(info1);
		Vertex v2 = vertices.get(info2);
		
		Edge e = new Edge(label, v1, v2);
		v1.neighbors.add(e);
		v2.neighbors.add(e);
	}
	
	private class Edge {
		T label;
		Vertex v1, v2;
		
		public Edge(T label, Vertex v1, Vertex v2){
			this.label = label; this.v1 = v1; this.v2 = v2;
		}
		
		public Vertex getneighbor(Vertex v){
			if(v.info.equals(v1.info)){
				return v2;
			}else{
				return v1;
			}
		}
	}

	private class Vertex {
		E info;
		HashSet<Edge> neighbors;
		
		public Vertex(E info) {
			this.info = info;
			neighbors = new HashSet<Edge>();
		}
		
	}
	
	public ArrayList<Object> search(E start, E target) {
		
		ArrayList<Vertex> toVisit = new ArrayList<Vertex>();
		toVisit.add(vertices.get(start));
		HashSet<Vertex> visited = new HashSet<Vertex>();
		visited.add(vertices.get(start));
		
		HashMap<Vertex, Edge> leadsTo = new HashMap<Vertex, Edge>();
		
		while (!toVisit.isEmpty()) {
			
			Vertex curr = toVisit.remove(0);
			
			for (Edge e : curr.neighbors) {
				
				Vertex v = e.getneighbor(curr);
				
				if (visited.contains(v)) continue;
				
				leadsTo.put(v, e);
				
				if (v.info.equals(target)) {		
					return backtrace(v, leadsTo);
				}else {
					toVisit.add(v);
					visited.add(v);
				}
			}
		}
		return null;
	}
	
	public ArrayList<Object> backtrace(Vertex target, HashMap<Vertex, Edge> leadsTo) {
		
		Vertex curr = target;
		ArrayList<Object> path = new ArrayList<Object>();
		path.add(0, curr.info.toString());

		while (leadsTo.get(curr) != null) {
			path.add(0, leadsTo.get(curr).label.toString());
			path.add(0, leadsTo.get(curr).getneighbor(curr).info.toString());
			
			curr = leadsTo.get(curr).getneighbor(curr);
		}
		return path;	
	}
		
	public static void main(String[] args) {
		
		Graph<String, String> g = new Graph<String, String>();
		
		g.addVertex("one");
		g.addVertex("two");
		g.addVertex("four");
		g.addVertex("three");
		g.addVertex("five");
		g.addVertex("na");
		
		g.connect("one","two", "g");
		g.connect("two", "three", "h");
		g.connect("four", "three", "a");
		g.connect("five", "four", "b");
		
		g.search("one", "two");
	}
}
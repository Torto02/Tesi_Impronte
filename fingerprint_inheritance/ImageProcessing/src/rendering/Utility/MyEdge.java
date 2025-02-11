package rendering.Utility;





public class MyEdge implements Comparable<MyEdge> {

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}
	
	public boolean getVisitedM1() {
		return visitedM1;
	}
	
	public boolean getVisitedM2() {
		return visitedM2;
	}
	
	public boolean getVisitedEdge() {
		return getVisitedM1() || getVisitedM2();
	}

	public int getId2() {
		return id2;
	}

	public int getId1() {
		return id1;
	}


	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int id1;
	private int id2;
	private boolean visitedM1;
	private boolean visitedM2;
	private boolean visitedArc;

	//thoose two constructors are probably never used 
	public MyEdge(int x1, int y1, int x2, int y2, int id1, int id2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.id1 = id1;
		this.id2 = id2;
		visitedM1 = false;
		visitedM2 = false;
		visitedArc = false;
	}
	
	public MyEdge(int x1, int y1, int x2, int y2, int id1, int id2, boolean v1, boolean v2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.id1 = id1;
		this.id2 = id2;
		this.visitedM1 = v1;
		this.visitedM2 = v2;
		visitedArc = false;
	}
	
	public void setVisitedM1(boolean visited) {
		this.visitedM1 = visited;
	}
	
	public void setVisitedM2(boolean visited) {
		this.visitedM2 = visited;
	}
	
	public void setVisited(boolean visited) {
		this.visitedArc=visited;
	}
	
	public boolean getVisitedArc() {
		return this.visitedArc;
	}
	
	@Override
	public int compareTo(MyEdge edge) {
		int result;
		result = this.y1 - edge.y1;
		return result;
	}
}

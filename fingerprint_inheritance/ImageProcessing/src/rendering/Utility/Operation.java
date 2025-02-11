package rendering.Utility;


public class Operation {
	
	private String operation;
	private int i1;
	private int j1;
	private int i2;
	private int j2;
	private int partialScore;
	private Operation left;
	private Operation right;
	
	private int indexLeft;
	private int indexRight;
	
	public double keyLeft;
	public double keyRight;
	
	public int getIndexLeft() {
		return this.indexLeft;
	}
	
	public int getIndexRight() {
		return this.indexRight;
	}
	
	public void setIndexRight(int r) {
		this.indexRight = r;
	}
	
	public void setIndexLeft(int l) {
		this.indexLeft = l;
	}
	
	public int geti1() {
		return this.i1;
	}
	
	public int getj1() {
		return this.j1;
	}
	
	public int geti2() {
		return this.i2;
	}
	
	public int getj2() {
		return this.j2;
	}
	
	public Operation getLeft() {
		return this.left;
	}
	
	public Operation getRight() {
		return this.right;
	}
	
	public String getOperationString() {
		return this.operation;
	}
	
	public String getIndexString() {
		return this.i1 + ", " + this.j1 + ", " + this.i2 + ", " + this.j2;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void setIndex(int i1, int j1, int i2, int j2) {
		this.i1 = i1;
		this.i2 = i2;
		this.j1 = j1;
		this.j2 = j2;
	}
	
	public void setPartialScore( int score) {
		this.partialScore = score;
	}
	
	public void setOperationLeft(Operation left) {
		this.left = left;
	}
	
	public void setOperationRight(Operation right) {
		this.right = right;
	}
	
	public int getPartialScore() {
		return this.partialScore;
	}

}

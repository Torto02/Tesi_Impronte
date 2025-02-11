package rendering.Utility;

public class MyPoint {

    public void setLocation(int height, int width) {
        setHeight(height);
        setWidth(width);
    }

    public int getY() {
        return getHeight();
    }

    public void decreaseHeight() {
    	this.m_height--;
    }
    
    public void increaseHeight() {
    	this.m_height++;
    }
    
    public void increaseWidth()  {
    	this.m_width++;
    }
    
    public void decreaseWidth()  {
    	this.m_width--;
    }
    public int getHeight() {
        return m_height;
    }

    public void setY(int height) {
        setHeight(height);
    }

    public void setHeight(int height) {
        this.m_height = height;
    }

    public int getX() {
        return getWidth();
    }

    public int getWidth() {
        return m_width;
    }

    public void setX(int width) {
        setWidth(width);
    }

    public void setWidth(int width) {
        this.m_width = width;
    }

    private int m_height;
    private int m_width;
     public MyPoint(){
         setWidth(0);
         setHeight(0);
     }

    public MyPoint(int height, int width) {
        setHeight(height);
        setWidth(width);
    }
}
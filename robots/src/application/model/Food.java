package application.model;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

public class Food implements Entity{
    private volatile int x;
    private volatile int y;
    boolean spawn;

    public Food(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.spawn=true;
    }

    public Food()
    {
        this.x = 100;
        this.y = 100;
        this.spawn=false;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setFoodPosition(Point p)
    {
        setX(p.x);
        setY(p.y);
    }

    protected Point getFoodPosition()
    {
        return new Point(getX(), getY());
    }

    public boolean isPositionCorrect(Dimension dimension)
    {
        return this.x <= dimension.width && this.y <= dimension.height;
    }

    @Override
    public void update() {

    }

    @Override
    public void onStart(PropertyChangeSupport publisher) {

    }

    @Override
    public void onFinish(PropertyChangeSupport publisher) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}

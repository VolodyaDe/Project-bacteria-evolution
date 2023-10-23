package application.model;


import application.model.additionals.Condition;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

public class Bot implements Entity
{
    private double positionX;
    private double positionY;
    private volatile double botDirection;
    private Food foodGoal;
    private int lifeTime;
    public static double maxVelocity = 0.05;
    public static final double maxAngularVelocity = 0.005;
    private Dimension dimension;
    private Condition condition;
    private boolean isAlive;
    private static final int START_LIFETIME = 40;
    private static final int FULL_LIFETIME = 90;

    public Bot(double startX, double startY)
    {
        this.positionX = startX;
        this.positionY = startY;
        this.dimension = new Dimension(400, 400);
        this.foodGoal = new Food();//new Food((int) (Math.random() * dimension.width), (int) (Math.random() * dimension.height));
        this.botDirection = Math.random() * 10;
        this.lifeTime = (int) (START_LIFETIME + Math.random() * (FULL_LIFETIME - START_LIFETIME));
        this.condition = Condition.randomCondition();
        this.isAlive = true;
    }

    public Bot()
    {
        this.positionX = 300;
        this.positionY = 300;
        this.foodGoal = new Food();
        this.botDirection = 0;
    }

    public void setDimension(Dimension dimension)
    {
        this.dimension = dimension;
        if (!foodGoal.isPositionCorrect(dimension))
        {
            foodGoal.setX((int) (Math.random() * dimension.width));
            foodGoal.setY((int) (Math.random() * dimension.height));
        }
    }

    private void setRandomCondition()
    {
        this.condition = Condition.randomCondition();
    }

    public Dimension getDimension()
    {
        return this.dimension;
    }

    public double getPositionX()
    {
        return positionX;
    }

    public void setPositionX(double positionX)
    {
        this.positionX = positionX;
    }

    public double getPositionY()
    {
        return positionY;
    }

    public void setPositionY(double positionY)
    {
        this.positionY = positionY;
    }

    public double getBotDirection()
    {
        return botDirection;
    }

    public void setBotDirection(double botDirection)
    {
        this.botDirection = botDirection;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private double normalizedPositionX(double x)
    {
        if (x < 0)
            return 0;
        if (x > dimension.width)
            return dimension.width;
        return x;
    }

    private double normalizedPositionY(double y)
    {
        if (y < 0)
            return 0;
        if (y > dimension.height)
            return dimension.height;
        return y;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        return Math.min(value, max);
    }

    public Food getFoodGoal()
    {
        return foodGoal;
    }

    public void changeLifeTime(int lifeTmeDif)
    {
        this.lifeTime += lifeTmeDif;
    }

    public void setFoodGoal(Point point)
    {
        if (point.x > dimension.width || point.y > dimension.height)
        {
            this.foodGoal.setFoodPosition(new Point(point.x / dimension.width, point.y / dimension.height));
        }
        this.foodGoal.setFoodPosition(point);
    }

    public void setFoodGoal(Food food){
        this.foodGoal = food;
    }

    private void moveBot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, Bot.maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -Bot.maxAngularVelocity, Bot.maxAngularVelocity);
        double newX = getPositionX() + velocity / angularVelocity *
                (Math.sin(getBotDirection() + angularVelocity * duration) -
                        Math.sin(getBotDirection()));
        if (!Double.isFinite(newX))
            newX = getPositionX() + velocity * duration * Math.cos(getBotDirection());

        double newY = getPositionY() - velocity / angularVelocity *
                (Math.cos(getBotDirection() + angularVelocity * duration) -
                        Math.cos(getBotDirection()));
        if (!Double.isFinite(newY))
            newY = getPositionY() + velocity * duration * Math.sin(getBotDirection());

        setPositionX(normalizedPositionX(newX));
        setPositionY(normalizedPositionY(newY));
        double newDirection = asNormalizedRadians(getBotDirection() + angularVelocity * duration);
        setBotDirection(newDirection);
    }

    public Condition getCondition()
    {
        return this.condition;
    }

    @Override
    public void update()
    {
        if (!this.isAlive)
        {
            this.setCondition(Condition.DEAD);

            return;
        }
        if (this.lifeTime < 50)
        {
            if (this.lifeTime < 0)
            {
                isAlive = false;
            }
            this.setCondition(Condition.HUNGRY);
        }

        double distance = distance(foodGoal.getX(), foodGoal.getY(),
                getPositionX(), getPositionY());

        if (distance < 0.5)
        {
            this.foodGoalAchieved();
            return;
        }
        double angleToFoodGoal = angleTo(getPositionX(), getPositionY(),
                foodGoal.getX(), foodGoal.getY());
        double angularVelocity = 0;
        if (angleToFoodGoal > getBotDirection())
            angularVelocity = Bot.maxAngularVelocity;
        if (angleToFoodGoal < getBotDirection())
            angularVelocity = -Bot.maxAngularVelocity;

        moveBot(Bot.maxVelocity, angularVelocity, 10);
    }

    private void setCondition(Condition condition)
    {
        this.condition = condition;
    }

    @Override
    public void onStart(PropertyChangeSupport publisher)
    {
        publisher.addPropertyChangeListener(this);
    }

    @Override
    public void onFinish(PropertyChangeSupport publisher)
    {
        publisher.removePropertyChangeListener(this);
    }

    private void foodGoalAchieved()
    {
        //this.setFoodGoal(new Point((int) (Math.random() * dimension.width), (int) (Math.random() * dimension.height)));
        this.foodGoal=new Food();
        this.lifeTime += 20;
        if (this.lifeTime > 50)
        {
            this.setRandomCondition();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getPropertyName().equals("new point"))
            setFoodGoal((Point) evt.getNewValue());
        if (evt.getPropertyName().equals("change life time"))
            changeLifeTime((int) evt.getNewValue());
        if (evt.getPropertyName().equals("set dimension"))
            setDimension((Dimension) evt.getNewValue());
    }
}
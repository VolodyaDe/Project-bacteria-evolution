package application.model;

import application.view.draw.FoodDrawer;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.List;

import static java.awt.geom.Point2D.distance;

public class GameModel
{
    private Dimension dimension;
    private final List<Bot> bots;
    private final List<Food> foods;
    private final PropertyChangeSupport support;
    private static final int BOT_COUNT = 5;
    private static final int FOOD_COUNT = 5;

    public GameModel()
    {
        this.bots = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
        this.dimension=new Dimension(400, 400);

        for (int j = 0; j < FOOD_COUNT; j++)
        {
            int x = (int)(Math.random() * 400);
            int y = (int)(Math.random() * 400);
            Food food = new Food(x, y);
            foods.add(food);
        }

        for (int i = 0; i < BOT_COUNT; i++)
        {
            Bot bot = new Bot(Math.random() * 400, Math.random() * 400);
            bot.setFoodGoal(findFood(bot));
            bots.add(bot);
            addPropertyChangeListener(bot);
        }


        Timer timer = initTimer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("timer");
                support.firePropertyChange("change life time", null, -10);
                if (Math.random() * 4 > 2.3)
                    suddenBotClone();
            }
        }, 0, 3000);
    }

    private static java.util.Timer initTimer()
    {
        return new Timer("events generator", true);
    }

    public Food findFood(Bot bot) {
        double minDistance = Double.MAX_VALUE;
        Food nearestFood = null;

        for (Food food : foods) {
            double distance = distance(bot.getPositionX(), bot.getPositionY(), food.getX(), food.getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearestFood = food;
            }
        }

        if (nearestFood != null) {
            foods.remove(nearestFood);
        }

        return nearestFood;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setDimension(Dimension dimension)
    {
        this.dimension=dimension;
        for (Bot robot : bots)
            robot.setDimension(dimension);
    }

    public Dimension getDimension()
    {
        return bots.get(0).getDimension();
    }

    public void updateModel()
    {
        for (Food food : foods){
            if (!food.isPositionCorrect(dimension))
            {
                food.setX((int) (Math.random() * dimension.width));
                food.setY((int) (Math.random() * dimension.height));
            }
        }
        for (Bot bot : bots){
            Food f = bot.getFoodGoal();
            if (f.spawn==false) {
                int xx = (int) (Math.random() * dimension.width);
                int yy = (int) (Math.random() * dimension.height);
                Food food = new Food(xx, yy);
                foods.add(food);
                bot.setFoodGoal(findFood(bot));
            }
            bot.update();
        }
    }


    private void suddenBotClone()
    {
        Random rand = new Random();
        Bot bot = bots.get(rand.nextInt(bots.size()));
        double x = bot.getPositionX();
        double y = bot.getPositionY();
        removePropertyChangeListener(bot);
        bots.remove(bot);
        for (int i = 0; i < 2; i++){
            int xx = (int) (Math.random() * dimension.width);
            int yy = (int) (Math.random() * dimension.height);
            Food food = new Food(xx, yy);
            foods.add(food);
            Bot b = new Bot(x, y);
            b.setFoodGoal(findFood(b));
            bots.add(b);
        }
    }

    public List<Bot> getRobots()
    {
        return bots;
    }

    public List<Food> getFoods()
    {
        return foods;
    }

    public void setFoodGoal(Point point)
    {
        support.firePropertyChange("new point", null, point);
    }
}

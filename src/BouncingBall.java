import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

import java.awt.*;

public class BouncingBall {

    // private final static double VELOCITY_STEP = 0.5f;

    private double x = 10.0f;

    private double y = 10.0f;


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity(Vect velocity) {
        this.velocity = velocity;
    }

    public int getRadius() {
        return radius;
    }

    public Vect getVect(){
        return new Vect(this.x, this.y);
    }

    private Vect velocity;
    private double gravity;
    private Vect friction;

    private int radius = 6;


    private Color color = new Color(255, 0, 0);

    // Keep track of the animation window that will be drawing this ball.
    private AnimationWindow win;

    /**
     * Constructor.
     * @param win Animation window that will be drawing this ball.
     */
    public BouncingBall(AnimationWindow win) {
        this.win = win;
        this.velocity=new Vect(5.0,0.0);
        gravity=5;
        friction=new Vect(1.0,1.0);
    }

    public void remove(){
        this.x = -100;
        this.y = -100;
        this.velocity=new Vect(5.0,0.0);
        win.setMode(false);
    }

    public void inTrack(){
        double vx = this.velocity.x();
        this.velocity=new Vect(vx,0.0);
    }

    /**
     * @modifies this
     * @effects Moves the ball according to its velocity.  Reflections off
     * walls cause the ball to change direction.
     */
    public void move(int inteval) {
        double intevals=inteval*1.0f/100;
        x +=  velocity.x() * intevals;
        y +=  velocity.y() * intevals;

        double newVx = velocity.x()  ;
        double newVy = velocity.y();
        if (x <= radius) {
            x = radius;
            newVx=-newVx;
        }
        if (x >= win.getWidth() - radius) {
            x = win.getWidth() - radius;
            newVx=-newVx;
        }


        if (y <= radius) {
            y = radius;
            newVy=-newVy;
        }
        if (y >= win.getHeight() - radius) {
            y = win.getHeight() - radius;
            newVy=-newVy;
        }
        newVx = newVx  *friction.x();
        newVy = (newVy+ gravity*intevals )*friction.y();
        if(newVx==0) newVx++;
        if(newVy==0) newVy++;
        this.velocity = new Vect(newVx, newVy);
    }

    public CollisionInfo detectCollision(java.util.List<AbstractGizmo> gizmoList) {
        Circle c=new Circle(x,y,radius);
        //only line collision, fill others here!
        for (AbstractGizmo gizmo : gizmoList) {
            for (LineSegment lineSegment : gizmo.lines) {
                if (Geometry.timeUntilWallCollision(lineSegment,c,velocity)<=0.5f) {
                    return new CollisionInfo(gizmo, lineSegment);
                }
            }

            for(Circle circle : gizmo.corners){
                if(Geometry.timeUntilCircleCollision(circle,c,velocity)<=0.5f){
                    return new CollisionInfo(gizmo, circle);
                }
            }
        }
        return null;
    }
    public void dealCollision(CollisionInfo collisionInfo) {
        AbstractGizmo abstractGizmo = collisionInfo.getGizmo();
        Vect newVect;
        LineSegment lineSegment;
        Circle circle;
        if(collisionInfo.getLineSegment() != null)
        {
            lineSegment = collisionInfo.getLineSegment();
            // Geometry.timeUntilWallCollision();
            newVect = Geometry.reflectWall(lineSegment, this.velocity);
            this.setVelocity(newVect);
        }

        else if(collisionInfo.getCircle() != null)
        {
            circle = collisionInfo.getCircle();
            newVect = Geometry.reflectCircle(circle.getCenter(), this.getVect(), this.velocity);
            this.setVelocity(newVect);
        }

        if(abstractGizmo.isCrashMove() == true)
        {
            if(x < abstractGizmo.x)
                abstractGizmo.x =abstractGizmo.x + 20;
            else
                abstractGizmo.x = abstractGizmo.x - 20;

            if(x < abstractGizmo.y)
                abstractGizmo.y =abstractGizmo.y + 20;
            else
                abstractGizmo.y = abstractGizmo.y- 20;

            int ok = 1;
            for(int j = 0; j < win.getGizmoList().size(); j++)
            {
                if(win.getGizmoList().get(j) != abstractGizmo)
                {
                    if(abstractGizmo.x == win.getGizmoList().get(j).x && abstractGizmo.y == win.getGizmoList().get(j).y)
                    {
                        ok = 0;
                        break;
                    }
                }
            }

            if(ok == 1)
            {
                abstractGizmo.buildMove(abstractGizmo.x, abstractGizmo.y );
                win.repaint();
            }
        }
    }

    /**
     * @modifies the Graphics object <g>.
     * @effects paints a circle on <g> reflecting the current position
     * of the ball.
     * @param g Graphics context to be used for drawing.
     */
    public void paint(Graphics g) {
        Rectangle clipRect = g.getClipBounds();
        if (clipRect.intersects(this.boundingBox())) {
            g.setColor(color);
            g.fillOval((int)x - radius, (int)y - radius, radius + radius, radius
                    + radius);
        }
    }

    /**
     * @return the smallest rectangle that completely covers the current
     * position of the ball.
     */
    public Rectangle boundingBox() {
        // a Rectangle is the x,y for the upper left corner and then the width and height
        return new Rectangle((int)x - radius - 1, (int)y - radius - 1, radius + radius + 2,
                radius + radius + 2);
    }


    public void resetPosition() {
        x=0;
        y=0;
        velocity=new Vect(5.0,0.0);
    }


}
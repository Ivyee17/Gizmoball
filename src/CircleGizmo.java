import javafx.scene.shape.Ellipse;
import physics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class CircleGizmo extends AbstractGizmo {

    public CircleGizmo(AnimationWindow animationWindow) {
        super(animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.PINK;
        makeCircle();
    }
    public CircleGizmo(int x, int y, int r, int degree, boolean crashMove,  AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.PINK;
        makeCircle();
    }
    public void makeCircle(){
        lines.clear();
        corners.clear();
        while(degree>=360) degree-=360;
        Angle angle=findAngleByDegree(degree);
        Vect center=new Vect(new Point(x,y));
        corners.add(Geometry.rotateAround( new Circle(x,y,r),center,angle));
    }



    @Override
    public void paint(Graphics g) {

        Rectangle clipRect = g.getClipBounds();
        Graphics2D g2d=(Graphics2D)g;
        g2d.setColor(color);
        makeCircle();
        for(LineSegment lineSegment:lines){
            Line2D.Double d=lineSegment.toLine2D();
            g2d.draw(d);
        }
        for(Circle corner:corners){
            Ellipse2D d= corner.toEllipse2D();
            Ellipse2D.Double dd=new Ellipse2D.Double(d.getX(),d.getY(),d.getHeight(),d.getWidth());
            g2d.fill(dd);
        }

    }

}
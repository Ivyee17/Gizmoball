import physics.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class TriangleGizmo extends AbstractGizmo {

    public TriangleGizmo(AnimationWindow animationWindow) {
        super(animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.BLUE;
        makeTriangle();
    }
    public TriangleGizmo(int x, int y, int r, int degree, boolean crashMove,  AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);
        makeTriangle();
    }
    public void makeTriangle(){
        lines.clear();
        corners.clear();
        while(degree>=360) degree-=360;
        Angle angle=findAngleByDegree(degree);
        Vect center=new Vect(new Point(x,y));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x+r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x-r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y+r,x+r,y+r),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y-r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y+r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x+r,y+r,0),center,angle));
    }



    @Override
    public void paint(Graphics g) {

        Rectangle clipRect = g.getClipBounds();
        Graphics2D g2d=(Graphics2D)g;
        g2d.setColor(color);
        makeTriangle();
        for(LineSegment lineSegment:lines){
            Line2D.Double d=lineSegment.toLine2D();
            g2d.draw(d);
        }
        for(Circle corner:corners){
            Ellipse2D d= corner.toEllipse2D();
            g2d.draw(d);
        }
        Polygon polygon = new Polygon(new int[] { x-r, x-r, x+r }, new int[] { y-r, y+r, y+r }, 3);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(degree), polygon.getBounds().getCenterX(),
                polygon.getBounds().getCenterY());
        Shape transformed = transform.createTransformedShape(polygon);
        g2d.fill(transformed);
    }
}

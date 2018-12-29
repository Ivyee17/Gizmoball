import physics.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class SquareGizmo extends AbstractGizmo {

    public SquareGizmo(AnimationWindow animationWindow) {
        super(animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.GREEN;
        makeSquare();
    }
    public SquareGizmo(int x, int y, int r, int degree, boolean crashMove,  AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.GREEN;
        makeSquare();
    }
    public void makeSquare(){
        lines.clear();
        corners.clear();
        while(degree>=360) degree-=360;
        Angle angle=findAngleByDegree(degree);
        Vect center=new Vect(new Point(x,y));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x+r,y-r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x-r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y+r,x+r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x+r,y-r,x+r,y+r),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y-r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y+r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x+r,y+r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x+r,y-r,0),center,angle));
    }



    @Override
    public void paint(Graphics g) {

        Rectangle clipRect = g.getClipBounds();
        Graphics2D g2d=(Graphics2D)g;
        g2d.setColor(color);
        makeSquare();
        for(LineSegment lineSegment:lines){
            Line2D.Double d=lineSegment.toLine2D();
            g2d.draw(d);
        }
        for(Circle corner:corners){
            Ellipse2D d= corner.toEllipse2D();
            g2d.draw(d);
        }
        Polygon polygon = new Polygon(new int[] { x-r, x-r, x+r, x+r }, new int[] { y-r, y+r, y+r, y-r }, 4);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(degree), polygon.getBounds().getCenterX(),
                polygon.getBounds().getCenterY());
        Shape transformed = transform.createTransformedShape(polygon);
        g2d.fill(transformed);
    }
}

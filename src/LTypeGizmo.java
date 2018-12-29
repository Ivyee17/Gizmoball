import physics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class LTypeGizmo extends AbstractGizmo {

    public LTypeGizmo(AnimationWindow animationWindow) {
        super(animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.YELLOW;
        makeSquare();
    }
    public LTypeGizmo(int x, int y, int r, int degree, boolean crashMove, AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);
        if(isTrack()) color=Color.BLACK;
        else color=Color.YELLOW;
        makeSquare();
    }
    public void makeSquare(){
        lines.clear();
        corners.clear();
        while(degree>=360) degree-=360;
        Angle angle=findAngleByDegree(degree);
        Vect center=new Vect(new Point(x,y));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x,y-r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x,y-r,x,y),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x,y,x+r,y),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x+r,y,x+r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y+r,x+r,y+r),center,angle));
        lines.add(Geometry.rotateAround( new LineSegment(x-r,y-r,x-r,y+r),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y-r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x,y-r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x,y,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x+r,y,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x+r,y+r,0),center,angle));
        corners.add(Geometry.rotateAround( new Circle(x-r,y+r,0),center,angle));
    }


    @Override
    public Rectangle[] boundingBoxes() {
        Rectangle[] rectangles=new Rectangle[2];
        rectangles[0]=new Rectangle(x-r,y-r,r+animationWindow.getSizePerUnit()/2,r-animationWindow.getSizePerUnit()/2);
        rectangles[1]=new Rectangle(x-r,y-animationWindow.getSizePerUnit()/2,2*r,r+animationWindow.getSizePerUnit()/2);
        return rectangles;
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
        Polygon polygon = new Polygon(new int[] { x-r,x,x,x+r,x+r,x-r }, new int[] { y-r,y-r,y,y,y+r,y+r }, 6);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(degree), polygon.getBounds().getCenterX(),
                polygon.getBounds().getCenterY());
        Shape transformed = transform.createTransformedShape(polygon);
        g2d.fill(transformed);
    }
}

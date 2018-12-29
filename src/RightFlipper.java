import physics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class RightFlipper extends Flipper {
    public RightFlipper(AnimationWindow animationWindow) {
        super(animationWindow);
    }
    public RightFlipper(int x, int y, int r, int degree, boolean crashMove, AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);

    }

    public void makeFlipper() {
        lines.clear();
        corners.clear();
        while (degree >= 360) degree -= 360;
        Angle angle = findAngleByDegree(degree);
        Vect center = new Vect(new Point(x , y ));
        Angle rotatingAngle = new Angle(Math.toRadians(rotatingDegree));
        Vect rotatingCenter = new Vect(new Point(x + r-rollerR, y - r+rollerR));
        LineSegment line1=Geometry.rotateAround(new LineSegment(x + r - rollerR * 2, y - r + rollerR, x + r - rollerR * 2, y + r - rollerR), center, angle);
        lines.add(Geometry.rotateAround(line1, rotatingCenter, rotatingAngle));
        LineSegment line2=Geometry.rotateAround(new LineSegment(x + r, y - r + rollerR, x + r, y + r - rollerR), center, angle);
        lines.add(Geometry.rotateAround(line2, rotatingCenter, rotatingAngle));
        Circle c1=Geometry.rotateAround(new Circle(x + r - rollerR, y - r + rollerR, rollerR), center, angle);
        corners.add(Geometry.rotateAround(c1, rotatingCenter, rotatingAngle));
        Circle c2=Geometry.rotateAround(new Circle(x + r - rollerR, y + r - rollerR, rollerR), center, angle);
        corners.add(Geometry.rotateAround(c2, rotatingCenter, rotatingAngle));
    }



    @Override
    public void paint(Graphics g) {
        Rectangle clipRect = g.getClipBounds();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        makeFlipper();

        while (rotatingDegree > 90) rotatingDegree -= 90;
        Angle angle = new Angle(Math.toRadians(rotatingDegree));
        Vect center = new Vect(new Point(x + r-rollerR, y - r+rollerR));
        for(LineSegment lineSegment:lines) {
            Line2D.Double d = lineSegment.toLine2D();
            g2d.draw(d);
        }

        Polygon polygon = new Polygon(new int[]{x + r - rollerR * 2, x + r, x + r, x + r - rollerR * 2}, new int[]{y - r + rollerR, y - r + rollerR, y + r - rollerR, y + r - rollerR}, 4);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(degree), x,y);
        transform.rotate(Math.toRadians(rotatingDegree), x + r - rollerR,
                y - r + rollerR);
        Shape transformed = transform.createTransformedShape(polygon);
        g2d.fill(transformed);

        g2d.setColor(rollerColor);
        Ellipse2D e1 = corners.get(0).toEllipse2D();
        g2d.fill(e1);
        g2d.setColor(color);
        g2d.draw(e1);
        Ellipse2D e2 = corners.get(1).toEllipse2D();
        g2d.fill(e2);
    }

    public void flip(){
        flipMode=true;
        if(rotatingDegree>=90){
            rotatingDegree=90;
            return;
        }
        rotatingDegree+=5;
        return;
    }

    @Override
    public void unflip() {
        if(rotatingDegree<=0){
            rotatingDegree=0;
            flipMode=false;
            return;
        }
        rotatingDegree-=5;
        return;
    }
}

import physics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public abstract class Flipper extends AbstractGizmo {
    public int getRollerR() {
        return rollerR;
    }

    protected int rollerR=animationWindow.getSizePerUnit()/10;
    protected Color rollerColor=Color.BLACK;
    protected int rotatingDegree;
    protected boolean flipMode;
    public Flipper(AnimationWindow animationWindow) {
        super(animationWindow);
        isTrack=false;
        if(isTrack()) color=Color.BLACK;
        else color=Color.GREEN;
        makeFlipper();
        rotatingDegree=0;
        flipMode=false;
    }
    public Flipper(int x, int y, int r, int degree, boolean crashMove,  AnimationWindow animationWindow) {
        super(x,y,r,degree,crashMove,animationWindow);
        isTrack=false;
        if(isTrack()) color=Color.BLACK;
        else color=Color.LIGHT_GRAY;
        makeFlipper();
        rotatingDegree=0;
        flipMode=false;
    }

    public abstract void makeFlipper() ;



    @Override
    public abstract void paint(Graphics g);

    public abstract void flip();

    public abstract void unflip();
}

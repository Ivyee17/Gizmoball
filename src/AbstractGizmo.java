import physics.Angle;
import physics.Circle;
import physics.LineSegment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public abstract class AbstractGizmo {

    // x,y is the [CENTER] of the square, r is the radius (that is, from the center to the left border).
    protected int x;
    protected int y;
    protected int r;

    public boolean isTracker() {
        return isTrack;
    }

    public boolean isCrashMove() {
        return crashMove;
    }

    protected boolean isTrack = false;
    protected boolean crashMove = false;
    private int oldDegree;
    protected int degree;

    protected AnimationWindow animationWindow;
    protected List<LineSegment> lines;
    protected List<Circle> corners;
    protected Color color;

    public AbstractGizmo(AnimationWindow animationWindow) {
        this.isTrack = false;
        this.crashMove = false;
        this.degree = 0;
        this.animationWindow = animationWindow;
        this.r = animationWindow.getSizePerUnit() / 2;
        this.oldDegree = -1;
        this.lines = new ArrayList<>();
        this.corners = new ArrayList<>();
        this.isTrack = animationWindow.isTrackMode();
        // random position, but no intersect with others.
        Rectangle th;
        while (true) {
            Random random = new Random();
            int x = (random.nextInt(animationWindow.getWindowUnitHeight()) + 1) * animationWindow.getSizePerUnit() - animationWindow.getSizePerUnit() / 2;
            int y = (random.nextInt(animationWindow.getWindowUnitWidth()) + 1) * animationWindow.getSizePerUnit() - animationWindow.getSizePerUnit() / 2;

            // 与其它有交集，删除
            th = new Rectangle(x - r, y - r, r + r, r + r);
            if (!animationWindow.hasCoincidenceWithOthers(th)) break;

        }
        this.x = (int) (th.getX() + (th.getWidth() / 2));
        this.y = (int) (th.getY() + (th.getHeight() / 2));
    }

    public AbstractGizmo(int x, int y, int r, int degree, boolean crashMove, AnimationWindow animationWindow) {
        this.x = x;
        this.y = y;
        this.isTrack = false;
        this.crashMove = crashMove;
        this.degree = degree;
        this.animationWindow = animationWindow;
        this.r = r;
        this.oldDegree = -1;
        this.lines = new ArrayList<>();
        this.corners = new ArrayList<>();
        this.isTrack = animationWindow.isTrackMode();
    }

    public abstract void paint(Graphics g);

    public Rectangle[] boundingBoxes() {
        return new Rectangle[]{new Rectangle(x - r, y - r, 2 * r, 2 * r)};
    }

    public void setIsTrack(boolean isTrack) {
        this.isTrack = isTrack;
    }

    public void rotate(int degree) {
        oldDegree = this.degree;
        this.degree += degree;

    }

    public void makeLarger() {
        r += animationWindow.getSizePerUnit();
        if (x > animationWindow.getWidth() - r || x < r
                || y > animationWindow.getHeight() - r || y < r) {
            r -= animationWindow.getSizePerUnit();
        }
    }

    /**
     * @return true if it can make small, (When r<1, it cannot small!)
     */
    public boolean makeSmaller() {
        if (r - animationWindow.getSizePerUnit() <= 0) return false;
        r -= animationWindow.getSizePerUnit();
        return true;
    }

    public void crashMovable(boolean c) {
        crashMove = c;
    }

    public void buildMove(int x, int y) {
        this.x = x / AnimationWindow.getSizePerUnit() * AnimationWindow.getSizePerUnit() + AnimationWindow.getSizePerUnit() / 2;
        if (x >= animationWindow.getWidth() - r) {
            this.x = animationWindow.getWidth() - r;
        }
        if (x <= r) {
            this.x = r;
        }
        this.y = y / AnimationWindow.getSizePerUnit() * AnimationWindow.getSizePerUnit() + AnimationWindow.getSizePerUnit() / 2;
        if (y >= animationWindow.getHeight() - r) {
            this.y = animationWindow.getHeight() - r;
        }
        if (y <= r) {
            this.y = r;
        }
    }

    @Override
    public String toString() {
        return " x=" + x + " y=" + y + " r=" + r + " crashMove" + crashMove + " degree" + degree;
    }

    public Angle findAngleByDegree(int degree) {
        if (degree == 0) return Angle.ZERO;
        else if (degree == 45) return Angle.DEG_45;
        else if (degree == 90) return Angle.DEG_90;
        else if (degree == 135) return Angle.DEG_135;
        else if (degree == 180) return Angle.DEG_180;
        else if (degree == 225) return Angle.DEG_225;
        else if (degree == 270) return Angle.DEG_270;
        else if (degree == 315) return Angle.DEG_315;
        else {
            throw new IllegalArgumentException("degree hasn't been implement yet");
        }
    }

    boolean isTrack() {
        return isTrack;
    }

}

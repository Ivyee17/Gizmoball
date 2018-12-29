import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Overview: an AnimationWindow is an area on the screen in which a
 * bouncing ball animation occurs.  AnimationWindows have two modes:
 * on and off.  During the on mode the ball moves, during the off
 * mode the ball doesn't move.
 */

public class AnimationWindow extends JComponent {

    private static final long serialVersionUID = 3257281448464364082L;

    // Controls how often we redraw
    private static int FRAMES_PER_SECOND = 25;

    private static int SIZE_PER_UNIT = 20;
    private static int WINDOW_UNIT_HEIGHT = 20;
    private static int WINDOW_UNIT_WIDTH = 20;

    private RunningModeEventListener runningEventListener;
    private BuildingModeEventListener buildingEventListener;
    private BouncingBall ball;

    private List<AbstractGizmo> gizmoList;
    private List<Flipper> flipperList;
    private AbstractGizmo clickedGizmo;

    private Timer runningTimer;
    private boolean mode;
    private List<JButton> optionButton;

    private boolean trackMode;

    public boolean isTrackMode() {
        return trackMode;
    }

    /**
     * @effects initializes this to be in the off mode.
     */
    public AnimationWindow(List<JButton> optionButton) {
        super();
        ball = new BouncingBall(this);
        // this only initializes the timer, we actually start and stop the timer in the setMode() method
        runningEventListener = new RunningModeEventListener();
        buildingEventListener = new BuildingModeEventListener();
        // The first parameter is how often (in milliseconds) the timer should call us back.
        runningTimer = new Timer(1000 / FRAMES_PER_SECOND, runningEventListener);
        mode = true;
        setMode(false);
        gizmoList = new ArrayList<>();
        flipperList = new ArrayList<>();
        this.optionButton = optionButton;
        this.trackMode = false;
    }


    /**
     * Repaints the Graphics area g.  Swing will then send the newly painted g to the screen.
     *
     * @param g Graphics context received by either system or app calling repaint()
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 1; i < WINDOW_UNIT_HEIGHT; i++) {
            g.drawLine(0, i * SIZE_PER_UNIT, SIZE_PER_UNIT * WINDOW_UNIT_WIDTH, i * SIZE_PER_UNIT);
        }
        for (int i = 1; i < WINDOW_UNIT_WIDTH; i++) {
            g.drawLine(i * SIZE_PER_UNIT, 0, i * SIZE_PER_UNIT, SIZE_PER_UNIT * WINDOW_UNIT_WIDTH);
        }

        // Wall
        g.drawRect(0, 0, 1, SIZE_PER_UNIT * WINDOW_UNIT_HEIGHT);
        g.drawRect(0, 0, SIZE_PER_UNIT * WINDOW_UNIT_HEIGHT, 1);
        g.drawRect(SIZE_PER_UNIT * WINDOW_UNIT_WIDTH - 1, 0, 1, SIZE_PER_UNIT * WINDOW_UNIT_HEIGHT);
        g.drawRect(0, SIZE_PER_UNIT * WINDOW_UNIT_HEIGHT - 1, SIZE_PER_UNIT * WINDOW_UNIT_HEIGHT, 1);

        ball.paint(g);
        for (AbstractGizmo abstractGizmo : gizmoList) {
            abstractGizmo.paint(g);
        }
    }

    /**
     * This method is called when the Timer goes off and we
     * need to move and repaint the ball.
     *
     * @modifies both the ball and the window that this listener owns
     * @effects causes the ball to move and the window to be updated
     * to show the new position of the ball.
     */
    private void runningModeRepaint() {
        Rectangle oldPos = ball.boundingBox();
        ball.move(runningTimer.getDelay());
        CollisionInfo collisionInfo;
        if ((collisionInfo = ball.detectCollision(gizmoList)) != null) {
            if (collisionInfo.getGizmo().getClass() == AbsorberGizmo.class)
                ball.remove();
            else if (collisionInfo.getGizmo().isTracker() == true)
                ball.inTrack();
            else
                ball.dealCollision(collisionInfo);
        }
        Rectangle repaintArea = oldPos.union(ball.boundingBox());
        repaint(repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height);
    }

    public Rectangle acquireOldBoundingBox() {
        Rectangle repaintArea = new Rectangle();
        for (AbstractGizmo abstractGizmo : gizmoList) {
            for (Rectangle rectangle : abstractGizmo.boundingBoxes())
                repaintArea = repaintArea.union(rectangle);
        }
        return repaintArea;
    }

    public void buildingModeRepaint(Rectangle rectangle) {
        Rectangle repaintArea = rectangle;
        for (AbstractGizmo abstractGizmo : gizmoList) {
            for (Rectangle rectangle2 : abstractGizmo.boundingBoxes())
                repaintArea = repaintArea.union(rectangle2);
        }
        repaint(repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height);
    }

    /**
     * Turns the animation on/off. mode=true if running mode, mode=false if building mode.
     *
     * @param m Boolean indicating if animation is on/off
     */
    public void setMode(boolean m) {
        if (mode == m) {
            return;
        }
        if (mode) { // from animation on to off.
            removeMouseListener(runningEventListener);
            removeMouseMotionListener(runningEventListener);
            removeKeyListener(runningEventListener);
            addMouseListener(buildingEventListener);
            addMouseMotionListener(buildingEventListener);
            requestFocus(); // make sure keyboard is directed to us
            mode = false;
            runningTimer.stop();
        } else { // from animation off to on
            addMouseListener(runningEventListener);
            addMouseMotionListener(runningEventListener);
            addKeyListener(runningEventListener);
            removeMouseListener(buildingEventListener);
            removeMouseMotionListener(buildingEventListener);
            requestFocus(); // make sure keyboard is directed to us
            mode = true;
            runningTimer.start();
        }
    }

    public void addGizmo(Class t) {
        if (t.getSuperclass() != AbstractGizmo.class && t.getSuperclass().getSuperclass() != AbstractGizmo.class)
            throw new IllegalArgumentException("not subclass of AbstractGizmo");
        System.out.println(summarizeGizmoSize());
        if (summarizeGizmoSize() == WINDOW_UNIT_HEIGHT * WINDOW_UNIT_WIDTH) {
            JOptionPane.showMessageDialog(this, "cannot add, now is crowded with gizmos");
            return;
        }
        Rectangle rectangle = acquireOldBoundingBox();
        try {
            Constructor c = t.getDeclaredConstructor(new Class[]{AnimationWindow.class});
            AbstractGizmo abstractGizmo = (AbstractGizmo) c.newInstance(new Object[]{this});
            gizmoList.add(abstractGizmo);
            buildingModeRepaint(rectangle);
            clickedGizmo = abstractGizmo;

            if (isTrackMode() == true)
                clickedGizmo.setIsTrack(true);
            else
                clickedGizmo.setIsTrack(false);

            if (t == LeftFlipper.class || t == RightFlipper.class)
                flipperList.add((Flipper) abstractGizmo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addGizmo(int x, int y, int r, int degree, boolean crashMove, Class t) {
        if (t.getSuperclass() != AbstractGizmo.class && t.getSuperclass().getSuperclass() != AbstractGizmo.class) {
            System.out.println(t.getSuperclass());
            throw new IllegalArgumentException("not subclass of AbstractGizmo");
        }
        Rectangle rectangle = acquireOldBoundingBox();
        try {
            //int x, int y, int r, int degree, boolean crashMove,  AnimationWindow animationWindow
            Constructor c = t.getDeclaredConstructor(new Class[]{int.class, int.class, int.class, int.class, boolean.class, AnimationWindow.class});
            AbstractGizmo abstractGizmo = (AbstractGizmo) c.newInstance(new Object[]{x, y, r, degree, crashMove, this});
            gizmoList.add(abstractGizmo);
            buildingModeRepaint(rectangle);
            clickedGizmo = abstractGizmo;

            if (isTrackMode() == true)
                clickedGizmo.setIsTrack(true);
            else
                clickedGizmo.setIsTrack(false);

            if (t == LeftFlipper.class || t == RightFlipper.class)
                flipperList.add((Flipper) abstractGizmo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete() {
        Rectangle rectangle = acquireOldBoundingBox();
        gizmoList.remove(clickedGizmo);
        clickedGizmo = null;
        for (JButton b : optionButton) {
            b.setEnabled(false);
        }
        buildingModeRepaint(rectangle);
    }

    public void rotate() {
        clickedGizmo.rotate(90);
        repaint();
    }

    public void makeLarger() {
        Rectangle rectangle = acquireOldBoundingBox();
        clickedGizmo.makeLarger();
        if (hasCoincidenceWithOthers(clickedGizmo)) {
            clickedGizmo.makeSmaller();
            JOptionPane.showMessageDialog(this, "has coincide with other object, so cannot make larger.");
        }
        repaint();
    }

    public void makeSmaller() {
        Rectangle rectangle = acquireOldBoundingBox();
        boolean canSmall = clickedGizmo.makeSmaller();
        if (!canSmall) JOptionPane.showMessageDialog(this, "Now is the smallest. Cannot be smaller.");
        repaint();
    }

    public void movable(ActionEvent event) {
        Rectangle rectangle = acquireOldBoundingBox();
        JButton button = (JButton) event.getSource();
        if (button.getText() == "Set movable") {
            clickedGizmo.crashMovable(true);
            button.setText("Set unmovable");
        } else {
            clickedGizmo.crashMovable(false);
            button.setText("Set movable");
        }
        buildingModeRepaint(rectangle);

    }

    public static int getSizePerUnit() {
        return SIZE_PER_UNIT;
    }

    public static int getWindowUnitHeight() {
        return WINDOW_UNIT_HEIGHT;
    }

    public static int getWindowUnitWidth() {
        return WINDOW_UNIT_WIDTH;
    }

    public boolean hasCoincidenceWithOthers(Rectangle th) {
        boolean has = false;
        for (AbstractGizmo a : gizmoList) {
            for (Rectangle rect : a.boundingBoxes()) {
                Rectangle intersectSet = rect.intersection(th);
                if (intersectSet.getWidth() > 0 && intersectSet.getHeight() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int summarizeGizmoSize() {
        int sizeContain = 0;
        for (AbstractGizmo a : gizmoList) {
            Rectangle[] rects = a.boundingBoxes();
            for (Rectangle rect : rects) {
                sizeContain += (rect.getHeight() / SIZE_PER_UNIT) * (rect.getWidth() / SIZE_PER_UNIT);
            }
        }

        return sizeContain;
    }

    public boolean hasCoincidenceWithOthers(AbstractGizmo gizmo) {
        for (AbstractGizmo a : gizmoList) {
            if (a == gizmo) continue;
            for (Rectangle arect : a.boundingBoxes()) {
                for (Rectangle rectangle : gizmo.boundingBoxes()) {
                    Rectangle intersectSet = arect.intersection(rectangle);
                    if (intersectSet.getWidth() > 1 && intersectSet.getHeight() > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void removeAll() {
        super.removeAll();
        gizmoList.clear();
    }

    public List<AbstractGizmo> getGizmoList() {
        return gizmoList;
    }

    public void resetBall() {
        ball.resetPosition();
        repaint();
    }

    public void setTrack(ActionEvent event) {

        JButton button = (JButton) event.getSource();
        if (button.getText() == "Set track") {
            //clickedGizmo.crashMovable(true);
            button.setText("Set normal");
            trackMode = true;
        } else {
            //clickedGizmo.crashMovable(false);
            button.setText("Set track");
            trackMode = false;
        }

    }


    /**
     * Overview: RunningModeEventListener, deal with all events here!
     */
    class RunningModeEventListener extends MouseAdapter implements
            KeyListener, ActionListener {

        // Need implement: mouseClicked, mouseEntered, mouseExited, mousePressed, and mouseReleased.

        @Override
        public void keyPressed(KeyEvent e) {
            int keynum = e.getKeyCode();
            if (keynum == KeyEvent.VK_F) { // press A-J
                for (int i = 0; i < flipperList.size(); i++) {
                    flipperList.get(i).flip();
                }
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keynum = e.getKeyCode();
            if (keynum == KeyEvent.VK_F) { // press A-J
                for (int i = 0; i < flipperList.size(); i++) {
                    Flipper f = flipperList.get(i);
                    while(f.rotatingDegree!=0) f.unflip();
                }
            }
            repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        /**
         * Callback for the timer
         *
         * @param e ActionEvent generated by timer
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            runningModeRepaint();
        }
    }

    /**
     * Overview: BuildingModeEventListener, deal with all events here!
     */
    class BuildingModeEventListener extends MouseAdapter implements
            MouseMotionListener {

        // Need implement: mouseClicked, mouseEntered, mouseExited, mousePressed, and mouseReleased.

        @Override
        public void mousePressed(MouseEvent e) {

            clickedGizmo = null;
            for (JButton b : optionButton) {
                b.setEnabled(false);
            }

            for (AbstractGizmo abstractGizmo : gizmoList) {
                Rectangle[] rects = abstractGizmo.boundingBoxes();
                for (Rectangle rect : rects) {
                    if (e.getX() >= rect.x && e.getX() <= rect.x + rect.getWidth()
                            && e.getY() >= rect.y && e.getY() <= rect.y + rect.getHeight()) {
                        clickedGizmo = abstractGizmo;
                        for (JButton b : optionButton) {
                            b.setEnabled(true);
                        }
                        if (clickedGizmo.crashMove) optionButton.get(optionButton.size() - 1).setText("Set unmovable");
                        else optionButton.get(optionButton.size() - 1).setText("Set movable");
                    }
                }
            }

        }

        @Override
        public synchronized void mouseDragged(MouseEvent e) {
            if (clickedGizmo != null) {
                Rectangle[] olds = clickedGizmo.boundingBoxes();
                clickedGizmo.buildMove(e.getX(), e.getY());
                for (AbstractGizmo abstractGizmo : gizmoList) {
                    if (abstractGizmo != clickedGizmo && hasCoincidenceWithOthers(abstractGizmo) == true)
                        clickedGizmo.buildMove((int) olds[0].x, (int) olds[0].y);
                }
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (clickedGizmo != null) {
                Rectangle[] olds = clickedGizmo.boundingBoxes();
                clickedGizmo.buildMove(e.getX(), e.getY());
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }


    }


}
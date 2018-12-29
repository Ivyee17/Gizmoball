import javafx.geometry.Orientation;
import physics.Circle;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Overview: An ApplicationWindow is a top level program window that
 * contains a toolbar and an animation window.
 */
public class ApplicationWindow extends JFrame {

    private static final long serialVersionUID = 3257563992905298229L;

    protected AnimationWindow animationWindow;
    private JToolBar reviseObjectToolBar;
    private JToolBar controlObjectToolBar;
    private JScrollPane scrollPane;
    private JPanel contentPane;
    List<JButton> optionButton;
    FileSolver fileSolver;

    public ApplicationWindow() {
        // Title bar
        super("Swing Demonstration Program");
        // respond to the window system asking us to quit
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setResizable(false);
        // Create the toolbar.
        reviseObjectToolBar = new JToolBar();
        addReviseButtons();
        controlObjectToolBar = new JToolBar();
        optionButton = addControlButtons();

        animationWindow = new AnimationWindow(optionButton);
        animationWindow.setSize(400, 400);

        // Put it in a scrollPane.
        scrollPane = new JScrollPane(animationWindow);
        fileSolver = new FileSolver(animationWindow);

        // Lay out the content pane.
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        controlObjectToolBar.setPreferredSize(new Dimension(100, 400));
        controlObjectToolBar.setFloatable(false);
        controlObjectToolBar.setOrientation(SwingConstants.VERTICAL);
        contentPane.add(controlObjectToolBar, BorderLayout.EAST);

        reviseObjectToolBar.setPreferredSize(new Dimension(100, 400));
        reviseObjectToolBar.setFloatable(false);
        reviseObjectToolBar.setOrientation(SwingConstants.VERTICAL);
        contentPane.add(reviseObjectToolBar, BorderLayout.CENTER);

        scrollPane.setPreferredSize(new Dimension(403, 403));
        contentPane.add(scrollPane, BorderLayout.WEST);
        setContentPane(contentPane);

    }


    protected void addReviseButtons() {
        addButton("New", "New a board", event -> fileSolver.removeAll(), reviseObjectToolBar);
        addButton("Save", "Save this board", event -> fileSolver.save(), reviseObjectToolBar);
        addButton("Read", "Read a board from file", event -> fileSolver.read(), reviseObjectToolBar);
        reviseObjectToolBar.addSeparator();
        addButton("Square", "Add square gizmo", event -> animationWindow.addGizmo(SquareGizmo.class), reviseObjectToolBar);
        addButton("Circle", "Add circle gizmo", event -> animationWindow.addGizmo(CircleGizmo.class), reviseObjectToolBar);
        addButton("Triangle", "Add triangle gizmo", event -> animationWindow.addGizmo(TriangleGizmo.class), reviseObjectToolBar);
        addButton("Trapezoid", "Add trapezoid gizmo", event -> animationWindow.addGizmo(TrapezoidGizmo.class), reviseObjectToolBar);
        addButton("LeftFlipper", "Add left flipper gizmo", event -> animationWindow.addGizmo(LeftFlipper.class), reviseObjectToolBar);
        addButton("RightFlipper", "Add right flipper gizmo", event -> animationWindow.addGizmo(RightFlipper.class), reviseObjectToolBar);
        addButton("Absorber", "Add absorber gizmo", event -> animationWindow.addGizmo(AbsorberGizmo.class), reviseObjectToolBar);
        addButton("L-Type", "Add L-type gizmo", event -> animationWindow.addGizmo(LTypeGizmo.class), reviseObjectToolBar);
        reviseObjectToolBar.addSeparator();
        addButton("Set track", "Add track", event -> animationWindow.setTrack(event), reviseObjectToolBar);
    }

    /**
     * Add buttons to board by using {@link #addButton(String, String, ActionListener, JToolBar)}.
     * Never try to revise {@link #addButton(String, String, ActionListener, JToolBar)} function.
     */
    protected List<JButton> addControlButtons() {
        List<JButton> list = new ArrayList<>();
        addButton("Run", "Start the animation", event -> animationWindow.setMode(true), controlObjectToolBar);
        addButton("Back to start", "Put ball at start position", event -> animationWindow.resetBall(), controlObjectToolBar);
        addButton("Stop", "Stop the animation", event -> animationWindow.setMode(false), controlObjectToolBar);
        addButton("Quit", "Quit the program", event -> System.exit(0), controlObjectToolBar);
        controlObjectToolBar.addSeparator();
        JButton deleteButton = addButton("Delete", "Delete this gizmo", event -> animationWindow.delete(), false, controlObjectToolBar);
        list.add(deleteButton);
        JButton rotateButton = addButton("Rotate", "Rotate 90 degree clockwise", event -> animationWindow.rotate(), false, controlObjectToolBar);
        list.add(rotateButton);
        JButton largerButton = addButton("Resize +", "Double the size", event -> animationWindow.makeLarger(), false, controlObjectToolBar);
        list.add(largerButton);
        JButton smallerButton = addButton("Resize -", "Minimize the size into half", event -> animationWindow.makeSmaller(), false, controlObjectToolBar);
        list.add(smallerButton);
        JButton movableOptionButton = addButton("Set movable", "Set whether this gizmo can move when it's collide", event -> animationWindow.movable(event), false, controlObjectToolBar);
        list.add(movableOptionButton);
        return list;
    }

    /**
     * Do not revise, it's the function to add a button.
     *
     * @param explicitText   the name explicit shown on the board
     * @param implicitText   the name implicit shown on the board (when the mouse touch the button, it'll show.
     * @param actionListener the actions if clicking the button.
     */
    private JButton addButton(String explicitText, String implicitText, ActionListener actionListener, boolean enabled, javax.swing.JToolBar toolbar) {
        JButton button;
        button = new JButton(explicitText);
        button.setToolTipText(implicitText);
        button.setEnabled(enabled);
        // when this button is pushed it calls animationWindow.setMode(true)
        button.addActionListener(actionListener);
        toolbar.add(button);
        return button;
    }

    private JButton addButton(String explicitText, String implicitText, ActionListener actionListener, javax.swing.JToolBar toolbar) {
        return addButton(explicitText, implicitText, actionListener, true, toolbar);
    }


}
import physics.Circle;
import physics.LineSegment;

public class CollisionInfo {
    public AbstractGizmo getGizmo() {
        return gizmo;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public Circle getCircle(){
        return circle;
    }

    private AbstractGizmo gizmo;
    private LineSegment lineSegment;
    private Circle circle;
    public CollisionInfo(AbstractGizmo gizmo, LineSegment lineSegment) {
        this.gizmo=gizmo;
        this.lineSegment=lineSegment;
    }

    public CollisionInfo(AbstractGizmo gizmo, Circle circle){
        this.gizmo=gizmo;
        this.circle=circle;
    }
}

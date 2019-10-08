package Code.View.components.MindMap;
import Code.View.ObservableObject;
import javafx.scene.shape.Line;


public interface MindMapFactory<T extends ObservableObject> {
    Line call(Line shape, T item);
    MindMapCell<T> call(T item);
}

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public interface GraphicsRegion
{
    public void resize(int cSize);

    public void toggleErase();

    public void toggleColors();

    public void reset(int size, int fillPercent);

    public void update();

    public Node getNode();

    public void draw();
}

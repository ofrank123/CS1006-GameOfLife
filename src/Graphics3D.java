import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class Graphics3D implements GraphicsRegion {
    SubScene ss;
    Camera camera;
    int cSize;
    int size = 50;
    int centerX, centerY, centerZ;

    private final int voxelSize = 8;
    private Point2D lastMousePos;
    private Box[][][] voxels;

    final Group group;

    public Graphics3D(int cSize, int fillPercent) {
        // Init variables
        this.cSize = cSize;
        centerX = cSize / 2;
        centerY = cSize / 2;
        centerZ = 0;
        voxels = new Box[size][size][size];
        group = new Group();

        // Setup Logic
        GameLogic3D.setSingleton(new GameLogic3D(fillPercent));

        // Setup camera
        camera = new PerspectiveCamera();
        camera.getTransforms().addAll(new Rotate(45, centerX, centerY, centerZ, Rotate.Y_AXIS));
        camera.getTransforms().addAll(new Rotate(45, centerX, centerY, centerZ, Rotate.X_AXIS));

        // Setup subscene
        ss = new SubScene(group, cSize, cSize, true, SceneAntialiasing.BALANCED);
        ss.setFill(Color.BLACK);
        ss.setCamera(camera);

        addMouseHandlers();
        draw();
    }

    public void addMouseHandlers() {
        ss.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
                int x = (int) (e.getX() - lastMousePos.getX());
                int y = -1 * (int) (e.getY() - lastMousePos.getY());

                camera.getTransforms().addAll(new Rotate(x, centerX, centerY, centerZ, Rotate.Y_AXIS),
                                              new Rotate(y, centerX, centerY, centerZ, Rotate.X_AXIS));

                lastMousePos = new Point2D(e.getX(), e.getY());
            });

        ss.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                lastMousePos = new Point2D(e.getX(), e.getY());
            });
    }

    public Node getNode() {
        return ss;
    }

    public void update() {
        Platform.runLater(new Runnable() {
                public void run() {
                    GameLogic3D.getSingleton().advanceBoardState();
                    draw();
                }
            });
    }

    private void setVoxel(int x, int y, int z, Box voxel) {
        voxels[z][y][x] = voxel;
    }

    private Box getVoxel(int x, int y, int z) {
        return voxels[z][y][x];
    }

    public void reset(int size, int fillPercent) {
        this.size = size;
        voxels = new Box[size][size][size];
        GameLogic3D.setSingleton(new GameLogic3D(size, fillPercent));
        group.getChildren().clear();
        camera = new PerspectiveCamera();
        camera.getTransforms().addAll(new Rotate(45, centerX, centerY, centerZ, Rotate.Y_AXIS));
        camera.getTransforms().addAll(new Rotate(45, centerX, centerY, centerZ, Rotate.X_AXIS));

        ss.setFill(Color.BLACK);
        ss.setCamera(camera);

        addMouseHandlers();
        draw();
    }

    public void resize(int cSize) {
        // TODO
    }

    public void toggleErase() {
        // TODO
    }

    public void toggleColors() {
        // TODO
    }

    public void draw() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    int state = GameLogic3D.getSingleton().getState(x, y, z);
                    int prevState = GameLogic3D.getSingleton().getPrevState(x, y, z);
                    if (state != prevState) {
                        if (state == 0) {
                            group.getChildren().remove(getVoxel(x, y, z));
                            setVoxel(x, y, z, null);
                        } else {
                            if (prevState > 0) {
                                Box voxel = getVoxel(x, y, z);
                                voxel.setMaterial(new PhongMaterial(Rule.getSingleton().getGradient(state)));
                            } else {
                                group.getChildren().remove(getVoxel(x, y, z));
                                Box voxel = new Box(voxelSize, voxelSize, voxelSize);
                                voxel.setTranslateX(centerX + ((x - size / 2) * voxelSize));
                                voxel.setTranslateY(centerY + ((y - size / 2) * voxelSize));
                                voxel.setTranslateZ(centerZ + ((z - size / 2) * voxelSize));
                                voxel.setMaterial(new PhongMaterial(Rule.getSingleton().getGradient(state)));
                                group.getChildren().add(voxel);
                                setVoxel(x, y, z, voxel);
                            }
                        }
                    }
                }
            }
        }
    }
}

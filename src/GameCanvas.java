import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GameCanvas implements GraphicsRegion {
    // https://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/
    class ResizableCanvas extends Canvas {
        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }

        @Override
        public void resize(double w, double h) {
            super.setWidth(w);
            super.setHeight(h);
        }
    }

    ResizableCanvas canvas;
    GraphicsContext gc;
    int size;
    int cSize;
    int rectSize;
    int mouseX = 0;
    int mouseY = 0;
    boolean erase = false;
    boolean colors = false;

    final int MIN_RECT_SIZE = 8;
    final int MAX_GRADIENT_AGE = 10;

    public GameCanvas(int cSize, int fillPercent) {
        this.cSize = cSize;
        GameLogic2D.setSingleton(new GameLogic2D(fillPercent));
        this.size = GameLogic2D.getSingleton().getSize();
        canvas = new ResizableCanvas();

        gc = canvas.getGraphicsContext2D();
        addMouseListeners();
        resize(cSize);
    }

    public void resize(int cSize) {
        this.cSize = cSize;
        this.rectSize = cSize / size;
        canvas.resize((double) cSize, (double) cSize);
        draw();
    }

    public void toggleErase() {
        erase = !erase;
    }

    public void toggleColors() {
        colors = !colors;
    }

    public void reset(int size, int fillPercent) {
        GameLogic2D.setSingleton(new GameLogic2D(size, fillPercent));
        this.size = GameLogic2D.getSingleton().getSize();
        rectSize = cSize / size;
        draw();
    }

    public void update() {
        GameLogic2D.getSingleton().advanceBoardState();
        // https://stackoverflow.com/questions/21231557/javafx-thread-crashes
        Platform.runLater(()->{
                draw();
        });
    }

    private void updateMouse(int xCoord, int yCoord) {
        mouseX = positionToCoordinate(xCoord);
        mouseY = positionToCoordinate(yCoord);
    }

    private void paintPixels(int xCoord, int yCoord) {
        int x = positionToCoordinate(xCoord);
        int y = positionToCoordinate(yCoord);
        if (x >= 0 && y >= 0 && x < size && y < size) {
            if (erase) {
                GameLogic2D.getSingleton().getPos(x, y).setState(0);
            } else {
                GameLogic2D.getSingleton()
                    .getPos(x, y)
                    .setState(Rule.getSingleton().getMaxState());
            }
        }
    }

    public void addMouseListeners() {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
                updateMouse((int) e.getX(), (int) e.getY());
                draw();
            });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                paintPixels((int) e.getX(), (int) e.getY());
                draw();
            });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
                paintPixels((int) e.getX(), (int) e.getY());
                updateMouse((int) e.getX(), (int) e.getY());
                draw();
            });
    }

    private int positionToCoordinate(int pos) {
        return pos / rectSize;
    }

    public Node getNode() {
        return canvas;
    }

    private Color ageGradient(int age, int maxAge) {
        // Clamp
        age = Math.min(Math.max(age, 0), maxAge);
        double t = (double) age / (maxAge);
        int blue = (int) (t * 255);
        int green = (int) (255 - (t * 255));
        return Color.rgb(0, green, blue);
    }

    public void draw() {
        gc.setStroke(Color.GREY);
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, cSize, cSize);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int state = GameLogic2D.getSingleton().getState(x, y);
                if (x == mouseX && y == mouseY) {
                    gc.setFill(Color.DARKGRAY);
                    gc.fillRect(rectSize * x, rectSize * y, rectSize, rectSize);
                } else if (state != 0) {
                    if (colors) {
                        int age = GameLogic2D.getSingleton().getAge(x, y);
                        gc.setFill(ageGradient(age, MAX_GRADIENT_AGE));
                    } else {
                        gc.setFill(Rule.getSingleton().getGradient(state));
                    }
                    gc.fillRect(rectSize * x, rectSize * y, rectSize, rectSize);
                }
                if (rectSize > MIN_RECT_SIZE) {
                    gc.strokeRect(rectSize * x, rectSize * y, rectSize, rectSize);
                }
            }
        }
        if (rectSize <= MIN_RECT_SIZE) {
            gc.strokeRect(0, 0, rectSize * size, rectSize * size);
        }
    }
}

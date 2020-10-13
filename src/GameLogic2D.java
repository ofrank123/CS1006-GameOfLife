public class GameLogic2D {
    int size;
    Point[][] board;

    private static GameLogic2D singleton;

    public GameLogic2D(int fillPercent) {
        this(50, fillPercent);
    }

    public GameLogic2D(int size, int fillPercent) {
        this.size = size;
        board = new Point[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Point p = new Point();
                int rand = (int) Math.floor(Math.random() * 101);
                if (rand < fillPercent) {
                    p.setState(Rule.getSingleton().getMaxState());
                } else {
                    p.setState(0);
                }
                setPos(x, y, p);
            }
        }
    }

    public static GameLogic2D getSingleton() {
        if (singleton == null)
            singleton = new GameLogic2D(0);
        return singleton;
    }

    public static void setSingleton(GameLogic2D gl) {
        singleton = gl;
    }

    public Point getPos(int x, int y) {
        return board[y][x];
    }

    public void setPos(int x, int y, Point p) {
        board[y][x] = p;
    }

    public int getState(int x, int y) {
        return getPos(x, y).getState();
    }

    public void setState(int x, int y, int s) {
        getPos(x, y).setState(s);
    }

    public void setNeighbours(int x, int y, int n) {
        getPos(x, y).setNeighbours(n);
    }

    public int getNeighbours(int x, int y) {
        return getPos(x, y).getNeighbours();
    }

    public int getSize() {
        return size;
    }

    public int getAge(int x, int y) {
        return getPos(x, y).getAge();
    }

    private void setAge(int x, int y, int age) {
        getPos(x, y).setAge(age);
    }

    private void incrementAge(int x, int y) {
        getPos(x, y).incrementAge();
    }

    private void genAgeBoard(){
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++){
                if( getState(x, y) == 0 ) {
                    setAge(x, y, -1);
                } else {
                    incrementAge(x, y);
                }

            }
        }
    }

    public void advanceBoardState() {
        updateNeighbours();
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                Rule.getSingleton().nextState(getPos(x, y));

        genAgeBoard();
    }

    public void updateNeighbours() {
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                Rule.getSingleton().updateNeighbours(x, y, 0);
    }

}

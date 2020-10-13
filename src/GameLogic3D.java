public class GameLogic3D {
    int size;
    Point[][][] board;

    private static GameLogic3D singleton;

    public GameLogic3D(int fillPercent) {
        this(50, fillPercent);
    }

    public GameLogic3D(int size, int fillPercent) {
        this.size = size;
        board = new Point[size][size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    Point p = new Point();
                    int rand = (int) Math.floor(Math.random() * 101);
                    if (rand < fillPercent) {
                        p.setState(Rule.getSingleton().getMaxState());
                    } else {
                        p.setState(0);
                    }
                    setPos(x, y, z, p);
                }
            }
        }
    }

    public static GameLogic3D getSingleton() {
        if (singleton == null)
            singleton = new GameLogic3D(0);
        return singleton;
    }

    public static void setSingleton(GameLogic3D gl) {
        singleton = gl;
    }

    public Point getPos(int x, int y, int z) {
        return board[z][y][x];
    }

    public void setPos(int x, int y, int z, Point p) {
        board[z][y][x] = p;
    }

    public int getState(int x, int y, int z) {
        return getPos(x, y, z).getState();
    }

    public void setState(int x, int y, int z, int s) {
        getPos(x, y, z).setState(s);
    }

    public int getPrevState(int x, int y, int z) {
        return getPos(x, y, z).getPrevState();
    }

    public void setNeighbours(int x, int y, int z, int n) {
        getPos(x, y, z).setNeighbours(n);
    }

    public int getNeighbours(int x, int y, int z) {
        return getPos(x, y, z).getNeighbours();
    }

    public int getSize() {
        return size;
    }

    public int getAge(int x, int y, int z) {
        return getPos(x, y, z).getAge();
    }

    private void setAge(int x, int y, int z, int age) {
        getPos(x, y, z).setAge(age);
    }

    private void incrementAge(int x, int y, int z) {
        getPos(x, y, z).incrementAge();
    }

    private void genAgeBoard(){
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if( getState(x, y, z) == 0 ) {
                        setAge(x, y, z, -1);
                    } else {
                        incrementAge(x, y, z);
                    }
                }
            }
        }
    }

    public void advanceBoardState() {
        updateNeighbours();
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                for (int z = 0; z < size; z++)
                    Rule.getSingleton().nextState(getPos(x, y, z));

        genAgeBoard();
    }

    public void updateNeighbours() {
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                for (int z = 0; z < size; z++)
                    Rule.getSingleton().updateNeighbours(x, y, z);
    }

}

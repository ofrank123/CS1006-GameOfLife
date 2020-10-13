import java.util.Set;

public class Rule2D extends Rule {
    public Rule2D(String str, Set<Integer> survive, Set<Integer> born, int states, boolean vn) {
        super(str, survive, born, states, vn);
    }

    public void updateNeighbours(int x, int y, int z) {

        int neighbours = 0;
        Point p = GameLogic2D.getSingleton().getPos(x, y);
        int[][] combinations;
        if (vn) {
            combinations = new int[][] { { 1, 0 },
                                         { -1, 0 },
                                         { 0, 1 },
                                         { 0, -1 } };
        }
        else {
            combinations = new int[][] { { 1, 0 },
                                         { -1, 0 },
                                         { 1, 1 },
                                         { 0, 1 },
                                         { -1, 1 },
                                         { 1, -1 },
                                         { 0, -1 },
                                         { -1, -1 } };
        }

        for( int[] pair : combinations ){
            int n_x = x + pair[0];
            int n_y = y + pair[1];
            int size = GameLogic2D.getSingleton().getSize();

            if (n_x < 0)
                n_x = size - 1;

            if (n_y < 0)
                n_y = size - 1;

            if (n_x >= size)
                n_x = 0;

            if (n_y >= size)
                n_y = 0;

            if(GameLogic2D.getSingleton().getPos(n_x, n_y).getState() == getMaxState())
                neighbours++;

        }
        p.setNeighbours(neighbours);
    }
}

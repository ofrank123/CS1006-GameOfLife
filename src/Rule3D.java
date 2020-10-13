import java.util.Set;

public class Rule3D extends Rule {
    public Rule3D(String str, Set<Integer> survive, Set<Integer> born, int states, boolean vn) {
        super(str, survive, born, states, vn);
    }

    public void updateNeighbours(int x, int y, int z) {

        int neighbours = 0;
        Point p = GameLogic3D.getSingleton().getPos(x, y, z);
        int[][] combinations;

        if (vn) {
            combinations = new int[][] {
                { 1, 0, 0 },
                { -1, 0, 0 },
                { 0, 1, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 },
                { 0, 0, -1}};
        }
        else {
            combinations = new int[26][];
            int c = 0;
            for (int xO = -1; xO <= 1; xO++) {
                for (int yO = -1; yO <= 1; yO++) {
                    for (int zO = -1; zO <= 1; zO++) {
                        //System.out.println(c + ": " + xO + ", " + yO + ", " + zO);
                        if (!(xO == 0 && yO == 0 && zO == 0)) {
                            combinations[c] = new int[] { xO, yO, zO };
                            c++;
                        }
                    }
                }
            }
        }

        for (int[] pair : combinations) {
            int n_x = x + pair[0];
            int n_y = y + pair[1];
            int n_z = z + pair[2];
            int size = GameLogic3D.getSingleton().getSize();

            if (n_x < 0)
                n_x = size - 1;

            if (n_y < 0)
                n_y = size - 1;

            if (n_z < 0)
                n_z = size - 1;

            if (n_x >= size)
                n_x = 0;

            if (n_y >= size)
                n_y = 0;

            if (n_z >= size)
                n_z = 0;

            if (GameLogic3D.getSingleton().getPos(n_x, n_y, n_z).getState() == getMaxState())
                neighbours++;

        }
        p.setNeighbours(neighbours);
    }
}

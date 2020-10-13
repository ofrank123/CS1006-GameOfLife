import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.paint.Color;

public abstract class Rule {
    Set<Integer> survive;
    Set<Integer> born;
    int states;
    boolean vn;
    String ruleString;

    private static Rule singleton;

    public Rule(String str, Set<Integer> survive, Set<Integer> born, int states, boolean vn) {
        this.ruleString = str;
        this.survive = survive;
        this.born = born;
        this.states = states;
        this.vn = vn;
    }

    public static Rule getSingleton() {
        if (singleton == null)
            singleton = new Rule2D("2,3/3/2/M", Set.of(2, 3), Set.of(3), 2, false);
        return singleton;
    }

    public static void setSingleton(Rule rule) {
        singleton = rule;
    }

    public String getRuleString() {
        return ruleString;
    }
    // Simple warm gradient from yellow to red
    public Color getGradient(int state) {
        double t = (double) (state - 1) / (getMaxState() - 1);
        int green = (int) (t * 255);
        return Color.rgb(255, green, 0);
    }

    public int getMaxState() {
        return states - 1;
    }

    public boolean survives(int n) {
        return survive.contains(n);
    }

    public boolean isBorn(int n) {
        return born.contains(n);
    }

    public void nextState(Point p) {
        int n = p.getNeighbours();
        int s = p.getState();
        if (s == getMaxState() && !survives(n)) {
            p.decrementState();
        }
        else if (s == 0 && isBorn(n)) {
            p.setState(getMaxState());
        }
        else if (s != getMaxState() && s != 0){
            p.decrementState();
        }
    }

    public abstract void updateNeighbours(int x, int y, int z);

    @Override
    public String toString() {
        String ret;
        ret = "Survive: ";
        for (int i : survive) {
            ret += i + ", ";
        }
        ret = ret.substring(0, ret.length() - 2);
        ret += "\n";

        ret += "Born:   ";
        for (int i : born) {
            ret += i + ", ";
        }
        ret = ret.substring(0, ret.length() - 2);
        ret += "\n";

        ret += "States: " + states;
        ret += "\n";

        ret += "Neighborhood: ";
        if (vn) {
            ret += "Von Neuman";
        } else {
            ret += "Moore";
        }

        return ret;
    }

    public static Rule parseRuleString(String ruleString, boolean is3d) throws IllegalArgumentException {
        List<Set<Integer>> ruleConsts = new ArrayList<>();
        boolean vn;

        String[] rules = ruleString.split("/");
        if (rules.length != 4)
            throw new IllegalArgumentException();

        if (rules[3].equals("M")) {
            vn = false;
        }
        else if (rules[3].equals("V")) {
            vn = true;
        }
        else {
            throw new IllegalArgumentException();
        }

        int max_neighbours = 8;
        if(is3d) {
            if (vn)
                max_neighbours = 6;
            else
                max_neighbours = 26;
        } else {
            if (vn) max_neighbours = 4;
        }

        for (int i = 0; i < 2; i++) {
            String rule = rules[i];
            String[] ranges = rule.split(",");
            Set<Integer> ruleList = new HashSet<>();

            for (String r : ranges) {
                String[] rangeNums = r.split("-");
                if (rangeNums.length == 2) {
                    int start, end;
                    try {
                        start = Integer.parseInt(rangeNums[0]);
                        end = Integer.parseInt(rangeNums[1]);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    }
                    if (start >= end || start < 0 || start > max_neighbours - 1 || end < 0 || end > max_neighbours)
                        throw new IllegalArgumentException();
                    for (int j = start; j <= end; j++) {
                        ruleList.add(j);
                    }
                } else if (rangeNums.length == 1) {
                    // Ignore empty
                    if (!rangeNums[0].equals("")) {
                        int num;
                        try {
                            num = Integer.parseInt(rangeNums[0]);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException();
                        }
                        ruleList.add(num);
                    }
                }
            }
            ruleConsts.add(ruleList);
        }
        int states;
        try {
            states = Integer.parseInt(rules[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }

        if (is3d) {
            return new Rule3D(ruleString, ruleConsts.get(0), ruleConsts.get(1), states, vn);
        } else {
            return new Rule2D(ruleString, ruleConsts.get(0), ruleConsts.get(1), states, vn);
        }
    }
}

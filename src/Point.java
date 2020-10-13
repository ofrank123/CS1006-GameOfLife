public class Point {
    private int state;
    private int prevState;
    private int age;
    private int neighbours;


    public Point() {
        prevState = 0;
    }

    public int getState() {
        return state;
    }

    public int getAge() {
        return age;
    }

    public int getNeighbours() {
        return neighbours;
    }

    public void setState(int state) {
        this.prevState = this.state;
        this.state = state;
    }

    public int getPrevState() {
        return prevState;
    }

    public void decrementState() {
        prevState = state;
        state--;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void incrementAge() {
        age++;
    }

    public void setNeighbours(int neighbours) {
        this.neighbours = neighbours;
    }
}

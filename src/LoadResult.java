public class LoadResult
{
    private boolean in3d;
    private GraphicsRegion gr;
    private int size;

    public LoadResult(boolean in3d, int size, GraphicsRegion gr) {
        this.in3d = in3d;
        this.gr = gr;
        this.size = size;
    }

    public boolean getIn3d() {
        return in3d;
    }

    public GraphicsRegion getGR() {
        return gr;
    }

    public int getSize() {
        return size;
    }
}

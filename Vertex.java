import java.util.ArrayList;

public class Vertex {
    double x,y;
    ArrayList<Edge> edges = new ArrayList<>();
    double[] coordsArr;
    public Vertex(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a string representation of the Vertex
     * @return string representation of the Vertex
     */
    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    /**
     * Converts a list of vertices to array of integers conducive towards painting the Polygon
     * @param vertices vertices being converted
     * @return array of integer coordinates
     */
    public static int[][] verticesToInts(ArrayList<Vertex> vertices)
    {
        int[] xs = new int[vertices.size()];
        int[] ys = new int[vertices.size()];
        for (int i = 0; i < vertices.size();i++) {
            xs[i] = (int) (vertices.get(i).x);
            ys[i] = (int) (vertices.get(i).y);
        }
        return new int[][]{xs,ys};
    }
    /**
     * Converts an array of vertices to array of integers conducive towards painting the Polygon
     * @param vertices vertices being converted
     * @return array of integer coordinates
     */
    public static int[][] verticesToInts(Vertex[] vertices)
    {
        int[] xs = new int[vertices.length];
        int[] ys = new int[vertices.length];
        for (int i = 0; i < vertices.length;i++) {
            xs[i] = (int) (vertices[i].x);
            ys[i] = (int) (vertices[i].y);
        }
        return new int[][]{xs,ys};
    }
    /**
     * Returns coordinates of this vertex
     * @return coordinates of this vertex
     */
    public double[] getCoordsArr()
    {
        if (coordsArr==null)
            return coordsArr = new double[]{x,y};
        return coordsArr;
    }
    /**
     * Returns x coordinate of this vertex
     * @return x coordinate of this vertex
     */
    public double getX()
    {
        return x;
    }
    /**
     * Returns y coordinate of this vertex
     * @return y coordinate of this vertex
     */
    public double getY()
    {
        return y;
    }
}

import java.awt.*;
import java.util.*;
import java.util.List;

public class Face {
    ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<Vertex> vertices = new ArrayList<>();
    public Face(){}
    public Face(List<Vertex> vertexList)
    {
        List<Vertex> vertices = new ArrayList<>(vertexList);
        if (vertices.size()<1)
            return;
        Vertex lowest = vertices.get(0);
        int loc = 0;
        for (int i = 1; i < vertices.size();i++)
        {
            if (lowest.y > vertices.get(i).y)
            {
                lowest = vertices.get(i);
                loc= i;
            }
        }
        if (left(lowest.getCoordsArr(),vertices.get((loc-1+vertices.size())% vertices.size()).getCoordsArr(),vertices.get((loc+1)% vertices.size()).getCoordsArr()))
            Collections.reverse(vertices);
        for (int i = 0; i < vertices.size();i++)
        {
            this.vertices.add(vertices.get(i));
            this.edges.add(new Edge(vertices.get(i),vertices.get((i+1)% vertices.size()),this,null));
        }

    }
    public Face(Vertex[] vertices)
    {
        for (int i = 0; i < vertices.length;i++)
        {
            this.vertices.add(vertices[i]);
            this.edges.add(new Edge(vertices[i],vertices[(i+1)% vertices.length],this,null));
        }
    }
    public Face(ArrayList<Vertex> vertices, Set<Edge> edgeHashet)
    {
        this(vertices.toArray(new Vertex[0]),edgeHashet);
    }

    /**
     * Makes a face but uses existing edges where possible
     * @param vertices the vertices of the face
     * @param edgeHashSet set of existing edges
     */
    public Face(Vertex[] vertices, Set<Edge> edgeHashSet)
    {
        for (int i = 0; i < vertices.length;i++)
        {
            boolean exists = false;
            this.vertices.add(vertices[i]);
            for (Edge edge : edgeHashSet)
            {
                if ((edge.vertex_d == vertices[i]&&edge.vertex_o==vertices[(i+1)% vertices.length]))
                {
                    exists = true;
                    this.edges.add(edge);
                    edge.replaceFace(this,null);
                    break;
                }
                if ((edge.vertex_o == vertices[i]&&edge.vertex_d==vertices[(i+1)% vertices.length]))
                {
                    exists = true;
                    this.edges.add(edge);
                    edge.replaceFace(this,null);
                    break;
                }
            }
            if (!exists)
                this.edges.add(new Edge(vertices[i],vertices[(i+1)% vertices.length],this,null));
        }
    }

    /**
     * String representation of this Face
     * @return
     */
    @Override
    public String toString() {
        return "Face{" +
                "edges=" + edges +
                ", vertices=" + vertices +
                '}';
    }

    /**
     * Triangulates the given face using ear-clipping
     * @param face the face being triangulated
     * @return a list of triangles whose union is the face
     */
    public static ArrayList<Face> triangulate(Face face)
    {
        ArrayList<Face> triangles = new ArrayList<>();
        if (face.vertices.size()<3)
            return triangles;
        triangles.add(face);
        int i = 0;
        while (face.vertices.size()>3&&i<200)
        {
            Face triangle = face.clipEar(i);
            if (triangle == null) {
                i++;
            }else {
                triangles.add(triangle);
            }
        }
        return triangles;
    }

    /**
     * Clips an ear vertex from the face
     * @param v0 index of vertex in face being clipped
     * @return the triangle that just got clipped
     */
    public Face clipEar(int v0)
    {
        v0 %= vertices.size();
        if (!diagonal(vertices.get((v0-1+vertices.size())%vertices.size()),vertices.get((v0+1)%vertices.size())))
        {
            return null;
        }
        Face triangle = new Face();
        Edge newEdge = new Edge(vertices.get((v0-1+vertices.size())%vertices.size()),vertices.get((v0+1)%vertices.size()),this,triangle);
        triangle.vertices.add(vertices.get((v0-1+vertices.size())%vertices.size()));
        triangle.vertices.add(vertices.get(v0));
        triangle.vertices.add(vertices.get((v0+1)%vertices.size()));
        triangle.edges.add(edges.get((v0-1+vertices.size())%vertices.size()));
        edges.get((v0-1+vertices.size())%vertices.size()).replaceFace(triangle,this);
        triangle.edges.add(edges.get(v0));
        edges.get(v0).replaceFace(triangle,this);
        triangle.edges.add(newEdge);
        this.vertices.remove(v0);
        this.edges.remove(triangle.edges.get(0));
        this.edges.remove(triangle.edges.get(1));
        this.edges.add((v0-1+vertices.size())%vertices.size(),newEdge);
        return triangle;
    }

    /**
     * Determines if there is a diagonal between two points
     * @param v0 a vertex in the face
     * @param v1 another vertex in the face
     * @return true if they have a diagonal, false elsewise
     */
    public boolean diagonal(Vertex v0, Vertex v1)
    {
        return inCone(v0,v1)&&inCone(v1,v0)&&diagonalie(v0,v1);
    }

    /**
     * Determines if two vertices can see each other
     * @param v0 first vertex
     * @param v1 second vertex
     * @return true if no edges overlap with line between vertices, false otherwise.
     */
    public boolean diagonalie(Vertex v0, Vertex v1)
    {
        for (Edge edge : edges)
        {
            if (edge.contains(v0)||edge.contains(v1))
            {
                continue;
            }
            if (intersectsProp(edge.vertex_o.getCoordsArr(),edge.vertex_d.getCoordsArr(),v0.getCoordsArr(), v1.getCoordsArr()))
            {
                return false;
            }
        }
        return true;
    }
    /**
     * Determines whether a vertex is in the "open cone" of another vertex.
     * @param v0 vertex who's in the cone is analyzed
     * @param v1 vertex who may or may not be in the cone
     * @return true if vertex is in the cone, false otherwise
     */
    public boolean inCone(Vertex v0, Vertex v1)
    {
        Vertex next = vertices.get((vertices.indexOf(v0)+1)% vertices.size());
        Vertex prev = vertices.get((vertices.indexOf(v0)-1+vertices.size())% vertices.size());
        if (leftOn(v0.getCoordsArr(),next.getCoordsArr(),prev.getCoordsArr()))
        {
            return left(v0.getCoordsArr(),v1.getCoordsArr(),prev.getCoordsArr())&&left(v1.getCoordsArr(),v0.getCoordsArr(),next.getCoordsArr());
        }
        return !(leftOn(v0.getCoordsArr(),v1.getCoordsArr(),next.getCoordsArr())&&leftOn(v1.getCoordsArr(),v0.getCoordsArr(),prev.getCoordsArr()));
    }
    /**
     * Determines if two line segments intersect
     * @param a first endpoint of first line segment
     * @param b second endpoint of first line segment
     * @param c first endpoint of second line segment
     * @param d first endpoint of second line segment
     * @return true if they intersect, false otherwise
     */
    public static boolean intersectsProp(double[] a, double[] b, double[] c, double[] d)
    {
        if (collinear(a,b,c) && collinear(a,b,d))
        {
            return (between(a,b,c)||between(a,b,d));
        }
        if ((between(a,b,c)||between(a,b,d)||between(c,d,a)||between(c,d,b)))
        {
            return true;
        }
        if (collinear(a,b,c) || collinear(a,b,d) || collinear (c, d, a) || collinear(c,d,b))
        {
            return false;
        }
        return (left(a,b,c) != left(a,b,d)) && (left(c,d,a) != left(c,d,b));
    }
    /**
     * Determines if a point is between two other points (all collinear)
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point between two other points
     * @return true if c is between a and b, false elsewise.
     */
    public static boolean between(double[]a, double[] b, double[] c)
    {
        if (!collinear(a,b,c))
            return false;
        if (a[0] != b[0])
            return ((a[0] <= c[0]) && (c[0] <=b[0])) ||
                    ((a[0] >= c[0]) && (c[0] >= b[0]));
        return ((a[1] <= c[1]) && (c[1] <= b[1])) ||
                ((a[1] >= c[1]) && (c[1] >= b[1]));
    }
    /**
     * Determines if a point is to the left of two other points
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point that may or may not be to the left
     * @return true if c is to the left of a and b, false elsewise.
     */
    public static boolean left(double[]a, double[] b, double[] c) {
        return crossProduct(a,b,a,c) > 0;
    }
    /**
     * Determines if a point is to the left of or inline with two other points
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point that may or may not be to the left
     * @return true if c is to the left of or collinear with a and b, false elsewise.
     */
    public static boolean leftOn(double[]a, double[] b, double[] c) {
        return crossProduct(a,b,a,c) >= 0;
    }
    /**
     * Determines if a point is to the collinear with two other points
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point that may or may not be collinear.
     * @return true if c is to the left of a and b, false elsewise.
     */
    public static boolean collinear(double[]a, double[] b, double[] c) {
        return crossProduct(a,b,a,c) == 0;
    }
    /**
     * Determines signed area of the parallelogram with points a,b,c,d
     * @param a coordinates of point a
     * @param b coordinates of point b
     * @param c coordinates of point c
     * @param d coordinates of point d
     * @return signed area of the parallelogram with points a,b,c,d
     */
    public static double crossProduct(double[] a, double[] b, double[] c, double[]d)
    {
        return (b[0] - a[0]) * (d[1] - c[1]) - (b[1] - a[1]) * (d[0] - c[0]);
    }

    /**
     * Paints the face
     * @param g Graphics object used by JPanel
     */
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        for (Edge edge : edges) {
            g.drawLine((int) edge.vertex_o.x, (int) edge.vertex_o.y, (int) edge.vertex_d.x, (int) edge.vertex_d.y);
        }
        g.setColor(new Color(200,100,100));
        for (Vertex vertex: vertices)
        {
            g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
        }
    }

    /**
     * Paints the face and fills it with blue
     * @param g Graphics object used by JPanel
     */
    public void paintFill(Graphics g)
    {
        g.setColor(new Color(140, 180, 248));
        int[][] coords = Vertex.verticesToInts(this.vertices);
        g.fillPolygon(coords[0], coords[1], coords[0].length);
    }
}
/**
 *  This File is a part of the Point in Polygon Demostrator
 * 
 *  The purpose of this program is to demonstrate how the Kirkpatrick method
 *  for logarithmic time queries for whether a given point lies in the processed
 *  polygon.
 * 
    Copyright (C) 2021  Reilly Browne

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.awt.*;
import java.util.*;
import java.util.List;
/**
 * The purpose of this class is to represent a node in the DAG
 */
public class DAGNode {
    public static ArrayList<Vertex> vertexList;
    public static ArrayList<Vertex> verticesInPlay;
    String name;
    double[] center;
    Vertex[] vertices;
    Set<DAGNode> children;
    boolean inner;
    int displayX,displayY;
    public DAGNode(Face face, boolean verticesSetUp)
    {
        if (vertexList == null)
            vertexList = new ArrayList<>();
        vertices = new Vertex[3];
        for (int i = 0; i < 3; i++)
        {
            vertices[i] = face.vertices.get(i);
            if (!verticesSetUp&&!vertexList.contains(face.vertices.get(i)))
                vertexList.add(face.vertices.get(i));
        }
        Arrays.sort(vertices, Comparator.comparingInt(o -> vertexList.indexOf(o)));
        name = "";
        for (Vertex vertex : vertices) {
            name += (vertexList.indexOf(vertex) + 1) + ",";
        }
        name = name.substring(0,name.length()-1);
        children = new LinkedHashSet<>();
        displayX = displayY = 100;
        center = new double[]{(vertices[0].x+vertices[1].x+vertices[2].x)/3,(vertices[0].y+vertices[1].y+vertices[2].y)/3};
        Arrays.sort(vertices, (a, b) -> {
            if (a==b)
                return 0;
            double cr = cross(a.getX() - center[0], a.getY() - center[1], b.getX() - center[0], b.getY() - center[1]);
            if (cr < 0)
                return 1;
            else
                return -1;
        });
    }

    /**
     * Determines if a point is inside the polygon or not.
     * @param coords the coordinates of the point being queried
     * @return true if the point is inside the polygon, false otherwise.
     */
    public boolean insidePolygon(double[] coords)
    {
        if (!inside(coords)&&!onPerimeter(coords))
            return false;
        for (DAGNode node : children)
        {
            if (node.insidePolygon(coords))
                return true;
        }
        return inner;
    }

    /**
     * Returns the list of nodes used in the DAG to determine if the point is in the Path
     * @param coords the coordinates of the point being queried
     *@return list of nodes used in the DAG to determine if the point is in the Path
     */
    public ArrayList<DAGNode> pathThroughDAG(double[] coords)
    {
        if (!inside(coords))
            return new ArrayList<>();
        ArrayList<DAGNode> lst = new ArrayList<>();
        for (DAGNode node : children)
        {
            lst.addAll(node.pathThroughDAG(coords));
        }
        lst.add(this);
        return lst;
    }

    /**
     * Converts the KPPSLG into a list of DAGNodes
     * @param kirk the KPPSLG being converted
     * @return the list of DAGNodes
     */
    public static ArrayList<DAGNode> dagNodesFromKirk(KPPSLG kirk)
    {
        vertexList = new ArrayList<>(kirk.vertices);
        verticesInPlay = new ArrayList<>(vertexList);
        verticesInPlay.removeAll(Arrays.asList(kirk.triangle.clone()));
        ArrayList<DAGNode> dagNodes = new ArrayList<>();
        for (Face face : kirk.faces)
        {
            DAGNode dagNode = new DAGNode(face,true);
            if (kirk.innerFaces.contains(face))
                dagNode.inner = true;
            dagNodes.add(dagNode);
        }
        return dagNodes;
    }

    /**
     * Converts a list of Faces into a list of DAGNodes
     * @param faces a list of faces
     * @return a list of DAGNodes
     */
    public static ArrayList<DAGNode> dagNodesFromFaceList(List<Face> faces)
    {
        ArrayList<DAGNode> dagNodes = new ArrayList<>();
        for (Face face : faces)
        {
            DAGNode dagNode = new DAGNode(face,true);
            dagNodes.add(dagNode);
        }
        return dagNodes;
    }

    /**
     * Determines if the list parent DAGNodes have any children among the list of children DAGNodes
     * @param parents the list of potential parent nodes
     * @param children the list of potential child nodes
     */
    public static void inheritance(List<DAGNode> parents, List<DAGNode> children)
    {
        for (DAGNode parent : parents)
        {
            if (children.contains(parent))
                continue;
            for (DAGNode child: children)
            {
                if (parents.contains(child))
                    continue;
                if (parent.subtriangle(child)||overlap(parent,child))
                {
                    parent.children.add(child);
                }
            }
        }
    }
    /**
     * Determines if 2 DAGNodes' triangles overlap
     * @param node1 a node being considered
     * @param node2 another node being considered
     * @return true if the nodes overlap, false otherwise
     */
    public static boolean overlap(DAGNode node1, DAGNode node2) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; i < 3; i++) {
                if (Face.intersectsProp(node1.vertices[i].getCoordsArr(),node1.vertices[(i+1)%3].getCoordsArr(),node2.vertices[j].getCoordsArr(),node2.vertices[(j+1)%3].getCoordsArr()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether or not a point is in the DAGNode's triangle
     * @param coords the coordinates of the point
     * @return true if the point is inside the triangle, false otherwise
     */
    public boolean inside(double[] coords)
    {
        for (int i = 0; i < 3; i++)
        {
            if(!Face.left(vertices[i].getCoordsArr(),vertices[(i+1)%3].getCoordsArr(),coords))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if a point is along the perimeter of the DAGNode's triangle
     * @param coords the coordinates of the point
     * @return true if the point is inside the triangle, false otherwise
     */
    public boolean onPerimeter(double[] coords)
    {
        for (int i = 0; i < 3; i++)
        {
            if(Face.collinear(vertices[i].getCoordsArr(),vertices[(i+1)%3].getCoordsArr(),coords))
                return true;
        }
        return  false;
    }

    /**
     * Determines if any of the points of the given DAGNode's triangle are inside the current instance's
     * @param node the triangle being checked
     * @return returns true if a point is inside, false otherwise
     */
    public boolean subtriangle(DAGNode node)
    {
        for (Vertex vertex : node.vertices)
        {
            if (inside(vertex.getCoordsArr()))
                return true;
        }
        return  false;
    }

    /**
     * Gives the other points in the DAGNode's triangle given one of them.
     * @param vertex the vertex to not invlude
     * @return a list with 2 vertices
     */
    public List<Vertex> other(Vertex vertex)
    {
        List<Vertex> list = new ArrayList<>();
        for (Vertex vertex1 : vertices)
        {
            if (vertex1==vertex)
                continue;
            list.add(vertex1);
        }
        return list;
    }
    /**
     * Removes a vertex from the current triangulation and retriangulates.
     * @param dagNodeList the list of nodes in the current triangulation
     * @param removal the vertex being deleted
     * @return the list of nodes after deletion
     */
    public static ArrayList<DAGNode> remove(List<DAGNode> dagNodeList, Vertex removal)
    {
        ArrayList<DAGNode> dagNodes = new ArrayList<>();
        HashSet<Vertex> newFaceVertices = new LinkedHashSet<>();
        for (DAGNode node : dagNodeList)
        {
            boolean clear = true;
            for (Vertex vertex : node.vertices)
            {
                if (removal == vertex)
                {
                    newFaceVertices.addAll(node.other(vertex));
                    clear = false;
                    break;
                }
            }
            if (clear)
                dagNodes.add(node);
        }
        ArrayList<Vertex> newFaceV = new ArrayList<>(newFaceVertices);
        newFaceV.sort((a, b) -> {
            if (a == b)
                return 0;
            double cr = cross(a.getX() - removal.getX(), a.getY() - removal.getY(), b.getX() - removal.getX(), b.getY() - removal.getY());
            if (cr < 0)
                return 1;
            else
                return -1;
        });
        List<Face> faceList = Face.triangulate(new Face(newFaceV));
        dagNodes.addAll(dagNodesFromFaceList(faceList));
        return dagNodes;
    }

    /**
     * Determines if a vertex is one of the vertices of this node
     * @param vertex vertex being considered
     * @return true if vertex is a point of the triangle, false otherwise
     */
    public boolean contains(Vertex vertex)
    {
        return (vertices[0]==vertex)||(vertices[1]==vertex)||(vertices[2]==vertex);
    }

    /**
     * Cross product of 2 vectors
     * @param x1 x-coord of first point
     * @param y1 y-coord of first point
     * @param x2 x-coord of second point
     * @param y2 y-coord of second point
     * @return cross product
     */
    public static double cross(double x1, double y1, double x2, double y2) {
        return x1 * y2 - x2 * y1;
    }
    @Override
    public String toString() {
        return "DAGNode{" +
                "name='" + name +"}";
    }

    /**
     * Paints the triangle of this node
     * @param g Graphics object used by JPanel
     */
    public void paintTriangle(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.drawPolygon(new int[]{(int)vertices[0].x,(int)vertices[1].x,(int)vertices[2].x},new int[]{(int)vertices[0].y,(int)vertices[1].y,(int)vertices[2].y},3);
    }

    /**
     * Paints a filled triangle of this node
     * @param g Graphics object used by JPanel
     */
    public void paintFilledTriangle(Graphics g)
    {
        g.setColor(new Color(150,200,150));
        g.fillPolygon(new int[]{(int)vertices[0].x,(int)vertices[1].x,(int)vertices[2].x},new int[]{(int)vertices[0].y,(int)vertices[1].y,(int)vertices[2].y},3);
        paintTriangle(g);
    }

    /**
     * Paints a pictoral depiction of the node
     * @param g Graphics object used by JPanel
     */
    public void paintDisplay(Graphics g)
    {
        g.setColor(new Color(250,150,100));
        if (inner)
        {
            g.setColor(new Color(140, 180, 248));
        }
        g.fillOval(displayX-20,displayY-20,40,40);
        g.setColor(Color.BLACK);
        g.drawOval(displayX-20,displayY-20,40,40);
        g.drawString(name,displayX-18,displayY+5);
        g.setColor(new Color(100,200,100));
    }
}

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
/**
 * The purpose of this class is to translate the inputted polygon into a PSLG for the DAG to use
 */
public class KPPSLG {
    Face innerPoly;
    Set<Edge> edges;
    Set<Vertex> vertices;
    Set<Face> faces;
    ArrayList<Face> innerFaces;
    Set<Vertex> permaMarked;
    boolean triangulated = false;
    Vertex[] triangle;
    Face special = new Face(new Vertex[]{});
    public KPPSLG(Face face)
    {
        innerFaces = new ArrayList<>();
        faces = new LinkedHashSet<>();
        vertices = new LinkedHashSet<>();
        edges = new HashSet<>();
        this.innerPoly = face;
        triangle = outerTriangle(innerPoly);
        permaMarked = new HashSet<>();
        permaMarked.addAll(Arrays.asList(triangle));
        vertices.addAll(Arrays.asList(triangle));
        vertices.addAll(face.vertices);
        edges.addAll(innerPoly.edges);
    }
    /**
     * Determines if two vertices see each other
     * @param vertex1 a vertex
     * @param vertex2 another vertex
     * @return true if vertices see each other, false otherwise.
     */
    public boolean sees(Vertex vertex1, Vertex vertex2)
    {
        for (Edge edge : edges)
        {
            if (edge.contains(vertex1)||edge.contains(vertex2))
            {
                continue;
            }
            boolean intersect = Face.intersectsProp(vertex1.getCoordsArr(),vertex2.getCoordsArr(),edge.vertex_o.getCoordsArr(),edge.vertex_d.getCoordsArr()) ;
            if (intersect){
                return false;
            }
        }
        return true;
    }
    /**
     * Triangulates the whole KPPSLG (polygon and the outer triangle)
     * @return the set of faces in the triangulation
     */
    public Set<Face> triangulation(){
        if (triangulated)
        {
            return null;
        }
        triangulated = true;
        faces = new LinkedHashSet<>();
        edges.add(new Edge(triangle[0],triangle[1],null,null));//adding in triangle edges
        edges.add(new Edge(triangle[1],triangle[2],null,null));
        edges.add(new Edge(triangle[2],triangle[0],null,null));

        Vertex[] contactPoints = new Vertex[3];
        for (Vertex vertex : innerPoly.vertices)
        {
            if(sees(vertex,triangle[0]))
            {
                contactPoints[0] = vertex;
                edges.add(new Edge(triangle[0],vertex,null,null));
            }
            if (sees(vertex,triangle[1]))
            {
                contactPoints[1] = vertex;
                edges.add(new Edge(triangle[1],vertex,null,null));

            }
            if (sees(vertex,triangle[2]))
            {
                contactPoints[2] = vertex;
                edges.add(new Edge(triangle[2],vertex,null,null));
            }
        }
        ArrayList<Vertex> outerFace1 = new ArrayList<>();
        ArrayList<Vertex> outerFace2 = new ArrayList<>();
        ArrayList<Vertex> outerFace3 = new ArrayList<>();
        //setting up beginning of outerfaces with contact point
        outerFace1.add(contactPoints[0]);
        outerFace1.add(triangle[0]);
        outerFace1.add(triangle[1]);
        if (contactPoints[1]!=contactPoints[0])
        {
            outerFace1.add(contactPoints[1]);
            for (int i = (innerPoly.vertices.indexOf(contactPoints[1])-1+innerPoly.vertices.size())%innerPoly.vertices.size(); i!= innerPoly.vertices.indexOf(contactPoints[0]); i = (i-1+innerPoly.vertices.size())%innerPoly.vertices.size())
            {
                outerFace1.add(innerPoly.vertices.get(i));
            }
        }

        outerFace2.add(contactPoints[1]);
        outerFace2.add(triangle[1]);
        outerFace2.add(triangle[2]);
        if (contactPoints[1]!=contactPoints[2])
        {
            outerFace2.add(contactPoints[2]);
            for (int i = (innerPoly.vertices.indexOf(contactPoints[2])-1+innerPoly.vertices.size())%innerPoly.vertices.size(); i!= innerPoly.vertices.indexOf(contactPoints[1]); i = (i-1+innerPoly.vertices.size())%innerPoly.vertices.size())
            {
                outerFace2.add(innerPoly.vertices.get(i));
            }
        }

        outerFace3.add(contactPoints[2]);
        outerFace3.add(triangle[2]);
        outerFace3.add(triangle[0]);
        if (contactPoints[2]!=contactPoints[0])
        {
            outerFace3.add(contactPoints[0]);
            for (int i = (innerPoly.vertices.indexOf(contactPoints[0])-1+innerPoly.vertices.size())%innerPoly.vertices.size(); i!= innerPoly.vertices.indexOf(contactPoints[2]); i = (i-1+innerPoly.vertices.size())%innerPoly.vertices.size())
            {
                outerFace3.add(innerPoly.vertices.get(i));
            }
        }
        innerFaces.addAll(Face.triangulate(new Face(innerPoly.vertices,edges)));
        faces.addAll(innerFaces);
        faces.addAll(Face.triangulate(new Face(outerFace1,edges)));
        faces.addAll(Face.triangulate(new Face(outerFace2,edges)));
        faces.addAll(Face.triangulate(new Face(outerFace3,edges)));

        return faces;
    }

    /**
     * Paints the KPPSLG
     * @param g g Graphics object used by JPanel
     */
    public void paint(Graphics g)
    {

        g.setColor(new Color(100,200,100));
        int[][] coords = Vertex.verticesToInts(special.vertices);
        g.fillPolygon(coords[0],coords[1],coords[0].length);
        if (innerFaces.size()>0) {
            g.setColor(Color.BLACK);
            for(Face face: faces)
            {
                face.paint(g);
            }
            int i = 1;
            g.setColor(new Color(0,100,0));
            for (Vertex vertex : vertices)
            {
                g.drawString(""+i++, (int)vertex.x+10,(int)vertex.y);
            }
        }
        else
        {
            coords = Vertex.verticesToInts(innerPoly.vertices);
            g.fillPolygon(coords[0],coords[1],coords[0].length);
            g.setColor(Color.BLACK);
            coords = Vertex.verticesToInts(triangle);
            g.drawPolygon(coords[0],coords[1],coords[0].length);
            g.setColor(new Color(200,100,100));
            for (Vertex vertex : innerPoly.vertices)
            {
                g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
            }
            for (Vertex vertex : triangle)
            {
                g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
            }
        }
        g.setColor(new Color(200,100,200));
        for (Vertex vertex : permaMarked)
        {
            g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
        }
    }
    /**
     * Constructs the outer triangle for contructing the KPPSLG
     * @param face the inner polygon
     * @return the vertices of the outer triangle
     */
    public static Vertex[] outerTriangle(Face face)
    {
        Vertex[] vertices = face.vertices.toArray(new Vertex[0]);
        double yMin, yMax;
        Vertex yMaxV, yMinV;
        yMaxV = yMinV = new Vertex(0,0);
        yMin = Double.MAX_VALUE;
        yMax = Double.MIN_VALUE;
        for (Vertex vertex: vertices) {
            if (yMax < vertex.y) {
                yMax = vertex.y;
                yMaxV = vertex;
            }
            if (yMin > vertex.y) {
                yMin = vertex.y;
                yMinV = vertex;
            }
            if (yMin == vertex.y && yMinV.x > vertex.x)
            {
                yMinV = vertex;
            }
        }
        Vertex top = new Vertex(yMaxV.x,yMaxV.y+100);
        Arrays.sort(vertices, (o1, o2) -> {
            double doto1 = (o1.x-top.x)/Math.hypot(o1.x-top.x,o1.y-top.y);
            double doto2 = (o2.x-top.x)/Math.hypot(o2.x-top.x,o2.y-top.y);
            return (int)Math.signum(doto1-doto2);
        });
        double slopeL = (vertices[0].x-20-top.x)/(top.y-vertices[0].y);
        double slopeR = (vertices[vertices.length-1].x+20-top.x)/(top.y-vertices[vertices.length-1].y);
        Vertex left = new Vertex(top.x+slopeL*(top.y-yMin+20),yMin-20);
        Vertex right = new Vertex(top.x+slopeR*(top.y-yMin+20),yMin-20);
        return new Vertex[]{top,left,right};
    }
}

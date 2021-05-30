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
import java.util.ArrayList;
/**
 * The purpose of this class is to represent a vertex
 */
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

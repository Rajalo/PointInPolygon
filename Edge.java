public class Edge {
    Vertex vertex_o, vertex_d;
    Face face_left, face_right;
    public Edge(Vertex vertex_o, Vertex vertex_d, Face face_left, Face face_right)
    {
        this.vertex_o = vertex_o;
        vertex_d.edges.add(this);
        vertex_o.edges.add(this);
        this.vertex_d = vertex_d;
        this.face_right = face_right;
        this.face_left = face_left;
    }
    /**
     * Replaces the given face with a new face
     * @param newFace face being added in
     * @param oldFace face being replaced
     */
    public void replaceFace(Face newFace, Face oldFace)
    {
        if (this.face_left == oldFace)
            this.face_left = newFace;
        else if (this.face_right == oldFace)
            this.face_right = newFace;
    }
    /**
     * Determines if the given Vertex is an endpoint of the edge
     * @param vertex vertex being considered
     * @return true if vertex is an endpoint, false elsewise
     */
    public boolean contains(Vertex vertex)
    {
        return (vertex == vertex_d)||(vertex==vertex_o);
    }

    /**
     * Returns a string representation of the Edge
     * @return a string representation of the Edge
     */
    @Override
    public String toString() {
        return "Edge{" +
                "vertex_o=" + vertex_o +
                ", vertex_d=" + vertex_d +
                '}';
    }
}


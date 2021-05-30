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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this panel is to visually demonstrate the method and allow user inputted polygons and query points
 */
public class KPPanel extends JPanel implements MouseListener, KeyListener {
    static KPPSLG kirk;
    Face polygon;
    ArrayList<Vertex> vertices;
    static int pointerX,pointerY;
    static int counter = 0;
    public KPPanel()
    {
        setBackground(Color.white);
        addMouseListener(this);
        setFocusable(true);
        addKeyListener(this);
        kirk = null;
        polygon = new Face(new Vertex[]{});
        vertices = new ArrayList<>();
        repaint();
        pointerX = pointerY = -10;
    }
    /**
     * Paints the left side of the screen
     * @param g Graphics object used by JPanel
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        polygon.paintFill(g);
        switch (Main.phase)
        {
            case DRAW:
                polygon.paint(g);
                kirk = null;
                break;
            case DAG:
                ArrayList<DAGNode> nodes = DAGPanel.triangulations.get(DAGPanel.triangulations.size()-1);
                for (DAGNode node: nodes)
                    node.paintTriangle(g);
                g.setColor(Color.BLACK);
                int i = 1;
                for (Vertex vertex : DAGNode.vertexList)
                {
                    g.drawString(""+i++, (int)vertex.x+10,(int)vertex.y);
                }
                g.setColor(new Color(200,100,200));
                for (Vertex vertex : DAGNode.vertexList)
                {
                    g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
                }
                g.setColor(new Color(200,100,100));
                for (Vertex vertex : DAGNode.verticesInPlay)
                {
                    g.fillOval((int)vertex.x-4,(int)vertex.y-4,8,8);
                }
                break;
            case TRIANGLE:
                if (kirk == null)
                {
                    ArrayList<Vertex> vertices = new ArrayList<>();
                    for (Vertex vertex : polygon.vertices)
                    {
                        vertices.add(new Vertex(vertex.x,vertex.y));
                    }
                    kirk = new KPPSLG(new Face(vertices));
                    kirk.triangulation();
                }
                kirk.paint(g);
                break;
            case SEARCH:
                List<DAGNode> nodeList = DAGPanel.triangulations.get(DAGPanel.triangulations.size()-1).get(0).pathThroughDAG(new double[]{pointerX,pointerY});
                if (nodeList.size()>0)
                    nodeList.get((counter+nodeList.size())%nodeList.size()).paintFilledTriangle(g);
                g.setColor(new Color(100,200,100));
                g.fillOval(pointerX-5,pointerY-5,10,10);
                g.setColor(Color.BLACK);
                String str = "Pointer is " + ((DAGPanel.triangulations.get(DAGPanel.triangulations.size()-1).get(0).insidePolygon(new double[]{pointerX,pointerY}))?"":"not ")+ "in the polygon";
                g.drawString(str,10,10);
                kirk.paint(g);
                break;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Determines what to do when a key is pressed
     * @param e KeyEvent containing info on which key was pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_BACK_SPACE||e.getKeyCode()==KeyEvent.VK_DOWN)
        {
            counter = (counter-1+200)%200;
            repaint();
            Main.dagPanel.repaint();
        }
        if (e.getKeyCode()==KeyEvent.VK_RIGHT||e.getKeyCode() == KeyEvent.VK_SPACE||e.getKeyCode()==KeyEvent.VK_UP)
        {
            counter = (counter+1+200)%200;
            repaint();
            Main.dagPanel.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    /**
     * Determines what to do when mouse is pressed
     * @param e KeyEvent containing info on which mouse button was pressed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        switch (Main.phase)
        {
            case DRAW:
                if (e.getButton()==MouseEvent.BUTTON1)
                {
                    vertices.add(new Vertex(e.getX(),e.getY()));
                    polygon = new Face(vertices);
                }
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    Vertex closest = vertices.get(0);
                    double dist = Math.hypot(closest.x-e.getX(), closest.y-e.getY());
                    for (int i = 1; i < vertices.size();i++)
                    {
                        Vertex vertex = vertices.get(i);
                        if (Math.hypot(vertex.x-e.getX(), vertex.y-e.getY())<dist)
                        {
                            dist = Math.hypot(vertex.x-e.getX(), vertex.y-e.getY());
                            closest = vertex;
                        }
                    }
                    vertices.remove(closest);
                    polygon = new Face(vertices);
                }
                break;
            case SEARCH:
                pointerX=e.getX();
                pointerY=e.getY();
                Main.dagPanel.repaint();
                break;
        }
        counter=0;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

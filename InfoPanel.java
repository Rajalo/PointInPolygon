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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The purpose of this class is to display infomration at the bottom of the screen
 */
public class InfoPanel extends JPanel implements MouseListener {
    public InfoPanel()
    {
        setBackground(Color.lightGray);
        addMouseListener(this);
        repaint();
    }

    /**
     * Paints the bar at the bottom of the screen
     * @param g Graphics object used by JPanel
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);
        String str = "Phase:";
        switch (Main.phase)
        {
            case DRAW:
                str += "Left Click to Add Vertices, Right Click to Remove!";
                break;
            case DAG:
                str += "Click CONTINUE to Remove the Next Independent Set of Vertices!";
                break;
            case SEARCH:
                str += "Left Click to Test if a Point is in the Polygon, Arrow keys to view DAG path";
                break;
            case TRIANGLE:
                str += "We've drawn the Outer Triangle and triangulated, time for the DAG!";
        }
        g.drawString(str, 30,55);
        g.setColor(new Color(100,100,200));
        g.fillRect(700,25,130,50);
        g.setColor(Color.WHITE);
        g.drawString("CONTINUE",710,55);
        g.setColor(new Color(200,70,70));
        g.fillRect(850,25,130,50);
        g.setColor(Color.WHITE);
        g.drawString("CLEAR",860,55);
        g.setFont(currentFont);
    }

    /**
     * Processes mouse being clicked in this region
     * @param e MouseEvent being analyzed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getX()>700&&e.getX()<830&&e.getY()>25&&e.getY()<75)
            Main.phaseAdd();
        if (e.getX()>850&&e.getX()<980&&e.getY()>25&&e.getY()<75)
            Main.phaseClear();
            repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

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

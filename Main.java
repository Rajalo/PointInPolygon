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
import java.util.ArrayList;
/**
 * The purpose of this class is to provide a main method to run the program and manages the stages
 */
public class Main {
    public static JFrame frame = new JFrame("Point In Polygon Demonstration");
    public static KPPanel kpanel;
    public static DAGPanel dagPanel;

    enum PhaseType {DRAW, TRIANGLE, DAG, SEARCH}
    public static PhaseType phase = PhaseType.DRAW;

    /**
     * Main method of the program
     * @param args
     */
    public static void main(String[] args)
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(0);
        frame.setLayout(borderLayout);
        frame.setSize(1200,600);
        kpanel = new KPPanel();
        kpanel.setPreferredSize(new Dimension(700,400));
        frame.add(kpanel, BorderLayout.WEST);
        dagPanel = new DAGPanel();
        dagPanel.setPreferredSize(new Dimension(500,400));
        frame.add(dagPanel, BorderLayout.LINE_END);
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setPreferredSize(new Dimension(1200,100));
        frame.add(infoPanel,BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Activated when the CONTINUE button is pressed, advances to the next Stage.
     */
    public static void phaseAdd()
    {
        if (phase != PhaseType.DAG || DAGPanel.triangulations.get(DAGPanel.triangulations.size()-1).size()<=2)
        {
            phase = PhaseType.values()[(phase.ordinal()+1)%4];
        }
        switch (phase)
        {
            case DRAW:
                DAGPanel.triangulations = new ArrayList<>();
                break;
            case DAG:
                if (DAGPanel.triangulations.size()<1)
                    dagPanel.start();
                else
                    dagPanel.increment();
                break;
            default:
        }
        kpanel.repaint();
    }

    /**
     * Activated when the CLEAR button is pressed, gets rid of the polygon and returns to DRAW Stage.
     */
    public static void phaseClear() {
        kpanel.polygon = new Face(new Vertex[]{});
        kpanel.vertices = new ArrayList<>();
        kpanel.repaint();
        phase = PhaseType.DRAW;
        DAGPanel.triangulations = new ArrayList<>();
        dagPanel.novelNodes = new ArrayList<>();
        dagPanel.repaint();
    }
}

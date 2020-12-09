import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

public class DAGPanel extends JPanel implements ActionListener{
    public static ArrayList<ArrayList<DAGNode>> triangulations;
    public ArrayList<ArrayList<DAGNode>> novelNodes;
    public DAGPanel()
    {
        setBackground(new Color(250,210,250));
        Timer timer = new Timer(1,this);
        timer.start();
        triangulations = new ArrayList<>();
        novelNodes = new ArrayList<>();
    }

    /**
     * Sets up the first layer of the DAG
     */
    public void start()
    {
        triangulations = new ArrayList<>();
        novelNodes = new ArrayList<>();
        triangulations.add(DAGNode.dagNodesFromKirk(KPPanel.kirk));
        novelNodes.add(triangulations.get(0));
        repaint();
    }

    /**
     * Adds a new level to the DAG
     */
    public void increment()
    {
        ArrayList<Vertex> removals = new ArrayList<>();
        HashSet<Vertex> marked = new HashSet<>();
        ArrayList<DAGNode> recent = triangulations.get(triangulations.size()-1);
        for (Vertex vertex : DAGNode.verticesInPlay)
        {
            if (marked.add(vertex))
            {
                int degree = 0;
                for (DAGNode node : recent)
                {
                    if (node.contains(vertex))
                    {
                        degree++;
                    }
                }
                if (degree >8)
                {
                    marked.remove(vertex);
                    continue;
                }
                removals.add(vertex);
                for (DAGNode node : recent)
                {
                    if (node.contains(vertex))
                    {
                        marked.addAll(node.other(vertex));
                    }
                }
            }
        }
        ArrayList<DAGNode> ans = new ArrayList<>(recent);
        for (Vertex vertex : removals)
        {
            ans = DAGNode.remove(ans,vertex);
        }
        DAGNode.verticesInPlay.removeAll(removals);
        DAGNode.inheritance(ans,triangulations.get(triangulations.size()-1));
        ArrayList<DAGNode> novels = new ArrayList<>();
        for (DAGNode node : ans)
        {
            boolean valid = true;
            for (ArrayList<DAGNode> lst : novelNodes)
            {
                if (lst.contains(node))
                {
                    valid = false;
                    break;
                }
            }
            if (valid)
                novels.add(node);
        }
        novelNodes.add(novels);
        triangulations.add(ans);
        repaint();
    }

    /**
     * Paints the DAG on the right side of the screen
     * @param g Graphics object used by JPanel
     */
    public void paint(Graphics g)
    {
        super.paintComponent(g);
        int center = this.getWidth()/2;
        int width = this.getWidth()-50;
        for (int i = novelNodes.size()-1; i >= 0; i--)
        {
            int displayY = (novelNodes.size()-i)*(42+(int)(30/(Math.sqrt(novelNodes.size()-0.001))))+(int)(50/(novelNodes.size()-0.001));
            if (novelNodes.get(i).size()==2)
            {
                novelNodes.get(i).get(0).displayX = center-width/4;
                novelNodes.get(i).get(1).displayX = center+width/4;
                novelNodes.get(i).get(0).displayY = novelNodes.get(i).get(1).displayY = displayY;
            }
            else{
                for (int j = 0; j < novelNodes.get(i).size(); j++)
                {
                    DAGNode node = novelNodes.get(i).get(j);
                    node.displayY = displayY;
                    node.displayX = (int)((j-novelNodes.get(i).size()/2+((novelNodes.get(i).size()%2==0)?0.5:0))*width/(novelNodes.get(i).size())+2)+center;
                }
            }
        }
        g.setColor(Color.black);
        for (int i = 1; i < novelNodes.size(); i++)
        {
            for (int j = 0; j < novelNodes.get(i).size(); j++)
            {
                DAGNode node = novelNodes.get(i).get(j);
                for (DAGNode child : node.children)
                {
                    g.drawLine(node.displayX, node.displayY, child.displayX,child.displayY);
                }
            }
        }
        for (ArrayList<DAGNode> novelNode : novelNodes) {
            for (DAGNode node : novelNode) {
                node.paintDisplay(g);
            }
        }
        if (Main.phase== Main.PhaseType.SEARCH)
        {
            g.setColor(new Color(100,200,100));
            int i = 0;
            ArrayList<DAGNode> nodeArrayList = triangulations.get(triangulations.size()-1).get(0).pathThroughDAG(new double[]{KPPanel.pointerX,KPPanel.pointerY});
            for (DAGNode node : nodeArrayList)
            {
                if (i==(KPPanel.counter+nodeArrayList.size())%nodeArrayList.size())
                {
                    g.setColor(new Color(100,250,150));
                    g.drawRoundRect(node.displayX-23,node.displayY-23,46,46,21,21);
                    g.drawRoundRect(node.displayX-24,node.displayY-24,48,48,22,22);
                }
                g.setColor(new Color(100,200,100));
                g.drawRoundRect(node.displayX-22,node.displayY-22,44,44,20,20);
                g.drawRoundRect(node.displayX-21,node.displayY-21,42,42,19,19);
                i++;
            }
        }
    }

    /**
     * Adjusts the window size
     * @param e Event that triggers the need to adjust the window size
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Main.kpanel.getWidth()>0)
        {
            setPreferredSize(new Dimension(Main.frame.getWidth()-Main.kpanel.getWidth(),this.getHeight()-1));
            setMinimumSize(new Dimension(Main.frame.getWidth()-Main.kpanel.getWidth(),400));
            if (this.getWidth()<Main.frame.getWidth()-Main.kpanel.getWidth())
                Main.frame.pack();
        }
    }
}

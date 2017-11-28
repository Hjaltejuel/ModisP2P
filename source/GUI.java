

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
   Just a fun little extra class that i made to help analyse and test the node system

 */
public class GUI extends JFrame {
    private static JFrame frame = new JFrame();
    private static Container contentPane = frame.getContentPane();
    private static JPanel node = new JPanel(new GridLayout(1,2));
    private static JPanel grid = new JPanel(new GridLayout(2,3));
    private static JPanel NodePanel = new JPanel();
    private static JLabel NodeID = new JLabel();
    private static JPanel constraint = new JPanel(new GridBagLayout());
    private static JPanel prevPanel = new JPanel();


    private static JLabel prevLabel = new JLabel("PrevNodePortNumber");

    private static JPanel prevPanel2 = new JPanel();
    private static JLabel prevLabel2 = new JLabel("NextNodePortNumber");

    private static JPanel prevPanel3 = new JPanel();
    private static JLabel prevLabel3 = new JLabel("NextNextNodePortNumber");

    private static JPanel prevPanel4 = new JPanel();
    private static JLabel prevLabel4 = new JLabel();

    private static JPanel prevPanel5 = new JPanel();
    private static JLabel prevLabel5 = new JLabel();

    private static JPanel prevPanel6 = new JPanel();
    private static JLabel prevLabel6 = new JLabel();
    private static JTextArea area = new JTextArea(11,30);
    private  static JTextArea values2 = new JTextArea(5,50);
    private static int i = 0;



    public static void main(String[] args){
         new GUI(5);
    }

    public GUI (int nodeID){
        frame.add(new JPanel());
        frame.setTitle("NodeNetwork");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(1280,720);
        frame.setLocationRelativeTo(null);
        makeStartFrame(nodeID);
        frame.setVisible(true);

    }

    public static void makeStartFrame(int nodeID){


        grid.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        node.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        NodePanel.add(NodeID);
        node.add(NodePanel);
        NodePanel.setBackground(Color.GRAY);
        NodeID.setText("NodePortNumber : " +nodeID);
        node.add(grid);
        JPanel top = new JPanel(new GridLayout(0,1));

        top.add(node);
        values2.setBackground(Color.LIGHT_GRAY);
        values2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        values2.append("Here the key value set will appear when changed");
        JScrollPane jScrollPane2 = new JScrollPane(values2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        top.add(jScrollPane2);

        constraint.add(top);

        prevPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevPanel3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevPanel4.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevPanel5.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevPanel6.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        prevLabel4.setText("Portnumber : null");
        prevLabel5.setText("Portnumber : null");
        prevLabel6.setText("Portnumber : null");
        prevPanel.add(prevLabel);
        prevPanel2.add(prevLabel2);
        prevPanel3.add(prevLabel3);
        prevPanel4.add(prevLabel4);
        prevPanel5.add(prevLabel5);
        prevPanel6.add(prevLabel6);
        grid.add(prevPanel);
        grid.add(prevPanel2);
        grid.add(prevPanel3);
        grid.add(prevPanel4);
        grid.add(prevPanel5);
        grid.add(prevPanel6);
        contentPane.add(constraint,BorderLayout.CENTER);



        area.setBackground(Color.LIGHT_GRAY);
        area.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        area.append("   This is were actions on the given nodes are written :D");

        JScrollPane jScrollPane = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel grid2 = new JPanel(new GridLayout(2,1));
        JLabel label = new JLabel("CONSOLE OUTPUT",SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        grid2.add(label);
        grid2.add(jScrollPane);
        contentPane.add(grid2,BorderLayout.SOUTH);



    }
    public static void appendValues(HashMap<Integer,String> myMap){
        values2.setText("Current configuration :");

        for(Integer s: myMap.keySet()){
            values2.append("\n   Key: " +  s + " Val: " + myMap.get(s));
        }

        values2.validate();
    }
    public static void append(String s){
        area.append("\n   " + i + ": " + s);
        i++;
        area.validate();
    }
   
    public static void setTextOnJPanels(RoutingInfo prev, RoutingInfo next, RoutingInfo nextnext){
        if(prev!= null) {
            prevLabel4.setText("Portnumber : " + prev.getPort());
        } else {
            prevLabel4.setText("Portnumber : null");
        }
        if(next!=null) {
            prevLabel5.setText("Portnumber : " + next.getPort());
        } else {
            prevLabel5.setText("Portnumber : null");
        }
        if(nextnext!=null) {
            prevLabel6.setText("Portnumber : " + nextnext.getPort());
        } else {
            prevLabel6.setText("Portnumber : null");
        }
    }



}

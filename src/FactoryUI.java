import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FactoryUI {

    static JFrame frame;
    static JPanel floorPanel;
    static JPanel statPanel;
    static JPanel listPanel;

    public static void main(String[] args) {
        initializeComponents();
    }

    static void initializeComponents() {
        // frame setup
        frame = new JFrame();
        frame.setTitle("Factory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3));
        frame.setSize(500, 500);
        frame.setVisible(true);

        //panel setup
        floorPanel = new JPanel();
        floorPanel.setSize(100,100);
        floorPanel.setBackground(Color.WHITE);
        floorPanel.setBorder(new LineBorder(Color.BLACK, 5, true));
        frame.add(floorPanel);

        statPanel = new JPanel();
        statPanel.setSize(100,100);
        statPanel.setBackground(Color.WHITE);
        frame.add(statPanel);

        listPanel = new JPanel();
        listPanel.setSize(100,100);
        listPanel.setBackground(Color.WHITE);
        frame.add(listPanel);

    }

}

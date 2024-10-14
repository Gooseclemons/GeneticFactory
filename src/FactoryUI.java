import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FactoryUI extends JFrame {

    // Define Components as Globals

    // Window and Component fields
    Color backgroundColor;
    final int SCREEN_WIDTH, SCREEN_HEIGHT;


    FactoryUI(Color backgroundColor) {
        this.backgroundColor = backgroundColor;

        // Sets screen to max on start and saves width and height for later
        setExtendedState(MAXIMIZED_BOTH);
        Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        SCREEN_WIDTH = (int) r.getWidth();
        SCREEN_HEIGHT = (int) r.getHeight();
        System.out.println(r);

        initializeComponents();
    }

    void initializeComponents() {

        // Window initialization/settings
        Container c = getContentPane();
        setTitle("GeneticFactory");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        c.setBackground(backgroundColor);
        setVisible(true);

        /*
         * Initializes the factoryGrid panel. Takes up EAST 2/3rds of screen
         */
        JPanel factoryGrid = new JPanel();
        factoryGrid.setBackground(backgroundColor);
        factoryGrid.setPreferredSize(new Dimension((int) ((SCREEN_WIDTH * 0.75) - 7),SCREEN_HEIGHT));
        factoryGrid.setBorder(new CompoundBorder(
                new EmptyBorder(5,5,5,5),
                new LineBorder(Color.BLACK, 2, true)));
        add(factoryGrid, BorderLayout.EAST);

        /*
         * Initializes the container for stat and floor panels. Takes up WEST 1/3rd of screen. Uses GridLayout
         */
        JPanel containerPanel = new JPanel();
        containerPanel.setBackground(backgroundColor);
        containerPanel.setPreferredSize(new Dimension((int) (SCREEN_WIDTH * 0.25) - 7, SCREEN_HEIGHT));
        containerPanel.setLayout(new GridLayout(2,1));
        containerPanel.setBorder(new CompoundBorder(
                new EmptyBorder(5,5,5,5),
                new LineBorder(Color.BLACK, 2, true)));
        add(containerPanel, BorderLayout.WEST);

    }

}
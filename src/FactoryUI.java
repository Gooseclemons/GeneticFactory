import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class FactoryUI extends JFrame {

    // Reference to Factory object to access the Floors (subpops) within for display
    Factory parentFactory;

    // Define Components as Globals
    JPanel factoryGrid;
    JPanel containerPanel;

    // Window and Component fields
    Color backgroundColor;
    final int SCREEN_WIDTH, SCREEN_HEIGHT;


    FactoryUI(Factory parentFactory, Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.parentFactory = parentFactory;

        // Sets screen to max on start and saves width and height for later
        setExtendedState(MAXIMIZED_BOTH);
        Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        SCREEN_WIDTH = (int) r.getWidth();
        SCREEN_HEIGHT = (int) r.getHeight();

        initializeComponents();
    }

    void initializeComponents() {

        // Window initialization/settings
        setTitle("GeneticFactory");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(backgroundColor);
        setVisible(true);

        /*
         * Initializes the factoryGrid panel. Takes up EAST 2/3rds of screen
         */
        factoryGrid = new JPanel();
        factoryGrid.setBackground(backgroundColor);
        factoryGrid.setPreferredSize(new Dimension((int) ((SCREEN_WIDTH * 0.75) - 7),SCREEN_HEIGHT));
        factoryGrid.setBorder(new CompoundBorder(
                new EmptyBorder(5,5,5,5),
                new LineBorder(Color.BLACK, 2, true)));
        add(factoryGrid, BorderLayout.EAST);

        /*
         * Initializes the container for stat and floor panels. Takes up WEST 1/3rd of screen. Uses GridLayout
         */
        containerPanel = new JPanel();
        containerPanel.setBackground(backgroundColor);
        containerPanel.setPreferredSize(new Dimension((int) (SCREEN_WIDTH * 0.25) - 7, SCREEN_HEIGHT));
        containerPanel.setLayout(new GridLayout(2,1));
        containerPanel.setBorder(new CompoundBorder(
                new EmptyBorder(5,5,5,5),
                new LineBorder(Color.BLACK, 2, true)));
        add(containerPanel, BorderLayout.WEST);

    }

    void updateGrid(Floor f) {
        factoryGrid.removeAll();
        int dimension = parentFactory.dimension;

        // Add gridLayout to factoryGrid before adding floor stations
        factoryGrid.setLayout(new GridLayout(dimension, dimension, 1, 1));

        // Paints stations as black and unoccupied spots as default background color
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                JPanel panel = new JPanel();
                if (f.availableCoordinates.contains(new Coordinate(i, j)))
                    panel.setBackground(backgroundColor);
                else
                    panel.setBackground(Color.BLACK);
                factoryGrid.add(panel);
            }
        }

        factoryGrid.revalidate(); // This is required to fix the GridLayout display because swing likes to be annoying :'(

    }

}
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

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

//    void updateGrid(Floor f) {
//        factoryGrid.removeAll();
//        int dimension = parentFactory.dimension;
//
//        // Add gridLayout to factoryGrid before adding floor stations
//        factoryGrid.setLayout(new GridLayout(dimension, dimension, 1, 1));
//
//        // Paints stations as black and unoccupied spots as default background color
//        for (int i = 0; i < dimension; i++) {
//            for (int j = 0; j < dimension; j++) {
//                JPanel panel = new JPanel();
//                if (f.availableCoordinates.contains(new Coordinate(i, j)))
//                    panel.setBackground(backgroundColor);
//                else {
//                    panel.setBackground(Color.GREEN);
//                    //panel.add(new JLabel("1", SwingConstants.CENTER));
//                }
//                factoryGrid.add(panel);
//            }
//        }
//
//        factoryGrid.revalidate(); // This is required to fix the GridLayout display because swing likes to be annoying :'(
//
//    }

    void updateGrid(Floor f) {
        factoryGrid.removeAll();
        int dimension = parentFactory.dimension;

        // Create and fill an array with black JPanels to be edited later
        JPanel[][] panelArray = new JPanel[dimension][dimension];

        factoryGrid.setLayout(new GridLayout(dimension, dimension, 1, 1));

        // Set all JPanels that are on available coordinates to background color
        for (Coordinate coordinate : f.availableCoordinates) {
            int x = coordinate.x;
            int y = coordinate.y;
            JPanel panel = new JPanel();
            panel.setBackground(backgroundColor);
            panelArray[x][y] = panel;
        }
        // Now set all of the Stations to panels
        for (Station s : f.stationSet.stations) {
            ArrayList<Coordinate> coordinates = s.coordinates;
            for (Coordinate coordinate : coordinates) {
                int x = coordinate.x;
                int y = coordinate.y;
                JPanel panel = new JPanel();
                if (s.type == 0) { panel.setBackground(Color.GREEN); panel.add(new JLabel("1"), SwingConstants.CENTER); }
                else if (s.type == 1) { panel.setBackground(Color.RED); panel.add(new JLabel("2"), SwingConstants.CENTER); }
                panelArray[x][y] = panel;
            }
        }

        // Add all of the panels to the factoryGrid
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                factoryGrid.add(panelArray[i][j]);
            }
        }

        factoryGrid.revalidate();

    }

}
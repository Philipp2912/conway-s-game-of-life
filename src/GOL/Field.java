package GOL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Formatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Field extends JPanel {
    //initialising all instances
    //determines next step
    private int action = 0;
    //generation counter
    private int generation = 0;
    //field size
    private int size;
    //delay
    private int sleep = 300;
    //prev delay
    private int prevDelay;
    //needed for random extinction and creation
    private Random random = new Random();
    //value of cells alive at any given time
    private int living = 0;
    //just a file
    private File file;
    //text field for generations
    JTextField field= new JTextField();
    int Nilschwein;
    private KeyListener listener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };
    //creates all buttons that are needed
    private JButton[] buttons = new JButton[8];
    private Tile[][] tiles;
    private JFileChooser fileChooser = new JFileChooser();
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttons[0])
                action=1;
            else if (e.getSource()==buttons[1])
                action=2;
            else if(e.getSource()==buttons[2])
                action=3;
            else if(e.getSource()==buttons[3])
                action=4;
            else if(e.getSource()==buttons[4])
                action=5;
            else if(e.getSource()==buttons[5])
                action=6;
            else if(e.getSource()==buttons[6])
                action=7;
            else if (e.getSource()==buttons[7])
                action=8;
        }
    };
    JFrame frame=new JFrame();


    public Field(int size) { //draws the Field
        this.size = size;
        int frameSizeY = (size * 15) + 100;
        if (frameSizeY < 1450)
            frameSizeY = 1450;
        frame.setSize((size * 15) + 500, frameSizeY);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        Container container = new Container();
        container.setSize((size * 15) + 500, (size * 15) + 100);
        frame.add(container);
        tiles=new Tile[size][size];

        container.add(field);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int x = (e.getX() - (e.getX() % 15)) / 15;
                    int y = (e.getY() - (e.getX() % 15)) / 15;
                    tiles[x][y].cellStatus=(tiles[x][y].occupied ? 0:1);
                    tiles[x][y].occupied = (tiles[x][y].occupied ? false : true);
                    repaint(tiles[x][y].getX()+1,tiles[x][y].getY()+1,13,13);
                    repaint();


                } catch (Exception e1) {
                    int x = (e.getX() - (e.getX() % 15)) / 15;
                    int y = (e.getY() - (e.getX() % 15)) / 15;
                    e1.printStackTrace();
                }
            }
        };
        addMouseListener(mouseAdapter);
        setBounds(20, 20, size * 15, size * 15);
        frame.add(this);
        setBackground(Color.BLACK);
        setVisible(true);
        for (int i=0; i<8; i++) {
            buttons[i]=new JButton();
            this.buttons[i].addActionListener(actionListener);
        }
        buttons[0].setBounds(15 * size + 50, 50, 400, 100);
        buttons[1].setBounds(15 * size + 50, 200, 400, 100);
        buttons[2].setBounds(15 * size + 50, 350, 400, 100);
        buttons[3].setBounds(15 * size + 50, 500, 400, 100);
        buttons[4].setBounds(15 * size + 50, 650, 400, 100);
        buttons[5].setBounds(15 * size + 50, 800, 400, 100);
        buttons[6].setBounds(15 * size + 50, 950, 400, 100);
        buttons[7].setBounds(15*size+50,1100,400,100);
        field.setBounds(15 * size + 50, 1250, 400, 50);
        buttons[0].setText("next step");
        buttons[1].setText("automated");
        buttons[2].setText("reset");
        buttons[3].setText("let there be chaos");
        buttons[4].setText("random extinction");
        buttons[5].setText("save to file");
        buttons[6].setText("load from file");
        buttons[7].setText("change speed");
        for (int i=0;i<8; i++) {
            container.add(buttons[i]);
            buttons[i].setVisible(true);
        }
        int generation = 0;
        field.setText("generation: " + generation);
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                try {
                    tiles[x][y] = new Tile((x * 15) + 21, (y * 15) + 21);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        while (true) {
            repaint();
            //next step
            if (action == 1) {
                nextStep();
                action = 0;
            }
            //fully automated
            else if (action == 2) {
                nextStep();
                try {
                    TimeUnit.MILLISECONDS.sleep(sleep);
                } catch (Exception ignored) {
                }
            }
            //reset.... obviously
            else if (action == 3) {
                reset();
                action = 0;
            }
            //adds random cells
            else if (action == 4) {
                addRandomCells(Integer.parseInt(JOptionPane.showInputDialog("insert number of cells to add from 0 to" + ((size * size) - livingCells()))));
                action = 0;
            }
            //removes random cells
            else if (action == 5) {
                try {
                    removeRandomCells(Integer.parseInt(JOptionPane.showInputDialog("insert number of cells should be removed from 0 to" + livingCells())));
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                System.out.println("a");
                action = 0;
            }
            //saves the current field
            else if (action == 6) {
                save();
                action=0;
            }
            //loads a file
            else if(action==7)
            {
                load();
                action=0;
            }
            else if(action==8)
            {
                prevDelay=sleep;
                try {
                    sleep=Integer.parseInt(JOptionPane.showInputDialog("enter the new speed (default: 300 || current: "+sleep+")"));
                }
                catch (Exception ignored)
                {
                    sleep=prevDelay;
                }
                action=0;
            }
        }
    }

    //performs the next step
    private void nextStep() {
        //determines the action for all tiles
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == 0 && y == 0) //top left corner
                    tiles[x][y].determineAction(false, false, false, false, false, tiles[1][0].occupied, tiles[1][1].occupied, tiles[0][1].occupied);

                if (x == 0 && y == size - 1) //top right corner
                    tiles[x][y].determineAction(false, false, false, false, false, tiles[0][size - 2].occupied, tiles[1][size - 2].occupied, tiles[1][size - 1].occupied);

                if (x == size - 1 && y == 0) //bottom left corner
                    tiles[x][y].determineAction(false, false, false, false, false, tiles[size - 2][0].occupied, tiles[size - 1][1].occupied, tiles[size - 2][1].occupied);

                if (x == size - 1 && y == size - 1) //bottom right corner
                    tiles[x][y].determineAction(false, false, false, false, false, tiles[size - 1][size - 2].occupied, tiles[size - 2][size - 1].occupied, tiles[size - 2][size - 2].occupied);

                if (x != 0 && y == 0 && x != size - 1)  //top border
                    tiles[x][y].determineAction(false, false, false, tiles[x - 1][y].occupied, tiles[x + 1][y].occupied, tiles[x - 1][y + 1].occupied, tiles[x][y + 1].occupied, tiles[x + 1][y + 1].occupied);

                if (x != 0 && x != size - 1 && y == size - 1) //Bottom corner
                    tiles[x][y].determineAction(false, false, false, tiles[x - 1][y].occupied, tiles[x + 1][y].occupied, tiles[x - 1][y - 1].occupied, tiles[x][y - 1].occupied, tiles[x + 1][y - 1].occupied);

                if (x == 0 && y != 0 && y != size - 1) //left border
                    tiles[x][y].determineAction(false, false, false, tiles[x][y - 1].occupied, tiles[x][y + 1].occupied, tiles[x + 1][y - 1].occupied, tiles[x + 1][y].occupied, tiles[x + 1][y + 1].occupied);

                if (x == size - 1 && y != 0 && y != size - 1) //right border
                    tiles[x][y].determineAction(false, false, false, tiles[x][y - 1].occupied, tiles[x][y + 1].occupied, tiles[x - 1][y - 1].occupied, tiles[x - 1][y].occupied, tiles[x - 1][y + 1].occupied);

                if (x != 0 && x != size - 1 && y != 0 && y != size - 1) //everything else
                    tiles[x][y].determineAction(tiles[x - 1][y - 1].occupied, tiles[x][y - 1].occupied, tiles[x + 1][y - 1].occupied, tiles[x - 1][y].occupied, tiles[x + 1][y].occupied, tiles[x - 1][y + 1].occupied, tiles[x][y + 1].occupied, tiles[x + 1][y + 1].occupied);

            }
        }
        generation += 1;
        //the number of the generations passed gets displayed on the right side of the window
        field.setText("generation: "+generation);
        //executes the action
        for (int z = 0; z < size; z++) {
            for (int y = 0; y < size; y++) {
                tiles[z][y].performAction();
            }
        }
        repaint();
    }

    //resets the field
    private void reset() {
        //resets the entire field
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                //the tile was never occupied
                tiles[x][y].determineAction(false);
                //the tile was never living
                tiles[x][y].cellStatus = 0;
                tiles[x][y].performAction();
            }
            //resets the counter of generations passed
            generation=0;
            field.setText("generation: "+generation);
        }
    }

    //generates random living cells
    private void addRandomCells(int amount) {
        if (amount == ((size * size) - livingCells())) {
            defaultOperation(true);
        } else {

                while (amount != 0) {
                int x = random.nextInt(size);
                int y = random.nextInt(size);
                if (!tiles[x][y].occupied) {
                    defaultOperation(x, y, true);
                    amount--;
                }
            }
        }
        repaint();
    }


    //removes random cells
    private void removeRandomCells(int amount) {
        if (amount == livingCells()) {
            defaultOperation(false);
        } else {
            while (amount != 0) {
                int x = random.nextInt(size);
                int y = random.nextInt(size);
                if (tiles[x][y].occupied) {
                    defaultOperation(x, y, false);
                    amount--;
                }
            }
        }
    }

    //the amount of cells being alive
    int livingCells() {
        living = 0;
        //looks at all tiles on the field
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++)
                if (tiles[x][y].occupied)
                {
                    living++;
                }
        }
        return living;
    }

    private void defaultOperation(boolean status) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                tiles[x][y].determineAction(status);
                tiles[x][y].performAction();
            }
        }
    }

    private void defaultOperation(int x, int y, boolean status) {
        tiles[x][y].determineAction(status);
        tiles[x][y].performAction();
    }
    //it simply saves the field in a file
    private void save()
    {
        Boolean save = true;
        try {
            fileChooser.setSelectedFile(file);
            int res = fileChooser.showSaveDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
            file = new File(file.getAbsolutePath());
            if (res == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (!file.exists()) {
                    try {
                        new Formatter(file);
                    } catch (Exception ignored) {

                    }
                } else {
                    save = (JOptionPane.showConfirmDialog(null, "Datei Existiert Bereits, soll sie überschrieben werden?", null, JOptionPane.YES_NO_OPTION) == 1 ? false : save);
                }
                if (save) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(size + "");
                    writer.newLine();
                    int fileSize = 0;
                    for (int x = 0; x < size; x++) {
                        for (int y = 0; y < size; y++) {
                            if (tiles[x][y].occupied) {
                                fileSize++;
                            }
                        }
                    }
                    writer.write(fileSize + "");
                    writer.newLine();
                    for (int x = 0; x < size; x++) {
                        for (int y = 0; y < size; y++) {
                            if (tiles[x][y].occupied) {
                                writer.write(x + " " + y + 'a');
                                writer.newLine();
                            }
                        }
                    }
                    writer.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load()
    {
        try
        {
            fileChooser.setSelectedFile(file);
            fileChooser.showOpenDialog(null);
            file=fileChooser.getSelectedFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            size=Integer.parseInt(reader.readLine());
            //looks at how many tiles are listed as alive
            int fileSize=Integer.parseInt(reader.readLine());
            //sets all tiles to empty
            for (int x1 = 0; x1 < size; x1++) {
                for (int y1 = 0; y1 < size; y1++) {
                    tiles[x1][y1].determineAction(false);
                    tiles[x1][y1].performAction();
                }
            }
            int pos;
            String line;
            String num;
            for (int i=0;i<fileSize;i++)
            {
                line=reader.readLine();
                int x=0;
                num="";
                while (line.charAt(x)!=' ')
                {
                    num=num+line.charAt(x);
                    x++;
                }
                x++;
                pos=Integer.parseInt(num);
                num="";
                while (line.charAt(x)!='a')
                {
                    num=num+line.charAt(x);
                    x++;
                }
                tiles[pos][Integer.parseInt(num)].determineAction(true);
                tiles[pos][Integer.parseInt(num)].performAction();
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                g.setColor(Color.BLACK);
                g.drawRect(tiles[x][y].getX() - 20, tiles[x][y].getY() - 20, 15, 15);
                /*if the tile is alive it is displayed as green, if it is dead it is red,
                if it was living at no point it is white*/
                g.setColor((tiles[x][y].occupied ? Color.GREEN : (tiles[x][y].cellStatus == 1 ? Color.RED : Color.WHITE)));
                g.fillRect(tiles[x][y].getX() - 20, tiles[x][y].getY() - 20, 13, 13);
                setBounds(20, 20, size * 15, size * 15);
            }
        }
    }
}

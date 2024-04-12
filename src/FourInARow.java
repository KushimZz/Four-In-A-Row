import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
public class FourInARow extends JFrame {
    int player = 1;
    boolean isPvP = true;
    boolean aiActivated= false;
    int ROWS = 6;
    int COLUMNS = 7;
    int[][] board = new int[ROWS][COLUMNS];
    JPanel boardPanel;
    int circleSize = 85;
    int width = 100;
    int length = 100;
    JTextField playerTurn = new JTextField();
    JPanel buttonPanel;
    //IMPORTANT NOTE: I dont know why but i cant add Board.png like "src/Board.png" so i have to write it like "out/production/FourInaRow/Board.png"
    // if it doesnt work please take the absolute path of Board.png and paste it to line 23. i failed to find a solution
    Image panelImage = new ImageIcon("out/production/FourInaRow/Board.png").getImage();
    FourInARow(){
        super("Four In A Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(715, 680);
        boardPanel();
        modeButton();
        playerTurn.setBounds(450, 610, 200, 35);
        playerTurn.setForeground(Color.BLACK);
        playerTurn.setBackground(Color.BLACK);
        playerTurn.setEditable(false);
        add(playerTurn, BorderLayout.SOUTH);
        add(boardPanel);
        add(buttonPanel,BorderLayout.SOUTH);
        buttonPanel.setBackground(Color.BLACK);
        setVisible(true);
        resetBoard();
        player=1;
    }
    private void boardPanel(){
        boardPanel = new JPanel();
        JLabel imageLabel = new JLabel(new ImageIcon(panelImage)){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                setCircles(g);
            }
        };
        boardPanel.add(imageLabel, BorderLayout.CENTER);
        boardPanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                mouseClickAction(e);
            }
        });
    }
    private void setCircles(Graphics g){
        final int CircleSet = 10;
        for(int row = 0; row < ROWS; row++){
            for(int col = 0; col < COLUMNS; col++){ 
                int x = col * width + CircleSet;
                int y = row * length + CircleSet;
                int playerValue = board[row][col];
                if(playerValue == 1){
                    drawCircle(g, x, y, Color.RED);
                }
                else if(playerValue == 2){
                    drawCircle(g, x, y, Color.YELLOW);
                }
            }
        }
    }
    private void drawCircle(Graphics g, int x, int y, Color color){
        g.setColor(color);
        g.fillOval(x, y, circleSize, circleSize);
    }
    private void placeCircle(int column) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = player;
                boardPanel.repaint();
                if(isGameOver(row,column,board,player)){
                    playerWin();
                }
                break;
            }
        }
    }
    private int artificalIntelligenceCheck(){
        ArrayList<Integer> potentialWinColumns = new ArrayList<>();
        ArrayList<Integer> potentialBlockColumns = new ArrayList<>();
        ArrayList<Integer> potentialRandomPosition = new ArrayList<>();
        int[][] artificalBoard = board;
        for(int column = 0; column<=COLUMNS-1; column++){
            for (int row = ROWS - 1; row >= 0; row--) {
                if (board[row][column] == 0) {
                    artificalBoard[row][column]=2;
                    if(isGameOver(row,column,board,2)){
                        potentialWinColumns.add(column);
                    }
                    artificalBoard[row][column]=1;
                    if (isGameOver(row,column,board,1)){
                        potentialBlockColumns.add(column);
                    }
                    artificalBoard[row][column]=0;
                    potentialRandomPosition.add(column);
                    break;
                }
            }
        }
        if(potentialWinColumns.size()>0){
            int randomWin=selectRandomElement(potentialWinColumns);
            System.out.println("Win: "+potentialWinColumns+" selected: "+randomWin);
            return randomWin;
        }
        else if(potentialBlockColumns.size()>0){
            int randomBlock=selectRandomElement(potentialBlockColumns);
            System.out.println("Block: "+potentialBlockColumns+" selected: "+randomBlock);
            return randomBlock;
        }
        else{
            int randomRandom=selectRandomElement(potentialRandomPosition);
            System.out.println("Random: "+ potentialRandomPosition+ " Random selected:"+ randomRandom);
            return randomRandom;
        }
    }
    public static <T> T selectRandomElement(ArrayList<T> list) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(list.size());
        return list.get(randomIndex);
    }
    private void mouseClickAction(MouseEvent e){
        int column = e.getX() / 100;
        placeCircle(column);
        changePlayer();
        if(isBoardFull()){
            draw();
        }
        else{
            if(player == 2 && aiActivated) {
                placeCircle(artificalIntelligenceCheck());
                changePlayer();
            }
        }
    }
    private void modeButton(){
        JButton Button = new JButton("Player vs Player Mode");
        Button.addActionListener(e -> changeGameMode(Button));
        buttonPanel = new JPanel();
        buttonPanel.add(Button);
    }
    private void changeGameMode(JButton playerButton) {
        isPvP=!isPvP;
        if(isPvP){
            playerButton.setText("Player vs Player Mode");
            aiActivated = false;
        }
        else{
            playerButton.setText("Player vs Computer Mode");
            aiActivated = true;
        }
    }
    private void changePlayer() {
        if(player==1){
            playerTurn.setForeground(Color.yellow);
            playerTurn.setText("Yellow's Turn");
            player=2;
        }
        else{
            playerTurn.setForeground(Color.red);
            playerTurn.setText("Red's Turn");
            player=1;
        }
    }
    private void resetBoard(){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                board[i][j] = 0;
            }
        }
        boardPanel.repaint();
    }
    private void playerWin(){
        if (aiActivated && player==2){
            JOptionPane.showMessageDialog(this,"Computer Won!");
        }
        else{
            JOptionPane.showMessageDialog(this,"Player "+player+" Won!");
        }
        resetBoard();
    }
    public void draw(){
        JOptionPane.showMessageDialog(this,"Draw!");
        resetBoard();
    }
    public boolean isGameOver(int x,int y, int[][] board,int player){
        if (checkRow(x,y,board,player)){
        return true;
        }
        else if(checkColumn(x,y,board,player)){
            return true;
        }
        else if(checkLeftCross(x,y,board,player)){
            return true;
        }
        else if(checkRightCross(x,y,board,player)){
            return true;
        } else if(isBoardFull()){
            draw();
        }
        return false;
    }
    private boolean isBoardFull() {
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                if(board[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkRow(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;
            for(int j =0; j<=3; j++){
                try {
                    if(board[x][y+i+j] == player){
                        counter++;
                    }
                } catch (Exception e){
                    //System.out.println("oisr");
                }
            }
            if(counter==4){
                //System.out.println("Row True");
                return true;
            }
        }
        return false;
    }
    private boolean checkColumn(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;
            for(int j =0; j<=3; j++){
                try {
                    if(board[x+i+j][y] == player){
                        counter++;
                    }
                } catch (Exception e){
                    //System.out.println("oiscol");
                }

            }
            if(counter==4){
                //System.out.println("Column true");
                return true;
            }
        }
        return false;
    }
    private boolean checkLeftCross(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;//for each circle for same player, count will up 1
            for(int j =0; j<=3; j++){//these loops checks if 3 to up left corss or 3 to down right cross circles are belongs to same player
                try {
                    if(board[x+i+j][y+i+j] == player){
                        counter++;
                    }
                } catch (Exception e){
                    //System.out.println("oiscrl");
                }

            }
            if(counter==4){
                //System.out.println("Cross Left true");
                return true;
            }
        }
        return false;
    }
    private boolean checkRightCross(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;
            for(int j =0; j<=3; j++){
                try {
                    if(board[x+i+j][y-(i+j)] == player){
                        counter++;
                    }
                } catch (Exception e){
                    //System.out.println("oiscrr");
                }
            }
            if(counter==4){
                //System.out.println("Cross Right true");
                return true;
            }
        }
        return false;
    }
}

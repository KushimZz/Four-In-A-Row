import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
public class FourInARow extends JFrame {
    int player = 1; // if true its turn for player 1 if not its for player 2
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
    //constructor
    FourInARow(){
        super("Four In A Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(715, 690);
        boardPanel();
        modeButton();
        playerTurn.setBounds(450, 614, 200, 35);
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

    //panel for game
    private void boardPanel(){
        boardPanel = new JPanel();
        JLabel imageLabel = new JLabel(new ImageIcon(panelImage)){
            @Override
            //for painting the Board.png
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                setCircles(g);
            }
        };
        boardPanel.add(imageLabel, BorderLayout.CENTER); // Add the Board.png into boardPanel
        boardPanel.addMouseListener(new MouseAdapter(){  //adds MouseListener to panel.
            @Override
            public void mouseClicked(MouseEvent e){//calls mouseClickAction() when clicked.
                mouseClickAction(e);
            }
        });
    }
    private void setCircles(Graphics g){
        final int CircleSet = 10;
        for(int row = 0; row < ROWS; row++){
            for(int col = 0; col < COLUMNS; col++){ //loops through the board represented by ROWS and COLUMNS.
                int x = col * width + CircleSet;
                int y = row * length + CircleSet;
                int playerValue = board[row][col];//For each cell in the board, it calculates the x and y coordinates to draw a circle.
                if(playerValue == 1){//player one
                    drawCircle(g, x, y, Color.RED);//determines the playerfrom the board array and draws a circle of the respective color.
                }
                else if(playerValue == 2){//player two
                    drawCircle(g, x, y, Color.YELLOW);
                }
            }
        }
    }

    private void drawCircle(Graphics g, int x, int y, Color color){//This method is a helper function used by setCircles.
        g.setColor(color);
        g.fillOval(x, y, circleSize, circleSize);
    }
    private void placeCircle(int column) {//finds the first empty row in the column and places the current player's circles inside of it.
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = player;
                boardPanel.repaint();//For showing the last circle before game over.
                if(isGameOver(row,column,board,player)){
                    playerWin();
                }
                break;
            }
        }
    }

    //COMPUTER
    private int artificalIntelligenceCheck(){
        ArrayList<Integer> potentialWinColumns = new ArrayList<>();
        ArrayList<Integer> potentialBlockColumns = new ArrayList<>();
        ArrayList<Integer> potentialRandomPosition = new ArrayList<>();
        int[][] artificalBoard = board;
        for(int column = 0; column<=COLUMNS-1; column++){//The method loops through each column of the game board.
            for (int row = ROWS - 1; row >= 0; row--) {// For each column, it iterates through the rows from bottom to top.
                if (board[row][column] == 0) {// checks if the current position in the board is empty.
                    artificalBoard[row][column]=2;// For each empty position, temporarily sets the board position to the AI player's value
                    if(isGameOver(row,column,board,2)){ //If turn is on ai it will try to find a column for winnig
                        potentialWinColumns.add(column);
                    }
                    artificalBoard[row][column]=1;
                    if (isGameOver(row,column,board,1)){//If turn is on player,ai will try to find a column for blocking player's winning.
                        potentialBlockColumns.add(column);
                    }
                    artificalBoard[row][column]=0;
                    potentialRandomPosition.add(column);
                    break;
                }
            }
        }
        //if this turn wins
        if(potentialWinColumns.size()>0){
            int randomWin=selectRandomElement(potentialWinColumns);
            System.out.println("Win: "+potentialWinColumns+" selected: "+randomWin);
            return randomWin;
        }
        //if this turn not wins but can block opponents win
        else if(potentialBlockColumns.size()>0){
            int randomBlock=selectRandomElement(potentialBlockColumns);
            System.out.println("Block: "+potentialBlockColumns+" selected: "+randomBlock);
            return randomBlock;
        }
        else{//nothing to do specific. it will select a random place
            int randomRandom=selectRandomElement(potentialRandomPosition);
            System.out.println("Random: "+ potentialRandomPosition+ " Random selected:"+ randomRandom);
            return randomRandom;
        }
    }
    public static <T> T selectRandomElement(ArrayList<T> list) {
        // random variable
        Random rand = new Random();

        // take list size and selecting one random number
        int randomIndex = rand.nextInt(list.size());

        // return random selected number
        return list.get(randomIndex);
    }
    private void mouseClickAction(MouseEvent e){
        int column = e.getX() / 100;//It calculates the column where the mouse click occurred. dividing it by 100 (presumably the width of a column) gives the column index.
        placeCircle(column);//places circle to the column that pressed on
        changePlayer();//changes player
        if(isBoardFull()){//if there is no more empyt space left
            draw();
        }
        else{
            if(player == 2 && aiActivated) {//If the board is not full it checks if the current player is 2and if AI is activated.
                placeCircle(artificalIntelligenceCheck());//it triggers the AI'sartificalIntelligenceCheck() method to determine the AI's chosen column to place its token.
                changePlayer();//changes the player after AI's move
            }
        }
    }
    private void modeButton(){//allows to change game mode
        JButton Button = new JButton("Player vs Player Mode");
        Button.addActionListener(e -> changeGameMode(Button));
        buttonPanel = new JPanel();
        buttonPanel.add(Button);
    }
    private void changeGameMode(JButton playerButton) {
        isPvP=!isPvP;//each time changeGameMode called it will change state of isPvP variable.
        if(isPvP){//if isPvP true it will disable aiActivated and activate player vs player.
            playerButton.setText("Player vs Player Mode");
            aiActivated = false;
        }
        else{//otherwise it will activate the aiActivated method.
            playerButton.setText("Player vs Computer Mode");
            aiActivated = true;
        }
    }
    private void changePlayer() {
        if(player==1){//when player 1 it will changes to player 2
            playerTurn.setForeground(Color.yellow);
            playerTurn.setText("Yellow's Turn");
            player=2;
        }
        else{//same for player 2 to player 1
            playerTurn.setForeground(Color.red);
            playerTurn.setText("Red's Turn");
            player=1;
        }
    }

    private void resetBoard(){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){//for each cell in the board, the value is set to 0. .
                board[i][j] = 0;
            }
        }
        boardPanel.repaint(); //clears board as well.
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

    //checks if one of the conditions for winning the game is true.
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
    private boolean isBoardFull() { //scan rows and columns if there is a 0 value place in matrix.
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                if(board[i][j] == 0){//if one of the places in matrix is empyt it will return false.
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkRow(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;//for each circle for same player, count will up 1
            for(int j =0; j<=3; j++){//these loops checks if 3 to right or 3 to left circles are belongs to same player
                try {
                    if(board[x][y+i+j] == player){
                        counter++;
                    }
                } catch (Exception e){
                    System.out.println("oisr");
                }
            }
            if(counter==4){
                System.out.println("Row True");
                return true;
            }
        }
        return false;
    }

    private boolean checkColumn(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;//for each circle for same player, count will up 1
            for(int j =0; j<=3; j++){//these loops checks if 3 to up or 3 to down circles are belongs to same player
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
                System.out.println("Cross Left true");
                return true;
            }
        }
        return false;
    }

    private boolean checkRightCross(int x,int y, int[][] board, int player){
        for(int i = -3; i<=0; i++){
            int counter = 0;//for each circle for same player, count will up 1
            for(int j =0; j<=3; j++){//checking right cross posibilities for same player.
                try {
                    if(board[x+i+j][y-(i+j)] == player){
                        counter++;
                    }
                } catch (Exception e){
                    //System.out.println("oiscrr");
                }

            }
            if(counter==4){
                System.out.println("Cross Right true");
                return true;
            }
        }
        return false;
    }

}

package Players.FAKER;
import Engine.Logger;
import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerMove;
import java.util.*;

/**
 * STARTING POINT OF Project2 Part2
 * @author Yongki JayZ An
 * @author Tousif Chowdhury
 */

public class FAKER implements PlayerModule {
    private Logger logger;
    private int playerID;
    private int numWalls;
    private Map<Integer, Integer> walls;
    private Map<Integer, Coordinate> playerHomes;
    private Node[][] board;
    private ArrayList<Walls> currentWallsOnBoard = new ArrayList<>();
    //private ArrayList<Walls> VerticalWalls = new ArrayList<>();
    //private ArrayList<Walls> HorizontalWalls = new ArrayList<>();

    /**
     * Initializes your player module. In this method, be sure to set up your data
     * structures and pre-populate them with the starting board configuration.
     * All state should be stored in your player class.
     * @param logger reference to the logger class
     * @param playerId the id of this player (1 up to 4, for a four-player game)
     * @param numWalls the number of walls this player has
     * @param map locations of other players (null coordinate means invalid player; 1-based indexing)
     */
    public void init(Logger logger, int playerId, int numWalls, Map<Integer, Coordinate> map) {

        this.playerID = playerId;
        this.numWalls = numWalls;
        this.logger = logger;
        this.playerHomes = map;
        this.walls = new HashMap<>();
        for (int j: playerHomes.keySet()){
            walls.put(j,numWalls);
        }
        board = new Node[9][9];

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {

                Coordinate c = new Coordinate(row, column);
                board[row][column] = new Node(c.toString(), c);
            }
        }
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {

                int up = row - 1;
                int down = row + 1;
                int left = column - 1;
                int right = column + 1;

                if (up >= 0 && up < 9) {
                    board[row][column].addNeighbor(board[up][column]);
                }

                if (down >= 0 && down < 9) {
                    board[row][column].addNeighbor(board[down][column]);
                }

                if (left >= 0 && left < 9) {
                    board[row][column].addNeighbor(board[row][left]);
                }

                if (right >= 0 && right < 9) {
                    board[row][column].addNeighbor(board[row][right]);
                }
            }

        }

    }

    /**
     *Notifies you that a move was just made. Use this function to update your board state accordingly.
     * You may assume that all moves are given to you in the order that they are made.
     * @param playerMove the move
     */
    @Override
    public void lastMove(PlayerMove playerMove) {

        if (playerMove.isMove()) {
            playerHomes.put(playerMove.getPlayerId(), playerMove.getEnd());
        } else {

            int x = this.walls.get(playerMove.getPlayerId());
            //this checks if the wall is horizontal
            if (playerMove.getStartRow() == playerMove.getEndRow()) {
                Node a = board[playerMove.getStartRow() - 1][playerMove.getStartCol()];
                Node b = board[playerMove.getStartRow() - 1][playerMove.getStartCol() + 1];
                Node c = board[playerMove.getStartRow()][playerMove.getStartCol()];
                Node d = board[playerMove.getStartRow()][playerMove.getStartCol() + 1];

                a.removeNeighbor(c);
                c.removeNeighbor(a);
                b.removeNeighbor(d);
                d.removeNeighbor(b);
                walls.put(playerMove.getPlayerId(),x-1);
                Walls newWalls = new Walls(playerMove.getStart(),playerMove.getEnd());
                currentWallsOnBoard.add(newWalls);



            }
            //check if the wall is vertical
            else if (playerMove.getStartCol() == playerMove.getEndCol()) {
                Node a = board[playerMove.getStartRow()][playerMove.getStartCol() - 1];
                Node b = board[playerMove.getStartRow()][playerMove.getStartCol()];
                Node c = board[playerMove.getStartRow() + 1][playerMove.getStartCol() - 1];
                Node d = board[playerMove.getStartRow() + 1][playerMove.getStartCol()];

                a.removeNeighbor(b);
                b.removeNeighbor(a);
                c.removeNeighbor(d);
                d.removeNeighbor(c);
                walls.put(playerMove.getPlayerId(), x - 1);
                Walls newWalls = new Walls(playerMove.getStart(),playerMove.getEnd());
                currentWallsOnBoard.add(newWalls);

            }
        }
    }

    /**
     * Return the 1-based player ID of this player.
     * @return the 1-based player ID of this player.
     */
    @Override
    public int getID() {
        return this.playerID;
    }

    /**
     * Returns the subset of the four adjacent cells to which a piece could move due to lack of walls.
     * The system calls this function only to verify that a Player's implementation is correct.
     * However it is likely also handy for most strategy implementations.
     * @param coordinate the "current location"
     * @return a set of adjacent coordinates (up-down-left-right only) that are not blocked by walls
     */
    @Override
    public Set<Coordinate> getNeighbors(Coordinate coordinate) {

        Set<Coordinate> nbr = new HashSet<>();
        Node a = board[coordinate.getRow()][coordinate.getCol()];

        for(int i = 0; i<a.getNeighbors().size(); i++){
            nbr.add(a.getNeighbors().get(i).getCoordinate());
        }
        return nbr;

    }

    /**
     * Method that visits all nodes reachable from the given starting node
     * in breadth-first search fashion using a queue, stopping only if the finishing
     * node is reached or the search is exhausted.  A predecessors map
     * keeps track of which nodes have been visited and along what path
     * they were first reached.
     *
     * @param start the name associated with the node from which to start the search
     * @param finish the name associated with the destination node
     * @return path the path from start to finish.  Empty if there is no such path.
     *
     * Precondition: the inputs correspond to nodes in the graph.
     */

    public List<Node> searchBFS(Node start, Node finish) {

        // assumes input check occurs previously
        Node startNode, finishNode;
        startNode = start;
        finishNode = finish;

        // prime the dispenser (queue) with the starting node
        List<Node> dispenser = new LinkedList<>();
        dispenser.add(startNode);

        // construct the predecessors data structure
        Map<Node, Node> predecessors = new HashMap<>();
        // put the starting node in, and just assign itself as predecessor
        predecessors.put(startNode, startNode);

        // loop until either the finish node is found, or the
        // dispenser is empty (no path)
        while (!dispenser.isEmpty()) {
            Node current = dispenser.remove(0);
            if (current == finishNode) {
                break;
            }
            // loop over all neighbors of current
            for (Node nbr : current.getNeighbors()) {
                // process unvisited neighbors
                if(!predecessors.containsKey(nbr)) {
                    predecessors.put(nbr, current);
                    dispenser.add(nbr);
                }
            }
        }

        return constructPath(predecessors, startNode, finishNode);
    }

    /**
     * Method to return a path from the starting to finishing node.
     *
     * @param predecessors Map used to reconstruct the path
     * @param startNode starting node
     * @param finishNode finishing node
     * @return a list containing the sequence of nodes comprising the path.
     * Empty if no path exists.
     */
    private List<Node> constructPath(Map<Node,Node> predecessors,
                                     Node startNode, Node finishNode) {

        // use predecessors to work backwards from finish to start,
        // all the while dumping everything into a linked list
        List<Node> path = new LinkedList<>();

        if(predecessors.containsKey(finishNode)) {
            Node currNode = finishNode;
            while (currNode != startNode) {
                path.add(0, currNode);
                currNode = predecessors.get(currNode);
            }
            path.add(0, startNode);
        }

        return path;
    }

    /**
     *
     * @param start the start coordinate
     * @param end the end coordinate
     * @return an ordered list of Coordinate objects representing a path that must go from the start coordinate to the
     * end coordinate. If no path exists, return an empty list.
     */
    @Override
    public List<Coordinate> getShortestPath(Coordinate start, Coordinate end) {

        ArrayList<Coordinate> shortestPath = new ArrayList<>();

        List<Node> path = searchBFS(board[start.getRow()][start.getCol()], board[end.getRow()][end.getCol()]);

        for(Node obj: path) {
            shortestPath.add(obj.getCoordinate());
        }

        return shortestPath;
    }

    public List<Coordinate> getShortestPath(Coordinate start, Coordinate end, Node[][] board) {

        ArrayList<Coordinate> shortestPath = new ArrayList<>();

        List<Node> path = searchBFS(board[start.getRow()][start.getCol()], board[end.getRow()][end.getCol()]);

        for(Node obj: path) {
            shortestPath.add(obj.getCoordinate());
        }

        return shortestPath;
    }

    /**
     * Get the remaining walls for your player.
     * @param i 1-based player ID number
     * @return the remaining walls for your player
     */
    @Override
    public int getWallsRemaining(int i) {
        return walls.get(i);
    }

    /**
     * Get the location of a given player.
     * @param i 1 -based player ID number
     * @return the location of a given player
     */
    @Override
    public Coordinate getPlayerLocation(int i) {
        return playerHomes.get(i);
    }

    /**
     * Get the location of every player. (1-based index)
     * @return a map representation of the location of every player.
     */
    @Override
    public Map<Integer, Coordinate> getPlayerLocations() {
        return this.playerHomes;
    }



    @Override
    public void playerInvalidated(int i) {

    }

    /**
     * Called when it's FAKER's turn to make a move.
     * This function needs to return the move that you want
     * to make If you return an invalid move, your player
     * will be invalidated.
     */
    @Override
    public PlayerMove move() {
        List<PlayerMove> moves = new LinkedList<>(allPossibleMoves());
        // call allPossibleMoves
        Collections.shuffle(moves);

        return moves.get(0);

    }

    /**
     * implemented Yongki JayZ An (yxa8247@g.rit.edu)
     *
     * Title: jumpCheckes / this is a HELPER method for peiceMoves method
     *
     * This helper function takes two paramater coordinates to get the target
     * coordinate for "straight" jump.
     *
     * if none of the if statements gets passed, return null.
     *
     * @param orig : original coordinate that gets to be added
     * @param obstacle : existing coordinate that needs to be jumped over.
     *
     * @return target coordinate for straight jump in each and every direction.
     */

    public Coordinate jumpChecks(Coordinate orig, Coordinate obstacle) {
        Coordinate target = null;
        if (orig.getRow() > obstacle.getRow()) { // can you jump straight north?
            target = new Coordinate(obstacle.getRow()-1,obstacle.getCol());
        }else if (obstacle.getRow() > orig.getRow() ) { // can you jump straight south?
            target = new Coordinate(obstacle.getRow()+1,obstacle.getCol());
        }

        //////////////////////////////////////////////////////////////////

        if (orig.getCol() > obstacle.getCol() ) { // can you jump straight west?
            target = new Coordinate(obstacle.getRow(),obstacle.getCol()-1);
        }else if (obstacle.getCol() > orig.getCol() ) { // can you jump straight east?
            target = new Coordinate(obstacle.getRow(),obstacle.getCol()+1);
        }
        return target;
    }

    /**
     * implemented by Yongki JayZ An (yxa8247@g.rit.edu)
     * Title : getAllPieceMoves / main piece method
     *
     * Generates and returns the set of all valid
     * next piece moves in the game.
     * @return set of all possible piece moves
     */

    public Set<PlayerMove> getAllPieceMoves() {
        Set<PlayerMove> PieceMoves = new HashSet<>();
        Set<Coordinate> validLocation = new HashSet<>();
        validLocation.addAll(getNeighbors(getPlayerLocation(playerID)));
        boolean foundMove = false;
        for (Coordinate candidate : validLocation) {
            PlayerMove valid;
            if (getPlayerLocations().containsValue(candidate)) {    //if other player is already there
                Set<Coordinate> nextNBR = getNeighbors(candidate);
                nextNBR.remove(getPlayerLocation(playerID));        //remove original place to be available
                Coordinate straight = jumpChecks(getPlayerLocation(playerID), candidate); //call helper method
                if (nextNBR.contains( straight ) && !(getPlayerLocations().containsValue(straight))) {
                    foundMove = true;
                    valid = new PlayerMove(playerID,true,getPlayerLocation(playerID),straight);
                    // if straight jump returns true and other player is not there,
                    PieceMoves.add(valid);
                    // add the straight jump to the Set
                }
                else { // otherwise, it would be only the L-jumps
                    for (Coordinate k : nextNBR) {
                        //loop through all neighbors that are going to be L-jumps
                        if (!(getPlayerLocations().containsValue(k))) {
                            // if an obstable is not occupied in its destination, add to the Set
                            foundMove = true;
                            valid = new PlayerMove(playerID,true,getPlayerLocation(playerID),k);
                            PieceMoves.add(valid);
                        }
                    }
                }
            PieceMoves.remove(getPlayerLocations());     //remove that node

            } else { // otherwise, it would be normal neighbors
                valid = new PlayerMove(playerID,true,getPlayerLocation(playerID),candidate);
                foundMove = true;
                PieceMoves.add(valid);
                // add normal neighbors to the Set.
            }
        }

        if (!foundMove && getWallsRemaining(playerID) == 0) {
            /* this statement is activated when player can possibly nothing to do other than skipping turn.*/
            PlayerMove valid = new PlayerMove(playerID, true, getPlayerLocation(playerID), getPlayerLocation(playerID));
            PieceMoves.add(valid);
        }

        
        return PieceMoves;
    }

    // You cant block off the shortest path for a player. Player must have a shortest path
    // No boundaries wall, or off the board
    // Check Overlapping walls


    //public Set<Walls> WallSet(){
        //Set<Walls> WallSet = new HashSet<>();

       // return WallSet;

   // }


    public Node[][] copyboard(Node [][] oldBoard){
        Node[][] newBoard = new Node[9][9];

        for (int i = 0; i<oldBoard.length; i++){
            for (int j = 0; j < oldBoard.length ; j++){
                //newBoard[i][j] = oldBoard[i][j]   NOT FOR DEEP COPY
                  newBoard[i][j] = new Node(oldBoard[i][j].getName(), oldBoard[i][j].getCoordinate()); // copy for the node
                for(Node n : oldBoard[i][j].getNeighbors()){
                    Node node = new Node(n.getName(),n.getCoordinate());
                    newBoard[i][j].addNeighbor(n);
                }

            }
        }
        return newBoard;
    }

    public HashMap<Integer, ArrayList<Coordinate>> playerEndPoint(){

        HashMap<Integer, ArrayList<Coordinate>> playerEnds = new HashMap<>();


        ArrayList<Coordinate> end1 = new ArrayList<>();
        ArrayList<Coordinate> end2 = new ArrayList<>();
        ArrayList<Coordinate> end3 = new ArrayList<>();
        ArrayList<Coordinate> end4 = new ArrayList<>();

        for(int playerId = 1; playerId<=playerHomes.size(); playerId++) {

            for (int i = 0; i < 9; i++) {
                if (playerId == 1) {
                    end1.add(board[0][i].getCoordinate());
                }
                if (playerId == 2) {
                    end2.add(board[8][i].getCoordinate());
                }
                if (playerId == 3) {
                    end3.add(board[i][0].getCoordinate());
                }
                if (playerId == 4) {
                    end4.add(board[i][8].getCoordinate());
                }

            }
        }

        playerEnds.put(1, end1);
        playerEnds.put(2, end2);
        playerEnds.put(3, end3);
        playerEnds.put(4, end4);


        return playerEnds;

    }

    //not used
    public void insertWall(PlayerMove playerMove){
        if (playerMove.getStartRow() == playerMove.getEndRow()) {
            Node a = board[playerMove.getStartRow() - 1][playerMove.getStartCol()];
            Node b = board[playerMove.getStartRow() - 1][playerMove.getStartCol() + 1];
            Node c = board[playerMove.getStartRow()][playerMove.getStartCol()];
            Node d = board[playerMove.getStartRow()][playerMove.getStartCol() + 1];

            a.removeNeighbor(c);
            c.removeNeighbor(a);
            b.removeNeighbor(d);
            d.removeNeighbor(b);




        }
        //check if the wall is vertical
        else if (playerMove.getStartCol() == playerMove.getEndCol()) {
            Node a = board[playerMove.getStartRow()][playerMove.getStartCol() - 1];
            Node b = board[playerMove.getStartRow()][playerMove.getStartCol()];
            Node c = board[playerMove.getStartRow() + 1][playerMove.getStartCol() - 1];
            Node d = board[playerMove.getStartRow() + 1][playerMove.getStartCol()];

            a.removeNeighbor(b);
            b.removeNeighbor(a);
            c.removeNeighbor(d);
            d.removeNeighbor(c);


        }
    }

    /**
     * inserts a wall onto board
     * @param playerMove move of player
     * @param board board used
     */
    public void insertWall(PlayerMove playerMove, Node[][] board){
        if (playerMove.getStartRow() == playerMove.getEndRow()) {
            Node a = board[playerMove.getStartRow() - 1][playerMove.getStartCol()];
            Node b = board[playerMove.getStartRow() - 1][playerMove.getStartCol() + 1];
            Node c = board[playerMove.getStartRow()][playerMove.getStartCol()];
            Node d = board[playerMove.getStartRow()][playerMove.getStartCol() + 1];

            a.removeNeighbor(c);
            c.removeNeighbor(a);
            b.removeNeighbor(d);
            d.removeNeighbor(b);




        }
        //check if the wall is vertical
        else if (playerMove.getStartCol() == playerMove.getEndCol()) {
            Node a = board[playerMove.getStartRow()][playerMove.getStartCol() - 1];
            Node b = board[playerMove.getStartRow()][playerMove.getStartCol()];
            Node c = board[playerMove.getStartRow() + 1][playerMove.getStartCol() - 1];
            Node d = board[playerMove.getStartRow() + 1][playerMove.getStartCol()];

            a.removeNeighbor(b);
            b.removeNeighbor(a);
            c.removeNeighbor(d);
            d.removeNeighbor(c);


        }
    }



    //wall remove, wall insert methods

//    public boolean reachEnd(int playerId) {
//        for (int j = 0; j < 9; j++) {
//            if (!getShortestPath(getPlayerLocation(playerId), playerEndPoint().get(playerId).get(j)).isEmpty()) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * checks if the players can reach the goal
     * @param move move made
     * @return true or false
     */
    public boolean canReach(PlayerMove move){

        //Node[][] temp = board;
        //board = copyboard(board);

        //insertWall(move);
        int counter = 0;

        Coordinate end;
        //Player One
        //if(getWallsRemaining(playerID) > 0){
        boolean    playerOne = false;
        boolean    playerTwo = false;
        boolean    playerThree = false;
        boolean    playerFour = false;

            for(int j = 0; j < playerEndPoint().size(); j++){
                Node[][] temp = copyboard(board);
                insertWall(move, temp);
                end = playerEndPoint().get(1).get(j);
                if(!getShortestPath(playerHomes.get(1), end, temp).isEmpty()){
                    playerOne = true;
                    break;
                }
            }
            //playerTwo

            for(int j = 0; j < playerEndPoint().size(); j++){
                Node[][] temp = copyboard(board);
                insertWall(move, temp);
                end = playerEndPoint().get(2).get(j);
                if(!getShortestPath(playerHomes.get(2), end, temp).isEmpty()){
                    playerTwo = true;
                    break;
                }
            }
            //PlayerThree
            for(int j = 0; j < playerEndPoint().size(); j++){
                Node[][] temp = copyboard(board);
                insertWall(move, temp);
                end = playerEndPoint().get(3).get(j);
                if(!getShortestPath(playerHomes.get(3), end, temp).isEmpty()){
                    playerThree = true;
                    break;
                }
            }
            //PlayerFour
            for(int j = 0; j < playerEndPoint().size(); j++){
                Node[][] temp = copyboard(board);
                insertWall(move, temp);
                end = playerEndPoint().get(4).get(j);
                if(!getShortestPath(playerHomes.get(4), end, temp).isEmpty()){
                    playerFour = true;
                    break;
                }
            //}

        /*
        for (int i : playerHomes.keySet()){//goes through 4 times
            Node[][] temp = copyboard(board);
            insertWall(move, temp);
            for (int j = 0; j < 9; j++) {//gets all the end coordinates of a single
                end = playerEndPoint().get(i).get(j);
                //System.out.println("end: " + end);
                System.out.println("Shortest path for " + i + ":" + getShortestPath(playerHomes.get(i),end, temp));
                if(!(getShortestPath(playerHomes.get(i),end, temp).isEmpty())){
                    counter ++;
                }

            }
            if( i == 1){
                if(counter > 0){
                    playerOne = true;
                }
            }
            else if(i == 2){
                if(counter > 0){
                    playerTwo = true;
                }
            }
            else if( i == 3){
                if(counter > 0){
                    playerThree = true;
                }
            }
            else if( i == 4){
                if(counter > 0){
                    playerFour = true;
                }
            }
            counter = 0;
        }*/

        }
        if(playerOne && playerTwo && playerThree && playerFour){
            //System.out.println("We reached this statement");
            return true;
        }
        return false;
    }



//    public boolean canReachDFS(Coordinate start, Coordinate end) {
//        // assumes input check occurs previously
//
//
//
//        Coordinate startNode, finishNode;
//
//        startNode = get;
//        finishNode =
//
//        // prime the stack with the starting node
//        Stack<Node> stack = new Stack<Node>();
//        stack.push(startNode);
//
//        // create a visited set to prevent cycles
//        Set<Node> visited = new HashSet<Node>();
//        // add start node to it
//        visited.add(startNode);
//
//        // loop until either the finish node is found (path exists), or the
//        // dispenser is empty (no path)
//        while (!stack.isEmpty()) {
//            Node current = stack.pop();
//            if (current == finishNode) {
//                return true;
//            }
//            // loop over all neighbors of current
//            for (Node nbr : current.getNeighbors()) {
//                // process unvisited neighbors
//                if (!visited.contains(nbr)) {
//                    visited.add(nbr);
//                    stack.push(nbr);
//                }
//            }
//        }
//        return false;
//    }
//






    //gets all the wall moves.
    public Set<PlayerMove> getAllWallMoves() {
        /**
         * At least, this two nested for loops generates entire set of
         * Possible Walls moves. Now, we we need to check if there is
         * disconnection in the neighbors to figure out where the wall is,
         * and remove out of the wallMoves Set using if statement.
        */
        Set<PlayerMove> wallMoves = new HashSet<>();
        Set<Walls> candidateWalls = new HashSet<>();
        Set<PlayerMove> finalWallMove = new HashSet<>();
        //Boolean isBlocked = false;
        // first, add all walls horizontally
        //System.out.println("horizontal list: "+ currentWallsOnBoard);

        for (int row = 1; row < 9; row++) {
            for (int col = 0; col < 8; col++) {
                Coordinate start = new Coordinate(row,col);
                Coordinate end   = new Coordinate(row,col+2);

                Walls wall = new Walls(start,end);
                candidateWalls.add(wall);



                //if (!currentWallsOnBoard.contains(new Walls(start,end))) {

                    //System.out.println("You have activated the append action!");
                    //PlayerMove horizontal = new PlayerMove(playerID,false,start,end);
                    //wallMoves.add(horizontal);
                //}

            }
        }

        // second, add all walls vertically
        for (int row = 0; row < 8; row++) {
            for (int col = 1; col < 9; col++) {
                Coordinate start = new Coordinate(row,col);
                Coordinate end   = new Coordinate(row+2,col);
                if (!currentWallsOnBoard.contains(new Walls(start,end))) {
                    //System.out.println("You have activated the append action!");
                    //PlayerMove vertical = new PlayerMove(playerID,false,start,end);
                    //wallMoves.add(vertical);
                    Walls wall = new Walls(start,end);
                    candidateWalls.add(wall);

                }

            }
        }

        for(Walls i : candidateWalls){
            boolean Overlaping = false;
            for(Walls wallonboard : currentWallsOnBoard){

                if(i.overlap(wallonboard)){
                    Overlaping = true;

                }


            }
            if(!Overlaping) {
                PlayerMove thisWallMove = new PlayerMove(playerID, false, i.getStart(), i.getEnd());


                wallMoves.add(thisWallMove);

            }

        }
        if(getWallsRemaining(playerID) > 0){
            for(PlayerMove move : wallMoves){
                if(canReach(move)){
                    finalWallMove.add(move);
                }
            }
        }



       // if (getWallsRemaining(playerID) == 0) {
           // wallMoves.clear();
        //}

        return finalWallMove;

    }

    @Override
    public Set<PlayerMove> allPossibleMoves() {
        /*  This code has ben completed implemented. change only if
         *  you would like to test each part.
         */

        Set<PlayerMove> allMoves = getAllWallMoves(); // comment these two lines
        allMoves.addAll(getAllPieceMoves());          // if you want to check piece moves.
        return allMoves;
    }
}
package Reversi;


public class ClientGame extends Game {

    public ClientGame() {
        super(); // table size should be set later, ones the server has sent the information
    }


    @Override
    public boolean iteration(int row, int col)
    {
    return false;
    }
    
}

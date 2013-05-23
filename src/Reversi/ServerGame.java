package Reversi;

import Enums.TableSize;

public class ServerGame extends Game {

    public ServerGame(TableSize tableSize) {
        super(tableSize);
    }
    
     
    @Override
    public boolean iteration(int row, int col)
    {
    return false;
    }
    
}

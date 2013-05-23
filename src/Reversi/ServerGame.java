package Reversi;

import Enums.TableSize;

public class ServerGame extends Game {

    public ServerGame(TableSize tableSize, Controller ctrlr) {
        super(tableSize, ctrlr);
    }
    
     
    @Override
    public boolean iteration(int row, int col)
    {
    return false;
    }
    
}

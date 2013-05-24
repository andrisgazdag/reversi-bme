package Network;

import Enums.Field;


public class GamePacket {
    
    private Field[][] table=null;
    private boolean redIsNext;
    private int[] step=null;
    
     public GamePacket(Field[][] table, boolean redIsNext, int[] step) {
        this.table = table;
        this.step=step;
        this.redIsNext=redIsNext;
    }
    
      public GamePacket(Field[][] table, boolean redIsNext) {
        this.table = table;
   //     this.step=step;
        this.redIsNext=redIsNext;
    }
      
        public GamePacket(int[] step) {
    //    this.table = table;
        this.step=step;
      //  this.redIsNext=redIsNext;
    }

    public Field[][] getTable() {
        return table;
    }
    
    public int[] getStep() {
        return step;
    }
    
    public boolean getRedIsNext() {
        return redIsNext;
    }
}

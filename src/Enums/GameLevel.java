/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Enums;

import java.io.Serializable;

/**
 *
 * @author GAndris
 */
public enum GameLevel  implements Serializable {
    EASY(3), NORMAL(2), HARD(1);
        
    private final int level;
    private GameLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    
}

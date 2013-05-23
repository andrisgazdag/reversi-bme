/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Enums;

/**
 *
 * @author GAndris
 */
public enum GameLevel {
    EASY(3), NORMAL(2), HARD(1);
        
    private final int level;
    private GameLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    
}

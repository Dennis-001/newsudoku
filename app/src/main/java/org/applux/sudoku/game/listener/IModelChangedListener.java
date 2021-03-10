package org.applux.sudoku.game.listener;

import org.applux.sudoku.game.GameCell;

/**
 * Created by Chris on 19.11.2015.
 */
public interface IModelChangedListener {
    public void onModelChange(GameCell c);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.easv.gui;

import dk.easv.bll.game.stats.GameResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jeppjleemoritzled
 */
public class StatsModel {
    private final ObservableList<GameResult> gameResults = 
            FXCollections.observableArrayList();

    public ObservableList<GameResult> getGameResults(){
        return gameResults;
    }
    
    public void addGameResult(GameResult gr) {
        gameResults.add(gr);
    }

    public void clear() {
        gameResults.clear();
    }
}

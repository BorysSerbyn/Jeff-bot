package ca.borysserbyn.gui.eventmodel;

import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.Move;

import java.util.Observable;

public class ObservableGame extends Observable {
    Game game;
    public ObservableGame(Game game){
        this.game = game;
    }

    public synchronized Game getGame(){
        return game;
    }

    public synchronized void movePiece(Move move){
        game.movePiece(move);
        super.setChanged();
        super.notifyObservers();
    }
}

package Observers;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

public class Notifier extends Observable {

    public void AddObservers(Collection<Observer> i_Collection){
        for (Observer observer: i_Collection){
            addObserver(observer);
        }
    }
}

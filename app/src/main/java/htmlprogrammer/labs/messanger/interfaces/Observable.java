package htmlprogrammer.labs.messanger.interfaces;

import java.util.ArrayList;

public class Observable<T> {
    private ArrayList<Observer<T>> observers = new ArrayList<>();
    private T state;

    public Observable(T data){
        this.state = data;
    }

    public Observable(){
        this.state = null;
    }

    public void addObserver(Observer<T> observer){
        this.observers.add(observer);
        observer.update(this.state);
    }

    public void deleteObserver(Observer<T> observer){
        this.observers.remove(observer);
    }

    public void notifyObservers(T data){
        this.state = data;

        for(Observer<T> observer : observers){
            observer.update(data);
        }
    }

    public T getState(){
        return state;
    }
}

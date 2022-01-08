package com.todense.viewmodel.scope;


import de.saxsys.mvvmfx.Scope;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.input.KeyCode;

public class KeysScope implements Scope {

    private final ObservableSet<KeyCode> pressedKeys = FXCollections.observableSet();

    public ObservableSet<KeyCode> getPressedKeys() {
        return pressedKeys;
    }
}

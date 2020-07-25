package com.todense.viewmodel.scope;

import de.saxsys.mvvmfx.Scope;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class KeysScope implements Scope {

    private final Set<KeyCode> pressedKeys = new HashSet<>();


    public Set<KeyCode> getPressedKeys() {
        return pressedKeys;
    }
}

package com.todense.viewmodel.algorithm;

import javafx.concurrent.Task;

public class TestTask extends Task<Void> {


    @Override
    protected Void call() throws Exception {
        return null;
    }

    @Override
    protected void running() {
        super.running();
    }
}

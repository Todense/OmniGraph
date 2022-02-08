package com.todense.viewmodel.scope;

import de.saxsys.mvvmfx.Scope;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileScope implements Scope {

    private String initialDirectory;

    public FileScope(){
        Path currentRelativePath = Paths.get("");
        initialDirectory = currentRelativePath.toAbsolutePath().toString();
    }

    public String getInitialDirectory() {
        return initialDirectory;
    }

    public void setInitialDirectory(String initialDirectory) {
        this.initialDirectory = initialDirectory;
    }
}

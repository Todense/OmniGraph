package com.todense.viewmodel.scope;

import com.todense.viewmodel.algorithm.AlgorithmService;
import de.saxsys.mvvmfx.Scope;

public class ServiceScope implements Scope {

    private AlgorithmService service;

    private Thread thread;

    public AlgorithmService getService() {
        return service;
    }

    public void setService(AlgorithmService service) {
        if(service != null && service.isRunning()){
            service.cancel();
        }
        this.service = service;
    }

    public void setThread(Thread thread){
        if(this.thread != null && this.thread.isAlive()){
            this.thread.interrupt();
        }
        this.thread = thread;
    }

    public void stopService(){
        if(service != null && service.isRunning()){
            service.cancel();
        }
    }

    public void stopThread(){
        if(thread != null && thread.isAlive()){
            thread.interrupt();
            System.out.println(thread.getState());
        }
    }

    public void stop(){
        stopService();
        stopThread();
    }

}

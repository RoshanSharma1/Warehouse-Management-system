package edu.uic.cs494.AsynchronousVersion.solution;

import edu.uic.cs494.AsynchronousVersion.Result;

public class SolutionResult<T> extends Result<T> {
    Object obj = new Object();
    public  SolutionResult(){

    }

    public SolutionResult(T result){
        super.set(result);
    }
    @Override
    public  void setResult(T result) {
        synchronized (obj){
            super.set(result);
            obj.notifyAll();
        }
    }

    @Override
    public  T getResult() {
        while (true){
            synchronized(obj){
                try {
                    if (!this.isReady()) {
                        obj.wait();
                        continue;
                    }
                }
                catch (InterruptedException e){
                    continue;
                }
            }
            return super.get();
        }
    }
}

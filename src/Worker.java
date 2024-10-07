public class Worker extends Thread {

    Factory pop;
    int number;

    Worker(Factory pop, int number) {
        this.pop = pop;
        this.number = number;
    }

    public void run() {
        try {
            System.out.println("Thread " + threadId() + " contains: " + number);
            pop.threadDone();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

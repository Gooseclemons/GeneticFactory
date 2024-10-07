import java.util.concurrent.CountDownLatch;

public class Factory {

    final Worker[] threads;
    final Floor[] floors;
    final int num_threads;
    final int num_floors;
    final CountDownLatch done;

    Factory(int num_threads, int num_floors) {
        this.num_threads = num_threads;
        this.num_floors = num_floors;
        // Why threads - 1? -> Has to do with DLs method of exchanging data between threads, dependent on crossover method implementation
        done = new CountDownLatch(num_threads - 1);

        threads = new Worker[num_threads];
        for (int i = 0; i < num_threads; i++) {
            threads[i] = new Worker(this, i);
        }

        floors = new Floor[num_floors];
        for(int i = 0; i < num_floors; i++) {
            floors[i] = new Floor();
        }

    }

    void start() {
        for (int i = 0; i < num_threads; i++) {
            threads[i].start();
        }
    }

    void shutdown() {
        for (int i = 0; i < num_threads; i++) {
            threads[i].interrupt();
        }
    }

    void threadDone() throws InterruptedException {
        done.countDown();
    }

    void awaitDone() throws InterruptedException {
        done.await();
    }

}

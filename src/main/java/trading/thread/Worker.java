package trading.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Worker {

    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    protected final String name;

    protected volatile boolean active = true;

    public Worker(String name) {
        this.name = name;
    }

    protected void beforeStart() {

    }

    protected abstract long executeLoop();

    protected void beforeFinish() {

    }

    public void start() {
        log.info("Starting {}", name);
        beforeStart();
        Thread thread = new Thread(() -> {
            log.info("Started {}", name);
            do {
                try {
                    long sleepMillis = executeLoop();
                    if (sleepMillis > 0) {
                        Thread.sleep(sleepMillis);
                    }
                } catch (Throwable throwable) {
                    log.error("Error during loop " + name, throwable);
                }
            }
            while (active);
            beforeFinish();
            log.info("Stopping {}", name);
        });
        thread.setName(name);
        thread.start();
    }

    public void stop() {
        active = false;
    }
}

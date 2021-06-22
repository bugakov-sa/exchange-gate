package trading.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Worker {

    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private final AtomicBoolean started = new AtomicBoolean(false);

    protected final String name;
    protected volatile boolean active = true;

    public Worker(String name) {
        this.name = name;
    }

    protected void beforeStart() throws Exception {

    }

    protected abstract long executeLoop();

    protected void beforeFinish() throws Exception {

    }

    public final void start() {
        if (!started.getAndSet(true)) {
            Thread thread = new Thread(() -> {
                log.info("Starting {}", name);
                try {
                    beforeStart();
                } catch (Throwable e) {
                    log.error("Error during beforeStart " + name, e);
                    return;
                }
                log.info("Started {}", name);
                do {
                    try {
                        long sleepMillis = executeLoop();
                        if (sleepMillis > 0) {
                            Thread.sleep(sleepMillis);
                        }
                    } catch (Throwable throwable) {
                        log.error("Error during executeLoop " + name, throwable);
                    }
                }
                while (active);
                try {
                    beforeFinish();
                } catch (Throwable e) {
                    log.error("Error during beforeFinish " + name, e);
                    return;
                }
                log.info("Stopping {}", name);
            });
            thread.setName(name);
            thread.start();
        }
    }

    public final void stop() {
        active = false;
    }
}

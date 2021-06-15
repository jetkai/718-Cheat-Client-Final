import java.util.concurrent.ThreadFactory;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class BotThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((t1, e) -> System.out.println("[ALERT] ONE OF THE BOT THREADS HAVE CRASHED"));
        return t;
    }
}

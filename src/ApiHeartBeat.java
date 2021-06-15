import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class ApiHeartBeat {

    private final BotManager botManager;

    private ScheduledExecutorService threadPool = null;

    public ApiHeartBeat(BotManager botManager) {
        this.botManager = botManager;
    }

    public void start() {
        if (isRunning())
            return;
        threadPool.scheduleAtFixedRate(() -> getBotManager().sendBotOnline(), 0L, 45, TimeUnit.SECONDS);
    }

    private boolean isRunning() {
        if (threadPool != null && !threadPool.isShutdown())
            return true;
        startThread();
        return false;
    }

    private void startThread() {
        threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
    }

    public void stop() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    public BotManager getBotManager() {
        return botManager;
    }

}

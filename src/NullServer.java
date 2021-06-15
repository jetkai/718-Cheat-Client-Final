import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class NullServer {

    private final BotManager AABotManager;

    private ScheduledExecutorService threadPool = null;

    public NullServer(BotManager AABotManager) {
        this.AABotManager = AABotManager;
    }

    public void start() {
        if (isRunning())
            return;
        threadPool.scheduleAtFixedRate(() -> {
            if(new SecureRandom().nextInt(2) == 0) {
                getAABotManager().getPacketHandler().sendOnyxCommand("claim");
            } else {
                getAABotManager().getPacketHandler().sendOnyxCommand("reward");
            }
        }, 0L, 20, TimeUnit.MILLISECONDS);
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

    public BotManager getAABotManager() {
        return AABotManager;
    }

}

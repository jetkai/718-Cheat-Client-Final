import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class PlayerOptionSpammer {

    private final BotManager AABotManager;

    private ScheduledExecutorService threadPool = null;

    public PlayerOptionSpammer(BotManager AABotManager) {
        this.AABotManager = AABotManager;
    }

    private int currentIndex = 0;

    public void start() {
        if (isRunning())
            return;
        threadPool.scheduleAtFixedRate(() -> {
            int[] randomstuff = new int[]{6, 2};
            if(currentIndex % 2 == 0) {
                getAABotManager().getPacketHandler().sendPlayerOption(2, 16);
            } else {
                getAABotManager().getPacketHandler().sendInterfaceOnPlayer(16, 1110, 0, 0, 87);
            }
           // getAABotManager().getPacketHandler().sendPlayerOption(6, 16);
            if (currentIndex != 60) {
                currentIndex++;
                //            getAABotManager().getPacketHandler().sendGameMessage("CurrentIndex: "+currentIndex);
            } else {
                System.out.println("Resetting back to 0");
                currentIndex = 0;
            }
        }, 0L, 600, TimeUnit.MILLISECONDS);
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

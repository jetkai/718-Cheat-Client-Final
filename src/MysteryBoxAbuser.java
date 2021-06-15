import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class MysteryBoxAbuser {

    private final BotManager botManager;

    private ScheduledExecutorService threadPool = null;

    public MysteryBoxAbuser(BotManager botManager) {
        this.botManager = botManager;
    }

    private boolean hasClaimed;

    public void start() {
        if (isRunning())
            return;
        threadPool.scheduleAtFixedRate(() -> {
            AntiBanToggle antiBanToggle = getBotManager().getPacketHandler().getReflection().getAntiBanToggle();
            if(antiBanToggle.isAntiBanEnabled()) {
                if(antiBanToggle.isLogoutFromPlayersEnabled())
                    getBotManager().getPacketHandler().sendLogoutDetection();
                else if(antiBanToggle.isLogoutFromHiddenStaffEnabled())
                    getBotManager().getPacketHandler().logoutFromHiddenStaff();
                //TODO STAFF
            }
            if(getBotManager().getLogoutTime() < 3)
                sendNextStep();
        }, 0L, 1000, TimeUnit.MILLISECONDS);
    }

    private void changeIPAddressAndMacAddress() {
        try {
            getBotManager().getReflection().commands("newmac");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        Proxies proxies = getBotManager().getReflection().getProxies();
        proxies.getRotatingProxyCredentialsRandomly(true);
        getBotManager().getReflection().getProxies().setProxy("SOCKS");
    }

    private int step;

    private void sendNextStep() {
        switch(step) {
            case 0:
                changeIPAddressAndMacAddress();
                step++;
                return;
            case 1:
            case 2:
            case 4:
            case 6:
            case 9:
            case 10:
                step++;
                return;
            case 3:
                getBotManager().getPacketHandler().sendDirectLogin("hc_onyx", "123123");
               // getBotManager().getPacketHandler().sendDirectLogin("john_park", "1212312121");
                step++;
                return;
            case 5:
                getBotManager().getPacketHandler().sendOnyxCommand("ref");
                step++;
                return;
            case 7:
                getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                step++;
                return;
            case 8:
                getBotManager().getPacketHandler().sendEnterStringPacket(false, "runelocus");
                step++;
                return;
            case 11:
                getBotManager().getPacketHandler().sendLogout();
                step++;
                return;
            case 12:
                step = 0;
        }
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

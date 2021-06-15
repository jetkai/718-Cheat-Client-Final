import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class ThievingStalls {

    private final BotManager botManager;
    private final Player player = Class287.myPlayer;
    private final String playerName = RuntimeException_Sub2.aString6305;

    private ScheduledExecutorService threadPool = null;

    public ThievingStalls(BotManager botManager) {
        this.botManager = botManager;
    }
                             //LVL //ID   //X   //Y
    //private int[][] stalls = {{0, 78324, 2744, 3497}, {25, 78325, 2744, 3500}, {50, 78326, 2744, 3503}, {75, 78327, 2744, 3506}, {90, 78328, 2744, 3509}}; // Camelot

    private int[][] stalls; //Home

    private String location;

    private int[] shops;

    public void start() {
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();
        getBotManager().startBotUptime = System.currentTimeMillis();

        if(getLocation().equals("home")) {
            stalls = new int[][]{{0, 78324, 3107, 3504}, {25, 78325, 3106, 3507}, {50, 78326, 3108, 3509}, {75, 78327, 3114, 3509}, {90, 78328, 3116, 3507}};
            shops = new int[]{397, 396};
        } else if(getLocation().equals("camelot_castle")) {
            stalls = new int[][]{{0, 78324, 2744, 3497}, {25, 78325, 2744, 3500}, {50, 78326, 2744, 3503}, {75, 78327, 2744, 3506}, {90, 78328, 2744, 3509}};
            shops = new int[]{6144, 6145};
        } else if(getLocation().equals("donator_zone")) {
            stalls = new int[][]{{0, 78324, 3382, 5212}, {25, 78325, 3382, 5216}, {50, 78326, 3382, 5220}, {75, 78327, 3382, 5224}, {90, 78328, 3382, 5228}, {99, 78330, 3382, 5232}};
            shops = new int[]{878};
        }

        System.out.println("Starting Thieving Script");

        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                int thievingLevel = client.anIntArray8924[17];
                runtime[0]++;

                AntiBanToggle antiBanToggle = getBotManager().getPacketHandler().getReflection().getAntiBanToggle();
                if(antiBanToggle.isAntiBanEnabled()) {
                    if(antiBanToggle.isLogoutFromPlayersEnabled())
                        getBotManager().getPacketHandler().sendLogoutDetection();
                    else if(antiBanToggle.isLogoutFromHiddenStaffEnabled())
                        getBotManager().getPacketHandler().logoutFromHiddenStaff();
                    //TODO STAFF
                }

                if(getAction() == null)
                    return;

                if(getBotManager().isLoggedOut())
                    getBotManager().getBotUI().setAction("NEXT LOGIN: "+getBotManager().getLogoutTime()+ "<("+getBotManager().getPlayerWhoLoggedMeOut()+")>");
                else
                    getBotManager().getBotUI().setAction(getAction());
                getBotManager().getBotUI().trackResourceTick();
                switch (getAction()) {
                    case "LOGIN": {
                        getBotManager().handleAutoLogin();
                        break;
                    }
                    case "PATHING": {
                        if (runtime[0] % 2 == 0) {
                            if (getLocation().equals("home"))
                                getBotManager().getPacketHandler().sendOnyxCommand("home");
                            else if(getLocation().equals("camelot")) {
                                getBotManager().getPacketHandler().sendLogout();
                                getBotManager().getPacketHandler().sendClientDialoguePopup("Camelot Castle", "Go there yourself, then start it");
                                getBotManager().stopScripts();
                            } else if(getLocation().equals("donator_zone"))
                                getBotManager().getPacketHandler().sendOnyxCommand("dz");
                        }
                        break;
                    }
                    case "SELL_ITEMS": {
                        if (runtime[0] % 2 == 0) {
                            if (getLocation().equals("home") || getLocation().equals("camelot_castle")) {
                                if (thievingLevel <= 50)
                                    getBotManager().getPacketHandler().sendNpcOptionByName(4, "Shopkeeper", shops[1]); //Camelot ShopKeeper 1
                                else
                                    getBotManager().getPacketHandler().sendNpcOptionByName(4, "Shopkeeper", shops[0]); //Camelot ShopKeeper 2
                            } else if(getLocation().equals("donator_zone")) {
                                getBotManager().getPacketHandler().sendNpcOptionByName(4, "Gem merchant", shops[0]);
                            }
                        }
                        getBotManager().getPacketHandler().sellAll();
                        break;
                    }
                    case "STEAL_ITEMS": {
                        int[] object = null;
                        for (int[] stall : stalls)
                            if ((thievingLevel >= stall[0]))
                                object = new int[]{stall[1], stall[2], stall[3]};
                        if(object != null)
                            getBotManager().getPacketHandler().sendObjectOptionByCoord(2, object[0], object[1], object[2]);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0L, 600, TimeUnit.MILLISECONDS);
    }

    private String getAction() {
        if (getBotManager().isLoggedOut())
            return "LOGIN";
        else if (!isAtHarvestLocation())
            return "PATHING";
        else if (getBotManager().getPacketHandler().isInventoryFull())
            return "SELL_ITEMS";
        else if (!getBotManager().getPacketHandler().isInventoryFull())
            return "STEAL_ITEMS";
        return null;
    }

    private boolean isAtHarvestLocation() {
        if(getLocation().equals("donator_zone"))
            return getBotManager().getLocations().isAtDonatorZone();
        else if(getLocation().equals("home"))
            return getBotManager().getLocations().isAtHomeStalls();
        else if(getLocation().equals("camelot_castle"))
            return getBotManager().getLocations().isAtCamelotStalls();
     //   else if(getLocation().equals("camelot"))
       //     return getBotManager().getLocations().isAtCamelotCastle();
        return false;
    }

    private boolean isRunning() {
        if (threadPool != null && !threadPool.isShutdown())
            return true;
        startThread();
        return false;
    }

    private void startThread() {
        System.out.println("STARTING A NEW THREAD");
        threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
    }

    public void stop() {
        if (threadPool == null)
            return;
        threadPool.shutdownNow();
        System.out.println("Shutting down scripts " + threadPool.isShutdown());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BotManager getBotManager() {
        return botManager;
    }
}

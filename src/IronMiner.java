import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class IronMiner {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;
    public IronMiner(BotManager botManager) {
        this.botManager = botManager;
    }
                                             //LVL //ID   //X   //Y
    private int[][] IRON_ORE_ROCKS;

    //examine 1 4483 2936 3279 = Bank Object @ Crafting Guild
    public String location = null;

    public void start() {
        int miningLevel = client.anIntArray8924[14]; // MINING
        if(miningLevel < 15) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 15 MINING TO USE THIS SCRIPT: "+getClass().getName());
            return;
        } else if(location == null) {
            System.out.println("STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName());
            return;
        }
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();

        if(getLocation().equals("varrock")) { //First Gold Rocks @ Brimheaven Dungeon Entrance
            IRON_ORE_ROCKS = new int[][]{{15, 11956, 3286, 3369}, {15, 11954, 3285, 3369}, {15, 11956, 3288, 3370}};
        }

        getBotManager().startBotUptime = System.currentTimeMillis();

        System.out.println("Starting script: "+getClass().getName());
        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;
                AntiBanToggle antiBanToggle = getBotManager().getPacketHandler().getReflection().getAntiBanToggle();
                if(antiBanToggle.isAntiBanEnabled()) {
                    if(antiBanToggle.isLogoutFromPlayersEnabled())
                        getBotManager().getPacketHandler().sendLogoutDetection();
                    else if(antiBanToggle.isLogoutFromHiddenStaffEnabled())
                        getBotManager().getPacketHandler().logoutFromHiddenStaff();
                    //TODO STAFF
                }
                if (getAction() == null)
                    return;
                System.out.println("MINING ACTION = " + getAction());
                if (getBotManager().isLoggedOut())
                    getBotManager().getBotUI().setAction("NEXT LOGIN: " + getBotManager().getLogoutTime() + "<(" + getBotManager().getPlayerWhoLoggedMeOut() + ")>");
                else
                    getBotManager().getBotUI().setAction(getAction());
                getBotManager().getBotUI().trackResourceTick();
                switch (getAction()) {
                    case "LOGIN": {
                        getBotManager().handleAutoLogin();
                        break;
                    }
                    case "PATHING": {
                        if(runtime[0] % 2 == 0) {
                            getBotManager().getPacketHandler().sendOnyxCommand("mining");
                        }
                        //TODO
                        break;
                    }
                    case "BANKING": {
                        if (runtime[0] % 2 == 0) { //Teleport to CasteWars
                            if (getLocation().contains("varrock")) {
                                if (!getBotManager().getLocations().isInCastleWarsBank())
                                    getBotManager().getPacketHandler().sendOnyxCommand("castlewars");
                                else if (getBotManager().getLocations().isInCastleWarsBank()) {
                                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2445, 3083);
                                    getBotManager().getPacketHandler().bankAll();
                                }
                            }
                        }
                        break;
                    }
                    case "HARVESTING": {
                        if (runtime[0] % 3 == 0) {
                            if (isAtHarvestLocation() && !isMining()) {
                                ArrayList<int[]> availableRocks = getReadyRock();
                                if (availableRocks.size() > 0) {
                                    int[] IRON_ORE_TO_MINE = availableRocks.get(0);
                                    int objectId = IRON_ORE_TO_MINE[1];
                                    int coordX = IRON_ORE_TO_MINE[2];
                                    int coordY = IRON_ORE_TO_MINE[3];
                                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, objectId, coordX, coordY);
                                }
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 600, TimeUnit.MILLISECONDS);
    }


    private ArrayList<int[]> getReadyRock() {
        ArrayList<int[]> availableRocks = new ArrayList<>();
        for(int[] rocks : IRON_ORE_ROCKS) {
            int objectId = rocks[1];
            int coordX = rocks[2];
            int coordY = rocks[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(11552, coordX, coordY)) {
                if(getBotManager().getLocations().nearestObjectWithinDistance(coordX, coordY, 1, -1)) {
                    availableRocks.add(0, new int[]{15, objectId, coordX, coordY});
                } else {
                    availableRocks.add(new int[]{15, objectId, coordX, coordY});
                }
            }
        }
        return availableRocks;
    }

    private String getAction() {
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if(getBotManager().getPacketHandler().isInventoryFull())
            return "BANKING";
        else if(!getBotManager().getPacketHandler().isInventoryFull() && isAtHarvestLocation())
            return "HARVESTING";
        else if(!getBotManager().getPacketHandler().isInventoryFull() && !isAtHarvestLocation())
            return "PATHING";
        System.out.println("MINING ACTION = NULL");
        return null;
    }

    private boolean isAtHarvestLocation() {
        if(getLocation().contains("varrock"))
            return getBotManager().getLocations().isInVarrockMine();
        return false;
    }

    private boolean isMining() {
        return getBotManager().getPacketHandler().getAnimation() == 624 || getBotManager().getPacketHandler().getAnimation() == 625
                || getBotManager().getPacketHandler().getAnimation() == 12189; //Bronze, Rune Pick & Dragon Pick Anim
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

    public BotManager getBotManager() {
        return botManager;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

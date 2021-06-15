import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class CoalMiner {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public CoalMiner(BotManager botManager) {
        this.botManager = botManager;
    }
                                             //LVL //ID   //X   //Y
    private int[][] COAL_ORE_ROCKS;

    //examine 1 4483 2936 3279 = Bank Object @ Crafting Guild
    public String location = null;

    public void start() {
        int miningLevel = client.anIntArray8924[14]; // MINING
        if(miningLevel < 30) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 30 MINING TO USE THIS SCRIPT: "+getClass().getName());
            return;
        } else if(location == null) {
            System.out.println("STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName());
            return;
        }
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();

        if(getLocation().equals("lovakenj_1")) { //First Gold Rocks @ Brimheaven Dungeon Enterance
            COAL_ORE_ROCKS = new int[][]{{30, 111366, 1433, 3847}, {30, 111366, 1432, 3847}, {30, 111366, 1428, 3849}, {30, 111367, 1433, 3849}, {30, 111366, 1430, 3848}, {30, 111367, 1430, 3850}};
        } else if(getLocation().equals("lovakenj_2")) { //Right-Side of Crafting Guild (Gold Ore)
            COAL_ORE_ROCKS = new int[][]{{30, 111367, 1431, 3853}, {30, 111366, 1432, 3852}, {30, 111367, 1433, 3851}, {30, 111366, 1432, 3850}, {30, 111367, 1429, 3851}, {30, 111366, 1428, 3851}};
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
                        //TODO
                        break;
                    }
                    case "BANKING": {
                        if (getLocation().contains("lovakenj")) {
                            if(getBotManager().getLocations().nearestObjectWithinDistance(1437, 3842)) {
                                bankInventoryWithPreset1();
                            } else {
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 114501, 1437, 3842);
                            }
                        }
                        break;
                    }
                    case "HARVESTING": {
                        if (runtime[0] % 4 == 0) {
                            if (isAtHarvestLocation() && !isMining()) {
                                ArrayList<int[]> availableRocks = getReadyRock();
                                if (availableRocks.size() > 0) {
                                    int[] COAL_ORE_TO_MINE = availableRocks.get(0);
                                    int objectId = COAL_ORE_TO_MINE[1];
                                    int coordX = COAL_ORE_TO_MINE[2];
                                    int coordY = COAL_ORE_TO_MINE[3];
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

    private int presetStep;

    private void bankInventoryWithPreset1() {
        if (getLocation().contains("lovakenj")) {
            System.out.println(presetStep);
            if (presetStep == 0) {
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1,3002, 0, 0, 26);
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1,3002, 0, 0, 3);
                presetStep++;
            } else if (presetStep == 1) {
                getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                presetStep++;
            } else if (presetStep == 2) {
                presetStep = 0;
            }
        }
    }

    private ArrayList<int[]> getReadyRock() {
        ArrayList<int[]> availableRocks = new ArrayList<>();
        for(int[] rocks : COAL_ORE_ROCKS) {
            int objectId = rocks[1];
            int coordX = rocks[2];
            int coordY = rocks[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(11552, coordX, coordY)) {
                if(getBotManager().getLocations().nearestObjectWithinDistance(coordX, coordY)) {
                    availableRocks.add(0, new int[]{30, objectId, coordX, coordY});
                } else {
                    availableRocks.add(new int[]{30, objectId, coordX, coordY});
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
        if(getLocation().contains("crafting_guild"))
            return getBotManager().getLocations().isInCraftingGuild();
        else if(getLocation().contains("brimheaven"))
            return getBotManager().getLocations().isInBrimHeavenMine();
        else if(getLocation().contains("lovakenj"))
            return getBotManager().getLocations().isInLovakenjMine();
        return false;
    }

    private boolean isMining() {
        return getBotManager().getPacketHandler().getAnimation() == 624 || getBotManager().getPacketHandler().getAnimation() == 625
                || getBotManager().getPacketHandler().getAnimation() == 250 || getBotManager().getPacketHandler().getAnimation() == 12189; //Rune Pick & Dragon Pick Anim
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

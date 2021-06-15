import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class GoldMiner {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;
    public GoldMiner(BotManager botManager) {
        this.botManager = botManager;
    }
                                             //LVL //ID   //X   //Y
    private int[][] GOLD_ORE_ROCKS;

    //examine 1 4483 2936 3279 = Bank Object @ Crafting Guild
    public String location = null;

    public void start() {
        int miningLevel = client.anIntArray8924[14]; // MINING
        if(miningLevel < 40) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 40 MINING TO USE THIS SCRIPT: "+getClass().getName());
            return;
        } else if(location == null) {
            System.out.println("STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName());
            return;
        }
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();

        if(getLocation().equals("brimheaven_1")) { //First Gold Rocks @ Brimheaven Dungeon Enterance
            GOLD_ORE_ROCKS = new int[][]{{40, 2099, 2746, 3148}, {40, 2098, 2746, 3149}, {40, 2099, 2745, 3150}, {40, 2099, 2742, 3149}, {40, 2099, 2743, 3152}};
        } else if(getLocation().equals("crafting_guild_1")) { //Right-Side of Crafting Guild (Gold Ore)
            GOLD_ORE_ROCKS = new int[][]{{40, 11183, 2938, 3280}, {40, 11185, 2938, 3278}, {40, 11184, 2939, 3276}};
        } else if(getLocation().equals("crafting_guild_2")) { //Left-Side of Crafting Guild (Gold Ore)
            GOLD_ORE_ROCKS = new int[][]{{40, 11183, 2941, 3276}, {40, 11184, 2942, 3276}, {40, 11184, 2943, 3280}, {40, 11185, 2943, 3279}};
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
                        if (runtime[0] % 2 == 0) {
                            if (getLocation().contains("brimheaven")) {
                                if (!getBotManager().getLocations().isInBrimHeavenMine()) {
                                    teleport("brimheaven");
                                } else {
                                    System.out.println("CONFUSED??");
                                }
                            } else if (getLocation().contains("crafting_guild") && !getBotManager().getLocations().isInCraftingGuild()) {
                                getBotManager().getPacketHandler().sendLogout();
                            }
                        }
                        break;
                    }
                    case "BANKING": {
                        if (runtime[0] % 2 == 0) { //Teleport to CaslteWars
                            if (getLocation().contains("brimheaven")) {
                                if (!getBotManager().getLocations().isInCastleWarsBank())
                                    getBotManager().getPacketHandler().sendOnyxCommand("castlewars");
                                else if (getBotManager().getLocations().isInCastleWarsBank()) {
                                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2445, 3083);
                                    getBotManager().getPacketHandler().bankAll();
                                }
                            } else if (getLocation().contains("crafting_guild")) {
                                //4483 2936 3279 = Bank Object @ Crafting Guild
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2936, 3279);
                                getBotManager().getPacketHandler().bankAll();
                            }
                        }
                        break;
                    }
                    case "HARVESTING": {
                        if (runtime[0] % 4 == 0) {
                            if (isAtHarvestLocation() && !isMining()) {
                                ArrayList<int[]> availableRocks = getReadyRock();
                                if (availableRocks.size() > 0) {
                                    int[] GOLD_ORE_TO_MINE = availableRocks.get(0);
                                    int objectId = GOLD_ORE_TO_MINE[1];
                                    int coordX = GOLD_ORE_TO_MINE[2];
                                    int coordY = GOLD_ORE_TO_MINE[3];
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

    private int teleportStep;

    private void teleport(String location) {
        int GO_BUTTON = 36;
        int BRIMHEAVEN_TELEPORT = 320;
        int PVM_TAB = 70;
        int TELE_INTERFACE = 3073;
        if (location.equals("brimheaven")) {
            if (teleportStep == 0) {
                getBotManager().getPacketHandler().sendOnyxCommand("teleport");
                teleportStep++;
            } else if (teleportStep == 1) {
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1, TELE_INTERFACE, 0, 0, PVM_TAB);
                teleportStep++;
            } else if (teleportStep == 2) {
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1, TELE_INTERFACE, 0, 0, BRIMHEAVEN_TELEPORT);
                teleportStep++;
            } else if (teleportStep == 3) {
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1, TELE_INTERFACE, 0, 0, GO_BUTTON);
                teleportStep++;
            } else if (teleportStep == 4) {
                teleportStep = 0;
            }
        }
    }

    private ArrayList<int[]> getReadyRock() {
        ArrayList<int[]> availableRocks = new ArrayList<>();
        for(int[] rocks : GOLD_ORE_ROCKS) {
            int objectId = rocks[1];
            int coordX = rocks[2];
            int coordY = rocks[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(11554, coordX, coordY)) {
                if(getBotManager().getLocations().nearestObjectWithinDistance(coordX, coordY)) {
                    availableRocks.add(0, new int[]{40, objectId, coordX, coordY});
                } else {
                    availableRocks.add(new int[]{40, objectId, coordX, coordY});
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

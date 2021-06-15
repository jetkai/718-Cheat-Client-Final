import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class GemMiner {

    private final BotManager botManager;

    private ScheduledExecutorService threadPool = null;

    public GemMiner(BotManager botManager) {
        this.botManager = botManager;
    }

    //LVL //ID   //X   //Y
    private  int[][] GEM_ROCKS;

    private final int[] bankers = {11246, 11247}; //Banker Index ID @ Shilo (NOT NPC ID)

    public void start() {

        getBotManager().getBotUI().setup();

        if(!getBotManager().gemZone2) {
            GEM_ROCKS = new int[][]{{40, 11194, 2820, 2998}, {40, 11195, 2821, 2998}, {40, 11364, 2821, 3000}, {40, 11195, 2823, 2999}}; //First Portion
        } else {
            GEM_ROCKS = new int[][]{{40, 11194, 2825, 3001}, {40, 11195, 2825, 3003}, {40, 11364, 2823, 3002}}; //Second Portion
        }

        int miningLevel = client.anIntArray8924[14]; // MINING
        if(miningLevel < 40) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 40 MINING TO USE THIS SCRIPT.");
            return;
        }
        if (isRunning())
            return;
        getBotManager().startBotUptime = System.currentTimeMillis();
        System.out.println("Starting Gold Miner Script");
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
                if(getBotManager().isLoggedOut())
                    getBotManager().getBotUI().setAction("NEXT LOGIN: "+getBotManager().getLogoutTime()+ "<("+getBotManager().getPlayerWhoLoggedMeOut()+")>");
                else
                    getBotManager().getBotUI().setAction(getAction());
                getBotManager().getBotUI().trackResourceTick();
              //  debugOutput();
                switch (getAction()) {
                    case "LOGIN": {
                        getBotManager().handleAutoLogin();
                        break;
                    }
                    case "PATHING": {
                        if (runtime[0] % 2 == 0) {
                            if (!getBotManager().getLocations().isAtGemRocks()) {
                                getBotManager().getPacketHandler().sendOnyxCommand("shilogems");
                            }
                        }
                        break;
                    }
                    case "BANKING": {
                        if (runtime[0] % 2 == 0) { //Teleport to Shilo
                            /*if (!getBotManager().getLocations().isInShiloVillage() || getBotManager().getLocations().isAtGemRocks())
                                getBotManager().getPacketHandler().sendOnyxCommand("shilo");
                            else if (getBotManager().getLocations().isInShiloVillage()) {
                                getBotManager().getPacketHandler().sendNpcOptionByName(4, "Banker", bankers[new SecureRandom().nextInt(bankers.length)]);
                                getBotManager().getPacketHandler().bankAll();
                            }*/

                            if (!getBotManager().getLocations().isInCastleWarsBank())
                                getBotManager().getPacketHandler().sendOnyxCommand("castlewars");
                            else if (getBotManager().getLocations().isInCastleWarsBank()) {
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2445, 3083);
                                getBotManager().getPacketHandler().bankAll();
                            }
                        }
                        break;
                    }
                    case "HARVESTING": {
                        if (runtime[0] % (new SecureRandom().nextInt(1) + 3) == 0) {
                            if (getBotManager().getLocations().isInShiloVillage() && !isMining()) {
                                ArrayList<int[]> availableRocks = getReadyRock();
                                if (availableRocks.size() > 0) {
                                    int[] GEM_ROCK_TO_MINE = availableRocks.get(0);
                                    int objectId = GEM_ROCK_TO_MINE[1];
                                    int coordX = GEM_ROCK_TO_MINE[2];
                                    int coordY = GEM_ROCK_TO_MINE[3];
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
        for(int[] rocks : GEM_ROCKS) {
            int objectId = rocks[1];
            int coordX = rocks[2];
            int coordY = rocks[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(11193, coordX, coordY)) {
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
        else if(!getBotManager().getPacketHandler().isInventoryFull() && !getBotManager().getLocations().isAtGemRocks())
            return "PATHING";
        else if(!getBotManager().getPacketHandler().isInventoryFull() && getBotManager().getLocations().isInShiloVillage())
            return "HARVESTING";
        System.out.println("MINING ACTION = NULL");
        return null;
    }

    private boolean isMining() {
        return getBotManager().getPacketHandler().getAnimation() == 624 || getBotManager().getPacketHandler().getAnimation() == 625
                || getBotManager().getPacketHandler().getAnimation() == 250 || getBotManager().getPacketHandler().getAnimation() == 12189;
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

    public BotManager getBotManager() {
        return botManager;
    }
}

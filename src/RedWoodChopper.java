import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class RedWoodChopper {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public RedWoodChopper(BotManager botManager) {
        this.botManager = botManager;
    }

    private int[][] RED_TREE;

    public String location = null;

    public void start() {
        int wooodcuttingLevel = client.anIntArray8924[Skills.WOODCUTTING];
        if(wooodcuttingLevel < 90) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 90 "+Skills.getLevelNameBySkillId(Skills.WOODCUTTING)+" TO USE THIS SCRIPT: "+getClass().getName());
            return;
        } else if(location == null) {
            System.out.println("STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName());
            return;
        }
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();

        if(getLocation().equals("redwood_1")) { //First Gold Rocks @ Brimheaven Dungeon Enterance
            RED_TREE = new int[][]{};
        } else if(getLocation().equals("redwood_2")) { //Right-Side of Crafting Guild (Gold Ore)
            RED_TREE = new int[][]{{90, 129668, 1572, 3492}, {90, 129670, 1572, 3493}, {90, 129668, 1571, 3494}, {90, 129670, 1570, 3494}, {90, 129668, 1568, 3493}, {90, 129670, 1568, 3492}};
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
                System.out.println(Skills.getLevelNameBySkillId(Skills.WOODCUTTING).toUpperCase()+" ACTION = " + getAction());
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
                        if(getBotManager().getLocations().isInWoodCuttingGuildHighTierTrees() && getBotManager().getLocations().getCoordZ() == 0)
                            getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 128857, 1575, 3493);
                        break;
                    }
                    case "BANKING": {
                        if (getLocation().contains("redwood")) {
                            if (getBotManager().getLocations().nearestObjectWithinDistance(1590, 3483))
                                bankInventoryWithPreset1();
                            else if (getBotManager().getLocations().isInWoodCuttingGuildHighTierTrees() && getBotManager().getLocations().getCoordZ() == 0)
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 103948, 1590, 3483);
                            else if(getBotManager().getLocations().isInWoodCuttingGuildHighTierTrees() && getBotManager().getLocations().getCoordZ() == 1)
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 128858, 1575, 3493);
                        }
                        break;
                    }
                    case "HARVESTING": {
                        if (runtime[0] % 4 == 0) {
                            if (isAtHarvestLocation() && !isWoodcutting()) {
                                ArrayList<int[]> availableTrees = getReadyTree();
                                if (availableTrees.size() > 0) {
                                    int[] TREE_TO_CHOP = availableTrees.get(0);
                                    int objectId = TREE_TO_CHOP[1];
                                    int coordX = TREE_TO_CHOP[2];
                                    int coordY = TREE_TO_CHOP[3];
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
        if (getLocation().contains("redwood")) {
            System.out.println(presetStep);
            if (presetStep == 0) {
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1, 3002, 0, 0, 26);
                getBotManager().getPacketHandler().sendButtonClickOnInterface(1, 3002, 0, 0, 3);
                presetStep++;
            } else if (presetStep == 1) {
                getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                presetStep++;
            } else if (presetStep == 2) {
                presetStep = 0;
            }
        }
    }

    private ArrayList<int[]> getReadyTree() {
        ArrayList<int[]> availableTree = new ArrayList<>();
        for(int[] tree : RED_TREE) {
            int objectId = tree[1];
            int coordX = tree[2];
            int coordY = tree[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(129671, coordX, coordY)) {
                if(getBotManager().getLocations().nearestObjectWithinDistance(coordX, coordY)) {
                    availableTree.add(0, new int[]{90, objectId, coordX, coordY});
                } else {
                    availableTree.add(new int[]{90, objectId, coordX, coordY});
                }
            }
        }
        return availableTree;
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
        if(getLocation().contains("redwood"))
            return getBotManager().getLocations().isInRedWoodTree() && getBotManager().getLocations().getCoordZ() == 1;
        return false;
    }

    private boolean isWoodcutting() {
        return getBotManager().getPacketHandler().getAnimation() == 879 || getBotManager().getPacketHandler().getAnimation() == 867
                || getBotManager().getPacketHandler().getAnimation() == 2846;
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

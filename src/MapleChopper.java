import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class MapleChopper {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;
    public MapleChopper(BotManager botManager) {
        this.botManager = botManager;
    }

    private int[][] MAPLE_TREE;

    public String location = null;

    public void start() {
        int wooodcuttingLevel = client.anIntArray8924[Skills.WOODCUTTING];
        if(wooodcuttingLevel < 50) {
            String errorMessage = "STOPPING SCRIPT: YOU MUST BE 50 "+Skills.getLevelNameBySkillId(Skills.WOODCUTTING)+" TO USE THIS SCRIPT: "+getClass().getName();
            System.out.println(errorMessage);
            getBotManager().getPacketHandler().sendGameMessage(errorMessage);
            getBotManager().stopScripts();
            return;
        } else if(location == null) {
            String errorMessage = "STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName();
            System.out.println(errorMessage);
            getBotManager().getPacketHandler().sendGameMessage(errorMessage);
            getBotManager().stopScripts();
            return;
        }
        if (isRunning())
            return;

        getBotManager().getBotUI().setup();
        if(getLocation().equals("seers")) //Right-Side of Crafting Guild (Gold Ore)
            MAPLE_TREE = new int[][]{{50, 51843, 2725, 3501}, {50, 51843, 2721, 3501}, {50, 51843, 2730, 3500}, {50, 51843, 2733, 3495}};

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
                    //getBotManager().getPacketHandler().logoutFromHiddenStaff();//Disabled on this one
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
                        if(!getBotManager().getLocations().isAtSeersVillage()) {
                            getBotManager().getPacketHandler().sendLogout();
                            getBotManager().getPacketHandler().sendClientDialoguePopup("AREA LOCATION",
                                    "You must be within Seer's Village to start this script, quit being lazy.");
                            getBotManager().stopScripts();
                        }
                        //if(getBotManager().getLocations().isInWoodCuttingGuildHighTierTrees() && getBotManager().getLocations().getCoordZ() == 0)
                        //    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 128857, 1575, 3493);
                        break;
                    }
                    case "BANKING": {
                        if (getLocation().contains("seers")) {
                            if(!getBotManager().getLocations().isAtSeersVillagePresetBankArea() && getBotManager().getLocations().isAtSeersVillage())
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4739, 2726, 3498);
                            else if (getBotManager().getLocations().isAtSeersVillagePresetBankArea() || getBotManager().getLocations().nearestObjectWithinDistance(2726, 3498))
                                bankInventoryWithPreset1();
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
        if (getLocation().contains("seers")) {
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
        for(int[] tree : MAPLE_TREE) {
            int objectId = tree[1];
            int coordX = tree[2];
            int coordY = tree[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(31057, coordX, coordY)) {
                if(getBotManager().getLocations().nearestObjectWithinDistance(coordX, coordY)) {
                    availableTree.add(0, new int[]{50, objectId, coordX, coordY});
                } else {
                    availableTree.add(new int[]{50, objectId, coordX, coordY});
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
        if(getLocation().contains("seers"))
            return getBotManager().getLocations().isAtSeersVillage();
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

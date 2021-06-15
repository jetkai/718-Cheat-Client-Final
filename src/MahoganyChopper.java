import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class MahoganyChopper {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public MahoganyChopper(BotManager botManager) {
        this.botManager = botManager;
    }

    private int[][] MAHOGANY_TREES;

    private int pathingStep;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();

        final int[] runtime = {0};
        pathingStep = 0;
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;

                //Checks if any staff are near the area, logout if so (Only detects hidden staff at the moment)
                AntiBanToggle antiBanToggle = getBotManager().getPacketHandler().getReflection().getAntiBanToggle();
                if(antiBanToggle.isAntiBanEnabled()) {
                    if(antiBanToggle.isLogoutFromPlayersEnabled())
                        getBotManager().getPacketHandler().sendLogoutDetection();
                    else if(antiBanToggle.isLogoutFromHiddenStaffEnabled())
                        getBotManager().getPacketHandler().logoutFromHiddenStaff();
                    //TODO STAFF
                }
                //Returns if no available action is ready
                if (getAction() == null)
                    return;

                if (getBotManager().isLoggedOut())
                    getBotManager().getBotUI().setAction("NEXT LOGIN: "+getBotManager().getLogoutTime()+ "<("+getBotManager().getPlayerWhoLoggedMeOut()+")>");
                else
                    getBotManager().getBotUI().setAction(getAction());
                getBotManager().getBotUI().trackResourceTick();
                //Prints action that is currently happening for debug purposes
                if (getBotManager().isDebug())
                    System.out.println(Skills.getLevelNameBySkillId(getSkillType()) + " ACTION = " + getAction());
                switch (getAction()) {

                    case "LOGIN": {
                        getBotManager().handleAutoLogin();
                        break;
                    }

                    case "PATHING": {
                        if (runtime[0] % 2 == 0) {
                            if (getLocation().contains("tai_bwo_wannai"))
                                handleTaiBwoWannaiPathing();
                            else if(getLocation().contains("farming_guild") && !getBotManager().getLocations().isAtFarmingGuildMahogany()) //If at not farming guild mahog area, will automatically do the tai_bwo_wannai mahogs
                                setLocation("tai_bwo_wannai");
                        }
                        break;
                    }

                    case "BANKING": {
                        if (runtime[0] % 2 == 0) { //Teleport to CaslteWars
                            if (getLocation().contains("tai_bwo_wannai")) {
                                if (!getBotManager().getLocations().isInCastleWarsBank())
                                    getBotManager().getPacketHandler().sendOnyxCommand("castlewars");
                                else if (getBotManager().getLocations().isInCastleWarsBank()) {
                                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2445, 3083);
                                    getBotManager().getPacketHandler().bankAll();
                                    pathingStep = 0;
                                }
                            } else if(getLocation().contains("farming_guild")) {
                                if(getBotManager().getLocations().nearestObjectWithinDistance(1246, 3765)) {
                                    bankInventoryWithPreset1();
                                } else {
                                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 134491, 1246, 3765);
                                }
                            }
                        }
                        break;
                    }

                    case "HARVESTING": {
                        if (runtime[0] % 5 == 0) {
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

    private void handleTaiBwoWannaiPathing() {

        int[] tempObjects = new int[]{1390, 1184, 340};
        int[][] pathingLocations = new int[][] {{2782, 3116}, {2811, 3105}, {2819, 3083}};

        if (pathingStep == 0) {
           if(!getBotManager().getLocations().isInBrimHeavenMine()) {
               teleport("brimheaven");
           } else if(getBotManager().getLocations().isInBrimHeavenMine()) {
               pathingStep++;
           }


        } else if((pathingStep >= 1 && pathingStep <= 2) || pathingStep == 4) {
            int pathingLocation;
            if(pathingStep == 4) {
                pathingLocation = pathingLocations.length - 1;
                if(!isDoorsOpen()) {
                    getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 9039, 2817, 3084);
                    return;
                }
            } else
                pathingLocation = pathingStep - 1;
            if(!getBotManager().getLocations().nearestObjectWithinDistance(pathingLocations[pathingLocation])) {
                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, tempObjects[pathingLocation], pathingLocations[pathingLocation][0], pathingLocations[pathingLocation][1]);
                System.out.println("Sending Walking Packet ("+pathingStep+"): "+ Arrays.toString(pathingLocations[pathingLocation]));
            } else if(getBotManager().getLocations().nearestObjectWithinDistance(pathingLocations[pathingLocation])) {
                pathingStep++;
            }
        } else if(pathingStep == 3) { //Open Door and go inside
            if(!isDoorsOpen()) {
                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 9039, 2817, 3084);
            } else if(isDoorsOpen()) {
                pathingStep++;
            }
        }
      //  return pathingStep >= 0 && pathingStep <= 7;//True means that it is still handling the pathing, False = Complete, Pathing step is at 8
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

    private int presetStep;

    private void bankInventoryWithPreset1() {
        if (getLocation().contains("farming_guild")) {
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

    private void setup() {
        if(getLocation().equals("tai_bwo_wannai")) //First Gold Rocks @ Brimheaven Dungeon Enterance
            MAHOGANY_TREES = new int[][]{{50, 70076, 2820, 3088}, {50, 70076, 2819, 3079}, {50, 70076, 2822, 3084}, {50, 70076, 2824, 3080}};
        else if(getLocation().equals("farming_guild"))
            MAHOGANY_TREES = new int[][]{{50, 109034, 1239, 3769}, {50, 109034, 1235, 3772}};

        getBotManager().startBotUptime = System.currentTimeMillis();
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        int woodcuttingLevel = Skills.getLevelBySkillId(getSkillType());
        if(woodcuttingLevel < 55) {
            System.out.println("STOPPING SCRIPT: YOU MUST BE 55 "+Skills.getLevelNameBySkillId(getSkillType())+" TO USE THIS SCRIPT: "+getClass().getName());
            return false;
        } else if(location == null) {
            System.out.println("STOPPING SCRIPT: YOU MUST HAVE A LOCATION SET TO RUN THIS SCRIPT: "+getClass().getName());
            return false;
        }
        return !isRunning();
    }

    private boolean isDoorsOpen() {
        return getBotManager().getPacketHandler().containsObjectAtCoord(9038, 2816, 3083);
    }

    private ArrayList<int[]> getReadyTree() {
        ArrayList<int[]> availableTree = new ArrayList<>();
        for(int[] tree : MAHOGANY_TREES) {
            int objectId = tree[1];
            int coordX = tree[2];
            int coordY = tree[3];
            if(!getBotManager().getPacketHandler().containsObjectAtCoord(9035, coordX, coordY)) {
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
        if(getBotManager().isDebug())
            System.out.println(""+Skills.getLevelNameBySkillId(Skills.WOODCUTTING)+" ACTION = NULL");
        return null;
    }

    private boolean isAtHarvestLocation() {
        if(getLocation().contains("tai_bwo_wannai"))
            return getBotManager().getLocations().isInTaiBwoWanniMahog();
        else if(getLocation().contains("farming_guild"))
            return getBotManager().getLocations().isAtFarmingGuildMahogany();
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
        threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
        if(getBotManager().isDebug())
            System.out.println("STARTING A NEW THREAD");
    }

    public void stop() {
        if (threadPool == null)
            return;
        threadPool.shutdownNow();
        if(getBotManager().isDebug())
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

    private int getSkillType() {
        return Skills.WOODCUTTING;
    }
}

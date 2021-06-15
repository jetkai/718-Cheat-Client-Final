import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class GemCrafter {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public GemCrafter(BotManager botManager) {
        this.botManager = botManager;
    }

    private int uncutGemItemId;
    private int cutGemItemId;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();
        uncutGemItemId = 0;
        cutGemItemId = 0;

        final int[] runtime = {0};
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
                        //TODO (Not really Needed)
                        break;
                    }

                    case "BANKING": {
                        //if (runtime[0] % 2 == 0) { //Teleport to CastleWars
                            bankInventoryWithPreset1();
                       // }
                        break;
                    }

                    case "INIT GEM CUT": {
                            if(!isCutting())
                                cutGem();
                        break;
                    }
                    case "CUTTING": {
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
            System.out.println(presetStep);
            if(uncutGemItemId == 0 && cutGemItemId == 0) {
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
                if (presetStep == 0) {
                    getBotManager().getPacketHandler().sendButtonClickOnInterface(1, 3002, 0, 0, 4);
                    presetStep++;
                } else if (presetStep == 1) {
                    getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                    presetStep++;
                } else if (presetStep == 2 && getBotManager().getPacketHandler().isInventoryFull()) {
                    uncutGemItemId = getBotManager().getPacketHandler().getItemInInventoryBySlot(27);
                    cutGemItemId = uncutGemItemId - 16; //Only for standard gems, onyx & other custom gems have different cut ids
                    presetStep = 0;
                }
            } else if(!getBotManager().getPacketHandler().inventoryContainsItem(uncutGemItemId)) {
                if (presetStep == 0) {
                    getBotManager().getPacketHandler().sendButtonClickOnInterface(1, 3002, 0, 0, 4);
                    presetStep++;
                } else if (presetStep == 1) {
                    getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                    presetStep++;
                } else if (presetStep == 2) {
                    presetStep = 0;
                }
            }
    }

    private boolean isCutting() {
        return getBotManager().getPacketHandler().getAnimation() != -1;
    }

    private int gemCutStep;

    private void cutGem() {
        System.out.println(gemCutStep);
        if(gemCutStep == 0) {
            getBotManager().getPacketHandler().sendButtonClickOnInterface(1, 679, uncutGemItemId, 27, 0);
            gemCutStep++;
        } else if(gemCutStep == 1) {
            getBotManager().getPacketHandler().sendDialogueOption(905, 0, 0);
            gemCutStep++;
        } else if(gemCutStep == 2) {
            gemCutStep = 0;
        }
    }

    private void setup() {
        getBotManager().startBotUptime = System.currentTimeMillis();
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        return !isRunning();
    }

    private String getAction() {
        System.out.println("GemId: "+uncutGemItemId+", "+cutGemItemId);
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if((uncutGemItemId == 0 && cutGemItemId == 0) || !getBotManager().getPacketHandler().inventoryContainsItem(uncutGemItemId) || !getBotManager().getPacketHandler().isInventoryFull())
            return "BANKING";
        else if(cutGemItemId != 0 && !getBotManager().getPacketHandler().inventoryContainsItem(cutGemItemId))
            return "INIT GEM CUT";
        else if(cutGemItemId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(cutGemItemId))
            return "CUTTING";
       /* else if(!getBotManager().getPacketHandler().isInventoryFull() && !isAtHarvestLocation())
            return "PATHING";*/
        if(getBotManager().isDebug())
            System.out.println(""+Skills.getLevelNameBySkillId(Skills.WOODCUTTING)+" ACTION = NULL");
        return null;
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

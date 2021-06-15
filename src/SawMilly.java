import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class SawMilly {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public SawMilly(BotManager botManager) {
        this.botManager = botManager;
    }

    private int logsId;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();
        logsId = 0;

        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;

                //Checks if any staff are near the area, logout if so (Only detects hidden staff at the moment)
             //   getBotManager().getPacketHandler().logoutFromAnyPlayerFor15Minutes();
                //Returns if no available action is ready
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
                        if(!getBotManager().getLocations().isInSawMill())
                            getBotManager().getPacketHandler().sendOnyxCommand("sawmill");
                        break;
                    }

                    case "BANKING": {
                        if(getBotManager().getLocations().isInSawMill()) {
                            if(new SecureRandom().nextInt(2) == 0)
                                getBotManager().getPacketHandler().sendOnyxCommand("etceteria");
                            else
                                getBotManager().getPacketHandler().sendOnyxCommand("keldagrim");
                        } else {
                            bankInventoryWithPreset1();
                        }
                        break;
                    }
                    case "GATHERING PLANKS": {
                        if (runtime[0] % 2 == 0)
                            getBotManager().getPacketHandler().sendNpcOptionByName(4, "Sawmill operator", 4961);
                        getBotManager().getPacketHandler().sendButtonClickOnInterface(5, 403, 0, 0, 15);
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
            if(logsId == 0) {
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
                    logsId = getBotManager().getPacketHandler().getItemInInventoryBySlot(27);
                    presetStep = 0;
                }
            } else if(!getBotManager().getPacketHandler().inventoryContainsItem(logsId)) {
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

    private void setup() {
        getBotManager().startBotUptime = System.currentTimeMillis();
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        return !isRunning();
    }

    private String getAction() {
        System.out.println("LogsId: "+ logsId);
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if((logsId == 0) || !getBotManager().getPacketHandler().inventoryContainsItem(logsId))
            return "BANKING";
        else if(logsId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(logsId) && !getBotManager().getLocations().isInSawMill())
            return "PATHING";
        else if(logsId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(logsId) && getBotManager().getLocations().isInSawMill())
            return "GATHERING PLANKS";
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

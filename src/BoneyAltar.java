import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class BoneyAltar {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public BoneyAltar(BotManager botManager) {
        this.botManager = botManager;
    }

    private int bonesId;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();
        bonesId = 0;

        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;

                //Checks if any staff are near the area, logout if so (Only detects hidden staff at the moment)
             //   getBotManager().getPacketHandler().logoutFromAnyPlayerFor15Minutes();
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
                        if(!getBotManager().getLocations().isInPrayerZone())
                            getBotManager().getPacketHandler().sendOnyxCommand("prayer");
                        break;
                    }

                    case "BANKING": {
                        //if (runtime[0] % 2 == 0) { //Teleport to CaslteWars
                        //if()
                        if(getBotManager().getLocations().isInPrayerZone()) {
                            getBotManager().getPacketHandler().sendOnyxCommand("smithing");
                        } else {
                            bankInventoryWithPreset1();
                        }
                       // }
                        break;
                    }

                    case "USING BONES": {
                        if (runtime[0] % 5 == 0) {
                            if (!isDoingSkillAnimation())
                                getBotManager().getPacketHandler().sendInterfaceOnObject(679, bonesId, 27, 113199, 3111, 3466);
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
            System.out.println(presetStep);
            if(bonesId == 0) {
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
                if (presetStep == 0) {
                    getBotManager().getPacketHandler().sendButtonClickOnInterface(1,3002, 0, 0, 4);
                    presetStep++;
                } else if (presetStep == 1) {
                    getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                    presetStep++;
                } else if (presetStep == 2 && getBotManager().getPacketHandler().isInventoryFull()) {
                    bonesId = getBotManager().getPacketHandler().getItemInInventoryBySlot(27);
                    presetStep = 0;
                }
            } else if(!getBotManager().getPacketHandler().isInventoryFull()) {
                if (presetStep == 0) {
                    getBotManager().getPacketHandler().sendButtonClickOnInterface(1,3002, 0, 0, 4);
                    presetStep++;
                } else if (presetStep == 1) {
                    getBotManager().getPacketHandler().sendDialogueOption(1188, 11, 11);
                    presetStep++;
                } else if (presetStep == 2) {
                    presetStep = 0;
                }
            }
    }

    private boolean isDoingSkillAnimation() {
        return getBotManager().getPacketHandler().getAnimation() != -1;
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
        System.out.println("BonesId: "+ bonesId);
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if((bonesId == 0) || !getBotManager().getPacketHandler().inventoryContainsItem(bonesId))
            return "BANKING";
        else if(bonesId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(bonesId) && !getBotManager().getLocations().isInPrayerZone())
            return "PATHING";
        else if(bonesId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(bonesId) && getBotManager().getLocations().isInPrayerZone())
            return "USING BONES";
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

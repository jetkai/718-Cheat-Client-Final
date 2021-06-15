import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class CannonBaller {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public CannonBaller(BotManager botManager) {
        this.botManager = botManager;
    }

    private int barId;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();
        barId = 0;

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
                        if(runtime[0] % 5 == 0 ) {
                            System.out.println("LoggedIn: "+getBotManager().getPacketHandler().getLoggedInPlayerName());
                            if(getBotManager().getPacketHandler().getLoggedInPlayerName().equalsIgnoreCase("expert kai")
                                    && !getBotManager().getLocations().isAtDonatorZone())
                                getBotManager().getPacketHandler().sendOnyxCommand("dz");
                            else if (!getBotManager().getLocations().isInSkillingBank())
                                getBotManager().getPacketHandler().sendOnyxCommand("smithing");
                            break;
                        }
                    }

                    case "BANKING": {
                        System.out.println("LoggedIn: "+getBotManager().getPacketHandler().getLoggedInPlayerName());
                        if(getBotManager().getPacketHandler().getLoggedInPlayerName().equalsIgnoreCase("expert kai") && !getBotManager().getLocations().isAtDonatorZone()) {
                            getBotManager().getPacketHandler().sendOnyxCommand("dz");
                        } else if (!getBotManager().getPacketHandler().getLoggedInPlayerName().equalsIgnoreCase("expert kai") && !getBotManager().getLocations().isInSkillingBank()) {
                            if(runtime[0] % 5 == 0 )
                                getBotManager().getPacketHandler().sendOnyxCommand("smithing");
                        } else {
                            bankInventoryWithPreset1();
                        }
                        break;
                    }
                    case "SMELTING": {
                        if ((!isDoingSkillAnimation()) && runtime[0] % 3 == 0)
                            sendSmithStep();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 722, TimeUnit.MILLISECONDS);
    }

    private boolean isDoingSkillAnimation() {
        return getBotManager().getPacketHandler().getAnimation() != -1;
    }

    private int smithStep;

    private void sendSmithStep() {
        if(smithStep == 0) {
            if(getBotManager().getLocations().isAtDonatorZone())
                getBotManager().getPacketHandler().sendInterfaceOnObject(679, barId, 27, 6189, 3365, 5228);
                else
                getBotManager().getPacketHandler().sendInterfaceOnObject(679, barId, 27, 106189, 3090, 3467);
            smithStep++;
        } else if(smithStep == 1) {
            getBotManager().getPacketHandler().sendDialogueOption(916, 0, 0);
            smithStep++;
        } else if(smithStep == 2) {
            smithStep = 0;
        }
    }

    private int presetStep;

    private void bankInventoryWithPreset1() {
            System.out.println(presetStep);
            if(barId == 0) {
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
                    barId = getBotManager().getPacketHandler().getItemInInventoryBySlot(27);
                    presetStep = 0;
                }
            } else if(!getBotManager().getPacketHandler().inventoryContainsItem(barId)) {
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
        System.out.println("BarId: "+ barId);
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if((barId == 0) || !getBotManager().getPacketHandler().inventoryContainsItem(barId))
            return "BANKING";
        else if(barId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(barId)
                && (!getBotManager().getLocations().isInSkillingBank() && !getBotManager().getLocations().isAtDonatorZone()))
            return "PATHING";
        else if(barId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(barId)
                && (getBotManager().getLocations().isInSkillingBank() || getBotManager().getLocations().isAtDonatorZone()))
            return "SMELTING";
        if(getBotManager().isDebug())
            System.out.println(""+Skills.getLevelNameBySkillId(Skills.SMITHING)+" ACTION = NULL");
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
        return Skills.SMITHING;
    }
}

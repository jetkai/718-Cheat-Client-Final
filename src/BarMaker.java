import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class BarMaker {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public BarMaker(BotManager botManager) {
        this.botManager = botManager;
    }

    private int oreId;
    private int oreId_2;

    public String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
            return;
        getBotManager().getBotUI().setup();
        oreId = 0;
        oreId_2 = 0;
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
                getBotManager().getPacketHandler().getReflection().getAntiBanToggle().isAntiBanEnabled();
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
                            if(getBotManager().getPacketHandler().getLoggedInPlayerName().equalsIgnoreCase("expert kai") && !getBotManager().getLocations().isAtDonatorZone())
                                getBotManager().getPacketHandler().sendOnyxCommand("dz");
                            else if (!getBotManager().getPacketHandler().getLoggedInPlayerName().equalsIgnoreCase("expert kai") && !getBotManager().getLocations().isInSkillingBank())
                                getBotManager().getPacketHandler().sendOnyxCommand("smithing");
                            break;
                        }
                    }

                    case "BANKING": {
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

    private int smithStep;

    private void sendSmithStep() {
        if(smithStep == 0) {
            if(getBotManager().getLocations().isAtDonatorZone())
                getBotManager().getPacketHandler().sendObjectOptionByCoord(2,6189, 3365, 5228);
                else
                getBotManager().getPacketHandler().sendObjectOptionByCoord(2,106189, 3090, 3467);
            smithStep++;
        } else if(smithStep == 1) {
            //14 = Bronze ////15 = Blurite // 16 = Iron // 17 = Silver // 18 = Steel // 19 = Gold // 20 = Mithril // 21 = Addy // 22 = Rune
            getBotManager().getPacketHandler().sendDialogueOption(916, 0, getComponentIdFromOre());
            smithStep++;
        } else if(smithStep == 2) {
            smithStep = 0;
        }
    }

    private int getComponentIdFromOre() {
        String oreName = String.valueOf(Class298_Sub32_Sub14.aClass477_9400.getItemDefinitions(oreId).name);
        String oreName2 = String.valueOf(Class298_Sub32_Sub14.aClass477_9400.getItemDefinitions(oreId_2).name);
        if(((oreName.equals("Copper ore") || oreName.equals("Tin ore")) && (oreName2.equals("Copper ore") || oreName2.equals("Tin ore")))) {
            return 14;
        } else if((oreName.equals("Blurite ore"))) {
            return 15;
        } else  if((oreName.equals("Silver ore") || oreName.equals("Coal")) && ((oreName2.equals("Coal") || oreName2.equals("Silver ore")))) {
            return 17;
        } else  if((oreName.equals("Iron ore") || oreName.equals("Coal")) && ((oreName2.equals("Coal") || oreName2.equals("Iron ore")))) {
            return 18;
        } else  if((oreName.equals("Gold ore"))) {
            return 19;
        } else  if((oreName.equals("Mithril ore") || oreName.equals("Coal")) && ((oreName2.equals("Coal") || oreName2.equals("Mithril ore")))) {
            return 20;
        } else  if((oreName.equals("Adamantite ore") || oreName.equals("Coal")) && ((oreName2.equals("Coal") || oreName2.equals("Adamantite ore")))) {
            return 21;
        } else  if((oreName.equals("Runite ore") || oreName.equals("Coal")) && ((oreName2.equals("Coal") || oreName2.equals("Runite ore")))) {
            return 22;
        } else if((oreName.equals("Iron ore"))) {
            return 16;
        }
        return 0;
    }


    private boolean isDoingSkillAnimation() {
        return getBotManager().getPacketHandler().getAnimation() != -1;
    }

    private int presetStep;

    private void bankInventoryWithPreset1() {
            System.out.println(presetStep);
            if(oreId == 0) {
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
                    oreId = getBotManager().getPacketHandler().getItemInInventoryBySlot(27);
                    oreId_2 = getBotManager().getPacketHandler().getItemInInventoryBySlot(0);
                    presetStep = 0;
                }
            } else if(!getBotManager().getPacketHandler().inventoryContainsItem(oreId)) {
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
        System.out.println("oreId: "+ oreId);
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if((oreId == 0) || !getBotManager().getPacketHandler().inventoryContainsItem(oreId))
            return "BANKING";
        else if(oreId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(oreId) &&
                (!getBotManager().getLocations().isInSkillingBank() && !getBotManager().getLocations().isAtDonatorZone()))
            return "PATHING";
        else if(oreId != 0 && getBotManager().getPacketHandler().inventoryContainsItem(oreId) &&
                (getBotManager().getLocations().isInSkillingBank() || getBotManager().getLocations().isAtDonatorZone()))
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

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class PickThemPockets {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public PickThemPockets(BotManager botManager) {
        this.botManager = botManager;
    }

    private String location = null;

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
        return;
        getBotManager().getBotUI().setup();

        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;

                //Checks if any staff are near the area, logout if so (Only detects hidden staff at the moment)
                AntiBanToggle antiBanToggle = getBotManager().getPacketHandler().getReflection().getAntiBanToggle();
                if(antiBanToggle.isAntiBanEnabled() && !getBotManager().getLocations().isAtPrayerZoneToRestoreHealth()) {
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
                        if (runtime[0] % 2 == 0)
                            if(getLocation().equals("draynor"))
                                getBotManager().getPacketHandler().sendOnyxCommand("draynormarket");
                            else if(getLocation().contains("ardougne"))
                                getBotManager().getPacketHandler().sendOnyxCommand("ardougne");
                        break;
                    }
                    case "BANKING": {
                        if (runtime[0] % 2 == 0) {
                            if(getLocation().equals("draynor")) {
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(2, 2015, 3091, 3244);
                                getBotManager().getPacketHandler().bankAll();
                            } else if (getLocation().contains("ardougne") && !getBotManager().getLocations().isInCastleWarsBank())
                                getBotManager().getPacketHandler().sendOnyxCommand("castlewars");
                            else if (getLocation().contains("ardougne") && getBotManager().getLocations().isInCastleWarsBank()) {
                                getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 4483, 2445, 3083);
                                getBotManager().getPacketHandler().bankAll();
                            }
                        }
                        break;
                    }
                    case "HEALING": { //IF player is under 20hp
                        if(getBotManager().getLocations().isAtPrayerZoneToRestoreHealth())
                            getBotManager().getPacketHandler().sendObjectOptionByCoord(1, 129241, 3110, 3478);
                        else if(!getBotManager().getLocations().isAtPrayerZoneToRestoreHealth())
                            getBotManager().getPacketHandler().sendOnyxCommand("prayer");
                        break;
                    }
                    case "PICKIN_POCKETS": {
                        handlePickpocket();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 600, TimeUnit.MILLISECONDS);
    }

    private void handlePickpocket() {
        boolean chillOut = new SecureRandom().nextInt(40) == 1;
        if(!chillOut) {
            if (getLocation().equals("draynor"))
                getBotManager().getPacketHandler().sendNpcOptionByName(4, "Martin the Master Gardener", 3633);
            else if (getLocation().equals("ardougne_knight"))
                getBotManager().getPacketHandler().sendNpcOptionByName(4, "Knight of Ardougne", 5000);
            else if (getLocation().equals("ardougne_paladin"))
                getBotManager().getPacketHandler().sendNpcOptionByName(4, "Paladin", 6218);
        } else {
            getBotManager().getPacketHandler().sendGameMessage("Chilling out");
        }
    }

    private void setup() {
        getBotManager().startBotUptime = System.currentTimeMillis();
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        int agilityLevel = Skills.getLevelBySkillId(getSkillType());
        if(agilityLevel < 38) {
            String warningText = "STOPPING SCRIPT: YOU MUST BE 38 "+Skills.getLevelNameBySkillId(getSkillType())+" TO USE THIS SCRIPT: "+getClass().getName();
            getBotManager().getPacketHandler().sendGameMessage(warningText);
            System.out.println(warningText);
            return false;
        }
        return !isRunning();
    }

    private String getAction() {
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if(getBotManager().getPacketHandler().getHealthFromLocalPlayer() <= 250)
            return "HEALING";
        else if(isAtHarvestLocation() && !getBotManager().getPacketHandler().isInventoryFull())
            return "PICKIN_POCKETS";
        else if(isAtHarvestLocation() && getBotManager().getPacketHandler().isInventoryFull())
            return "BANKING";
        else if(!isAtHarvestLocation())
            return "PATHING";
        return null;
    }

    private boolean isAtHarvestLocation() {
        if(getLocation().equals("draynor"))
            return getBotManager().getLocations().isAtDraynorMarket();
        else if(getLocation().contains("ardougne"))
            return getBotManager().getLocations().isAtArdougneMarket();
        return false;
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

    public BotManager getBotManager() {
        return botManager;
    }

    private int getSkillType() {
        return Skills.THIEVING;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}

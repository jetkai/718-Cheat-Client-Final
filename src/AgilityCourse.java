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
public class AgilityCourse {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public AgilityCourse(BotManager botManager) {
        this.botManager = botManager;
    }

    private String location = null;

    private ArrayList<int[]> agilityObstacles;

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
                    case "DOING AGILITY": {
                        setObstacleDone();
                        if (runtime[0] % 2 == 0)
                            handleObjectPathing();
                        break;
                    }
                    case "PATHING": {
                        if(getLocation().equals("gnome_course") && runtime[0] % 2 == 0 && isNotDoingAnimation())
                            getBotManager().getPacketHandler().sendOnyxCommand("agility");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 600, TimeUnit.MILLISECONDS);
    }

    private void handleObjectPathing() {
        int[] obstacle = getNextObstacle();
        if(obstacle != null && obstacle.length > 3 && isNotDoingAnimation())
            getBotManager().getPacketHandler().sendObjectOptionByCoord(1, obstacle[0], obstacle[1], obstacle[2]);
    }

    private boolean isNotDoingAnimation() {
        return getBotManager().getPacketHandler().getAnimation() == -1;
    }

    private void setObstacleDone() {
        int[] coords = getBotManager().getLocations().getPlayerLocation();

        int courseComplete = 0;
        for (int[] agilityObstacle : agilityObstacles)
            if (agilityObstacle[3] == 1)
                courseComplete++;

       /* System.out.println(courseComplete);
        System.out.println(agilityObstacles.size());*/

        if (courseComplete == agilityObstacles.size())
            for (int[] obs : agilityObstacles)
                agilityObstacles.set(agilityObstacles.indexOf(obs), new int[]{obs[0], obs[1], obs[2], 0, obs[4], obs[5]});

        for (int[] obs : agilityObstacles) {
            int index = agilityObstacles.indexOf(obs);
            int[] secondFinalObject = agilityObstacles.get(agilityObstacles.size() - 2);
            if(getLocation().equals("gnome_course") && index == 1) {
                if(getBotManager().getLocations().isAtGnomeTreePart1())
                    if (secondFinalObject[3] != 0 || index != (agilityObstacles.size() - 1))
                        agilityObstacles.set(agilityObstacles.indexOf(obs), new int[]{obs[0], obs[1], obs[2], 1, obs[4], obs[5]});
            } else if (coords[0] == obs[4] && coords[1] == obs[5]) {
                if (secondFinalObject[3] != 0 || index != (agilityObstacles.size() - 1)) {
                    agilityObstacles.set(agilityObstacles.indexOf(obs), new int[]{obs[0], obs[1], obs[2], 1, obs[4], obs[5]});
                }
            }
        }
    }

    private int[] getNextObstacle() {
        for(int[] obs : agilityObstacles)
            if(obs[3] == 0)
                return obs;
        return null;
    }

    private void setup() {
        agilityObstacles = new ArrayList<>();
        int[][] obstacles = new int[0][];

        if(getLocation().equals("wilderness_course")) {
            obstacles = new int[][]{{65362, 3004, 3938, 0, 3004, 3949}, {64696, 3005, 3952, 0, 3005, 3958},
                    {64699, 3001, 3960, 0, 2996, 3960}, {64698, 3001, 3945, 0, 2994, 3945},
                    {65734, 2993, 3936, 0, 2994, 3935}};
        } else if(getLocation().equals("gnome_course")) {
            obstacles = new int[][]{{69526, 2474, 3435, 0, 2474,3429}, {69383, 2473, 3425, 0, 2473, 3423},
                    {69508, 2473, 3421, 0, 2473, 3420}, {2312, 2478, 3420, 0, 2483, 3420},
                    {69507, 2487, 3420, 0, 2487, 3421}, {69384, 2485, 3426, 0, 2486, 3427},
                    {69378, 2483, 3431, 0, 2483, 3437}};
        }
        if(obstacles.length > 0)
            agilityObstacles.addAll(Arrays.asList(obstacles));
        getBotManager().startBotUptime = System.currentTimeMillis();
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        int agilityLevel = Skills.getLevelBySkillId(getSkillType());
        if(agilityLevel < 52 && getLocation().equals("wilderness_course")) {
            String warningText = "STOPPING SCRIPT: YOU MUST BE 52 "+Skills.getLevelNameBySkillId(getSkillType())+" TO USE THIS SCRIPT: "+getClass().getName();
            getBotManager().getPacketHandler().sendGameMessage(warningText);
            System.out.println(warningText);
            getBotManager().getBotUI().hideBotUI();
            return false;
        }
        return !isRunning();
    }

    private String getAction() {
        if(getBotManager().isLoggedOut())
            return "LOGIN";
        else if(isAtAgilityCourse())
            return "DOING AGILITY";
        else if(!isAtAgilityCourse())
            return "PATHING";
        return null;
    }

    private boolean isAtAgilityCourse() {
        if(getLocation().equals("wilderness_course"))
            return getBotManager().getLocations().isAtWildernessAgilityCourse();
        else if(getLocation().equals("gnome_course"))
            return getBotManager().getLocations().isAtGnomeAgilityCourse();
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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    private int getSkillType() {
        return Skills.AGILITY;
    }
}

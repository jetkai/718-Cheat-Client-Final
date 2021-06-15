import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 10/02/2016.
 *
 * @ Jet Kai
 */
public class BotManager {

    public long startBotUptime;
    public int uptimeSeconds;
    public int uptimeMinutes;
    public int uptimeHours;
    public int uptimeDays;
    public int itemsInInventory;
    public boolean isBotting = false; //TURN TO FALSE IF YOU DON'T WANT IT TO AUTO BOT ON LOGIN
    public boolean gemZone2 = false;

    private String lastUsername;
    private String lastPassword;

    private String lastStatus;
    private String harvestedResources;

    private final PacketHandler packetHandler;

    //Thieving
    private ThievingStalls thievingStallsScript;
    private PickThemPockets pickThemPockets;

    //Mining
    private GoldMiner goldMiner;
    private GemMiner gemMiner;
    private CoalMiner coalMiner;
    private IronMiner ironMiner;

    //Smithing
    private BarMaker barMaker;
    private CannonBaller cannonBaller;

    //Prayer
    private BoneyAltar boneyAltar;

    //Woodcutting
    private MahoganyChopper mahoganyChopper;
    private RedWoodChopper redWoodChopper;
    private MapleChopper mapleChopper;

    //Agility
    private AgilityCourse agilityCourse;

    //SawMill
    private SawMilly sawMilly;

    //GemCutting
    private GemCrafter gemCrafter;

    //Singing
    private Singing singing;

    //Monitor
    private GameChatMonitor gameChatMonitor;
    private ApiHeartBeat apiHeartBeat;

    //Packet Abusing
    private PlayerOptionSpammer playerOptionSpammer;
    private NullServer nullServer;
    private MysteryBoxAbuser mysteryBoxAbuser;
    private LogoutLogin logoutLogin;

    //BotUI
    private BotUI botUI;

    private final ArrayList<String> botAccounts = new ArrayList<>();
    private int logoutTime;
    private String playerWhoLoggedMeOut;


    public BotManager(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void preloadBotScripts() {
        thievingStallsScript = new ThievingStalls(this);
        goldMiner = new GoldMiner(this);
        gemMiner = new GemMiner(this);
        mahoganyChopper = new MahoganyChopper(this);
        gemCrafter = new GemCrafter(this);
        coalMiner = new CoalMiner(this);
        ironMiner = new IronMiner(this);
        boneyAltar = new BoneyAltar(this);
        redWoodChopper = new RedWoodChopper(this);
        singing = new Singing(this);
        sawMilly = new SawMilly(this);
        barMaker = new BarMaker(this);
        cannonBaller = new CannonBaller(this);
        mapleChopper = new MapleChopper(this);
        agilityCourse = new AgilityCourse(this);
        pickThemPockets = new PickThemPockets(this);
        playerOptionSpammer = new PlayerOptionSpammer(this);
        mysteryBoxAbuser = new MysteryBoxAbuser(this);
        nullServer = new NullServer(this);
        logoutLogin = new LogoutLogin(this);
        botUI = new BotUI(this);
        gameChatMonitor = new GameChatMonitor(this);
        apiHeartBeat = new ApiHeartBeat(this);

      //  apiHeartBeat.start();

       // logoutLogin.start();
        // gameChatMonitor.start();
        initBotUI();
        getBotUI().hideBotUI();
    }

    public void sendBotUptime() {
        long milliseconds = System.currentTimeMillis() - startBotUptime;
        uptimeSeconds = (int) (milliseconds / 1000) % 60;
        uptimeMinutes = (int) ((milliseconds / (1000 * 60)) % 60);
        uptimeHours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        uptimeDays = (int) (milliseconds / (1000 * 60 * 60 * 24));
    }

    public LogoutLogin getLogoutLogin() {
        return logoutLogin;
    }

    public void setGemZone2(boolean gemZone2) {
        this.gemZone2 = gemZone2;
    }

    public int getInventorySize() {
        return itemsInInventory;
    }

    public boolean isBotting() {
        return isBotting;
    }

    public ThievingStalls getThievingStallsScript() {
        return thievingStallsScript;
    }

    public ArrayList<String> getBots() {
        return botAccounts;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public MahoganyChopper getMahoganyChopper() {
        return mahoganyChopper;
    }

    public GemMiner getGemMiner() {
        return gemMiner;
    }

    public Locations getLocations() {
        return new Locations();
    }

    public IronMiner getIronMiner() {
        return ironMiner;
    }

    public CoalMiner getCoalMiner() {
        return coalMiner;
    }

    public GoldMiner getGoldMiner() {
        return goldMiner;
    }

    public MapleChopper getMapleChopper() {
        return mapleChopper;
    }

    public GemCrafter getGemCrafter() {
        return gemCrafter;
    }

    public boolean isDebug() {
        return true;
    }


    private Image background = null;

    public void initBotUI() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("botFrame.png");
            /*Reflection.class.getResource("Reflection.class").toString().contains(".jar")*/
            background = inputStream != null ? ImageIO.read(inputStream) : ImageIO.read(new File("media/botFrame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getBotUI().showBotUI();
    }

    public void sendBotOnline() {
        if(getPacketHandler().isLoggedIn()) {
            Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(() -> {
                Reflection reflection = getPacketHandler().getReflection();
                String username = Class360.username.replaceAll(" ", "_").toLowerCase();
                String password = Class360.password;
                if(!username.equals("") && !password.equals("")) {
                    String[] users = reflection.getTrevorsApi().getOnlineBotsUsernames();
                    reflection.getTrevorsApi().sendBotOnline(username, password, reflection.getProxies().getCurrentProxyAddress());
                    if(users != null) {
                        getPacketHandler().setTrustedAccounts(users);
                       // System.out.println("Online Bots Usernames: " + Arrays.toString(users));
                    }
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    public void drawBotUI() {
        //Loader.botPanel.repaint();
        Graphics g = Loader.botPanel.getGraphics();
        Font font = new Font("Consolas", Font.PLAIN, 13);
        g.drawImage(background, 2, 0, null);

        g.setColor(Color.WHITE);
        g.setFont(font);
        // if (currentBotTask != null) {
        // g.drawString("CURRENT TASK: ", 5, 25);
        g.drawString("HARVESTED: "+getHarvestedResources(), 8, 65);
        g.drawString("TASK: "+getLastStatus(), 8, 85);
        g.drawString("RUNTIME: "+String.format("%02d:%02d:%02d", getUptimeHours(),+getUptimeMinutes(), getUptimeSeconds()), 8, 105);
        // } else {
        // g.drawString("MAGICCUTTER V", 5, 125);

        Loader.botPanel.paintComponents(g);

        System.out.println("Drawing & Repacking");
        // Loader.frame.pack();
        System.out.println("Drawing & Repacking Done");
        // Loader.botPanel.repaint();
    }

    private boolean rotatePremiumProxy = false;

    public void handleAutoLogin() {
        if(!rotatePremiumProxy && (getLogoutTime() >= 15 && getLogoutTime() <= 30) && getPacketHandler().getReflection().getAntiBanToggle().isUsingPaidProxies()) {
            getPacketHandler().getReflection().getAntiBanToggle().updateProxy();
            rotatePremiumProxy = true;
        } else if(getLogoutTime() == 4) {
            Class63.method741(getLastUsername(), getLastPassword(), 2101690439);
            setLogoutTime(getLogoutTime() - 1);
            rotatePremiumProxy = false;
        } else if(getLogoutTime() > 0) {
            setLogoutTime(getLogoutTime() - 1);
            System.out.println("Logout Time Remaining: "+getLogoutTime());
        }
    }

    public void startScript(String plugin) {
        if(plugin.equals("PLUGINS")) {
            JOptionPane.showMessageDialog(Loader.frame, "You must select a \"PLUGIN\"\nfrom the drop-down menu", "PLUGINS", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        initBotUI();
        plugin = plugin.replaceAll("_", "").toLowerCase();
        getPacketHandler().sendGameMessage("<col=1a0b63><shad=00ffff><img=3> STARTING \"PLUGIN\": ["+plugin.toUpperCase()+"]");
        if (plugin.length() > 1) {
            sendBotOnline(); // Sends Heart Beat
            switch (plugin) {
                case "thieving":
                case "thievinghome":
                    getThievingStallsScript().setLocation("home");
                    getThievingStallsScript().start();
                    break;
                case "thieving2":
                case "thievingcammy":
                    getThievingStallsScript().setLocation("camelot_castle");
                    getThievingStallsScript().start();
                    break;
                case "thieving3":
                case "thievingdzone":
                    getThievingStallsScript().setLocation("donator_zone");
                    getThievingStallsScript().start();
                    break;
                case "gemminer":
                    setGemZone2(false);
                    getGemMiner().start();
                    break;
                case "gemminer2":
                    setGemZone2(true);
                    getGemMiner().start();
                    break;
                case "gemcrafter":
                case "gemcutter":
                    getGemCrafter().start();
                    break;
                case "prayer":
                case "boneyaltar":
                    getBoneyAltar().start();
                    break;
                case "coalminer":
                    getCoalMiner().setLocation("lovakenj_1");
                    getCoalMiner().start();
                    break;
                case "coalminer2":
                    getCoalMiner().setLocation("lovakenj_2");
                    getCoalMiner().start();
                    break;
                case "ironminer":
                    getIronMiner().setLocation("varrock");
                    getIronMiner().start();
                    break;
                case "goldminer":
                    getGoldMiner().setLocation("brimheaven_1");
                    getGoldMiner().start();
                    break;
                case "goldminer2":
                    getGoldMiner().setLocation("crafting_guild_1");
                    getGoldMiner().start();
                    break;
                case "goldminer3":
                    getGoldMiner().setLocation("crafting_guild_2");
                    getGoldMiner().start();
                    break;
                case "mahogany":
                    getMahoganyChopper().setLocation("tai_bwo_wannai");
                    getMahoganyChopper().start();
                    break;
                case "mahogany2":
                    getMahoganyChopper().setLocation("farming_guild");
                    getMahoganyChopper().start();
                    break;
                case "redwood":
                    getRedWoodChopper().setLocation("redwood_2");
                    getRedWoodChopper().start();
                    break;
                case "sawmill":
                case "sawmilly":
                    getSawMilly().start();
                    break;
                case "barmaker":
                case "steelbars":
                    getBarMaker().start();
                    break;
                case "trivia":
                case "reaction":
                    getGameChatMonitor().start();
                    getBotUI().hideBotUI();
                    break;
                case "cannonballer":
                case "cannonballs":
                    getCannonBaller().start();
                    break;
                case "maple":
                case "maples":
                    getMapleChopper().setLocation("seers");
                    getMapleChopper().start();
                    break;
                case "agility":
                case "gnomecourse":
                    getWildernessAgility().setLocation("gnome_course");
                    getWildernessAgility().start();
                    break;
                case "wildernessagility":
                    getWildernessAgility().setLocation("wilderness_course");
                    getWildernessAgility().start();
                    break;
                case "martinpickpocket":
                case "draynorpickpocket":
                case "farmerpickpocket":
                    getPickThemPockets().setLocation("draynor");
                    getPickThemPockets().start();
                    break;
                case "ardougneknightpickpocket":
                    getPickThemPockets().setLocation("ardougne_knight");
                    getPickThemPockets().start();
                    break;
                case "ardougnepaladinpickpocket":
                    getPickThemPockets().setLocation("ardougne_paladin");
                    getPickThemPockets().start();
                    break;
                case "dwarfpickpocket":
                case "keldagrimpickpocket":
                    getPickThemPockets().setLocation("keldagrim");
                    getPickThemPockets().start();
                    break;
                case "mystery":
                    getMysteryBoxAbuser().start();;
                    break;
                case "nullserver":
                    System.out.println("nulling server");
                    getNullServer().start();
                    break;
                case "loginflood":
                    System.out.println("nulling server");
                    getLogoutLogin().start();
                    break;
            }
        }
    }

    public void stopScripts() {
        getBotUI().hideBotUI();
        getThievingStallsScript().stop();
        getGoldMiner().stop();
        getGemMiner().stop();
        getMahoganyChopper().stop();
        getGemCrafter().stop();
        getCoalMiner().stop();
        getIronMiner().stop();
        getBoneyAltar().stop();
        getRedWoodChopper().stop();
        getSinging().stop();
        getSawMilly().stop();
        getBarMaker().stop();
        getCannonBaller().stop();
        getMapleChopper().stop();
        getPlayerOptionSpammer().stop();
        getWildernessAgility().stop();
        getPickThemPockets().stop();
        getMysteryBoxAbuser().stop();
        getNullServer().stop();
        getLogoutLogin().stop();
        setLogoutTime(0);
    }

    public NullServer getNullServer() {
        return nullServer;
    }

    public MysteryBoxAbuser getMysteryBoxAbuser() {
        return mysteryBoxAbuser;
    }

    public PickThemPockets getPickThemPockets() {
        return pickThemPockets;
    }

    public AgilityCourse getWildernessAgility() {
        return agilityCourse;
    }

    public PlayerOptionSpammer getPlayerOptionSpammer() {
        return playerOptionSpammer;
    }

    public void setHarvestedResources(String harvestedResources) {
        this.harvestedResources = harvestedResources;
    }

    public String getHarvestedResources() {
        return harvestedResources;
    }

    public int getUptimeHours() {
        return uptimeHours;
    }

    public int getUptimeMinutes() {
        return uptimeMinutes;
    }

    public int getUptimeSeconds() {
        return uptimeSeconds;
    }

    public int getUptimeDays() {
        return uptimeDays;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public BotUI getBotUI() {
        return botUI;
    }

    public void setLogoutTime(int i) {
        this.logoutTime = i;
    }

    public int getLogoutTime() {
        return logoutTime;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public boolean isLoggedOut() {
        return getLogoutTime() > 0;
    }

    public String getLastPassword() {
        return lastPassword;
    }

    public BoneyAltar getBoneyAltar() {
        return boneyAltar;
    }

    public RedWoodChopper getRedWoodChopper() {
        return redWoodChopper;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public void setLastPassword(String lastPassword) {
        this.lastPassword = lastPassword;
    }

    public void setPlayerWhoLoggedMeOut(String playerName) {
        this.playerWhoLoggedMeOut = playerName;
    }

    public BarMaker getBarMaker() {
        return barMaker;
    }

    public String getPlayerWhoLoggedMeOut() {
        return playerWhoLoggedMeOut;
    }

    public SawMilly getSawMilly() {
        return sawMilly;
    }

    public GameChatMonitor getGameChatMonitor() {
        return gameChatMonitor;
    }

    public Singing getSinging() {
        return singing;
    }

    public CannonBaller getCannonBaller() {
        return cannonBaller;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Reflection getReflection() {
        return packetHandler.getReflection();
    }
}
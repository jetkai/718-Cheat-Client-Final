import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author D
 */
public class Reflection {

    private Loader loader = null;
    private static client client = null;

    private BotManager botManager;

    private final PacketHandler packetHandler = new PacketHandler(this);
    private final AntiBanToggle antiBanToggle = new AntiBanToggle(this);
    private final Proxies proxies = new Proxies(this);
    private final BotApi botApi = new BotApi(this);

    private Field[] loginFields;

    private String CUSTOM_MAC_ADDRESS;
    private String CUSTOM_SERIAL;

    private String currentProxyAddress;
    private String currentProxyCredentials;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        FlatCarbonIJTheme.install();
        Reflection reflection = new Reflection();
        try {
            reflection.init();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void init() throws NoSuchFieldException, IllegalAccessException {
        loader = new Loader(this);
        loader.init();
        finishReflection();
    }

    private void finishReflection() throws IllegalAccessException, NoSuchFieldException {
        if(loader != null) {

            Field[] loaderFields = loader.getClass().getDeclaredFields();
            client = (client) getField("client", loaderFields).get(loader);

            Field[] clientFields = client.getClass().getDeclaredFields();
            Method[] clientMethods = client.getClass().getDeclaredMethods();
            Constructor<?>[] clientConstructors = client.getClass().getConstructors();

            for (Field field : clientFields)
                field.setAccessible(true);
            for (Method method : clientMethods)
                method.setAccessible(true);
            for (Constructor<?> con: clientConstructors)
                con.setAccessible(true);

            updateClientLoginFields();
            System.out.println("[client]:   Found " + clientMethods.length + " methods and " + clientFields.length + " fields");
            System.out.println("Reflection complete.");

            botManager = new BotManager(packetHandler);
            botManager.preloadBotScripts();
            antiBanToggle.setLocationByPlatform(true);
            antiBanToggle.setUndecorated(true);
            antiBanToggle.pack();
            antiBanToggle.setVisible(false);
            antiBanToggle.refreshLists();

            commands("newmac");
            commands("newserial");

            CUSTOM_MAC_ADDRESS = Class460.MAC_ADDRESS;
            CUSTOM_SERIAL = Class460.hwid();
        } else {
            System.out.println("WARNING!! LOADER IS NULL.");
        }
    }

    public void updateClientLoginFields() throws NoSuchFieldException, IllegalAccessException {
        Class460 loginClass = new Class460(client.aClass437_8841);

        loginFields = loginClass.getClass().getDeclaredFields();

        for (Field field : loginFields) {
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

        Method[] loginMethods = loginClass.getClass().getDeclaredMethods();
        Constructor<?>[] loginConstructors = loginClass.getClass().getConstructors();
        for (Method method : loginMethods)
            method.setAccessible(true);
        for (Constructor<?> con: loginConstructors)
            con.setAccessible(true);

        System.out.println("[Class460]: Found " + loginMethods.length + " methods and " + loginFields.length + " fields");
    }

    public void commands(String command) throws IllegalAccessException, NoSuchFieldException {
        packetHandler.sendGameMessage("<col=1a0b63><shad=00ffff><img=3> LAST COMMAND: "+command.toUpperCase()+"");
        System.out.println("Last Command: "+command);
        String[] commandArgs = command.split(" ");
        command = commandArgs[0];
        if(command.equalsIgnoreCase("initbots")) {
            botManager = new BotManager(packetHandler);
            botManager.preloadBotScripts();
        } else if(command.equals("getbotsonline")) {
            System.out.println(Arrays.toString(getTrevorsApi().getOnlineBots()));
        } else if(command.equals("getlastbots")) {
            System.out.println(Arrays.toString(getTrevorsApi().getPreviouslyOnlineBots()));
        } else if(command.equals("getusedproxies")) {
            System.out.println(Arrays.toString(getTrevorsApi().getCurrentBotsProxies()));
        } else if(command.equalsIgnoreCase("serial")) {
            System.out.println(Hardware4Win.getSerialNumber());
        } else if(command.equalsIgnoreCase("chatt")) {
            ClientScript2 class403 = Class211.method1950(1034965053);
            String string = (String)class403.anObjectArray5240[(class403.anInt5241 -= 969361751) * -203050393];
            System.out.println(string);
            //Class70.method803();
        } else if(command.equalsIgnoreCase("say")) {
            StringBuilder stringBuilder = new StringBuilder();
            for(String text : commandArgs)
                stringBuilder.append(text).append(" ");
            System.out.println((getProxies().sendPostRequest("http://www.gizoogle.net/textilizer.php", stringBuilder.toString())));
        } else if(command.equalsIgnoreCase("title")) {
            if (commandArgs.length > 1)
                loader.frame.setTitle(commandArgs[1]);
        } else if(command.equalsIgnoreCase("antiban")) {
            SwingUtilities.invokeLater(() -> {
                antiBanToggle.updateUI();
                antiBanToggle.setLocationRelativeTo(Loader.panel);
                antiBanToggle.setVisible(true);
            });
        } else if(command.equalsIgnoreCase("quiztest")) {
            packetHandler.sendGameMessage("Broadcast: [Pop quiz] What is 4.0 minus 4.0 plus 8.0? ::quiz #");
        } else if(command.equalsIgnoreCase("login")) {
            packetHandler.sendLoginRequest(null, null, null);
        } else if(command.equalsIgnoreCase("newmac")) {
            updateClientLoginFields();
            Field macAddress = getField("MAC_ADDRESS", loginFields);
            if (macAddress != null) {
                System.out.println("Before Mac Change: " + getField("MAC_ADDRESS", loginFields).get("MAC_ADDRESS"));
                CUSTOM_MAC_ADDRESS = getRandomMacAddress().toUpperCase();
                macAddress.set(macAddress, CUSTOM_MAC_ADDRESS);
                System.out.println("After Mac Change: " + getField("MAC_ADDRESS", loginFields).get("MAC_ADDRESS"));
            } else {
                System.out.println("Issue changing the Mac Address.");
            }
        } else if(command.equalsIgnoreCase("setmac")) {
            updateClientLoginFields();
            Field macAddress = getField("MAC_ADDRESS", loginFields);
            if (macAddress != null) {
                System.out.println("Before Mac Change: " + getField("MAC_ADDRESS", loginFields).get("MAC_ADDRESS"));
                CUSTOM_MAC_ADDRESS = commandArgs[1];
                macAddress.set(macAddress, CUSTOM_MAC_ADDRESS);
                System.out.println("After Mac Change: " + getField("MAC_ADDRESS", loginFields).get("MAC_ADDRESS"));
            } else {
                System.out.println("Issue changing the Mac Address.");
            }
        } else if(command.equalsIgnoreCase("mymac")) {
            packetHandler.sendGameMessage("Assigned Mac Address:"+Class460.MAC_ADDRESS);
        } else if(command.equalsIgnoreCase("newserial")) {
            updateClientLoginFields();
            Field hwid = getField("HWID", loginFields);
            if (hwid != null) {
                System.out.println("Before HWID Change: " + getField("HWID", loginFields).get("HWID"));
                CUSTOM_SERIAL = "System";
                hwid.set(hwid, CUSTOM_SERIAL);
                System.out.println("After HWID Change: " + getField("HWID", loginFields).get("HWID"));
            } else {
                System.out.println("Issue changing the Serial.");
            }
        } else if(command.equalsIgnoreCase("subbuild") || command.equalsIgnoreCase("sub")) {
            if(commandArgs.length > 1) {
                Settings.SUB_BUILD = Integer.parseInt(commandArgs[1]);
                System.out.println("Updated Sub-Build: "+Settings.SUB_BUILD);
            }
        } else if(command.equalsIgnoreCase("pass")) {
            System.out.println(Class360.username + ":" + Class360.password);
        } else if(command.equalsIgnoreCase("resetlogout")) {
            getBotManager().setLogoutTime(5);
        } else if(command.equalsIgnoreCase("poni")) {
            if (commandArgs.length > 3) {
                packetHandler.sendInterfaceOnPlayer(Integer.parseInt(commandArgs[1]), Integer.parseInt(commandArgs[2]),
                        Integer.parseInt(commandArgs[3]), Integer.parseInt(commandArgs[4]),  Integer.parseInt(commandArgs[5]));
            } else {
                System.out.println("Not enough args");
            }
        } else if(command.equalsIgnoreCase("button")) {
            if (commandArgs.length > 3) {
                packetHandler.sendButtonClickOnInterface(Integer.parseInt(commandArgs[1]), Integer.parseInt(commandArgs[2]),
                        Integer.parseInt(commandArgs[3]), Integer.parseInt(commandArgs[4]), Integer.parseInt(commandArgs[5]));
            } else {
                System.out.println("Not enough args");
            }
        } else if(command.equalsIgnoreCase("msg")) {
            if (commandArgs.length > 3) {
                GrabFolder.method2282(Integer.parseInt(commandArgs[1]), Integer.parseInt(commandArgs[2]), commandArgs[3],
                        commandArgs[4], commandArgs[5], commandArgs[6], -1468983571);
            } else {
                System.out.println("Not enough args");
            }
        } else if(command.equalsIgnoreCase("myinv")) {
            System.out.println(Arrays.deepToString(packetHandler.getInventoryItems()));
        } else if(command.equalsIgnoreCase("io")) {
            if(commandArgs.length > 4) {
                packetHandler.sendInterfaceOnObject(Integer.parseInt(commandArgs[1]), Integer.parseInt(commandArgs[2]), Integer.parseInt(commandArgs[3]),
                        Integer.parseInt(commandArgs[4]), Integer.parseInt(commandArgs[5]), Integer.parseInt(commandArgs[6]));
            } else {
                System.out.println("Not enough args");
            }
        } else if(command.equalsIgnoreCase("hide")) {
            getBotManager().getBotUI().hideBotUI();
        } else if(command.equalsIgnoreCase("show")) {
            getBotManager().getBotUI().showBotUI();
        } else if(command.equalsIgnoreCase("dialogue")) {
            if (commandArgs.length > 2)
                packetHandler.sendDialogueOption(Integer.parseInt(commandArgs[1]), Integer.parseInt(commandArgs[2]), Integer.parseInt(commandArgs[3]));
            else
                System.out.println("Not enough args");
        } else if(command.equals("sing")) {
            if (commandArgs.length > 1) {
                getBotManager().getSinging().setSongId(Integer.parseInt(commandArgs[1]));
                botManager.getSinging().start();
            } else {
                System.out.println("Not enough args");
            }
        } else if(command.equalsIgnoreCase("test")) {
            /* WorldMap.method3706(); System.out.println("DecodeMap: " + decodeMap(0, 50, 0, 5000)); //WorldMap.decodeFromMap(); System.out.println(WorldMap.currentArea);
            System.out.println(Arrays.toString(WorldMap.aClass453_3236.toArray())); System.out.println(Class301_Sub1.currentArea); //WorldMap.decodeMap(1,1,1,1);
            System.out.println(Arrays.toString(WorldMap.worldObjects));// System.out.println(WorldMap.aClass375_3253.) ObjectConfig dreamTree = getObjectConfig(16604);
            System.out.println(dreamTree.name); */
            for(Object object : Class298_Sub1.aClass453_7152) {
                Map class341 = client.aClass283_8716.method2628(681479919);

                int objectId = (((Class298_Sub1) object).anInt7156 * 856355825);

                int objectLocalX = (((Class298_Sub1) object).anInt7150 * 634196087);
                int objectLocalY = (((Class298_Sub1) object).anInt7155 * -2146829167);
                int coordX = (objectLocalX) + (class341.gameSceneBaseX * -1760580017);
                int coordY = (objectLocalY) + (class341.gameSceneBaseY * 283514611);

                System.out.println("ObjectName: "+packetHandler.getObjectDefinition(objectId).name);
                System.out.println("ObjectId: " +objectId);
                System.out.println("ObjectLocalX: "+objectLocalX);
                System.out.println("ObjectLocalY: "+objectLocalY);
                System.out.println("ObjectX: "+coordX);
                System.out.println("ObjectX: "+coordY);
            }
        } else if(command.equalsIgnoreCase("examine")) {
            int posX = getBotManager().getLocations().getCoordX();
            int posY = getBotManager().getLocations().getCoordY();
            int posZ = getBotManager().getLocations().getCoordZ();
            System.out.println("MyPos: " + posX + ", " + posY+", "+posZ);
            if (commandArgs.length > 3) {
                int option = Integer.parseInt(commandArgs[1]);
                int objectId = Integer.parseInt(commandArgs[2]);
                int coordX = Integer.parseInt(commandArgs[3]);
                int coordY = Integer.parseInt(commandArgs[4]);
                packetHandler.sendObjectOptionByCoord(option, objectId, coordX, coordY);
            }
        } else if(command.equalsIgnoreCase("coords")) {
            int[] coords = new int[]{getBotManager().getLocations().getCoordX(), getBotManager().getLocations().getCoordY(),
                    getBotManager().getLocations().getCoordZ()};
            getBotManager().getPacketHandler().sendGameMessage(Arrays.toString(coords));
            System.out.println("MyPos: " + Arrays.toString(coords));
        }
        else if(command.equalsIgnoreCase("crash1")) {
            packetHandler.crashRegion();
        } else if(command.equalsIgnoreCase("crash2")) {
            ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
            threadPool.scheduleAtFixedRate(packetHandler::crashRegion, 0, 800, TimeUnit.MILLISECONDS);
        } else if(command.equalsIgnoreCase("npu")) {
            packetHandler.test();
        } else if(command.equalsIgnoreCase("nbrute")) {
            ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
            final int[] npcIndex = {0};//dwarf trader 8500+
            threadPool.scheduleAtFixedRate(() -> {
                String attemptMessage = "Attempting NpcIndex: "+ npcIndex[0];
                packetHandler.sendNpcOptionByName(7, "Sawmill operator"/*Knight of Ardougne*/, npcIndex[0]);
                System.out.println(attemptMessage);
               // packetHandler.sendGameMessage(attemptMessage);
                npcIndex[0]++;
            }, 0, 200, TimeUnit.MILLISECONDS);
        } else if(command.equalsIgnoreCase("npce")) {
            Class235 class235 = getPlayer().method4337();
            Map class341 = client.aClass283_8716.method2628(681479919);
            int posX = ((int)class235.aClass217_2599.x >> 9) + class341.gameSceneBaseX * -1760580017;
            int posY = ((class341.gameSceneBaseY * 283514611) + ((int)class235.aClass217_2599.y >> 9));
            System.out.println("MyPos: "+posX+", "+posY);
            if(commandArgs.length >= 3) {
                int option = Integer.parseInt(commandArgs[1]);
                int npcIndex = Integer.parseInt(commandArgs[2]);
                packetHandler.sendNpcOptionByName(option, "Sawmill operator", npcIndex);
                System.out.println("Sent NPC Option "+option);
            }
        } else if(command.equalsIgnoreCase("ex")) {
            System.out.println(Arrays.toString(getPlayerPos()));
            Class235 class235 = getPlayer().method4337();
            Class326 class326 = getPlayer().aClass331_7722.aClass326ArrayArrayArray3516[getPlayer().plane]
                    [(int)class235.aClass217_2599.x >> 9][(int)class235.aClass217_2599.y >> 9];
            System.out.println((int)class235.aClass217_2599.x >> 3);
            System.out.println((int)class235.aClass217_2599.y >> 3);

            // >> 3 = actual coord
            // >> 9 = local coord
            System.out.println(getPlayer().plane);
            //Class298_Sub1.aClass453_7152
            for(Object object : Class298_Sub1.aClass453_7152.toArray()) {
                Class298_Sub1 class298 = (Class298_Sub1) object;

                int objectId = (856355825 * class298.anInt7156);
                int localX = (class298.anInt7155 / 284247153);
                int localY = (class298.anInt7157 / 1088435253);

                int localPlayerX = ((int)class235.aClass217_2599.x >> 9);
                int localPlayerY = ((int)class235.aClass217_2599.y >> 9);

                int x = (int)/*((int)class235.aClass217_2599.x >> 9) + */ 656787783 / class298.anInt7150;//(class298.anInt7155 / 284247153)
                int y = (int)/*((int)class235.aClass217_2599.y >> 9) + */class298.anInt7155 / 284247153;//(class298.anInt7157 / 1088435253)

                int objectSizeX = (class298.anInt7153 / 998055383);
                int objectSizeY = (class298.anInt7149 / 1034640391);

                System.out.println("Tree X: "+ x);
                System.out.println("Tree Y: "+ y);

                if(objectId != -1)
                    packetHandler.sendObjectOption(PacketHandler.EXAMINE_OBJECT_OPTION, objectId, x,y);
                //   System.out.println(class298.anInt7148);
                // System.out.println(class298.anInt7149 / 1034640391);
                System.out.println("1:: "+(class298.anInt7150 * 656787783)); //ObjectId
                System.out.println((class298.anInt7151 /  -196260341)); //Z
                // System.out.println(class298.anInt7153 / 998055383);
                // System.out.println(class298.anInt7154);
                System.out.println((class298.anInt7155 / 284247153)); //X
                // System.out.println(class298.anInt7156);
                System.out.println("4:: "+(class298.anInt7157 / 1088435253)); //Y
                // System.out.println(class298.anInt7161);
                System.out.println(856355825 * class298.anInt7156); //ObjectId
            }
            // Class298_Sub1.aClass453_7152.toArray()

            System.out.println(Arrays.toString(Class298_Sub1.aClass453_7152.toArray()));
        } else if(command.equalsIgnoreCase("object")) {
            int i_76_ = getPlayerLocalCoords()[0];
            int i_77_ = 134435705 * Class162.baseTileX + (i_76_ >> 4 & 0x7);
            int i_78_ = -105526719 * Class216.baseTileY + (i_76_ & 0x7);
            int i_79_ = getPlayerLocalCoords()[1];
            int i_80_ = i_79_ >> 2;
            int i_81_ = i_79_ & 0x3;
            int i_82_ = client.anIntArray8739[i_80_];
            int i_83_ = 16604;
            if (client.aClass283_8716.method2674(39788259).method2522((byte)29) || (i_77_ >= 0 && i_78_ >= 0 && i_77_ < client.aClass283_8716.method2629(-2063854515) && i_78_ < client.aClass283_8716.method2630(-1828303048)))
                Class420.method5606(-191892109 * Class375.basePlane, i_77_, i_78_, i_82_, i_83_, i_80_, i_81_, 2010363527);
        } else if(command.equalsIgnoreCase("npcs")) {
            npcs();
        } else if(command.equalsIgnoreCase("clienttest")) {
            JOptionPane.showMessageDialog(Loader.frame, "TESTING TESTING 123", "<><", JOptionPane.QUESTION_MESSAGE);
        } else if(command.equalsIgnoreCase("trivia") || command.equalsIgnoreCase("reaction")) {
            getBotManager().getGameChatMonitor().start();
            System.out.println("Trivia & Recation enabled");
        } else if(command.equalsIgnoreCase("admin")) {
            client.playerRights = 2;
        } else if(command.equalsIgnoreCase("itest")) {
            packetHandler.getInventoryItems();
            System.out.println("AmIFull?:"+ packetHandler.isInventoryFull());
        } else if(command.equalsIgnoreCase("start")) {
            getBotManager().startScript(commandArgs[1]);
        } else if(command.equalsIgnoreCase("stop")) {
            getBotManager().stopScripts();
        } else if(command.equalsIgnoreCase("reveal")) {
            packetHandler.revealHiddenStaff();
        } else if(command.equalsIgnoreCase("walk")) {
            Class235 class235 = getPlayer().method4337();
            int coordx = (botManager.getLocations().getPlayerLocation()[0] - 2743);
            int coordy = (botManager.getLocations().getPlayerLocation()[1] - 3142);
            System.out.println("Coord After Sub: "+coordx+", "+coordy);
            int finalX = ((int) class235.aClass217_2599.x >> 9) + coordx;
            int finalY = ((int) class235.aClass217_2599.y >> 9) + coordy;
            System.out.println("Final:"+finalX+", "+finalY);
            packetHandler.sendWalkPacket(finalX, finalY);
            System.out.println(Arrays.toString(getPlayerLocalCoords()));
            System.out.println("Attempting to walk");
        } else if(command.equalsIgnoreCase("emote")) {
            if(commandArgs.length > 1)
                packetHandler.sendAnimation(Integer.parseInt(commandArgs[1]));
        } else if(command.equalsIgnoreCase("myanim")) {
            System.out.println(packetHandler.getAnimation());
        } else if(command.equalsIgnoreCase("getchat")) {
            packetHandler.getGameChatMessages();
        } else if(command.equalsIgnoreCase("optionspam")) {
            getBotManager().getPlayerOptionSpammer().start();
        }
    }

    public int npcs() {
        int index = 0;
        for(ObjectValue npcs : client.npcs) {
            if(npcs != null) {
                NPC npc = (NPC) npcs.value;
                if(npc != null) {
                    System.out.println("NPC: " + npc.aString10186 + ", " + index);
                    if(npc.aClass498_10181 != null) {
                        long l = 0;
                        System.out.println("Index: " + npc.aClass498_10181.aLong6110);
                        System.out.println("Index2: " + (l |= -8495627389615588201L * npc.aClass498_10181.aLong6110 << 24L));
                        System.out.println("Index3: "+(npc.aClass498_10181.aLong6110 * 2236412381003659263L));
                    }
                }
                index++;
            }
        }
        return client.npcs.length;
    }

    public ObjectValue getNpcByName(String npcName) {
        for(ObjectValue npc : client.npcs) {
            if(npc != null) {
                if(((NPC) npc.value).aString10186.equals(npcName))
                    return npc;
            }
        }
        return null;
    }

    public static boolean isTriHardChat = true;

    public static void TriHardIfy(String originalMessage) {
        final boolean[] success = {false};
        final int[] attempts = {0};
        if(originalMessage.startsWith("::") || originalMessage.startsWith(";;") || originalMessage.startsWith("/")) {
            Class25 class25 = Class429.method5760((short) 512);
            Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.CHAT_PACKET, class25.aClass449_330, (byte) 107);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
            int i_4_ = 385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index;
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
            Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, originalMessage, 1526854691);
            class298_sub36.aClass298_Sub53_Sub2_7396.method3649(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_4_, (byte) -57);
            class25.method390(class298_sub36, (byte) -12);
            return;
        }
        Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(() ->{
            while(!success[0]) {
                if(attempts[0] > 14)
                    success[0] = true;
                StringBuilder data = new StringBuilder();
                try {
                    URL api = new URL("http://www.gizoogle.net/textilizer.php");
                    HttpURLConnection con = (HttpURLConnection) api.openConnection();
                    con.setConnectTimeout(2000);
                    con.setReadTimeout(2000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
                    con.setUseCaches(true);
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("charset", "utf-8");
                    String urlParameters = "translatetext=" + originalMessage;
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                    int postDataLength = postData.length;
                    con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                        wr.write(postData);
                    }
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        data.append(inputLine).append(System.lineSeparator());
                    in.close();
                } catch (IOException io) {
                    attempts[0]++;
                    System.err.println("Failed to connect to TriHard API - Retrying... "+attempts[0] + " / 15");
                }
                String output = data.toString();
                if (output.length() > 10)
                    output = output.substring(output.indexOf("height:250px;\"/>") + "height:250px;\"/>".length(), (output.indexOf("</textarea>") - 1));
                if(output.length() > 0 && output.length() < 199) {
                    success[0] = true;
                    Class25 class25 = Class429.method5760((short) 512);
                    Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.CHAT_PACKET, class25.aClass449_330, (byte) 107);
                    class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
                    int i_4_ = 385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index;
                    class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(1);
                    class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
                    Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, output, 1526854691);
                    class298_sub36.aClass298_Sub53_Sub2_7396.method3649(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_4_, (byte) -57);
                    class25.method390(class298_sub36, (byte) -12);
                }
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public Player getPlayer() {
        return Class287.myPlayer;
    }

    public float[] getPlayerPos() {
        SceneObjectPosition class217 = SceneObjectPosition.method2005(getPlayer().method4337().aClass217_2599);
        return new float[]{class217.x, class217.y};
    }

    public int[] getPlayerLocalCoords() {
        Class235 class235 = getPlayer().method4337();
        return new int[]{(int)class235.aClass217_2599.x >> 9, (int)class235.aClass217_2599.y >> 9, getPlayer().plane};
    }

    public  Field getField(String s, Field[] fields) {
        return Arrays.stream(fields).filter(field -> field.getName().equals(s)).findFirst().orElse(null);
    }

    public Method getMethod(String s, Method[] methods) {
        return Arrays.stream(methods).filter(method -> method.getName().equals(s)).findFirst().orElse(null);
    }

/*    private String getRandomMacAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);
        macAddr[0] = (byte)(macAddr[0] & (byte)254);
        StringBuilder sb = new StringBuilder(18);
        for(byte b : macAddr){
            if(sb.length() > 0)
                sb.append("-");
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }*/

    public static String getRandomMacAddress() {
        Random r = new Random();
        StringBuilder mac = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int n = r.nextInt(255);
            if(mac.length() > 0)
                mac.append("-");
            mac.append(String.format("%02x", n));
        }
        return mac.toString().toUpperCase();
    }

    public BotApi getTrevorsApi() {
        return botApi;
    }

    public String getCustomMacAddress() {
        return CUSTOM_MAC_ADDRESS;
    }

    public String getCustomSerial() {
        return CUSTOM_SERIAL;
    }

    public String getCurrentProxyAddress() {
        return currentProxyAddress;
    }

    public String getCurrentProxyCredentials() {
        return currentProxyCredentials;
    }

    public void setCurrentProxyAddress(String currentProxyAddress) {
        this.currentProxyAddress = currentProxyAddress;
    }

    public void setCurrentProxyCredentials(String currentProxyCredentials) {
        this.currentProxyCredentials = currentProxyCredentials;
    }

    public Proxies getProxies() {
        return proxies;
    }

    public AntiBanToggle getAntiBanToggle() {
        return antiBanToggle;
    }

    public client getClient() {
        return client;
    }

    public BotManager getBotManager() {
        return botManager;
    }
}

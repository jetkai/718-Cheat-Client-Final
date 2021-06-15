import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PacketHandler {

    private final Reflection reflection;

    public PacketHandler(Reflection reflection) {
        this.reflection = reflection;
    }

    /** Commands **/
    public void sendOnyxCommand(String command) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.COMMANDS_PACKET, client.aClass25_8711.aClass449_330, (byte)6);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(command.length() + 3);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString(command, 2127017558);
        client.aClass25_8711.method390(class298_sub36, (byte)-55);
    }

    public static int EXAMINE_OBJECT_OPTION = 6;
    /** Objects **/
    public void sendObjectOption(int option, int objectId, int i_32_, int i_33_) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        OutcommingPacket packet = null;
        int i = Class165.aClass319_6366.method3894((byte)-80);
        int i_30_ = Class165.aClass319_6366.method3883((byte)-10);
      /*  int i_32_ = -887503319 * class298_sub37_sub15.anInt9658;
        int i_33_ = class298_sub37_sub15.anInt9663 * -502720623;
        int i_34_ = 946432351 * class298_sub37_sub15.anInt9662;
        int i_35_ = (int)(class298_sub37_sub15.aLong9661 * 2236412381003659263L);
        long l = 2236412381003659263L * class298_sub37_sub15.aLong9661;*/
        // objectId = (-4396451777151645697L * objectId);
        System.out.println("Big: "+objectId);
        //  long l = 2236412381003659263L * objectId;
        //  System.out.println("Big2: "+l);
        Map class341 = client.aClass283_8716.method2628(681479919);
        System.out.println("SendingItem: "+objectId/*((int)(l >>> 32L) & Integer.MAX_VALUE)*/);
        System.out.println("SendingX: "+(i_32_ + class341.gameSceneBaseX * -1760580017));
        System.out.println("SendingY: "+(class341.gameSceneBaseY * 283514611 + i_33_));
        if (option == 1) {
            packet = OutcommingPacket.aClass198_2019;
        } else if (option == 2) { // Option 2
            packet = OutcommingPacket.aClass198_2052;
        } else if (option == 3) {
            packet = OutcommingPacket.aClass198_2033;
        } else if (option == 4) {
            packet = OutcommingPacket.aClass198_2016;
        } else if (option == 5) {
            packet = OutcommingPacket.aClass198_2012;
        } else if (option == 6) {
            packet = OutcommingPacket.EXAMINE_OBJECT_PACKET;
        }
        if (packet != null) {
            client.anInt8784 = 143636043 * i;
            client.anInt8785 = i_30_ * 381532777;
            client.anInt8748 = 554324810;
            client.anInt8786 = 0;
            if (OutcommingPacket.EXAMINE_OBJECT_PACKET != packet) {
                Class336 class336;
                // int i_3_ = (int)l >> 14 & 0x1F;
                //int i_4_ = (int)l >> 20 & 0x3;
                // int i_5_ = (int)(l >>> 32L) & Integer.MAX_VALUE;
                Class424 class424 = (Class424)Class422_Sub20.method5701((Interface21[])Class336_Sub6.method4108(114624527), i_32_, (byte)2);
                if (Class424.aClass424_6611 == class424 || Class424.aClass424_6604 == class424 || Class424.aClass424_6610 == class424) {
                    int i_6_, i_7_;
                    ObjectConfig class432 = client.aClass283_8716.method2641(-1208362615).getObjectDefinitions(objectId);
                    if (0 == i_33_ || i_33_ == 2) {
                        i_6_ = -1125834887 * class432.sizeX;
                        i_7_ = -565161399 * class432.sizeY;
                    } else {
                        i_6_ = -565161399 * class432.sizeY;
                        i_7_ = class432.sizeX * -1125834887;
                    }
                    if (i_33_ == 0);
                    class336 = Class336_Sub5.method4105(i_32_, i_33_, i_6_, i_7_, Class424.aClass424_6614, 0, 1300552038);
                } else if (Class82_Sub9.method900(-1976050083 * class424.anInt6613, (byte)28)) {
                    class336 = Class336_Sub5.method4105(i_32_, i_33_, 0, 0, class424, i_33_, 740164949);
                } else {
                    class336 = Class194.method1867(i_32_, i_33_, 0, 0, class424, i_33_, (byte)-22);
                }
                Class277.sendWalkPacket(class336);
                System.out.println("Sending Walk Packet now.");
            }
            Class298_Sub36 class298_sub36 = Class18.method359(packet, client.aClass25_8711.aClass449_330, (byte)94);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte128(1, 1999137832);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeIntLE(/*(int)(l >>> 32L) & Integer.MAX_VALUE*/objectId, (byte)1);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE(i_32_ + class341.gameSceneBaseX * -1760580017, 1077977138);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(class341.gameSceneBaseY * 283514611 + i_33_);
            client.aClass25_8711.method390(class298_sub36, (byte)-13);
            Class93.method1013(i_32_, i_33_, objectId);
            System.out.println("Finishing Up the Obejct Packet");
        }
    }

    public ObjectConfig getObjectDefinition(int objectId) {
        return client.aClass283_8716.method2641(-527347356).getObjectDefinitions(objectId);
    }

    public void sendObjectOptionByCoord(int option, int objectId, int coordX, int coordY) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        OutcommingPacket packet = null;
        int i = Class165.aClass319_6366.method3894((byte)-80);
        int i_30_ = Class165.aClass319_6366.method3883((byte)-10);

        // Map class341 = client.aClass283_8716.method2628(681479919);
        System.out.println("ObjectID: "+objectId/*((int)(l >>> 32L) & Integer.MAX_VALUE)*/);
        System.out.println("CoordX: "+coordX);
        System.out.println("CoordY: "+coordY);
        if (option == 1) {
            packet = OutcommingPacket.aClass198_2019;
        } else if (option == 2) { // Option 2
            packet = OutcommingPacket.aClass198_2052;
        } else if (option == 3) {
            packet = OutcommingPacket.aClass198_2033;
        } else if (option == 4) {
            packet = OutcommingPacket.aClass198_2016;
        } else if (option == 5) {
            packet = OutcommingPacket.aClass198_2012;
        } else if (option == 6) {
            packet = OutcommingPacket.EXAMINE_OBJECT_PACKET;
        }
        if (packet != null) {
            client.anInt8784 = 143636043 * i;
            client.anInt8785 = i_30_ * 381532777;
            client.anInt8748 = 554324810;
            client.anInt8786 = 0;
            if (OutcommingPacket.EXAMINE_OBJECT_PACKET != packet) {
                Class336 class336;
                int i_3_ = 0;
                int i_4_ = 0;
                Class424 class424 = (Class424)Class422_Sub20.method5701((Interface21[])Class336_Sub6.method4108(114624527), i_3_, (byte)2);
                if (Class424.aClass424_6611 == class424 || Class424.aClass424_6604 == class424 || Class424.aClass424_6610 == class424) {
                    int i_6_, i_7_;
                    ObjectConfig class432 = client.aClass283_8716.method2641(-1208362615).getObjectDefinitions(objectId);
                    i_6_ = -1125834887 * class432.sizeX;
                    i_7_ = -565161399 * class432.sizeY;
                    ;
                    class336 = Class336_Sub5.method4105(coordX, coordY, i_6_, i_7_, Class424.aClass424_6614, 0, 1300552038);
                } else if (Class82_Sub9.method900(-1976050083 * class424.anInt6613, (byte)28)) {
                    class336 = Class336_Sub5.method4105(coordX, coordY, 0, 0, class424, i_4_, 740164949);
                } else {
                    class336 = Class194.method1867(coordX, coordY, 0, 0, class424, i_4_, (byte)-22);
                }
                Class277.sendWalkPacket(class336);
                System.out.println("Sending Walk Packet now.");
            }
            Class298_Sub36 class298_sub36 = Class18.method359(packet, client.aClass25_8711.aClass449_330, (byte)94);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte128(1, 1999137832);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeIntLE(/*(int)(l >>> 32L) & Integer.MAX_VALUE*/objectId, (byte)1);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE(coordX, 1077977138);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(coordY);
            client.aClass25_8711.method390(class298_sub36, (byte)-13);
            method1013(coordX, coordY, objectId);
            System.out.println("Finishing Up the Object Packet");
        }
    }

    public void sendInterfaceOnObject(int interfaceId, int itemId, int slot, int objectId, int coordX, int coordY) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
     //   Map class341 = client.aClass283_8716.method2628(681479919);
        int i = Class165.aClass319_6366.method3894((byte)-80);
        int i_30_ = Class165.aClass319_6366.method3883((byte)-10);
        interfaceId = (interfaceId << 16); // 679 = InventoryInterface in Fixed
        Class336 class336;
        int l = 0;
        int i_3_ = (int)l >> 14 & 0x1F;
        int i_4_ = (int)l >> 20 & 0x3;
        int i_5_ = (int)(l >>> 32L) & Integer.MAX_VALUE;
        Class424 class424 = (Class424)Class422_Sub20.method5701((Interface21[])Class336_Sub6.method4108(114624527), i_3_, (byte)2);
        if (Class424.aClass424_6611 == class424 || Class424.aClass424_6604 == class424 || Class424.aClass424_6610 == class424) {
            int i_6_, i_7_;
            ObjectConfig class432 = client.aClass283_8716.method2641(-1208362615).getObjectDefinitions(i_5_);
            i_6_ = -1125834887 * class432.sizeX;
            i_7_ = -565161399 * class432.sizeY;
            ;
            class336 = Class336_Sub5.method4105(coordX, coordY, i_6_, i_7_, Class424.aClass424_6614, 0, 1300552038);
        } else if (Class82_Sub9.method900(-1976050083 * class424.anInt6613, (byte)28)) {
            class336 = Class336_Sub5.method4105(coordX, coordY, 0, 0, class424, i_4_, 740164949);
        } else {
            class336 = Class194.method1867(coordX, coordY, 0, 0, class424, i_4_, (byte)-22);
        }
        Class277.sendWalkPacket(class336);
        client.anInt8784 = 143636043 * i;
        client.anInt8785 = i_30_ * 381532777;
        client.anInt8748 = 554324810;
        client.anInt8786 = 0;
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.INTERFACE_ON_OBJECT, client.aClass25_8711.aClass449_330, (byte)52);
        class298_sub36.aClass298_Sub53_Sub2_7396.write128Byte(Class151.method1644(-427290804) ? 1 : 0, (byte)1);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(itemId);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(coordY);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeIntV2(objectId);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeInt(interfaceId, 671195475);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE(slot, 462918069);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(coordX);
        System.out.println("Send Interface Hash: "+interfaceId+", ItemId: "+itemId+", Slot: "+slot);
        client.aClass25_8711.method390(class298_sub36, (byte)-39);
        method1013(coordX, coordY, l);
    }

    private void method1013(int i, int i_2_, int objectId) {
        int i_3_ = 0;
        int i_4_ = 0;
        Class424 class424 = (Class424)Class422_Sub20.method5701(Class336_Sub6.method4108(114624527), i_3_, (byte)2);
        Class336 class336;
        if (Class424.aClass424_6611 != class424 && Class424.aClass424_6604 != class424 && Class424.aClass424_6610 != class424) {
            if (Class82_Sub9.method900(-1976050083 * class424.anInt6613, (byte)28)) {
                class336 = Class336_Sub5.method4105(i, i_2_, 0, 0, class424, i_4_, 740164949);
            } else {
                class336 = Class194.method1867(i, i_2_, 0, 0, class424, i_4_, (byte)-22);
            }
        } else {
            ObjectConfig class432 = client.aClass283_8716.method2641(-1208362615).getObjectDefinitions(objectId);
            int i_6_;
            int i_7_;
            i_6_ = -1125834887 * class432.sizeX;
            i_7_ = -565161399 * class432.sizeY;
            class336 = Class336_Sub5.method4105(i, i_2_, i_6_, i_7_, Class424.aClass424_6614, 0, 1300552038);
        }
        Class82_Sub21.method938(i, i_2_, true, class336, -1680742639);
    }

    public boolean containsObjectAtCoord(int objectId, int coordX, int coordY) {
        for(Object object : Class298_Sub1.aClass453_7152) {
            Map class341 = client.aClass283_8716.method2628(681479919);
            int localObjectId = (((Class298_Sub1) object).anInt7156 * 856355825);
            int objectLocalX = (((Class298_Sub1) object).anInt7150 * 634196087);
            int objectLocalY = (((Class298_Sub1) object).anInt7155 * -2146829167);
            int objectCoordX = (objectLocalX) + (class341.gameSceneBaseX * -1760580017);
            int objectCoordY = (objectLocalY) + (class341.gameSceneBaseY * 283514611);
            if(objectId == localObjectId && coordX == objectCoordX && coordY == objectCoordY)
                return true;
        }
        return false;
    }

    public int[] getLocalObjects() {

     /*   SceneObjectPosition class217 = SceneObjectPosition.method2005((class365_sub1_sub1_sub2.method4337()).aClass217_2599);
        int i_7_ = (int)class217.x;
        int i_8_ = (int)class217.y;
        int i_9_ = class365_sub1_sub1_sub2.scenePositionXQueue[class365_sub1_sub1_sub2.anInt10120 * 2050671733 - 1] * 512 + class365_sub1_sub1_sub2.getSize() * 256;
        int i_10_ = class365_sub1_sub1_sub2.scenePositionYQueue[class365_sub1_sub1_sub2.anInt10120 * 2050671733 - 1] * 512 + class365_sub1_sub1_sub2.getSize() * 256;*/

        return null;
    }

    public static final int ATTACK_NPC_OPTION = 2;
    public static final int EXAMINE_NPC_OPTION = 7;
    /** NPC **/
    public void sendNpcOption(int option, int npcIndex) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int unknown = 0;
        int unknown2 = 0;
        OutcommingPacket class198_37_ = null;
        if (option == 1)
            class198_37_ = OutcommingPacket.aClass198_2024;
        else if (option == 2)
            class198_37_ = OutcommingPacket.ATTACK_NPC_PACKET;
        else if (option == 4)
            class198_37_ = OutcommingPacket.aClass198_2094;
        else if (option == 5)
            class198_37_ = OutcommingPacket.aClass198_2031;
        else if (option == 6)
            class198_37_ = OutcommingPacket.aClass198_2077;
        else if (option == 7)
            class198_37_ = OutcommingPacket.NPC_EXAMINE_PACKET;
        if (null != class198_37_) {
            ObjectValue class298_sub29 = (ObjectValue)client.aClass437_8696.method5812(npcIndex);
            if (null != class298_sub29) {
                NPC npc = (NPC)class298_sub29.value;
                if (class198_37_ != OutcommingPacket.NPC_EXAMINE_PACKET)
                    Class277.sendWalkPacket(Class325.method3963(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], npc.getSize(), npc.getSize(), 0, (byte)-51));
                client.anInt8784 = 143636043 * unknown;
                client.anInt8785 = unknown2 * 381532777;
                client.anInt8748 = 554324810;
                client.anInt8786 = 0;
                Class298_Sub36 class298_sub36 = Class18.method359(class198_37_, client.aClass25_8711.aClass449_330, (byte)15);
                class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(npcIndex);
                class298_sub36.aClass298_Sub53_Sub2_7396.write128Byte(Class151.method1644(1824393579) ? 1 : 0, (byte)1);
                client.aClass25_8711.method390(class298_sub36, (byte)-120);
                Class82_Sub21.method938(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], true, Class325.method3963(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], npc.getSize(), npc.getSize(), 0, (byte)-51), -1636676956);
            }
        }
    }

    public void sellAll() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.aClass198_1993, class25.aClass449_330, (byte)65);
       // Class125.method1396(class298_sub36, 82968576, 25452, -1232467723 * 3, -206334631);
        int hash = 82968576;
        int itemId = 25452;
        int slot = 3;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeIntV2(hash);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(itemId);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(slot);
        class25.method390(class298_sub36, (byte)-32);
    }

    public Player getPlayer() {
        return getReflection().getBotManager().isLoggedOut() || !isLoggedIn() ? null : Class287.myPlayer;
    }

    public int getHealthFromLocalPlayer() {
        return getReflection().getBotManager().isLoggedOut() || !isLoggedIn() ? -1 : Class128.aClass148_6331.method250(7198, (byte) 0);
    }

    public void logoutFromHiddenStaff() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        for(int i = 0; i < client.players.length; i++) {
            Player player = client.players[i];
            if(player != null && player.hidden) {
                sendLogout();
                getReflection().getBotManager().setPlayerWhoLoggedMeOut(player.aString10200);
                getReflection().getBotManager().setLogoutTime(new SecureRandom().nextInt(700) + 700);
            }
        }
    }

    public void revealHiddenStaff() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        for(int i = 0; i < client.players.length; i++) {
            Player player = client.players[i];
            if(player != null && player.hidden) {
                System.out.println("Revealed: "+player.aString10200);
                player.combatLevel = 69;
                player.hidden = false;
            }
        }
    }

    public boolean isLoggedIn() {
        return RuntimeException_Sub2.aString6305 != null && !RuntimeException_Sub2.aString6305.equals("");
    }

    public String[] trustedAccounts = new String[]{};

    public void sendLogoutDetection() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
       /* String[] trustedAccounts = new String[]{
                getLoggedInPlayerName().toLowerCase(),
                *//*"choppy",*//* "crashed star", *//*"gems",*//* "luckies", "jet kai", "expert kai", //Kai's original accounts
        };*/
        for(int i = 0; i < client.players.length; i++) {
            Player player = client.players[i];
            if(player != null) {
                String playerName = player.aString10200;
                if(playerName == null)
                    return;
                playerName = playerName.toLowerCase().replaceAll(" ", "_");
                System.out.println("Player Around: "+playerName);
                if (!playerName.equalsIgnoreCase(getLoggedInPlayerName().replaceAll(" ", "_")) && !Arrays.asList(trustedAccounts).contains(playerName)) {
                    getReflection().getBotManager().setLastUsername(Class360.username.replaceAll(" ", "_").toLowerCase());
                    getReflection().getBotManager().setLastPassword(Class360.password);
                    getReflection().getBotManager().setPlayerWhoLoggedMeOut(playerName);
                    sendLogout();
                    getReflection().getBotManager().setLogoutTime(new SecureRandom().nextInt(1000) + 1000);
                }
            }
        }
    }

    public void sendClientDialoguePopup(String title, String message) {
        JOptionPane.showMessageDialog(Loader.frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public String[] getGameChatMessages() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return null;
        String[] chatMessages = new String[1000];
        for (int i_27_ = 0; i_27_ < Class107.chatMessages.length; i_27_++) {
            if(Class107.chatMessages[i_27_] != null) {
                chatMessages[i_27_] = Class107.chatMessages[i_27_].aString1094;
                /*System.out.println("1:" + Class107.chatMessages[i_27_].aString1088); //Player Name with Crown, Chat Message Image and Name, stops </col>
                System.out.println("2:" + Class107.chatMessages[i_27_].aString1089); //null
                System.out.println("3:" + Class107.chatMessages[i_27_].aString1090); //PlayerName with Crown
                System.out.println("4:" + Class107.chatMessages[i_27_].aString1091); //PlayerName
                System.out.println("5:" + Class107.chatMessages[i_27_].aString1094); //Message
                //JUNK BELOW
                System.out.println("6:" + Class107.chatMessages[i_27_].anInt1085);
                System.out.println("7:" + Class107.chatMessages[i_27_].anInt1086);
                System.out.println("8:" + Class107.chatMessages[i_27_].anInt1087);
                System.out.println("9:" + Class107.chatMessages[i_27_].anInt1092);
                System.out.println("10:" + Class107.chatMessages[i_27_].anInt1093);*/
            }
        }
        return chatMessages;
    }

    public String getLastChatMessage() {
        return Class107.chatMessages[0] != null ? Class107.chatMessages[0].aString1094 : "";
    }

    public Player[] getLocalPlayersInRegion() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return null;
        return client.players;
    }

    public String[] getLocalPlayerNamesInRegion() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return null;
        Player[] players = client.players;
        String[] playerNames = new String[255];
        IntStream.range(0, players.length).forEachOrdered(i -> playerNames[i] = players[i].aString10200);
        return playerNames;
    }

    public String getLoggedInPlayerName() {
        String playerName = RuntimeException_Sub2.aString6305;
        return playerName == null ? "" : RuntimeException_Sub2.aString6305;
    }

    public void sendLogout() {
        if(isLoggedIn())
            sendButtonClickOnInterface(1,182, 0, 0, 6); //Sends Logout Button (for immediate log)
        ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
        threadPool.schedule(() -> {
            if (0 == client.anInt8752 * -1233866115) { //Sends Client Drop request if the Logout Button fails
                SubIncommingPacket.method1923(554378996);
            } else if (17 == -1233866115 * client.anInt8752) {
                client.aClass25_8711.aBoolean347 = true;
            }
        }, 600, TimeUnit.MILLISECONDS);
    }

    public void sendAnimation(int emoteId) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int[] is = new int[(Class522.method6325((byte)-10)).length];
        for (int i_46_ = 0; i_46_ < 4; i_46_++)
            is[i_46_] = emoteId;
        int i_47_ = 0;
        Class431.method5768(getPlayer(), is, i_47_, false, (byte)-1);
    }

    public int getAnimation() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return -1;
        AnimationConfig class391 = getPlayer().aClass438_10078.method5820(1190945096);
        if(class391 != null)
            return (class391.id * -1945308871);
        return -1;
    }

    public void bankAll() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.ACTION_BUTTON1_PACKET, class25.aClass449_330, (byte)65);
        // Class125.method1396(class298_sub36, 82968576, 25452, -1232467723 * 3, -206334631);
        int hash = 49938465;
        int itemId = -1;
        int slot = -1;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeIntV2(hash);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(itemId);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(slot);
        class25.method390(class298_sub36, (byte)-32);
    }

    public void sendGameMessage(String message) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        GrabFolder.method2282(0, 0, "", "", message, message, -1468983571);
    }

    public void sendInterfaceOnPlayer(int playerIndex, int interfaceId, int itemId, int slot, int componentId) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int hash = interfaceId << 16 | componentId;
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.INTERFACE_ON_PLAYER, client.aClass25_8711.aClass449_330, (byte)82);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort(itemId, 16711935);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort(playerIndex, 16711935);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeIntV2(hash);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(slot);
        class298_sub36.aClass298_Sub53_Sub2_7396.write128Byte(1, (byte)1);
        client.aClass25_8711.method390(class298_sub36, (byte)-113);
        System.out.println("Made it here");
    }

    public void sendButtonClickOnInterface(int option, int interfaceId, int slot, int slot2, int componentId) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        if(option < 1 || option > 9) {
            System.out.println("INVALID OPTION");
            return;
        }
        OutcommingPacket packet = null;
        if(option == 1)
            packet = OutcommingPacket.ACTION_BUTTON1_PACKET;
        else if(option == 2)
            packet = OutcommingPacket.ACTION_BUTTON2_PACKET;
        else if(option == 3)
            packet = OutcommingPacket.aClass198_2054;
        else if(option == 4)
            packet = OutcommingPacket.aClass198_2048;
        else if(option == 5)
            packet = OutcommingPacket.aClass198_1993;
        else if(option == 6)
            packet = OutcommingPacket.aClass198_2020;
        else if(option == 7)
            packet = OutcommingPacket.aClass198_1999;
        else if(option == 8)
            packet = OutcommingPacket.aClass198_2089;
        else
            packet = OutcommingPacket.aClass198_2025;
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(packet, class25.aClass449_330, (byte)65);
        // Class125.method1396(class298_sub36, 82968576, 25452, -1232467723 * 3, -206334631);
        int hash = interfaceId << 16 | componentId;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeIntV2(hash);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(slot);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(slot2);
        System.out.println("Send Interface Hash: "+hash+", ButtonId1: "+slot+", ButtonId2: "+slot2);
        class25.method390(class298_sub36, (byte)-32);
    }

    public void sendDialogueOption(int interfaceId, int option, int componentId) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int hash = interfaceId << 16 | componentId;
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.DIALOGUE_CONTINUE_PACKET, client.aClass25_8711.aClass449_330, (byte)84);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeInt(hash, -1769259974);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(option);
        client.aClass25_8711.method390(class298_sub36, (byte)-5);
        System.out.println("Send Interface Hash: "+hash+", ButtonId1: "+option);
    }

    public void sendPlayerOption(int option, int playerIndex) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        OutcommingPacket class198 = null;
        if (option == 1) {
            class198 = OutcommingPacket.ATTACK_PLAYER_PACKET;
        } else if (2 == option) {
            class198 = OutcommingPacket.aClass198_2010;
        } else if (option == 3) {
            class198 = OutcommingPacket.aClass198_2070;
        } else if (4 == option) {
            class198 = OutcommingPacket.aClass198_2042;
        } else if (5 == option) {
            class198 = OutcommingPacket.aClass198_2056;
        } else if (6 == option) {
            class198 = OutcommingPacket.aClass198_2049;
        }
        if (class198 != null) {
            Class298_Sub36 class298_sub36 = Class18.method359(class198, client.aClass25_8711.aClass449_330, (byte)102);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(playerIndex);
            client.aClass25_8711.method390(class298_sub36, (byte)-127);
        }
    }

    public void crashRegion() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        System.out.println("Made it here 1");
        byte[] array = new byte[150]; // length is bounded by 7
        new Random().nextBytes(array);
        String message = new String(array, StandardCharsets.UTF_8);
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.CHAT_PACKET, class25.aClass449_330, (byte)107);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
        int i_4_ = 385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeInt(-1, 0);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString("<euro>OVERFLOW", 0);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeLong(Long.MAX_VALUE);
        Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, "Test+€<euro>"+message, 1526854691);
      //  class298_sub36.aClass298_Sub53_Sub2_7396.method3649(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_4_, (byte)-57);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString("<euro>OVERFLOW", 0);
        class25.method390(class298_sub36, (byte)-93);
    }

    public void sendRequest() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int i = new Random().nextInt(7);
        OutcommingPacket class198 = null;
        if (i == 1) {
            class198 = OutcommingPacket.ATTACK_PLAYER_PACKET;
        } else if (2 == i) {
            class198 = OutcommingPacket.aClass198_2010;
        } else if (3 == i) {
            class198 = OutcommingPacket.aClass198_2070;
        } else if (4== i) {
            class198 = OutcommingPacket.aClass198_2042;
        } else if (5 == i) {
            class198 = OutcommingPacket.aClass198_2056;
        } else if (6 == i) {
            class198 = OutcommingPacket.aClass198_2049;
        }
        if (class198 != null) {
            Class298_Sub36 class298_sub36 = Class18.method359(class198, client.aClass25_8711.aClass449_330, (byte)102);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShortLE128(new Random().nextInt(2047));
            client.aClass25_8711.method390(class298_sub36, (byte)-127);
        }
        Class264.method2492(4, Tradution.aClass470_5853.method6049(Class321.aClass429_3357, -875414210) + "Jacob420", (byte)-70);
    }

    public void crashPM() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        byte[] array = new byte[1048]; // length is bounded by 7
        new Random().nextBytes(array);
        String message = new String(array, StandardCharsets.UTF_8);
       // String message = "drunken sailor <>< �";
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.SEND_PERSONAL_MESSAGE, class25.aClass449_330, (byte)122);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString("�<euro>�", 16711935);
        int i_46_ = class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString("Jacob420", 2138877432);
        Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, "�€�€�"+message, 208361391);
        class298_sub36.aClass298_Sub53_Sub2_7396.method3593(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_46_, 1585504133);
        class25.method390(class298_sub36, (byte)-93);
    }

    public void sendPrivateMessage(String playerName, String message) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
            Class25 class25 = Class429.method5760((short) 512);
            Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.SEND_PERSONAL_MESSAGE, class25.aClass449_330, (byte) 122);
            class298_sub36.aClass298_Sub53_Sub2_7396.writeShort(0, 16711935);
            int i_46_ = class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775;
            class298_sub36.aClass298_Sub53_Sub2_7396.writeString(playerName, 2138877432);
            Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, message, 208361391);
            class298_sub36.aClass298_Sub53_Sub2_7396.method3593(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_46_, 1585504133);
            class25.method390(class298_sub36, (byte) -93);
    }

    public void sendEnterStringPacket(boolean isLongText, String value) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        OutcommingPacket packet = isLongText ? OutcommingPacket.aClass198_2041 : OutcommingPacket.aClass198_2027;
        Class298_Sub36 class298_sub36 = Class18.method359(packet, client.aClass25_8711.aClass449_330, (byte)34);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(value.length() + 1);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeString(value, 2114518021);
        client.aClass25_8711.method390(class298_sub36, (byte)-1);
    }

    public void toggleClanChat() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.CHAT_TYPE_PACKET, class25.aClass449_330, (byte)56);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(1);
        class25.method390(class298_sub36, (byte)-69);
    }

    public void sendPublicChatMessage(String message) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        Class25 class25 = Class429.method5760((short)512);
        Class298_Sub36 class298_sub36 = Class18.method359(OutcommingPacket.CHAT_PACKET, class25.aClass449_330, (byte)107);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
        int i_4_ = 385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index;
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(0);
        class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(new Random().nextInt(5));
        Class23.method379(class298_sub36.aClass298_Sub53_Sub2_7396, message, 1526854691);
        class298_sub36.aClass298_Sub53_Sub2_7396.method3649(class298_sub36.aClass298_Sub53_Sub2_7396.index * 385051775 - i_4_, (byte)-57);
        class25.method390(class298_sub36, (byte)-12);
    }

    public boolean inventoryContainsItem(int itemId) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return false;
        int key = 93;
        boolean bool = false;
        long l = key | 0;
        Class298_Sub9 class298_sub9 = (Class298_Sub9)Class298_Sub9.aClass437_7224.method5812(l);
        for(int i = 0; i < 28; i++) {
            int finalItemId = class298_sub9.anIntArray7226[i];
            if(itemId == finalItemId)
                return true;
        }
        return false;
    }

    public int getItemInInventoryBySlot(int slot) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return -1;
        int key = 93;
        boolean bool = false;
        long l = key | 0;
        Class298_Sub9 class298_sub9 = (Class298_Sub9)Class298_Sub9.aClass437_7224.method5812(l);
        int[] itemArray = class298_sub9.anIntArray7226;
        if(itemArray.length > 0)
            return class298_sub9.anIntArray7226[slot];
        return -1;
    }

    public boolean isInventoryFull() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return false;
        int key = 93;
        boolean bool = false;
        long l = key | 0;
        Class298_Sub9 class298_sub9 = (Class298_Sub9)Class298_Sub9.aClass437_7224.method5812(l);
        for(int i = 0; i < 28; i++) {
            int itemId = class298_sub9.anIntArray7226[i];
            if(itemId == -1)
                return false;
        }
        return true;
    }

    public int[][] getInventoryItems() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return null;
        int key = 93;
        boolean bool = false;
        long l = key | 0;
        Class298_Sub9 class298_sub9 = (Class298_Sub9)Class298_Sub9.aClass437_7224.method5812(l);
        System.out.println("ArrayDump1: "+ Arrays.toString(class298_sub9.anIntArray7226));
        System.out.println("ArrayDump2: "+ Arrays.toString(class298_sub9.anIntArray7227));
        return null;
    }

    public void sendNpcOptionByName(int option, String npcName, int npcIndex) {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        int unknown = 0;
        int unknown2 = 0;
        OutcommingPacket class198_37_ = null;
        if (option == 1)
            class198_37_ = OutcommingPacket.aClass198_2024;
        else if (option == 2)
            class198_37_ = OutcommingPacket.ATTACK_NPC_PACKET;
        else if (option == 4)
            class198_37_ = OutcommingPacket.aClass198_2094;
        else if (option == 5)
            class198_37_ = OutcommingPacket.aClass198_2031;
        else if (option == 6)
            class198_37_ = OutcommingPacket.aClass198_2077;
        else if (option == 7)
            class198_37_ = OutcommingPacket.NPC_EXAMINE_PACKET;
        if (null != class198_37_) {
            ObjectValue class298_sub29 = getReflection().getNpcByName(npcName);//(ObjectValue)client.aClass437_8696.method5812(npcIndex);
            if (null != class298_sub29) {
                NPC npc = (NPC)class298_sub29.value;
                if (class198_37_ != OutcommingPacket.NPC_EXAMINE_PACKET)
                    Class277.sendWalkPacket(Class325.method3963(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], npc.getSize(), npc.getSize(), 0, (byte)-51));
                client.anInt8784 = 143636043 * unknown;
                client.anInt8785 = unknown2 * 381532777;
                client.anInt8748 = 554324810;
                client.anInt8786 = 0;
                Class298_Sub36 class298_sub36 = Class18.method359(class198_37_, client.aClass25_8711.aClass449_330, (byte)15);
                class298_sub36.aClass298_Sub53_Sub2_7396.writeShort128(npcIndex); //NpcIndex
                class298_sub36.aClass298_Sub53_Sub2_7396.write128Byte(1, (byte)1); //ForceRun, 1 = Yes, 0 = No
                client.aClass25_8711.method390(class298_sub36, (byte)-120);
                Class82_Sub21.method938(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], true, Class325.method3963(npc.scenePositionXQueue[0], npc.scenePositionYQueue[0], npc.getSize(), npc.getSize(), 0, (byte)-51), -1636676956);
            }
        }
    }

    public void sendWalkPacket(int coordX, int coordY) { //TODO
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
       /* int stepsCount = Class298_Sub37.calculateRoute(Class287.myPlayer.scenePositionXQueue[0], Class287.myPlayer.scenePositionYQueue[0], Class287.myPlayer.getSize(), class336, client.aClass283_8716.getSceneClipDataPlane(Class287.myPlayer.plane), true, client.calculatedScenePositionXs, client.calculatedScenePositionYs);
        if (stepsCount < 0) {
            stepsCount = 0;
        } else if (stepsCount > 25) {
            stepsCount = 25;
        }
        Class298_Sub36 packet = null;
        if (0 == type)
            packet = Class18.method359(OutcommingPacket.WALKING_PACKET, client.aClass25_8711.aClass449_330, (byte)51);
        if (type == 1)
            packet = Class18.method359(OutcommingPacket.MINI_WALKING_PACKET, client.aClass25_8711.aClass449_330, (byte)28);
        packet.aClass298_Sub53_Sub2_7396.writeByte(5 + stepsCount * 2);
        Map class341 = client.aClass283_8716.method2628(681479919);
        packet.aClass298_Sub53_Sub2_7396.writeShort128(class341.gameSceneBaseX * -1760580017);
        packet.aClass298_Sub53_Sub2_7396.write128Byte(Class151.method1644(-545107710) ? 1 : 0, (byte)1);
        packet.aClass298_Sub53_Sub2_7396.writeShort128(class341.gameSceneBaseY * 283514611);
        for (int c = stepsCount - 1; c >= 0; c--) {
            packet.aClass298_Sub53_Sub2_7396.writeByte(Class285.routeFinderXArray[c]);
            packet.aClass298_Sub53_Sub2_7396.writeByte(Class285.routeFinderYArray[c]);
        }
        Class3.aBoolean63 = false;
        if (stepsCount > 0) {
            Class3.flagY = Class285.routeFinderYArray[stepsCount - 1] * -1835291189;
            Class3.flagX = Class285.routeFinderXArray[stepsCount - 1] * -1129029761;
        }
        client.aClass25_8711.method390(packet, (byte)-115);*/
        Class277.sendWalkPacket(method3963(coordX, coordY,0, 0, 0, (byte)-81));
        /*Class336 class336;
        int i_3_ = 0;
        int i_4_ = 0;
        Class424 class424 = (Class424) Class422_Sub20.method5701(Class336_Sub6.method4108(114624527), i_3_, (byte) 2);
        if (Class424.aClass424_6611 == class424 || Class424.aClass424_6604 == class424 || Class424.aClass424_6610 == class424) {
            int i_6_, i_7_;

            if (Class82_Sub9.method900(-1976050083 * class424.anInt6613, (byte) 28)) {
                class336 = Class336_Sub5.method4105(coordX, coordY, 0, 0, class424, i_4_, 740164949);
            } else {
                class336 = Class194.method1867(coordX, coordY, 0, 0, class424, i_4_, (byte) -22);
            }
            Class277.sendWalkPacket(class336);
            System.out.println("Sending Walk Packet now.");

        }*/
    }

    public static Class336 method3963(int i, int i_3_, int i_4_, int i_5_, int i_6_, byte i_7_) {
        try {
            Class315.aClass336_Sub3_3310.toX = i * -760677635;
            Class315.aClass336_Sub3_3310.toY = i_3_ * 167105303;
            Class315.aClass336_Sub3_3310.sizeX = i_4_ * -1544157451;
            Class315.aClass336_Sub3_3310.sizeY = i_5_ * -1468199503;
            Class315.aClass336_Sub3_3310.anInt7715 = 89792661 * i_6_;
            return Class315.aClass336_Sub3_3310;
        } catch (RuntimeException var7) {
            throw Class346.method4175(var7, "no.b(" + ')');
        }
    }

    public static Class336 method1867(int i, int i_2_, int i_3_, int i_4_, Class424 class424, int i_5_, byte i_6_) {
        try {
            Class315.aClass336_Sub2_3309.toX = i * -760677635;
            Class315.aClass336_Sub2_3309.toY = 167105303 * i_2_;
            Class315.aClass336_Sub2_3309.sizeX = i_3_ * -1544157451;
            Class315.aClass336_Sub2_3309.sizeY = -1468199503 * i_4_;
            Class315.aClass336_Sub2_3309.aClass424_7713 = class424;
            Class315.aClass336_Sub2_3309.anInt7714 = -2142070477 * i_5_;
            return Class315.aClass336_Sub2_3309;
        } catch (RuntimeException var8) {
            throw Class346.method4175(var8, "if.k(" + ')');
        }
    }

    public void sendNpcAttack(int npcId) {
        sendNpcOption(ATTACK_NPC_OPTION, npcId);
    }

    public void test() {
        if(getReflection().getBotManager().isLoggedOut() || !isLoggedIn())
            return;
        System.out.println("Starting");
        for (int i_0_ = 0; i_0_ < client.anInt8808 * -976358333; i_0_++) {
            int npcIndex = client.anIntArray8706[i_0_];
            NPC npc = (NPC) ((ObjectValue) client.aClass437_8696.method5812(npcIndex)).value;
            System.out.println("NPC Index: "+npcIndex);
            if(npc != null)
                System.out.println("NPC Name: "+npc.aString10186);
        }

        for(int i = 0; i < client.anIntArray8706.length; i++) {
            int npcIndex = client.anIntArray8706[i];
            if(npcIndex != 0)
                System.out.println(npcIndex);
        }
    }

    private int decodeMap(int fromMapX, int fromMapY, int mapX, int mapY) {
        int baseX = 0;
        int baseY = 0;
        //Added above ^
        int archiveID = Class65.aClass243_665.getArchiveIdByName(Class62.aClass248_612.aClass283_2751.method2669(true, false, fromMapX, fromMapY, (byte)-113), -317623107);
        if (archiveID == -1) {
            return 0;
        } else if (!Class65.aClass243_665.method2290(archiveID, 0, 0)) {
            return -1;
        } else {
            byte[] data = Class65.aClass243_665.getFileFromArchive(archiveID, 0, (byte)-67);
            if (data == null) {
                return 0;
            } else {
                RsByteBuffer buffer = new RsByteBuffer(data);

                for(int height = 0; height < 2; ++height) {
                    for(int x = 0; x < 64; ++x) {
                        for(int y = 0; y < 64; ++y) {
                            int xInWorldMap = mapX * 64 + x - baseX;
                            int yInWorldMap = mapY * 64 + y - baseY;
                            WorldMap.decodeFromMap(buffer, xInWorldMap, yInWorldMap, height);
                            System.out.println(Arrays.toString(WorldMap.worldObjects));
                        }
                    }
                }
                return 1;
            }
        }
    }

    public void sendLoginRequest(Socket socket, String username, String password) {
        System.out.println("Made it here");
        String MAC_ADDRESS = Class460.MAC_ADDRESS;
        String HWID = "System";
        Class360.anInt3896 = 928688093;
       /* Class360.anInt3896 = -395862839;
        Class360.anInt3871 = -988354658;
        Class360.anInt3892 = -946395782;*/
    //    getReflection().getClient().method2809(-2054858271);
      //  Class288_Sub1.method2730(-1664553677);
        System.out.println(1 != Class360.anInt3896 * -707576455 && 100 != Class360.anInt3896 * -707576455);
        try {
            if (1 != Class360.anInt3896 * -707576455 && 100 != Class360.anInt3896 * -707576455) {
                try {
                    short i_0_;
                    if (0 == 1820934059 * Class360.anInt3904) {
                        i_0_ = 250;
                    } else {
                        i_0_ = 2000;
                    }

                    if (Class360.aBoolean3886 && Class360.anInt3896 * -707576455 >= 62) {
                        i_0_ = 6000;
                    }

                    if (-122629167 * Class360.anInt3868 != 264 || 203 != Class360.anInt3896 * -707576455 && 42 != -1372893999 * Class360.anInt3871) {
                        Class360.anInt3900 += -975705897;
                    }

                    if (-1937798425 * Class360.anInt3900 > i_0_) {
                        Class360.aClass25_3905.method384((byte)57);
                        if (Class360.anInt3904 * 1820934059 >= 3) {
                            Class360.anInt3896 = -395862839;
                            Class78.method845(-5, 1141860334);
                            return;
                        }

                        if (264 == Class360.anInt3868 * -122629167) {
                            Class474.aClass471_5979.method6058(-281677177);
                        } else {
                            Class241.aClass471_2705.method6058(-213625938);
                        }

                        Class360.anInt3900 = 0;
                        Class360.anInt3904 += -72367357;
                        Class360.anInt3896 = -455386772;
                    }

                    Class298_Sub36 class298_sub36;
                    int i_16_;
                    int i_5_;
                    System.out.println("Class360int"+Class360.anInt3896);
                  //  if (12 == Class360.anInt3896 * -707576455) {
                        if (264 == -122629167 * Class360.anInt3868) {
                            Class360.aClass25_3905.method389(Class264_Sub4.method2515(Class474.aClass471_5979.method6056(295506052), 15000, -649048480), Class474.aClass471_5979.address, (byte)0);
                        } else {
                            Class360.aClass25_3905.method389(Class264_Sub4.method2515(Class241.aClass471_2705.method6056(295506052), 15000, -649048480), Class241.aClass471_2705.address, (byte)0);
                        }
                        System.out.println("Made it here2");
                        Class360.aClass25_3905.method383((short)8191);
                        class298_sub36 = Class82_Sub6.method885(-1825045529);
                        if (Class360.aBoolean3886) {
                            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(Class211.aClass211_2413.anInt2418 * -1813470547);
                            class298_sub36.aClass298_Sub53_Sub2_7396.writeShort(0, 16711935);
                            i_16_ = 385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index;
                            class298_sub36.aClass298_Sub53_Sub2_7396.writeInt(718, -1354427278);
                            if (Settings.SUB_BUILD != -1) {
                                class298_sub36.aClass298_Sub53_Sub2_7396.writeInt(Settings.SUB_BUILD, 376398822);
                            }

                            if (-122629167 * Class360.anInt3868 == 264) {
                                class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(5 == client.anInt8752 * -1233866115 ? 1 : 0);
                            }

                            RsByteBuffer class298_sub53 = Class322.method3933(-1454924768);
                            class298_sub53.writeByte(2084404473 * Class360.anInt3873);
                            class298_sub53.writeShort((int)(Math.random() * 9.9999999E7D), 16711935);
                            class298_sub53.writeByte(Class321.aClass429_3357.method242(694163818));
                            class298_sub53.writeInt(client.anInt8665 * -1154804873, -1393012818);

                            for(i_5_ = 0; i_5_ < 6; ++i_5_) {
                                class298_sub53.writeInt((int)(Math.random() * 9.9999999E7D), 499420945);
                            }

                            class298_sub53.writeLong(client.aLong8675 * -8380697455384249973L);
                            class298_sub53.writeByte(-937307905 * client.aClass411_8944.gameType);
                            class298_sub53.writeByte((int)(Math.random() * 9.9999999E7D));
                            class298_sub53.applyRsa(Class50.aBigInteger500, Class50.MODULUS, 1533826109);
                            class298_sub36.aClass298_Sub53_Sub2_7396.writeBytes(class298_sub53.buffer, 0, 385051775 * class298_sub53.index, (short)-29754);
                            class298_sub36.aClass298_Sub53_Sub2_7396.method3593(385051775 * class298_sub36.aClass298_Sub53_Sub2_7396.index - i_16_, 1585504133);
                        } else {
                            class298_sub36.aClass298_Sub53_Sub2_7396.writeByte(-1813470547 * Class211.aClass211_2416.anInt2418);
                        }

                        Class360.aClass25_3905.method390(class298_sub36, (byte)-57);
                        Class360.aClass25_3905.method386(-1781606732);
                        Class360.anInt3896 = 1009016718;
                 //   }

                    int i_17_;
                    if (Class360.anInt3896 * -707576455 == 30) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)-19)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)75);
                        i_17_ = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255;
                        if (0 != i_17_) {
                            Class360.anInt3896 = -395862839;
                            Class78.method845(i_17_, 352942199);
                            Class360.aClass25_3905.method384((byte)110);
                            ClientScriptsExecutor.method4693(1976641602);
                            return;
                        }

                        if (Class360.aBoolean3886) {
                            Class360.anInt3896 = -238095732;
                        } else {
                            Class360.anInt3896 = 1898985570;
                        }
                    }

                    if (-707576455 * Class360.anInt3896 == 44) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(2, (byte)-17)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 2, (byte)-39);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.readUnsignedShort() * 116413311;
                        Class360.anInt3896 = -1485208182;
                    }

                    if (Class360.anInt3896 * -707576455 == 58) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(385051775 * Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index, (byte)6)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 385051775 * Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index, (byte)33);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.method3610(Class360.anIntArray3889, 642509947);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                        String string = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.readJagString(681479919);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                        String string_4_ = Class212.aClass212_2430.method1951(-1670386026);
                        if (!client.aBoolean8638 || !Class65.method762(string, 1, string_4_, -2049749087)) {
                            Class273.method2559(string, true, Class422_Sub25.preferences.graphicsMode.method5677(-671601354) == 5, string_4_, client.aBoolean8867, client.aBoolean8651, -1487322449);
                        }

                        Class360.anInt3896 = 1226307758;
                    }

                    if (Class360.anInt3896 * -707576455 == 62) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)73)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)68);
                        if ((Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255) == 1) {
                            Class360.anInt3896 = 1562646664;
                        }
                    }

                    if (Class360.anInt3896 * -707576455 == 72) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(16, (byte)-100)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 16, (byte)59);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 1862612976;
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.method3610(Class360.anIntArray3889, 642509947);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                        Class360.usernameL = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.readLong((short)27770) * -2742373017286080113L;
                        Class360.aLong3911 = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.readLong((short)10381) * 3207425516430892907L;
                        Class360.anInt3896 = 1898985570;
                    }

                    RsBitsBuffer stream;
                    if (-707576455 * Class360.anInt3896 == 82) {
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                        Class360.aClass25_3905.method383((short)8191);
                        class298_sub36 = Class82_Sub6.method885(-1133801446);
                        stream = class298_sub36.aClass298_Sub53_Sub2_7396;
                        int i_6_;
                        RsByteBuffer class298_sub53;
                        Class211 class211;
                        if (264 == -122629167 * Class360.anInt3868) {
                            if (Class360.aBoolean3886) {
                                class211 = Class211.aClass211_2417;
                            } else {
                                class211 = Class211.aClass211_2409;
                            }

                            stream.writeByte(-1813470547 * class211.anInt2418);
                            stream.writeShort(0, 16711935);
                            i_5_ = 385051775 * stream.index;
                            i_6_ = 385051775 * stream.index;
                            if (!Class360.aBoolean3886) {
                                stream.writeInt(718, 711122101);
                                if (Settings.SUB_BUILD != -1) {
                                    stream.writeInt(Settings.SUB_BUILD, 98092954);
                                }

                                stream.writeByte(client.anInt8752 * -1233866115 == 5 ? 1 : 0);
                                i_6_ = 385051775 * stream.index;
                                class298_sub53 = Class_ta_Sub2.method6003(-2133378997);
                                stream.writeBytes(class298_sub53.buffer, 0, class298_sub53.index * 385051775, (short)-31677);
                                i_6_ = stream.index * 385051775;
                                stream.writeByte(Class360.usernameL * 122690138525332847L == -1L ? 1 : 0);
                                if (Class360.usernameL * 122690138525332847L == -1L) {
                                    stream.writeString(Class360.username, 2140422151);
                                } else {
                                    stream.writeLong(122690138525332847L * Class360.usernameL);
                                }
                            }

                            stream.writeByte(Class190.getDisplayMode((byte)-90));
                            stream.writeShort(-2110394505 * Class462.screenWidth, 16711935);
                            stream.writeShort(Class298_Sub40_Sub9.screenHeight * -1111710645, 16711935);
                            stream.writeByte(Class422_Sub25.preferences.antialiasing.method5675(-217929740));
                            Class10.method322(stream, (byte)1);
                            stream.writeString(client.aString8927, 2114152304);
                            stream.writeInt(-1154804873 * client.anInt8665, -1572632938);
                            class298_sub53 = Class422_Sub25.preferences.encode(-611972750);
                            stream.writeByte(385051775 * class298_sub53.index);
                            stream.writeBytes(class298_sub53.buffer, 0, class298_sub53.index * 385051775, (short)-20549);
                            client.sentPreferences = true;
                            stream.writeInt(client.anInt8713 * -2059460167, -1287558190);
                            stream.writeLong(client.aLong8645 * -5648129435911399733L);
                            stream.writeByte(null == client.aString8648 ? 0 : 1);
                            if (client.aString8648 != null) {
                                stream.writeString(client.aString8648, 2119355084);
                            }

                            stream.writeByte(Class84.aClass305_770.method3747("jagtheora", 1302159774) ? 1 : 0);
                            stream.writeByte(client.aBoolean8638 ? 1 : 0);
                            stream.writeByte(client.aBoolean8958 ? 1 : 0);
                            stream.writeByte(StanceConfig.anInt3758 * -180909151);
                            stream.writeInt(1886068421 * client.anInt8654, 576248494);
                            stream.writeString(client.aString8655, 2141094155);
                            stream.writeByte(null != Class386.aClass471_4146 && 1606920449 * Class474.aClass471_5979.worldId == Class386.aClass471_4146.worldId * 1606920449 ? 0 : 1);
                            Class486.writeCacheCRCs(stream, -956052447);
                            stream.writeString(!HWID.isEmpty() && !HWID.equalsIgnoreCase("not") && !HWID.equalsIgnoreCase("system") && !HWID.equalsIgnoreCase("default") && !HWID.equalsIgnoreCase("to") ? HWID : MAC_ADDRESS, 0);
                            stream.method3611(Class360.anIntArray3889, i_6_, 385051775 * stream.index, 1729780581);
                            stream.method3593(385051775 * stream.index - i_5_, 1585504133);
                        } else {
                            if (Class360.aBoolean3886) {
                                class211 = Class211.aClass211_2417;
                            } else {
                                class211 = Class211.aClass211_2411;
                            }

                            stream.writeByte(-1813470547 * class211.anInt2418);
                            stream.writeShort(0, 16711935);
                            i_5_ = 385051775 * stream.index;
                            i_6_ = 385051775 * stream.index;
                            if (!Class360.aBoolean3886) {
                                stream.writeInt(718, -452164382);
                                if (Settings.SUB_BUILD != -1) {
                                    stream.writeInt(Settings.SUB_BUILD, 317411115);
                                }

                                class298_sub53 = Class_ta_Sub2.method6003(-1358767373);
                                stream.writeBytes(class298_sub53.buffer, 0, class298_sub53.index * 385051775, (short)-22572);
                                i_6_ = stream.index * 385051775;
                                stream.writeByte(122690138525332847L * Class360.usernameL == -1L ? 1 : 0);
                                if (-1L == 122690138525332847L * Class360.usernameL) {
                                    stream.writeString(Class360.username, 2126472832);
                                } else {
                                    stream.writeLong(122690138525332847L * Class360.usernameL);
                                }
                            }

                            stream.writeByte(-937307905 * client.aClass411_8944.gameType);
                            stream.writeByte(Class321.aClass429_3357.method242(694163818));
                            Class10.method322(stream, (byte)1);
                            stream.writeString(client.aString8927, 2107324025);
                            class298_sub53 = Class422_Sub25.preferences.encode(-1517637513);
                            stream.writeByte(class298_sub53.index * 385051775);
                            stream.writeBytes(class298_sub53.buffer, 0, class298_sub53.index * 385051775, (short)-4570);
                            if (!Settings.DISABLE_MACHINE_INFORMATION) {
                                RsByteBuffer buf = new RsByteBuffer(Class12.aClass298_Sub44_9946.method3530(-1431420237));
                                Class12.aClass298_Sub44_9946.writeMachineInformation(buf, 1834978848);
                                stream.writeBytes(buf.buffer, 0, buf.buffer.length, (short)-14969);
                            }

                            stream.writeInt(-1154804873 * client.anInt8665, 142980326);
                            stream.writeInt(client.anInt8654 * 1886068421, -840792899);
                            stream.writeString(client.aString8655, 2126130218);
                            Class486.writeCacheCRCs(stream, -535015918);
                            stream.writeString(!HWID.isEmpty() && !HWID.equalsIgnoreCase("system") && !HWID.equalsIgnoreCase("default") && !HWID.equalsIgnoreCase("to") ? HWID : MAC_ADDRESS, 0);
                            stream.method3611(Class360.anIntArray3889, i_6_, 385051775 * stream.index, -1390268287);
                            stream.method3593(385051775 * stream.index - i_5_, 1585504133);
                        }

                        Class360.aClass25_3905.method390(class298_sub36, (byte)-65);
                        Class360.aClass25_3905.method386(-1062695636);
                        Class360.aClass25_3905.aClass449_330 = new IsaacCipher(Class360.anIntArray3889);

                        for(int i_11_ = 0; i_11_ < 4; ++i_11_) {
                            int[] var10000 = Class360.anIntArray3889;
                            var10000[i_11_] += 50;
                        }

                        Class360.aClass25_3905.aClass449_334 = new IsaacCipher(Class360.anIntArray3889);
                        new IsaacCipher(Class360.anIntArray3889);
                        Class360.aClass25_3905.aClass298_Sub53_Sub2_333.method3665(Class360.aClass25_3905.aClass449_334, (byte)80);
                        Class360.anIntArray3889 = null;
                        Class360.anInt3896 = 1443598798;
                    }

                    if (94 == -707576455 * Class360.anInt3896) {
                        if (!Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)-69)) {
                            return;
                        }

                        Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)-72);
                        i_17_ = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255;
                        if (21 == i_17_) {
                            Class360.anInt3896 = 2056752677;
                        } else if (29 != i_17_ && 45 != i_17_) {
                            if (i_17_ == 1) {
                                Class360.anInt3896 = -931578236;
                                Class78.method845(i_17_, 1486771183);
                                return;
                            }

                            if (i_17_ == 2) {
                                Class360.anInt3896 = 1601365905;
                            } else {
                                if (15 != i_17_) {
                                    if (i_17_ == 23 && 1820934059 * Class360.anInt3904 < 3) {
                                        Class360.anInt3900 = 0;
                                        Class360.anInt3904 += -72367357;
                                        Class360.anInt3896 = -455386772;
                                        Class360.aClass25_3905.method384((byte)93);
                                        return;
                                    } else if (i_17_ == 42) {
                                        Class360.anInt3896 = 1244222307;
                                        Class78.method845(i_17_, 1924139793);
                                        return;
                                    } else {
                                        if (Class360.aBoolean3870 && !Class360.aBoolean3886 && -1 != 2084404473 * Class360.anInt3873 && 35 == i_17_) {
                                            Class360.aBoolean3886 = true;
                                            Class360.anInt3900 = 0;
                                            Class360.anInt3896 = -455386772;
                                            Class360.aClass25_3905.method384((byte)93);
                                        } else {
                                            Class360.anInt3896 = -395862839;
                                            Class78.method845(i_17_, 1778518954);
                                            Class360.aClass25_3905.method384((byte)124);
                                            ClientScriptsExecutor.method4693(1976641602);
                                        }

                                        return;
                                    }
                                }

                                Class360.aClass25_3905.anInt336 = -1763582762;
                                Class360.anInt3896 = 907883401;
                            }
                        } else {
                            Class197.anInt1992 = 974522705 * i_17_;
                            Class360.anInt3896 = -616044022;
                        }
                    }

                    if (Class360.anInt3896 * -707576455 == 117) {
                        Class360.aClass25_3905.method383((short)8191);
                        class298_sub36 = Class82_Sub6.method885(-1106381844);
                        stream = class298_sub36.aClass298_Sub53_Sub2_7396;
                        stream.method3665(Class360.aClass25_3905.aClass449_330, (byte)41);
                        stream.method3668(-1813470547 * Class211.aClass211_2419.anInt2418, (byte)1);
                        Class360.aClass25_3905.method390(class298_sub36, (byte)-122);
                        Class360.aClass25_3905.method386(-1208418920);
                        Class360.anInt3896 = 1443598798;
                    } else if (125 == Class360.anInt3896 * -707576455) {
                        if (Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)48)) {
                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)-14);
                            i_17_ = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255;
                            Class360.anInt3908 = i_17_ * -1954130922;
                            Class360.anInt3896 = -395862839;
                            Class78.method845(21, 779029063);
                            Class360.aClass25_3905.method384((byte)10);
                            ClientScriptsExecutor.method4693(1976641602);
                        }
                    } else if (-707576455 * Class360.anInt3896 == 203) {
                        if (Class360.aClass25_3905.method387(537308016).isAvailable(2, (byte)92)) {
                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 2, (byte)31);
                            Class360.anInt3866 = (((Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255) << 8) + (Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[1] & 255)) * -1156978587;
                            Class360.anInt3896 = 1443598798;
                        }
                    } else if (-707576455 * Class360.anInt3896 == 186) {
                        if (29 == Class197.anInt1992 * 1892081585) {
                            if (!Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)78)) {
                                return;
                            }

                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)-63);
                            Class360.anInt3910 = 1200969765 * (Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255);
                        } else {
                            if (Class197.anInt1992 * 1892081585 != 45) {
                                throw new IllegalStateException();
                            }

                            if (!Class360.aClass25_3905.method387(537308016).isAvailable(3, (byte)-85)) {
                                return;
                            }

                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 3, (byte)-63);
                            Class360.anInt3910 = 1200969765 * (Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255);
                            Class360.anInt3909 = 1719895145 * ((Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[2] & 255) + ((Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[1] & 255) << 8));
                        }

                        Class360.anInt3896 = -395862839;
                        Class78.method845(1892081585 * Class197.anInt1992, -28417078);
                        Class360.aClass25_3905.method384((byte)7);
                        ClientScriptsExecutor.method4693(1976641602);
                    } else if (137 == Class360.anInt3896 * -707576455) {
                        if (Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)-1)) {
                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 1, (byte)-34);
                            Class360.loginConfigsSize = 1962471985 * (Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer[0] & 255);
                            Class360.anInt3896 = 1541841972;
                        }
                    } else {
                        if (-707576455 * Class360.anInt3896 == 148) {
                            stream = Class360.aClass25_3905.aClass298_Sub53_Sub2_333;
                            if (264 == Class360.anInt3868 * -122629167) {
                                if (!Class360.aClass25_3905.method387(537308016).isAvailable(-1359010095 * Class360.loginConfigsSize, (byte)68)) {
                                    return;
                                }

                                Class360.aClass25_3905.method387(537308016).readBytes(stream.buffer, 0, Class360.loginConfigsSize * -1359010095, (byte)13);
                                stream.index = 0;
                                client.playerRights = stream.readUnsignedByte() * 1835619115;
                                client.anInt8932 = stream.readUnsignedByte() * -418443653;
                                client.aBoolean8811 = stream.readUnsignedByte() == 1;
                                client.aBoolean8812 = stream.readUnsignedByte() == 1;
                                client.aBoolean8813 = stream.readUnsignedByte() == 1;
                                client.aBoolean8802 = stream.readUnsignedByte() == 1;
                                client.playerIndex = stream.readUnsignedShort() * 1448461709;
                                client.aBoolean8807 = stream.readUnsignedByte() == 1;
                                Class298_Sub41.anInt7456 = stream.read24BitInteger((byte)-39) * 777394511;
                                client.isMemberWorld = stream.readUnsignedByte() == 1;
                                Class112.aString1369 = stream.readString(-796084606);
                                client.aClass283_8716.method2641(-1352577967).method5790(client.isMemberWorld, 915103443);
                                Class62.aClass248_612.method2384((short)206).method2641(-884206015).method5790(client.isMemberWorld, 915103443);
                                Class298_Sub32_Sub14.aClass477_9400.method6092(client.isMemberWorld, 798055588);
                                Class15.aClass507_224.method6270(client.isMemberWorld, (byte)-2);
                                Preferences.username = Class360.username;
                                Class3.savePreferences(656179282);
                            } else {
                                if (!Class360.aClass25_3905.method387(537308016).isAvailable(Class360.loginConfigsSize * -1359010095, (byte)-27)) {
                                    return;
                                }

                                Class360.aClass25_3905.method387(537308016).readBytes(stream.buffer, 0, Class360.loginConfigsSize * -1359010095, (byte)29);
                                stream.index = 0;
                                client.playerRights = stream.readUnsignedByte() * 1835619115;
                                client.anInt8932 = stream.readUnsignedByte() * -418443653;
                                client.aBoolean8811 = stream.readUnsignedByte() == 1;
                                Class298_Sub41.anInt7456 = stream.read24BitInteger((byte)-98) * 777394511;
                                Class287.myPlayer.aByte10220 = (byte)stream.readUnsignedByte();
                                client.aBoolean8812 = stream.readUnsignedByte() == 1;
                                client.aBoolean8813 = stream.readUnsignedByte() == 1;
                                Class247.aLong2748 = stream.readLong((short)9875) * 9182695496232067233L;
                                IntegerValue.aLong7395 = (-536549149186981023L * Class247.aLong2748 - Class122.method1319((byte)1) - stream.method3601((byte)74)) * -7894334964002250373L;
                                i_16_ = stream.readUnsignedByte();
                                client.aBoolean8807 = 0 != (i_16_ & 1);
                                Class510.aBoolean6222 = 0 != (i_16_ & 2);
                                Class384.anInt4128 = stream.readInt((byte)99) * -1704395451;
                                Class525.aBoolean6300 = stream.readUnsignedByte() == 1;
                                Class100.anInt1079 = stream.readInt((byte)-9) * -442700441;
                                Class66.anInt666 = stream.readUnsignedShort() * -1652734029;
                                OutcommingPacket.anInt2099 = stream.readUnsignedShort() * 808373911;
                                Class298.anInt3190 = stream.readUnsignedShort() * -591256495;
                                Class298.anInt3191 = stream.readInt((byte)39) * -1316190437;
                                Class251.aClass524_2773 = new Class524(Class298.anInt3191 * 2071493395);
                                (new Thread(Class251.aClass524_2773)).start();
                                Class95.anInt923 = stream.readUnsignedByte() * 1240622393;
                                Class485.anInt6059 = stream.readUnsignedShort() * 556974909;
                                Class52_Sub2.anInt6815 = stream.readUnsignedShort() * 580840459;
                                Class406.aBoolean5274 = stream.readUnsignedByte() == 1;
                                Class287.myPlayer.aString10195 = Class287.myPlayer.aString10200 = RuntimeException_Sub2.aString6305 = stream.readJagString(681479919);
                                Class95.anInt924 = stream.readUnsignedByte() * 821936487;
                                Class216.anInt6659 = stream.readInt((byte)-28) * 2029589759;
                                client.aBoolean8640 = stream.readUnsignedByte() == 1;
                                Class386.aClass471_4146 = new IPAddress();
                                Class386.aClass471_4146.worldId = stream.readUnsignedShort() * 348739329;
                                if (65535 == 1606920449 * Class386.aClass471_4146.worldId) {
                                    Class386.aClass471_4146.worldId = -348739329;
                                }

                                Class386.aClass471_4146.address = stream.readJagString(681479919);
                                if (Class401.aClass401_6557 != Class242.aClass401_2708) {
                                    Class386.aClass471_4146.anInt5954 = Class386.aClass471_4146.worldId * -1670427267 + 815680320;
                                    Class386.aClass471_4146.anInt5955 = -52655920 + Class386.aClass471_4146.worldId * 925746937;
                                }

                                if (Class242.aClass401_2708 != Class401.aClass401_6552 && (Class242.aClass401_2708 != Class401.aClass401_6554 || 1806357379 * client.playerRights < 2) && Class474.aClass471_5979.method6057(Class474.aClass471_5976, 2123928060)) {
                                    Class380.method4678(-1667448332);
                                }
                            }

                            if ((!client.aBoolean8811 || client.aBoolean8813) && !client.aBoolean8807) {
                                try {
                                    Class466.method6021(ClientScriptMap.anApplet6044, "unzap", (short)10429);
                                } catch (Throwable var9) {
                                }
                            } else {
                                try {
                                    Class466.method6021(ClientScriptMap.anApplet6044, "zap", (short)11786);
                                } catch (Throwable var11) {
                                    if (client.aBoolean8639) {
                                        try {
                                            ClientScriptMap.anApplet6044.getAppletContext().showDocument(new URL(ClientScriptMap.anApplet6044.getCodeBase(), "blank.ws"), "tbi");
                                        } catch (Exception var10) {
                                        }
                                    }
                                }
                            }

                            if (Class401.aClass401_6557 == Class242.aClass401_2708) {
                                Class212.aClass212_2422.method1952(-1392768715);
                            }

                            if (264 != Class360.anInt3868 * -122629167) {
                                Class360.anInt3896 = -395862839;
                                Class78.method845(2, 1533583535);
                                ObjectConfig.method5789(-954161588);
                                Class439.method5851(14, 1153867870);
                                Class360.aClass25_3905.INCOMMING_PACKET = null;
                                return;
                            }

                            Class360.anInt3896 = -101133317;
                        }

                        if (-707576455 * Class360.anInt3896 == 163) {
                            if (!Class360.aClass25_3905.method387(537308016).isAvailable(3, (byte)75)) {
                                return;
                            }

                            Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 3, (byte)-16);
                            Class360.anInt3896 = -952382928;
                        }

                        if (-707576455 * Class360.anInt3896 == 176) {
                            stream = Class360.aClass25_3905.aClass298_Sub53_Sub2_333;
                            stream.index = 0;
                            if (stream.method3661((byte)-27)) {
                                if (!Class360.aClass25_3905.method387(537308016).isAvailable(1, (byte)-10)) {
                                    return;
                                }

                                Class360.aClass25_3905.method387(537308016).readBytes(stream.buffer, 3, 1, (byte)-25);
                            }

                            Class360.aClass25_3905.INCOMMING_PACKET = Class510.method6290(-1456212765)[stream.readUnsignedSmart(250607366)];
                            Class360.aClass25_3905.anInt336 = stream.readUnsignedShort() * -1265692267;
                            Class360.anInt3896 = 1878180878;
                        }

                        if (158 == -707576455 * Class360.anInt3896) {
                            if (Class360.aClass25_3905.method387(537308016).isAvailable(-866602563 * Class360.aClass25_3905.anInt336, (byte)-22)) {
                                Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, -866602563 * Class360.aClass25_3905.anInt336, (byte)29);
                                Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                                i_17_ = Class360.aClass25_3905.anInt336 * -866602563;
                                Class360.anInt3896 = -395862839;
                                Class78.method845(2, 1250429131);
                                Class411.method5579(-2092028687);
                                Class51.decodeLswp(Class360.aClass25_3905.aClass298_Sub53_Sub2_333, 837096225);
                                i_16_ = i_17_ - Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index * 385051775;
                                stream = new RsBitsBuffer(i_16_);
                                System.arraycopy(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index * 385051775, stream.buffer, 0, i_16_);
                                RsBitsBuffer var22 = Class360.aClass25_3905.aClass298_Sub53_Sub2_333;
                                var22.index += i_16_ * 116413311;
                                if (Class360.aClass25_3905.INCOMMING_PACKET == IncommingPacket.LOAD_MAP_SCENE_DYNAMIC_PACKET) {
                                    client.aClass283_8716.sendMapScene(new Class267(Class266.LOAD_MAP_SCENE_DYNAMIC, stream), -1991819579);
                                } else {
                                    client.aClass283_8716.sendMapScene(new Class267(Class266.LOAD_MAP_SCENE_NORMAL, stream), -1991819579);
                                }

                                if (i_17_ != Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index * 385051775) {
                                    throw new RuntimeException(385051775 * Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index + " " + i_17_);
                                }

                                Class360.aClass25_3905.INCOMMING_PACKET = null;
                            }
                        } else if (193 == Class360.anInt3896 * -707576455) {
                            if (-2 == -866602563 * Class360.aClass25_3905.anInt336) {
                                if (!Class360.aClass25_3905.method387(537308016).isAvailable(2, (byte)39)) {
                                    return;
                                }

                                Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, 2, (byte)9);
                                Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                                Class360.aClass25_3905.anInt336 = Class360.aClass25_3905.aClass298_Sub53_Sub2_333.readUnsignedShort() * -1265692267;
                            }

                            if (Class360.aClass25_3905.method387(537308016).isAvailable(-866602563 * Class360.aClass25_3905.anInt336, (byte)77)) {
                                Class360.aClass25_3905.method387(537308016).readBytes(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.buffer, 0, Class360.aClass25_3905.anInt336 * -866602563, (byte)-73);
                                Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index = 0;
                                i_17_ = Class360.aClass25_3905.anInt336 * -866602563;
                                Class360.anInt3896 = -395862839;
                                Class78.method845(15, 380191322);
                                Class431.method5766(144926411);
                                Class51.decodeLswp(Class360.aClass25_3905.aClass298_Sub53_Sub2_333, 822397380);
                                if (385051775 * Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index != i_17_) {
                                    throw new RuntimeException(Class360.aClass25_3905.aClass298_Sub53_Sub2_333.index * 385051775 + " " + i_17_);
                                }

                                Class360.aClass25_3905.INCOMMING_PACKET = null;
                            }
                        }
                    }
                } catch (IOException var12) {
                    System.out.println("IO EXCEPTION");
                    Class360.aClass25_3905.method384((byte)40);
                    if (1820934059 * Class360.anInt3904 < 3) {
                        if (264 == -122629167 * Class360.anInt3868) {
                            Class474.aClass471_5979.method6058(-1442409390);
                        } else {
                            Class241.aClass471_2705.method6058(-734598763);
                        }

                        Class360.anInt3900 = 0;
                        Class360.anInt3904 += -72367357;
                        Class360.anInt3896 = -455386772;
                    } else {
                        Class360.anInt3896 = -395862839;
                        Class78.method845(-4, 500556519);
                        ClientScriptsExecutor.method4693(1976641602);
                    }
                }
            }

        } catch (Exception var13) {
           // var13.printStackTrace();
        }
    }

    public void sendDirectLogin(String username, String password) {
        Class63.method741(username, password, 2101690439);
    }

    public void setTrustedAccounts(String[] trustedAccounts) {
        if(trustedAccounts != null && trustedAccounts.length > 0)
            this.trustedAccounts = trustedAccounts;
    }

    public Reflection getReflection() {
        return reflection;
    }
}

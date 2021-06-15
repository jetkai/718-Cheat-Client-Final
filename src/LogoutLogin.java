import javax.swing.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class LogoutLogin {

    private final BotManager AABotManager;

    private ScheduledExecutorService threadPool = null;

    public LogoutLogin(BotManager AABotManager) {
        this.AABotManager = AABotManager;
    }

    private ArrayList<String[]> proxies2;
    private ArrayList<String[]> proxies3;

    public void start() {
        if (isRunning())
            return;
        proxies2 = getBotManager().getReflection().getProxies().getBestProxyFromTrevorsApi();
        proxies3 = getBotManager().getReflection().getProxies().getLeonsProxies(false);
        getBotManager().getReflection().getAntiBanToggle().getLoginFloodProperties();
        threadPool.scheduleAtFixedRate(this::updateProxy, 0L, 2000, TimeUnit.MILLISECONDS);
        //threadPool.scheduleAtFixedRate(this::sendNextStep, 0L, 600, TimeUnit.MILLISECONDS);
    }

    private String getRandomString(int minimumLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < minimumLength + new Random().nextInt(12 - minimumLength); i++)
            stringBuilder.append((char)(new Random().nextInt(26) + 'a'));
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    private String getRandomIlName(int minimumLength) {
        String[] il = new String[]{"i", "l"};
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < minimumLength + new Random().nextInt(12 - minimumLength); i++)
            stringBuilder.append(il[new Random().nextInt(il.length)]);
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }


    public void updateProxy2() {
        if(proxies2 == null || proxies2.size() <= 0)
            return;
        String[] proxyArray = proxies2.get(1);
        if(proxyArray != null) {
            ArrayList<String> dropMenuItems = new ArrayList<>();
            Collections.addAll(dropMenuItems, proxyArray);
            String proxy = dropMenuItems.get(new SecureRandom().nextInt(dropMenuItems.size()));

            System.err.println("ProxyArray: "+ proxy);
            boolean isPremium = proxy.contains("Premium");
            String[] paramContains = new String[]{"[", "]", "\""};
            proxy = proxy.split(",")[0];
            for (String param : paramContains) {
                if (proxy.contains(param))
                    proxy = proxy.replace(param, "");
            }
            if (proxy.contains(":")) {
                String credentials = isPremium ? "<API USERNAME>:<API PASSWORD>" : "";
                getBotManager().getReflection().getProxies().setCurrentProxyAddress(proxy);
                getBotManager().getReflection().getProxies().setCurrentProxyCredentials(credentials);
                getBotManager().getReflection().getProxies().setProxy("SOCKS");
                System.out.println("Setting Proxy: "+proxy);
            }
        }
    }

    public void updateProxyLeon() {
        if(proxies3 == null || proxies3.size() <= 0)
            return;
        String[] proxyArray = proxies3.get(1);
        if(proxyArray != null) {
            ArrayList<String> dropMenuItems = new ArrayList<>();
            Collections.addAll(dropMenuItems, proxyArray);
            String proxy = dropMenuItems.get(new SecureRandom().nextInt(dropMenuItems.size()));

            System.err.println("ProxyArray: "+ proxy);
            boolean isPremium = proxy.contains("Premium");
            String[] paramContains = new String[]{"[", "]", "\""};
            proxy = proxy.split(",")[0];
            for (String param : paramContains) {
                if (proxy.contains(param))
                    proxy = proxy.replace(param, "");
            }
            if (proxy.contains(":")) {
                String credentials = isPremium ? "<API USERNAME>:<API PASSWORD>" : "";
                getBotManager().getReflection().getProxies().setCurrentProxyAddress(proxy);
                getBotManager().getReflection().getProxies().setCurrentProxyCredentials(credentials);
                getBotManager().getReflection().getProxies().setProxy("SOCKS");
                System.out.println("Setting Proxy: "+proxy);
            }
        }
    }

    private int step;

    private void sendNextStep() {
        switch(step) {
            case 0:
                try {
                    getBotManager().getReflection().commands("newmac");
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                getBotManager().getPacketHandler().sendDirectLogin(getRandomString(1)/*getRandomIlName(6)*/, getRandomString(3));
                step++;
                return;
           /* case 2:
            case 3:
                if(getBotManager().getPacketHandler().isLoggedIn()) {
                   // getBotManager().getPacketHandler().sendOnyxCommand("claim");
                    getBotManager().getPacketHandler().sendPublicChatMessage("Bang it out of debug mode");
                }
                step++;
                break;*/
            case 1:
              /*  if (0 == client.anInt8752 * -1233866115) { //Sends Client Drop request if the Logout Button fails
                    SubIncommingPacket.method1923(554378996);
                } else if (17 == -1233866115 * client.anInt8752) {
                    client.aClass25_8711.aBoolean347 = true;
                }*/
                step++;
            case 2:
                step = 0;
                break;
        }
    }

    private void updateProxy() {
        getBotManager().getReflection().getProxies().setProxy("SOCKS");
    }

    private boolean isRunning() {
        if (threadPool != null && !threadPool.isShutdown())
            return true;
        startThread();
        return false;
    }

    private void startThread() {
        threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
    }

    public void stop() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    public BotManager getBotManager() {
        return AABotManager;
    }

}

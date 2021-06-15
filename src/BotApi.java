import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class BotApi {

    private final Reflection reflection;

    public BotApi(Reflection reflection) {
        this.reflection = reflection;
    }

    public void sendBotOnline(String username, String password, String proxy) {
        username = username.toLowerCase().replaceAll(" ", "_");
        System.out.println(getSiteContent(" http://<REPLACE WITH YOUR OWN API>/onyx_bot_online?username=" + username + "&password=" + password + "&proxy=" + proxy));
    }

    public String[] getOnlineBots() {
        StringBuilder accountBuilder = new StringBuilder();
        String botsJson = getSiteContent("http://<REPLACE WITH YOUR OWN API>/onyx_get_online_bots");
        String key = "online_accounts";
        if(botsJson.contains(key)) {
            JSONObject json = new JSONObject(botsJson);
            JSONArray accounts = json.getJSONArray(key);
            for(int i = 0; i < accounts.length(); i++) {
                String account = String.valueOf(json.getJSONArray(key).get(i)).toLowerCase().replaceAll(" ", "_");
                accountBuilder.append(account).append("#");
            }
        }
        return accountBuilder.toString().contains("#") ? accountBuilder.toString().split("#") : null;
    }

    public String[] getOnlineBotsUsernames() {
        String[] bots = getOnlineBots();
        if(bots == null)
            return null;
        StringBuilder accountBuilder = new StringBuilder();
        for(String bot : bots) {
            if(bot.contains(":")) {
                String username = bot.split(":")[0];
                accountBuilder.append(username).append("#");
            }
        }
        return accountBuilder.toString().contains("#") ? accountBuilder.toString().split("#") : null;
    }

    public String[] getCurrentBotsProxies() {
        StringBuilder accountBuilder = new StringBuilder();
        String botsJson = getSiteContent("http://<REPLACE WITH YOUR OWN API>/used_proxies");
        String key = "in_use_proxies";
        if(botsJson.contains(key)) {
            JSONObject json = new JSONObject(botsJson);
            JSONArray accounts = json.getJSONArray(key);
            for(int i = 0; i < accounts.length(); i++) {
                String account = String.valueOf(json.getJSONArray(key).get(i)).toLowerCase().replaceAll(" ", "_");
                accountBuilder.append(account).append("#");
            }
        }
        return accountBuilder.toString().contains("#") ? order(accountBuilder.toString().split("#")) : null;
    }

    public String[] getPreviouslyOnlineBots() {
       StringBuilder accountBuilder = new StringBuilder();
        String botsJson = getSiteContent("http://<REPLACE WITH YOUR OWN API>/onyx_last_logged_bots");
        JSONObject json = new JSONObject(botsJson);
        for (Iterator<String> it = json.keys(); it.hasNext();) {
            String key = it.next();
            System.out.println("Order: "+key);
            String value = String.valueOf(json.get(key));
            key = key.toLowerCase().replaceAll(" ", "_");
            accountBuilder.append(key).append(":").append(value).append("#");
        }
        System.out.println("Account Builder: "+ accountBuilder);
        return accountBuilder.toString().contains("#") ? order(accountBuilder.toString().split("#")) : null;
    }

    private String[] order(String[] data) {
       Arrays.sort(data);
       return data;
    }

    public String[] getBotStatsFromHiScores(String username) {
        String botsJson = getSiteContent("http://<REPLACE WITH YOUR OWN API>/onyx_hiscore_lookup?username="+username);
        return new String[]{botsJson};
    }

    public String[] getRandomCredentials() {
        String botsJson = getSiteContent("http://<REPLACE WITH YOUR OWN API>/random_name");
        if(botsJson.contains("username") && botsJson.contains("password") && botsJson.contains("mac")) {
            JSONObject jsonObject = new JSONObject(botsJson);
            String username = jsonObject.getString("username").toLowerCase().replaceAll(" ", "_");
            String password = jsonObject.getString("password").replaceAll(" ", "_");
            String mac = jsonObject.getString("mac");
            return new String[]{username, password, mac};
        }
        return null;
    }

    private String getSiteContent(String url) {
        StringBuilder proxiesJson = new StringBuilder();
        try {
            URL api = new URL(url);
            HttpURLConnection con = (HttpURLConnection) api.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0" );
            con.setUseCaches(false);
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                proxiesJson.append(inputLine)/*.append(System.lineSeparator())*/;
            in.close();
        } catch (IOException ignored) {
           // JOptionPane.showMessageDialog(Loader.panel, "Error loading site url: "+getSiteContent(url), "Push Bot Information", JOptionPane.ERROR_MESSAGE);
          //  io.printStackTrace();
        }
        return proxiesJson.toString();
    }

    public Reflection getReflection() {
        return reflection;
    }

    public PacketHandler getPacketHandler() {
        return reflection.getBotManager().getPacketHandler();
    }
}

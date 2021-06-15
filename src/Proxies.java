import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Proxies {

    private String currentProxyAddress;
    private String currentProxyCredentials;

    private final Reflection reflection;

    public Proxies(Reflection reflection) {
        this.reflection = reflection;
    }

    public void setProxy(String type) {
        if(currentProxyAddress != null && currentProxyAddress.contains(":")) {
            String[] proxy = currentProxyAddress.split(":");
            System.setProperty("java.net.useSystemProxies", "true");
            System.getProperties().put("proxySet", "true");
            //System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
           // System.setProperty("jdk.https.auth.tunneling.disabledSchemes", "");
            System.err.println("PROXY DEBUG: TYPE-"+type+", IP:PORT-"+currentProxyAddress+", CRED-"+currentProxyCredentials);
            if (type.contains("HTTPS")) {
                System.getProperties().put("https.proxyHost", proxy[0]);
                System.getProperties().put("https.proxyPort", proxy[1]);
                System.out.println("Testing Https Proxy: "+proxy[0]);
            } else if (type.equals("SOCKS4")) {
                System.setProperty("socksProxyVersion", "4");
                System.getProperties().put("socksProxyVersion", "4");
                System.getProperties().put("socksProxyHost", proxy[0]);
                System.getProperties().put("socksProxyPort", proxy[1]);
            } else if(type.equals("SOCKS5")) {
                System.setProperty("socksProxyVersion", "5");
                System.getProperties().put("socksProxyVersion", "5");
                System.getProperties().put("socksProxyHost", proxy[0]);
                System.getProperties().put("socksProxyPort", proxy[1]);
                Loader.frame.setTitle(Loader.title + " | (" + currentProxyAddress + ")");
                if (currentProxyCredentials != null && currentProxyCredentials.contains(":")) {
                    String[] credentials = currentProxyCredentials.split(":");
                    System.getProperties().put("java.net.socks.username", credentials[0]);
                    System.getProperties().put("java.net.socks.password", credentials[1]);
                    System.out.println("Set User: " + credentials[0] + ", Set Password:" + credentials[1]);
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(credentials[0], credentials[1] == null ? new char[]{} : credentials[1].toCharArray());
                        }
                    });
                }
            }
            Loader.frame.setTitle(Loader.title + " | (" + currentProxyAddress + ")");
        }
    }

    public void clearProxy() {
        System.setProperty("java.net.useSystemProxies", "false");
        System.getProperties().put( "proxySet", "false" );
        System.setProperty("socksProxyVersion", "");
        System.getProperties().put("socksProxyVersion", "");
        System.getProperties().put( "socksProxyHost", "" );
        System.getProperties().put( "socksProxyPort", "" );
        System.getProperties().put( "java.net.socks.username", "" );
        System.getProperties().put( "java.net.socks.password", "" );
        setCurrentProxyAddress("");
        setCurrentProxyCredentials("");
    }

    public String getConnectedIPAddress() {
        try {
            return getSiteContent("http://checkip.amazonaws.com/");
           /* String proxiesJson = getSiteContent("http://api.ipify.org?format=json");
            JSONObject jsonObject = new JSONObject(proxiesJson);
            if (jsonObject.has("ip"))
                return jsonObject.get("ip").toString();*/
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getReflection().getAntiBanToggle(), "Error whilst checking http://checkip.amazonaws.com/\nProxy may be unstable.", "Test Proxy", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String[]> getRotatingProxyCredentialsRandomly(boolean setImmediately) {
        clearProxy();
        ArrayList<String[]> proxyList = new ArrayList<>();
        String proxies = getSecureSiteContent("https://proxy.webshare.io/proxy/list/download/<REPLACE WITH YOUR OWN API KEY>/-/socks/username/direct/");
        if(proxies.contains("\n")) {
            String[] proxyArray = proxies.split("\n");
            StringBuilder tempProxyArray = new StringBuilder();
            for (String tempProxyList : proxyArray) {
                if (tempProxyList != null && tempProxyList.contains(":")) {
                    String[] proxy = tempProxyList.split(":");
                    String format = "[\""+proxy[0]+":"+proxy[1]+"\", Premium]#";
                    tempProxyArray.append(format);
                }
            }
            proxyList.add(tempProxyArray.toString().split("#"));
            if(setImmediately) {
                String randomProxy = proxyArray[new SecureRandom().nextInt(proxyArray.length)];
                if (randomProxy != null && randomProxy.contains(":")) {
                    String[] proxy = randomProxy.split(":");
                    setCurrentProxyAddress(proxy[0] + ":" + proxy[1]);
                    setCurrentProxyCredentials(proxy[2] + ":" + proxy[3]);
                }
            }
        }
        String infoJson = getInfoFromProxyApi();
        if(infoJson != null) {
            JSONObject jsonObject = new JSONObject(infoJson);
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Object timestamp = jsonObject.get("automatic_refresh_last_at");
            OffsetDateTime date1 = OffsetDateTime.parse(timestamp.toString());
            Long minusTime = date1.toEpochSecond();
            long lastUpdated = Math.abs(longTime-minusTime) / 60;
            proxyList.add(0, new String[]{"Last Updated", String.valueOf(lastUpdated)});
            return proxyList;
        }
        proxyList.add(0, new String[]{"Last Updated", "-1"});
        return proxyList;
    }

    public ArrayList<String[]> getLeonsProxies(boolean setImmediately) {
        clearProxy();
        ArrayList<String[]> proxyList = new ArrayList<>();
        String proxies = getSecureSiteContent("https://proxy.webshare.io/proxy/list/download/<REPLACE WITH YOUR OWN API KEY>/-/http/username/direct/");
        if(proxies.contains("\n")) {
            String[] proxyArray = proxies.split("\n");
            StringBuilder tempProxyArray = new StringBuilder();
            for (String tempProxyList : proxyArray) {
                if (tempProxyList != null && tempProxyList.contains(":")) {
                    String[] proxy = tempProxyList.split(":");
                    String format = "[\""+proxy[0]+":"+proxy[1]+"\", Premium]#";
                    tempProxyArray.append(format);
                }
            }
            proxyList.add(tempProxyArray.toString().split("#"));
            if(setImmediately) {
                String randomProxy = proxyArray[new SecureRandom().nextInt(proxyArray.length)];
                if (randomProxy != null && randomProxy.contains(":")) {
                    String[] proxy = randomProxy.split(":");
                    setCurrentProxyAddress(proxy[0] + ":" + proxy[1]);
                    setCurrentProxyCredentials(proxy[2] + ":" + proxy[3]);
                }
            }
        }
        String infoJson = getInfoFromProxyApi();
        if(infoJson != null) {
            JSONObject jsonObject = new JSONObject(infoJson);
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Object timestamp = jsonObject.get("automatic_refresh_last_at");
            OffsetDateTime date1 = OffsetDateTime.parse(timestamp.toString());
            Long minusTime = date1.toEpochSecond();
            long lastUpdated = Math.abs(longTime-minusTime) / 60;
            proxyList.add(0, new String[]{"Last Updated", String.valueOf(lastUpdated)});
            return proxyList;
        }
        proxyList.add(0, new String[]{"Last Updated", "-1"});
        return proxyList;
    }


    public void setRotatingProxyCredentials() {
        setCurrentProxyAddress("p.webshare.io:80");
        setCurrentProxyCredentials("<API USERNAME>:<API PASSWORD>");
    }

    public ArrayList<String[]> getBestProxyFromTrevorsApi() {
        clearProxy();
        ArrayList<String[]> proxyList = new ArrayList<>();
        try {
            String proxiesJson = getSiteContent("http://<REPLACE WITH YOUR PROXY API>/proxy/proxy-list.json");
            if (!proxiesJson.contains("run timestamp")) {
                JOptionPane.showMessageDialog(getReflection().getAntiBanToggle(), "Proxies are currently being generated\nPlease wait 30 seconds and try again.", "Get Proxies", JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println("Downloaded Results: " + proxiesJson);
                JSONObject jsonObject = new JSONObject(proxiesJson);
                Date now = new Date();
                Long longTime = now.getTime() / 1000;
                Long timestamp = jsonObject.getLong("run timestamp");
                long minutes = Math.abs(longTime-timestamp) / 60;

                String[] proxiesType = getProxyTypes();

                proxyList.add(0, new String[]{"Last Updated", String.valueOf(minutes)});

                StringBuilder tempProxyArray = new StringBuilder();
                for(String proxyType : proxiesType) {
                    if (proxyType.length() > 0 && jsonObject.has(proxyType)) {
                        JSONArray socksArray = jsonObject.getJSONArray(proxyType);
                        for(int tempProxy=0; tempProxy < socksArray.length(); tempProxy++)
                            tempProxyArray.append(socksArray.getJSONArray(tempProxy).toString()).append("#");
                        String[] addProxy = tempProxyArray.toString().split("#");
                        proxyList.add(1, addProxy);
                        System.out.println("Adding Proxy: "+ Arrays.toString(addProxy));
                       // System.out.println("FinalList: "+ Arrays.toString(Arrays.stream(proxyList.get(1)).toArray()));
                        System.out.println("First Proxy From List:" + socksArray.get(0));
                       /* int size = socksArray.length();
                        JSONArray randomProxy = socksArray.getJSONArray(new SecureRandom().nextInt(size));
                        String fetchedProxy = randomProxy.get(0).toString();
                        System.out.println("List contains " + proxyType);
                        if (fetchedProxy != null && fetchedProxy.length() > 1)
                            proxyList.set(0, new String[]{"Last Updated", String.valueOf(minutes)});*/
                    }
                }
            }
        } catch (Exception e) {
            clearProxy();
            if(e.getMessage().contains("Expected a ',' or ']'") || e.getMessage().contains("java.lang.IndexOutOfBoundsException: Index: 1, Size: 1") || e.getMessage().contains("Unterminated string at")) {
                JOptionPane.showMessageDialog(getReflection().getAntiBanToggle(), "Proxies are currently being generated\nPlease wait 30 seconds and try again.", "Get Proxies", JOptionPane.WARNING_MESSAGE);
            }
            e.printStackTrace();
        }
        return proxyList;
    }

    private String[] getProxyTypes() {
        StringBuilder stringBuilder = new StringBuilder();
        if(getReflection().getAntiBanToggle().isHttpFilterSelected())
            stringBuilder.append("HTTP,").append("HTTPS,");
        if(getReflection().getAntiBanToggle().isSocksFilterSelected())
            stringBuilder.append("SOCKS4,").append("SOCKS5,");
        return stringBuilder.toString().split(",");
    }

    private String getSiteContent(String url) {
        StringBuilder proxiesJson = new StringBuilder();
        try {
            URL api = new URL(url);
            HttpURLConnection con = (HttpURLConnection) api.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0" );
            con.setUseCaches(false);
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                proxiesJson.append(inputLine).append(System.lineSeparator());
            in.close();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(getReflection().getAntiBanToggle(), "Error connecting to proxy.", "Test Proxy", JOptionPane.ERROR_MESSAGE);
            io.printStackTrace();
        }
        return proxiesJson.toString();
    }

    private String getSecureSiteContent(String url) {
        StringBuilder proxiesJson = new StringBuilder();
        try {
            URL api = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) api.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0" );
            con.setUseCaches(false);
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                proxiesJson.append(inputLine).append(System.lineSeparator());
            in.close();
        } catch (IOException ignored) {
           // io.printStackTrace();
        }
        return proxiesJson.toString();
    }

    public String getInfoFromProxyApi() {
        String API_KEY = "Token <REPLACE WITH YOUR API KEY>";
        StringBuilder proxiesJson = new StringBuilder();
        try {
            URL api = new URL("https://proxy.webshare.io/api/proxy/replacement/info/");
            HttpsURLConnection con = (HttpsURLConnection) api.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("GET");
            con.setRequestProperty ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            con.setRequestProperty ("Authorization", API_KEY);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                proxiesJson.append(inputLine).append(System.lineSeparator());
            in.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return proxiesJson.toString();
    }

    public String sendPostRequest(String s, String toString) {
        StringBuilder proxiesJson = new StringBuilder();
        try {
            URL api = new URL(s);
            HttpURLConnection con = (HttpURLConnection) api.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("POST");
            con.setRequestProperty ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty( "charset", "utf-8");
            toString = toString.replace("say ", "");
            String urlParameters  = "translatetext="+toString;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
                wr.write( postData );
            }
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                proxiesJson.append(inputLine).append(System.lineSeparator());
            in.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        String output = proxiesJson.toString();
        if(output.length() > 10)
            output = output.substring(output.indexOf("height:250px;\"/>") + "height:250px;\"/>".length(), (output.indexOf("</textarea>") - 1));
        return output;
    }

    public void setCurrentProxyCredentials(String currentProxyCredentials) {
        this.currentProxyCredentials = currentProxyCredentials;
    }

    public void setCurrentProxyAddress(String currentProxyAddress) {
        this.currentProxyAddress = currentProxyAddress;
    }

    public String getCurrentProxyCredentials() {
        return currentProxyCredentials;
    }

    public String getCurrentProxyAddress() {
        return currentProxyAddress;
    }

    public Reflection getReflection() {
        return reflection;
    }
}
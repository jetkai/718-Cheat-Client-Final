
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class DumpProxies {

    private final ArrayList<String> SOCKS4 = new ArrayList<>();
    private final ArrayList<String> SOCKS5 = new ArrayList<>();

    private final ArrayList<String> ALIVE_SOCKS4 = new ArrayList<>();
    private final ArrayList<String> ALIVE_SOCKS5 = new ArrayList<>();

    public static void main(String[] args) {
        DumpProxies dumpProxies = new DumpProxies();
        dumpProxies.initDumper();
        //dumpProxies.initChecker();
    }

    private void initDumper() {
        ThreadFactory threadFactory = new BotThreadFactory();
        Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(() -> {
            connectURL();
            appendProxiesToFile();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void initChecker() {
        getProxiesFromFile();
        if(SOCKS4.size() > 0 || SOCKS5.size() > 0)
            initTestProxyThreads(100);
    }

    private void connectURL() {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        StringBuilder stringBuilder = new StringBuilder();

        try {
            String API_URL = "https://www.proxyscan.io/api/proxy?type=socks4,socks5&limit=100&lastCheck=3600"; //Thx for API link @JayArrowz
            url = new URL(API_URL);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null)
                stringBuilder.append(line).append(System.lineSeparator());
        } catch (IOException mue) {
            mue.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        getProxiesFromJson(stringBuilder.toString());
    }

    private void getProxiesFromJson(String response) {
        JSONArray jsonArray = new JSONArray(response);
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String proxyFormat = (jsonObject.get("Ip") + ":" + jsonObject.get("Port"));
            String type = String.valueOf(jsonObject.getJSONArray("Type").getString(0));
            if(type.equals("SOCKS4")) {
                SOCKS4.add(proxyFormat);
            } else if(type.equals("SOCKS5")) {
                SOCKS5.add(proxyFormat);
            }
        }
    }

    private void getProxiesFromFile() {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("socks4.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if(!SOCKS4.contains(line))
                        SOCKS4.add(line);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("socks5.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if(!SOCKS5.contains(line))
                        SOCKS5.add(line);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Proxies loaded");
    }

    private void initTestProxyThreads(int threads) {
        IntStream.range(0, threads).forEach(i -> {
            ThreadFactory threadFactory = new BotThreadFactory();
            Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(() -> testProxies("SOCKS4", i), 0, 3, TimeUnit.SECONDS);
        });
        IntStream.range(0, threads).forEach(i -> {
            ThreadFactory threadFactory = new BotThreadFactory();
            Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(() -> testProxies("SOCKS5", i), 0, 3, TimeUnit.SECONDS);
        });
    }

    private void appendProxiesToFile() {
        SOCKS4.forEach(socks4 -> TextEditor.writeToFile("socks4.txt", socks4));
        SOCKS5.forEach(socks5 -> TextEditor.writeToFile("socks5.txt", socks5));
    }

    private void testProxies(String type, int threadId) {
        Socket socket = null;
        Class318 rsSocket = null;
        String proxyAddress = null;
        int proxyPort = 0;
        try {
            if (type.equals("SOCKS4")) {
                String[] proxyFormat = SOCKS4.remove(threadId).split(":");
                proxyAddress = proxyFormat[0];
                proxyPort = Integer.parseInt(proxyFormat[1]);
            } else if (type.equals("SOCKS5")) {
                String[] proxyFormat = SOCKS5.remove(threadId).split(":");
                proxyAddress = proxyFormat[0];
                proxyPort = Integer.parseInt(proxyFormat[1]);
            } else
                return;
            if (proxyAddress == null || proxyPort == -1)
                return;
            String proxyAddressAndPort = proxyAddress + ":" + proxyPort;
            Proxy proxies = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddress, proxyPort));
            socket = new Socket(proxies);

            try {
                socket.setTcpNoDelay(true);
                socket.setReuseAddress(true);
                socket.setSoTimeout(3000);
                socket.setKeepAlive(true);
                socket.setSoLinger(true, 3000);
                int serverPort = 43594;
                String serverAddress = "54.39.28.201";
                socket.connect(new InetSocketAddress(serverAddress, serverPort));
            } catch (IOException e) {
                removeProxyFromFile(type.equals("SOCKS4") ? "SOCKS4.txt" : "SOCKS5.txt", proxyAddressAndPort);
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            Class360.aClass25_3905.method389(Class264_Sub4.method2515(socket, 15000, -649048480), Class241.aClass471_2705.address, (byte) 0);
            Class360.aClass25_3905.method383((short) 8191);
            // Bits above ^ Added after
            rsSocket = Class264_Sub4.method2515(socket, 15000, 0);
            Class298_Sub36 stream = Class82_Sub6.method885(-1825045529);
            //Class360.aClass25_3905.method389(Class264_Sub4.method2515(Class241.aClass471_2705.method6056(295506052), 15000, -649048480), Class241.aClass471_2705.address, (byte)0);
            stream.aClass298_Sub53_Sub2_7396.writeByte(Class211.aClass211_2413.anInt2418 * -1813470547);
            stream.aClass298_Sub53_Sub2_7396.writeShort(0, 16711935);
            int i_1_ = 385051775 * stream.aClass298_Sub53_Sub2_7396.index;
            stream.aClass298_Sub53_Sub2_7396.writeInt(718, -1354427278);
            if (Settings.SUB_BUILD != -1)
                stream.aClass298_Sub53_Sub2_7396.writeInt(Settings.SUB_BUILD, 376398822);
            if (-122629167 * Class360.anInt3868 == 264)
                stream.aClass298_Sub53_Sub2_7396.writeByte((5 == client.anInt8752 * -1233866115) ? 1 : 0);
            RsByteBuffer class298_sub53 = Class322.method3933(-1454924768);
            class298_sub53.writeByte(2084404473 * Class360.anInt3873);
            class298_sub53.writeShort((int) (Math.random() * 9.9999999E7D), 16711935);
            class298_sub53.writeByte(Class321.aClass429_3357.method242(694163818));
            class298_sub53.writeInt(client.anInt8665 * -1154804873, -1393012818);
            for (int i_2_ = 0; i_2_ < 6; i_2_++)
                class298_sub53.writeInt((int) (Math.random() * 9.9999999E7D), 499420945);
            class298_sub53.writeLong(client.aLong8675 * -8380697455384249973L);
            class298_sub53.writeByte(-937307905 * client.aClass411_8944.gameType);
            class298_sub53.writeByte((int) (Math.random() * 9.9999999E7D));
            class298_sub53.applyRsa(Class50.aBigInteger500, Class50.MODULUS, 1533826109);
            stream.aClass298_Sub53_Sub2_7396.writeBytes(class298_sub53.buffer, 0, 385051775 * class298_sub53.index, (short) -29754);
            stream.aClass298_Sub53_Sub2_7396.method3593(385051775 * stream.aClass298_Sub53_Sub2_7396.index - i_1_, 1585504133);
            Class360.aClass25_3905.method390(stream, (byte) -57);
            Class360.aClass25_3905.method386(-1781606732);
            Class360.anInt3896 = 1009016718;
            System.out.println("Is Available: " + Class360.aClass25_3905.method387(537308016).isAvailable(2, (byte) -17));
       /* try {
            rsSocket.queueBytes(2, stream.buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int socketResponse = rsSocket.read();*/
            int socketResponse = 0;
            if (socketResponse != -1) {
                if (type.equals("SOCKS4"))
                    TextEditor.writeToFile("alive_socks4.txt", proxyAddressAndPort);
                    // ALIVE_SOCKS4.add(proxyAddressAndPort);
                else
                    TextEditor.writeToFile("alive_socks5.txt", proxyAddressAndPort);
                //ALIVE_SOCKS5.add(proxyAddressAndPort);
                System.out.println("Great Proxy! (" + proxyAddress + ":" + proxyPort + ")");
            }
        } catch (IOException e) {
            System.out.println("Bad Proxy! (" + proxyAddress + ":" + proxyPort + ")");
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
           // rsSocket.close();
        }
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
     //   rsSocket.close();
        //appendAliveProxiesToFile();
    }

    private void removeProxyFromFile(String folderName, String proxyAddress) {
      /*  try {
            String result = fileToString(folderName);
            result = result.replaceAll(proxyAddress + "\n", "");
            PrintWriter writer = new PrintWriter(folderName);
            writer.append(result);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}

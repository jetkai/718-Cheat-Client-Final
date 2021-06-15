import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Loader extends Applet {
    private static final long serialVersionUID = 7639088664641445302L;

    private static final Properties client_parameters = new Properties();

    public static JFrame frame;

    public static client client = null;
    public static Loader loader = null;
    public static JLayeredPane jLayeredPane = null;
    public static JPanel panel = null;
    public static JPanel botPanel = null;

    public static String title = "Onyx 718 - Cheat Client (Jet Kai)";

    private final Reflection reflection;

    public Loader(Reflection reflection) {
        this.reflection = reflection;
    }

    public void init() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        setParams();
        jLayeredPane = new JLayeredPane();
        client = new client();
        loader = new Loader(reflection);
        client.supplyApplet(loader);
        frame = new JFrame();
        panel = new JPanel();
        setupKeys();
        botPanel = new JPanel();
        panel.setLayout(new BorderLayout());
        botPanel.setLayout(new BorderLayout());
        panel.add(loader);
        jLayeredPane.add(panel, 0, 0);
        jLayeredPane.add(botPanel, 1, 0);

        panel.setPreferredSize(new Dimension(768, 503));
        frame.setMinimumSize(new Dimension(768, 503));

        //botPanel.setBackground(Color.TRANSLUCENT);
        botPanel.setBounds(8, 345, 505, 113);
        panel.setOpaque(true);
        botPanel.setOpaque(false);

        panel.setBackground(Color.BLACK);
        botPanel.setBackground(Color.WHITE);
        botPanel.setVisible(false);
        //final CardLayout layout = new CardLayout();
        frame.setTitle(title);
        frame.setResizable(true);
        frame.getContentPane().add(botPanel, "Center");
        frame.getContentPane().add(panel, "Center");
        JMenuBar bar = new JMenuBar();
        setupJComboBars(bar);
        frame.setJMenuBar(bar);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("icon.ico")));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
        client.init();
        client.start();
    }

    private void setupKeys() {
        try {
            LogManager.getLogManager().reset();
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeEvent) { }
                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeEvent) { }
                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                    System.out.println("KeyCode: "+nativeEvent.getRawCode());
                    //1 = 35
                    //2 = 40
                    //3 = 34
                    switch(nativeEvent.getRawCode()) {
                        case 35://1 on Numpad
                          //frame.
                            break;
                        case 40://2 on Numpad
                            frame.setAlwaysOnTop(true);
                            frame.toFront();
                            frame.requestFocus();
                            frame.setAlwaysOnTop(false);
                            break;
                        case 33://9 on Numpad
                            boolean enabled = Reflection.isTriHardChat = !Reflection.isTriHardChat;
                            String enabledMessage = "TriHard Mode: "+enabled;
                            System.out.println(enabledMessage);
                            getReflection().getPacketHandler().sendGameMessage(enabledMessage);
                            break;
                    }
                }
            });
        } catch (NativeHookException ignored) { }
    }

    private void setupJComboBars(JMenuBar bar) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("settings_24by24.png");
            BufferedImage image = inputStream != null ? ImageIO.read(inputStream) : ImageIO.read(new File("media/settings_24by24.png"));
            if (image != null) {
                JButton button = new JButton(new ImageIcon(image));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    try {
                        getReflection().commands("antiban");
                    } catch (IllegalAccessException | NoSuchFieldException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }));
                bar.add(button);
            }
            String[] commands = {"COMMANDS", "INITBOTS", "COORDS", "REVEAL", "SHOW", "HIDE", "RESETLOGOUT"};
            JComboBox<String> cb = new JComboBox<>(commands);
            cb.setLightWeightPopupEnabled(false);
            cb.setMaximumSize(new Dimension(125, 25));
            bar.add(cb);
            cb.addActionListener(e -> {
                try {
                    getReflection().commands(Objects.requireNonNull(cb.getSelectedItem()).toString().toLowerCase());
                } catch (IllegalAccessException | NoSuchFieldException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                cb.setSelectedIndex(0);
            });
            JTextField commandTextField = new JTextField();
            commandTextField.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                try {
                    getReflection().commands(commandTextField.getText());
                    commandTextField.setText("");
                } catch (IllegalAccessException | NoSuchFieldException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }));
            bar.add(commandTextField);
            String[] scripts = {
                    "PLUGINS", "THIEVING_HOME", "THIEVING_CAMMY", "THIEVING_DZONE", "GEM_MINER", "GEM_MINER_2", "GEM_CUTTER",
                    "GOLD_MINER", "GOLD_MINER2", "GOLD_MINER3", "MAHOGANY", "MAHOGANY_2", "BONEY_ALTAR", "COAL_MINER", "COAL_MINER2", "IRON_MINER",
                    "REDWOOD", "SAW_MILLY", "BAR_MAKER", "CANNON_BALLER", "MAPLES", "GNOME_AGILITY", "WILDERNESS_AGILITY", "DRAYNOR_PICKPOCKET", "ARDOUGNE_KNIGHT_PICKPOCKET",
                    "ARDOUGNE_PALADIN_PICKPOCKET", "TRIVIA"};
            JComboBox<String> script = new JComboBox<>(scripts);
            script.setLightWeightPopupEnabled(false);
            script.setMaximumSize(new Dimension(85, 25));
            bar.add(script);

            for (Option o : Option.values()) {
                MenuButton menu = new MenuButton(o.name);
                menu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                menu.setBorder(new LineBorder(new Color(0xC1C1C1)));
                menu.setMargin(new Insets(0, 5, 0, 5));
                menu.setHorizontalAlignment(SwingConstants.CENTER);
                Dimension dimension = new Dimension(55, 25);
                menu.setMinimumSize(dimension);
                menu.setPreferredSize(dimension);
                menu.setBorder(menu.getText().equals("START") ? new LineBorder(new Color(0x67EA67)) : new LineBorder(new Color(0xD43434)));
                menu.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        switch (o.action) {
                            case "START_SCRIPT":
                                getReflection().getBotManager().startScript(String.valueOf(script.getSelectedItem()));
                                break;
                            case "STOP SCRIPT":
                                getReflection().getBotManager().stopScripts();
                                break;
                        }
                    }
                    public void mousePressed(MouseEvent e) { }
                    public void mouseReleased(MouseEvent e) { }
                    public void mouseEntered(MouseEvent e) { }
                    public void mouseExited(MouseEvent e) { }
                });
                bar.add(menu);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void setParams() {
        client_parameters.put("separate_jvm", "true");
        client_parameters.put("boxbgcolor", "black");
        client_parameters.put("image", "http://www.runescape.com/img/game/splash2.gif");
        client_parameters.put("centerimage", "true");
        client_parameters.put("boxborder", "false");
        client_parameters.put("java_arguments", "-Xmx1024m -Xss2m -Dsun.java2d.noddraw=true -XX:CompileThreshold=1500 -Xincgc -XX:+UseConcMarkSweepGC -XX:+UseParNewGC");
        client_parameters.put("27", "0");
        client_parameters.put("1", "0");
        client_parameters.put("16", "false");
        client_parameters.put("17", "false");
        client_parameters.put("21", "1");
        client_parameters.put("30", "false");
        client_parameters.put("20", Settings.local);
        client_parameters.put("29", "");
        client_parameters.put("11", "true");
        client_parameters.put("25", "1378752098");
        client_parameters.put("28", "0");
        client_parameters.put("8", ".runescape.com");
        client_parameters.put("23", "false");
        client_parameters.put("32", "0");
        client_parameters.put("15", "wwGlrZHF5gKN6D3mDdihco3oPeYN2KFybL9hUUFqOvk");
        client_parameters.put("0", "IjGJjn4L3q5lRpOR9ClzZQ");
        client_parameters.put("2", "");
        client_parameters.put("4", "1");
        client_parameters.put("14", "");
        client_parameters.put("5", "8194");
        client_parameters.put("-1", "QlwePyRU5GcnAn1lr035ag");
        client_parameters.put("6", "0");
        client_parameters.put("24", "true,false,0,43,200,18,0,21,354,-15,Verdana,11,0xF4ECE9,candy_bar_middle.gif,candy_bar_back.gif,candy_bar_outline_left.gif,candy_bar_outline_right.gif,candy_bar_outline_top.gif,candy_bar_outline_bottom.gif,loadbar_body_left.gif,loadbar_body_right.gif,loadbar_body_fill.gif,6");
        client_parameters.put("3", "hAJWGrsaETglRjuwxMwnlA/d5W6EgYWx");
        client_parameters.put("12", "false");
        client_parameters.put("13", "0");
        client_parameters.put("26", "0");
        client_parameters.put("9", "77");
        client_parameters.put("22", "false");
        client_parameters.put("18", "false");
        client_parameters.put("33", "");
        client_parameters.put("haveie6", "false");
    }

    public enum Option {
        START("START", "START_SCRIPT"),
        STOP("STOP", "STOP SCRIPT");

        private final String name;
        private final String action;

        Option(String name, String action) {
            this.name = name;
            this.action = action;
        }
    }


    public String getParameter(String string) {
        return (String)client_parameters.get(string);
    }

    public URL getDocumentBase() {
        return getCodeBase();
    }

    public URL getCodeBase() {
        Settings.SUB_BUILD = 35;
        try {
            return new URL("http://" + Settings.local);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException|java.net.URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    public Reflection getReflection() {
        return reflection;
    }
}

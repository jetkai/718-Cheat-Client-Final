import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AntiBanToggle extends JDialog {

    private JPanel contentPane;
    private JButton saveButton;
    private JButton cancelButton;

    private JCheckBox logoutFromPlayersExcludingCheckBox;
    private JCheckBox logoutFromHiddenStaffCheckBox;
    private JCheckBox logoutFromAnyStaffCheckBox;
    private JCheckBox enableAntiBanCheckBox;
    private JCheckBox useRandomSOCKSProxiesCheckBox;
    private JCheckBox useRandomMacAddressCheckBox;
    private JCheckBox useSystemSerialCheckBox;
    private JTextField macTextField;
    private JTextField serialTextField;
    private JTextField IPPortTextField;
    private JButton testProxyButton;
    private JTextField proxyUserProxyPassTextField;
    private JButton generateButton;
    private JButton getProxyButton;
    private JLabel connectedIPLabel;
    private JCheckBox paidProxiesCheckBox;
    private JLabel lastCheckedLable;
    private JComboBox IPPortDropMenu;
    private JTabbedPane tabbedPane;
    private JList list1;
    private JComboBox comboBox1;
    private JButton loginButton;
    private JButton hiscoreButton;
    private JButton refreshButton;
    private JTree tree1;
    private JTextArea TODOIGNORETHISTABTextArea;
    private JButton compileSaveButton;
    private JButton pickScriptButton;
    private JButton popoutButton;
    private JLabel totalLable;
    private JTabbedPane tabbedPane2;
    private JList recentAccountsList;
    private JButton recentLoginButton;
    private JButton recentHiscoreButton;
    private JButton recentRefreshButton;
    private JTextArea testTextArea;
    private JButton generateGenerateButton;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton generateLoginButton;
    private JTextField macAddressTextField;
    private JList onlineBotsList;
    private JPanel exitPanel;
    private JPanel accountsTab;
    private JButton onlineRefreshButton;
    private JCheckBox HTTPHTTPSProxiesCheckBox;
    private JCheckBox SOCKS4SOCKS5ProxiesCheckBox;
    private JTextField IPPortManualProxyTextField;
    private JButton xButton;

    private final Reflection reflection;

    public AntiBanToggle(Reflection reflection) {
        this.reflection = reflection;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(saveButton);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        SwingUtilities.invokeLater(() -> {
            lastCheckedLable.setBorder(new EmptyBorder(0, 4, 0, 0));
            connectedIPLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        });
        /* Actions */

        enableAntiBanCheckBox.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            if (enableAntiBanCheckBox.isSelected()) {
                //  logoutFromAnyStaffCheckBox.setEnabled(true);
                logoutFromHiddenStaffCheckBox.setEnabled(true);
                logoutFromPlayersExcludingCheckBox.setEnabled(true);
                // useRandomMacAddressCheckBox.setEnabled(true);
                //    useRandomSOCKSProxiesCheckBox.setEnabled(true);
                //   useSystemSerialCheckBox.setEnabled(true);
            } else {
                logoutFromAnyStaffCheckBox.setEnabled(false);
                logoutFromHiddenStaffCheckBox.setEnabled(false);
                logoutFromPlayersExcludingCheckBox.setEnabled(false);
                useRandomMacAddressCheckBox.setEnabled(false);
                useRandomSOCKSProxiesCheckBox.setEnabled(false);
                useSystemSerialCheckBox.setEnabled(false);
            }
        }));

        recentHiscoreButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            String recentAccount = recentAccountsList.getSelectedValue().toString();
            if(recentAccount != null && recentAccount.contains(":"))
                testTextArea.setText(Arrays.toString(getReflection().getTrevorsApi().getBotStatsFromHiScores(recentAccount.split(":")[0])));
        }));

        generateGenerateButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            String[] credentials = getReflection().getTrevorsApi().getRandomCredentials();
            usernameField.setText(credentials[0]);
            passwordField.setText(credentials[1]);
            macAddressTextField.setText(credentials[2]);
            usernameField.setEnabled(true);
            passwordField.setEnabled(true);
            macAddressTextField.setEnabled(true);
        }));

        recentLoginButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            String credentials = String.valueOf(recentAccountsList.getSelectedValue());
            if(credentials != null && credentials.contains(":")) {
                String[] credentialsArray = credentials.split(":");
                Class360.username = credentialsArray[0].replaceAll(" ", "_").toLowerCase();
                Class360.password = credentialsArray[1];
                Class63.method741(credentialsArray[0], credentialsArray[1], 2101690439);
                dispose();
            }
        }));

        recentRefreshButton.addActionListener(e -> SwingUtilities.invokeLater(this::refreshRecentAccounts));

        generateButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            try {
                getReflection().commands("newmac");
                updateUI();
            } catch (IllegalAccessException | NoSuchFieldException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }));

        IPPortDropMenu.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            Object selected = comboBox.getSelectedItem();
            String proxy = String.valueOf(selected);
            boolean isPremium = proxy.contains("Premium");
            boolean isSocksProxy = proxy.contains("SOCKS");
            String[] paramContains = new String[]{"[", "]", "\""};
            String proxyType = "SOCKS5";
            if(isSocksProxy)
                proxyType = proxy.split(",")[2].replaceAll("\"", "").replace("]", "");
            proxy = proxy.split(",")[0];
            for (String param : paramContains) {
                if (proxy.contains(param))
                    proxy = proxy.replace(param, "");
            }
            if (proxy.contains(":")) {
                String credentials = isPremium ? "<username>:<password>" : "";
                if(proxy.equals("98.185.94.76:4145")) {
                    JOptionPane.showMessageDialog(this, "Blacklisted IP - You are not allowed this IP Address (Mule Account)\n" +
                            "[98.185.94.76:4145]", "Get Proxy", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                getReflection().getProxies().setCurrentProxyAddress(proxy);
                getReflection().getProxies().setCurrentProxyCredentials(credentials);
                proxyUserProxyPassTextField.setText(credentials);
               // if(isSocks)
                getReflection().getProxies().setProxy(proxyType);
                System.out.println("Setting Proxy: "+proxy);
            }
        }));

        onlineRefreshButton.addActionListener(e -> SwingUtilities.invokeLater(this::refreshOnlineAccounts));

        tabbedPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JTabbedPane parent = (JTabbedPane) e.getSource();
                    if(parent.getSelectedIndex() == 1) { //Accounts Tab
                        //refreshLists();
                    }
                });
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        generateLoginButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String macAddress = macAddressTextField.getText();
            if(!username.equals("Username") && !password.equals("Password") && !macAddress.equals("Mac Address")) {
                try {
                    getReflection().commands("setmac "+macAddress);
                } catch (IllegalAccessException | NoSuchFieldException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                Class63.method741(username, password, 2101690439);
                dispose();
            } else {
                JOptionPane.showMessageDialog(contentPane, "You must generate an account before clicking this button.", "Generate", JOptionPane.ERROR_MESSAGE);
            }
        }));

        getProxyButton.addActionListener(e -> SwingUtilities.invokeLater(this::updateProxy));

        testProxyButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            String connectIP = getReflection().getProxies().getConnectedIPAddress();
            connectedIPLabel.setText(connectIP != null ? "Connected IP: " + connectIP : "Connected IP: " + connectIP + "(Errors)");
        }));

        contentPane.registerKeyboardAction(e -> SwingUtilities.invokeLater(this::onCancel),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        exitPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                SwingUtilities.invokeLater(() -> {
                    JPanel parent = (JPanel)e.getSource();
                    parent.setBackground(new Color(23, 32,48));
                    parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    parent.revalidate();
                    onOK();
                });
            }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JPanel parent = (JPanel) e.getSource();
                    parent.setBackground(new Color(51, 71, 108));
                    parent.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    parent.revalidate();
                });
            }
            @Override
            public void mouseExited(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JPanel parent = (JPanel) e.getSource();
                    parent.setBackground(new Color(23, 32, 48));
                    parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    parent.revalidate();
                });
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> onCancel());
            }
        });
    }

    public void refreshRecentAccounts() {
        Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(() -> {
            String[] accounts = getReflection().getTrevorsApi().getPreviouslyOnlineBots();
            DefaultListModel<String> model = new DefaultListModel<>();
            System.out.println("Recent Accounts1: " + Arrays.toString(accounts));
            if (accounts != null) {
                for (String account : accounts)
                    model.addElement(account);
            }
            SwingUtilities.invokeLater(() -> recentAccountsList.setModel(model));
        }, 100, TimeUnit.MILLISECONDS);
    }

    public void refreshOnlineAccounts() {
        Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(() -> {
            String[] accounts = getReflection().getTrevorsApi().getOnlineBots();
            DefaultListModel<String> model = new DefaultListModel<>();
            if (accounts != null) {
                for (String account : accounts)
                    model.addElement(account);
            }
            SwingUtilities.invokeLater(() -> onlineBotsList.setModel(model));
        }, 100, TimeUnit.MILLISECONDS);
    }

    public void updateUI() {
        SwingUtilities.invokeLater(() -> {
            macTextField.setText(getReflection().getCustomMacAddress());
            serialTextField.setText(getReflection().getCustomSerial());
        });
    }

    public void refreshLists() {
        refreshRecentAccounts();
        refreshOnlineAccounts();
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void showDialogue(AntiBanToggle dialogue) {
        dialogue.setVisible(true);
        dialogue.setAlwaysOnTop(true);
    }

    public void updateProxy() {
        ArrayList<String[]> proxies = paidProxiesCheckBox.isSelected()
                ? getReflection().getProxies().getRotatingProxyCredentialsRandomly(false)
                : getReflection().getProxies().getBestProxyFromTrevorsApi();
        if(proxies == null || proxies.size() <= 0)
            return;
        IPPortDropMenu.removeAllItems();
        try {
        String[] proxyArray = proxies.get(1);
        if(proxyArray != null) {
            for (String proxy : proxyArray)
                IPPortDropMenu.addItem(proxy);
            ArrayList<String> dropMenuItems = getDropMenuItems();
            String randomProxy = dropMenuItems.get(new SecureRandom().nextInt(dropMenuItems.size()));
            IPPortDropMenu.setSelectedItem(randomProxy);
            totalLable.setText("Total: " + proxyArray.length);
            lastCheckedLable.setText("Last Checked: " + proxies.get(0)[1] + " minutes ago");
            proxyUserProxyPassTextField.setText(getReflection().getProxies().getCurrentProxyCredentials());
        }
        } catch(IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "No proxies found with the selected filter.", "Proxies", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String findDropMenuString(String stringToFind) {
        int size = IPPortDropMenu.getItemCount();
        for (int i = 0; i < size; i++) {
            String item = String.valueOf(IPPortDropMenu.getItemAt(i));
            if(item.contains(stringToFind))
                return item;
        }
        return null;
    }

    private ArrayList<String> getDropMenuItems() {
        ArrayList<String> dropMenuItems = new ArrayList<>();
        int size = IPPortDropMenu.getItemCount();
        for (int i = 0; i < size; i++) {
            String item = String.valueOf(IPPortDropMenu.getItemAt(i));
            dropMenuItems.add(item);
        }
        return dropMenuItems;
    }


    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        FlatCarbonIJTheme.install();
        AntiBanToggle antiBanToggle = new AntiBanToggle(null);
        // antiBanToggle.setLocationRelativeTo(null);
        antiBanToggle.setUndecorated(true);
        antiBanToggle.pack();
        antiBanToggle.showDialogue(antiBanToggle);
        //antiBanToggle.setLocationRelativeTo(Loader.frame);
        System.exit(0);
    }

    public void getLoginFloodProperties() {
        paidProxiesCheckBox.setSelected(true);
        enableAntiBanCheckBox.setSelected(false);
        logoutFromAnyStaffCheckBox.setSelected(false);
        logoutFromHiddenStaffCheckBox.setSelected(false);
        logoutFromPlayersExcludingCheckBox.setSelected(false);
    }

    public boolean isUsingPaidProxies() {
        return paidProxiesCheckBox.isSelected();
    }

    public boolean isAntiBanEnabled() {
        return enableAntiBanCheckBox.isSelected();
    }

    public boolean isLogoutFromHiddenStaffEnabled() {
        return logoutFromHiddenStaffCheckBox.isSelected() && isAntiBanEnabled();
    }

    public boolean isLogoutFromStaffEnabled() {
        return logoutFromAnyStaffCheckBox.isSelected() && isAntiBanEnabled();
    }

    public boolean isLogoutFromPlayersEnabled() {
        return logoutFromPlayersExcludingCheckBox.isSelected() && isAntiBanEnabled();
    }

    public boolean isHttpFilterSelected() {
        return HTTPHTTPSProxiesCheckBox.isSelected();
    }

    public boolean isSocksFilterSelected() {
        return SOCKS4SOCKS5ProxiesCheckBox.isSelected();
    }

    public Reflection getReflection() {
        return reflection;
    }
}

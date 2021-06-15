import java.util.ArrayList;

public class BotUI {

    private ArrayList<Object[]> lastInventoryTick;
    private ArrayList<Object[]> totalHarvestedResources;
    private String action;

    private final BotManager botManager;

    public BotUI(BotManager botManager) {
        this.botManager = botManager;
    }

    public void setup() {
        action = "STARTING";
        lastInventoryTick = new ArrayList<>();
        totalHarvestedResources = new ArrayList<>();
        for (int i = 0; i < 28; i++)
            lastInventoryTick.add(new Object[]{"", 0});
    }

    public void trackResourceTick() {

        getBotManager().setLastStatus(getAction());
        getBotManager().sendBotUptime();
        getBotManager().drawBotUI();

        if(getBotManager().isLoggedOut() || !getBotManager().getPacketHandler().isLoggedIn())
            return;

        int key = 93;

        Class298_Sub9 class298_sub9 = (Class298_Sub9)Class298_Sub9.aClass437_7224.method5812(key);

        ArrayList<Object[]> inventory = new ArrayList<>();
        for (int i = 0; i < 28; i++)
            inventory.add(new Object[]{"NONE", 0});

        for(int slot = 0; slot < 28; slot++) {
            int itemId = class298_sub9.anIntArray7226[slot];
            int amount = class298_sub9.anIntArray7227[slot];
            if(itemId != -1) {
                String itemName = String.valueOf(Class298_Sub32_Sub14.aClass477_9400.getItemDefinitions(itemId).name);
                inventory.set(slot, new Object[]{itemName, amount});
            }
        }

        for(int i = 0; i < lastInventoryTick.size(); i++) {
            Object[] resourceArray = lastInventoryTick.get(i);
            String lastItemName = String.valueOf(resourceArray[0]);
            int lastAmount = (int) resourceArray[1];

            Object[] invent = inventory.get(i);
            String newInventoryItemName = (String) invent[0];
            int newInventoryAmount = (int) invent[1];

            if (!newInventoryItemName.equals(lastItemName) || newInventoryAmount != lastAmount) {
                int index = getIndexOfItemName(newInventoryItemName, totalHarvestedResources);
                if(index == -1) {
                    totalHarvestedResources.add(new Object[]{newInventoryItemName, newInventoryAmount});
                } else {
                    int previousAmount = Integer.parseInt(String.valueOf(totalHarvestedResources.get(index)[1]));
                    int newAmount = previousAmount + newInventoryAmount;
                    totalHarvestedResources.set(index, new String[]{newInventoryItemName, String.valueOf(newAmount)});
                }
                lastInventoryTick.set(i, new Object[]{newInventoryItemName, newInventoryAmount});
                if(!newInventoryItemName.equals("NONE"))
                    getBotManager().setHarvestedResources(totalHarvestedResourceByItemName(newInventoryItemName) + " ("+newInventoryItemName+")");
            }
        }
    }

    private int totalHarvestedResourceByItemName(String itemName) {
        int totalResources = 0;
        for(Object[] resources : totalHarvestedResources) {
            String name = (String) resources[0];
            if(itemName.equals(name)) {
                totalResources = Integer.parseInt(String.valueOf(resources[1]));
                System.out.println("Total Harvested Resource: "+name+", "+totalResources);
            }
        }
        return totalResources;
    }

    private int getIndexOfItemName(String itemName, ArrayList<Object[]> arrayList) {
        for(int i = 0; i < arrayList.size(); i++) {
            Object[] array = arrayList.get(i);
            if(array[0].equals(itemName))
                return i;
        }
        return -1;
    }

    public void hideBotUI() {
        Loader.botPanel.repaint();
        Loader.botPanel.setVisible(false);
    }

    public void showBotUI() {
        Loader.botPanel.setVisible(true);
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

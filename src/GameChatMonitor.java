import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameChatMonitor {

    private final BotManager botManager;
    private ScheduledExecutorService threadPool = null;

    public GameChatMonitor(BotManager botManager) {
        this.botManager = botManager;
    }

    private final String[][] triviaAnswers = new String[][]{
            {"What is the maximum amount of cash a player can hold?", "2147m"},
            {"What is the lowest number you can roll with a Dicebag?", "1"},
            {"What Crafting level do you need to craft an Amulet of Fury?", "90"},
            {"What spellbook has Ice Barrage on it?", "ancients"},
            {"Which Wilderness Demi-boss has two phases?", "vet'ion"},
            {"Which Donator Rank allows access to ::ddz?", "diamond"},
            {"What is the new best-in-slot magic weapon on Onyx?", "cataclysm"},
            {"What is the name of the new World Boss on Onyx?", "callus"},
            {"What Hunter creature can you catch at level 77?", "grenwall"},
            {"What is the name of the Bandos GWD General?", "graardor"},
            {"How many Loyalty Tokens does a Thok's Sword cost?", "1000"},
            {"What fancy monster is presented on our client background?", "dragon"},
            {"Who helped Zio construct the VIP Zone?", "monk"},
            {"What Agility level do you need to access the Advanced Gnome Course?", "85"},
            {"What is the top Donator Rank on Onyx?", "zenyte"},
            {"What is the name of the final boss in the Theatre of Blood?", "lady verzik"},
            {"What is the name of Nick and Zio's unique PvM events?", "the horde"},
            {"What are the name of a Loadout you can customize via the rightmost icon in the Quest tab?", "preset"},
            {"How many trivia questions must be answered for a regular Completionist Cape?", "3"},
            {"What is the name of the PvM event with the most variety of bosses?", "boss bonanza"},
            {"How many skills do we have on Onyx?", "25"},
            {"What drops the Primordial crystal?", "cerberus"},
            {"What boss drops Torva, Pernix, and Virtus equipment?", "nex"},
            {"How many pieces of armor are there in the Justiciar set?", "3"},
            {"What boss drops the Trident of the Seas?", "kraken"},
            {"What is the name of the Spider boss within the Wilderness?", "venenatis"},
            {"What stall can you pickpocket at ::home if you have 60 Thieving?", "ruby"},
            {"What is Zulrah's signature ranged weapon drop?", "blowpipe"},
            {"Which Prayer level unlocks Soul Split?", "92"},
            {"How many waves are there in the Inferno?", "69"},
            {"What Summoning level do you need to summon a Steel Titan?", "99"},
            {"What is the name of the blue dragon boss?", "vorkath"},
            {"How many vote tickets can you receive every 12H?", "10"},
            {"What minigame have teams that consist of Zamorak and Saradomin?", "castle wars"},
            {"What monster drops Dragon Claws?", "tormented demon"},
            {"What command allows me to teleport to the previous location I was in?", "prev"},
            {"How much xp do you need to achieve 120 in a skill (to the nearest million)?", "104m"},
            {"Which Donator Rank allows access to ::dz?", "sapphire"},
            {"What Summoning level do you need to summon a Pack Yak?", "96"},
            {"How many spins do you get per day if you are a Diamond Donator?", "5"},
            {"How many skills do we have on Onyx?", "25"},
            {"What is the name of the altar that allows you to switch to any spellbook?", "altar of the occult"},
            {"What minigame allows you to obtain Swift Gloves?", "dominion tower"},
            {"When receiving a gravestome upon death, which direction does it spawn from the bank at ::home?", "south"},
            {"How many Quest bossfights do we offer on Onyx?", "6"},
            {"How many Donator Ranks do we have on Onyx?", "6"},
            {"How many different color flowers are available from Mithril Seeds?", "9"},
            {"What Summoning level do you need to summon a Lava Titan?", "83"},
            {"What Herblore level do you need to create a Prayer Renewal?", "94"},
            {"Which Prayer level unlocks Turmoil?", "95"},
            {"What revision of RSPS does Onyx fall under?", "718"},
            {"What is the thread number for our Guide Directory (contains links to most guides)?", "2657"},
            {"What is the rarest and most expensive partyhat color on our server?", "black"},
            {"Which boss drops the Divine Sigil?", "corporeal beast"},
            {"What is the command to get to our Gambling Zone?", "dice"},
            {"What platform can you join to stay up-to-date with Onyx news, market, media, etc?", "discord"},
            {"What fantasy monster is presented on our client background?", "dragon"},
            {"Who is the Owner and Developer of Onyx?", "dragonkk"},
            {"What is the strongest version of the Completionist Cape?", "elite"},
            {"What is the name of the wilderness boss that randomly spawns throughout the day?", "galvek"},
            {"Which Dungeoneering setting highlights the fastest route to the boss via the minimap, at a minor XP loss?", "guide mode"},
            {"What Dungeoneering item was the original Twisted Bow, in terms of enemy Magic scaling?", "hexhunter bow"},
            {"What armor type do you have a chance of acquiring from Minigame Boxes?", "hybrid"},
            {"What is the best-in-slot ring on Onyx?", "infinity ring"},
            {"What is the highest level Slayer Master available on Onyx?", "kuradel"},
            {"What Dungeoneering floor type is the highest Experience per hour?", "large"},
            {"What melee weapon on Onyx is the fastest hitting?", "llru"},
            {"Who is the designer of the Callus boss?", "nick"},
            {"What is the name of the website for our forums?", "onyxftw"},
            {"What Dungeoneering setting, not recommended for beginners, disables the team key pouch?", "pre share"},
            {"What is Zio's favorite color partyhat?", "purple"},
            {"What boss drops the Royal Crossbow (give the abbreviation)?", "qbd"},
            {"Which ring reduces the cost of any weapon's special attack by 10%?", "ring of vigour"},
            {"What is the name of the NPC that runs the vote point shop?", "robin"},
            {"What stall can you pickpocket at ::home if you have 60 Thieving?", "ruby"},
            {"What is the best way to search items dropped by NPC's?", "searchitem"},
            {"Who is the new Developer of Onyx?", "simplex"},
            {"What is the name of the Dark Beast boss in the Theatre of Blood?", "sotetseg"},
            {"What is the name of the upgraded version of Justiciar armor?", "templar"},
            {"What command opens up the Onyx Teleport Interface?", "tp"},
            {"What is the most powerful ranged weapon versus monsters with a high magic level?", "twisted bow"},
            {"Who is the Co-Owner of Onyx?", "zio"},
            {"How many Dungeoneering Tokens do you need to buy a Chaotic?", "1000000"}
};

    private String previousMessage = "";

    public void start() {
        //Initially configures the script before threadPool startup
        setup();

        //Checks if bot can start, returns error if not
        if(!canStart())
            return;
        final int[] runtime = {0};
        threadPool.scheduleAtFixedRate(() -> {
            try {
                runtime[0]++;
                switch (getAction()) {
                    case "MONITOR": {
                        String lastChatMessage = getBotManager().getPacketHandler().getLastChatMessage();
                        if(!previousMessage.equals(lastChatMessage)) {
                            previousMessage = lastChatMessage;
                            System.out.println("MONITORING STATE: " + getBotManager().getPacketHandler().getLastChatMessage());
                            TextEditor.writeToFile("logchat", lastChatMessage);
                        }
                        break;
                    }
                    case "TRIVIA": { //::Answer
                        String lastChatMessage = getBotManager().getPacketHandler().getLastChatMessage();
                        if(!previousMessage.equals(lastChatMessage)) {
                            previousMessage = lastChatMessage;
                            for(String[] triviaAnswer : triviaAnswers) {
                                if(lastChatMessage.contains(triviaAnswer[0])) {
                                    int randomDelay = new SecureRandom().nextInt(2000);
                                  //  getBotManager().getPacketHandler().sendOnyxCommand("answer " + triviaAnswer[1]);
                                    for(int loop = 2; loop < 3; loop++) {
                                        Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(
                                                () -> getBotManager().getPacketHandler().sendOnyxCommand("answer " + triviaAnswer[1]),
                                                randomDelay + (loop * 1500), TimeUnit.MILLISECONDS);
                                    }
                                }
                            }
                            lastChatMessage = lastChatMessage.replace("<img=6><col=ff8c38><shad=000>News: [Trivia] ::answer <col=9999ff>", "")
                                    .replace("<col=ff8c38> to win rewards!</shad></col>", "");
                            TextEditor.writeToFile("trivia", lastChatMessage);
                        }
                        break;
                    }
                    case "TRIVIA_ANSWER": {
                        String lastChatMessage = getBotManager().getPacketHandler().getLastChatMessage();
                        if(!previousMessage.equals(lastChatMessage)) {
                            previousMessage = lastChatMessage;
                            lastChatMessage = lastChatMessage.replace("<img=6><col=ff8c38><shad=000>News: ", "").replace("</shad></col>", "");
                            TextEditor.writeToFile("trivia", lastChatMessage);
                        }
                        break;
                    }
              /*      case "REACTION": { //::Type
                        String lastChatMessage = getBotManager().getPacketHandler().getLastChatMessage();
                        if(!previousMessage.equals(lastChatMessage)) {
                            previousMessage = lastChatMessage;
                            if (lastChatMessage.contains("::type") && lastChatMessage.contains(" to win")) {
                                String typeString = lastChatMessage.substring(lastChatMessage.indexOf("::type") + 7, lastChatMessage.indexOf(" to win"));
                                typeString = typeString.replaceAll("<col=9999ff>", "").replaceAll("<col=ff8c38>", "");
                                System.out.println("TypeString: " + typeString);
                                String finalTypeString = typeString;
                                getBotManager().getPacketHandler().sendOnyxCommand("type " + finalTypeString);
                                int randomDelay = new SecureRandom().nextInt(2000);
                                for(int loop = 1; loop < 4; loop++) {
                                    Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(
                                            () ->  getBotManager().getPacketHandler().sendOnyxCommand("type " + finalTypeString),
                                            randomDelay + (loop * 1500), TimeUnit.MILLISECONDS);
                                }
                               // Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(() -> getBotManager().getPacketHandler().sendOnyxCommand("type " + finalTypeString), new SecureRandom().nextInt(700) + 1300, TimeUnit.MILLISECONDS);
                            }
                            System.out.println("LastChatMessage: " + lastChatMessage);
                        }
                        break;
                    }*/
                    case "POP_QUIZ": { //::Type
                        String lastChatMessage = getBotManager().getPacketHandler().getLastChatMessage();
                        if(!previousMessage.equals(lastChatMessage)) {
                            previousMessage = lastChatMessage; //Broadcast: [Pop quiz] What is 2.0 multiplied by 6.0 minus 2.0? ::quiz #
                            if (lastChatMessage.contains("::quiz")) {
                                ArrayList<String[]> condition = getCondition(lastChatMessage);
                                double[] numbers = getNumbers(lastChatMessage, condition);
                                System.out.println(Arrays.toString(condition.get(1)));
                                System.out.println(Arrays.toString(numbers));
                                if(condition.size() > 1 && numbers != null) {
                                    int finalValue = getCalculatedFormula(condition, numbers);
                                    System.out.println("Final Value Test: "+finalValue);
                                    int randomDelay = new SecureRandom().nextInt(2000);
                                    for(int loop = 2; loop < 3; loop++) {
                                        Executors.newSingleThreadScheduledExecutor(new BotThreadFactory()).schedule(
                                                () ->  getBotManager().getPacketHandler().sendOnyxCommand("quiz " + finalValue),
                                                randomDelay + (loop * 1500), TimeUnit.MILLISECONDS);
                                    }
                                }
                            }
                            TextEditor.writeToFile("popquiz", lastChatMessage);
                            System.out.println("LastChatMessage2: " + lastChatMessage);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 100, TimeUnit.MILLISECONDS);
    }

    private ArrayList<String[]> getCondition(String lastChatMessage) {
        //Broadcast: [Pop quiz] What is 2.0 multiplied by 6.0 minus 2.0? ::quiz #
        String[][] mathAsString = new String[][]{
                {"plus", "+"}, {"add", "+"},
                {"minus", "-"}, {"subtract", "-"}, {"subtracted by", "-"},
                {"divide by", "/"}, {"divided by", "/"},
                {"multiplied by", "*"}, {"times by", "*"} , {"multiply by", "*"}};
        ArrayList<String[]> finalCondition = new ArrayList<>();
        ArrayList<String[]> condition = Arrays.stream(mathAsString).filter(x -> lastChatMessage.contains(x[0])).map(x -> new String[]{x[0], x[1],
                String.valueOf(lastChatMessage.indexOf(x[0]))}).collect(Collectors.toCollection(ArrayList::new));
        if(condition.size() > 1 && (Integer.parseInt(condition.get(0)[2]) < Integer.parseInt(condition.get(1)[2]))) {
           finalCondition.add(condition.get(0));
           finalCondition.add(condition.get(1));
        } else {
            finalCondition.add(condition.get(1));
            finalCondition.add(condition.get(0));
        }
        return finalCondition;
    }

    private double[] getNumbers(String lastChatMessage, ArrayList<String[]> condition) {
        String firstTerm = "What is ";
        String lastTerm = "? ::quiz";
        if(lastChatMessage.contains(firstTerm) && lastChatMessage.contains(lastTerm) && condition.size() > 1) {
            double firstDouble = Double.parseDouble(lastChatMessage.substring(lastChatMessage.indexOf(firstTerm) + firstTerm.length(), lastChatMessage.indexOf(condition.get(0)[0])));
            double secondDouble = Double.parseDouble(lastChatMessage.substring(lastChatMessage.indexOf(condition.get(0)[0]) + condition.get(0)[0].length() + 1, lastChatMessage.indexOf(condition.get(1)[0]) - 1));
            double thirdDouble = Double.parseDouble(lastChatMessage.substring(lastChatMessage.indexOf(condition.get(1)[0]) + condition.get(1)[0].length() + 1, lastChatMessage.indexOf(lastTerm) - 1));
            return new double[]{firstDouble, secondDouble, thirdDouble};
        }
        return null;
    }

    private int getCalculatedFormula(ArrayList<String[]> condition, double[] numbers) {
        if(numbers.length > 2 && condition.size() > 1) {
            int firstValue = stringToFormula(condition.get(0)[1], (int) numbers[0], (int) numbers[1]);
            return stringToFormula(condition.get(1)[1], firstValue, (int) numbers[2]);
        }
        return -1;
    }

    private int stringToFormula(String type, int firstValue, int secondValue) {
        switch(type) {
            case "*":
                return firstValue * secondValue;
            case "/":
                return firstValue / secondValue;
            case "-":
                return firstValue - secondValue;
            case "+":
                return firstValue + secondValue;
        }
        return -1;
    }

    private void setup() {
        if(getBotManager().isDebug())
            System.out.println("Starting script: "+getClass().getName());
    }

    private boolean canStart() {
        return !isRunning();
    }

    private String getAction() {
        if(getBotManager().getPacketHandler().getLastChatMessage().contains("Broadcast: ::answer"))
            return "TRIVIA";
        else if(getBotManager().getPacketHandler().getLastChatMessage().contains("News:") && getBotManager().getPacketHandler().getLastChatMessage().contains("won trivia!"))
            return "TRIVIA_ANSWER";
        else if(getBotManager().getPacketHandler().getLastChatMessage().contains("Broadcast: [Pop quiz]"))
            return "POP_QUIZ";
        else
            return "MONITOR";
    }

    private boolean isRunning() {
        if (threadPool != null && !threadPool.isShutdown())
            return true;
        startThread();
        return false;
    }

    private void startThread() {
        threadPool = Executors.newSingleThreadScheduledExecutor(new BotThreadFactory());
        if(getBotManager().isDebug())
            System.out.println("STARTING A NEW THREAD");
    }

    public void stop() {
        if (threadPool == null)
            return;
        threadPool.shutdownNow();
        if(getBotManager().isDebug())
            System.out.println("Shutting down scripts " + threadPool.isShutdown());
    }

    public BotManager getBotManager() {
        return botManager;
    }


}

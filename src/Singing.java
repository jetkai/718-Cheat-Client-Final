import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kai on 02/05/2016.
 *
 * @ Jet Kai
 */
public class Singing {

    private final BotManager AABotManager;

    private ScheduledExecutorService threadPool = null;

    private int currentLine = 0;

    private int songId;

    private final String[] justinBieber = {"Oh Whoa", "Oh Whoa", "Oh Whoa",
            "You know you love me, I know you care",
            "Just shout whenever, and I'll be there",
            "You are my love, you are my heart",
            "And we will never, ever , ever be apart",
            "Are we an item? Girl, quit playing",
            "\"Were just friends,\" what are you saying",
            "Said \"there's another,\" and looked right in my eyes",
            "My first love broke my heart for the first time",
            "And I was like baby, baby, baby, oh",
            "Like baby, baby, baby, oh",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "Baby, baby, baby, oh",
            "Like baby, baby, baby, no",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "For you, I would have done whatever",
            "And I just can't believe we aren't together",
            "And I want to play it cool, but I'm losing you",
            "I'll buy you anything, I'll buy you diamond ring",
            "And I'm in pieces, baby fix me",
            "And just shake me till you wake me from this bad dream",
            "I'm going down, down, down, down",
            "And I just can't believe my first love won't be around",
            "And I'm like baby, baby, baby, oh",
            "Like baby, baby, baby, no",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "Baby, baby, baby, oh",
            "Like baby, baby, baby, no",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "When I was 13, I had my first love",
            "There was nobody that compared to my baby",
            "And nobody came between us who could ever come above",
            "She had me going crazy, oh I was starstruck",
            "She woke me up daily, don't need no Starbucks",
            "She made my heart pound",
            "I skip a beat when I see her in the street",
            "And at school on the playground",
            "But I really want to see her on the weekend",
            "She knows she got me dazing 'cause she was so amazing",
            "And now my heart is breaking but I just keep on saying",
            "Baby, baby, baby, oh",
            "Like baby, baby, baby, no",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "Baby, baby, baby, oh",
            "Like baby, baby, baby, no",
            "Like baby, baby, baby, oh",
            "I thought you'd always be mine, mine",
            "I'm all gone",
            "(Yeah, yeah, yeah)",
            "(Yeah, yeah, yeah)",
            "Now I'm all gone",
            "(Yeah, yeah, yeah)",
            "(Yeah, yeah, yeah)",
            "Now I'm all gone",
            "Now I'm all gone, gone, gone, gone",
            "I'm gone"};

    private final String[] adeleHello = new String[]{"Hello, it's me, I was wondering",
            "If after all these years you'd like to meet to go over everything",
            "They say that time's supposed to heal, yeah",
            "But I ain't done much healing",
            "Hello, can you hear me?",
            "I'm in California dreaming about who we used to be",
            "When we were younger and free",
            "I've forgotten how it felt before the world fell at our feet",
            "There's such a difference between us",
            "And a million miles",
            "Hello from the other side",
            "I must've called a thousand times",
            "To tell you I'm sorry, for everything that I've done",
            "But when I call you never seem to be home",
            "Hello from the outside",
            "At least I can say that I've tried",
            "To tell you I'm sorry, for breaking your heart",
            "But it don't matter, it clearly doesn't tear you apart anymore",
            "Hello, how are you?",
            "It's so typical of me to talk about myself, I'm sorry",
            "I hope that you're well",
            "Did you ever make it out of that town where nothing ever happened?",
            "It's no secret",
            "That the both of us are running out of time",
            "So hello from the other side",
            "I must've called a thousand times",
            "To tell you I'm sorry, for everything that I've done",
            "But when I call you never seem to be home",
            "Hello from the outside",
            "At least I can say that I've tried ",
            "To tell you I'm sorry, for breaking your heart",
            "But it don't matter, it clearly doesn't tear you apart anymore",
            "Ooh, anymore",
            "Ooh, anymore",
            "Ooh, anymore",
            "Anymore...",
            "Hello from the other side",
            "I must've called a thousand times",
            "To tell you I'm sorry, for everything that I've done",
            "But when I call you never seem to be home",
            "Hello from the outside",
            "At least I can say that I've tried",
            "To tell you I'm sorry, for breaking your heart",
            "But it don't matter, it clearly doesn't tear you apart anymore"};

    private final String[] snoopDogg = new String[]{"La da da da dah"};

    public Singing(BotManager AABotManager) {
        this.AABotManager = AABotManager;
    }

    final int[] TalkTime = {0};
    public void start() {
        if (isRunning())
            return;
        System.out.println("Starting Bot");
        currentUser = 0;
        currentLine = 0;
        final boolean[] isInClan = {false};
        if(getAABotManager().getPacketHandler().isLoggedIn()) {
            getAABotManager().getPacketHandler().toggleClanChat();
        }
        threadPool.scheduleAtFixedRate(() -> {
            if(getAABotManager().getPacketHandler().isLoggedIn()) {
                if(!isInClan[0]) {
                    getAABotManager().getPacketHandler().toggleClanChat();
                    isInClan[0] = true;
                }
                //  if(TalkTime[0] == 3) {
                singSong();
                // TalkTime[0] = 0;
                //  }
                  TalkTime[0]++;
            }
        }, 0L, 3100, TimeUnit.MILLISECONDS);
    }

    private int currentUser = 0;
    private void singSong() {
        try {

            String users = "Dragonkk, Simplex";
            String[] userArray = new String[]{users};

            //getAABotManager().getPacketHandler().get
            //Random r = new Random();
            //  int low = 0;
            // int high = 2;
            //  int songId = r.nextInt(3);
            // for(String username : userArray) {

            String username = userArray[currentUser];


            if (currentLine < justinBieber.length && songId == 0) {
                  getAABotManager().getPacketHandler().sendPublicChatMessage(justinBieber[currentLine]);
                // getAABotManager().getPacketHandler().sendPrivateMessage(username, justinBieber[currentLine]);
                //  currentLine++;
            } else if (currentLine < adeleHello.length && songId == 2) {
                getAABotManager().getPacketHandler().sendPublicChatMessage(adeleHello[currentLine]);
              //  getAABotManager().getPacketHandler().sendPrivateMessage(username, adeleHello[currentLine]);
                //  currentLine++;
            } else if (currentLine < snoopDogg.length && songId == 1) {
                 getAABotManager().getPacketHandler().sendPublicChatMessage(snoopDogg[currentLine]);
               // getAABotManager().getPacketHandler().sendPrivateMessage(username, snoopDogg[currentLine]);
                // currentLine++;
            } else
                currentLine = 0;
            // }
            if (currentUser != (userArray.length - 1)) {
                currentUser++;
            } else {
                currentLine++;
                currentUser = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getSongId() {
        return songId;
    }

    public BotManager getAABotManager() {
        return AABotManager;
    }


}

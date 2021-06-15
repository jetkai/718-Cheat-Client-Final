public class Skills {

    public static final int ATTACK = 0;
    public static final int DEFENCE = 1;
    public static final int STRENGTH = 2;
    public static final int HITPOINTS = 3;
    public static final int RANGED = 4;
    public static final int PRAYER = 5;
    public static final int MAGIC = 6;
    public static final int COOKING = 7;
    public static final int WOODCUTTING = 8;
    public static final int FLETCHING = 9;
    public static final int FISHING = 10;
    public static final int FIREMAKING = 11;
    public static final int CRAFTING = 12;
    public static final int SMITHING = 13;
    public static final int MINING = 14;
    public static final int HERBLORE = 15;
    public static final int AGILITY = 16;
    public static final int THIEVING = 17;
    public static final int SLAYER = 18;
    public static final int FARMING = 19;
    public static final int RUNECRAFTING = 20;
    public static final int HUNTER = 21;
    public static final int CONSTRUCTION = 22;
    public static final int SUMMONING = 23;
    public static final int DUNGEONEERING = 24;

    private static final String[] SKILLS =
            new String[]{"ATTACK", "DEFENCE", "STRENGTH", "HITPOINTS",
                        "RANGED", "PRAYER", "MAGIC", "COOKING",
                        "WOODCUTTING", "FLETCHING", "FISHING", "FIREMAKING",
                        "CRAFTING", "SMITHING", "MINING", "HERBLORE",
                        "AGILITY", "THIEVING", "SLAYER", "FARMING",
                        "RUNECRAFTING", "HUNTER", "CONTRUCTION", "SUMMONING",
                        "DUNGEONEERING"};

    public static int getLevelBySkillId(int skillId) {
        return skillId > -1 && skillId < 25 ? client.anIntArray8924[skillId] : 0;
    }

    public static String getLevelNameBySkillId(int skillId) {
        return skillId > -1 && skillId < 25 ? SKILLS[skillId] : "UNKNOWN SKILL";
    }


}

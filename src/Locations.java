public class Locations {
    
    private final int coordX;
    private final int coordY;
    private final int coordZ;

    public Locations() {
        Player player = Class287.myPlayer;
        Class235 class235 = player.method4337();
        Map map = client.aClass283_8716.method2628(681479919);
        coordX = ((int) class235.aClass217_2599.x >> 9) + map.gameSceneBaseX * -1760580017;
        coordY = ((map.gameSceneBaseY * 283514611) + ((int) class235.aClass217_2599.y >> 9));
        coordZ = Class287.myPlayer.plane;
    }

    public int[] getPlayerLocation() {
        return new int[]{coordX, coordY};
    }

    public boolean isInBrimHeavenMine() { //Usages: Gold Miner
        return (coordX >= 2436 && coordX <= 2751 && coordY >= 3143 && coordY <= 3156); //BrimHeaven Mine
    }

    public boolean isInVarrockMine() { //Usages: Gold Miner
        return (coordX >= 3277 && coordX <= 3294 && coordY >= 3355 && coordY <= 3373); //Varrock Mine
    }

    public boolean isInLovakenjMine() {
        return (coordX >= 1420 && coordX <= 1452  && coordY >= 3805 && coordY <= 3861); //LovakenjMine
    }

    public boolean isInPrayerZone() {
        return (coordX >= 3105 && coordX <= 3118  && coordY >= 3465 && coordY <= 3471); //::Prayer
    }

    public boolean isInSawMill() {
        return (coordX >= 3295 && coordX <= 3319  && coordY >= 3484 && coordY <= 3496); //::Prayer
    }

    public boolean isInSkillingBank() {
        return (coordX >= 3089 && coordX <= 3098  && coordY >= 3465 && coordY <= 3471); //::Skilling
    }

    public boolean isInCastleWarsBank() { //Usages: Gold Miner
        return (coordX >= 2438 && coordX <= 2445 && coordY >= 3082 && coordY <= 3097); //Castle Wars Banking Area
    }

    public boolean isInShiloVillage() { //Usages: Gem Miner
        return (coordX >= 2817 && coordX <= 2866 && coordY >= 2947 && coordY <= 3006); //Shilo Village
    }

    public boolean isAtGemRocks() { //Usages: Gem Miner
        return (coordX >= 2817 && coordX <= 2831 && coordY >= 2989 && coordY <= 3006); //Gem Rocks @ Shilo
       // return (coordX >= 2817 && coordX <= 2828 && coordY >= 2995 && coordY <= 3006); //Gem Rocks @ Shilo
    }

    public boolean isInCraftingGuild() { //Usages: Gold Miner
        return (coordX >= 2928 && coordX <= 2944 && coordY >= 3275 && coordY <= 3292); //Crafting Guild
    }

    public boolean isInTaiBwoWanniMahog() {
        return (coordX >= 2817 && coordX <= 2829 && coordY >= 3076 && coordY <= 3090);
    }

    public boolean isOutsideTaiBwoWanniMahog() {
        return (coordX >= 2814 && coordX <= 2816 && coordY >= 3078 && coordY <= 3088);
    }

    public boolean isInWoodCuttingGuildHighTierTrees() {
        return (coordX >= 1564 && coordX <= 1599 && coordY >= 3473 && coordY <= 3593);
    }

    public boolean isInRedWoodTree() {
        return (coordX >= 1566 && coordX <= 1575 && coordY >= 3478 && coordY <= 3497);
    }

    public boolean isAtFarmingGuildMahogany() {
        return (coordX >= 1224 && coordX <= 1264 && coordY >= 3755 && coordY <= 3781);
    }

    public boolean isAtDonatorZone() {
        return (coordX >= 3336 && coordX <= 3385 && coordY >= 5191 && coordY <= 5240);
    }

    public boolean isAtHomeStalls() {
        return (coordX >= 3096 && coordX <= 3119 && coordY >= 3489 && coordY <= 3512); //Thiev Stall & Spawn Chunk At Home
    }

    public boolean isAtSeersVillage() {
        return (coordX >= 2713 && coordX <= 2741 && coordY >= 3447 && coordY <= 3507);
    }

    public boolean isAtSeersVillagePresetBankArea() {
        return (coordX >= 2717 && coordX <= 2735 && coordY >= 3491 && coordY <= 3502);
    }

    public boolean isAtCamelotStalls() {
        return (coordX >= 2744 && coordX <= 2750 && coordY >= 3487 && coordY <= 3518);
    }

    public boolean isAtWildernessAgilityCourse() {
        return (coordX >= 2988 && coordX <= 3009 && coordY >= 3931 && coordY <= 3966);
    }

    public boolean isAtGnomeAgilityCourse() {
        return (coordX >= 2467 && coordX <= 2492 && coordY >= 3412 && coordY <= 3442);
    }

    public boolean isAtGnomeTreePart1() {
        return (coordX >= 2471 && coordX <= 2476 && coordY >= 3422 && coordY <= 3424);
    }

    public boolean isAtPrayerZoneToRestoreHealth() {
        return (coordX >= 3105 && coordX <= 3118 && coordY >= 3463 && coordY <= 3482);
    }

    public boolean isAtDraynorMarket() {
        return (coordX >= 3069 && coordX <= 3104 && coordY >= 3238 && coordY <= 3263);
    }

    public boolean isAtArdougneMarket() {
        return (coordX >= 2643 && coordX <= 2680 && coordY >= 3288 && coordY <= 3325);
    }

    public boolean isAtEtceteria() {
        return (coordX >= 2594 && coordX <= 2620 && coordY >= 3883 && coordY <= 3901);
    }

    public boolean isAtKeldagrimBank() {
        return (coordX >= 2831 && coordX <= 2852 && coordY >= 10201 && coordY <= 10219);
    }

    public boolean nearestObjectWithinDistance(int otherX, int otherY) { //Usages: Gold Miner, Gem Miner
        int deltaX = otherX - coordX, deltaY = otherY - coordY;
        return deltaX <= 2 && deltaX >= -1 && deltaY <= 2 && deltaY >= -1; //Adjusted for Gold Ore
    }

    public boolean nearestObjectWithinDistance(int otherX, int otherY, int pos, int off) { //Usages: IronMiner
        int deltaX = otherX - coordX, deltaY = otherY - coordY;
        return deltaX <= pos && deltaX >= off && deltaY <= pos && deltaY >= off;
    }

    public boolean nearestObjectWithinDistance(int[] coords) {
        int otherX = coords[0];
        int otherY = coords[1];
        int deltaX = otherX - coordX, deltaY = otherY - coordY;
        return deltaX <= 2 && deltaX >= -1 && deltaY <= 2 && deltaY >= -1;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public int getCoordZ() {
        return coordZ;
    }

}

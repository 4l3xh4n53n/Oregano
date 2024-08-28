package Commands.Administrative;

import Main.Oregano;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.TreeMap;

public class Warn extends AdministrativeCommand{


    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getPurpose() {
        return null;
    }

    @Override
    public Permission getBuiltInPermission() {
        return null;
    }

    public Warn(){
        JDA jda  = Oregano.getJDA();
        HashMap<String, TreeMap<Integer, String>> memberMap;
        for (Guild guild : jda.getGuilds()){
            memberMap = new HashMap<>();
            for (Member member : guild.getMembers()){
                memberMap.put(member.getId(), new TreeMap<>());
            }
            warns.put(guild.getId(), memberMap);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Warn.class);

    //                          guildID         memberID    warnID    reason
    private static final HashMap<String, HashMap<String, TreeMap<Integer, String>>> warns = new HashMap<>();

    protected static void addGuild(String guildID){ // todo call on guild join event
        warns.computeIfAbsent(guildID, k -> new HashMap<>());
    }

    protected static void addMember(String guildID, String memberID){ // todo call on member join event
        warns.get(guildID).computeIfAbsent(memberID, k -> new TreeMap<>());
    }

    protected static void addWarn(String guildID, String memberID, String reason){
        int warnCount = warns.get(guildID).get(memberID).size();
        warns.get(guildID).get(memberID).put(warnCount + 1, reason);
    }

    protected static void removeWarn(String guildID, String memberID, int warnID){
        warns.get(guildID).get(memberID).remove(warnID);
    }

    protected static void clearWarns(String guildID, String memberID){
        warns.get(guildID).get(memberID).clear();
    }

    protected static int getWarns(String guildID, String memberID){
        return warns.get(guildID).get(memberID).size();
    }

    // TODO database



    private static void backupWarns() {

    }

    @Override
    public String onCommand(MessageReceivedEvent e, Message message, Guild guild, String guildID, String[] args) {
        AdministrativeChecksAndData c = checks(e, message, guild, guildID, "mute", args, Permission.MODERATE_MEMBERS, 1, true);
        if (!c.checksSuccessful()) return c.checksMessage();

        // increment warn count

        // store warn in list of warns with reason and ID

        // figure out a way to back up warns to database.

        return null;
    }
}

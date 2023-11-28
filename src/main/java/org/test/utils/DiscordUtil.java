package org.test.utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DiscordUtil {
    public static Role getRoleById(String roleId, Guild guild) {
        if (guild != null && roleId != null && !roleId.isEmpty()) {
            return guild.getRoleById(roleId);
        } else {
            return null;
        }
    }

    public static Member getMemberById(String memberId, Guild guild) {
        if (guild != null && memberId != null && !memberId.isEmpty()) {
            return guild.retrieveMemberById(memberId).complete();
        } else {
            return null;
        }
    }

    public static void addRolToMember(Long roleID, Long memberID, Guild guild) throws Exception
    {
        Role role = getRoleById(roleID.toString(), guild);
        Member member = getMemberById(memberID.toString(), guild);

        if (role != null && member != null) {
            guild.addRoleToMember(member, role).queue();
        } else {
            throw new Exception();
        }
    }
}

package Commands.Administrative;

import net.dv8tion.jda.api.entities.Member;

public record AdministrativeChecksAndData (
        boolean checksSuccessful,
        String checksMessage,
        Member mentioned
) {
}

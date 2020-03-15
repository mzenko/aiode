package net.robinfriedli.botify.discord.property.properties;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.robinfriedli.botify.Botify;
import net.robinfriedli.botify.concurrent.ExecutionContext;
import net.robinfriedli.botify.discord.MessageService;
import net.robinfriedli.botify.discord.property.AbstractGuildProperty;
import net.robinfriedli.botify.entities.GuildSpecification;
import net.robinfriedli.botify.entities.xml.GuildPropertyContribution;
import net.robinfriedli.botify.exceptions.InvalidPropertyValueException;

/**
 * Property that customised the bot name that may be used as command prefix.
 */
public class BotNameProperty extends AbstractGuildProperty {

    public BotNameProperty(GuildPropertyContribution contribution) {
        super(contribution);
    }

    @Override
    public void doValidate(Object state) {
        String input = (String) state;
        if (input.length() < 1 || input.length() > 32) {
            throw new InvalidPropertyValueException("Length should be 1 - 32 characters");
        }
    }

    @Override
    public Object process(String input) {
        return input;
    }

    @Override
    public void setValue(String value, GuildSpecification guildSpecification) {
        Botify botify = Botify.get();
        Guild guild = guildSpecification.getGuild(botify.getShardManager());
        if (guild != null) {
            try {
                guild.getSelfMember().modifyNickname(value).queue();
            } catch (InsufficientPermissionException e) {
                ExecutionContext executionContext = ExecutionContext.Current.get();
                if (executionContext != null) {
                    MessageService messageService = botify.getMessageService();
                    TextChannel channel = executionContext.getChannel();
                    messageService.sendError("I do not have permission to change my nickname, but you can still call me " + value, channel);
                }
            }
        }
        guildSpecification.setBotName(value);
    }

    @Override
    public Object extractPersistedValue(GuildSpecification guildSpecification) {
        return guildSpecification.getBotName();
    }
}

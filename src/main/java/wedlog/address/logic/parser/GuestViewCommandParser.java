package wedlog.address.logic.parser;

import static wedlog.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import wedlog.address.commons.core.index.Index;
import wedlog.address.logic.commands.GuestViewCommand;
import wedlog.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input specifically for GuestView commands.
 */
public class GuestViewCommandParser implements Parser<GuestViewCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ViewCommand
     * and returns a ViewCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public GuestViewCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new GuestViewCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GuestViewCommand.MESSAGE_USAGE), pe);
        }
    }
}
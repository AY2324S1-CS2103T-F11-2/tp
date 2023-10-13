package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GuestDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Parses user input specifically for GuestDelete commands.
 */
public class GuestDeleteCommandParser implements Parser<GuestDeleteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public GuestDeleteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new GuestDeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GuestDeleteCommand.MESSAGE_USAGE), pe);
        }
    }
}

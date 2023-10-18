package wedlog.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static wedlog.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static wedlog.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import wedlog.address.logic.commands.GuestDeleteCommand;
import wedlog.address.logic.parser.exceptions.ParseException;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the GuestDeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the GuestDeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class GuestDeleteCommandParserTest {

    private GuestDeleteCommandParser parser = new GuestDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsGuestDeleteCommand() throws ParseException {
        assertTrue(parser.parse("1") instanceof GuestDeleteCommand);

        // code below invalid due to un-evolved GuestDeleteCommand (equals method)
        // assertParseSuccess(parser, "1", new GuestDeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                GuestDeleteCommand.MESSAGE_USAGE));
    }
}

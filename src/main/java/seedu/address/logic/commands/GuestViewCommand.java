package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class GuestViewCommand extends Command {
    public static final String COMMAND_WORD = "view";
    public GuestViewCommand(Index index) {
        // temporary empty constructor
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        return null; // temporary returns a null
    }
}
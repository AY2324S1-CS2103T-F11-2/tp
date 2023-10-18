package wedlog.address.logic.commands;

import wedlog.address.commons.core.index.Index;
import wedlog.address.logic.commands.exceptions.CommandException;
import wedlog.address.model.Model;

/**
 * Deletes a Guest from the address book.
 */
public class GuestDeleteCommand extends Command {
    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the guest identified by the index number used in the displayed guest list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_GUEST_SUCCESS = "Deleted Guest: %1$s";

    // private final Index targetIndex;

    public GuestDeleteCommand(Index targetIndex) {
        // temporary empty constructor
        // this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException("Command not created yet, wait for evolve for better testing");
    }
}

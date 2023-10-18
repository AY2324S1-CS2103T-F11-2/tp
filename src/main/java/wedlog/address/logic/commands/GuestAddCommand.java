package wedlog.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_DIETARY;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_NAME;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_RSVP;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_TAG;

import wedlog.address.commons.util.ToStringBuilder;
import wedlog.address.logic.Messages;
import wedlog.address.logic.commands.exceptions.CommandException;
import wedlog.address.logic.parser.GuestCommandParser;
import wedlog.address.model.Model;
import wedlog.address.model.person.Guest;

/**
 * Adds a Guest to WedLog.
 */
public class GuestAddCommand extends Command {
    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = GuestCommandParser.GUEST_COMMAND_WORD + " " + COMMAND_WORD
            + ": Adds a guest to the address book. "
            + "Compulsory Parameters: "
            + PREFIX_NAME + "NAME "
            + "Optional Parameters: "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + GuestCommandParser.GUEST_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_RSVP + "yes "
            + PREFIX_DIETARY + "vegetarian "
            + PREFIX_TAG + "friends";
    public static final String MESSAGE_SUCCESS = "New guest added: %1$s";
    public static final String MESSAGE_DUPLICATE_GUEST = "This guest already exists in WedLog.";

    private final Guest toAdd;

    /**
     * Creates a GuestAddCommand to add the specified {@code Guest}
     */
    public GuestAddCommand(Guest guest) {
        requireNonNull(guest);
        this.toAdd = guest;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasGuest(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_GUEST);
        }

        model.addGuest(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // checks that Object is a GuestAddCommand
        if (!(other instanceof GuestAddCommand)) {
            return false;
        }

        GuestAddCommand otherGuestAddCommand = (GuestAddCommand) other;
        return toAdd.equals(otherGuestAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAddGuest", toAdd)
                .toString();
    }

}

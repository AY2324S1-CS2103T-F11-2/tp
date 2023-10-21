package wedlog.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_NAME;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static wedlog.address.logic.parser.CliSyntax.PREFIX_TAG;
import static wedlog.address.logic.parser.VendorCommandParser.VENDOR_COMMAND_WORD;
import static wedlog.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import wedlog.address.commons.core.index.Index;
import wedlog.address.commons.util.CollectionUtil;
import wedlog.address.commons.util.ToStringBuilder;
import wedlog.address.logic.Messages;
import wedlog.address.logic.commands.exceptions.CommandException;
import wedlog.address.model.Model;
import wedlog.address.model.person.Address;
import wedlog.address.model.person.Email;
import wedlog.address.model.person.Name;
import wedlog.address.model.person.Phone;
import wedlog.address.model.person.Vendor;
import wedlog.address.model.tag.Tag;

/**
 * Edits the details of an existing vendor in the address book.
 */
public class VendorEditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = VENDOR_COMMAND_WORD + " "
            + COMMAND_WORD + ": Edits the details of the vendor identified "
            + "by the index number used in the displayed vendor list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + VENDOR_COMMAND_WORD + " "
            + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_VENDOR_SUCCESS = "Edited Vendor: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_VENDOR = "This vendor already exists in the address book.";

    private final Index index;
    private final EditVendorDescriptor editVendorDescriptor;

    /**
     * @param index of the vendor in the filtered vendor list to edit
     * @param editVendorDescriptor details to edit the vendor with
     */
    public VendorEditCommand(Index index, EditVendorDescriptor editVendorDescriptor) {
        requireNonNull(index);
        requireNonNull(editVendorDescriptor);

        this.index = index;
        this.editVendorDescriptor = new EditVendorDescriptor(editVendorDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Vendor> lastShownList = model.getFilteredVendorList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_VENDOR_DISPLAYED_INDEX);
        }

        Vendor vendorToEdit = lastShownList.get(index.getZeroBased());
        Vendor editedVendor = createEditedVendor(vendorToEdit, editVendorDescriptor);

        if (!vendorToEdit.isSamePerson(editedVendor) && model.hasVendor(editedVendor)) {
            throw new CommandException(MESSAGE_DUPLICATE_VENDOR);
        }

        model.setVendor(vendorToEdit, editedVendor);
        model.updateFilteredVendorList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_VENDOR_SUCCESS, Messages.format(editedVendor)));
    }

    /**
     * Creates and returns a {@code Vendor} with the details of {@code vendorToEdit}
     * edited with {@code editVendorDescriptor}.
     */
    private static Vendor createEditedVendor(Vendor vendorToEdit, EditVendorDescriptor editVendorDescriptor) {
        assert vendorToEdit != null;

        Name updatedName = editVendorDescriptor.getName().orElse(vendorToEdit.getName());
        Phone updatedPhone = editVendorDescriptor.getPhone().orElse(vendorToEdit.getPhone().orElse(null));
        Email updatedEmail = editVendorDescriptor.getEmail().orElse(vendorToEdit.getEmail().orElse(null));
        Address updatedAddress = editVendorDescriptor.getAddress().orElse(vendorToEdit.getAddress().orElse(null));
        Set<Tag> updatedTags = editVendorDescriptor.getTags().orElse(vendorToEdit.getTags());

        return new Vendor(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VendorEditCommand)) {
            return false;
        }

        VendorEditCommand otherEditCommand = (VendorEditCommand) other;
        return index.equals(otherEditCommand.index)
                && editVendorDescriptor.equals(otherEditCommand.editVendorDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editVendorDescriptor", editVendorDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the vendor with. Each non-empty field value will replace the
     * corresponding field value of the vendor.
     */
    public static class EditVendorDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;

        public EditVendorDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditVendorDescriptor(EditVendorDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditVendorDescriptor)) {
                return false;
            }

            EditVendorDescriptor otherEditVendorDescriptor = (EditVendorDescriptor) other;
            return Objects.equals(name, otherEditVendorDescriptor.name)
                    && Objects.equals(phone, otherEditVendorDescriptor.phone)
                    && Objects.equals(email, otherEditVendorDescriptor.email)
                    && Objects.equals(address, otherEditVendorDescriptor.address)
                    && Objects.equals(tags, otherEditVendorDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }
}

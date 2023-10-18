package wedlog.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wedlog.address.logic.parser.Prefix;
import wedlog.address.model.person.Person;
import wedlog.address.model.tag.Tag;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_INVALID_VENDOR_DISPLAYED_INDEX = "The vendor index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final DisplayBuilder builder = new DisplayBuilder(person.getName().fullName);
        builder.add("Phone", person.getPhone())
                .add("Email", person.getEmail())
                .add("Address", person.getAddress())
                .addTags(person.getTags());

        return builder.toString();
    }

    private static class DisplayBuilder {
        private static final String FIELD_SEPARATOR = ", ";
        private static final String FIELD_NAME_VALUE_SEPARATOR = ":";
        private final StringBuilder stringBuilder = new StringBuilder();

        DisplayBuilder(String objectName) {
            stringBuilder.append(objectName);
        }

        public DisplayBuilder add(String fieldName, Object fieldValue) {
            if (fieldValue == null) {
                return this;
            }
            stringBuilder.append(FIELD_SEPARATOR)
                    .append(fieldName)
                    .append(FIELD_NAME_VALUE_SEPARATOR).append(fieldValue);
            return this;
        }

        private DisplayBuilder addTags(Set<Tag> tags) {
            stringBuilder.append(FIELD_SEPARATOR).append("Tags").append(FIELD_NAME_VALUE_SEPARATOR);
            tags.forEach(stringBuilder::append);
            return this;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }
    }
}

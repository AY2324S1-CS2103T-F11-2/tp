package wedlog.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wedlog.address.commons.exceptions.IllegalValueException;
import wedlog.address.model.person.Address;
import wedlog.address.model.person.DietaryRequirements;
import wedlog.address.model.person.Email;
import wedlog.address.model.person.Guest;
import wedlog.address.model.person.Name;
import wedlog.address.model.person.Phone;
import wedlog.address.model.person.RsvpStatus;
import wedlog.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Guest}.
 */
class JsonAdaptedGuest extends JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Guest's %s field is missing!";

    private final String rsvpStatus;
    private final String dietaryRequirements;

    /**
     * Constructs a {@code JsonAdaptedGuest} with the given guest details.
     */
    @JsonCreator
    public JsonAdaptedGuest(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
                            @JsonProperty("email") String email, @JsonProperty("address") String address,
                            @JsonProperty("rsvpStatus") String rsvpStatus,
                            @JsonProperty("dietaryRequirements") String dietaryRequirements,
                            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        super(name, phone, email, address, tags);

        this.rsvpStatus = rsvpStatus;
        this.dietaryRequirements = dietaryRequirements;
    }

    /**
     * Converts a given {@code Guest} into this class for Jackson use.
     */
    public JsonAdaptedGuest(Guest source) {
        super(source);

        rsvpStatus = source.getRsvpStatus().value;
        dietaryRequirements = source.getDietaryRequirements().value;
    }

    /**
     * Converts this Jackson-friendly adapted guest object into the model's {@code Guest} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Guest toModelType() throws IllegalValueException {
        final List<Tag> guestTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            guestTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (rsvpStatus == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    RsvpStatus.class.getSimpleName()));
        }
        if (!RsvpStatus.isValidRsvpStatus(rsvpStatus)) {
            throw new IllegalValueException(RsvpStatus.MESSAGE_CONSTRAINTS);
        }
        final RsvpStatus modelRsvpStatus = new RsvpStatus(rsvpStatus);

        if (dietaryRequirements == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    DietaryRequirements.class.getSimpleName()));
        }
        if (!DietaryRequirements.isValidDietaryRequirement(dietaryRequirements)) {
            throw new IllegalValueException(DietaryRequirements.MESSAGE_CONSTRAINTS);
        }
        final DietaryRequirements modelDietaryRequirements = new DietaryRequirements(dietaryRequirements);

        final Set<Tag> modelTags = new HashSet<>(guestTags);
        return new Guest(modelName, modelPhone, modelEmail, modelAddress, modelRsvpStatus,
                modelDietaryRequirements, modelTags);
    }

}
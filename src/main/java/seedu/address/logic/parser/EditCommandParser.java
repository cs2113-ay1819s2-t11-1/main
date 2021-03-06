package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ACTIVITY_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    private final String commandType; //to differentiate between edit and interleave

    public EditCommandParser(String commandWord) {
        this.commandType = commandWord;
    }

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_ACTIVITY_DAY, PREFIX_ACTIVITY_TIME, PREFIX_ACTIVITY, PREFIX_MODULES, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble()); //only this is needed for interleaving
        } catch (ParseException pe) {
            if (this.commandType.equals(EditCommand.EDIT_COMMAND)) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.EDIT_MESSAGE_USAGE), pe);
            } else { //if (this.commandType.equals(EditCommand.INTERLEAVE_COMMAND))
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.INTERLEAVE_MESSAGE_USAGE), pe);
            }
        }

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        if (this.commandType.equals(EditCommand.EDIT_COMMAND)) {
            if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
                editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
            }
            if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
                editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
            }
            if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
                editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
            }
            if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
                editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
            }
            parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
            if (argMultimap.getValue(PREFIX_MODULES).isPresent()) {
                editPersonDescriptor.setModuleList(ParserUtil.parseModules(argMultimap.getAllValues(PREFIX_MODULES)));
            }
            if (argMultimap.getValue(PREFIX_ACTIVITY_DAY).isPresent()
                    && argMultimap.getValue(PREFIX_ACTIVITY_TIME).isPresent()
                    && argMultimap.getValue(PREFIX_ACTIVITY).isPresent()) {
                editPersonDescriptor.setTimetable(ParserUtil.parseTimetable(
                        argMultimap.getValue(PREFIX_ACTIVITY_DAY).get(),
                        argMultimap.getValue(PREFIX_ACTIVITY_TIME).get(), argMultimap.getValue(PREFIX_ACTIVITY).get()));
            }
        }

        if (commandType.equals(EditCommand.INTERLEAVE_COMMAND)) {
            editPersonDescriptor.setInterleaved(true);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}

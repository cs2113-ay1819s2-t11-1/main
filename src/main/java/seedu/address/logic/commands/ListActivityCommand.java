package seedu.address.logic.commands;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;

import static java.util.Objects.requireNonNull;
// import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EVENTS;

/**
 * Lists all activities in a person to the user.
 */
public class ListActivityCommand extends Command {

    public static final String COMMAND_WORD = "listactivity";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists activities of a person according to a given person index. "
            + "Parameters: INDEX\n" + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Listed all activities";

    private int personIndex;

    public ListActivityCommand(int index) {
        personIndex = index;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        // model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        return new CommandResult(MESSAGE_SUCCESS, false, false, 2, personIndex);
    }
}
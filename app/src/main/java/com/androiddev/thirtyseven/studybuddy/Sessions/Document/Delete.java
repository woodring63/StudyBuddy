package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

/**
 * Created by Nathan on 4/18/2016.
 */
public class Delete extends Mutation {

    protected int toDelete; // number of characters to delete
    protected Delete subDelete; // used for when an insert splits the delete

    /**
     * Constructs a new Delete
     * @param index - the index that the mutation begins at
     * @param toDelete - number of characters to delete
     */
    public Delete(int index, int toDelete) {
        super(MUTATION_DELETE, index);
        this.toDelete = toDelete;
        subDelete = null;
    }

    @Override
    public void transform(Mutation mutation) {
        if (subDelete != null) {
            subDelete.transform(mutation);
        }
        switch (mutation.type) {
            case MUTATION_INSERT:
                if (mutation.index <= index) { // this needs to be modified
                    index += ((Insert) mutation).toInsert.length();
                }
                else if (mutation.index - index < toDelete) { // mutation splits this
                    subDelete = new Delete(mutation.index + ((Insert) mutation).toInsert.length(),
                            toDelete - (mutation.index - index));
                    toDelete = mutation.index - index;
                }
                break;
            case MUTATION_DELETE:
                Delete del = (Delete) mutation;
                if (del.subDelete != null) {
                    transform(del.subDelete);
                }

                // Several cases where this modified
                // For cases: . = not modified, | = del, - = this, + = both
                if (del.index < index) {
                    if (del.index + del.toDelete <= index) {
                        // Case: ....||||..---.. or ..||||----...
                        index -= del.toDelete;
                    }
                    else if (del.index + del.toDelete < index + toDelete) {
                        // Case: ....|||+++----..
                    }
                }
                break;
            default:
                break;
        }
    }
}

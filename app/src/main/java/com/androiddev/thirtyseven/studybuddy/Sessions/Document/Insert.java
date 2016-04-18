package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

/**
 * Created by Nathan on 4/18/2016.
 */
public class Insert extends Mutation {

    protected String toInsert; // text to insert

    /**
     * Constructs a new Insert
     * @param index - the index that the mutation begins at
     * @param toInsert - the text to insert
     */
    public Insert(int index, String toInsert) {
        super(MUTATION_INSERT, index);
        this.toInsert = toInsert;
    }

    @Override
    public void transform(Mutation mutation) {
        switch (mutation.type) {
            case MUTATION_INSERT:
                if (mutation.index < index) { // this needs to be modified
                    index += ((Insert) mutation).toInsert.length();
                }
                break;
            case MUTATION_DELETE:
                Delete del = (Delete) mutation;
                if (del.subDelete != null) {
                    transform(del.subDelete);
                }

                if (mutation.index < index) { // this needs to me modified
                    if (mutation.index - index < del.toDelete) { // mutation is split
                        index -= mutation.index - index;
                    }
                    else {
                        index -= del.toDelete;
                    }
                }
                break;
            default:
                break;
        }
    }
}

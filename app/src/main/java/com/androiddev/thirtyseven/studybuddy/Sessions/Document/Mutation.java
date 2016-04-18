package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

/**
 * Created by Nathan on 4/18/2016.
 */
public abstract class Mutation {

    public static final String TAG = "Mutation";
    public static final int MUTATION_INSERT = 1;
    public static final int MUTATION_DELETE = 2;

    protected int type; // MUTATION_INSERT or MUTATION_DELETE
    protected int index; // the index that the mutation begins at

    /**
     * Constructs a new Mutation
     * @param type - MUTATION_INSERT or MUTATION_DELETE
     * @param index - the index that the mutation begins at
     */
    public Mutation(int type, int index) {
        this.type = type;
        this.index = index;
    }

    /**
     * Changes this Mutation as necessary to account for the
     * given Mutation
     * @param mutation
     */
    public abstract void transform(Mutation mutation);
}

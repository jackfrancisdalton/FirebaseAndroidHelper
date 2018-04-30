import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Works as a facade for all of the firebase content.
 */
public class FirebaseHandler {

    private FirebaseDatabase database;
    private DatabaseReference mainDatabaseRef;
    private FirebaseStorage storage;
    private StorageReference mainStorageRef;
    private FirebaseAuth auth;

    private static FirebaseHandler instance = null;

    protected FirebaseHandler() {
        initialise();
    }

    public static FirebaseHandler getInstance() {
        if (instance == null) {
            instance = new FirebaseHandler();
        }
        return instance;
    }

    /**
     * Sets up the handlers instances, and stores the references for both storage and database roots
     */
    private void initialise() {

        //Set up database and main reference
        database = FirebaseDatabase.getInstance();
        mainDatabaseRef = database.getReferenceFromUrl(FirebaseConstants.FIREBASE_DATABASE_URL);

        //Set up storage and main reference
        storage = FirebaseStorage.getInstance();
        mainStorageRef = storage.getReferenceFromUrl(FirebaseConstants.FIREBASE_STORAGE_URL);

        auth = FirebaseAuth.getInstance();
    }

    //region Functions

    public void signOutUser() {
        auth.signOut();
    }

    //endregion

    //region Getters

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getMainDatabaseRef() {
        return mainDatabaseRef;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public StorageReference getMainStorageRef() {
        return mainStorageRef;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    //endregion
}
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class FirebaseActivity extends AppCompatActivity {

    private FirebaseHandler firebaseHandler;
    private FirebaseAuth.AuthStateListener authStateListener;
    private boolean handleAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseFirebase();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initialiseFirebase();
    }

    protected void initFirebaseApplication(Context context) {
        FirebaseApp.initializeApp(context);
    }

    //POI: sets up the firebaseHandler for any activity extending FirebaseActivity
    private void initialiseFirebase() {
        firebaseHandler = FirebaseHandler.getInstance();
        handleAuth = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (handleAuth && authStateListener != null) {
            firebaseHandler.getAuth().addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (handleAuth && authStateListener != null) {
            firebaseHandler.getAuth().removeAuthStateListener(authStateListener);
        }
    }

    //POI: this is the main method for binding all of the authstatelistners

    /**
     * Handles the AuthStateListener functionality and binds it to onStart and onCreate. This
     * method must be set up in the activities OnCreate() method
     *
     * @param enable                        if enabled changes in the auth state will result in activity changing, if false
     *                                      it will auth changes entirely
     * @param authenticatedTarget           if the AuthStateListener fires off and has a valid user it will
     *                                      fire off an intent to go to this activity
     * @param unauthenticatedFallbackTarget if the AuthStateListener fires off and has a valid
     *                                      user it will  fire off an intent to go to this activity
     */
    protected void setAuthenticationCallbacks(boolean enable, @Nullable final Class authenticatedTarget, @Nullable final Class unauthenticatedFallbackTarget) {
        handleAuth = enable;

        if (handleAuth) {
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();

                    if (mFirebaseUser != null) {
                        if (authenticatedTarget != null) {
                            Intent intent = new Intent(getApplicationContext(), authenticatedTarget);
                            startActivity(intent);
                        }
                    } else {
                        if (unauthenticatedFallbackTarget != null) {
                            Intent intent = new Intent(getApplicationContext(), unauthenticatedFallbackTarget);
                            startActivity(intent);
                        }

                        // User is signed out
                        //Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }

    protected FirebaseHandler getFirebaseHandler() {
        return firebaseHandler;
    }

    protected void signOutFirebaseUser() {
        this.firebaseHandler.signOutUser();
    }

    /**
     * Resets the currently validated users password and sends an email to their respective address
     * This method overload assumes a user is currently authenticated
     *
     * @return boolean defining if a user was logged in and the process was a success or if it was
     * aborted due to a lack of a logged in user
     */
    protected boolean sendResetPasswordEmail() {

        FirebaseUser firebaseUser = firebaseHandler.getFirebaseUser();

        if (firebaseUser != null) {
            sendResetPasswordEmail(firebaseUser.getEmail());
            return true;
        } else
            return false;
    }

    /**
     * Resets the currently validated users password and sends an email to their respective address
     * This method requires an statically defined email for the account to be reset
     *
     * @param email the statically defined email to be reset (alternative to getting the currently
     *              logged in user email)
     */
    protected void sendResetPasswordEmail(String email) {

        //Will reset and send email, returning true of success, false if fail
        firebaseHandler.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            handlePasswordReset(true);
                        } else {
                            handlePasswordReset(false);
                        }
                    }
                });
    }

    /**
     * Handles the result of a password reset, displaying an relevant Toast prompt
     * Can be overridden for the activities specific needs
     *
     * @param success defines if the password reset was a success or a failure
     */

    protected void handlePasswordReset(boolean success) {
        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG);
    }

}
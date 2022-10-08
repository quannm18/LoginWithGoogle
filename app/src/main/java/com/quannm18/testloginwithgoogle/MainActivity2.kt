package com.quannm18.testloginwithgoogle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class MainActivity2 : AppCompatActivity() {
    private val imageView: ImageView by lazy { findViewById<ImageView>(R.id.imageView) }
    private val textView: TextView by lazy { findViewById<TextView>(R.id.textView) }
    private val signInButton: SignInButton by lazy { findViewById<SignInButton>(R.id.sign_in_button) }
    private val btnSignOut: Button by lazy { findViewById<Button>(R.id.btnSignOut) }
    private val btnRevoke: Button by lazy { findViewById<Button>(R.id.btnRevoke) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {

            signIn(mGoogleSignInClient)
//            mGoogleSignInClient.silentSignIn()
//                .addOnCompleteListener(
//                    this
//                ) {
//                        task -> handleSignInResult(task)
//                }
        }

        btnSignOut.setOnClickListener {
            signOut(mGoogleSignInClient)
        }
        btnRevoke.setOnClickListener {
            revokeAccess(mGoogleSignInClient)
        }


    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.let {
            textView.setText(
                "id:" + account.id + "\ndisplayName: " + account.displayName + "\nEmail:" +
                        account.email + "\nFamilyName: " + account.familyName + "\nGivenName: " + account.givenName
                        + "\nIDToken: " + account.idToken + "\nServerAuthCode: " + account.serverAuthCode
            )
            Glide.with(imageView).load(it.photoUrl).override(200, 200).centerCrop().into(imageView)
        }

    }

    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    companion object {
        const val RC_SIGN_IN: Int = 999
        val TAG = javaClass.simpleName
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun signOut(mGoogleSignInClient: GoogleSignInClient?) {
        mGoogleSignInClient?.let { mSignOut ->
            mSignOut.signOut()
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast.makeText(applicationContext, "isSuccessful", Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (it.isCanceled) {
                        Toast.makeText(applicationContext, "isCanceled", Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }

    private fun revokeAccess(mGoogleSignInClient: GoogleSignInClient?) {
        mGoogleSignInClient?.let {
            it.revokeAccess()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "isSuccessful Revoke",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    if (it.isCanceled) {
                        Toast.makeText(applicationContext, "isCanceled", Toast.LENGTH_SHORT).show()

                    }
                })
        }

    }

}
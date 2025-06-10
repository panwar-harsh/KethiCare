package com.example.greendoc.Ecommerce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.api.exceptions.ApiException
import com.example.greendoc.MainActivity
import com.example.greendoc.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase



class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val dontHaveAccount = findViewById<TextView>(R.id.dontHaveAccount)
        val loginButton = findViewById<Button>(R.id.Login)
        val googleButton = findViewById<Button>(R.id.GoogleButton)

        dontHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString().trim()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please Fill All Details",Toast.LENGTH_SHORT).show()
            }else
            {
                loginUser(email,password)
            }
        }

        googleButton.setOnClickListener {
            signInWithGoogle()
        }

        setupGoogleSignIn()
    }

        private fun loginUser(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserToRealtimeDB(user)
                    Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToRealtimeDB(user: FirebaseUser?) {
        user?.let {
            val databaseRef = database.reference
            val userRef = databaseRef.child("users").child(user.uid)

            val userData = hashMapOf(
                "name" to (user.displayName ?: ""),
                "email" to user.email,
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "uid" to user.uid
            )

            userRef.setValue(userData)
                .addOnSuccessListener {
                    Log.d("RealtimeDB", "User saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("RealtimeDB", "Error saving user", e)
                }
        }
    }
    // Check if user already login
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null)
        {
            startActivity(Intent(this,HomeScreenActivity::class.java))
            finish()
        }
    }
}



package com.mysasse.wheretonext.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mysasse.wheretonext.HomeActivity
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.PROFILES_NODE
import com.mysasse.wheretonext.utils.hide
import com.mysasse.wheretonext.utils.show
import com.mysasse.wheretonext.utils.toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var confirmPasswordEt: EditText
    private lateinit var registerProgressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Init fire-base vars
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        nameEt = findViewById(R.id.name_et)
        emailEt = findViewById(R.id.email_et)
        passwordEt = findViewById(R.id.password_et)
        confirmPasswordEt = findViewById(R.id.confirm_password_et)

        registerProgressBar = findViewById(R.id.register_progress_bar)

        val registerButton = findViewById<Button>(R.id.register_button)
        val gotoLoginTv = findViewById<TextView>(R.id.goto_login_tv)

        gotoLoginTv.setOnClickListener { finish() }

        registerButton.setOnClickListener {

            val name = nameEt.text.toString()
            val email = emailEt.text.toString()
            val password = passwordEt.text.toString()
            val confirmPassword = confirmPasswordEt.text.toString()

            //Validate user Input
            if (!isValid(name, email, password, confirmPassword)) return@setOnClickListener

            register(name, email, password)

        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun isValid(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (TextUtils.isEmpty(name) || name.length < 3) {
            nameEt.error = "At least 3 chars name is required"
            nameEt.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(email)) {
            emailEt.error = "Email is required"
            emailEt.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(password) || password.length < 6) {
            passwordEt.error = "Password should be at least characters"
            passwordEt.requestFocus()
            return false
        }

        if (!password.equals(confirmPassword, true)) {
            passwordEt.error = "Passwords did not match"
            passwordEt.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.error = "Invalid email format"
            emailEt.requestFocus()
            return false
        }

        return true
    }

    private fun register(name: String, email: String, password: String) {

        registerProgressBar.show()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { registerTask ->
                if (registerTask.isSuccessful) {

                    //Get authenticated user id if one exists
                    val currentUser = mAuth.currentUser

                    if (currentUser != null) {

                        //Create a collection for the authenticated user and add the user's name
                        val userMap = mapOf("name" to name)

                        mDb.collection(PROFILES_NODE)
                            .document(currentUser.uid)
                            .set(userMap)
                            .addOnCompleteListener(this) { addUserMapTask ->
                                registerProgressBar.hide()

                                if (addUserMapTask.isSuccessful) {

                                    toast("Registration process was successfully")

                                } else {

                                    toast("Name registration failed, ${addUserMapTask.exception!!.localizedMessage}")
                                    Log.e(
                                        TAG,
                                        "Updating user name after registration",
                                        addUserMapTask.exception
                                    )

                                }
                            }

                        sendHome()
                    }


                } else {

                    registerProgressBar.hide()
                    toast("Registration failed, ${registerTask.exception!!.localizedMessage}")
                    Log.e(TAG, "Registration Failed", registerTask.exception)
                }
            }
    }

    companion object RegisterActivity {
        const val TAG = "RegisterActivity"
    }

    private fun sendHome() {
        Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }.also { startActivity(it) }
    }
}

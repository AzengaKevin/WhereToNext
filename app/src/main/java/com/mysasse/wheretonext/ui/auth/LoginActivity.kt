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
import com.mysasse.wheretonext.HomeActivity
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.utils.hide
import com.mysasse.wheretonext.utils.show
import com.mysasse.wheretonext.utils.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    //Global Views
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Init fire-base vars
        mAuth = FirebaseAuth.getInstance()

        //Register View In Kotlin
        emailEt = findViewById(R.id.email_et)
        passwordEt = findViewById(R.id.password_et)
        loginProgressBar = findViewById(R.id.login_progress_bar)

        val loginButton = findViewById<Button>(R.id.login_button)
        val forgotPasswordTv = findViewById<TextView>(R.id.forgot_password_tv)
        val createAccountTv = findViewById<TextView>(R.id.create_account_tv)

        loginButton.setOnClickListener {

            val email = emailEt.text.toString()
            val password: String = passwordEt.text.toString()

            if (!isValid(email, password)) return@setOnClickListener

            login(email, password)
        }

        createAccountTv.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        forgotPasswordTv.setOnClickListener {

        }

    }

    private fun login(email: String, password: String) {

        Log.d(TAG, "Email: $email, Password; $password")


        loginProgressBar.show()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { loginTask ->

                loginProgressBar.hide()

                if (loginTask.isSuccessful) {
                    toast("Login Successful")

                    sendHome()

                } else {
                    toast("Login failed, ${loginTask.exception!!.localizedMessage}")
                    Log.e(TAG, "Login Failed", loginTask.exception)
                }
            }
    }

    private fun isValid(email: String, password: String): Boolean {

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

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.error = "Invalid email format"
            emailEt.requestFocus()
            return false
        }

        return true

    }

    companion object LoginActivity {
        const val TAG = "LoginActivity"
    }

    private fun sendHome() {
        Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }.also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser

        if (currentUser != null)
            sendHome()
    }
}

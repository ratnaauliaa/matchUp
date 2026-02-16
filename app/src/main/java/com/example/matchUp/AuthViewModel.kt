package com.example.matchUp

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    fun registerUser(email: String, pass: String, name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isEmpty() && name.isEmpty() && pass.isEmpty()) {
            onError("all_empty") //
            return
        } else if (email.isEmpty()) {
            onError("email_empty") // Email saja yang kosong
            return
        } else if (pass.isEmpty()) {
            onError("pass_empty") // Password saja yang kosong
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "error")
                }
            }
    }

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isEmpty() && pass.isEmpty()) {
            onError("all_empty") // Keduanya kosong
            return
        } else if (email.isEmpty()) {
            onError("email_empty") // Email saja yang kosong
            return
        } else if (pass.isEmpty()) {
            onError("pass_empty") // Password saja yang kosong
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed. Please check your Email/Password.")
                }
            }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isEmpty()) {
            onError("Please enter your email address.")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Failed to send reset email.")
                }
            }
    }
}
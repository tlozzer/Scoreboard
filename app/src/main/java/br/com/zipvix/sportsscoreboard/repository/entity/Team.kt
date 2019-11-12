package br.com.zipvix.sportsscoreboard.repository.entity

import com.google.firebase.firestore.DocumentId

data class Team(@DocumentId val id: String? = null, val name: String = "") {
    override fun toString(): String {
        return name
    }
}
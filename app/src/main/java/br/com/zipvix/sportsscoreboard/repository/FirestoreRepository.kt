package br.com.zipvix.sportsscoreboard.repository

import br.com.zipvix.sportsscoreboard.repository.entity.Team
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    fun listTeams(onLoadFinished: (List<Team>?) -> Unit) {
        db.collection(TEAMS_COLLECTION)
            .orderBy(NAME_FIELD)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener

                val teams = querySnapshot?.toObjects(Team::class.java)
                onLoadFinished(teams?.toList())
            }
    }

    private companion object {
        const val TEAMS_COLLECTION = "teams"
        const val NAME_FIELD = "name"
    }
}
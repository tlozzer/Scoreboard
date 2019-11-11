package br.com.zipvix.sportsscoreboard.repository

import br.com.zipvix.sportsscoreboard.model.TeamListModel
import br.com.zipvix.sportsscoreboard.repository.entity.Team
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    fun listTeams(onLoadFinished: (TeamListModel) -> Unit) {
        db.collection(TEAMS_COLLECTION)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener

                val teams = querySnapshot?.toObjects(Team::class.java)
                val result = TeamListModel(mutableListOf<String>().also {
                    teams?.forEach { team -> it.add(team.toString()) }
                })
                onLoadFinished(result)
            }
    }

    private companion object {
        const val TEAMS_COLLECTION = "teams"
    }
}
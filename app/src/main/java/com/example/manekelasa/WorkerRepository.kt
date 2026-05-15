package com.example.manekelasa

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class WorkerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun login(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password).await()

    fun logout() = auth.signOut()

    fun currentUserId() = auth.currentUser?.uid

    suspend fun saveUserRole(role: String, name: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .set(mapOf("role" to role, "name" to name, "uid" to uid)).await()
    }

    suspend fun getUserRole(): String? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).get().await().getString("role")
    }

    suspend fun saveWorkerProfile(worker: Worker): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")
        db.collection("workers").document(uid).set(worker.copy(userId = uid)).await()
    }

    suspend fun getWorkerProfile(uid: String): Worker? =
        db.collection("workers").document(uid).get().await().toObject(Worker::class.java)

    suspend fun setAvailability(available: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("workers").document(uid).update("available", available).await()
    }

    suspend fun getWorkers(skill: String, town: String, availableOnly: Boolean): List<Worker> {
        var q: Query = db.collection("workers").whereEqualTo("role", "worker")
        if (skill.isNotBlank() && skill != "All") q = q.whereEqualTo("skill", skill)
        if (town.isNotBlank() && town != "All") q = q.whereEqualTo("town", town)
        if (availableOnly) q = q.whereEqualTo("available", true)
        return q.get().await().toObjects(Worker::class.java).sortedBy { it.area }
    }

    suspend fun thumbsUp(workerId: String) {
        val ref = db.collection("workers").document(workerId)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val count = snap.getLong("thumbsUpCount") ?: 0
            tx.update(ref, "thumbsUpCount", count + 1)
        }.await()
    }

    suspend fun postJobRequest(req: JobRequest): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")
        val ref = db.collection("jobRequests").document()
        db.collection("jobRequests").document(ref.id)
            .set(req.copy(id = ref.id, seekerUserId = uid)).await()
    }

    suspend fun getJobRequestsForSkill(skill: String): List<JobRequest> {
        var q: Query = db.collection("jobRequests").whereEqualTo("active", true)
        if (skill.isNotBlank() && skill != "All") q = q.whereEqualTo("skillNeeded", skill)
        return q.get().await().toObjects(JobRequest::class.java)
            .sortedByDescending { it.timestamp }
    }
}

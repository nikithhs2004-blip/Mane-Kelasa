package com.example.manekelasa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class WorkerViewModel : ViewModel() {

    private val repo = WorkerRepository()

    private val _uiState = MutableLiveData<UiState<Any>>()
    val uiState: LiveData<UiState<Any>> = _uiState

    private val _workers = MutableLiveData<List<Worker>>()
    val workers: LiveData<List<Worker>> = _workers

    private val _jobRequests = MutableLiveData<List<JobRequest>>()
    val jobRequests: LiveData<List<JobRequest>> = _jobRequests

    private val _myProfile = MutableLiveData<Worker?>()
    val myProfile: LiveData<Worker?> = _myProfile

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    fun checkSession() = viewModelScope.launch {
        if (repo.currentUserId() != null) _userRole.value = repo.getUserRole()
        else _userRole.value = null
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            repo.login(email, password)
            _uiState.value = UiState.Success(repo.getUserRole() ?: "seeker")
        } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Login failed") }
    }

    fun register(email: String, password: String, role: String, name: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            repo.register(email, password)
            repo.saveUserRole(role, name)
            _uiState.value = UiState.Success(role)
        } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Registration failed") }
    }

    fun logout() = repo.logout()

    fun loadMyProfile() = viewModelScope.launch {
        val uid = repo.currentUserId() ?: return@launch
        _myProfile.value = repo.getWorkerProfile(uid)
    }

    fun saveProfile(worker: Worker) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        repo.saveWorkerProfile(worker).fold(
            onSuccess = { _uiState.value = UiState.Success("saved") },
            onFailure = { _uiState.value = UiState.Error(it.message ?: "Save failed") }
        )
    }

    fun setAvailability(available: Boolean) = viewModelScope.launch {
        repo.setAvailability(available)
        _myProfile.value = _myProfile.value?.copy(available = available)
    }

    fun searchWorkers(skill: String, town: String, availableOnly: Boolean) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            val list = repo.getWorkers(skill, town, availableOnly)
            _workers.value = list
            _uiState.value = UiState.Success(list)
        } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Search failed") }
    }

    fun thumbsUp(workerId: String) = viewModelScope.launch { repo.thumbsUp(workerId) }

    fun loadJobRequests(skill: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            val list = repo.getJobRequestsForSkill(skill)
            _jobRequests.value = list
            _uiState.value = UiState.Success(list)
        } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Load failed") }
    }

    fun postJobRequest(req: JobRequest) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        repo.postJobRequest(req).fold(
            onSuccess = { _uiState.value = UiState.Success("posted") },
            onFailure = { _uiState.value = UiState.Error(it.message ?: "Post failed") }
        )
    }

    fun currentUserId() = repo.currentUserId()
}

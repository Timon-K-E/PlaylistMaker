package com.practicum.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp() = repository.shareApp()

    override fun openSupport() = repository.openSupport()

    override fun openUserAgreement() = repository.openUserAgreement()
}
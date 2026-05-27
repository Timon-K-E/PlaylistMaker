package com.practicum.playlistmaker.settings.ui

sealed class SettingsNavigationCommand {
    object ShareApp : SettingsNavigationCommand()
    object OpenSupport : SettingsNavigationCommand()
    object OpenUserAgreement : SettingsNavigationCommand()
}
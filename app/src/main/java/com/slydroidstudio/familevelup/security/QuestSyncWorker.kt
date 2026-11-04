package com.slydroidstudio.familevelup.security

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.slydroidstudio.familevelup.util.NotificationHelper

class QuestSyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        try {
            NotificationHelper.showNotification(
                applicationContext,
                "Rappel FamiLevelUp 🏰",
                "N'oublie pas de valider tes quêtes du jour pour gagner des pièces et de l'XP !"
            )
            return Result.success()
        } catch (_: Exception) {
            return Result.failure()
        }
    }
}

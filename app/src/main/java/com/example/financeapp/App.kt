package com.example.financeapp

import android.app.Application
import com.example.financeapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Limpar DataStore legado que causa crash (OrigemDados.OFFLINE_FIRST)
        limparDataStoreLegado()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun limparDataStoreLegado() {
        try {
            val dataStoreDir = File(filesDir, "datastore")
            if (dataStoreDir.exists()) {
                dataStoreDir.listFiles()?.forEach { file ->
                    // Remove apenas o DataStore de configuração legada, não o de auth
                    if (file.name.contains("config") || file.name.contains("origem")) {
                        file.delete()
                    }
                }
            }
        } catch (_: Exception) {
            // Silenciar — não crítico
        }
    }
}
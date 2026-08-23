package com.urielhuerta.criterio.data.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("published_at") val publishedAt: String?,
    @SerializedName("assets") val assets: List<GitHubAsset>?
)

data class GitHubAsset(
    @SerializedName("name") val name: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("size") val size: Long
)

data class UpdateCheckResult(
    val isUpdateAvailable: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val releaseTitle: String,
    val changelog: String,
    val downloadUrl: String?
)

class AppUpdateManager(
    private val repoOwner: String = "zess33",
    private val repoName: String = "criterio"
) {
    private val gson = Gson()

    fun getInstalledVersionName(context: Context): String {
        return try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            pInfo.versionName ?: "1.0.1"
        } catch (e: Exception) {
            "1.0.1"
        }
    }

    suspend fun checkForUpdates(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        val currentVersion = getInstalledVersionName(context)
        try {
            val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/releases/latest"
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "Criterio-Android-App")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode == 200) {
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                val release = gson.fromJson(json, GitHubRelease::class.java)

                val cleanLatest = release.tagName.removePrefix("v").trim()
                val cleanCurrent = currentVersion.removePrefix("v").trim()

                val isNewer = isVersionNewer(cleanLatest, cleanCurrent)
                val apkAsset = release.assets?.firstOrNull { it.name.endsWith(".apk") }
                val apkUrl = apkAsset?.downloadUrl ?: "https://github.com/$repoOwner/$repoName/releases/download/${release.tagName}/Criterio-v$cleanLatest.apk"

                UpdateCheckResult(
                    isUpdateAvailable = isNewer,
                    currentVersion = currentVersion,
                    latestVersion = cleanLatest,
                    releaseTitle = release.name ?: "Versión ${release.tagName}",
                    changelog = release.body ?: "Mejoras de rendimiento, estabilidad y nuevas funciones.",
                    downloadUrl = apkUrl
                )
            } else {
                UpdateCheckResult(
                    isUpdateAvailable = false,
                    currentVersion = currentVersion,
                    latestVersion = currentVersion,
                    releaseTitle = "Sin actualizaciones",
                    changelog = "Estás en la versión más reciente disponible.",
                    downloadUrl = null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateCheckResult(
                isUpdateAvailable = false,
                currentVersion = currentVersion,
                latestVersion = currentVersion,
                releaseTitle = "Error de conexión",
                changelog = "No se pudo verificar la versión en GitHub. Revisa tu conexión a internet.",
                downloadUrl = null
            )
        }
    }

    suspend fun downloadAndInstallApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Float) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                withContext(Dispatchers.Main) {
                    onError("El servidor devolvió código: ${connection.responseCode}")
                }
                return@withContext
            }

            val fileLength = connection.contentLength
            val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Criterio-update.apk")
            if (destinationFile.exists()) destinationFile.delete()

            connection.inputStream.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count
                        if (fileLength > 0) {
                            val progress = total.toFloat() / fileLength
                            withContext(Dispatchers.Main) { onProgress(progress) }
                        }
                        output.write(data, 0, count)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onSuccess()
                launchApkInstaller(context, destinationFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError("Error al descargar: ${e.localizedMessage ?: "Error desconocido"}")
            }
        }
    }

    fun launchApkInstaller(context: Context, apkFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun isVersionNewer(latest: String, current: String): Boolean {
        try {
            val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
            val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val length = maxOf(latestParts.size, currentParts.size)

            for (i in 0 until length) {
                val l = latestParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
            return false
        } catch (e: Exception) {
            return latest != current
        }
    }
}

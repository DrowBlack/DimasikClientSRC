package dimasik.utils.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MediaTracker {
    public static MediaInfo getSystemMediaInfo() {
        try {
            String command = "powershell -command \"try {\n    # \u041f\u0440\u043e\u0431\u0443\u0435\u043c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044e \u0447\u0435\u0440\u0435\u0437 SMTC\n    $ErrorActionPreference = 'Stop'\n    \n    # \u0417\u0430\u0433\u0440\u0443\u0436\u0430\u0435\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b\u0435 \u0442\u0438\u043f\u044b\n    Add-Type -AssemblyName System.Runtime.WindowsRuntime\n    \n    # \u0424\u0443\u043d\u043a\u0446\u0438\u044f \u0434\u043b\u044f \u0440\u0430\u0431\u043e\u0442\u044b \u0441 \u0430\u0441\u0438\u043d\u0445\u0440\u043e\u043d\u043d\u044b\u043c\u0438 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044f\u043c\u0438\n    $asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { \n        $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' \n    })[0]\n    \n    function Await($WinRtTask, $ResultType) {\n        $asTask = $asTaskGeneric.MakeGenericMethod($ResultType)\n        $netTask = $asTask.Invoke($null, @($WinRtTask))\n        $netTask.Wait(-1) | Out-Null\n        $netTask.Result\n    }\n    \n    # \u041f\u043e\u043b\u0443\u0447\u0430\u0435\u043c \u043c\u0435\u043d\u0435\u0434\u0436\u0435\u0440 \u0441\u0435\u0441\u0441\u0438\u0439\n    $manager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()\n    $sessionManager = Await $manager ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\n    \n    # \u041f\u043e\u043b\u0443\u0447\u0430\u0435\u043c \u0442\u0435\u043a\u0443\u0449\u0443\u044e \u0441\u0435\u0441\u0441\u0438\u044e\n    $currentSession = $sessionManager.GetCurrentSession()\n    \n    if ($currentSession -eq $null) {\n        return 'NO_ACTIVE_SESSION'\n    }\n    \n    # \u041f\u043e\u043b\u0443\u0447\u0430\u0435\u043c \u0441\u0432\u043e\u0439\u0441\u0442\u0432\u0430 \u043c\u0435\u0434\u0438\u0430\n    $mediaProperties = Await ($currentSession.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\n    \n    # \u041f\u043e\u043b\u0443\u0447\u0430\u0435\u043c \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044e \u043e \u0432\u043e\u0441\u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0434\u0435\u043d\u0438\u0438\n    $playbackInfo = $currentSession.GetPlaybackInfo()\n    \n    # \u0424\u043e\u0440\u043c\u0438\u0440\u0443\u0435\u043c \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\n    $artist = if ([string]::IsNullOrEmpty($mediaProperties.Artist)) { 'Unknown Artist' } else { $mediaProperties.Artist }\n    $title = if ([string]::IsNullOrEmpty($mediaProperties.Title)) { 'Unknown Title' } else { $mediaProperties.Title }\n    $album = if ([string]::IsNullOrEmpty($mediaProperties.AlbumTitle)) { '' } else { $mediaProperties.AlbumTitle }\n    $status = $playbackInfo.PlaybackStatus.ToString()\n    \n    return $artist + '|||' + $title + '|||' + $album + '|||' + $status\n    \n} catch {\n    return 'ERROR: ' + $_.Exception.Message\n}\"";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String result = reader.readLine();
            reader.close();
            process.destroy();
            return MediaTracker.parseMediaResult(result);
        }
        catch (Exception e) {
            return new MediaInfo("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f", "", "", "error", e.getMessage());
        }
    }

    private static MediaInfo parseMediaResult(String result) {
        String[] parts;
        if (result == null) {
            return new MediaInfo("\u041d\u0435\u0442 \u043e\u0442\u0432\u0435\u0442\u0430", "", "", "no_response", "PowerShell \u043d\u0435 \u0432\u0435\u0440\u043d\u0443\u043b \u0434\u0430\u043d\u043d\u044b\u0435");
        }
        if (result.equals("NO_ACTIVE_SESSION")) {
            return new MediaInfo("\u041d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u0439 \u0441\u0435\u0441\u0441\u0438\u0438", "", "", "no_session", "\u041d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u0433\u043e \u043c\u0435\u0434\u0438\u0430-\u043f\u043b\u0435\u0435\u0440\u0430");
        }
        if (result.startsWith("ERROR:")) {
            return new MediaInfo("\u041e\u0448\u0438\u0431\u043a\u0430", "", "", "error", result.substring(6));
        }
        if (result.contains("|||") && (parts = result.split("\\|\\|\\|")).length >= 4) {
            return new MediaInfo(parts[0], parts[1], parts[2], parts[3], "\u0423\u0441\u043f\u0435\u0448\u043d\u043e");
        }
        return new MediaInfo("\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442", result, "", "unknown", "\u041d\u0435\u043f\u0440\u0435\u0434\u0432\u0438\u0434\u0435\u043d\u043d\u044b\u0439 \u043e\u0442\u0432\u0435\u0442: " + result);
    }

    public static MediaInfo getSimpleMediaInfo() {
        try {
            String command = "powershell -command \"try {\n    # \u041f\u0440\u043e\u0441\u0442\u0430\u044f \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u0441\u0438\u0441\u0442\u0435\u043c\u043d\u044b\u0445 \u043c\u0435\u0434\u0438\u0430-\u043a\u043e\u043d\u0442\u0440\u043e\u043b\u043e\u0432\n    $process = Get-Process | Where-Object { \n        $_.ProcessName -in @('ApplicationFrameHost', 'SystemSettings') -and\n        $_.MainWindowTitle -ne ''\n    } | Select-Object -First 1\n    \n    if ($process) {\n        return 'SYSTEM_MEDIA_DETECTED'\n    } else {\n        return 'NO_SYSTEM_MEDIA'\n    }\n} catch {\n    return 'ERROR: ' + $_.Exception.Message\n}\"";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String result = reader.readLine();
            reader.close();
            process.destroy();
            if ("SYSTEM_MEDIA_DETECTED".equals(result)) {
                return new MediaInfo("\u0421\u0438\u0441\u0442\u0435\u043c\u043d\u044b\u0439 \u043c\u0435\u0434\u0438\u0430-\u043f\u043b\u0435\u0435\u0440", "\u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d", "", "detected", "System media detected");
            }
            return new MediaInfo("\u041d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d", "", "", "not_detected", result);
        }
        catch (Exception e) {
            return new MediaInfo("\u041e\u0448\u0438\u0431\u043a\u0430", "", "", "error", e.getMessage());
        }
    }

    public static class MediaInfo {
        private final String artist;
        private final String title;
        private final String album;
        private final String status;
        private final String message;

        public MediaInfo(String artist, String title, String album, String status, String message) {
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.status = status;
            this.message = message;
        }

        public String getArtist() {
            return this.artist;
        }

        public String getTitle() {
            return this.title;
        }

        public String getAlbum() {
            return this.album;
        }

        public String getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isPlaying() {
            return "Playing".equalsIgnoreCase(this.status);
        }

        public boolean isPaused() {
            return "Paused".equalsIgnoreCase(this.status);
        }

        public boolean hasError() {
            return "error".equalsIgnoreCase(this.status);
        }

        public String toString() {
            if (this.hasError()) {
                return "\u041e\u0448\u0438\u0431\u043a\u0430: " + this.message;
            }
            if (this.isPlaying() || this.isPaused()) {
                return this.artist + " - " + this.title + " (" + this.status + ")";
            }
            return this.message;
        }
    }
}

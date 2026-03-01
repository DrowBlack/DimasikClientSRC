package org.openjdk.nashorn.internal.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;

class CommandExecutor {
    private static final int BUFFER_SIZE = 1024;
    private static final boolean IS_WINDOWS = AccessController.doPrivileged(() -> System.getProperty("os.name").contains("Windows"));
    private static final String CYGDRIVE = "/cygdrive/";
    private static final String HOME_DIRECTORY = AccessController.doPrivileged(() -> System.getProperty("user.home"));
    private static final String[] redirectPrefixes = new String[]{"<", "0<", ">", "1>", ">>", "1>>", "2>", "2>>", "&>", "2>&1"};
    private static final RedirectType[] redirects = new RedirectType[]{RedirectType.REDIRECT_INPUT, RedirectType.REDIRECT_INPUT, RedirectType.REDIRECT_OUTPUT, RedirectType.REDIRECT_OUTPUT, RedirectType.REDIRECT_OUTPUT_APPEND, RedirectType.REDIRECT_OUTPUT_APPEND, RedirectType.REDIRECT_ERROR, RedirectType.REDIRECT_ERROR_APPEND, RedirectType.REDIRECT_OUTPUT_ERROR_APPEND, RedirectType.REDIRECT_ERROR_TO_OUTPUT};
    static final int EXIT_SUCCESS = 0;
    static final int EXIT_FAILURE = 1;
    private Map<String, String> environment;
    private String inputString = "";
    private String outputString = "";
    private String errorString = "";
    private int exitCode = 0;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private OutputStream errorStream = null;
    private final List<ProcessBuilder> processBuilders = new ArrayList<ProcessBuilder>();

    CommandExecutor() {
        this.environment = new HashMap<String, String>();
    }

    private String envVarValue(String key, String deflt) {
        return this.environment.getOrDefault(key, deflt);
    }

    private long envVarLongValue(String key) {
        try {
            return Long.parseLong(this.envVarValue(key, "0"));
        }
        catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private boolean envVarBooleanValue(String key) {
        return this.envVarLongValue(key) != 0L;
    }

    private static String stripQuotes(String token) {
        if (token.startsWith("\"") && token.endsWith("\"") || token.startsWith("'") && token.endsWith("'")) {
            token = token.substring(1, token.length() - 1);
        }
        return token;
    }

    private static Path resolvePath(String cwd, String fileName) {
        return Paths.get(CommandExecutor.sanitizePath(cwd), new String[0]).resolve(fileName).normalize();
    }

    private boolean builtIn(List<String> cmd, String cwd) {
        switch (cmd.get(0)) {
            case "cd": {
                boolean cygpath = IS_WINDOWS && cwd.startsWith(CYGDRIVE);
                String newCWD = cmd.size() < 2 ? HOME_DIRECTORY : cmd.get(1);
                Path cwdPath = CommandExecutor.resolvePath(cwd, newCWD);
                File file = cwdPath.toFile();
                if (!file.exists()) {
                    this.reportError("file.not.exist", file.toString());
                    return true;
                }
                if (!file.isDirectory()) {
                    this.reportError("not.directory", file.toString());
                    return true;
                }
                Object scwd = cwdPath.toString();
                if (cygpath && ((String)scwd).length() >= 2 && Character.isLetter(((String)scwd).charAt(0)) && ((String)scwd).charAt(1) == ':') {
                    scwd = CYGDRIVE + Character.toLowerCase(((String)scwd).charAt(0)) + "/" + ((String)scwd).substring(2);
                }
                this.environment.put("PWD", (String)scwd);
                return true;
            }
            case "setenv": {
                if (3 <= cmd.size()) {
                    String key = cmd.get(1);
                    String value = cmd.get(2);
                    this.environment.put(key, value);
                }
                return true;
            }
            case "unsetenv": {
                if (2 <= cmd.size()) {
                    String key = cmd.get(1);
                    this.environment.remove(key);
                }
                return true;
            }
        }
        return false;
    }

    private List<String> preprocessCommand(List<String> tokens, String cwd, RedirectInfo redirectInfo) {
        ArrayList<String> command = new ArrayList<String>();
        Iterator<String> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (redirectInfo.check(token, iterator, cwd)) continue;
            command.add(CommandExecutor.stripQuotes(token));
        }
        if (command.size() > 0) {
            command.set(0, CommandExecutor.sanitizePath((String)command.get(0)));
        }
        return command;
    }

    private static String sanitizePath(String d) {
        if (!IS_WINDOWS || !d.startsWith(CYGDRIVE)) {
            return d;
        }
        String pd = d.substring(CYGDRIVE.length());
        if (pd.length() >= 2 && pd.charAt(1) == '/') {
            return pd.charAt(0) + ":" + pd.substring(1);
        }
        if (pd.length() == 1) {
            return pd.charAt(0) + ":";
        }
        return d;
    }

    private void createProcessBuilder(List<String> command, String cwd, RedirectInfo redirectInfo) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(CommandExecutor.sanitizePath(cwd)));
        Map<String, String> processEnvironment = pb.environment();
        processEnvironment.clear();
        processEnvironment.putAll(this.environment);
        redirectInfo.apply(pb);
        this.processBuilders.add(pb);
    }

    private void command(List<String> tokens, boolean isPiped) {
        RedirectInfo redirectInfo;
        String cwd;
        List<String> command;
        if (this.envVarBooleanValue("JJS_ECHO")) {
            System.out.println(String.join((CharSequence)" ", tokens));
        }
        if ((command = this.preprocessCommand(tokens, cwd = this.envVarValue("PWD", HOME_DIRECTORY), redirectInfo = new RedirectInfo())).isEmpty() || this.builtIn(command, cwd)) {
            return;
        }
        this.createProcessBuilder(command, cwd, redirectInfo);
        if (isPiped) {
            return;
        }
        ProcessBuilder firstProcessBuilder = this.processBuilders.get(0);
        ProcessBuilder lastProcessBuilder = this.processBuilders.get(this.processBuilders.size() - 1);
        boolean inputIsPipe = firstProcessBuilder.redirectInput() == ProcessBuilder.Redirect.PIPE;
        boolean outputIsPipe = lastProcessBuilder.redirectOutput() == ProcessBuilder.Redirect.PIPE;
        boolean errorIsPipe = lastProcessBuilder.redirectError() == ProcessBuilder.Redirect.PIPE;
        boolean inheritIO = this.envVarBooleanValue("JJS_INHERIT_IO");
        if (inputIsPipe && (inheritIO || this.inputStream == System.in)) {
            firstProcessBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            inputIsPipe = false;
        }
        if (outputIsPipe && (inheritIO || this.outputStream == System.out)) {
            lastProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            outputIsPipe = false;
        }
        if (errorIsPipe && (inheritIO || this.errorStream == System.err)) {
            lastProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            errorIsPipe = false;
        }
        ArrayList<Process> processes = new ArrayList<Process>();
        for (ProcessBuilder pb : this.processBuilders) {
            try {
                processes.add(pb.start());
            }
            catch (IOException ex) {
                this.reportError("unknown.command", String.join((CharSequence)" ", pb.command()));
                return;
            }
        }
        this.processBuilders.clear();
        Process firstProcess = (Process)processes.get(0);
        Process lastProcess = (Process)processes.get(processes.size() - 1);
        ByteArrayOutputStream byteOutputStream = null;
        ByteArrayOutputStream byteErrorStream = null;
        ArrayList<Piper> piperThreads = new ArrayList<Piper>();
        if (inputIsPipe) {
            piperThreads.add(new Piper(Objects.requireNonNullElseGet(this.inputStream, () -> new ByteArrayInputStream(this.inputString.getBytes())), firstProcess.getOutputStream()).start());
        }
        if (outputIsPipe) {
            if (this.outputStream != null) {
                piperThreads.add(new Piper(lastProcess.getInputStream(), this.outputStream).start());
            } else {
                byteOutputStream = new ByteArrayOutputStream(1024);
                piperThreads.add(new Piper(lastProcess.getInputStream(), byteOutputStream).start());
            }
        }
        if (errorIsPipe) {
            if (this.errorStream != null) {
                piperThreads.add(new Piper(lastProcess.getErrorStream(), this.errorStream).start());
            } else {
                byteErrorStream = new ByteArrayOutputStream(1024);
                piperThreads.add(new Piper(lastProcess.getErrorStream(), byteErrorStream).start());
            }
        }
        int n = processes.size() - 1;
        for (int i = 0; i < n; ++i) {
            Process prev = (Process)processes.get(i);
            Process next = (Process)processes.get(i + 1);
            piperThreads.add(new Piper(prev.getInputStream(), next.getOutputStream()).start());
        }
        try {
            long timeout = this.envVarLongValue("JJS_TIMEOUT");
            if (timeout != 0L) {
                if (lastProcess.waitFor(timeout, TimeUnit.MILLISECONDS)) {
                    this.exitCode = lastProcess.exitValue();
                } else {
                    this.reportError("timeout", Long.toString(timeout));
                }
            } else {
                this.exitCode = lastProcess.waitFor();
            }
            for (Piper piper : piperThreads) {
                piper.join();
            }
            this.outputString = this.outputString + (byteOutputStream != null ? byteOutputStream.toString() : "");
            this.errorString = this.errorString + (byteErrorStream != null ? byteErrorStream.toString() : "");
        }
        catch (InterruptedException ex) {
            processes.forEach(p -> {
                if (p.isAlive()) {
                    p.destroy();
                }
                this.exitCode = this.exitCode == 0 ? p.exitValue() : this.exitCode;
            });
        }
        if (this.exitCode != 0 && this.envVarBooleanValue("JJS_THROW_ON_EXIT")) {
            throw ECMAErrors.rangeError("exec.returned.non.zero", ScriptRuntime.safeToString(this.exitCode));
        }
    }

    private static StreamTokenizer createTokenizer(String script) {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(script));
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.commentChar(35);
        tokenizer.quoteChar(34);
        tokenizer.quoteChar(39);
        tokenizer.eolIsSignificant(true);
        tokenizer.ordinaryChar(59);
        tokenizer.ordinaryChar(124);
        return tokenizer;
    }

    void process(String script) {
        StreamTokenizer tokenizer = CommandExecutor.createTokenizer(script);
        ArrayList<String> command = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        try {
            while (tokenizer.nextToken() != -1) {
                String token = tokenizer.sval;
                if (token == null) {
                    if (sb.length() != 0) {
                        command.add(sb.append(token).toString());
                        sb.setLength(0);
                    }
                    this.command(command, tokenizer.ttype == 124);
                    if (this.exitCode != 0) {
                        return;
                    }
                    command.clear();
                    continue;
                }
                if (token.endsWith("\\")) {
                    sb.append(token, 0, token.length() - 1).append(' ');
                    continue;
                }
                if (sb.length() == 0) {
                    if (tokenizer.ttype != -3) {
                        sb.append((char)tokenizer.ttype);
                        sb.append(token);
                        sb.append((char)tokenizer.ttype);
                        token = sb.toString();
                        sb.setLength(0);
                    }
                    command.add(token);
                    continue;
                }
                command.add(sb.append(token).toString());
                sb.setLength(0);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (sb.length() != 0) {
            command.add(sb.toString());
        }
        this.command(command, false);
    }

    void process(List<String> tokens) {
        ArrayList<String> command = new ArrayList<String>();
        Iterator<String> iterator = tokens.iterator();
        block8: while (iterator.hasNext() && this.exitCode == 0) {
            String token = iterator.next();
            if (token == null) continue;
            switch (token) {
                case "|": {
                    this.command(command, true);
                    command.clear();
                    continue block8;
                }
                case ";": {
                    this.command(command, false);
                    command.clear();
                    continue block8;
                }
            }
            command.add(token);
        }
        this.command(command, false);
    }

    void reportError(String msg, String object) {
        this.errorString = this.errorString + ECMAErrors.getMessage("range.error.exec." + msg, object);
        this.exitCode = 1;
    }

    String getOutputString() {
        return this.outputString;
    }

    String getErrorString() {
        return this.errorString;
    }

    int getExitCode() {
        return this.exitCode;
    }

    void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    void setInputString(String inputString) {
        this.inputString = inputString;
    }

    void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    void setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
    }

    private static class Piper
    implements Runnable {
        private final InputStream input;
        private final OutputStream output;
        private final Thread thread;

        Piper(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
            this.thread = new Thread(this, "$EXEC Piper");
        }

        Piper start() {
            this.thread.setDaemon(true);
            this.thread.start();
            return this;
        }

        @Override
        public void run() {
            try {
                int read;
                byte[] b = new byte[1024];
                while (-1 < (read = this.input.read(b, 0, b.length))) {
                    this.output.write(b, 0, read);
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Broken pipe", e);
            }
            finally {
                try {
                    this.input.close();
                }
                catch (IOException iOException) {}
                try {
                    this.output.close();
                }
                catch (IOException iOException) {}
            }
        }

        public void join() throws InterruptedException {
            this.thread.join();
        }
    }

    private static class RedirectInfo {
        private boolean hasRedirects = false;
        private ProcessBuilder.Redirect inputRedirect = ProcessBuilder.Redirect.PIPE;
        private ProcessBuilder.Redirect outputRedirect = ProcessBuilder.Redirect.PIPE;
        private ProcessBuilder.Redirect errorRedirect = ProcessBuilder.Redirect.PIPE;
        private boolean mergeError = false;

        RedirectInfo() {
        }

        boolean check(String token, Iterator<String> iterator, String cwd) {
            for (int i = 0; i < redirectPrefixes.length; ++i) {
                String prefix = redirectPrefixes[i];
                if (!token.startsWith(prefix)) continue;
                this.hasRedirects = true;
                RedirectType redirect = redirects[i];
                token = token.substring(prefix.length());
                File file = null;
                if (redirect != RedirectType.REDIRECT_ERROR_TO_OUTPUT) {
                    if (token.length() == 0) {
                        token = iterator.hasNext() ? iterator.next() : (IS_WINDOWS ? "NUL:" : "/dev/null");
                    }
                    file = CommandExecutor.resolvePath(cwd, token).toFile();
                }
                switch (redirect) {
                    case REDIRECT_INPUT: {
                        this.inputRedirect = ProcessBuilder.Redirect.from(file);
                        break;
                    }
                    case REDIRECT_OUTPUT: {
                        this.outputRedirect = ProcessBuilder.Redirect.to(file);
                        break;
                    }
                    case REDIRECT_OUTPUT_APPEND: {
                        this.outputRedirect = ProcessBuilder.Redirect.appendTo(file);
                        break;
                    }
                    case REDIRECT_ERROR: {
                        this.errorRedirect = ProcessBuilder.Redirect.to(file);
                        break;
                    }
                    case REDIRECT_ERROR_APPEND: {
                        this.errorRedirect = ProcessBuilder.Redirect.appendTo(file);
                        break;
                    }
                    case REDIRECT_OUTPUT_ERROR_APPEND: {
                        this.outputRedirect = ProcessBuilder.Redirect.to(file);
                        this.errorRedirect = ProcessBuilder.Redirect.to(file);
                        this.mergeError = true;
                        break;
                    }
                    case REDIRECT_ERROR_TO_OUTPUT: {
                        this.mergeError = true;
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        void apply(ProcessBuilder pb) {
            if (this.hasRedirects) {
                File outputFile = this.outputRedirect.file();
                File errorFile = this.errorRedirect.file();
                if (outputFile != null && outputFile.equals(errorFile)) {
                    this.mergeError = true;
                }
                pb.redirectInput(this.inputRedirect);
                pb.redirectOutput(this.outputRedirect);
                pb.redirectError(this.errorRedirect);
                pb.redirectErrorStream(this.mergeError);
            }
        }
    }

    static enum RedirectType {
        NO_REDIRECT,
        REDIRECT_INPUT,
        REDIRECT_OUTPUT,
        REDIRECT_OUTPUT_APPEND,
        REDIRECT_ERROR,
        REDIRECT_ERROR_APPEND,
        REDIRECT_OUTPUT_ERROR_APPEND,
        REDIRECT_ERROR_TO_OUTPUT;

    }
}

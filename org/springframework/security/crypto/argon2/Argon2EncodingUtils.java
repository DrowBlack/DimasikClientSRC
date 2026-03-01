package org.springframework.security.crypto.argon2;

import java.util.Base64;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;

final class Argon2EncodingUtils {
    private static final Base64.Encoder b64encoder = Base64.getEncoder().withoutPadding();
    private static final Base64.Decoder b64decoder = Base64.getDecoder();

    private Argon2EncodingUtils() {
    }

    static String encode(byte[] hash, Argon2Parameters parameters) throws IllegalArgumentException {
        StringBuilder stringBuilder = new StringBuilder();
        switch (parameters.getType()) {
            case 0: {
                stringBuilder.append("$argon2d");
                break;
            }
            case 1: {
                stringBuilder.append("$argon2i");
                break;
            }
            case 2: {
                stringBuilder.append("$argon2id");
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid algorithm type: " + parameters.getType());
            }
        }
        stringBuilder.append("$v=").append(parameters.getVersion()).append("$m=").append(parameters.getMemory()).append(",t=").append(parameters.getIterations()).append(",p=").append(parameters.getLanes());
        if (parameters.getSalt() != null) {
            stringBuilder.append("$").append(b64encoder.encodeToString(parameters.getSalt()));
        }
        stringBuilder.append("$").append(b64encoder.encodeToString(hash));
        return stringBuilder.toString();
    }

    static Argon2Hash decode(String encodedHash) throws IllegalArgumentException {
        String[] parts = encodedHash.split("\\$");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid encoded Argon2-hash");
        }
        int currentPart = 1;
        Argon2Parameters.Builder paramsBuilder = switch (parts[currentPart++]) {
            case "argon2d" -> new Argon2Parameters.Builder(0);
            case "argon2i" -> new Argon2Parameters.Builder(1);
            case "argon2id" -> new Argon2Parameters.Builder(2);
            default -> throw new IllegalArgumentException("Invalid algorithm type: " + parts[0]);
        };
        if (parts[currentPart].startsWith("v=")) {
            paramsBuilder.withVersion(Integer.parseInt(parts[currentPart].substring(2)));
        }
        int n = ++currentPart;
        ++currentPart;
        String[] performanceParams = parts[n].split(",");
        if (performanceParams.length != 3) {
            throw new IllegalArgumentException("Amount of performance parameters invalid");
        }
        if (!performanceParams[0].startsWith("m=")) {
            throw new IllegalArgumentException("Invalid memory parameter");
        }
        paramsBuilder.withMemoryAsKB(Integer.parseInt(performanceParams[0].substring(2)));
        if (!performanceParams[1].startsWith("t=")) {
            throw new IllegalArgumentException("Invalid iterations parameter");
        }
        paramsBuilder.withIterations(Integer.parseInt(performanceParams[1].substring(2)));
        if (!performanceParams[2].startsWith("p=")) {
            throw new IllegalArgumentException("Invalid parallelity parameter");
        }
        paramsBuilder.withParallelism(Integer.parseInt(performanceParams[2].substring(2)));
        paramsBuilder.withSalt(b64decoder.decode(parts[currentPart++]));
        return new Argon2Hash(b64decoder.decode(parts[currentPart]), paramsBuilder.build());
    }

    public static class Argon2Hash {
        private byte[] hash;
        private Argon2Parameters parameters;

        Argon2Hash(byte[] hash, Argon2Parameters parameters) {
            this.hash = Arrays.clone((byte[])hash);
            this.parameters = parameters;
        }

        public byte[] getHash() {
            return Arrays.clone((byte[])this.hash);
        }

        public void setHash(byte[] hash) {
            this.hash = Arrays.clone((byte[])hash);
        }

        public Argon2Parameters getParameters() {
            return this.parameters;
        }

        public void setParameters(Argon2Parameters parameters) {
            this.parameters = parameters;
        }
    }
}

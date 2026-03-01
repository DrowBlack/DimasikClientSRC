package cpw.mods.modlauncher;

import cpw.mods.modlauncher.TransformerVote;
import java.util.List;

public class VoteDeadlockException
extends RuntimeException {
    <T> VoteDeadlockException(List<TransformerVote<T>> votes, Class<?> aClass) {
    }
}

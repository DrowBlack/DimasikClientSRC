package cpw.mods.modlauncher;

import cpw.mods.modlauncher.TransformerVote;
import java.util.List;

public class VoteRejectedException
extends RuntimeException {
    <T> VoteRejectedException(List<TransformerVote<T>> votes, Class<?> aClass) {
    }
}

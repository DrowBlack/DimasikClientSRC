package dimasik.modules.player;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NoInteract
extends Module {
    public CheckboxOption allBlocks = new CheckboxOption("\u0412\u0441\u0435 \u0431\u043b\u043e\u043a\u0438", false);
    public MultiOption ignoreInteract = new MultiOption("\u041e\u0431\u044c\u0435\u043a\u0442\u044b", new MultiOptionValue("\u0421\u0442\u043e\u0439\u043a\u0438", true), new MultiOptionValue("\u0421\u0443\u043d\u0434\u0443\u043a\u0438", true), new MultiOptionValue("\u0414\u0432\u0435\u0440\u0438", true), new MultiOptionValue("\u041a\u043d\u043e\u043f\u043a\u0438", true), new MultiOptionValue("\u0412\u043e\u0440\u043e\u043d\u043a\u0438", true), new MultiOptionValue("\u0420\u0430\u0437\u0434\u0430\u0442\u0447\u0438\u043a\u0438", true), new MultiOptionValue("\u041d\u043e\u0442\u043d\u044b\u0435 \u0431\u043b\u043e\u043a\u0438", true), new MultiOptionValue("\u0412\u0435\u0440\u0441\u0442\u0430\u043a\u0438", true), new MultiOptionValue("\u041b\u044e\u043a\u0438", true), new MultiOptionValue("\u041f\u0435\u0447\u043a\u0438", true), new MultiOptionValue("\u041a\u0430\u043b\u0438\u0442\u043a\u0438", true), new MultiOptionValue("\u041d\u0430\u043a\u043e\u0432\u0430\u043b\u044c\u043d\u0438", true), new MultiOptionValue("\u0420\u044b\u0447\u0430\u0433\u0438", true)).visible(() -> (Boolean)this.allBlocks.getValue() == false);

    public NoInteract() {
        super("NoInteract", Category.PLAYER);
        this.settings(this.allBlocks, this.ignoreInteract);
    }

    public Set<Integer> getBlocks() {
        HashSet<Integer> blocks = new HashSet<Integer>();
        this.addBlocksForInteractionType(blocks, 1, 147, 329, 270);
        this.addBlocksForInteractionType(blocks, 2, 173, 161, 485, 486, 487, 488, 489, 720, 721);
        this.addBlocksForInteractionType(blocks, 3, 183, 308, 309, 310, 311, 312, 313, 718, 719, 758);
        this.addBlocksForInteractionType(blocks, 4, 336);
        this.addBlocksForInteractionType(blocks, 5, 70, 342, 508);
        this.addBlocksForInteractionType(blocks, 6, 74);
        this.addBlocksForInteractionType(blocks, 7, 151);
        this.addBlocksForInteractionType(blocks, 8, 222, 223, 224, 225, 226, 227, 712, 713, 379);
        this.addBlocksForInteractionType(blocks, 9, 154, 670);
        this.addBlocksForInteractionType(blocks, 10, 250, 475, 476, 477, 478, 479, 714, 715);
        this.addBlocksForInteractionType(blocks, 11, 328, 327, 326);
        this.addBlocksForInteractionType(blocks, 12, 171);
        return blocks;
    }

    private void addBlocksForInteractionType(Set<Integer> blocks, int interactionType, Integer ... blockIds) {
        if (this.ignoreInteract.getIndex(interactionType)) {
            blocks.addAll(Arrays.asList(blockIds));
        }
    }
}

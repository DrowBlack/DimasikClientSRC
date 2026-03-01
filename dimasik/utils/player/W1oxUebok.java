package dimasik.utils.player;

import dimasik.utils.player.W1oxUebokAction;
import dimasik.utils.time.TimerUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class W1oxUebok {
    private final TimerUtils time = new TimerUtils();
    private final List<W1oxStep> W1oxSteps = new LinkedList<W1oxStep>();
    private int currentStepIndex;
    private boolean loop;
    private boolean interrupt;

    private W1oxUebok() {
        this.cleanup();
    }

    public static W1oxUebok create() {
        return new W1oxUebok();
    }

    public W1oxUebok addStep(int delay, W1oxUebokAction action) {
        return this.addStep(delay, action, () -> true, 0);
    }

    public W1oxUebok addStep(int delay, W1oxUebokAction action, BooleanSupplier condition) {
        return this.addStep(delay, action, condition, 0);
    }

    public W1oxUebok addStep(int delay, W1oxUebokAction action, int priority) {
        return this.addStep(delay, action, () -> true, priority);
    }

    public W1oxUebok addStep(int delay, W1oxUebokAction action, BooleanSupplier condition, int priority) {
        this.W1oxSteps.add(new W1oxStep(delay, action, condition, priority));
        Collections.sort(this.W1oxSteps);
        return this;
    }

    public void timeReset() {
        this.time.reset();
    }

    public void resetStep() {
        this.currentStepIndex = 0;
    }

    public W1oxUebok cleanup() {
        this.W1oxSteps.clear();
        this.timeReset();
        this.resetStep();
        return this;
    }

    private boolean shouldLoop() {
        return this.currentStepIndex >= this.W1oxSteps.size() && this.loop;
    }

    public void update() {
        if (!this.W1oxSteps.isEmpty() && !this.interrupt) {
            this.W1oxSteps.forEach(step -> {
                W1oxStep currentStep;
                if (this.currentStepIndex < this.W1oxSteps.size() && (currentStep = this.W1oxSteps.get(this.currentStepIndex)).getCondition().getAsBoolean() && this.time.isReached(currentStep.getDelay())) {
                    currentStep.getAction().perform();
                    ++this.currentStepIndex;
                    this.time.reset();
                    if (this.shouldLoop()) {
                        this.resetStep();
                    }
                }
            });
            this.currentStepIndex = Math.min(this.currentStepIndex, this.W1oxSteps.size());
        }
    }

    public boolean isFinished() {
        return this.currentStepIndex >= this.W1oxSteps.size() && !this.loop && !this.interrupt;
    }

    public TimerUtils getTime() {
        return this.time;
    }

    public List<W1oxStep> getW1oxSteps() {
        return this.W1oxSteps;
    }

    public int getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public boolean isInterrupt() {
        return this.interrupt;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    class W1oxStep
    implements Comparable<W1oxStep> {
        private final int delay;
        private final W1oxUebokAction action;
        private final BooleanSupplier condition;
        private final int priority;

        public W1oxStep(int delay, W1oxUebokAction action, BooleanSupplier condition, int priority) {
            this.delay = delay;
            this.action = action;
            this.condition = condition;
            this.priority = priority;
        }

        @Override
        public int compareTo(W1oxStep otherStep) {
            return Integer.compare(otherStep.getPriority(), this.getPriority());
        }

        public int getDelay() {
            return this.delay;
        }

        public W1oxUebokAction getAction() {
            return this.action;
        }

        public BooleanSupplier getCondition() {
            return this.condition;
        }

        public int getPriority() {
            return this.priority;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof W1oxStep) {
                BooleanSupplier other$condition;
                W1oxUebokAction other$action;
                W1oxStep other = (W1oxStep)o;
                if (!other.canEqual(this)) {
                    return false;
                }
                if (this.getDelay() != other.getDelay()) {
                    return false;
                }
                W1oxUebokAction this$action = this.getAction();
                if (!Objects.equals(this$action, other$action = other.getAction())) {
                    return false;
                }
                BooleanSupplier this$condition = this.getCondition();
                if (!Objects.equals(this$condition, other$condition = other.getCondition())) {
                    return false;
                }
                return this.getPriority() == other.getPriority();
            }
            return false;
        }

        protected boolean canEqual(Object other) {
            return other instanceof W1oxStep;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getDelay();
            W1oxUebokAction $action = this.getAction();
            result = result * 59 + ($action == null ? 43 : $action.hashCode());
            BooleanSupplier $condition = this.getCondition();
            result = result * 59 + ($condition == null ? 43 : $condition.hashCode());
            result = result * 59 + this.getPriority();
            return result;
        }

        public String toString() {
            int var10000 = this.getDelay();
            return "ScriptUtil.ScriptStep(delay=" + var10000 + ", action=" + String.valueOf(this.getAction()) + ", condition=" + String.valueOf(this.getCondition()) + ", priority=" + this.getPriority() + ")";
        }
    }
}

package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import net.minecraft.entity.ai.brain.schedule.DutyTime;

public class ScheduleDuties {
    private final List<DutyTime> dutyTimes = Lists.newArrayList();
    private int index;

    public ScheduleDuties addDutyTime(int duration, float active) {
        this.dutyTimes.add(new DutyTime(duration, active));
        this.sortDutyTimes();
        return this;
    }

    private void sortDutyTimes() {
        Int2ObjectAVLTreeMap int2objectsortedmap = new Int2ObjectAVLTreeMap();
        this.dutyTimes.forEach(dutyTime -> {
            DutyTime dutytime = int2objectsortedmap.put(dutyTime.getDuration(), dutyTime);
        });
        this.dutyTimes.clear();
        this.dutyTimes.addAll(int2objectsortedmap.values());
        this.index = 0;
    }

    public float updateActiveDutyTime(int dayTime) {
        DutyTime dutytime2;
        if (this.dutyTimes.size() <= 0) {
            return 0.0f;
        }
        DutyTime dutytime = this.dutyTimes.get(this.index);
        DutyTime dutytime1 = this.dutyTimes.get(this.dutyTimes.size() - 1);
        boolean flag = dayTime < dutytime.getDuration();
        int i = flag ? 0 : this.index;
        float f = flag ? dutytime1.getActive() : dutytime.getActive();
        int j = i;
        while (j < this.dutyTimes.size() && (dutytime2 = this.dutyTimes.get(j)).getDuration() <= dayTime) {
            this.index = j++;
            f = dutytime2.getActive();
        }
        return f;
    }
}

package com.nightshadepvp.core.quest;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MutableClassToInstanceMap;
import com.nightshadepvp.core.Core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Blok on 12/10/2017.
 */
public class QuestHandler {

    private final ClassToInstanceMap<Quest> quests = MutableClassToInstanceMap.create();

    private Core plugin;

    public QuestHandler(Core plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        addQuest(new HalfHeartQuest()); //done
        addQuest(new BruteForceQuest());
        addQuest(new LoserQuest()); //done
        addQuest(new LoserDeluxeQuest());
        addQuest(new BracketBusterQuest());
        addQuest(new FirstBloodQuest());
        addQuest(new UHCWinnerQuest());
        addQuest(new DroppedQuest());
        addQuest(new UnderdogQuest());
    }

    private void addQuest(Quest quest) {
        quest.setId(quests.size());
        this.quests.put(quest.getClass(), quest);
        plugin.getServer().getPluginManager().registerEvents(quest, plugin);
    }

    public Quest getQuest(String name) {
        for (Quest quest : this.quests.values()) {
            if (quest.getName().equalsIgnoreCase(name)) {
                return quest;
            }
        }

        return null;
    }

    public <T extends Quest> T getQuest(Class<T> clazz){
        return quests.getInstance(clazz);
    }


    public List<Quest> getQuests() {
        return ImmutableList.copyOf(quests.values());
    }
}

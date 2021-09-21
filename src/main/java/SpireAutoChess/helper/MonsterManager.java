package SpireAutoChess.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import SpireAutoChess.monsters.AbstractTeamMonster;

public class MonsterManager {
    public static final HashMap<String, AbstractTeamMonster> AllMonsters = new HashMap<>();

    public static void RegisterMonster(String id, AbstractTeamMonster monster) {
        AllMonsters.put(id, monster);
    }

    public static AbstractTeamMonster GetMonsterInstance(String id) {
        if (!AllMonsters.containsKey(id))
            return null;
        AbstractTeamMonster m = AllMonsters.get(id);
        try {
            return (AbstractTeamMonster) m.getClass().getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    // public static
}
package SpireAutoChess.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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

    public static ArrayList<AbstractTeamMonster> GetRandomMonsters(int num, boolean canDup) {
        ArrayList<AbstractTeamMonster> tmp = new ArrayList<>();
        ArrayList<String> pool = new ArrayList<>(AllMonsters.keySet());
        boolean canAdd;

        while (tmp.size() < num) {
            String id = pool.get(AbstractDungeon.merchantRng.random(0, pool.size() - 1));
            canAdd = true;

            if (!canDup) {
                for (AbstractTeamMonster m : tmp) {
                    if (id.equals(m.id)) {
                        canAdd = false;
                    }
                    break;
                }
            }

            if (canAdd) {
                tmp.add(GetMonsterInstance(id));
            }
        }
        return tmp;
    }

    // public static
}
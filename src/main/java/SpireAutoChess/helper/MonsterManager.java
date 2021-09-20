package SpireAutoChess.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class MonsterManager {
    public static final HashMap<String, AbstractMonster> AllMonsters = new HashMap<>();
    public static final HashMap<String, MonsterStrings> MonsterLocale = new HashMap<>();

    public static void RegisterMonster(String id, AbstractMonster monster) {
        AllMonsters.put(id, monster);
        MonsterLocale.put(id, CardCrawlGame.languagePack.getMonsterStrings(id));
    }

    public static AbstractMonster GetMonsterInstance(String id) {
        AbstractMonster m = AllMonsters.get(id);
        try {
            return (AbstractMonster) m.getClass().getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static MonsterStrings GetLocale(String id) {
        return MonsterLocale.get(id);
    }

    // public static
}
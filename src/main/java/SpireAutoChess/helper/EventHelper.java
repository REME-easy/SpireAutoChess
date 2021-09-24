package SpireAutoChess.helper;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class EventHelper {
    public static final ArrayList<BattleStartSubscriber> BATTLE_START_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<TeamMonsterDeathSubscriber> TEAM_MONSTER_DEATH_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<EnemyDeathSubscriber> ENEMY_DEATH_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<StartOfTurnSubscriber> START_OF_TURN_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<EndOfTurnSubscriber> END_OF_TURN_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<TeamMonsterChangePositionSubscriber> TEAM_MONSTER_CHANGE_POSITION_SUBSCRIBERS = new ArrayList<>();
    public static final ArrayList<TeamMonsterRemoveSubscriber> TEAM_MONSTER_REMOVE_SUBSCRIBERS = new ArrayList<>();

    private static <T> void subscribeIfInstance(ArrayList<T> list, CustomSubscriber sub, Class<T> clazz) {
        if (clazz.isInstance(sub)) {
            list.add(clazz.cast(sub));
        }
    }

    public static void subscribe(CustomSubscriber sub) {
        subscribeIfInstance(BATTLE_START_SUBSCRIBERS, sub, BattleStartSubscriber.class);
        subscribeIfInstance(TEAM_MONSTER_DEATH_SUBSCRIBERS, sub, TeamMonsterDeathSubscriber.class);
        subscribeIfInstance(ENEMY_DEATH_SUBSCRIBERS, sub, EnemyDeathSubscriber.class);
        subscribeIfInstance(START_OF_TURN_SUBSCRIBERS, sub, StartOfTurnSubscriber.class);
        subscribeIfInstance(END_OF_TURN_SUBSCRIBERS, sub, EndOfTurnSubscriber.class);
        subscribeIfInstance(TEAM_MONSTER_CHANGE_POSITION_SUBSCRIBERS, sub, TeamMonsterChangePositionSubscriber.class);
        subscribeIfInstance(TEAM_MONSTER_REMOVE_SUBSCRIBERS, sub, TeamMonsterRemoveSubscriber.class);
    }

    public static void receiveOnBattleStart(AbstractRoom abstractRoom) {
        GenericHelper.info("battle start");

        BATTLE_START_SUBSCRIBERS.clear();
        TEAM_MONSTER_DEATH_SUBSCRIBERS.clear();
        ENEMY_DEATH_SUBSCRIBERS.clear();
        START_OF_TURN_SUBSCRIBERS.clear();
        END_OF_TURN_SUBSCRIBERS.clear();
        TEAM_MONSTER_CHANGE_POSITION_SUBSCRIBERS.clear();
        TEAM_MONSTER_REMOVE_SUBSCRIBERS.clear();

        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof CustomSubscriber) {
                ((CustomSubscriber) card).subscribe();
            }
        }
        for (AbstractTeamMonster m : TeamMonsterGroup.Inst().Monsters) {
            if (m instanceof CustomSubscriber) {
                ((CustomSubscriber) m).subscribe();
            }
        }

        for (BattleStartSubscriber sub : BATTLE_START_SUBSCRIBERS) {
            sub.OnBattleStart(abstractRoom);
        }
    }

    public interface CustomSubscriber {
        default void subscribe() {
            EventHelper.subscribe(this);
        }
    }

    public interface BattleStartSubscriber extends CustomSubscriber {
        void OnBattleStart(AbstractRoom room);
    }

    public interface StartOfTurnSubscriber extends CustomSubscriber {
        void OnStartOfTurn(int turns);
    }

    public interface EndOfTurnSubscriber extends CustomSubscriber {
        void OnEndOfTurn(int turns);
    }

    public interface TeamMonsterDeathSubscriber extends CustomSubscriber {
        void OnTeamMonsterDeath(AbstractTeamMonster monster);
    }

    public interface EnemyDeathSubscriber extends CustomSubscriber {
        void OnMonsterDeath(AbstractMonster monster);
    }

    public interface TeamMonsterChangePositionSubscriber extends CustomSubscriber {
        void OnChangPosition(int source, int target);
    }

    public interface TeamMonsterRemoveSubscriber extends CustomSubscriber {
        void OnRemoveMonster(int position);
    }

}

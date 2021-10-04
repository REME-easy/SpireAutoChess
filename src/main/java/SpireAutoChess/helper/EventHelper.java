package SpireAutoChess.helper;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class EventHelper {
    public static final ArrayList<BattleStartSubscriber> BattleStartSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterDeathSubscriber> TeamMonsterDeathSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterSpawnSubscriber> TeamMonsterSpawnSubscribers = new ArrayList<>();
    public static final ArrayList<EnemyDeathSubscriber> EnemyDeathSubscribers = new ArrayList<>();
    public static final ArrayList<StartOfTurnSubscriber> StartOfTurnSubscribers = new ArrayList<>();
    public static final ArrayList<EndOfTurnSubscriber> EndOfTurnSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterChangePositionSubscriber> TeamMonsterChangePositionSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterRemoveSubscriber> TeamMonsterRemoveSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterTakeTurnSubscriber> TeamMonsterTakeTurnSubscribers = new ArrayList<>();
    public static final ArrayList<TeamMonsterGainBlockSubscriber> TeamMonsterGainBlockSubscribers = new ArrayList<>();

    private static <T> void subscribeIfInstance(ArrayList<T> list, CustomSubscriber sub, Class<T> clazz) {
        if (clazz.isInstance(sub)) {
            list.add(clazz.cast(sub));
        }
    }

    public static void subscribe(CustomSubscriber sub) {
        subscribeIfInstance(BattleStartSubscribers, sub, BattleStartSubscriber.class);
        subscribeIfInstance(TeamMonsterDeathSubscribers, sub, TeamMonsterDeathSubscriber.class);
        subscribeIfInstance(TeamMonsterSpawnSubscribers, sub, TeamMonsterSpawnSubscriber.class);
        subscribeIfInstance(EnemyDeathSubscribers, sub, EnemyDeathSubscriber.class);
        subscribeIfInstance(StartOfTurnSubscribers, sub, StartOfTurnSubscriber.class);
        subscribeIfInstance(EndOfTurnSubscribers, sub, EndOfTurnSubscriber.class);
        subscribeIfInstance(TeamMonsterChangePositionSubscribers, sub, TeamMonsterChangePositionSubscriber.class);
        subscribeIfInstance(TeamMonsterRemoveSubscribers, sub, TeamMonsterRemoveSubscriber.class);
        subscribeIfInstance(TeamMonsterTakeTurnSubscribers, sub, TeamMonsterTakeTurnSubscriber.class);
        subscribeIfInstance(TeamMonsterGainBlockSubscribers, sub, TeamMonsterGainBlockSubscriber.class);
    }

    public static void receiveOnBattleStart(AbstractRoom abstractRoom) {
        GenericHelper.info("battle start");

        BattleStartSubscribers.clear();
        TeamMonsterDeathSubscribers.clear();
        TeamMonsterSpawnSubscribers.clear();
        EnemyDeathSubscribers.clear();
        StartOfTurnSubscribers.clear();
        EndOfTurnSubscribers.clear();
        TeamMonsterChangePositionSubscribers.clear();
        TeamMonsterRemoveSubscribers.clear();
        TeamMonsterTakeTurnSubscribers.clear();
        TeamMonsterGainBlockSubscribers.clear();

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

        for (BattleStartSubscriber sub : BattleStartSubscribers) {
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

    public interface TeamMonsterSpawnSubscriber extends CustomSubscriber {
        void OnTeamMonsterSpawn(AbstractTeamMonster monster);
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

    public interface TeamMonsterTakeTurnSubscriber extends CustomSubscriber {
        void OnTakeTurn(AbstractTeamMonster monster);
    }

    public interface TeamMonsterGainBlockSubscriber extends CustomSubscriber {
        float OnGainBlock(AbstractTeamMonster monster, float amt);
    }

}

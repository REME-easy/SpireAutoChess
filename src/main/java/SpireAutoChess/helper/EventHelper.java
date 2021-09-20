package SpireAutoChess.helper;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class EventHelper {
    // public static final ArrayList<SpellTriggerSubscriber> spellTriggerSubscribers
    // = new ArrayList<>();
    // public static final ArrayList<SpellApplySubscriber> spellApplySubscribers =
    // new ArrayList<>();
    // public static final ArrayList<EnterPackSubscriber> enterPackSubscribers = new
    // ArrayList<>();
    // public static final ArrayList<OutPackSubscriber> outPackSubscribers = new
    // ArrayList<>();
    // public static final ArrayList<EndTurnSubscriber> endTurnSubscribers = new
    // ArrayList<>();

    private static <T> void AddToList(ArrayList<T> list, CustomSubscriber sub, Class<T> clazz) {
        if (clazz.isInstance(sub)) {
            list.add(clazz.cast(sub));
        }
    }

    public static void Subscribe(CustomSubscriber sub) {
        // AddToList(spellTriggerSubscribers, sub, SpellTriggerSubscriber.class);
        // AddToList(spellApplySubscribers, sub, SpellApplySubscriber.class);
        // AddToList(enterPackSubscribers, sub, EnterPackSubscriber.class);
        // AddToList(outPackSubscribers, sub, OutPackSubscriber.class);
        // AddToList(endTurnSubscribers, sub, EndTurnSubscriber.class);
    }

    public static void receiveOnBattleStart(AbstractRoom abstractRoom) {
        // spellApplySubscribers.clear();
        // spellTriggerSubscribers.clear();
        // enterPackSubscribers.clear();
        // outPackSubscribers.clear();
        // endTurnSubscribers.clear();
        GenericHelper.info("battle start");
        // for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
        // if (c instanceof AbstractCeobeCard) {
        // ((AbstractCeobeCard) c).receiveBattleStart();
        // }
        // }
        // for (AbstractCard c : AbstractDungeon.player.hand.group) {
        // if (c instanceof AbstractCeobeCard) {
        // ((AbstractCeobeCard) c).receiveBattleStart();
        // }
        // }
        // for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
        // if (c instanceof AbstractCeobeCard) {
        // ((AbstractCeobeCard) c).receiveBattleStart();
        // }
        // }
        // GenericHelper.foreachCardNotExhausted((card) -> {
        // if (card instanceof AbstractCeobeCard) {
        // ((AbstractCeobeCard) card).receiveBattleStart();
        // }
        // return false;
        // });
    }

    interface CustomSubscriber {
    }

}

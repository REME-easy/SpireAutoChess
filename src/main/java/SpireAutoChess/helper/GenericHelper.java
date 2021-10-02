package SpireAutoChess.helper;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericHelper {
    private static final Logger logger = LogManager.getLogger(GenericHelper.class);

    public GenericHelper() {
    }

    public static AbstractMonster getRandomMonsterSafe() {
        AbstractMonster m = AbstractDungeon.getRandomMonster();
        if (m != null && !m.isDeadOrEscaped() && !m.isDead) {
            return m;
        } else
            return null;
    }

    public static boolean isInBattle() {
        return CardCrawlGame.dungeon != null && AbstractDungeon.currMapNode != null
                && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static ArrayList<AbstractMonster> monsters() {
        return AbstractDungeon.getMonsters().monsters;
    }

    public static boolean isAlive(AbstractCreature c) {
        return c != null && !c.isDeadOrEscaped() && !c.isDead;
    }

    public static int aliveMonstersAmount() {
        int i = 0;
        for (AbstractMonster m : monsters()) {
            if (!m.isDeadOrEscaped() && !m.isDead) {
                i++;
            }
        }
        return i;
    }

    public static AbstractMonster getFrontMonster() {
        ArrayList<AbstractMonster> list = (ArrayList<AbstractMonster>) GenericHelper.monsters().stream()
                .filter((m) -> GenericHelper.isAlive(m)).collect(Collectors.toList());
        GenericHelper.info(list.toString());
        if (list.size() > 0) {
            AbstractMonster target = list.get(0);
            for (AbstractMonster m : list) {
                if (m.hb.cX < target.hb.cX) {
                    target = m;
                }
            }
            return target;
        }
        return null;
    }

    public static void MoveMonster(AbstractMonster m, float x, float y) {
        m.drawX = x;
        m.drawY = y;
        m.animX = 0.0F;
        m.animY = 0.0F;
        m.hb.move(m.drawX + m.hb_x + m.animX, m.drawY + m.hb_y + m.hb_h / 2.0F);
        m.healthHb.move(m.hb.cX, m.hb.cY - m.hb_h / 2.0F - m.healthHb.height / 2.0F);
        m.refreshIntentHbLocation();
    }

    public static void addToNext(AbstractGameAction action) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            int index = Math.min(AbstractDungeon.actionManager.actions.size(), 1);
            AbstractDungeon.actionManager.actions.add(index, action);
        }
    }

    public static void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public static void addToBotAbstract(VoidSupplier func) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                func.get();
                isDone = true;
            }
        });
    }

    public static void applyPowerToSelf(AbstractCreature source, Function<AbstractCreature, AbstractPower> fac) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source, fac.apply(source)));
    }

    public static void applyPowerTo(AbstractCreature source, Function<AbstractCreature, AbstractPower> fac,
            AbstractCreature... targets) {
        for (AbstractCreature t : targets) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, t, fac.apply(t)));
        }
    }

    public static void addEffect(AbstractGameEffect effect) {
        AbstractDungeon.effectList.add(effect);
    }

    public static void GainRelic(AbstractRelic r) {
        AbstractDungeon.player.relics.add(r);
        r.onEquip();
        AbstractDungeon.player.reorganizeRelics();
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static AbstractCard makeStatEquivalentCopy(AbstractCard c) {
        AbstractCard card = c.makeStatEquivalentCopy();
        card.retain = c.retain;
        card.selfRetain = c.selfRetain;
        card.purgeOnUse = c.purgeOnUse;
        card.isEthereal = c.isEthereal;
        card.exhaust = c.exhaust;
        card.glowColor = c.glowColor;
        card.rawDescription = c.rawDescription;
        card.cardsToPreview = c.cardsToPreview;
        card.initializeDescription();
        return card;
    }

    public static void foreachCardNotExhausted(Function<AbstractCard, Boolean> func) {
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (func.apply(c))
                return;
        }
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (func.apply(c))
                return;
        }
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (func.apply(c))
                return;
        }
    }

    public static void foreachCardNotExhaustedNotHand(Function<AbstractCard, Boolean> func) {
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (func.apply(c))
                return;
        }
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (func.apply(c))
                return;
        }
    }

    public static void foreachPowerHeroAndMonstersHave(Function<AbstractPower, Boolean> func) {
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (func.apply(p))
                return;
        }
        for (AbstractMonster m : monsters()) {
            if (isAlive(m))
                for (AbstractPower p : m.powers) {
                    if (func.apply(p))
                        return;
                }
        }
    }

    public static void foreachAliveMonster(Function<AbstractMonster, Boolean> func) {
        for (AbstractMonster m : monsters()) {
            if (isAlive(m)) {
                if (func.apply(m))
                    return;
            }
        }
    }

    public static void tempLoseStrength(AbstractCreature mo, AbstractCreature p, int amt) {
        addToBot(new ApplyPowerAction(mo, p, new StrengthPower(mo, -amt), -amt, true, AttackEffect.NONE));
        if (!mo.hasPower("Artifact")) {
            addToBot(new ApplyPowerAction(mo, p, new GainStrengthPower(mo, amt), amt, true, AttackEffect.NONE));
        }
    }

    // public static Pack MakePackAndRemoveCard(ArrayList<AbstractCard> cards) {
    //
    // }

    public interface VoidSupplier {
        void get();
    }

}

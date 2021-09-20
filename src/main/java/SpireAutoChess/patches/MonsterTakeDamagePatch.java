package SpireAutoChess.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import SpireAutoChess.character.TeamMonsterGroup;

public class MonsterTakeDamagePatch {
    private static final Logger logger = LogManager.getLogger(MonsterTakeDamagePatch.class.getName());
    public static AbstractCreature target;

    public MonsterTakeDamagePatch() {
    }

    public static boolean getTarget() {
        ArrayList<AbstractMonster> tmp2 = TeamMonsterGroup.GetMonsters();
        ArrayList<AbstractMonster> tmp = new ArrayList<>();
        for (AbstractMonster o1 : tmp2) {
            if (!o1.isDeadOrEscaped())
                tmp.add(o1);
        }
        if (tmp.size() > 0) {
            target = tmp.get(tmp.size() - 1);
            logger.info(target.name + "承受伤害");
            return true;
        } else {
            target = null;
            logger.info("getTarget:你承受伤害");
            return false;
        }
    }

    @SpirePatch(clz = AbstractGameAction.class, method = "setValues", paramtypez = { AbstractCreature.class,
            DamageInfo.class })
    public static class ChangeDamageTarget {
        public static void Postfix(AbstractGameAction _inst, AbstractCreature target, DamageInfo info) {
            if (target != null && info.type != DamageInfo.DamageType.HP_LOSS
                    && (info.owner == null || !info.owner.isPlayer) && target == AbstractDungeon.player
                    && getTarget()) {
                _inst.target = MonsterTakeDamagePatch.target;
                logger.info(MonsterTakeDamagePatch.target + "承受伤害");
            } else {
                logger.info("你承受伤害");
            }

        }
    }

    @SpirePatch(clz = AbstractGameAction.class, method = "setValues", paramtypez = { AbstractCreature.class,
            AbstractCreature.class, int.class })
    public static class ChangeBuffTarget {
        public static void Postfix(AbstractGameAction _inst, AbstractCreature target, AbstractCreature source,
                int amount) {
            if (source != null && target != null && !source.isPlayer && target == AbstractDungeon.player
                    && getTarget()) {
                _inst.target = MonsterTakeDamagePatch.target;
                logger.info(MonsterTakeDamagePatch.target + "承受buff");
            }
        }
    }

    @SpirePatch(clz = AbstractGameAction.class, method = "setValues", paramtypez = { AbstractCreature.class,
            AbstractCreature.class })
    public static class ChangeBuff2Target {
        public static void Postfix(AbstractGameAction _inst, AbstractCreature target, AbstractCreature source) {

            if (source != null && target != null && !source.isPlayer && target == AbstractDungeon.player
                    && getTarget()) {
                _inst.target = MonsterTakeDamagePatch.target;
                logger.info(MonsterTakeDamagePatch.target + "承受伤害");

            }

        }
    }

    @SpirePatch(clz = ApplyPowerAction.class, method = "<ctor>", paramtypez = { AbstractCreature.class,
            AbstractCreature.class, AbstractPower.class, int.class, boolean.class,
            AbstractGameAction.AttackEffect.class })
    public static class ChangeApplyBuffTarget {
        public static void Postfix(ApplyPowerAction _inst, AbstractCreature target, AbstractCreature source,
                AbstractPower power, int n, boolean b, AbstractGameAction.AttackEffect e) {
            if (source != null && target != null && power.owner != _inst.target
                    && power.owner != AbstractDungeon.player) {
                power.owner = _inst.target;
                logger.info("改变了目标。目前：target:" + _inst.target.name + ",owner:" + power.owner);
            }
        }
    }
}

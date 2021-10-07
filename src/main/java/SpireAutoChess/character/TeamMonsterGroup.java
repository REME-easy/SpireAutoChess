package SpireAutoChess.character;

import java.util.ArrayList;
import java.util.function.Function;

import SpireAutoChess.patches.AutoEndTurnPatch;
import SpireAutoChess.ui.AutoEndTurnOption;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterQueueItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;

import SpireAutoChess.character.TeamMonsterGroup.MonsterSaveInfo;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.helper.MonsterManager;
import SpireAutoChess.monsters.AbstractTeamMonster;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.ISubscriber;
import javassist.CtBehavior;

public class TeamMonsterGroup implements ISubscriber, CustomSavable<ArrayList<ArrayList<MonsterSaveInfo>>> {
    public final ArrayList<AbstractTeamMonster> Monsters = new ArrayList<>();
    public final ArrayList<AbstractTeamMonster> BattleMonsters = new ArrayList<>();
    public final ArrayList<AbstractTeamMonster> WaitingMonsters = new ArrayList<>();

    private AbstractTeamMonster hoveredMonster;

    public static int maxSize = 5;

    /**
     * 获取怪物团队的单例。
     * 
     * @return TeamMonsterGroup
     */
    public static TeamMonsterGroup Inst() {
        return MonstersFields.Monsters.get(AbstractDungeon.player);
    }

    public TeamMonsterGroup(ArrayList<AbstractTeamMonster> input) {
        this.hoveredMonster = null;
        this.Monsters.addAll(input);
        BaseMod.subscribe(this);
        BaseMod.addSaveField("TeamMonsters", this);
    }

    public TeamMonsterGroup() {
        this(new ArrayList<>());
    }

    /**
     * 向战斗中怪物团队添加怪物。
     * 
     * @param o
     */
    public void addMonster(AbstractTeamMonster... o) {
        for (AbstractTeamMonster m : o) {
            this.Monsters.add(m);
        }
    }

    /**
     * 获取所有参战（战斗中）怪物。
     * 
     * @return ArrayList<AbstractTeamMonster>
     */
    public static ArrayList<AbstractTeamMonster> GetMonsters() {
        return MonstersFields.Monsters.get(AbstractDungeon.player).Monsters;
    }

    /**
     * 获取所有参战（战斗外）怪物。
     * 
     * @return ArrayList<AbstractTeamMonster>
     */
    public static ArrayList<AbstractTeamMonster> GetBattleMonsters() {
        return MonstersFields.Monsters.get(AbstractDungeon.player).BattleMonsters;
    }

    /**
     * 获取所有待战怪物。
     * 
     * @return ArrayList<AbstractTeamMonster>
     */
    public static ArrayList<AbstractTeamMonster> GetWaitingMonsters() {
        return MonstersFields.Monsters.get(AbstractDungeon.player).WaitingMonsters;
    }

    /**
     * 向战斗中所有友方怪物执行动作。
     * 
     * @param func 向每个怪物执行的动作，若为true则停止执行。
     */
    public static void ApplyFuncToEachMonster(Function<AbstractTeamMonster, Boolean> func) {
        for (AbstractTeamMonster m : GetMonsters()) {
            if (func.apply(m))
                return;
        }
    }

    private void usePreBattleAction() {
        if (!AbstractDungeon.loading_post_combat) {
            GenericHelper.info("use pre battle action");
            Monsters.clear();
            BattleMonsters.forEach((m) -> {
                AbstractTeamMonster tmp = MonsterManager.GetMonsterInstance(m.id);
                Monsters.add(tmp);
                tmp.init();
                tmp.usePreBattleAction();
            });
            for (int i = 0; i < Monsters.size(); i++) {
                GenericHelper.MoveMonster(Monsters.get(i), (i - 1) * 200.0F * Settings.scale, AbstractDungeon.floorY);
            }
        }
    }

    private void atEndOfTurn() {
        for (AbstractTeamMonster m : this.Monsters) {
            m.applyEndOfTurnTriggers();
        }
        queueMonsters();
    }

    private void atStartOfTurn() {
        for (AbstractTeamMonster m : this.Monsters) {
            if (!m.hasPower("Barricade")) {
                m.loseBlock();
            }
            m.applyStartOfTurnPowers();
            m.applyStartOfTurnPostDrawPowers();
        }
    }

    /**
     * 使用序号获取战斗中怪物。从左向右。可以为负，负数从右往左计算。
     * 
     * @param index
     * @return AbstractTeamMonster
     */
    public AbstractTeamMonster GetMonsterByIndex(int index) {
        if (index < 0) {
            return this.Monsters.get(this.Monsters.size() + index);
        }
        return this.Monsters.get(index);
    }

    /**
     * 使用ID获取战斗中怪物。只获取从左向右第一个。
     * 
     * @param id
     * @return AbstractTeamMonster
     */
    public AbstractTeamMonster GetMonsterByID(String id) {
        for (AbstractTeamMonster m : Monsters) {
            if (id.equals(m.id)) {
                return m;
            }
        }
        return null;
    }

    public void queueMonsters() {
        for (AbstractTeamMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                AbstractDungeon.actionManager.monsterQueue.add(new MonsterQueueItem(m));
            }
        }
    }

    public void showIntent() {
        for (AbstractTeamMonster m : this.Monsters) {
            GenericHelper.info("try to show intent of:" + m.name);
            m.createIntent();
        }
        GenericHelper.info("intent showed");
    }

    public void applyPowers() {
        for (AbstractTeamMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                m.applyPowers();
            }
        }
    }

    public void init() {
        for (AbstractTeamMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                m.init();
            }
        }
    }

    public void update() {
        for (int i = this.Monsters.size() - 1; i >= 0; i--) {
            AbstractTeamMonster m = this.Monsters.get(i);
            m.update();
            if (m.isDead) {
                this.Monsters.remove(m);
            }
        }
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.DEATH) {
            this.hoveredMonster = null;

            for (AbstractTeamMonster m : this.Monsters) {
                if (!m.isDying) {
                    m.hb.update();
                }
                if (m.hb.hovered || m.healthHb.hovered) {
                    this.hoveredMonster = m;
                }
            }

            if (this.hoveredMonster == null) {
                AbstractDungeon.player.hoverEnemyWaitTimer = -1.0F;
            }
        } else {
            this.hoveredMonster = null;
        }
    }

    private void updateAnimations() {
        for (AbstractTeamMonster m : this.Monsters) {
            m.updatePowers();
        }
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.currMapNode != null) {
            if (this.hoveredMonster != null && !this.hoveredMonster.isDead
                    && (!AbstractDungeon.isScreenUp || PeekButton.isPeeking)) {
                this.hoveredMonster.renderTip(sb);
            }

            for (AbstractTeamMonster m : this.Monsters) {
                m.render(sb);
            }
        }
    }

    public void renderReticle(SpriteBatch sb) {
        for (AbstractTeamMonster m : this.Monsters) {
            if (!m.isDying) {
                m.renderReticle(sb);
            }
        }
    }

    @Override
    public void onLoad(ArrayList<ArrayList<MonsterSaveInfo>> monsters) {
        if (monsters.size() == 2) {
            monsters.get(0).forEach((info) -> {
                AbstractTeamMonster m = MonsterManager.GetMonsterInstance(info.id);
                for (int i = 0; i < info.upgradedTimes; i++) {
                    m.upgrade(i);
                }
                BattleMonsters.add(m);
            });
            monsters.get(1).forEach((info) -> {
                AbstractTeamMonster m = MonsterManager.GetMonsterInstance(info.id);
                for (int i = 0; i < info.upgradedTimes; i++) {
                    m.upgrade(i);
                }
                WaitingMonsters.add(m);
            });
        }
    }

    @Override
    public ArrayList<ArrayList<MonsterSaveInfo>> onSave() {
        ArrayList<ArrayList<MonsterSaveInfo>> monsters = new ArrayList<>();
        ArrayList<MonsterSaveInfo> battle = new ArrayList<>();
        BattleMonsters.forEach((m) -> {
            battle.add(new MonsterSaveInfo(m.id, m.upgradedTimes));
        });
        monsters.add(battle);
        ArrayList<MonsterSaveInfo> waiting = new ArrayList<>();
        WaitingMonsters.forEach((m) -> {
            waiting.add(new MonsterSaveInfo(m.id, m.upgradedTimes));
        });
        monsters.add(waiting);
        return monsters;
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "<class>")
    public static class MonstersFields {
        public static SpireField<TeamMonsterGroup> Monsters = new SpireField<>(() -> new TeamMonsterGroup());
    }

    @SpirePatch(clz = AbstractRoom.class, method = "update")
    public static class MonsterUpdatePatch {
        public static void Postfix(AbstractRoom _inst) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null) {
                (MonstersFields.Monsters.get(p)).update();
                (MonstersFields.Monsters.get(p)).updateAnimations();
            }
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class MonsterRenderPatch {
        @SpireInsertPatch(rloc = 13)
        public static void Insert(AbstractRoom _inst, SpriteBatch sb) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null)
                MonstersFields.Monsters.get(p).render(sb);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class MonsterStartOfTurnPatch {
        public static void Postfix(AbstractPlayer _inst) {
            (MonstersFields.Monsters.get(_inst)).atStartOfTurn();
            if (AutoEndTurnOption.isAutoEnding()) {
                GenericHelper.info("检测到自动结束回合");
                AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class MonsterStartOfBattlePatch {
        public static void Postfix(AbstractPlayer _inst) {
            (MonstersFields.Monsters.get(_inst)).usePreBattleAction();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "applyEndOfTurnRelics")
    public static class MonsterEndOfTurnPatch {
        public static void Postfix(AbstractRoom _inst) {
            (MonstersFields.Monsters.get(AbstractDungeon.player)).atEndOfTurn();
        }
    }

    @SpirePatch(clz = BattleStartEffect.class, method = "update")
    public static class ShowSkillBarPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(BattleStartEffect _inst) {
            TeamMonsterGroup tmp = MonstersFields.Monsters.get(AbstractDungeon.player);
            if (tmp.Monsters.size() > 0) {
                for (AbstractTeamMonster o : tmp.Monsters) {
                    o.showHealthBar();
                    o.healthBarRevivedEvent();
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "showHealthBar");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = BattleStartEffect.class, method = "update")
    public static class ShowIntentStartBattlePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(BattleStartEffect _inst) {
            Inst().showIntent();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "showIntent");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = PlayerTurnEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShowIntentPatch {
        public static void Postfix(PlayerTurnEffect _inst) {
            Inst().showIntent();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "onModifyPower")
    public static class OnModifyPowerPatch {
        public static void Postfix() {
            if (AbstractDungeon.player != null && AbstractDungeon.getMonsters() != null)
                Inst().applyPowers();
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "init")
    public static class RollMovePatch {
        public static void Postfix(MonsterGroup _inst) {
            Inst().init();
        }
    }

    public class MonsterSaveInfo {
        public String id;
        public int upgradedTimes;

        public MonsterSaveInfo(String id, int upgradedTimes) {
            this.id = id;
            this.upgradedTimes = upgradedTimes;
        }
    }
}

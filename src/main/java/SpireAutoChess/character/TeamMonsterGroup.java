package SpireAutoChess.character;

import java.util.ArrayList;
import java.util.function.Function;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterQueueItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;

import SpireAutoChess.helper.GenericHelper;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.ISubscriber;
import javassist.CtBehavior;

public class TeamMonsterGroup implements ISubscriber, CustomSavable<ArrayList<String>> {
    public final ArrayList<AbstractMonster> Monsters = new ArrayList<>();
    private AbstractMonster hoveredMonster;

    public static int maxSize = 5;

    public static TeamMonsterGroup Inst() {
        return MonstersFields.Monsters.get(AbstractDungeon.player);
    }

    public TeamMonsterGroup(ArrayList<AbstractMonster> input) {
        this.hoveredMonster = null;
        this.Monsters.addAll(input);
        BaseMod.subscribe(this);
        BaseMod.addSaveField("TeamMonsters", this);
    }

    public TeamMonsterGroup() {
        this(new ArrayList<>());
    }

    public void addMonster(AbstractMonster... o) {
        for (AbstractMonster m : o) {
            this.Monsters.add(m);
        }
    }

    public static ArrayList<AbstractMonster> GetMonsters() {
        return MonstersFields.Monsters.get(AbstractDungeon.player).Monsters;
    }

    public static void ApplyFuncToEachMonster(Function<AbstractMonster, Boolean> func) {
        for (AbstractMonster m : GetMonsters()) {
            if (func.apply(m))
                return;
        }
    }

    private void usePreBattleAction() {
        if (!AbstractDungeon.loading_post_combat) {
            for (int i = 0; i < Monsters.size(); i++) {
                AbstractMonster m = Monsters.get(i);
                m.usePreBattleAction();
                GenericHelper.addToBot(new RollMoveAction(m));
                GenericHelper.MoveMonster(m, i * 250.0F * Settings.scale, AbstractDungeon.floorY);
            }
        }
    }

    private void atEndOfTurn() {
        for (AbstractMonster m : this.Monsters) {
            m.applyEndOfTurnTriggers();
            // for (AbstractPower p : m.powers) {
            // p.atEndOfRound();
            // }
        }
        queueMonsters();
    }

    private void atStartOfTurn() {
        for (AbstractMonster m : this.Monsters) {
            if (!m.hasPower("Barricade")) {
                m.loseBlock();
            }
            m.applyStartOfTurnPowers();
            m.applyStartOfTurnPostDrawPowers();
        }
    }

    public AbstractMonster GetMonsterByIndex(int index) {
        if (index < 0) {
            return this.Monsters.get(this.Monsters.size() - index);
        }
        return this.Monsters.get(index);
    }

    public AbstractMonster GetMonsterByID(String id) {
        for (AbstractMonster m : Monsters) {
            if (id.equals(m.id)) {
                return m;
            }
        }
        return null;
    }

    public void queueMonsters() {
        for (AbstractMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                AbstractDungeon.actionManager.monsterQueue.add(new MonsterQueueItem(m));
            }
        }
    }

    public void ShowIntent() {
        for (AbstractMonster m : this.Monsters) {
            m.createIntent();
        }
        GenericHelper.info("intent showed");
    }

    public void applyPowers() {
        for (AbstractMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                m.applyPowers();
            }
        }
    }

    public void Init() {
        for (AbstractMonster m : this.Monsters) {
            if (!m.isDeadOrEscaped() || m.halfDead) {
                m.init();
            }
        }
    }

    public void update() {
        for (int i = this.Monsters.size() - 1; i >= 0; i--) {
            AbstractMonster m = this.Monsters.get(i);
            m.update();
            if (m.isDead) {
                this.Monsters.remove(m);
            }
        }
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.DEATH) {
            this.hoveredMonster = null;

            for (AbstractMonster m : this.Monsters) {
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
        for (AbstractMonster m : this.Monsters) {
            m.updatePowers();
        }
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.currMapNode != null) {
            if (this.hoveredMonster != null && !this.hoveredMonster.isDead
                    && (!AbstractDungeon.isScreenUp || PeekButton.isPeeking)) {
                this.hoveredMonster.renderTip(sb);
            }

            for (AbstractMonster m : this.Monsters) {
                m.render(sb);
            }
        }
    }

    public void renderReticle(SpriteBatch sb) {
        for (AbstractMonster m : this.Monsters) {
            if (!m.isDying) {
                m.renderReticle(sb);
            }
        }
    }

    @Override
    public void onLoad(ArrayList<String> monsters) {

    }

    @Override
    public ArrayList<String> onSave() {
        ArrayList<String> monsters = new ArrayList<>();
        for (AbstractMonster m : this.Monsters) {
            monsters.add(m.id);
        }
        return monsters;
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "<class>")
    public static class MonstersFields {
        public static SpireField<TeamMonsterGroup> Monsters = new SpireField<TeamMonsterGroup>(() -> {
            return new TeamMonsterGroup();
        });

        public MonstersFields() {
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "update")
    public static class MonsterUpdatePatch {
        public MonsterUpdatePatch() {
        }

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
        public MonsterRenderPatch() {
        }

        @SpireInsertPatch(rloc = 13)
        public static void Insert(AbstractRoom _inst, SpriteBatch sb) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null)
                MonstersFields.Monsters.get(p).render(sb);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class MonsterStartOfTurnPatch {
        public MonsterStartOfTurnPatch() {
        }

        public static void Postfix(AbstractPlayer _inst) {
            (MonstersFields.Monsters.get(_inst)).atStartOfTurn();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class MonsterStartOfBattlePatch {
        public MonsterStartOfBattlePatch() {
        }

        public static void Postfix(AbstractPlayer _inst) {
            (MonstersFields.Monsters.get(_inst)).usePreBattleAction();
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "applyEndOfTurnRelics")
    public static class MonsterEndOfTurnPatch {
        public MonsterEndOfTurnPatch() {
        }

        public static void Postfix(AbstractRoom _inst) {
            (MonstersFields.Monsters.get(AbstractDungeon.player)).atEndOfTurn();
        }
    }

    @SpirePatch(clz = BattleStartEffect.class, method = "update")
    public static class ShowSkillBarPatch {
        public ShowSkillBarPatch() {
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(BattleStartEffect _inst) {
            TeamMonsterGroup tmp = MonstersFields.Monsters.get(AbstractDungeon.player);
            if (tmp.Monsters.size() > 0) {
                for (AbstractMonster o : tmp.Monsters) {
                    o.showHealthBar();
                    o.healthBarRevivedEvent();
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

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
            Inst().ShowIntent();
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "showIntent");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = PlayerTurnEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShowIntentPatch {
        public static void Postfix(PlayerTurnEffect _inst) {
            Inst().ShowIntent();
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
            Inst().Init();
        }
    }
}

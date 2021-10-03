package SpireAutoChess.monsters.uncommon;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TBanditChild extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TBanditChild.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 30;
    public static final int POWER_AMT = 60;
    public static final int DAMAGE_1 = 5;
    public static final int DAMAGE_AMT_1 = 2;

    public TBanditChild() {
        super(NAME, ID, MAX_HP, -5.0F, -4.0F, 190.0F, 180.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.rarity = MonsterRarity.UNCOMMON;
        this.actNum = 2;

        this.addMoveInfoOnlyMagic(POWER_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1), 0, DAMAGE_AMT_1);

        this.maxUpgradeTimes = 2;

        this.loadAnimation("images/monsters/theCity/pointy/skeleton.atlas",
                "images/monsters/theCity/pointy/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.state.setTimeScale(1.0F);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        if (AbstractDungeon.player.gold >= getMagicNumber(0)) {
            GenericHelper.addToBotAbstract(() -> {
                ApplyPowerToSelf(new StrengthPower(this, AbstractDungeon.player.gold / getMagicNumber(0)));
            });
        }
    }

    @Override
    protected void getMove(int num) {
        this.setNextMove((byte) 1, Intent.ATTACK, getDamage(1), () -> {
            DamageFront(getDamageInfo(1), getMagicNumber(1));
            return false;
        });
    }

    @Override
    public void upgrade(int level) {
        switch (level) {
            case 0:
                this.changeDamage(1, 4);
                break;
            case 1:
                this.changeMagicNumber(0, -25);
                break;
        }
        this.upgradedTimes++;
    }
}
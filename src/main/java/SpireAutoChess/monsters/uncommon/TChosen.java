package SpireAutoChess.monsters.uncommon;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;

import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.powers.THexPower;

public class TChosen extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TChosen.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 30;
    public static final int POWER_AMT = 1;
    public static final int DAMAGE_1 = 8;
    public static final int DAMAGE_2 = 6;
    public static final int WEAK_AMT = 1;

    private int moveIndex = 0;
    private int maxMoveIndex = 2;

    public TChosen() {
        super(NAME, ID, MAX_HP, 5.0F, -10.0F, 200.0F, 280.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.rarity = MonsterRarity.UNCOMMON;
        this.actNum = 2;

        this.addMoveInfoOnlyMagic(POWER_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1));
        this.addMoveInfo(new DamageInfo(this, DAMAGE_2), 0, WEAK_AMT);

        this.maxUpgradeTimes = 2;

        this.loadAnimation("images/monsters/theCity/chosen/skeleton.atlas",
                "images/monsters/theCity/chosen/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.stateData.setMix("Attack", "Idle", 0.2F);
        this.state.setTimeScale(0.8F);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        // TODO
        ApplyPowerToSelf(new THexPower(this, getMagicNumber(0)));
    }

    @Override
    protected void getMove(int num) {
        switch (this.moveIndex % this.maxMoveIndex) {
            case 0:
                this.setNextMove((byte) 1, Intent.BUFF, 0, () -> {
                    DamageFront(getDamageInfo(1));
                    return false;
                });
                break;
            case 1:
                this.setNextMove((byte) 2, Intent.ATTACK, getDamage(2), () -> {
                    DamageFront(getDamageInfo(2));
                    ApplyPowerToOther((m) -> new WeakPower(m, getMagicNumber(2), false),
                            GenericHelper.getFrontMonster());
                    return false;
                });
                break;
        }
        moveIndex++;
    }

    @Override
    public void upgrade(int level) {
        switch (level) {
            case 0:
                this.changeDamage(1, 2);
                break;
            case 1:
                this.changeMagicNumber(0, 2);
                break;
        }
        this.upgradedTimes++;
    }
}
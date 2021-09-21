package SpireAutoChess.monsters.common;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TLouseNormal extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TLouseNormal.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 20;
    public static final int DAMAGE_1 = 6;
    public static final int POWER_AMT = 2;

    private int moveIndex = 0;
    private int maxMoveIndex = 2;

    public TLouseNormal() {
        super(NAME, ID, MAX_HP, 0.0F, -5.0F, 180.0F, 140.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange(1);

        this.addMoveInfo(0, POWER_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1));

        this.loadAnimation("images/monsters/theBottom/louseRed/skeleton.atlas",
                "images/monsters/theBottom/louseRed/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        ;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
    }

    @Override
    protected void getMove(int num) {
        switch (this.moveIndex % this.maxMoveIndex) {
            case 0:
                this.setNextMove((byte) 0, Intent.BUFF, 0, () -> {
                    ApplyPowerToOther((m) -> new StrengthPower(m, getMagicNumber(0)), -1);
                    return false;
                });
                break;
            case 1:
                this.setNextMove((byte) 1, Intent.ATTACK, getDamage(1), () -> {
                    DamageFront(getDamageInfo(1));
                    return false;
                });
                break;
        }
        moveIndex++;
    }
}
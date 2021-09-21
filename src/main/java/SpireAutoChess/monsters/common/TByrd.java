package SpireAutoChess.monsters.common;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TByrd extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TByrd.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 30;
    public static final int DAMAGE_1 = 1;
    public static final int DAMAGE_AMT_1 = 4;
    public static final int DAMAGE_2 = 8;
    public static final int POWER_AMT = 1;

    private int moveIndex = 0;
    private int maxMoveIndex = 3;

    public TByrd() {
        super(NAME, ID, MAX_HP, 0.0F, 50.0F, 240.0F, 180.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange(2);

        this.addMoveInfo(new DamageInfo(this, DAMAGE_1), 0, DAMAGE_AMT_1);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_2));
        this.addMoveInfo(0, POWER_AMT);

        this.loadAnimation("images/monsters/theCity/byrd/flying.atlas", "images/monsters/theCity/byrd/flying.json",
                1.0F);
        TrackEntry e = this.state.setAnimation(0, "idle_flap", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.moveIndex = 0;
    }

    @Override
    protected void getMove(int num) {
        switch (this.moveIndex % this.maxMoveIndex) {
            case 0:
                this.setNextMove((byte) 0, Intent.ATTACK, getDamage(0), getMagicNumber(0), () -> {
                    for (int i = 0; i < getMagicNumber(0); i++)
                        DamageFront(getDamageInfo(0));
                    return false;
                });
                break;
            case 1:
                this.setNextMove((byte) 1, Intent.ATTACK, getDamage(1), () -> {
                    DamageFront(getDamageInfo(1));
                    return false;
                });
                break;
            case 2:
                this.setNextMove((byte) 2, Intent.BUFF, () -> {
                    ApplyPowerToSelf(new StrengthPower(this, getMagicNumber(2)));
                    return false;
                });
        }
        moveIndex++;
    }
}
package SpireAutoChess.monsters.common;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ThornsPower;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TSpiker extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TSpiker.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 10;
    public static final int POWER_AMT = 2;
    public static final int BLOCK_AMT = 8;

    private int moveIndex = 0;
    private int maxMoveIndex = 2;

    public TSpiker() {
        super(NAME, ID, MAX_HP, 0.0F, -5.0F, 180.0F, 140.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.rarity = MonsterRarity.COMMON;
        this.race = MonsterRace.MACHINE;
        this.actNum = 2;

        this.addMoveInfoOnlyMagic(POWER_AMT);
        this.addMoveInfoOnlyBlock(BLOCK_AMT);

        this.loadAnimation("images/monsters/theForest/spiker/skeleton.atlas",
                "images/monsters/theForest/spiker/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
    }

    @Override
    protected void getMove(int num) {
        switch (this.moveIndex % this.maxMoveIndex) {
            case 0:
                this.setNextMove((byte) 0, Intent.BUFF, () -> {
                    ApplyPowerToOther((m) -> new ThornsPower(m, getMagicNumber(0)), -1);
                    return false;
                });
                break;
            case 1:
                this.setNextMove((byte) 1, Intent.DEFEND, () -> {
                    AddBlockToSelf(getBlock(1));
                    return false;
                });
                break;
        }
        moveIndex++;
    }
}
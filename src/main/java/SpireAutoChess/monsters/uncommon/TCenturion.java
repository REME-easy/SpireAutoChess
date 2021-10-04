package SpireAutoChess.monsters.uncommon;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.powers.TJuggernautPower;

public class TCenturion extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TCenturion.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 30;
    public static final int POWER_AMT = 4;
    public static final int DAMAGE_1 = 8;
    public static final int BLOCK_1 = 8;

    public TCenturion() {
        super(NAME, ID, MAX_HP, -14.0F, -20.0F, 250.0F, 330.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.rarity = MonsterRarity.UNCOMMON;
        this.actNum = 2;

        this.addMoveInfoOnlyMagic(POWER_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1), BLOCK_1, 0);

        this.maxUpgradeTimes = 2;

        this.loadAnimation("images/monsters/theCity/tank/skeleton.atlas", "images/monsters/theCity/tank/skeleton.json",
                1.0F);
        TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.state.setTimeScale(0.8F);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        ApplyPowerToSelf(new TJuggernautPower(this, getMagicNumber(0)));
    }

    @Override
    protected void getMove(int num) {
        this.setNextMove((byte) 1, Intent.ATTACK_DEFEND, getDamage(1), () -> {
            DamageFront(getDamageInfo(1));
            AddBlockToSelf(getBlock(1));
            return false;
        });
    }

    @Override
    public void upgrade(int level) {
        switch (level) {
            case 0:
                this.changeBlock(1, 4);
                break;
            case 1:
                this.changeDamage(1, 4);
                break;
        }
        this.upgradedTimes++;
    }
}
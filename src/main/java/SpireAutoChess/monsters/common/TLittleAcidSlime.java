package SpireAutoChess.monsters.common;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TLittleAcidSlime extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TLittleAcidSlime.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 20;
    public static final int DAMAGE_1 = 6;
    public static final int POWER_AMT = 2;

    public TLittleAcidSlime() {
        super(NAME, ID, MAX_HP, 0.0F, -4.0F, 130.0F, 100.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.rarity = MonsterRarity.COMMON;
        this.race = MonsterRace.MACHINE;
        this.actNum = 2;

        this.addMoveInfoOnlyMagic(POWER_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1));

        this.maxUpgradeTimes = 2;
        this.loadAnimation("images/monsters/theBottom/slimeS/skeleton.atlas",
                "images/monsters/theBottom/slimeS/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.addListener(new SlimeAnimListener());
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        // ApplyPowerToSelf(new TExplosivePower(this, getMagicNumber(0)));
    }

    @Override
    protected void getMove(int num) {
        this.setNextMove((byte) 1, Intent.ATTACK, getDamage(1), () -> {
            DamageFront(getDamageInfo(1));
            return false;
        });
    }

    @Override
    public void upgrade(int level) {
        switch (level) {
            case 0:
                break;
            case 1:
                this.changeMagicNumber(0, 1);
                break;
        }
        this.upgradedTimes++;
    }
}
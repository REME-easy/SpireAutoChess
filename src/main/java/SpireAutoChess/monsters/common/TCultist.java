package SpireAutoChess.monsters.common;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.RitualPower;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class TCultist extends AbstractTeamMonster {
    public static final String ID = ChessPlayerModCore.MakePath(TCultist.class.getSimpleName());
    public static final MonsterStrings STRINGS = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = STRINGS.NAME;
    public static final String[] MOVES = STRINGS.MOVES;
    public static final String[] DIALOG = STRINGS.DIALOG;
    public static final int MAX_HP = 60;
    public static final int DAMAGE_1 = 6;
    public static final int RITUAL_AMT = 3;

    private boolean firstMove;

    public TCultist() {
        super(NAME, ID, MAX_HP, -8.0F, 10.0F, 230.0F, 240.0F, (String) null, 0.0F, 0.0F);
        this.setDescriptionRange();
        this.dialogX = this.drawX;
        this.dialogY = this.drawY + 150.0F;

        this.firstMove = true;

        this.addMoveInfo(0, RITUAL_AMT);
        this.addMoveInfo(new DamageInfo(this, DAMAGE_1));

        this.maxUpgradeTimes = 2;

        this.loadAnimation("images/monsters/theBottom/cultist/skeleton.atlas",
                "images/monsters/theBottom/cultist/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "waving", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
    }

    @Override
    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            this.setNextMove((byte) 0, Intent.BUFF, 0, () -> {
                playSfx();
                // addToBot(new TalkAction(this, DIALOG[0]));
                ApplyPowerToSelf(new RitualPower(this, getMagicNumber(0), true));
                return false;
            });
        } else {
            this.setNextMove((byte) 1, Intent.ATTACK, getDamage(1), () -> {
                DamageFront(getDamageInfo(1));
                return false;
            });
        }
    }

    @Override
    public void upgrade(int level) {
        switch (level) {
            case 0:
            case 1:
                this.changeMagicNumber(0, 2);
                break;
        }
        this.upgradedTimes++;
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            addToBot(new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            addToBot(new SFXAction("VO_CULTIST_1B"));
        } else {
            addToBot(new SFXAction("VO_CULTIST_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_CULTIST_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_CULTIST_2B");
        } else {
            CardCrawlGame.sound.play("VO_CULTIST_2C");
        }

    }

    public void die() {
        this.playDeathSfx();
        this.state.setTimeScale(0.1F);
        this.useShakeAnimation(5.0F);

        super.die();
    }
}
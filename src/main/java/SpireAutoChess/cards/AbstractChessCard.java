package SpireAutoChess.cards;

import static SpireAutoChess.character.ChessPlayer.Enums.CHESS_PLAYER_CARD;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;

public abstract class AbstractChessCard extends CustomCard {
    public final CardStrings strings;

    public int baseSecondaryMagicNum;
    public int SecondaryMagicNum;
    public boolean upgradedSecondaryMagicNum;
    public boolean isSecondaryMagicNumModified;
    public boolean isEnchanting = false;

    public AbstractChessCard(String ID, CardStrings strings, int COST, CardType TYPE, CardRarity RARITY,
            CardTarget TARGET) {
        this(ID, false, strings, COST, TYPE, RARITY, TARGET);
    }

    public AbstractChessCard(String ID, boolean useTmpArt, CardStrings strings, int COST, CardType TYPE,
            CardRarity RARITY, CardTarget TARGET) {
        super(ID, strings.NAME, useTmpArt ? GetTmpImgPath(TYPE) : GetImgPath(TYPE, ID), COST, strings.DESCRIPTION, TYPE,
                CHESS_PLAYER_CARD, RARITY, TARGET);
        this.strings = strings;
    }

    private static String GetTmpImgPath(CardType t) {
        String type;
        switch (t) {
            case ATTACK:
                type = "attack";
                break;
            case POWER:
                type = "power";
                break;
            case STATUS:
            case CURSE:
            case SKILL:
                type = "skill";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + t);
        }
        return String.format("ChessPlayerResources/img/card/test_%s.png", type);
    }

    private static String GetImgPath(CardType t, String name) {
        String type;
        switch (t) {
            case ATTACK:
                type = "attack";
                break;
            case POWER:
                type = "power";
                break;
            case STATUS:
                type = "status";
                break;
            case CURSE:
                type = "curse";
                break;
            case SKILL:
                type = "skill";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + t);
        }
        return String.format("ChessPlayerResources/img/card/%s/%s.png", type, name.replace("ChessPlayer_", ""));
    }

    protected void setupDamage(int amt) {
        this.baseDamage = amt;
        this.damage = amt;
    }

    protected void setupBlock(int amt) {
        this.baseBlock = amt;
        this.block = amt;
    }

    protected void setupMagicNumber(int amt) {
        this.baseMagicNumber = amt;
        this.magicNumber = amt;
    }

    protected void setupSecondMagicNumber(int amt) {
        this.baseSecondaryMagicNum = amt;
        this.SecondaryMagicNum = amt;
    }

    protected void upgradeSecondMagicNumber(int amount) {
        this.baseSecondaryMagicNum += amount;
        this.SecondaryMagicNum = this.baseSecondaryMagicNum;
        this.upgradedSecondaryMagicNum = true;
    }

    @Override
    public void use(AbstractPlayer arg0, AbstractMonster arg1) {
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
    }

    public void limitedUpgrade() {

    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            limitedUpgrade();
            this.initializeDescription();
        }
    }
}

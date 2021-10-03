package SpireAutoChess.cards;

import com.megacrit.cardcrawl.localization.CardStrings;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.EventHelper.TeamMonsterChangePositionSubscriber;
import SpireAutoChess.helper.EventHelper.TeamMonsterRemoveSubscriber;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class AbstractEnchantingCard extends AbstractChessCard
        implements TeamMonsterChangePositionSubscriber, TeamMonsterRemoveSubscriber {

    public AbstractEnchantingCard(String ID, boolean useTmpArt, CardStrings strings, CardType TYPE, CardRarity RARITY,
            CardTarget TARGET) {
        super(ID, useTmpArt, strings, -2, TYPE, RARITY, TARGET);
        this.isEnchanting = true;
    }

    public AbstractTeamMonster GetEquippedMonster() {
        return TeamMonsterGroup.Inst().GetMonsterByIndex(this.misc);
    }

    @Override
    public void applyPowers() {
        this.misc = this.SecondaryMagicNum;
        super.applyPowers();
    }

    @Override
    public void OnRemoveMonster(int position) {
        if (this.misc == position) {

        }
    }

    @Override
    public void OnChangPosition(int source, int target) {
        if (this.misc == source || this.misc == target) {

        }
    }

}
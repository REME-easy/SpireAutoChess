package SpireAutoChess.relics;

import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.screens.OrganizationScreen;
import basemod.abstracts.CustomRelic;

public class MonsterOrganization extends CustomRelic {
    private static final String ID = ChessPlayerModCore.MakePath(MonsterOrganization.class.getSimpleName());

    public MonsterOrganization() {
        super(ID, ImageMaster.loadImage("ChessPlayerResources/img/relics/HoneyCake.png"), RelicTier.SPECIAL,
                LandingSound.CLINK);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void update() {
        super.update();
        if (this.hb.hovered && InputHelper.justReleasedClickRight) {
            OrganizationScreen.Inst().open();
        }
    }
}

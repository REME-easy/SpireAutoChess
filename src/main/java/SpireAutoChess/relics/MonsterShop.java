package SpireAutoChess.relics;

import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import SpireAutoChess.modcore.ChessPlayerModCore;
import SpireAutoChess.screens.MonsterShopScreen;
import basemod.abstracts.CustomRelic;

public class MonsterShop extends CustomRelic {
    private static final String ID = ChessPlayerModCore.MakePath(MonsterShop.class.getSimpleName());

    public MonsterShop() {
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
            MonsterShopScreen.Inst().open();
        }
    }
}

package SpireAutoChess.relics;

import SpireAutoChess.modcore.ChessPlayerModCore;
import basemod.abstracts.CustomRelic;

public class CeobeBirthright extends CustomRelic {
    private static final String ID = CeobeBirthright.class.getSimpleName();

    public static boolean HaveCeobeBirthright = false;

    public CeobeBirthright() {
        super(ChessPlayerModCore.MakePath(ID), "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        super.onEquip();
        HaveCeobeBirthright = true;
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        HaveCeobeBirthright = false;
    }
}

package SpireAutoChess.character;

import static SpireAutoChess.character.ChessPlayer.Enums.CHESS_PLAYER;
import static SpireAutoChess.character.ChessPlayer.Enums.CHESS_PLAYER_CARD;
import static SpireAutoChess.modcore.ChessPlayerModCore.GetCharColor;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.relics.BustedCrown;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import basemod.abstracts.CustomPlayer;

public class ChessPlayer extends CustomPlayer {
    private static final String MY_CHARACTER_SHOULDER = "ChessPlayerResources/img/char/shoulder.png";
    private static final float[] LAYER_SPEED = new float[] { -40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F,
            -5.0F, 0.0F };
    private static final String[] ORB_TEXTURES = new String[] { "ChessPlayerResources/img/UI/orb/layer5.png",
            "ChessPlayerResources/img/UI/orb/layer4.png", "ChessPlayerResources/img/UI/orb/layer3.png",
            "ChessPlayerResources/img/UI/orb/layer2.png", "ChessPlayerResources/img/UI/orb/layer1.png",
            "ChessPlayerResources/img/UI/orb/layer6.png", "ChessPlayerResources/img/UI/orb/layer5d.png",
            "ChessPlayerResources/img/UI/orb/layer4d.png", "ChessPlayerResources/img/UI/orb/layer3d.png",
            "ChessPlayerResources/img/UI/orb/layer2d.png", "ChessPlayerResources/img/UI/orb/layer1d.png" };
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack
            .getCharacterString("ChessPlayer_Player");

    public ChessPlayer(String name) {
        super(name, CHESS_PLAYER, ORB_TEXTURES, "ChessPlayerResources/img/UI/orb/vfx.png", LAYER_SPEED, null, null);
        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 150.0F * Settings.scale);
        this.initializeClass("ChessPlayerResources/img/char/empty.png", MY_CHARACTER_SHOULDER, MY_CHARACTER_SHOULDER,
                null, this.getLoadout(), 0.0F, 0.0F, 5.0F, 5.0F, new EnergyManager(0));
    }

    @Override
    public void applyStartOfCombatLogic() {
        super.applyStartOfCombatLogic();
        // GenericHelper.addToBot(new AbstractGameAction() {
        // @Override
        // public void update() {
        // // AbstractDungeon.overlayMenu.hideCombatPanels();
        // hideHealthBar();
        // isDone = true;
        // }
        // });
    }

    @Override
    public void applyStartOfTurnPowers() {
        super.applyStartOfTurnPowers();
        // GenericHelper.addToBot(new EndlessQueueMonstersAction());
    }

    public ArrayList<String> getStartingDeck() {
        ArrayList<String> val = new ArrayList<>();
        val.add(Strike_Blue.ID);
        val.add(Strike_Blue.ID);
        val.add(Strike_Blue.ID);
        val.add(Strike_Blue.ID);
        val.add(Strike_Blue.ID);
        return val;
    }

    public ArrayList<String> getStartingRelics() {
        ArrayList<String> val = new ArrayList<>();
        val.add(BustedCrown.ID);
        return val;
    }

    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(characterStrings.NAMES[0], characterStrings.TEXT[0], 50, 50, 0, 99, 0, this,
                this.getStartingRelics(), this.getStartingDeck(), false);
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return characterStrings.NAMES[0];
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return CHESS_PLAYER_CARD;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Strike_Blue();
    }

    @Override
    public Color getCardTrailColor() {
        return GetCharColor();
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public void playDeathAnimation() {
        state.setAnimation(0, "Die", false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    @Override
    public String getLocalizedCharacterName() {
        return characterStrings.NAMES[0];
    }

    @Override
    public AbstractPlayer newInstance() {
        return new ChessPlayer(this.name);
    }

    @Override
    public String getSpireHeartText() {
        return characterStrings.TEXT[1];
    }

    @Override
    public Color getSlashAttackColor() {
        return GetCharColor();
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[1];
    }

    @Override
    public Color getCardRenderColor() {
        return GetCharColor();
    }

    @Override
    public void renderHealth(SpriteBatch sb) {
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] { AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL };
    }

    public static class Enums {
        @SpireEnum
        public static PlayerClass CHESS_PLAYER;

        @SpireEnum(name = "CHESS_PLAYER_GREY")
        public static AbstractCard.CardColor CHESS_PLAYER_CARD;

        @SpireEnum(name = "CHESS_PLAYER_GREY")
        public static CardLibrary.LibraryType CHESS_PLAYER_LIB;
    }
}

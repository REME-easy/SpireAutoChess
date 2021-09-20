package SpireAutoChess.screens;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.mainMenu.HorizontalScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;

import SpireAutoChess.helper.CustomTipRenderer;
import SpireAutoChess.helper.MonsterManager;

public class OrganizationScreen implements ScrollBarListener {
    public static OrganizationScreen _inst;

    public static OrganizationScreen Inst() {
        if (_inst == null)
            _inst = new OrganizationScreen();
        return _inst;
    }

    public ArrayList<AbstractMonster> teamMonsters;
    public ArrayList<Hitbox> monstersHitboxes;

    private boolean grabbedScreen = false;
    private float grabStartX = 0.0F;
    private float scrollX;
    private float targetX;
    private float currentWidth;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean shouldShowScrollBar;
    private HorizontalScrollBar scrollBar;
    public ConfirmButton confirmButton;
    public MonsterInfoScreen infoScreen;

    public OrganizationScreen() {
        this.scrollX = Settings.WIDTH - 300.0F * Settings.xScale;
        this.targetX = this.scrollX;
        this.scrollLowerBound = (float) Settings.WIDTH - 300.0F * Settings.xScale;
        this.scrollUpperBound = 2400.0F * Settings.scale;
        this.shouldShowScrollBar = false;
        this.confirmButton = new ConfirmButton();
        this.targetX = 0.0F;
        this.currentWidth = 0.0F;
        this.teamMonsters = new ArrayList<>();
        this.monstersHitboxes = new ArrayList<>();
        this.scrollBar = new HorizontalScrollBar(this, (float) Settings.WIDTH / 2.0F,
                50.0F * Settings.scale + HorizontalScrollBar.TRACK_H / 2.0F,
                (float) Settings.WIDTH - 256.0F * Settings.scale);
        this.infoScreen = new MonsterInfoScreen();
    }

    public void open(AbstractMonster... m) {
        this.addMonsters(m);
        confirmButton.isDisabled = false;
        overlayMenu.proceedButton.hide();
        overlayMenu.cancelButton.hide();
        overlayMenu.hideCombatPanels();
        confirmButton.hideInstantly();
        confirmButton.show();
    }

    public void reopen() {
    }

    public void end() {
        this.teamMonsters.clear();
        this.monstersHitboxes.clear();
    }

    public void addMonsters(AbstractMonster... m) {
        for (AbstractMonster monster : m) {
            this.teamMonsters.add(monster);
            currentWidth += monster.hb_w;
        }
        this.checkIfShowScrollBar();
    }

    public void selectMonster(int index) {

    }

    public void update() {
        if (!this.infoScreen.isOpen) {
            this.confirmButton.update();
            if (this.confirmButton.hb.clicked) {
                this.confirmButton.hb.clicked = false;
                this.confirmButton.hb.clickStarted = false;
                this.confirmButton.isDisabled = true;
                this.confirmButton.hide();
            }

            // this.updateControllerInput();
            if (!this.scrollBar.update()) {
                this.updateScrolling();
            }
        } else {
            this.infoScreen.update();
        }

    }

    private void updateScrolling() {
        int x = InputHelper.mX;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.targetX += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.targetX -= Settings.SCROLL_SPEED;
            }

            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartX = (float) (-x) - this.targetX;
            }
        } else if (InputHelper.isMouseDown) {
            this.targetX = (float) (-x) - this.grabStartX;
        } else {
            this.grabbedScreen = false;
        }

        this.scrollX = MathHelper.scrollSnapLerpSpeed(this.scrollX, this.targetX);
        this.resetScrolling();
        this.updateBarPosition();
    }

    public void updateMonsters() {
        int teamSize = this.teamMonsters.size();
        float preWidth = 0;

        for (int i = 0; i < teamSize; i++) {
            AbstractMonster m = this.teamMonsters.get(i);
            m.drawX = targetX + Settings.WIDTH / 2.0F + (i - (float) (teamSize - 1) / 2.0F) * preWidth * 1.2F;
            preWidth = m.hb_w;

            m.update();
            m.hb.update();
            if (m.hb.hovered) {
                StringBuilder builder = new StringBuilder();
                MonsterStrings txt = MonsterManager.GetLocale(m.id);
                for (String s : txt.MOVES) {
                    builder.append(s + " NL ");
                }
                CustomTipRenderer.renderGenericTip(m.drawX + m.hb_w, m.drawY, m.name, builder.toString());
            }
        }
    }

    private void resetScrolling() {
        if (this.targetX < this.scrollLowerBound) {
            this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollLowerBound);
        } else if (this.targetX > this.scrollUpperBound) {
            this.targetX = MathHelper.scrollSnapLerpSpeed(this.targetX, this.scrollUpperBound);
        }

    }

    public void render(SpriteBatch sb) {
        this.confirmButton.render(sb);
        this.renderMonsters(sb);
        if (this.shouldShowScrollBar) {
            this.scrollBar.render(sb);
        }

        if (this.infoScreen.isOpen) {
            sb.setColor(Color.BLACK);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
            this.infoScreen.render(sb);
            sb.setColor(Color.WHITE);
        }
    }

    private void renderMonsters(SpriteBatch sb) {
        int teamSize = this.teamMonsters.size();
        for (int i = 0; i < teamSize; i++) {
            AbstractMonster m = this.teamMonsters.get(i);
            m.render(sb);
        }

    }

    private void checkIfShowScrollBar() {
        if (!(this.shouldShowScrollBar = this.currentWidth > Settings.WIDTH))
            this.resetScrolling();
    }

    @Override
    public void scrolledUsingBar(float newPercent) {
        this.scrollX = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        this.targetX = this.scrollX;
        this.updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollX);
        this.scrollBar.parentScrolledToPercent(percent);
    }

}
package SpireAutoChess.screens;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.mainMenu.HorizontalScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.CustomTipRenderer;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.helper.MonsterManager;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.patches.OrganizationScreenPatch;

public class OrganizationScreen implements ScrollBarListener {
    private static OrganizationScreen _inst;

    public static OrganizationScreen Inst() {
        if (_inst == null)
            _inst = new OrganizationScreen();
        return _inst;
    }

    public ArrayList<AbstractTeamMonster> teamMonsters;
    public ArrayList<UpgradeButton> upgradeButtons;
    public ArrayList<SellButton> sellButtons;

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
    public MonsterUpgradeScreen upgradeScreen;

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_OrganizationScreen").TEXT;

    public OrganizationScreen() {
        this.scrollX = Settings.WIDTH - 300.0F * Settings.xScale;
        this.targetX = this.scrollX;
        this.scrollLowerBound = 0.0F;
        this.scrollUpperBound = 2400.0F * Settings.scale;
        this.shouldShowScrollBar = false;
        this.confirmButton = new ConfirmButton();
        this.targetX = 0.0F;
        this.currentWidth = 0.0F;
        this.teamMonsters = new ArrayList<>();
        this.upgradeButtons = new ArrayList<>();
        this.sellButtons = new ArrayList<>();
        this.scrollBar = new HorizontalScrollBar(this, (float) Settings.WIDTH / 2.0F,
                50.0F * Settings.scale + HorizontalScrollBar.TRACK_H / 2.0F,
                (float) Settings.WIDTH - 256.0F * Settings.scale);
        this.upgradeScreen = new MonsterUpgradeScreen();
    }

    public void open(AbstractTeamMonster... m) {
        this.addMonsters(m);
        for (AbstractMonster monster : m) {
            monster.showHealthBar();
        }
        AbstractDungeon.screen = OrganizationScreenPatch.Enum.ORGANIZATION_SCREEN;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        confirmButton.isDisabled = false;
        overlayMenu.proceedButton.hide();
        overlayMenu.cancelButton.hide();
        overlayMenu.hideCombatPanels();
        confirmButton.hideInstantly();
        confirmButton.show();
    }

    public void open(TeamMonsterGroup group) {
        currentWidth = 0.0F;
        for (AbstractTeamMonster m : group.Monsters) {
            AbstractTeamMonster inst = MonsterManager.GetMonsterInstance(m.id);
            this.addMonsters(inst);
            inst.showHealthBar();
        }
        AbstractDungeon.screen = OrganizationScreenPatch.Enum.ORGANIZATION_SCREEN;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        confirmButton.isDisabled = false;
        overlayMenu.proceedButton.hide();
        overlayMenu.cancelButton.hide();
        AbstractDungeon.dynamicBanner.appear(TEXT[0]);
        overlayMenu.hideCombatPanels();
        confirmButton.hideInstantly();
        confirmButton.show();
    }

    public void reopen() {
        AbstractDungeon.screen = OrganizationScreenPatch.Enum.ORGANIZATION_SCREEN;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.endTurnButton.disable();
        AbstractDungeon.dynamicBanner.appear(TEXT[0]);
        GenericHelper.info("screen reopen");
    }

    public void close() {
        this.teamMonsters.clear();
        this.upgradeButtons.clear();
        this.sellButtons.clear();
        confirmButton.hide();
        AbstractDungeon.isScreenUp = false;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.dynamicBanner.hide();
        overlayMenu.showCombatPanels();
    }

    public void addMonsters(AbstractTeamMonster... m) {
        for (AbstractTeamMonster monster : m) {
            this.teamMonsters.add(monster);
            this.upgradeButtons.add(new UpgradeButton(this, this.teamMonsters.size() - 1));
            this.sellButtons.add(new SellButton(this, this.teamMonsters.size() - 1));
            currentWidth += monster.hb_w * 2.0F;
        }
        this.checkIfShowScrollBar();
    }

    public void selectToUpgrade(int index) {
        upgradeScreen.open(teamMonsters.get(index));
    }

    public void selectToSell(int index) {
    }

    public void update() {
        if (!this.upgradeScreen.isOpen) {
            this.confirmButton.update();
            if (this.confirmButton.hb.clicked) {
                this.confirmButton.hb.clicked = false;
                this.confirmButton.hb.clickStarted = false;
                this.confirmButton.isDisabled = true;
                this.confirmButton.hide();
                this.close();
            }

            // this.updateControllerInput();
            if (!this.scrollBar.update()) {
                this.updateScrolling();
            }
            updateMonsters();
        } else {
            this.upgradeScreen.update();
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
            AbstractTeamMonster m = this.teamMonsters.get(i);
            GenericHelper.MoveMonster(m, preWidth - scrollX, m.drawY);
            preWidth += m.hb_w * 2.0F;

            m.update();
            m.hb.update();
            if (m.hb.hovered) {
                CustomTipRenderer.renderGenericTip(m.drawX + m.hb_w, m.drawY + m.hb_h / 2.0F, m.name,
                        m.getDescription());
            }

            UpgradeButton btn = this.upgradeButtons.get(i);
            btn.update();
            btn.current_x = m.drawX - 107.0F * Settings.scale;
            btn.current_y = m.drawY - 64.0F * Settings.scale;

            SellButton sell = this.sellButtons.get(i);
            sell.update();
            sell.current_x = m.drawX + 43.0F * Settings.scale;
            sell.current_y = m.drawY - 64.0F * Settings.scale;
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
        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.95F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT - 64.0F * Settings.scale);
        sb.setColor(Color.WHITE);
        this.confirmButton.render(sb);
        this.renderMonsters(sb);
        if (this.shouldShowScrollBar) {
            this.scrollBar.render(sb);
        }

        if (this.upgradeScreen.isOpen) {
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.9F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
            this.upgradeScreen.render(sb);
            sb.setColor(Color.WHITE);
        }
    }

    private void renderMonsters(SpriteBatch sb) {
        int teamSize = this.teamMonsters.size();
        for (int i = 0; i < teamSize; i++) {
            if (!this.upgradeScreen.isOpen) {
                this.upgradeButtons.get(i).render(sb);
                this.sellButtons.get(i).render(sb);
            }
            AbstractTeamMonster m = this.teamMonsters.get(i);
            m.render(sb);
            m.renderHealth(sb);
        }
    }

    private void checkIfShowScrollBar() {
        // if (!(this.shouldShowScrollBar = this.currentWidth > Settings.WIDTH * 1.5F))
        // {
        this.scrollLowerBound = -500.0F;
        this.scrollUpperBound = Settings.WIDTH - this.currentWidth + 300.0F;
        this.shouldShowScrollBar = true;
        this.resetScrolling();
        // }
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
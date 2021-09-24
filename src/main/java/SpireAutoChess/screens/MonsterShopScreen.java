package SpireAutoChess.screens;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.DialogWord.AppearEffect;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import com.megacrit.cardcrawl.vfx.ShopSpeechBubble;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;

import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.CustomTipRenderer;
import SpireAutoChess.helper.GenericHelper;
import SpireAutoChess.helper.MonsterManager;
import SpireAutoChess.monsters.AbstractTeamMonster;
import SpireAutoChess.patches.CustomScreenQueuePatch.ICustomScreen;

public class MonsterShopScreen implements ICustomScreen {
    private static MonsterShopScreen _inst;

    public static MonsterShopScreen Inst() {
        if (_inst == null)
            _inst = new MonsterShopScreen();
        return _inst;
    }

    public boolean isOpen = false;
    private float rugY;
    private float handTimer;
    private float handX;
    private float handY;
    private float handTargetX;
    private FloatyEffect f_effect;
    private float handTargetY;
    private ShopSpeechBubble speechBubble;
    private SpeechTextEffect dialogTextEffect;
    private float speechTimer;
    private boolean saidWelcome;

    public ArrayList<AbstractTeamMonster> monsters;
    public ArrayList<PurchaseButton> purchaseButtons;
    public ConfirmButton confirmButton;

    private static Texture rugImg;
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ChessPlayer_MonsterShopScreen").TEXT;
    private static final Texture handImg = ImageMaster.loadImage("images/npcs/merchantHand.png");
    private static final float SPEECH_TEXT_R_X = 164.0F * Settings.scale;
    private static final float SPEECH_TEXT_L_X = -166.0F * Settings.scale;
    private static final float SPEECH_TEXT_Y = 126.0F * Settings.scale;
    private static final float HAND_W = (float) handImg.getWidth() * Settings.scale;
    private static final float HAND_H = (float) handImg.getHeight() * Settings.scale;

    public MonsterShopScreen() {
        this.rugY = (float) Settings.HEIGHT / 2.0F + 540.0F * Settings.yScale;
        this.monsters = new ArrayList<>();
        this.purchaseButtons = new ArrayList<>();
        this.confirmButton = new ConfirmButton();
        this.handTimer = 1.0F;
        this.handX = (float) Settings.WIDTH / 2.0F;
        this.handY = (float) Settings.HEIGHT;
        this.handTargetX = 0.0F;
        this.handTargetY = (float) Settings.HEIGHT;
        this.f_effect = new FloatyEffect(20.0F, 0.1F);
        this.speechTimer = 0.0F;
        this.speechBubble = null;

        if (rugImg == null) {
            switch (Settings.language) {
                case DEU:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/deu.png");
                    break;
                case EPO:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/epo.png");
                    break;
                case FRA:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/fra.png");
                    break;
                case ITA:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/ita.png");
                    break;
                case JPN:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/jpn.png");
                    break;
                case KOR:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/kor.png");
                    break;
                case RUS:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/rus.png");
                    break;
                case THA:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/tha.png");
                    break;
                case UKR:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/ukr.png");
                    break;
                case ZHS:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/zhs.png");
                    break;
                default:
                    rugImg = ImageMaster.loadImage("images/npcs/rug/eng.png");
                    break;
            }
        }
    }

    public void open() {
        CardCrawlGame.sound.play("SHOP_OPEN");
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.topPanel.unhoverHitboxes();
        confirmButton.isDisabled = false;
        overlayMenu.proceedButton.hide();
        overlayMenu.cancelButton.hide();
        overlayMenu.hideCombatPanels();
        confirmButton.hideInstantly();
        confirmButton.show();
        this.queueToFont();
        this.isOpen = true;
        this.rugY = (float) Settings.HEIGHT;
        this.rugY = (float) Settings.HEIGHT;
        this.handX = (float) Settings.WIDTH / 2.0F;
        this.handY = (float) Settings.HEIGHT;
        this.handTargetX = this.handX;
        this.handTargetY = this.handY;
        this.handTimer = 1.0F;
        this.speechTimer = 1.5F;
        this.speechBubble = null;

        // TODO
        for (AbstractTeamMonster m : MonsterManager.GetRandomMonsters(4, true)) {
            addMonster(m);
        }
    }

    public void reopen() {
        this.isOpen = true;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        GenericHelper.info("screen reopen");
    }

    public void close() {
        this.isOpen = false;
        this.monsters.clear();
        this.purchaseButtons.clear();
        this.confirmButton.hide();
        AbstractDungeon.isScreenUp = false;
        AbstractDungeon.dynamicBanner.hide();
    }

    public void addMonster(AbstractTeamMonster... monster) {
        for (AbstractTeamMonster m : monster) {
            this.monsters.add(m);
            this.purchaseButtons.add(new PurchaseButton(this, m));
        }
    }

    public void purchaseMonster(AbstractTeamMonster monster) {
        if (this.monsters.contains(monster)) {
            this.monsters.remove(monster);
            TeamMonsterGroup.Inst().addMonster(monster);
        }
    }

    public void createSpeech(String msg) {
        boolean isRight = MathUtils.randomBoolean();
        float x = MathUtils.random(660.0F, 1260.0F) * Settings.scale;
        float y = (float) Settings.HEIGHT - 380.0F * Settings.scale;
        this.speechBubble = new ShopSpeechBubble(x, y, 4.0F, msg, isRight);
        float offset_x = 0.0F;
        if (isRight) {
            offset_x = SPEECH_TEXT_R_X;
        } else {
            offset_x = SPEECH_TEXT_L_X;
        }

        this.dialogTextEffect = new SpeechTextEffect(x + offset_x, y + SPEECH_TEXT_Y, 4.0F, msg, AppearEffect.BUMP_IN);
    }

    private void welcomeSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_MERCHANT_3A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_MERCHANT_3B");
        } else {
            CardCrawlGame.sound.play("VO_MERCHANT_3C");
        }

    }

    private void playMiscSfx() {
        int roll = MathUtils.random(5);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_MERCHANT_MA");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_MERCHANT_MB");
        } else if (roll == 2) {
            CardCrawlGame.sound.play("VO_MERCHANT_MC");
        } else if (roll == 3) {
            CardCrawlGame.sound.play("VO_MERCHANT_3A");
        } else if (roll == 4) {
            CardCrawlGame.sound.play("VO_MERCHANT_3B");
        } else {
            CardCrawlGame.sound.play("VO_MERCHANT_3C");
        }

    }

    public void playBuySfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_MERCHANT_KA");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_MERCHANT_KB");
        } else {
            CardCrawlGame.sound.play("VO_MERCHANT_KC");
        }

    }

    public void playCantBuySfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_MERCHANT_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_MERCHANT_2B");
        } else {
            CardCrawlGame.sound.play("VO_MERCHANT_2C");
        }

    }

    public void update() {
        this.confirmButton.update();
        if (this.confirmButton.hb.clicked) {
            this.confirmButton.hb.clicked = false;
            this.confirmButton.hb.clickStarted = false;
            this.confirmButton.isDisabled = true;
            this.confirmButton.hide();
            this.close();
        }
        this.f_effect.update();
        this.updateHand();
        this.updateRug();
        this.updateMonsters();
        this.updateSpeech();
    }

    public void updateHand() {
        if (this.handTimer != 0.0F) {
            this.handTimer -= Gdx.graphics.getDeltaTime();
            if (this.handTimer < 0.0F) {
                this.handTimer = 0.0F;
            }
        }

        if (this.handTimer == 0.0F) {
            if (this.handX != this.handTargetX) {
                this.handX = MathUtils.lerp(this.handX, this.handTargetX, Gdx.graphics.getDeltaTime() * 6.0F);
            }

            if (this.handY != this.handTargetY) {
                if (this.handY > this.handTargetY) {
                    this.handY = MathUtils.lerp(this.handY, this.handTargetY, Gdx.graphics.getDeltaTime() * 6.0F);
                } else {
                    this.handY = MathUtils.lerp(this.handY, this.handTargetY,
                            Gdx.graphics.getDeltaTime() * 6.0F / 4.0F);
                }
            }
        }
    }

    public void moveHand(float x, float y) {
        this.handTargetX = x - 50.0F * Settings.xScale;
        this.handTargetY = y + 90.0F * Settings.yScale;
    }

    public void updateRug() {
        if (this.rugY != 0.0F) {
            this.rugY = MathUtils.lerp(this.rugY, (float) Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale,
                    Gdx.graphics.getDeltaTime() * 5.0F);
            if (Math.abs(this.rugY - 0.0F) < 0.5F) {
                this.rugY = 0.0F;
            }
        }
    }

    public void updateMonsters() {
        for (int i = 0; i < this.monsters.size(); i++) {
            AbstractTeamMonster m = this.monsters.get(i);
            m.update();
            GenericHelper.MoveMonster(m,
                    Settings.WIDTH / 2.0F + (i - this.monsters.size() / 2.0F) * 200.0F * Settings.scale,
                    this.rugY + Settings.HEIGHT * 0.35F);
            m.hb.update();
            if (m.hb.hovered) {
                moveHand(m.drawX - m.hb_w, m.drawY + m.hb_h / 2.0F);
                if (m.drawX < Settings.WIDTH * 0.75F) {
                    CustomTipRenderer.renderGenericTip(m.drawX + m.hb_w, m.drawY + m.hb_h / 2.0F, m.name,
                            m.getDescription(), m.keywords);
                } else {
                    CustomTipRenderer.renderGenericTip(m.drawX - m.hb_w - CustomTipRenderer.BOX_W,
                            m.drawY + m.hb_h / 2.0F, m.name, m.getDescription(), m.keywords);
                }
            }
            PurchaseButton btn = this.purchaseButtons.get(i);
            btn.update();
            btn.current_x = m.drawX;
            btn.current_y = m.drawY - 64.0F * Settings.scale;
        }
    }

    public void updateSpeech() {
        if (this.speechBubble != null) {
            this.speechBubble.update();
            if (this.speechBubble.hb.hovered && this.speechBubble.duration > 0.3F) {
                this.speechBubble.duration = 0.3F;
                this.dialogTextEffect.duration = 0.3F;
            }

            if (this.speechBubble.isDone) {
                this.speechBubble = null;
            }
        }

        if (this.dialogTextEffect != null) {
            this.dialogTextEffect.update();
            if (this.dialogTextEffect.isDone) {
                this.dialogTextEffect = null;
            }
        }

        this.speechTimer -= Gdx.graphics.getDeltaTime();
        if (this.speechBubble == null && this.dialogTextEffect == null && this.speechTimer <= 0.0F) {
            this.speechTimer = MathUtils.random(40.0F, 60.0F);
            if (!this.saidWelcome) {
                this.createSpeech(TEXT[2]);
                this.saidWelcome = true;
                this.welcomeSfx();
            } else {
                this.playMiscSfx();
                this.createSpeech(this.getIdleMsg());
            }
        }
    }

    public void CantBuySpeech() {
        this.playCantBuySfx();
        this.createSpeech(getCantBuyMsg());
    }

    public void BuySpeech() {
        this.playBuySfx();
        this.createSpeech(getBuyMsg());
    }

    private String getIdleMsg() {
        return TEXT[MathUtils.random(3, 18)];
    }

    private String getCantBuyMsg() {
        return TEXT[MathUtils.random(19, 24)];
    }

    private String getBuyMsg() {
        return TEXT[MathUtils.random(25, 29)];
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(rugImg, 0.0F, this.rugY, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        this.renderMonstersAndPrices(sb);
        this.renderHand(sb);
        if (this.speechBubble != null) {
            this.speechBubble.render(sb);
        }

        if (this.dialogTextEffect != null) {
            this.dialogTextEffect.render(sb);
        }
        this.confirmButton.render(sb);
    }

    public void renderHand(SpriteBatch sb) {
        sb.draw(handImg, this.handX + this.f_effect.x, this.handY + this.f_effect.y, HAND_W, HAND_H);
    }

    public void renderMonstersAndPrices(SpriteBatch sb) {
        for (int i = 0; i < monsters.size(); i++) {
            AbstractTeamMonster m = monsters.get(i);
            m.render(sb);
            purchaseButtons.get(i).render(sb);
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

}
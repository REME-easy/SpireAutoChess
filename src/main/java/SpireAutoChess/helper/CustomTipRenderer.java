package SpireAutoChess.helper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

import static com.megacrit.cardcrawl.helpers.FontHelper.*;

public class CustomTipRenderer {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private static final Logger logger;
    private static boolean renderedTipThisFrame;
    private static boolean isCard;
    private static float drawX;
    private static float drawY;
    private static ArrayList<String> KEYWORDS;
    private static ArrayList<PowerTip> POWER_TIPS;
    private static String HEADER;
    private static String BODY;
    private static AbstractCard card;
    private static final Color BASE_COLOR;
    private static final float CARD_TIP_PAD;
    private static final float SHADOW_DIST_Y;
    private static final float SHADOW_DIST_X;
    private static final float BOX_EDGE_H;
    private static final float BOX_BODY_H;
    private static final float BOX_W;
    private static GlyphLayout gl;
    private static float textHeight;
    private static final float TEXT_OFFSET_X;
    private static final float HEADER_OFFSET_Y;
    private static final float ORB_OFFSET_Y;
    private static final float BODY_OFFSET_Y;
    private static final float BODY_TEXT_WIDTH;
    private static final float TIP_DESC_LINE_SPACING;
    private static final float POWER_ICON_OFFSET_X;

    public CustomTipRenderer() {
    }

    public static void render(SpriteBatch sb) {
        if (!Settings.hidePopupDetails && renderedTipThisFrame) {
            if (AbstractDungeon.player != null && (AbstractDungeon.player.inSingleTargetMode
                    || AbstractDungeon.player.isDraggingCard && !Settings.isTouchScreen)) {
                HEADER = null;
                BODY = null;
                card = null;
                renderedTipThisFrame = false;
                return;
            }

            if (Settings.isTouchScreen && AbstractDungeon.player != null && AbstractDungeon.player.isHoveringDropZone) {
                HEADER = null;
                BODY = null;
                card = null;
                renderedTipThisFrame = false;
                return;
            }

            if (isCard && card != null) {
                if (card.current_x > (float) Settings.WIDTH * 0.75F) {
                    renderKeywords(card.current_x - AbstractCard.IMG_WIDTH / 2.0F - CARD_TIP_PAD - BOX_W,
                            card.current_y + AbstractCard.IMG_HEIGHT / 2.0F - BOX_EDGE_H, sb, KEYWORDS);
                } else {
                    renderKeywords(card.current_x + AbstractCard.IMG_WIDTH / 2.0F + CARD_TIP_PAD,
                            card.current_y + AbstractCard.IMG_HEIGHT / 2.0F - BOX_EDGE_H, sb, KEYWORDS);
                }

                card = null;
                isCard = false;
            } else if (HEADER != null) {
                textHeight = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, BODY, BODY_TEXT_WIDTH,
                        TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
                renderTipBox(drawX, drawY, sb, HEADER, BODY);
                HEADER = null;
            } else {
                renderPowerTips(drawX, drawY, sb, POWER_TIPS);
            }

            renderedTipThisFrame = false;
        }

    }

    public static void renderGenericTip(float x, float y, String header, String body) {
        if (!Settings.hidePopupDetails) {
            if (!renderedTipThisFrame) {
                renderedTipThisFrame = true;
                HEADER = header;
                BODY = body;
                drawX = x;
                drawY = y;
            } else if (HEADER == null && !KEYWORDS.isEmpty()) {
                logger.info("! " + KEYWORDS.get(0));
            }
        }

    }

    public static void queuePowerTips(float x, float y, ArrayList<PowerTip> powerTips) {
        if (!renderedTipThisFrame) {
            renderedTipThisFrame = true;
            drawX = x;
            drawY = y;
            POWER_TIPS = powerTips;
        } else if (HEADER == null && !KEYWORDS.isEmpty()) {
            logger.info("! " + KEYWORDS.get(0));
        }

    }

    public static void renderTipForCard(AbstractCard c, SpriteBatch sb, ArrayList<String> keywords) {
        if (!renderedTipThisFrame) {
            isCard = true;
            card = c;
            convertToReadable(keywords);
            KEYWORDS = keywords;
            renderedTipThisFrame = true;
        }

    }

    private static void convertToReadable(ArrayList<String> keywords) {
        keywords.addAll(new ArrayList<>());
    }

    private static void renderPowerTips(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips) {
        float originalY = y;
        boolean offsetLeft = false;
        if (x > (float) Settings.WIDTH / 2.0F) {
            offsetLeft = true;
        }

        float offset = 0.0F;

        float offsetChange;
        for (Iterator<PowerTip> var7 = powerTips.iterator(); var7.hasNext(); offset += offsetChange) {
            PowerTip tip = (PowerTip) var7.next();
            textHeight = getPowerTipHeight(tip);
            offsetChange = textHeight + BOX_EDGE_H * 3.15F;
            if (offset + offsetChange >= (float) Settings.HEIGHT * 0.7F) {
                y = originalY;
                offset = 0.0F;
                if (offsetLeft) {
                    x -= 324.0F * Settings.scale;
                } else {
                    x += 324.0F * Settings.scale;
                }
            }

            renderTipBox(x, y, sb, tip.header, tip.body);
            gl.setText(FontHelper.tipHeaderFont, tip.header, Color.WHITE, 0.0F, -1, false);
            if (tip.img != null) {
                sb.setColor(Color.WHITE);
                sb.draw(tip.img, x + TEXT_OFFSET_X + gl.width + 5.0F * Settings.scale, y - 10.0F * Settings.scale,
                        32.0F * Settings.scale, 32.0F * Settings.scale);
            } else if (tip.imgRegion != null) {
                sb.setColor(Color.WHITE);
                sb.draw(tip.imgRegion, x + gl.width + POWER_ICON_OFFSET_X - (float) tip.imgRegion.packedWidth / 2.0F,
                        y + 5.0F * Settings.scale - (float) tip.imgRegion.packedHeight / 2.0F,
                        (float) tip.imgRegion.packedWidth / 2.0F, (float) tip.imgRegion.packedHeight / 2.0F,
                        (float) tip.imgRegion.packedWidth, (float) tip.imgRegion.packedHeight, Settings.scale * 0.75F,
                        Settings.scale * 0.75F, 0.0F);
            }

            y -= offsetChange;
        }

    }

    private static float getPowerTipHeight(PowerTip powerTip) {
        return -FontHelper.getSmartHeight(FontHelper.tipBodyFont, powerTip.body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING)
                - 7.0F * Settings.scale;
    }

    public static float calculateAdditionalOffset(ArrayList<PowerTip> powerTips, float hBcY) {
        return powerTips.isEmpty() ? 0.0F
                : (1.0F - hBcY / (float) Settings.HEIGHT) * getTallestOffset(powerTips)
                        - (getPowerTipHeight((PowerTip) powerTips.get(0)) + BOX_EDGE_H * 3.15F) / 2.0F;
    }

    public static float calculateToAvoidOffscreen(ArrayList<PowerTip> powerTips, float hBcY) {
        return powerTips.isEmpty() ? 0.0F : Math.max(0.0F, getTallestOffset(powerTips) - hBcY);
    }

    private static float getTallestOffset(ArrayList<PowerTip> powerTips) {
        float currentOffset = 0.0F;
        float maxOffset = 0.0F;

        for (PowerTip p : powerTips) {
            float offsetChange = getPowerTipHeight(p) + BOX_EDGE_H * 3.15F;
            if (currentOffset + offsetChange >= (float) Settings.HEIGHT * 0.7F) {
                currentOffset = 0.0F;
            }

            currentOffset += offsetChange;
            if (currentOffset > maxOffset) {
                maxOffset = currentOffset;
            }
        }

        return maxOffset;
    }

    private static void renderKeywords(float x, float y, SpriteBatch sb, ArrayList<String> keywords) {
        if (keywords.size() >= 4) {
            y += (float) (keywords.size() - 1) * 62.0F * Settings.scale;
        }

        for (String s : keywords) {
            if (!GameDictionary.keywords.containsKey(s)) {
                logger.info("MISSING: " + s + " in Dictionary!");
            } else {
                textHeight = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, (String) GameDictionary.keywords.get(s),
                        BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
                renderBox(sb, s, x, y);
                y -= textHeight + BOX_EDGE_H * 3.15F;
            }
        }

    }

    private static void renderTipBox(float x, float y, SpriteBatch sb, String title, String description) {
        float h = textHeight;
        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y,
                Settings.GOLD_COLOR);
        FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, description, x + TEXT_OFFSET_X, y + BODY_OFFSET_Y,
                BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
    }

    private static void renderTipEnergy(SpriteBatch sb, TextureAtlas.AtlasRegion region, float x, float y) {
        sb.setColor(Color.WHITE);
        sb.draw(region.getTexture(), x + region.offsetX * Settings.scale, y + region.offsetY * Settings.scale, 0.0F,
                0.0F, (float) region.packedWidth, (float) region.packedHeight, Settings.scale, Settings.scale, 0.0F,
                region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), false,
                false);
    }

    private static void renderBox(SpriteBatch sb, String word, float x, float y) {
        float h = textHeight;
        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);
        TextureAtlas.AtlasRegion currentOrb = AbstractDungeon.player != null ? AbstractDungeon.player.getOrb()
                : AbstractCard.orb_red;
        if (!word.equals("[R]") && !word.equals("[G]") && !word.equals("[B]") && !word.equals("[W]")
                && !word.equals("[E]")) {
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, capitalize(word), x + TEXT_OFFSET_X,
                    y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        } else {
            switch (word) {
                case "[R]":
                    renderTipEnergy(sb, AbstractCard.orb_red, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);
                    break;
                case "[G]":
                    renderTipEnergy(sb, AbstractCard.orb_green, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);
                    break;
                case "[B]":
                    renderTipEnergy(sb, AbstractCard.orb_blue, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);
                    break;
                case "[W]":
                    renderTipEnergy(sb, AbstractCard.orb_purple, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);
                    break;
                default:
                    renderTipEnergy(sb, currentOrb, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);
                    break;
            }

            FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, capitalize(TEXT[0]),
                    x + TEXT_OFFSET_X * 2.5F, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }

        FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, GameDictionary.keywords.get(word), x + TEXT_OFFSET_X,
                y + BODY_OFFSET_Y, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
    }

    private static String capitalize(String input) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char tmp = input.charAt(i);
            if (i == 0) {
                tmp = Character.toUpperCase(tmp);
            } else {
                tmp = Character.toLowerCase(tmp);
            }

            sb.append(tmp);
        }

        return sb.toString();
    }

    public static void renderViewTip(SpriteBatch sb, String msg, float x, float y, Color color) {
        layout.setText(cardDescFont_N, msg);
        sb.setColor(Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x - layout.width / 2.0F - 12.0F * Settings.scale,
                y - 24.0F * Settings.scale, layout.width + 24.0F * Settings.scale, 48.0F * Settings.scale);
        renderFontCentered(sb, cardDescFont_N, msg, x, y, color);
    }

    @SpirePatch(clz = TipHelper.class, method = "render")
    public static class TipHelperPatch {
        public TipHelperPatch() {
        }

        public static void Postfix(SpriteBatch sb) {
            CustomTipRenderer.render(sb);
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("TipHelper");
        TEXT = uiStrings.TEXT;
        logger = LogManager.getLogger(TipHelper.class.getName());
        renderedTipThisFrame = false;
        isCard = false;
        KEYWORDS = new ArrayList<>();
        POWER_TIPS = new ArrayList<>();
        HEADER = null;
        BODY = null;
        BASE_COLOR = new Color(1.0F, 0.9725F, 0.8745F, 1.0F);
        CARD_TIP_PAD = 12.0F * Settings.scale;
        SHADOW_DIST_Y = 14.0F * Settings.scale;
        SHADOW_DIST_X = 9.0F * Settings.scale;
        BOX_EDGE_H = 32.0F * Settings.scale;
        BOX_BODY_H = 64.0F * Settings.scale;
        BOX_W = 320.0F * Settings.scale;
        gl = new GlyphLayout();
        TEXT_OFFSET_X = 22.0F * Settings.scale;
        HEADER_OFFSET_Y = 12.0F * Settings.scale;
        ORB_OFFSET_Y = -8.0F * Settings.scale;
        BODY_OFFSET_Y = -20.0F * Settings.scale;
        BODY_TEXT_WIDTH = 280.0F * Settings.scale;
        TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;
        POWER_ICON_OFFSET_X = 40.0F * Settings.scale;
    }
}

package SpireAutoChess.modcore;

import static SpireAutoChess.character.ChessPlayer.Enums.CHESS_PLAYER;
import static SpireAutoChess.character.ChessPlayer.Enums.CHESS_PLAYER_CARD;
import static com.megacrit.cardcrawl.core.Settings.language;

import java.nio.charset.StandardCharsets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import SpireAutoChess.character.ChessPlayer;
import SpireAutoChess.character.TeamMonsterGroup;
import SpireAutoChess.helper.EventHelper;
import SpireAutoChess.helper.SecondaryMagicVariable;
import SpireAutoChess.monsters.common.TCultist;
import basemod.BaseMod;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;

@SpireInitializer
public class ChessPlayerModCore implements EditCharactersSubscriber, EditStringsSubscriber, EditCardsSubscriber,
        EditRelicsSubscriber, EditKeywordsSubscriber, OnStartBattleSubscriber, AddAudioSubscriber,
        PostInitializeSubscriber, StartGameSubscriber {
    private static final Logger logger = LogManager.getLogger(ChessPlayerModCore.class);
    private static final String MY_CHARACTER_BUTTON = "ChessPlayerResources/img/char/Arknights_Button.png";
    private static final String MY_CHARACTER_PORTRAIT = "ChessPlayerResources/img/char/Arknights_Portrait.png";
    private static final String BG_ATTACK_512 = "ChessPlayerResources/img/512/bg_attack_512.png";
    private static final String BG_POWER_512 = "ChessPlayerResources/img/512/bg_power_512.png";
    private static final String BG_SKILL_512 = "ChessPlayerResources/img/512/bg_skill_512.png";
    private static final String small_orb = "ChessPlayerResources/img/char/small_orb.png";
    private static final String BG_ATTACK_1024 = "ChessPlayerResources/img/1024/bg_attack.png";
    private static final String BG_POWER_1024 = "ChessPlayerResources/img/1024/bg_power.png";
    private static final String BG_SKILL_1024 = "ChessPlayerResources/img/1024/bg_skill.png";
    private static final String big_orb = "ChessPlayerResources/img/char/card_orb.png";
    private static final String energy_orb = "ChessPlayerResources/img/char/cost_orb.png";
    private static final Color ORANGE = GetCharColor();

    public ChessPlayerModCore() {
        BaseMod.subscribe(this);
        BaseMod.addColor(CHESS_PLAYER_CARD, ORANGE, ORANGE, ORANGE, ORANGE, ORANGE, ORANGE, ORANGE, BG_ATTACK_512,
                BG_SKILL_512, BG_POWER_512, energy_orb, BG_ATTACK_1024, BG_SKILL_1024, BG_POWER_1024, big_orb,
                small_orb);
    }

    public static void initialize() {
        new ChessPlayerModCore();
    }

    @Override
    public void receiveEditCharacters() {
        logger.info("===小刻正在准备！===");
        logger.info("正在添加" + CHESS_PLAYER.toString());
        BaseMod.addCharacter(new ChessPlayer(CardCrawlGame.playerName), MY_CHARACTER_BUTTON, MY_CHARACTER_PORTRAIT,
                CHESS_PLAYER);
        logger.info("===小刻准备完啦！===");
    }

    public void receiveEditStrings() {
        String lang = "eng";
        if (language == Settings.GameLanguage.ENG) {
            lang = "eng";
        } else if (language == Settings.GameLanguage.ZHS) {
            lang = "zh";
        }
        // BaseMod.loadCustomStringsFile(RelicStrings.class,
        // "ChessPlayerResources/localization/ChessPlayerRelics_" + lang + ".json");
        // BaseMod.loadCustomStringsFile(CardStrings.class,
        // "ChessPlayerResources/localization/ChessPlayerCards_" + lang + ".json");
        // BaseMod.loadCustomStringsFile(PowerStrings.class,
        // "ChessPlayerResources/localization/ChessPlayerPowers_" + lang + ".json");
        // BaseMod.loadCustomStringsFile(EventStrings.class,
        // "ChessPlayerResources/localization/ChessPlayerEvents_" + lang + ".json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                "ChessPlayerResources/localization/ChessPlayerMonsters_" + lang + ".json");
        // BaseMod.loadCustomStringsFile(OrbStrings.class,"ChessPlayerResources/localization/ChessPlayerOrbs_"
        // + lang + ".json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                "ChessPlayerResources/localization/ChessPlayerChar_" + lang + ".json");
        BaseMod.loadCustomStringsFile(UIStrings.class,
                "ChessPlayerResources/localization/ChessPlayerUI_" + lang + ".json");

    }

    public void receiveEditCards() {
        logger.info("===小刻正在准备卡牌和法术！===");
        BaseMod.addDynamicVariable(new SecondaryMagicVariable());
        // AutoAdd cards = new AutoAdd("ChessPlayerMod");
        // cards.packageFilter(AbstractChessPlayerCard.class).setDefaultSeen(false).any(AbstractChessPlayerCard.class,
        // ((info, card) -> {
        // if (card != null) {
        // BaseMod.addCard(card);
        // if (info.seen) {
        // UnlockTracker.unlockCard(card.cardID);
        // }
        // }
        // }));

        // AbstractSpell[] spells = new AbstractSpell[] {
        // new FunSpraySpell(), new HotSpellSpell(), new SpellLikeFireworkSpell(),
        // new VeryColdAxeSpell()
        // };
        // for (AbstractSpell s : spells) {
        // AbstractSpell.AllSpells.put(s.ID, s);
        // }
        logger.info("===小刻随时可以出发！===");
    }

    public void receiveEditRelics() {
    }

    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String lang = "eng";
        if (language == Settings.GameLanguage.ZHS) {
            lang = "zh";
        }

        logger.info("===小刻的关键词===");
        String json = Gdx.files.internal("ChessPlayerResources/localization/ChessPlayerKeywords_" + lang + ".json")
                .readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                logger.info(String.format("正在加载关键词：%s", keyword.NAMES[0]));
                BaseMod.addKeyword("ChessPlayer", keyword.NAMES[0], keyword.NAMES, keyword.DESCRIPTION);
            }
        }
        logger.info("===小刻的关键词也好啦===");
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("ChessPlayer_1", "ChessPlayerResources/sound/ChessPlayer_1.ogg");
        BaseMod.addAudio("ChessPlayer_2", "ChessPlayerResources/sound/ChessPlayer_2.ogg");
    }

    @Override
    public void receivePostInitialize() {
        receiveAddMonster();
        receiveAddEvent();
    }

    private void receiveAddMonster() {
    }

    private void receiveAddEvent() {
    }

    public static Color GetCharColor() {
        return new Color(1.0f, 0.6f, 0.2f, 1.0F);
    }

    public static String MakePath(String id) {
        return "ChessPlayer_" + id;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        EventHelper.receiveOnBattleStart(abstractRoom);
    }

    @Override
    public void receiveStartGame() {
        TeamMonsterGroup.Inst().addMonster(new TCultist(), new TCultist(), new TCultist());
    }
}
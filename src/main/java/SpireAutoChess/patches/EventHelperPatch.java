package SpireAutoChess.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

import SpireAutoChess.helper.EventHelper;
import SpireAutoChess.monsters.AbstractTeamMonster;

public class EventHelperPatch {
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class AddBlockPatch {

        @SpireInsertPatch(rloc = 20, localvars = { "tmp" })
        public static void Insert(AbstractCreature _inst, int blockAmount, @ByRef float[] tmp) {
            if (_inst instanceof AbstractTeamMonster) {
                EventHelper.TeamMonsterGainBlockSubscribers.forEach((sub) -> {
                    tmp[0] = sub.OnGainBlock((AbstractTeamMonster) _inst, tmp[0]);
                });
            }
        }
    }
}
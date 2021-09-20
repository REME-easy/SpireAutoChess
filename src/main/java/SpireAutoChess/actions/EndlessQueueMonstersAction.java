package SpireAutoChess.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import SpireAutoChess.character.TeamMonsterGroup;

public class EndlessQueueMonstersAction extends AbstractGameAction {

    private boolean firstTurn;

    public EndlessQueueMonstersAction(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    @Override
    public void update() {
        // if (this.firstTurn) {
        // AbstractDungeon.player.app
        // }
        AbstractDungeon.getCurrRoom().monsters.queueMonsters();
        TeamMonsterGroup.Inst().queueMonsters();
        addToBot(new EndlessQueueMonstersAction(false));
        this.isDone = true;
    }

}
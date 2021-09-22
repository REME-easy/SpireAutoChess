package SpireAutoChess.utils;

import SpireAutoChess.screens.MonsterShopScreen;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class ShopScreenCommand extends ConsoleCommand {

    @Override
    protected void execute(String[] tokens, int depth) {
        try {
            MonsterShopScreen.Inst().open();
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg();
        }
    }

    public void errorMsg() {
        DevConsole.couldNotParse();
    }

}
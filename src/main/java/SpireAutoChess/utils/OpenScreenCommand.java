package SpireAutoChess.utils;

import SpireAutoChess.screens.OrganizationScreen;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class OpenScreenCommand extends ConsoleCommand {

    @Override
    protected void execute(String[] tokens, int depth) {
        try {
            OrganizationScreen.Inst().open();
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg();
        }
    }

    public void errorMsg() {
        DevConsole.couldNotParse();
    }

}
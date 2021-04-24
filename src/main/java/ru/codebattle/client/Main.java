package ru.codebattle.client;

import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

public class Main {

//    private static final String SERVER_ADDRESS = "http://codingdojo2019.westeurope.cloudapp.azure.com/codenjoy-contest/board/player/kjfserksnckshus?code=3948792673423&gameName=loderunner";
    private static final String SERVER_ADDRESS = "https://dojorena.io/codenjoy-contest/board/player/dojorena394?code=874162015969976807";

    public static void main(String[] args) throws IOException {
        ReconnectableLodeRunnerClientWrapper client = new ReconnectableLodeRunnerClientWrapper(SERVER_ADDRESS, Main::doAction);

        client.run();

        System.in.read();

        client.initiateExit();
    }

    private static LoderunnerAction doAction(GameBoard gameBoard) {
        Random random = new Random(System.currentTimeMillis());
        MyBot bot = new MyBot();

        LoderunnerAction action =  bot.iterationMove(gameBoard);

        bot.printMap();

        return action;
//        return LoderunnerAction.values()[random.nextInt(LoderunnerAction.values().length)];
    }
}

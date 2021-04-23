package ru.codebattle.client;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

import java.util.*;

public class MyBot {

    // TODO удалить
    private static List<String> gameBoardList;


    public static void nextMove (GameBoard gameBoard) {
        BoardPoint botLocation = gameBoard.getMyPosition();

//        System.out.println(botLocation.getX() + ", Y: " + botLocation.getY());

//        for ( String s : gameBoard.getBoardString()) {System.out.println(s);}

        List<BoardPoint> goldList = gameBoard.getGoldPositions();
        Map<BoardPoint, Double> distanceMap = new HashMap<>();
        for (BoardPoint bp : goldList) {
            double distanceToGold = distance(bp.getY(), bp.getY(), botLocation.getX(), botLocation.getY());
            distanceMap.put(new BoardPoint(bp.getX(), bp.getY()), distanceToGold);
        }

        BoardPoint nearestGold = null;
        Double min = 100.0;

        for (Map.Entry<BoardPoint, Double> entry : distanceMap.entrySet()) {
            BoardPoint bp = entry.getKey();
            Double dist = entry.getValue();

            if (dist < min) {
                min = dist;
                if (nearestGold == null) nearestGold = new BoardPoint(bp.getX(), bp.getY());
                else {
                    nearestGold.setX(bp.getX());
                    nearestGold.setY(bp.getY());
                }
            }
        }

        assert nearestGold != null;
//        System.out.println("MIN - X: " + nearestGold.getX() + ", Y: " + nearestGold.getY());
    }

    public LoderunnerAction firstMove(GameBoard gameBoard, int x, int y) {
        System.out.println("firstMove: " + x + " " + y);
//        gameBoard.printBoard();

        gameBoardList = gameBoard.getBoardString();

//        for ( String s : gameBoardList) {System.out.println(s);}

        if (gameBoard.hasPipeAt(new BoardPoint(x, y))) {
            System.out.println("PIPE AT");
            return LoderunnerAction.GO_DOWN;
        }

        double up = 1000.0;
        double right = 1000.0;
        double down = 1000.0;
        double left = 1000.0;

        for (int i = 0; i < 4; i++){

            BoardPoint nextPos;

            switch (i){
                case 0:
                    nextPos = new BoardPoint(x, y - 1);
                    if (gameBoard.hasLadderAt(nextPos)) {
                        System.out.println("CAN GO UP IN FIRSTMOVE");
                        up = doMove(gameBoard, LoderunnerAction.GO_UP, nextPos.getX(), nextPos.getY());
                    }
                    break;

                case 1:
                    nextPos = new BoardPoint(x + 1, y);
                    BoardPoint underMeAtRight = new BoardPoint(x + 1, y + 1);

                    if (gameBoard.hasGoldAt(nextPos)){
                        return LoderunnerAction.GO_RIGHT;
                    }

                    if (!gameBoard.hasWallAt(nextPos) && (gameBoard.hasWallAt(underMeAtRight) || gameBoard.hasLadderAt(underMeAtRight))) {
                        System.out.println("CAN GO RIGHT IN FIRSTMOVE");
                        right = doMove(gameBoard, LoderunnerAction.GO_RIGHT, nextPos.getX(), nextPos.getY());
                    }
                    break;

                case 2:
                    nextPos = new BoardPoint(x, y + 1);
                    if (gameBoard.hasLadderAt(nextPos) || !gameBoard.hasWallAt(nextPos)) {
                        System.out.println("CAN GO DOWN IN FIRSTMOVE");
                        down = doMove(gameBoard, LoderunnerAction.GO_DOWN, nextPos.getX(), nextPos.getY());
                    }
                    break;

                case 3:
                    nextPos = new BoardPoint(x - 1, y);
                    BoardPoint underMeAtLeft = new BoardPoint(x - 1, y + 1);

                    if (gameBoard.hasGoldAt(nextPos)){
                        return LoderunnerAction.GO_LEFT;
                    }

                    if (!gameBoard.hasWallAt(nextPos) && (gameBoard.hasWallAt(underMeAtLeft) || gameBoard.hasLadderAt(underMeAtLeft))) {
                        System.out.println("IS SHADOW AT LEFT: " + gameBoard.hasShadowAt(nextPos));
                        System.out.println("IS OTHER HERO AT LEFT: " + gameBoard.hasOtherHeroAt(nextPos));
                        System.out.println("CAN GO LEFT IN FIRSTMOVE");
                        left = doMove(gameBoard, LoderunnerAction.GO_LEFT, nextPos.getX(), nextPos.getY());
                    }
                    break;
            }
        }


        double min = Math.min(Math.min(up, down), Math.min(left, right));
        System.out.println("MINNNN: " + min + ", UP: " + up + ", RIGHT: " + right + ", DOWN: " + down + ", LEFT: " + left);


//        for ( String s : gameBoardList) {System.out.println(s);}

        if (min == left) {
            return LoderunnerAction.GO_LEFT;
        }
        else if (min == right) {
            return LoderunnerAction.GO_RIGHT;
        }
        else if (min == down) {
            return LoderunnerAction.GO_DOWN;
        }
        else if (min == up) {
            return LoderunnerAction.GO_UP;
        }

        return LoderunnerAction.DO_NOTHING;
    }

    private double doMove(GameBoard gameBoard, LoderunnerAction action, int x, int y) {
        // 0 - UP, 1 - RIGHT, 2 - DOWN, 3 - LEFT

//        System.out.println("doMove: " + x + " " + y);

        List<BoardPoint> goldList = gameBoard.getGoldPositions();
//        String s = gameBoardList.get(y);
//
//        char[] myNameChars = s.toCharArray();
//        myNameChars[x] = '+';
//        s = String.valueOf(myNameChars);
//        gameBoardList.set(y, s);


        double up = 1000.0;
        double right = 1000.0;
        double down = 1000.0;
        double left = 1000.0;

//        for (BoardPoint bp : goldList) System.out.println("GOLD X: " + bp.getX() + ", Y: " + bp.getY());

        for (int i = 0; i < 4; i++){
            if (    (i == 0 && action == LoderunnerAction.GO_DOWN) ||
                    (i == 1 && action == LoderunnerAction.GO_LEFT) ||
                    (i == 2 && action == LoderunnerAction.GO_UP) ||
                    (i == 3 && action == LoderunnerAction.GO_RIGHT)) {
                continue;
            }

            switch (i){
                case 0:
                    double newUp = upwardMove(gameBoard, x, y);
                    up = newUp == -1.0 ? up : newUp;
                    break;

                case 1:
                    double newRight = rightMove(gameBoard, x, y);
                    right = newRight == -1.0 ? right : newRight;
                    break;

                case 2:
                    double newDown = downwardMove(gameBoard, x, y);
                    down = newDown == -1.0 ? down : newDown;
                    break;

                case 3:
                    double newLeft = leftMove(gameBoard, x, y);
                    left = newLeft == -1.0 ? left : newLeft;
                    break;
            }
        }

//        System.out.println(", UP: " + up + ", RIGHT: " + right + ", DOWN: " + down + ", LEFT: " + left);
        return Math.min(Math.min(up, down), Math.min(left, right));
    }


    public static double distance(int x1, int y1, int x2, int y2) {
        double xDist = Math.pow(x2 - x1, 2);
        double yDist = Math.pow(y2 - y1, 2);

        return Math.sqrt(xDist + yDist);
    }

    private double upwardMove(GameBoard gameBoard, int x, int y) {
        BoardPoint nextPos = new BoardPoint(x, y - 1);
        if (gameBoard.hasLadderAt(nextPos)) {
            return doMove(gameBoard, LoderunnerAction.GO_UP, nextPos.getX(), nextPos.getY());
        }

        return -1.0;
    }

    private double rightMove(GameBoard gameBoard, int x, int y) {
        BoardPoint nextPos = new BoardPoint(x + 1, y);
        BoardPoint underMeAtRight = new BoardPoint(x + 1, y + 1);

        if (gameBoard.hasGoldAt(nextPos)){
            BoardPoint botLocation = gameBoard.getMyPosition();
            return distance(botLocation.getX(), botLocation.getY(), nextPos.getX(), nextPos.getY());
        }

        if (!gameBoard.hasWallAt(nextPos) && (gameBoard.hasWallAt(underMeAtRight) || gameBoard.hasLadderAt(underMeAtRight))) {
            return doMove(gameBoard, LoderunnerAction.GO_RIGHT, nextPos.getX(), nextPos.getY());
        }

        return -1.0;
    }

    private double downwardMove(GameBoard gameBoard, int x, int y) {
        BoardPoint nextPos = new BoardPoint(x, y + 1);
        if (gameBoard.hasLadderAt(nextPos) || !gameBoard.hasWallAt(nextPos)) {
            return doMove(gameBoard, LoderunnerAction.GO_DOWN, nextPos.getX(), nextPos.getY());
        }

        return -1.0;
    }

    private double leftMove(GameBoard gameBoard, int x, int y) {
        BoardPoint nextPos = new BoardPoint(x - 1, y);
        BoardPoint underMeAtLeft = new BoardPoint(x - 1, y + 1);

        if (gameBoard.hasGoldAt(nextPos)){
            BoardPoint botLocation = gameBoard.getMyPosition();
            return distance(botLocation.getX(), botLocation.getY(), nextPos.getX(), nextPos.getY());
        }

        if (!gameBoard.hasWallAt(nextPos) && (gameBoard.hasWallAt(underMeAtLeft) || gameBoard.hasLadderAt(underMeAtLeft))) {
            return doMove(gameBoard, LoderunnerAction.GO_LEFT, nextPos.getX(), nextPos.getY());
        }

        return -1.0;
    }

}


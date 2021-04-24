package ru.codebattle.client;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

import java.util.*;

public class MyBot {

    // TODO удалить
    private static List<String> gameBoardList;

    private static List<BoardPoint> points = null;

    public static LoderunnerAction lastAction = null;

    public static int count = 0;

    public static double distance(int x1, int y1, int x2, int y2) {
        double xDist = Math.pow(x2 - x1, 2);
        double yDist = Math.pow(y2 - y1, 2);

        return Math.sqrt(xDist + yDist);
    }

    public static class Path{
        public BoardPoint bp;
        public LoderunnerAction lastAction;
        public LoderunnerAction firstAction;

        public Path(BoardPoint coordinates, LoderunnerAction lastAction, LoderunnerAction firstAction) {
            this.bp = coordinates;
            this.lastAction = lastAction;
            this.firstAction = firstAction;
        }

        public Path(Path path) {
            this.bp = new BoardPoint(path.bp.getX(), path.bp.getY());
            this.lastAction = path.lastAction;
            this.firstAction = path.firstAction;
        }
    }

    public LoderunnerAction iterationMove (GameBoard gameBoard) {

        if (gameBoard.isGameOver()) return LoderunnerAction.DO_NOTHING;

        count = 0;

        gameBoardList = gameBoard.getBoardString();

        List<Path> pathList = new ArrayList<>();

        List<BoardPoint> points = new ArrayList<>();

        points.add(gameBoard.getMyPosition());

        pathList.add(new Path(gameBoard.getMyPosition(), null, null));

        int i = 0;
        int timeToSuicide = 25;

        if (lastAction != null) {
            if (lastAction == LoderunnerAction.DRILL_LEFT) {
                lastAction = LoderunnerAction.GO_LEFT;
                return LoderunnerAction.GO_LEFT;
            }
            else if (lastAction == LoderunnerAction.DRILL_RIGHT) {
                lastAction = LoderunnerAction.GO_RIGHT;
                return LoderunnerAction.GO_RIGHT;
            }
        }


        while (true) {
            List<Path> pathListBuff = new ArrayList<>();

            for (Path path : pathList) {

                // ----------------------------
                // DOWN
                // ----------------------------

                Path pathBufDown = new Path(path);

                if (gameBoard.hasSomeInteresting(pathBufDown.bp.shiftBottom())) {
                    lastAction = pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                    return pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                }

                if (
                        (gameBoard.hasLadderAt(pathBufDown.bp.shiftBottom())
                                || gameBoard.hasAlreadyDrilledPitAt(pathBufDown.bp.shiftBottom())
                                || gameBoard.hasEmptinessAt(pathBufDown.bp.shiftBottom())
                                || gameBoard.hasPipeAt(pathBufDown.bp.shiftBottom()))
                                && !points.contains(path.bp.shiftBottom())
                                && !gameBoard.hasEnemyAt(pathBufDown.bp.shiftBottom())
                ) {

                    boolean wasInWhile = false;
                    while (gameBoard.hasEmptinessAt(pathBufDown.bp.shiftBottom())) {
                        if (gameBoard.hasSomeInteresting(pathBufDown.bp.shiftBottom())) {
                            lastAction = pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                            return pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                        }
                        pathBufDown.bp.setY(pathBufDown.bp.getY() + 1);

                        setPlus(pathBufDown.bp);
                        wasInWhile = true;
                    }

                    if (wasInWhile) {
                        points.add(pathBufDown.bp);
                        setPlus(pathBufDown.bp);

                        pathListBuff.add(new Path(pathBufDown.bp, LoderunnerAction.GO_DOWN,
                                pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction));
                    }
                    else {
                        points.add(pathBufDown.bp.shiftBottom());
                        setPlus(pathBufDown.bp.shiftBottom());

                        pathListBuff.add(new Path(pathBufDown.bp.shiftBottom(), LoderunnerAction.GO_DOWN,
                                pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction));
                    }
                }




                // ----------------------------
                // UP
                // ----------------------------

                Path pathBufUp = new Path(path);

                if (
                        gameBoard.hasLadderAt(pathBufUp.bp)
                        && !points.contains(path.bp.shiftTop())
                        && (
                                gameBoard.hasEmptinessAt(pathBufUp.bp.shiftTop())
                                || gameBoard.hasLadderAt(pathBufUp.bp.shiftTop())
                                || gameBoard.hasPipeAt(pathBufUp.bp.shiftTop())
                                || gameBoard.hasSomeInteresting(pathBufUp.bp.shiftTop())
                            )
                        && !gameBoard.hasEnemyAt(pathBufUp.bp.shiftTop())
                ){

                    if (gameBoard.hasSomeInteresting(pathBufUp.bp.shiftTop())) {
                        lastAction = pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction;
                        return pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction;
                    }

                    points.add(pathBufUp.bp.shiftTop());

                    pathListBuff.add(new Path(pathBufUp.bp.shiftTop(), LoderunnerAction.GO_UP,
                            pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction));
                }

                // ----------------------------
                // LEFT
                // ----------------------------

                Path pathBufLeft = new Path(path);

                if (
                        !gameBoard.hasWallAt(pathBufLeft.bp.shiftLeft())    // СЛЕВА НЕТ СТЕНЫ
                        && pathBufLeft.lastAction != LoderunnerAction.GO_RIGHT
                        && !gameBoard.hasOtherHeroAt(pathBufLeft.bp.shiftLeft())
                        && !points.contains(path.bp.shiftLeft())
                        && !gameBoard.hasEnemyAt(pathBufLeft.bp.shiftLeft())

                ) {

                    if (gameBoard.hasSomeInteresting(pathBufLeft.bp.shiftLeft())) {
                        lastAction = pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                        return pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                    }

                    while (gameBoard.hasEmptinessAt(pathBufLeft.bp.shiftLeft().shiftBottom())) {
                        pathBufLeft.bp.setY(pathBufLeft.bp.getY() + 1);
                        setPlus(pathBufLeft.bp);
                        if (gameBoard.hasSomeInteresting(pathBufLeft.bp.shiftLeft())) {
                            lastAction = pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                            return pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                        }
//                        i++;
//                        if (i > timeToSuicide)  return LoderunnerAction.SUICIDE;
                    }

                    if (gameBoard.hasPipeAt(pathBufLeft.bp.shiftLeft().shiftBottom())) {
                        points.add(pathBufLeft.bp.shiftLeft().shiftBottom());
                        MyBot.setPlus(pathBufLeft.bp.shiftLeft().shiftBottom());

                        pathListBuff.add(new Path(pathBufLeft.bp.shiftLeft().shiftBottom(), LoderunnerAction.GO_LEFT,
                                pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction));
                    }
                    else {
                        points.add(pathBufLeft.bp.shiftLeft());
                        MyBot.setPlus(pathBufLeft.bp.shiftLeft());

                        pathListBuff.add(new Path(pathBufLeft.bp.shiftLeft(), LoderunnerAction.GO_LEFT,
                                pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction));
                    }
                }

                // ----------------------------
                // RIGHT
                // ----------------------------
                Path pathBufRight = new Path(path);

                if (
                        !gameBoard.hasWallAt(pathBufRight.bp.shiftRight())  // СПРАВА НЕТ СТЕНЫ
                        && pathBufRight.lastAction != LoderunnerAction.GO_LEFT
                        && !gameBoard.hasOtherHeroAt(pathBufRight.bp.shiftRight())
                        && !points.contains(path.bp.shiftRight())
                        && !gameBoard.hasEnemyAt(pathBufRight.bp.shiftRight())
                ){

                    if (gameBoard.hasSomeInteresting(pathBufRight.bp.shiftRight())) {
                        lastAction = pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                        return pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                    }

                    while (gameBoard.hasEmptinessAt(pathBufRight.bp.shiftRight().shiftBottom())) {
                        if (gameBoard.hasSomeInteresting(pathBufRight.bp.shiftRight())) {
                            lastAction = pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                            return pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                        }
                        pathBufRight.bp.setY(pathBufRight.bp.getY() + 1);
                        setPlus(pathBufRight.bp);
//                        i++;
//                        if (i > timeToSuicide)  return LoderunnerAction.SUICIDE;
                    }

                    if (gameBoard.hasPipeAt(pathBufRight.bp.shiftRight().shiftBottom())) {
                        points.add(pathBufRight.bp.shiftRight().shiftBottom());
                        MyBot.setPlus(pathBufRight.bp.shiftRight().shiftBottom());

                        pathListBuff.add(new Path(pathBufRight.bp.shiftRight().shiftBottom(), LoderunnerAction.GO_RIGHT,
                                pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction));
                    }

                    else {
                        points.add(pathBufRight.bp.shiftRight());
                        MyBot.setPlus(pathBufRight.bp.shiftRight());

                        pathListBuff.add(new Path(pathBufRight.bp.shiftRight(), LoderunnerAction.GO_RIGHT,
                                pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction));
                    }
                }


                // ----------------------------
                // DRILL_LEFT
                // ----------------------------

                Path pathBufDrill = new Path(path);

                if (gameBoard.hasDestroyableWallAt(pathBufDrill.bp.shiftLeft().shiftBottom())
                    && !gameBoard.hasLadderAt(pathBufDrill.bp.shiftLeft())
                    && !gameBoard.hasOtherHeroAt(pathBufDrill.bp.shiftLeft())
                    && !gameBoard.hasWallAt(pathBufDrill.bp.shiftLeft())) {
                    pathListBuff.add(new Path(pathBufDrill.bp.shiftLeft().shiftBottom(), LoderunnerAction.DRILL_LEFT,
                            pathBufDrill.firstAction == null ? LoderunnerAction.DRILL_LEFT : pathBufDrill.firstAction));
                }

                // ----------------------------
                // DRILL_RIGHT
                // ----------------------------

                if (gameBoard.hasDestroyableWallAt(pathBufDrill.bp.shiftRight().shiftBottom())
                    && !gameBoard.hasLadderAt(pathBufDrill.bp.shiftRight())
                    && !gameBoard.hasOtherHeroAt(pathBufDrill.bp.shiftRight())
                    && !gameBoard.hasWallAt(pathBufDrill.bp.shiftRight())) {
                    pathListBuff.add(new Path(pathBufDrill.bp.shiftRight().shiftBottom(), LoderunnerAction.DRILL_RIGHT,
                            pathBufDrill.firstAction == null ? LoderunnerAction.DRILL_RIGHT : pathBufDrill.firstAction));
                }
            }
            pathList.clear();
            pathList = pathListBuff;

            System.out.println("i: " + i + ", pathListSize: " + pathList.size() + ", " + pathListBuff.size());

            if (i > timeToSuicide) {

                if (gameBoard.hasEnemyAt(gameBoard.getMyPosition().shiftTop())
                    || gameBoard.hasEnemyAt(gameBoard.getMyPosition().shiftRight())
                    || gameBoard.hasEnemyAt(gameBoard.getMyPosition().shiftBottom())
                    || gameBoard.hasEnemyAt(gameBoard.getMyPosition().shiftLeft())
                ){
                   return LoderunnerAction.DO_NOTHING;
                }




                return LoderunnerAction.SUICIDE;
            }

            i++;
        }
    }


    public static void setPlus(BoardPoint p){
        count++;
        String s = gameBoardList.get(p.getY());
        char[] myNameChars = s.toCharArray();
        myNameChars[p.getX()] = (char)((count % 10) + '0');
        s = String.valueOf(myNameChars);
        gameBoardList.set(p.getY(), s);
    }

    public void printMap(){
        for ( String s : gameBoardList) {System.out.println(s);}

    }
}

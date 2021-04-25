package ru.codebattle.client;

import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

import java.util.*;

public class MyBot {

    // TODO удалить
    private static List<String> gameBoardList;

    public static LoderunnerAction lastAction = null;

    public static int count = 0;

    public static int counterRepeat = 0;

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
        
        LoderunnerAction actionToPortal = null;

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
            else if (gameBoard.hasPortalAt(gameBoard.getMyPosition())) {    // ЕСЛИ МЫ СТОИМ В ПОРТАЛЕ
                if (
                        !gameBoard.hasWallAt(gameBoard.getMyPosition().shiftLeft())    // СЛЕВА НЕТ СТЕНЫ
                                && !gameBoard.myHasOtherHeroAt(gameBoard.getMyPosition().shiftLeft())
                                && !points.contains(gameBoard.getMyPosition().shiftLeft())
                                && !gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftLeft())
                                && (gameBoard.hasWallAt(gameBoard.getMyPosition().shiftBottom())   // ЕСЛИ ПОД НАМИ ЕСТЬ СТЕНА
                                || gameBoard.hasPipeAt(gameBoard.getMyPosition())              // ИЛИ МЫ ВИСИМ НА ТРУБЕ
                                || gameBoard.hasLadderAt(gameBoard.getMyPosition())            // ИЛИ МЫ НАХОДИМСЯ НА ЛЕСТНИЦЕ
                                || gameBoard.hasLadderAt(gameBoard.getMyPosition().shiftBottom())  // ИЛИ ПОД НАМИ ЛЕСТНИЦА
                                || gameBoard.myHasOtherHeroAt(gameBoard.getMyPosition().shiftBottom())   // ИЛИ ПОД НАМИ ДРУГОЙ ГЕРОЙ
                        )

                ) {
                    return LoderunnerAction.GO_LEFT;
                }
                else if (
                        !gameBoard.hasWallAt(gameBoard.getMyPosition().shiftRight())  // СПРАВА НЕТ СТЕНЫ
                                && !gameBoard.myHasOtherHeroAt(gameBoard.getMyPosition().shiftRight())
                                && !points.contains(gameBoard.getMyPosition().shiftRight())
                                && !gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftRight())
                                && (gameBoard.hasWallAt(gameBoard.getMyPosition().shiftBottom())   // ЕСЛИ ПОД НАМИ ЕСТЬ СТЕНА
                                || gameBoard.hasPipeAt(gameBoard.getMyPosition())              // ИЛИ МЫ ВИСИМ НА ТРУБЕ
                                || gameBoard.hasLadderAt(gameBoard.getMyPosition())            // ИЛИ МЫ НАХОДИМСЯ НА ЛЕСТНИЦЕ
                                || gameBoard.hasLadderAt(gameBoard.getMyPosition().shiftBottom())  // ИЛИ ПОД НАМИ ЛЕСТНИЦА
                                || gameBoard.myHasOtherHeroAt(gameBoard.getMyPosition().shiftBottom())   // ИЛИ ПОД НАМИ ДРУГОЙ ГЕРОЙ
                        )
                ) {
                    return LoderunnerAction.GO_LEFT;
                }
            }
        }

        count =  0;

        while (true) {
            List<Path> pathListBuff = new ArrayList<>();

            count++;

            for (Path path : pathList) {
                // ----------------------------
                // IF BOT IS FALLING DOWN
                // ----------------------------

                if (
                        ((gameBoard.hasEmptinessAt(path.bp.shiftBottom())               // ЕСЛИ ПОД НАМИ ПУСТОТА
                            || gameBoard.hasAlreadyDrilledPitAt(path.bp.shiftBottom())  // ИЛИ ПОД НАМИ ПРОСВЕРЛЕННАЯ ДЫРКА
                            || gameBoard.hasSomeInterestingAt(path.bp.shiftBottom())    // ИЛИ ПОД НАМИ ЗОЛОТО/ТАБЛЕТКА
                            || gameBoard.hasPortalAt(path.bp.shiftBottom())             // ИЛИ ПОД НАМИ ПОРТАЛ
                            || gameBoard.hasPipeAt(path.bp.shiftBottom()))              // ИЛИ ПОД НАМИ ТРУБА
                        && !gameBoard.hasLadderAt(path.bp)                              // ПРИ ЭТОМ МЫ НЕ ВИСИМ НА ЛЕСТНИЦЕ
                        && !gameBoard.hasPipeAt(path.bp)                                // НЕ ВИСИМ НА ТРУБЕ
                        && !gameBoard.hasOtherHeroAt(path.bp.shiftBottom())             // ВНИЗУ НЕТ ДРУГОГО ГЕРОЯ
                        && !gameBoard.hasEnemyAt(path.bp.shiftBottom())                 // И ВРАГА
                        && !points.contains(path.bp.shiftBottom()))
                ) {

                    if (gameBoard.hasSomeInterestingAt(path.bp.shiftBottom())) {
                        lastAction = path.firstAction == null ? LoderunnerAction.DO_NOTHING : path.firstAction;
                        return path.firstAction == null ? LoderunnerAction.DO_NOTHING : path.firstAction;
                    }
                    setPlus(path.bp.shiftBottom());
                    points.add(path.bp.shiftBottom());
                    pathListBuff.add(new Path(path.bp.shiftBottom(), LoderunnerAction.GO_DOWN,
                            path.firstAction == null ? LoderunnerAction.DO_NOTHING : path.firstAction));
                    continue;
                }


                // ----------------------------
                // DOWN
                // ----------------------------

                Path pathBufDown = new Path(path);

                if (gameBoard.hasSomeInterestingAt(pathBufDown.bp.shiftBottom())) {
                    lastAction = pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                    return pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction;
                }

                if (
                        ((gameBoard.hasLadderAt(pathBufDown.bp.shiftBottom())      // ЕСЛИ ПОД НАМИ ЛЕСТНИЦА
                          || gameBoard.hasEmptinessAt(pathBufDown.bp.shiftBottom())// ИЛИ ПОД НАМИ ПУСТОТА
                          || gameBoard.hasPipeAt(pathBufDown.bp.shiftBottom()))   // ИЛИ ПОД НАМИ ТРУБА, ТО
                            && (gameBoard.hasLadderAt(pathBufDown.bp)               // МЫ ДОЛЖНЫ БЫТЬ НА ЛЕСТНИЦЕ
                                || gameBoard.hasPipeAt(pathBufDown.bp)))           // ИЛИ НА ТРУБЕ
                        && !points.contains(pathBufDown.bp.shiftBottom())
                        && !gameBoard.myHasEnemyAt(pathBufDown.bp.shiftBottom())
                        && !gameBoard.myHasOtherHeroAt(pathBufDown.bp.shiftBottom())
                ) {
                        points.add(pathBufDown.bp.shiftBottom());
                        setPlus(pathBufDown.bp.shiftBottom());

                        pathListBuff.add(new Path(pathBufDown.bp.shiftBottom(), LoderunnerAction.GO_DOWN,
                                pathBufDown.firstAction == null ? LoderunnerAction.GO_DOWN : pathBufDown.firstAction));
//                    }
                }


                // ----------------------------
                // UP
                // ----------------------------

                Path pathBufUp = new Path(path);

                if (
                        gameBoard.hasLadderAt(pathBufUp.bp)
                                && !points.contains(pathBufUp.bp.shiftTop())
                                && (
                                gameBoard.hasEmptinessAt(pathBufUp.bp.shiftTop())
                                        || gameBoard.hasLadderAt(pathBufUp.bp.shiftTop())
                                        || gameBoard.hasPipeAt(pathBufUp.bp.shiftTop())
                                        || gameBoard.hasSomeInterestingAt(pathBufUp.bp.shiftTop())
                        )
                                && !gameBoard.myHasEnemyAt(pathBufUp.bp.shiftTop())
                ){

                    if (gameBoard.hasSomeInterestingAt(pathBufUp.bp.shiftTop())) {
                        lastAction = pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction;
                        return pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction;
                   
                    } else if (gameBoard.hasPortalAt(pathBufUp.bp.shiftTop()) && actionToPortal == null) {
                        actionToPortal = pathBufUp.firstAction == null ? LoderunnerAction.GO_UP : pathBufUp.firstAction;
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
                        && !gameBoard.myHasOtherHeroAt(pathBufLeft.bp.shiftLeft())
                        && !points.contains(pathBufLeft.bp.shiftLeft())
                        && !gameBoard.myHasEnemyAt(pathBufLeft.bp.shiftLeft())
                        && (gameBoard.hasWallAt(pathBufLeft.bp.shiftBottom())   // ЕСЛИ ПОД НАМИ ЕСТЬ СТЕНА
                            || gameBoard.hasPipeAt(pathBufLeft.bp)              // ИЛИ МЫ ВИСИМ НА ТРУБЕ
                            || gameBoard.hasLadderAt(pathBufLeft.bp)            // ИЛИ МЫ НАХОДИМСЯ НА ЛЕСТНИЦЕ
                            || gameBoard.hasLadderAt(pathBufLeft.bp.shiftBottom())  // ИЛИ ПОД НАМИ ЛЕСТНИЦА
                            || gameBoard.myHasOtherHeroAt(pathBufLeft.bp.shiftBottom())   // ИЛИ ПОД НАМИ ДРУГОЙ ГЕРОЙ
                        )

                ) {

                    if (gameBoard.hasSomeInterestingAt(pathBufLeft.bp.shiftLeft())) {
                        lastAction = pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                        return pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;
                   
                    } else if (gameBoard.hasPortalAt(pathBufLeft.bp.shiftLeft()) && actionToPortal == null) {
                        actionToPortal = pathBufLeft.firstAction == null ? LoderunnerAction.GO_LEFT : pathBufLeft.firstAction;

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
                        && !gameBoard.myHasOtherHeroAt(pathBufRight.bp.shiftRight())
                        && !points.contains(pathBufRight.bp.shiftRight())
                        && !gameBoard.myHasEnemyAt(pathBufRight.bp.shiftRight())
                        && (gameBoard.hasWallAt(pathBufRight.bp.shiftBottom())   // ЕСЛИ ПОД НАМИ ЕСТЬ СТЕНА
                            || gameBoard.hasPipeAt(pathBufRight.bp)              // ИЛИ МЫ ВИСИМ НА ТРУБЕ
                            || gameBoard.hasLadderAt(pathBufRight.bp)            // ИЛИ МЫ НАХОДИМСЯ НА ЛЕСТНИЦЕ
                            || gameBoard.hasLadderAt(pathBufRight.bp.shiftBottom())  // ИЛИ ПОД НАМИ ЛЕСТНИЦА
                            || gameBoard.myHasOtherHeroAt(pathBufRight.bp.shiftBottom())   // ИЛИ ПОД НАМИ ДРУГОЙ ГЕРОЙ
                        )
                ) {

                    if (gameBoard.hasSomeInterestingAt(pathBufRight.bp.shiftRight())) {
                        lastAction = pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                        return pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;
                    
                    } else if (gameBoard.hasPortalAt(pathBufRight.bp.shiftRight()) && actionToPortal == null) {
                        actionToPortal = pathBufRight.firstAction == null ? LoderunnerAction.GO_RIGHT : pathBufRight.firstAction;

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
                        && !gameBoard.myHasOtherHeroAt(pathBufDrill.bp.shiftLeft())
                        && !gameBoard.hasWallAt(pathBufDrill.bp.shiftLeft())) {
                    pathListBuff.add(new Path(pathBufDrill.bp.shiftLeft().shiftBottom(), LoderunnerAction.DRILL_LEFT,
                            pathBufDrill.firstAction == null ? LoderunnerAction.DRILL_LEFT : pathBufDrill.firstAction));
                }

                // ----------------------------
                // DRILL_RIGHT
                // ----------------------------

                if (gameBoard.hasDestroyableWallAt(pathBufDrill.bp.shiftRight().shiftBottom())
                        && !gameBoard.hasLadderAt(pathBufDrill.bp.shiftRight())
                        && !gameBoard.myHasOtherHeroAt(pathBufDrill.bp.shiftRight())
                        && !gameBoard.hasWallAt(pathBufDrill.bp.shiftRight())) {
                    pathListBuff.add(new Path(pathBufDrill.bp.shiftRight().shiftBottom(), LoderunnerAction.DRILL_RIGHT,
                            pathBufDrill.firstAction == null ? LoderunnerAction.DRILL_RIGHT : pathBufDrill.firstAction));
                }
            }
            pathList.clear();
            pathList = pathListBuff;

//            System.out.println("i: " + i + ", pathListSize: " + pathList.size() + ", " + pathListBuff.size());


            if (i > 5 && actionToPortal != null){
                lastAction = actionToPortal;
                return actionToPortal;
            }

            if (i > timeToSuicide) {
                if (gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftTop())
                        || gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftRight())
                        || gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftBottom())
                        || gameBoard.myHasEnemyAt(gameBoard.getMyPosition().shiftLeft())
                ) {
                    return LoderunnerAction.DO_NOTHING;
                }
                return LoderunnerAction.SUICIDE;
            }

            i++;
        }
    }

    public static void setPlus(BoardPoint p) {

//        String s = gameBoardList.get(p.getY());
//        char[] myNameChars = s.toCharArray();
////        System.out.println((char)(count % 10) + '0');
//        myNameChars[p.getX()] = (char) ((count % 10) + 'A');
////        myNameChars[p.getX()] = '+';
//        s = String.valueOf(myNameChars);
//        gameBoardList.set(p.getY(), s);
////        count++;
    }

    public void printMap(){
//        for ( String s : gameBoardList) {System.out.println(s);}

    }
}

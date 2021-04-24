package ru.codebattle.client.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBoard {

//    @Getter
    private final String boardString;

    public List<String> getBoardString(){
        List<String> board = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            board.add(boardString.substring(i * size(), size() * (i+1)));
        }


        return board;
    }

    public GameBoard(String boardString) {
        this.boardString = boardString.replace("\n", "");
    }

    public int size() {
        return (int) Math.sqrt(boardString.length());
    }

    public BoardPoint getMyPosition() {
        List<BoardPoint> result = findAllElements(BoardElement.HERO_DIE);
        result.addAll(findAllElements(BoardElement.HERO_DRILL_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_DRILL_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_FALL_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_FALL_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_LADDER));
        result.addAll(findAllElements(BoardElement.HERO_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_DRILL_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_DRILL_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_LADDER));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_FALL_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_FALL_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_RIGHT));
        return result.get(0);
    }

    public boolean isGameOver() {
        return boardString.contains("" + BoardElement.HERO_DIE.getSymbol());
    }

    public boolean hasElementAt(BoardPoint point, BoardElement element) {
        if (point.isOutOfBoard(size())) {
            return false;
        }

        return getElementAt(point) == element;
    }

    public BoardElement getElementAt(BoardPoint point) {
        return BoardElement.valueOf(boardString.charAt(getShiftByPoint(point)));
    }

    public void printBoard() {
        for (int i = 0; i < size(); i++) {
            System.out.println(boardString.substring(i * size(), size() * (i+1)));
        }
    }

    public List<BoardPoint> findAllElements(BoardElement element) {
        List<BoardPoint> result = new ArrayList<>();

        for (int i = 0; i < size() * size(); i++) {
            BoardPoint pt = getPointByShift(i);

            if (hasElementAt(pt, element)) {
                result.add(pt);
            }
        }

        return result;
    }

    public List<BoardPoint> getEnemyPositions() {
        List<BoardPoint> result = findAllElements(BoardElement.ENEMY_LADDER);

        result.addAll(findAllElements(BoardElement.ENEMY_LEFT));
        result.addAll(findAllElements(BoardElement.ENEMY_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.ENEMY_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.ENEMY_RIGHT));
        result.addAll(findAllElements(BoardElement.ENEMY_PIT));
        return result;
    }

    public List<BoardPoint> getOtherHeroPositions() {
        List<BoardPoint> result = findAllElements(BoardElement.OTHER_HERO_LEFT);
        result.addAll(findAllElements(BoardElement.OTHER_HERO_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_LADDER));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_DRILL_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_DRILL_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_FALL_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_FALL_RIGHT));

        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_LADDER));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_DRILL_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_DRILL_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_FALL_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_FALL_RIGHT));

        return result;
    }

    public List<BoardPoint> getShadowPills() {
        return findAllElements(BoardElement.SHADOW_PILL);
    }

    public List<BoardPoint> getPortals() {
        return findAllElements(BoardElement.PORTAL);
    }

    public List<BoardPoint> getWallPositions() {
        List<BoardPoint> result = findAllElements(BoardElement.BRICK);
        result.addAll(findAllElements(BoardElement.UNDESTROYABLE_WALL));
        return result;
    }

    public List<BoardPoint> getLadderPositions() {
        List<BoardPoint> result = findAllElements(BoardElement.LADDER);
        result.addAll(findAllElements(BoardElement.HERO_LADDER));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_LADDER));
        result.addAll(findAllElements(BoardElement.ENEMY_LADDER));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_LADDER));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_LADDER));
        return result;
    }

    public List<BoardPoint> getGoldPositions() {
        List<BoardPoint> result = findAllElements(BoardElement.YELLOW_GOLD);
        result.addAll(findAllElements(BoardElement.GREEN_GOLD));
        result.addAll(findAllElements(BoardElement.RED_GOLD));
        return result;
    }

    public List<BoardPoint> getPipePositions() {
        List<BoardPoint> result = findAllElements(BoardElement.PIPE);

        result.addAll(findAllElements(BoardElement.HERO_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.ENEMY_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.ENEMY_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_RIGHT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_LEFT));
        result.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_RIGHT));
        return result;
    }

    public List<BoardPoint> getBarriers() {
        return getWallPositions();
    }

    //MY OWN:
    public List<BoardPoint> getNones() {
        return findAllElements(BoardElement.NONE);
    }

    //MY OWN:
    public List<BoardPoint> getDestroyableWallPositions() {
        return findAllElements(BoardElement.BRICK);
    }

    //MY OWN:
    public List<BoardPoint> getDrillPits() {
        return findAllElements(BoardElement.DRILL_PIT);
    }

    //MY OWN:
    public List<BoardPoint> getAlreadyDrilledPits() {
        List<BoardPoint> result = findAllElements(BoardElement.PIT_FILL_1);
        result.addAll(findAllElements(BoardElement.PIT_FILL_2));
        result.addAll(findAllElements(BoardElement.PIT_FILL_3));
        result.addAll(findAllElements(BoardElement.PIT_FILL_4));
        return result;
    }

    public boolean hasElementAt(BoardPoint point, BoardElement... elements) {
        return Arrays.stream(elements).anyMatch(element -> hasElementAt(point, element));
    }

    public boolean isNearToElement(BoardPoint point, BoardElement element) {
        if (point.isOutOfBoard(size()))
            return false;

        return hasElementAt(point.shiftBottom(), element)
                || hasElementAt(point.shiftTop(), element)
                || hasElementAt(point.shiftLeft(), element)
                || hasElementAt(point.shiftRight(), element);
    }

    public boolean hasEnemyAt(BoardPoint point) {
        return getEnemyPositions().contains(point);
    }

    public boolean hasOtherHeroAt(BoardPoint point) {
        return getOtherHeroPositions().contains(point);
    }

    public boolean hasWallAt(BoardPoint point) {
        return getWallPositions().contains(point);
    }

    public boolean hasLadderAt(BoardPoint point) {
        return getLadderPositions().contains(point);
    }

    public boolean hasGoldAt(BoardPoint point) {
        return getGoldPositions().contains(point);
    }

    public boolean hasPipeAt(BoardPoint point) {
        return getPipePositions().contains(point);
    }

    public boolean hasShadowAt(BoardPoint point) {
        return getShadows().contains(point);
    }

    public boolean hasPortalAt(BoardPoint point) {
        return getPortals().contains(point);
    }

    public boolean hasBarrierAt(BoardPoint point){
        return getBarriers().contains(point);
    }

    //MY OWN:
    public boolean hasEmptinessAt(BoardPoint point) {
        return getNones().contains(point);
    }

    //MY OWN:
    public boolean hasDestroyableWallAt(BoardPoint point) {
        return getDestroyableWallPositions().contains(point);
    }

    //MY OWN:
    public boolean hasDrillPitAt(BoardPoint point) {
        return getDrillPits().contains(point);
    }

    //MY OWN:
    public boolean hasAlreadyDrilledPitAt(BoardPoint point) {
        return getAlreadyDrilledPits().contains(point);
    }

    //MY OWN:
    public boolean hasShadowPillAt(BoardPoint point) {
        return getShadowPills().contains(point);
    }

    //MY OWN:
    public boolean hasSomeInteresting(BoardPoint p){
        return hasGoldAt(p) || hasShadowPillAt(p);
    }


    private List<BoardPoint> getShadows() {
        List<BoardPoint> shadows = findAllElements(BoardElement.HERO_SHADOW_LEFT);
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_RIGHT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_LADDER));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_LEFT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_PIPE_RIGHT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_DRILL_LEFT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_DRILL_RIGHT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_FALL_LEFT));
        shadows.addAll(findAllElements(BoardElement.HERO_SHADOW_FALL_RIGHT));

        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_LEFT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_RIGHT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_LADDER));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_LEFT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_PIPE_RIGHT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_DRILL_LEFT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_DRILL_RIGHT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_FALL_LEFT));
        shadows.addAll(findAllElements(BoardElement.OTHER_HERO_SHADOW_FALL_RIGHT));
        return shadows;
    }

    public int getCountElementsNearToPoint(BoardPoint point, BoardElement element) {
        if (point.isOutOfBoard(size()))
            return 0;

        return boolToInt(hasElementAt(point.shiftLeft(), element)) +
                boolToInt(hasElementAt(point.shiftRight(), element)) +
                boolToInt(hasElementAt(point.shiftTop(), element)) +
                boolToInt(hasElementAt(point.shiftBottom(), element));
    }

    private int getShiftByPoint(BoardPoint point) {
        return point.getY() * size() + point.getX();
    }

    private BoardPoint getPointByShift(int shift) {
        return new BoardPoint(shift % size(), shift / size());
    }

    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
}

package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// used to find how safe the king is from the enemy pieces
public final class KingSafetyAnalyzer {

    private static final KingSafetyAnalyzer INSTANCE = new KingSafetyAnalyzer();
    @SuppressWarnings("unused")
    private static final List<List<Boolean>> COLUMNS = initColumns();
    private KingSafetyAnalyzer() {
    }
    public static KingSafetyAnalyzer get() {
        return INSTANCE;
    }
    // initializes the column values for the board
    private static List<List<Boolean>> initColumns() {
        final List<List<Boolean>> columns = new ArrayList<>();
        columns.add(BoardUtils.INSTANCE.FIRST_COLUMN);
        columns.add(BoardUtils.INSTANCE.SECOND_COLUMN);
        columns.add(BoardUtils.INSTANCE.THIRD_COLUMN);
        columns.add(BoardUtils.INSTANCE.FOURTH_COLUMN);
        columns.add(BoardUtils.INSTANCE.FIFTH_COLUMN);
        columns.add(BoardUtils.INSTANCE.SIXTH_COLUMN);
        columns.add(BoardUtils.INSTANCE.SEVENTH_COLUMN);
        columns.add(BoardUtils.INSTANCE.EIGHTH_COLUMN);
        return ImmutableList.copyOf(columns);
    }
    // calculates the distance between the king and closest enemy piece
    public KingDistance calculateKingTropism(final Player player) {
        final int playerKingSquare = player.getPlayerKing().getPiecePosition();
        final Collection<Move> enemyMoves = player.getOpponent().getLegalMoves();
        Piece closestPiece = null;
        int closestDistance = Integer.MAX_VALUE;
        for(final Move move : enemyMoves) {
            final int currentDistance = calculateChebyshevDistance(playerKingSquare, move.getDestinationCoordinate());
            if(currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestPiece = move.getMovedPiece();
            }
        }
        return new KingDistance(closestPiece, closestDistance);
    }
    // used to calculate the chebyshev distance 
    // between 2 tiles
    private static int calculateChebyshevDistance(final int kingTileId,
                                           final int enemyAttackTileId) {
        final int rankDistance = Math.abs(getRank(enemyAttackTileId) - getRank(kingTileId));
        final int fileDistance = Math.abs(getFile(enemyAttackTileId) - getFile(kingTileId));
        return Math.max(rankDistance, fileDistance);
    }
    // returns the file of the given tile (i.e. column)
    private static int getFile(final int coordinate) {
        if(BoardUtils.INSTANCE.FIRST_COLUMN.get(coordinate)) {
            return 1;
        } else if(BoardUtils.INSTANCE.SECOND_COLUMN.get(coordinate)) {
            return 2;
        } else if(BoardUtils.INSTANCE.THIRD_COLUMN.get(coordinate)) {
            return 3;
        } else if(BoardUtils.INSTANCE.FOURTH_COLUMN.get(coordinate)) {
            return 4;
        } else if(BoardUtils.INSTANCE.FIFTH_COLUMN.get(coordinate)) {
            return 5;
        } else if(BoardUtils.INSTANCE.SIXTH_COLUMN.get(coordinate)) {
            return 6;
        } else if(BoardUtils.INSTANCE.SEVENTH_COLUMN.get(coordinate)) {
            return 7;
        } else if(BoardUtils.INSTANCE.EIGHTH_COLUMN.get(coordinate)) {
            return 8;
        }
        throw new RuntimeException("should not reach here!");
    }
    // returns the rank of the given tile (i.e. row)
    private static int getRank(final int coordinate) {
        if(BoardUtils.INSTANCE.FIRST_ROW.get(coordinate)) {
            return 1;
        } else if(BoardUtils.INSTANCE.SECOND_ROW.get(coordinate)) {
            return 2;
        } else if(BoardUtils.INSTANCE.THIRD_ROW.get(coordinate)) {
            return 3;
        } else if(BoardUtils.INSTANCE.FOURTH_ROW.get(coordinate)) {
            return 4;
        } else if(BoardUtils.INSTANCE.FIFTH_ROW.get(coordinate)) {
            return 5;
        } else if(BoardUtils.INSTANCE.SIXTH_ROW.get(coordinate)) {
            return 6;
        } else if(BoardUtils.INSTANCE.SEVENTH_ROW.get(coordinate)) {
            return 7;
        } else if(BoardUtils.INSTANCE.EIGHTH_ROW.get(coordinate)) {
            return 8;
        }
        throw new RuntimeException("should not reach here!");
    }
    // calculate the distance between the king and a given enemy piece
    static class KingDistance {
        final Piece enemyPiece;
        final int distance;
        KingDistance(final Piece enemyDistance,
                     final int distance) {
            this.enemyPiece = enemyDistance;
            this.distance = distance;
        }
        public Piece getEnemyPiece() {
            return enemyPiece;
        }
        public int getDistance() {
            return distance;
        }
        public int tropismScore() {
            return (enemyPiece.getPieceValue()/10) * distance;
        }
    }
}

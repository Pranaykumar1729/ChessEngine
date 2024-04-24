package com.chess.engine.classic.player;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Move.MoveStatus;
import com.chess.engine.classic.board.MoveTransition;
import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.pieces.Piece;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.chess.engine.classic.board.MoveUtils.NULL_MOVE;
import static com.chess.engine.classic.pieces.Piece.PieceType.KING;
import static java.util.stream.Collectors.collectingAndThen;

// this class is made to represent the eack player's common characteristics in the game
@SuppressWarnings("unused")
public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;
    Player(final Board board,
           final Collection<Move> playerLegals,
           final Collection<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
        playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
        this.legalMoves = Collections.unmodifiableCollection(playerLegals);
    }
    // check whether the king is in check or not
    public boolean isInCheck() {
        return this.isInCheck;
    }
    // check wheather the King is in Check mate or not
    public boolean isInCheckMate() {
       return this.isInCheck && !hasEscapeMoves();
    }
    // check whether the King is in Stale Mate or not
    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }
    // check weather the Castle Move Happened or Not
    public boolean isCastled() {
        return this.playerKing.isCastled();
    }
    // check whether the king is elegible to make the castling on kings's side
    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }
    // check whether the king is elegible to make the castling on queens's side
    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }
    public King getPlayerKing() {
        return this.playerKing;
    }

    private King establishKing() {
        return (King) getActivePieces().stream()
                                       .filter(piece -> piece.getPieceType() == KING)
                                       .findAny()
                                       .orElseThrow(RuntimeException::new);
    }
    // checking whether the king has an escape moves when he is in check
    private boolean hasEscapeMoves() {
        return this.legalMoves.stream()
                              .anyMatch(move -> makeMove(move)
                              .getMoveStatus().isDone());
    }
    // retrive all the legal moves of the Player
    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }
    // Attacks on the current tile
    static Collection<Move> calculateAttacksOnTile(final int tile,
                                                   final Collection<Move> moves) {
        return moves.stream()
                    .filter(move -> move.getDestinationCoordinate() == tile)
                    .collect(collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
    // make the move and create new board with the new move
    public MoveTransition makeMove(final Move move) {
        if (!this.legalMoves.contains(move)) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        return transitionedBoard.currentPlayer().getOpponent().isInCheck() ?
                new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }
    // undo a move
    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }
    protected boolean hasCastleOpportunities() {
        return !this.isInCheck && !this.playerKing.isCastled() &&
                (this.playerKing.isKingSideCastleCapable() || this.playerKing.isQueenSideCastleCapable());
    }
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentLegals);
}

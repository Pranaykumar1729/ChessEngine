package com.chess.engine.classic.pieces;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// class used to represent the pawn type
public final class Pawn
        extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};
    public Pawn(final Alliance allegiance,
                final int piecePosition) {
        super(PieceType.PAWN, allegiance, piecePosition, true);
    }
    public Pawn(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove) {
        super(PieceType.PAWN, alliance, piecePosition, isFirstMove);
    }
    // used to give bonus to pawn based on its location
    @Override
    public int locationBonus() {
        return this.getPieceAlliance().pawnBonus(this.getPiecePosition());
    }
    // used to calculate the legal moves of the pawn
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate =
                    this.getPiecePosition() + (this.getPieceAlliance().getDirection() * currentCandidateOffset);
            // checks if the destination tile is valid or not
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            // checks if the major move is possible or not
            if (currentCandidateOffset == 8 && board.getPiece(candidateDestinationCoordinate) == null) {
                if (this.getPieceAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedQueen(this.getPieceAlliance(), candidateDestinationCoordinate)));
                }
                else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }
            // checks if the pawn jump is possible or not
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.INSTANCE.SECOND_ROW.get(this.getPiecePosition()) && this.getPieceAlliance().isBlack()) ||
                     (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.getPiecePosition()) && this.getPieceAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate =
                        this.getPiecePosition() + (this.getPieceAlliance().getDirection() * 8);
                if (board.getPiece(candidateDestinationCoordinate) == null &&
                    board.getPiece(behindCandidateDestinationCoordinate) == null) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // checks if attack move is possible or not
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.getPiecePosition()) && this.getPieceAlliance().isWhite()) ||
                      (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.getPiecePosition()) && this.getPieceAlliance().isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    final Piece pieceOnCandidate = board.getPiece(candidateDestinationCoordinate);
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        // checks if promotion move
                        if (this.getPieceAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedQueen(this.getPieceAlliance(), candidateDestinationCoordinate)));
                        }
                        // executes attack move
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } 
                // check for enpassant move
                else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                           (this.getPiecePosition() + (this.getPieceAlliance().getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        legalMoves.add(
                                new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
            // checks if attack move is possible or not
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.INSTANCE.FIRST_COLUMN.get(this.getPiecePosition()) && this.getPieceAlliance().isWhite()) ||
                      (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.getPiecePosition()) && this.getPieceAlliance().isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    if (this.getPieceAlliance() !=
                            board.getPiece(candidateDestinationCoordinate).getPieceAlliance()) {
                        // checks if promotion move
                        if (this.getPieceAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)),
                                            PieceUtils.INSTANCE.getMovedQueen(this.getPieceAlliance(),
                                            candidateDestinationCoordinate)));
                        }
                        // executes attack move
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)));
                        }
                    }
                } 
                //checks for enpassant move
                else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.getPiecePosition() - (this.getPieceAlliance().getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        legalMoves.add(
                                new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    @Override
    public String toString() {
        return this.getPieceType().toString();
    }
    @Override
    public Pawn movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedPawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }
}
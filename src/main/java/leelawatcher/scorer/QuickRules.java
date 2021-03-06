/*
    Copyright 2017 Patrick G. Heck

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package leelawatcher.scorer;

import leelawatcher.goboard.Board;
import leelawatcher.goboard.MarkablePosition;
import leelawatcher.goboard.PointOfPlay;
import leelawatcher.goboard.Position;
import leelawatcher.goboard.move.Move;
import leelawatcher.goboard.move.MoveNode;
import leelawatcher.goboard.move.RootNode;

import java.util.Iterator;

public class QuickRules extends AbstractRules {
  public QuickRules() {
    super();
  }

  public boolean isLegalMove(PointOfPlay p, Board board) {
    // it is always legal to pass
    return p.getX() == Move.PASS || (isEmpty(p, board) && !isSelfCapture(p, board) && !isKo(p, board));
  }

  public boolean isSelfCapture(PointOfPlay p, Board board) {
    // we must build a MarkablePosition that shows the board as it would be
    // if the stone were placed in order to test if this would result in self
    // capture (illegal in most rules of the game)
    Move tmpRoot = new RootNode();
    char color = board.isWhiteMove() ? Move.MOVE_WHITE : Move.MOVE_BLACK;
    Move testMove = new MoveNode(p.getX(), p.getY(), color, tmpRoot);
    Position tmpPos = new Position(board.getCurrPos(), testMove);
    MarkablePosition testMPos = new MarkablePosition(tmpPos);
    //testMPos.dPrint();

    //System.out.println(captureOpponent);
    PointOfPlay neighbor = new PointOfPlay(p.getX(), p.getY() + 1);
    boolean captureOpponent = board.isOnBoard(neighbor)
        && countLibs(neighbor, 0, testMPos, board) == 0
        && testMPos.colorAt(p) != testMPos.colorAt(neighbor);
    testMPos.clearMarks();
    neighbor = new PointOfPlay(p.getX() + 1, p.getY());
    //System.out.println(captureOpponent);
    captureOpponent = (captureOpponent
        || ((board.isOnBoard(neighbor)
            && (countLibs(neighbor, 0, testMPos, board) == 0))
            && (testMPos.colorAt(p) != testMPos.colorAt(neighbor))));
    testMPos.clearMarks();
    neighbor = new PointOfPlay(p.getX(), p.getY() - 1);
    //System.out.println(captureOpponent);
    captureOpponent = (captureOpponent
        || ((board.isOnBoard(neighbor)
            && (countLibs(neighbor, 0, testMPos, board) == 0))
            && (testMPos.colorAt(p) != testMPos.colorAt(neighbor))));
    testMPos.clearMarks();
    neighbor = new PointOfPlay(p.getX() - 1, p.getY());
    //System.out.println(captureOpponent);
    captureOpponent = (captureOpponent
        || ((board.isOnBoard(neighbor)
            && (countLibs(neighbor, 0, testMPos, board) == 0))
            && (testMPos.colorAt(p) != testMPos.colorAt(neighbor))));
    testMPos.clearMarks();
    //testMPos.dPrint();
    //System.out.println(countLibs(p,0,testMPos));
    //testMPos.clearMarks();
    //System.out.println(captureOpponent);
    return (countLibs(p, 0, testMPos, board) == 0) && !captureOpponent;
  }

  @Override
  public boolean isSelfCaptureAllowed() {
    return false;
  }

  public boolean isKo(PointOfPlay p, Board board) {
    Move tmproot = new RootNode();
    int stonesRemoved = 0;
    char color = Move.MOVE_BLACK;

    PointOfPlay neighbor;
    Move testMove;
    Position tmpPos;
    MarkablePosition testMPos;

    // we must build a MarkablePosition that shows the board as it would be
    // if the stone were placed in order to test if this would result in self
    // capture (illegal in most rules of the game)

    if (board.isWhiteMove()) {
      color = Move.MOVE_WHITE;
    }
    testMove = new MoveNode(p.getX(), p.getY(), color, tmproot);
    tmpPos = new Position(board.getCurrPos(), testMove);
    testMPos = new MarkablePosition(tmpPos);

    neighbor = new PointOfPlay(p.getX(), p.getY() + 1);
    if (board.isOnBoard(neighbor)
        && (countLibs(neighbor, 0, testMPos, board) == 0)
        && (testMPos.getGroupSet(neighbor,
        null,
        board.getBoardSize()).size() == 1)) {
      //System.out.println("rem + y");
      ++stonesRemoved;
      testMPos.removeStoneAt(neighbor);
    }

    neighbor = new PointOfPlay(p.getX() + 1, p.getY());
    if (board.isOnBoard(neighbor)
        && (countLibs(neighbor, 0, testMPos, board) == 0)
        && (testMPos.getGroupSet(neighbor,
        null,
        board.getBoardSize()).size() == 1)) {
      //System.out.println("rem + x");
      ++stonesRemoved;
      testMPos.removeStoneAt(neighbor);
    }
    neighbor = new PointOfPlay(p.getX(), p.getY() - 1);
    if (board.isOnBoard(neighbor)
        && (countLibs(neighbor, 0, testMPos, board) == 0)
        && (testMPos.getGroupSet(neighbor,
        null,
        board.getBoardSize()).size() == 1)) {
      //System.out.println("rem - y");
      ++stonesRemoved;
      testMPos.removeStoneAt(neighbor);
    }
    neighbor = new PointOfPlay(p.getX() - 1, p.getY());
    if (board.isOnBoard(neighbor)
        && (countLibs(neighbor, 0, testMPos, board) == 0)
        && (testMPos.getGroupSet(neighbor,
        null,
        board.getBoardSize()).size() == 1)) {
      //System.out.println("rem - x");
      ++stonesRemoved;
      testMPos.removeStoneAt(neighbor);
    }

    //System.out.println(stonesRemoved);
    if (stonesRemoved == 1) {
      for (Iterator<Position> i = board.getPosIter(); i.hasNext(); ) {
        if (i.next().equals(testMPos)) {
          return true;
        }
      }
    }
    return false;
  }
}

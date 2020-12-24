package ca.borysserbyn.jeffbot;
import ca.borysserbyn.*;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class NodeTest {
    private static int maxTestDepth = 5;
    private static int testBreadth = 5;

    public static void main(String args[]){
        for (int i = 2; i <= maxTestDepth; i++) {
            Board board = new Board(1);
            Node baseNode = new Node(0, null, null, i, testBreadth, Color.WHITE);
            long start_time = System.nanoTime();
            baseNode.addNodes(board, 0);
            long end_time = System.nanoTime();
            System.out.println((end_time - start_time) / 1e6);
        }
    }
}

package borys.serbyn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ChessGUI{
    private Board board;
    private PieceButton originButton;

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private PieceButton[][] chessBoardSquares = new PieceButton[8][8];
    private JPanel chessBoard;
    private JPanel graveyardPanel;
    private JScrollPane graveyardScroll;
    private final JLabel message = new JLabel(
            "Jeff bot is not ready yet.");
    private static final String COLS = "ABCDEFGH";

    ChessGUI() {
        board = new Board(1);
        initializeGui();
    }
    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }



    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e){
        PieceButton selectedButton = (PieceButton)e.getSource();
        Piece selectedPiece = selectedButton.getPiece();
        if(originButton == null && selectedPiece != null){ //is there a piece in the selected square and was a piece already selected
            if(selectedPiece.getColor() == board.getTurn()){//is it that colors turn to move
                originButton = selectedButton;
            }
        }else if(originButton != null){ //is there a piece to be moved
            if(selectedPiece == null){
                movePiece(selectedButton);
            }else if(selectedPiece.getColor() == originButton.getPiece().getColor()){
                originButton = selectedButton;
            }else{
                movePiece(selectedButton);
            }
        }
    }

    //Handles piece movement in the gui and in the logic of the board.
    public void movePiece(PieceButton selectedButton){
        if(board.isMoveLegal(originButton.getPiece(), selectedButton.getTile())){ //is the move legal
            Piece selectedPiece = selectedButton.getPiece();
            selectedButton.setAndMovePiece(board, originButton.getPiece());
            originButton.removePiece();

            System.out.println(board.getState());
            if(board.getState() == BoardState.PIECE_EATEN || board.getState() == BoardState.PROMOTING_AND_EATING){ //add normaly eaten piece to graveyard.
                sendPieceToGraveyard(selectedPiece);
            }else if(board.getState() == BoardState.EN_PASSANT){//remove enpassanted pieces and add to the graveyard.
                PieceButton deadPieceButton = chessBoardSquares[selectedButton.getTile().getX()][originButton.getTile().getY()];
                sendPieceToGraveyard(deadPieceButton.getPiece());
                deadPieceButton.removePiece();
            }else if(board.getState() == BoardState.CASTLING_SHORT || board.getState() == BoardState.CASTLING_LONG){//move castle if castling short
                castleMovement(selectedButton);
            }

            if(board.getState() == BoardState.PROMOTING_AND_EATING || board.getState() == BoardState.PROMOTING_PAWN){
                displayPromotionWindow(selectedButton);
            }
        }
        if(selectedButton.getPiece() != null){
            board.isPieceThreatened(selectedButton.getPiece());
        }
        originButton = null;
    }

    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(PieceButton selectedButton){
        String[] choices = { "QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(gui, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        board.promotingPawn(selectedButton.getPiece(), chosenPieceName);
        selectedButton.updateIcon();

    }

    //Handles castling in the gui.
    public void castleMovement(PieceButton selectedButton){
        PieceButton rookButton;
        PieceButton moveButton;
        int rookMove;
        int x = selectedButton.getTile().getX();

        if(board.getOrientation() == 1){
            rookMove = board.getState() == BoardState.CASTLING_SHORT ? 2 : -3;
            rookButton = board.getState() == BoardState.CASTLING_SHORT ? chessBoardSquares[0][selectedButton.getTile().getY()] : chessBoardSquares[7][selectedButton.getTile().getY()];
        }else{

            rookMove = board.getState() == BoardState.CASTLING_SHORT ? -2 : 3;
            rookButton = board.getState() == BoardState.CASTLING_LONG ? chessBoardSquares[0][selectedButton.getTile().getY()] : chessBoardSquares[7][selectedButton.getTile().getY()];
        }

        Piece rook = rookButton.getPiece();
        System.out.println(board.getState());
        moveButton = chessBoardSquares[rookButton.getTile().getX()+rookMove][rookButton.getTile().getY()];
        moveButton.setPiece(rook);
        rookButton.removePiece();
    }

    //Sends piece to the gui graveyard (not the boards)
    public void sendPieceToGraveyard(Piece deadPiece){
        JLabel deadPieceLabel = new JLabel("");
        deadPieceLabel.setIcon(new ImageIcon(ChessSprites.getSpriteByPiece(deadPiece)));
        graveyardPanel.add(deadPieceLabel);
        graveyardPanel.revalidate();
        graveyardPanel.repaint();
    }


    /*
    Initializes the gui with a new board
     */
    public final void initializeGui() {
        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        tools.add(new JButton("New")); // TODO - add functionality!
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);
        originButton = null;

        //add the graveyard to the gui
        graveyardPanel = new JPanel();
        graveyardScroll = new JScrollPane(graveyardPanel);
        graveyardScroll.setPreferredSize(new Dimension(512, 100));
        gui.add(graveyardScroll, BorderLayout.SOUTH);

        //add the chessboard to the gui
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(chessBoard);

        // create the chess board squares
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                PieceButton b = new PieceButton(board.getTileByPosition(jj, ii));
                b.setMargin(buttonMargin);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        clickTile(e);
                    }
                } );
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }

        //add the pieces to the board
        for(Piece piece:board.getPieces()){
            int x = piece.getTile().getX();
            int y = piece.getTile().getY();
            chessBoardSquares[x][y].setPiece(piece);
            chessBoardSquares[x][y].repaint();
        }

        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                        chessBoard.repaint();
                }
            }
        }
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ChessGUI cb =
                        new ChessGUI();

                JFrame f = new JFrame("Jeff Bot");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}

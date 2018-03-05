package dk.easv.gui;

import com.jfoenix.controls.JFXButton;

import dk.easv.bll.bot.*;
// https://bintray.com/jerady/maven/FontAwesomeFX/8.15#files/de%2Fjensd%2Ffontawesomefx-commons%2F8.15
// https://bintray.com/jerady/maven/FontAwesomeFX/8.15#files/de%2Fjensd%2Ffontawesomefx-fontawesome%2F4.7.0-5
// https://bintray.com/jerady/maven/FontAwesomeFX/8.15#files/de%2Fjensd%2Ffontawesomefx-materialdesignfont%2F1.7.22-4
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UTTTGameController implements Initializable {

    private static final long BOTDELAY = 500;
    @FXML
    private GridPane gridMacro;

    @FXML
    private StackPane stackMain;

    private final GridPane[][] gridMicros = new GridPane[3][3];
    private final JFXButton[][] jfxButtons = new JFXButton[9][9];

    BoardModel model;
    IBot bot0 = null;
    IBot bot1 = null;
    String player0 = null;
    String player1 = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridMacro.toFront(); // Or the buttons will not work
        createMicroGridPanes();
    }

    public void startGame() {
        if (model != null) {
            model.removeListener(observable -> update());
        }
        model.addListener(observable -> update());

        // HumanVsHuman
        if (player0 != null && player1 != null) {

        }
        // HumanVsAI
        else if (bot1 != null && player0 != null) {

        }
        // AIvsHuman
        else if (bot0 != null && player1 != null) {
            doBotMove();
        }
        // AIvsAI
        else if (bot0 != null && bot1 != null) {
            Thread t = new Thread(() -> {
                while (model.getGameOverState() == GameManager.GameOverState.Active) {
                    doBotMove();
                    try {
                        Thread.sleep(BOTDELAY);
                    }
                    catch (InterruptedException ex) {
                        Logger.getLogger(UTTTGameController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
            t.setDaemon(true); // Stops thread when main thread dies
            t.start();
        }
    }

    private void doBotMove() {
        int currentPlayer = model.getCurrentPlayer();
        Boolean valid = model.doMove();
        checkAndLockIfGameEnd(currentPlayer);
    }

    private boolean doMove(IMove move) {
        int currentPlayer = model.getCurrentPlayer();
        boolean validMove = model.doMove(move);
        checkAndLockIfGameEnd(currentPlayer);
        return validMove;
    }

    private String getWinnerName(int winnerId) {
        if (winnerId == 0) {
            if (bot0 != null) {
                return bot0.getBotName();
            }
            else {
                return player0;
            }
        }
        else if (winnerId == 1) {
            if (bot1 != null) {
                return bot1.getBotName();
            }
            else {
                return player1;
            }
        }
        throw new RuntimeException("Player id not found " + winnerId);
    }

    private void showWinnerPane(String winner) {
        String winMsg;
        if (winner.equalsIgnoreCase("TIE")) {
            winMsg = "Game tie";
        }
        else {
            winMsg = getWinnerName(Integer.parseInt(winner)) + " wins";
        }

        Label lblWinAnnounce = new Label(winMsg);
        lblWinAnnounce.setAlignment(Pos.CENTER);
        lblWinAnnounce.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lblWinAnnounce.getStyleClass().add("winner-text");
        lblWinAnnounce.getStyleClass().add("player" + winner);

        Label lbl = new Label();
        lbl.getStyleClass().add("winmsg");
        lbl.getStyleClass().add("player" + winner);

        lbl.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);

        Text fontAwesomeIcon = getFontAwesomeIconFromPlayerId(winner + "");
        lbl.setGraphic(fontAwesomeIcon);
        GridPane gridPane = new GridPane();
        gridPane.addColumn(0);
        gridPane.addRow(0);
        gridPane.addRow(1);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100);
        cc.setHgrow(Priority.ALWAYS); // allow column to grow
        cc.setFillWidth(true); // ask nodes to fill space for column
        gridPane.getColumnConstraints().add(cc);

        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS); // allow row to grow
        rc.setFillHeight(true);
        rc.setPercentHeight(90);
        gridPane.getRowConstraints().add(rc);
        RowConstraints rc2 = new RowConstraints();
        rc2.setVgrow(Priority.ALWAYS); // allow row to grow
        rc2.setFillHeight(true);
        rc2.setPercentHeight(10);
        gridPane.getRowConstraints().add(rc2);

        gridPane.add(lbl, 0, 0);
        gridPane.add(lblWinAnnounce, 0, 1);
        gridPane.setGridLinesVisible(true);

        Platform.runLater(() -> stackMain.getChildren().add(gridPane));

    }

    private void createMicroGridPanes() {
        for (int i = 0; i < 3; i++) {
            gridMacro.addRow(i);
            for (int k = 0; k < 3; k++) {
                GridPane gp = new GridPane();
                for (int m = 0; m < 3; m++) {
                    gp.addColumn(m);
                    gp.addRow(m);
                }
                gridMicros[i][k] = gp;
                for (int j = 0; j < 3; j++) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setPercentWidth(33);
                    cc.setHgrow(Priority.ALWAYS); // allow column to grow
                    cc.setFillWidth(true); // ask nodes to fill space for column
                    gp.getColumnConstraints().add(cc);

                    RowConstraints rc = new RowConstraints();
                    rc.setVgrow(Priority.ALWAYS); // allow row to grow
                    rc.setFillHeight(true);
                    rc.setPercentHeight(33);
                    gp.getRowConstraints().add(rc);
                }

                gp.setGridLinesVisible(true);
                gridMacro.addColumn(k);
                gridMacro.add(gp, i, k);
            }
        }
        insertButtonsIntoGridPanes();
    }

    private void insertButtonsIntoGridPanes() {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                GridPane gp = gridMicros[i][k];
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        JFXButton btn = new JFXButton("");
                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                        btn.getStyleClass().add("tictaccell");
                        btn.setUserData(new Move(x + i * 3, y + k * 3));
                        btn.setFocusTraversable(false);
                        btn.setOnMouseClicked(
                                event -> {
                                    doMove((IMove) btn.getUserData()); // Player move

                                    boolean isHumanVsBot = player0 != null ^ player1 != null;
                                    if (isHumanVsBot) {
                                        doBotMove();
                                    }
                                }
                        );
                        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        gp.add(btn, x, y);
                        jfxButtons[x + i * 3][y + k * 3] = btn;
                    }
                }
            }
        }
    }

    private void checkAndLockIfGameEnd(int currentPlayer) {
        if (model.getGameOverState() != GameManager.GameOverState.Active) {
            System.out.println("GAME OVER + " + model.getGameOverState() + " " + currentPlayer);
            String[][] macroboard = model.getMacroboard();
            // Lock game
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    if (macroboard[i][k].equals(IField.AVAILABLE_FIELD)) {
                        macroboard[i][k] = IField.EMPTY_FIELD;
                    }
                }
            }
            if (model.getGameOverState().equals(GameManager.GameOverState.Tie)) {
                showWinnerPane("TIE");
            }
            else {
                showWinnerPane(currentPlayer + "");
            }
        }
    }

    private Text getFontAwesomeIconFromPlayerId(String playerId) throws RuntimeException {
        Text fontAwesomeIcon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ASTERISK);
        switch (playerId) {
            case "0":
                return FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.DIAMOND);
            case "1":
                return FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.TRASH);
            case "TIE":
                return FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.BLACK_TIE);
            default:
                throw new RuntimeException("PlayerId not valid");
        }
    }

    private void updateGUI() throws RuntimeException {
        String[][] board = model.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[i].length; k++) {
                if (board[i][k].equals(IField.EMPTY_FIELD)) {
                    jfxButtons[i][k].getStyleClass().add("empty");
                }
                else {
                    Text fontAwesomeIcon = getFontAwesomeIconFromPlayerId(board[i][k]);
                    jfxButtons[i][k].getStyleClass().add("player" + board[i][k]);
                    jfxButtons[i][k].setGraphic(fontAwesomeIcon);
                }

            }
        }
        String[][] macroBoard = model.getMacroboard();
        for (int i = 0; i < macroBoard.length; i++) {
            for (int k = 0; k < macroBoard[i].length; k++) {
                if (gridMicros[i][k] != null) {
                    if (macroBoard[i][k].equals(IField.AVAILABLE_FIELD)) {
                        gridMicros[i][k].getStyleClass().add("highlight");
                    }
                    else {
                        gridMicros[i][k].getStyleClass().removeAll("highlight");
                    }

                    // If there is a win
                    if (!macroBoard[i][k].equals(IField.AVAILABLE_FIELD)
                            && !macroBoard[i][k].equals(IField.EMPTY_FIELD)
                            && gridMicros[i][k] != null) {
                        gridMacro.getChildren().remove(gridMicros[i][k]);
                        Label lbl = new Label("");
                        Text fontAwesomeIcon = getFontAwesomeIconFromPlayerId(macroBoard[i][k]);
                        lbl.setGraphic(fontAwesomeIcon);
                        lbl.getStyleClass().add("winner-label");
                        lbl.getStyleClass().add("player" + macroBoard[i][k]);
                        lbl.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        gridMicros[i][k] = null;
                        gridMacro.add(lbl, i, k);
                    }
                }
            }
        }

    }

    private void updateConsole() {
        String[][] board = model.getGameState().getField().getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[i].length; k++) {
                System.out.print("|" + board[i][k] + "|");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public void update() {
        //updateConsole();
        Platform.runLater(() -> updateGUI());
    }

    public void setupGame(IBot bot0, IBot bot1) {
        model = new BoardModel(bot0, bot1);
        this.bot0 = bot0;
        this.bot1 = bot1;
    }

    public void setupGame(String humanName, IBot bot1) {
        model = new BoardModel(bot1, true);
        this.bot1 = bot1;
        this.player0 = humanName;
    }

    public void setupGame(IBot bot0, String humanName) {
        model = new BoardModel(bot0, false);
        this.bot0 = bot0;
        this.player1 = humanName;
    }

    public void setupGame(String humanName0, String humanName1) {
        model = new BoardModel();
        this.player0 = humanName0;
        this.player1 = humanName1;
    }
}

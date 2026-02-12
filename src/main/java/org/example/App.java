package org.example;

import java.util.*;

public class App {
    static final char EMPTY = ' ';
    static char[][] board = new char[3][3];
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    static final int[][][] WIN_LINES = {
            {{0,0},{0,1},{0,2}},
            {{1,0},{1,1},{1,2}},
            {{2,0},{2,1},{2,2}},
            {{0,0},{1,0},{2,0}},
            {{0,1},{1,1},{2,1}},
            {{0,2},{1,2},{2,2}},
            {{0,0},{1,1},{2,2}},
            {{0,2},{1,1},{2,0}}
    };

    public static void main(String[] args) {

        while (true) {
            System.out.print("Input command: ");
            String[] command = scanner.nextLine().split(" ");

            if (command[0].equals("exit")) {
                break;
            }

            if (!validParameters(command)) {
                System.out.println("Bad parameters!");
                continue;
            }

            initBoard();
            printBoard();

            String player1 = command[1];
            String player2 = command[2];
            char current = 'X';

            while (true) {
                makeMove(player1, current);
                printBoard();
                if (gameOver())
                    break;

                current = 'O';
                makeMove(player2, current);
                printBoard();
                if (gameOver())
                    break;

                current = 'X';
            }
        }
    }

    // to validate the parameters

    static boolean validParameters(String[] cmd) {
        return cmd.length == 3 &&
                cmd[0].equals("start") &&
                isPlayer(cmd[1]) &&
                isPlayer(cmd[2]);
    }

    static boolean isPlayer(String s) {
        return s.equals("user") || s.equals("easy") ||
                s.equals("medium") || s.equals("hard");
    }

    // to make the moves

    static void makeMove(String player, char symbol) {
        switch (player) {
            case "user":
                userMove(symbol);
                break;
            case "easy":
                System.out.println("Making move level \"easy\"");
                randomMove(symbol);
                break;
            case "medium":
                mediumMove(symbol);
                break;
            case "hard":
                hardMove(symbol);
                break;

            default:
                System.out.println("Invalid Input!");
        }
    }

    // to make the user move

    static void userMove(char symbol) {
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] parts = scanner.nextLine().split(" ");

            try {
                int x = Integer.parseInt(parts[0]) - 1;
                int y = Integer.parseInt(parts[1]) - 1;

                if (x < 0 || x > 2 || y < 0 || y > 2) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else if (board[x][y] != EMPTY) {
                    System.out.println("This cell is occupied! Choose another one!");
                } else {
                    board[x][y] = symbol;
                    break;
                }
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
            }
        }
    }

    // to make the medium move

    static void mediumMove(char symbol) {
        System.out.println("Making move level \"medium\"");
        char opponent = symbol == 'X' ? 'O' : 'X';

        int[] win = findWinningMove(symbol);
        if (win != null) {
            board[win[0]][win[1]] = symbol;
            return;
        }

        int[] block = findWinningMove(opponent);
        if (block != null) {
            board[block[0]][block[1]] = symbol;
            return;
        }

        randomMove(symbol);
    }

    static int[] findWinningMove(char symbol) {
        for (int[][] line : WIN_LINES) {
            int count = 0;
            int[] empty = null;

            for (int[] c : line) {
                if (board[c[0]][c[1]] == symbol) count++;
                if (board[c[0]][c[1]] == EMPTY) empty = c;
            }

            if (count == 2 && empty != null) return empty;
        }
        return null;
    }

    // to make the hard move

    static void hardMove(char symbol) {
        System.out.println("Making move level \"hard\"");
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = symbol;
                    int score = minimax(false, symbol);
                    board[i][j] = EMPTY;

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }

        board[bestMove[0]][bestMove[1]] = symbol;
    }

    static int minimax(boolean isMaximizing, char aiSymbol) {
        char opponent = aiSymbol == 'X' ? 'O' : 'X';

        if (checkWin(aiSymbol)) return 10;
        if (checkWin(opponent)) return -10;
        if (isDraw()) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = aiSymbol;
                        best = Math.max(best, minimax(false, aiSymbol));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = opponent;
                        best = Math.min(best, minimax(true, aiSymbol));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        }
    }

    // to make the random move

    static void randomMove(char symbol) {
        List<int[]> empty = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == EMPTY)
                    empty.add(new int[]{i, j});

        int[] move = empty.get(random.nextInt(empty.size()));
        board[move[0]][move[1]] = symbol;
    }

    // to get the state of the board

    static boolean gameOver() {
        if (checkWin('X')) {
            System.out.println("X wins");
            return true;
        }
        if (checkWin('O')) {
            System.out.println("O wins");
            return true;
        }
        if (isDraw()) {
            System.out.println("Draw");
            return true;
        }
        return false;
    }

    static boolean checkWin(char s) {
        for (int[][] line : WIN_LINES) {
            if (board[line[0][0]][line[0][1]] == s &&
                    board[line[1][0]][line[1][1]] == s &&
                    board[line[2][0]][line[2][1]] == s)
                return true;
        }
        return false;
    }

    static boolean isDraw() {
        for (char[] row : board)
            for (char c : row)
                if (c == EMPTY) return false;
        return true;
    }

    // to fill the board

    static void initBoard() {
        for (int i = 0; i < 3; i++)
            Arrays.fill(board[i], EMPTY);
    }

    static void printBoard() {
        System.out.println("---------");
        for (char[] row : board) {
            System.out.print("| ");
            for (char c : row)
                System.out.print(c + " ");
            System.out.println("|");
        }
        System.out.println("---------");
    }
}
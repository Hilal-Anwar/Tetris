package org.game.tetris;

import java.io.IOException;

public class Main {
    static final KeyBoardInput keyBoardInput = new KeyBoardInput();

    public static void main(String[] args) throws IOException, InterruptedException {
        Game.play("sound/background_sound.wav",-25.0f,true);
        String information = """
                1. Enter the \033[0;33m"up"\33[0m key to rotate left
                2. Enter the \033[0;33m"down"\33[0m key to rotate right
                3. Enter the \033[0;33m"left"\33[0m key to move left
                4. Enter the \033[0;33m"right"\33[0m key to move right
                5. Enter the \033[0;33m"d"\33[0m key to drop downward(move fast forward)
                6. Enter the \033[0;33m"TAB"\33[0m key for about
                7. Enter the \033[0;33m"BACKSPACE"\33[0m key to move back
                8. Enter the \033[0;33m"EAC"\33[0m key to exit
                """;
        String about = """
                                                About
                                          
                \033[0;33mWhat is Tetris ?\33[0m
                It's the addictive puzzle game that started it all!     
                By embracing our universal desire to create order out of chaos, the Tetris® game provides 
                intellectual sport that combines continuous fun with mental stimulation.                                
                As one of the most recognizable and influential video game brands in the world,
                it’s no wonder why there are hundreds of millions of Tetris products being played, worn, 
                and enjoyed by fans in their everyday lives. For over thirty-five years, the game and brand
                have truly transcended the barriers of culture and language, resulting in a fun and exciting
                playing experience for everyone, everywhere!
                                
                \033[0;33mHow do you play?\33[0m
                The Tetris® game requires players to strategically rotate, move, and drop a procession of
                Tetriminos that fall into the rectangular Matrix at increasing speeds. Players attempt to
                clear as many lines as possible by completing horizontal rows of blocks without empty space,
                but if the Tetriminos surpass the Skyline the game is over! It might sound simple, but 
                strategy and speed can go a long way! Are YOU up for the challenge?
                                
                                
                Copyright 2022-23
                Author :Helal Anwar
                Email  :hilal123anwar@gmail.com
                                
                \033[0;33mPlease do give your useful suggestions\33[0m
                """;
        String message = information;
        while (keyBoardInput.getKeyBoardKey() != Key.ENTER) {
            var k = keyBoardInput.getKeyBoardKey();
            System.out.println("Welcome to tetris");
            System.out.println(message);
            System.out.println("Press enter to continue");
            if (k.equals(Key.TAB)) {
                message = about;
            } else if (k.equals(Key.BACKSPACE)) {
                message = information;
            } else if (k.equals(Key.ESC)) {
                System.exit(0);
            }
            Thread.sleep(100);
            cls();
        }
        var game = new Game();
        game.start();
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}

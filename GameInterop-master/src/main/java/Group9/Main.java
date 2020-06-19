package Group9;

import Group9.agent.factories.DefaultAgentFactory;
import Group9.map.parser.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class Main {
    private static long startTime = 0;
    private static long stopTime = 0;

    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("GuardTest.txt", true);
            for (int i = 0; i < 1000; i++) {
                startTime = System.currentTimeMillis();
                Game game = new Game(Parser.parseFile("C:\\Users\\Nickb\\Github\\DKE-Project-2-2\\GameInterop-master\\src\\main\\java\\Group9\\map\\maps\\test_2.map"), new DefaultAgentFactory(), false);
                game.run();
                stopTime = System.currentTimeMillis();
                System.out.println("The winner is: "+ game.getWinner()+ " "+i);
                long elapsedTime = stopTime - startTime;
                writer.write(Long.toString(elapsedTime));
                System.out.println("writed");
                writer.write("\r\n");   // write new line
            }
            writer.close();
            System.out.println("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

package Group9;

import Group9.agent.factories.DefaultAgentFactory;
import Group9.map.parser.Parser;

public class Main {

    public static void main(String[] args) {
        Game game = new Game(Parser.parseFile("C:\\Users\\Nickb\\Github\\DKE-Project-2-2\\GameInterop-master\\src\\main\\java\\Group9\\map\\maps\\test_2.map"), new Group4.AgentsFactory(), false);
        game.run();
        System.out.println("The winner is: "+ game.getWinner());
    }


}

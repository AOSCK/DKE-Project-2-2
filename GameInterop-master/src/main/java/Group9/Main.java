package Group9;

import Group9.agent.factories.DefaultAgentFactory;
import Group9.map.parser.Parser;


public class Main {

    public static void main(String[] args) {

        Game game = new Game(Parser.parseFile("C:/Users/Me/Dropbox/Uni/Project/2-2/DKE-Project-2-2/GameInterop-master/src/main/java/Group9/map/maps/test_2.map"), new DefaultAgentFactory(), false);
        game.run();
        System.out.printf("The winner is: %s\n", game.getWinner());

    }


}

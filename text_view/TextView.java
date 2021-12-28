package text_view;

import globalVariables.GameVariables;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextView {
    public void show () {
        while(true) { // ?
            this.showMap();
            switch(this.chooseAction()) {
                case 1:
                    this.showInventory();
                    break;
                default:
                    break;
            }
        }
    }

    public int chooseAction() {
        System.out.println("=== Choisir action ===");
        System.out.println("[1] Voir inventaire");
        System.out.println("[2] Jeter les dés");

        Scanner sc = new Scanner(System.in);
        System.out.println("Votre choix ? : ");
        int choice = sc.nextInt();
        Integer[] choices = {1, 2};
        List<Integer> choicesList = Arrays.asList(choices);

        while (!choicesList.contains(choice)) {
            System.out.println("Votre choix ? : ");
            choice = sc.nextInt();
        }

        return choice;
    }

    public void showMap() {
        System.out.println("= Carte =");
        System.out.println(GameVariables.map);
    }

    public void showInventory() {
        System.out.println("= Inventaire =");
        System.out.println(GameVariables.players[0].ressources);
    }
}
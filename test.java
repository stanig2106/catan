import javax.swing.*;
import java.awt.*;

class MyJLayeredPane extends JFrame {
   public MyJLayeredPane() {
      setSize(200, 200);
      JLayeredPane pane = getLayeredPane();

      // Créer des boutons
      JButton btn1 = new JButton();
      btn1.setBackground(Color.yellow);
      btn1.setBounds(30, 30, 60, 60);

      JButton btn2 = new JButton();
      btn2.setBackground(Color.orange);
      btn2.setBounds(50, 50, 60, 60);

      JButton btn3 = new JButton();
      btn3.setBackground(Color.red);
      btn3.setBounds(70, 70, 60, 60);

      // Ajouter les boutons au panel en spécifiant l'ordre
      pane.add(btn1, 1);
      pane.add(btn2, 2);
      pane.add(btn3, 3);
   }

   public static void main(String[] args) {
      MyJLayeredPane frame = new MyJLayeredPane();
      frame.setVisible(true);
   }
}
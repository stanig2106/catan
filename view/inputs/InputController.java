package view.inputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.awt.event.*;

import javax.swing.event.MouseInputListener;

import player.Player;
import view.View;

public abstract class InputController extends EventListener {
   final View view;
   protected static List<InputController> enabledInputControllers = new ArrayList<InputController>();
   protected boolean enabled = false;
   protected Optional<Player> player = Optional.empty();

   InputController(View view) {
      this.view = view;
   }

   public void enable(Player player) {
      this.player = Optional.of(player);
      this.enabled = true;
      InputController.enabledInputControllers.add(this);
   }

   public void disable() {
      this.enabled = false;
      InputController.enabledInputControllers.remove(this);
   }

}

abstract class EventListener implements MouseInputListener, MouseWheelListener, KeyListener {
   @Override
   public void mouseEntered(MouseEvent event) {
   }

   @Override
   public void mouseExited(MouseEvent event) {
   }

   @Override
   public void mouseClicked(MouseEvent event) {
   }

   @Override
   public void mouseDragged(MouseEvent event) {
   }

   @Override
   public void mouseMoved(MouseEvent event) {
   }

   @Override
   public void mousePressed(MouseEvent event) {
   }

   @Override
   public void mouseReleased(MouseEvent event) {
   }

   @Override
   public void mouseWheelMoved(MouseWheelEvent event) {

   }

   @Override
   public void keyPressed(KeyEvent event) {
   }

   @Override
   public void keyReleased(KeyEvent event) {
   }

   @Override
   public void keyTyped(KeyEvent event) {
   }
}
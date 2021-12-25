package view;

import java.awt.Cursor;

public abstract class Scene {
   final protected View view;

   protected Scene(View view) {
      this.view = view;
   }

   protected void preEnable() {
      this.view.removeAllListener();
      this.view.foreground.setBounds(0, 0, 0, 0);
      this.view.content.setCursor(Cursor.getDefaultCursor());
   }

}
